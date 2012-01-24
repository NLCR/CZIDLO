/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.Utils;
import cz.nkp.urnnbn.core.dto.DigitalRepresentation;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.DigitalRepresentationDAO;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 *
 * @author Martin Řehánek
 */
public class InsertDigitalRepresentation implements StatementWrapper {

    private final DigitalRepresentation representation;

    public InsertDigitalRepresentation(DigitalRepresentation representation) {
        this.representation = representation;
    }

    @Override
    public String preparedStatement() {
        return "INSERT into " + DigitalRepresentationDAO.TABLE_NAME
                + "(" + DigitalRepresentationDAO.ATTR_ID
                + "," + DigitalRepresentationDAO.ATTR_INT_ENT_ID
                + "," + DigitalRepresentationDAO.ATTR_REGISTRAR_ID
                + "," + DigitalRepresentationDAO.ATTR_ARCHIVER_ID
                + "," + DigitalRepresentationDAO.ATTR_CREATED
                + "," + DigitalRepresentationDAO.ATTR_UPDATED
                + "," + DigitalRepresentationDAO.ATTR_FORMAT
                + "," + DigitalRepresentationDAO.ATTR_EXTENT
                + "," + DigitalRepresentationDAO.ATTR_RESOLUTION
                + "," + DigitalRepresentationDAO.ATTR_COLOR_DEPTH
                + "," + DigitalRepresentationDAO.ATTR_ACCESS
                + "," + DigitalRepresentationDAO.ATTR_FINANCED
                + ") values(?,?,?,?,?,?,?,?,?,?,?,?)";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, representation.getId());
            st.setLong(2, representation.getIntEntId());
            st.setLong(3, representation.getRegistrarId());
            st.setLong(4, representation.getArchiverId());
            Timestamp now = Utils.nowTs();
            st.setTimestamp(5, now);
            st.setTimestamp(6, now);
            st.setString(7, representation.getFormat());
            st.setString(8, representation.getExtent());
            st.setString(9, representation.getResolution());
            st.setString(10, representation.getColorDepth());
            st.setString(11, representation.getAccessibility());
            st.setString(12, representation.getFinancedFrom());
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}
