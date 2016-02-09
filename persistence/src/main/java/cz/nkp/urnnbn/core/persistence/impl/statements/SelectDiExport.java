package cz.nkp.urnnbn.core.persistence.impl.statements;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.logging.Logger;

import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;

public class SelectDiExport implements StatementWrapper {

    private static final Logger LOGGER = Logger.getLogger(SelectDiExport.class.getName());

    private final List<String> registrarCodes;
    private final List<String> entityTypes;
    private final boolean includeUrnActive;
    private final boolean includeUrnDeactivated;
    private final boolean includeDiActive;
    private final boolean includeDiDeactivated;

    public SelectDiExport(List<String> registrarCodes, List<String> entityTypes, boolean includeUrnActive, boolean includeUrnDeactivated,
            boolean includeDiActive, boolean includeDiDeactivated) {
        this.registrarCodes = registrarCodes;
        this.entityTypes = entityTypes;
        this.includeUrnActive = includeUrnActive;
        this.includeUrnDeactivated = includeUrnDeactivated;
        this.includeDiActive = includeDiActive;
        this.includeDiDeactivated = includeDiDeactivated;
    }

    @Override
    public String preparedStatement() {
        StringBuilder builder = new StringBuilder();
        boolean filterUrnActivity = (includeUrnActive && !includeUrnDeactivated) || (!includeUrnActive && includeUrnDeactivated);
        boolean filterDiActivity = (includeDiActive && !includeDiDeactivated) || (!includeDiActive && includeDiDeactivated);
        builder.append("SELECT");
        builder.append(" urnNbn.registrarcode AS regCode,");
        builder.append("urnNbn.documentcode AS docCode,");
        builder.append("urnNbn.active AS urnActive,");
        builder.append("intEnt.entityType AS ieType,");
        builder.append("digInst.url AS diUrl,");
        builder.append("digInst.active AS diActive,");
        builder.append("digInst.format AS diFormat,");
        builder.append("digInst.accessibility AS diAccessibility,");
        builder.append("digInst.created AS diCreated,");
        builder.append("digInst.deactivated AS diDeactivated");
        builder.append(" FROM");
        builder.append(" (SELECT registrarcode, documentcode, digitaldocumentid, active FROM urnnbn WHERE registrarcode IN ").append(
                buildRegistrarList());
        if (filterUrnActivity) {
            builder.append(" AND active=").append(includeUrnActive);
        }
        builder.append(") AS urnNbn");
        builder.append(" JOIN");
        builder.append(" (SELECT * FROM digitaldocument) AS digDoc ON digDoc.id = urnNbn.digitaldocumentid");
        builder.append(" JOIN");
        builder.append(" (SELECT * FROM intelectualEntity WHERE entityType IN ").append(buildEntityTypeList())
                .append(") AS intEnt ON intEnt.id = digDoc.intelectualentityid");
        builder.append(" JOIN");
        builder.append(" (SELECT * FROM digitalinstance");
        if (filterDiActivity) {
            builder.append(" WHERE active=").append(includeDiActive);
        }
        builder.append(") AS digInst ON digInst.digitaldocumentid = digDoc.id");
        builder.append(" ORDER BY regCode,docCode;");
        String result = builder.toString();
        // LOGGER.info("statement: " + result);
        return result;
    }

    private String buildEntityTypeList() {
        // in format ('MONOGRAPH','PERIODICAL')
        StringBuilder builder = new StringBuilder();
        builder.append('(');
        for (int i = 0; i < entityTypes.size(); i++) {
            builder.append('\'').append(entityTypes.get(i)).append('\'');
            if (i < entityTypes.size() - 1) {
                builder.append(',');
            }
        }
        builder.append(')');
        String result = builder.toString();
        // LOGGER.info("entityTypes: " + result);
        return result;
    }

    private String buildRegistrarList() {
        // in format ('aba001','tst01','tst02','nk','tst05')
        StringBuilder builder = new StringBuilder();
        builder.append('(');
        for (int i = 0; i < registrarCodes.size(); i++) {
            builder.append('\'').append(registrarCodes.get(i)).append('\'');
            if (i < registrarCodes.size() - 1) {
                builder.append(',');
            }
        }
        builder.append(')');
        String result = builder.toString();
        // LOGGER.info("registrars: " + result);
        return result;
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        // nothing
    }

}
