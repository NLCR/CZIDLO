package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.UrnNbnExportFilter;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import org.joda.time.DateTime;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class SelectUrnNbnExport implements StatementWrapper {

    private final String languageCode;
    private final UrnNbnExportFilter filter;
    private final boolean withDigitalInstances;

    public SelectUrnNbnExport(String languageCode, UrnNbnExportFilter filter, boolean withDigitalInstances) {
        this.languageCode = languageCode;
        this.filter = filter;
        this.withDigitalInstances = withDigitalInstances;
    }

    public String preparedStatement() {
        String parameters = buildParameters();
        String result = "SELECT * FROM " + "(SELECT 'urn:nbn:" + languageCode + ":'|| urn.registrarcode || '-' || urn.documentcode AS urn_nbn"
                + ",urn.registrarcode AS registrar" + ",urn.reserved AS reserved" + ",urn.registered AS registered"
                + ",urn.deactivated AS deactivated" + ",urn.active AS active" + ",ie.entitytype AS entity_type"
                + ",EXISTS(SELECT 1 FROM ieidentifier AS ied WHERE ied.intelectualentityid = ie.id AND ied.type='CCNB') AS cnb"
                + ",EXISTS(SELECT 1 FROM ieidentifier AS ied WHERE ied.intelectualentityid = ie.id AND ied.type='ISSN') AS issn"
                + ",EXISTS(SELECT 1 FROM ieidentifier AS ied WHERE ied.intelectualentityid = ie.id AND ied.type='ISBN') AS isbn"
                + ",(SELECT idvalue from ieidentifier AS ied where ied.intelectualentityid = ie.id and ied.type='TITLE') as id_title"
                + ",(SELECT idvalue from ieidentifier AS ied where ied.intelectualentityid = ie.id and ied.type='SUB_TITLE') as id_sub_title"
                + ",(SELECT idvalue from ieidentifier AS ied where ied.intelectualentityid = ie.id and ied.type='VOLUME_TITLE') as id_volume_title"
                + ",(SELECT idvalue from ieidentifier AS ied where ied.intelectualentityid = ie.id and ied.type='ISSUE_TITLE') as id_issue_title";
        if (withDigitalInstances) {
            result += ",(SELECT COUNT(*) FROM digitalinstance WHERE digitaldocumentid=dd.id) AS digital_instances";
        } else {
            result += ",(SELECT 0) AS digital_instances";
        }
        result += " FROM " + "urnnbn urn" + " JOIN digitaldocument dd ON dd.id = urn.digitaldocumentid"
                + " JOIN intelectualentity ie ON ie.id = dd.intelectualentityid" + ") AS row WHERE (" + parameters + ");";
        return result;
    }

    private String buildParameters() {
        StringBuilder parameters = new StringBuilder();
        //registration date range
        if (filter.getRegistrationStart() != null) {
            parameters.append("row.registered>= ? AND ");
        }
        if (filter.getRegistrationEnd() != null) {
            parameters.append("row.registered<= ? AND ");
        }
        // registrars
        List<String> registrars = filter.getRegistrars();
        if (registrars != null && registrars.size() > 0) {
            StringBuilder registrarsBody = new StringBuilder("?");
            if (registrars.size() > 1) {
                for (int i = 1; i != registrars.size(); i++) {
                    registrarsBody.append(", ?");
                }
            }
            parameters.append(String.format("row.registrar in (%s) AND ", registrarsBody.toString()));
        }
        // types
        List<String> entityTypes = filter.getEntityTypes();
        if (entityTypes != null) {
            StringBuilder typesBody = new StringBuilder("?");
            if (entityTypes.size() > 1) {
                for (int i = 1; i != entityTypes.size(); i++) {
                    typesBody.append(", ?");
                }
            }
            parameters.append(String.format("row.entity_type in (%s) AND ", typesBody.toString()));
        }
        //missing identifiers
        if (filter.getWithMissingCcnbOnly()) {
            parameters.append("row.cnb = false AND ");
        }
        if (filter.getWithMissingIssnOnly()) {
            parameters.append("row.issn = false AND ");
        }
        if (filter.getWithMMissingIsbnOnly()) {
            parameters.append("row.isbn = false AND ");
        }
        //state
        if (!filter.getReturnActive() || !filter.getReturnDeactivated()) {
            if (filter.getReturnActive()) {
                parameters.append("row.active = true AND ");
            } else {
                parameters.append("row.active = false AND ");
            }
        }
        //deactivation date range
        if (filter.getDeactivationStart() != null) {
            parameters.append("row.deactivated>= ? AND ");
        }
        if (filter.getDeactivationEnd() != null) {
            parameters.append("row.deactivated<= ? AND ");
        }
        parameters.append(" true");
        return parameters.toString();
    }

    public void populate(PreparedStatement st) throws SyntaxException {
        int index = 1;
        try {
            DateTime registrationStart = filter.getRegistrationStart();
            if (registrationStart != null) {
                st.setTimestamp(index, DateTimeUtils.datetimeToTimestamp(registrationStart));
                index++;
            }
            DateTime registrationEnd = filter.getRegistrationEnd();
            if (registrationEnd != null) {
                st.setTimestamp(index, DateTimeUtils.datetimeToTimestamp(registrationEnd));
                index++;
            }
            List<String> registrars = filter.getRegistrars();
            if (registrars != null && registrars.size() > 0) {
                for (String registrar : registrars) {
                    st.setString(index, registrar);
                    index++;
                }
            }
            List<String> entityTypes = filter.getEntityTypes();
            if (entityTypes != null) {
                for (String type : entityTypes) {
                    st.setString(index, type);
                    index++;
                }
            }
            DateTime deactivationStart = filter.getDeactivationStart();
            if (deactivationStart != null) {
                st.setTimestamp(index, DateTimeUtils.datetimeToTimestamp(deactivationStart));
                index++;
            }
            DateTime deactivationEnd = filter.getDeactivationEnd();
            if (deactivationEnd != null) {
                st.setTimestamp(index, DateTimeUtils.datetimeToTimestamp(deactivationEnd));
                index++;
            }
        } catch (SQLException sqle) {
            throw new SyntaxException(sqle);
        }
    }

}
