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

/**
 *
 * @author Martin Řehánek
 */
public class UpdateDigitalRepresentation implements StatementWrapper {

    private final DigitalRepresentation rep;
    //TODO: atribut lastUpdated

    public UpdateDigitalRepresentation(DigitalRepresentation rep) {
        this.rep = rep;
    }

    @Override
    public String preparedStatement() {
        return "UPDATE " + DigitalRepresentationDAO.TABLE_NAME + " SET "
                + DigitalRepresentationDAO.ATTR_UPDATED + "=?,"
                + DigitalRepresentationDAO.ATTR_FORMAT + "=?,"
                + DigitalRepresentationDAO.ATTR_EXTENT + "=?,"
                + DigitalRepresentationDAO.ATTR_RESOLUTION + "=?,"
                + DigitalRepresentationDAO.ATTR_COLOR_DEPTH + "=?,"
                + DigitalRepresentationDAO.ATTR_ACCESS + "=?,"
                + DigitalRepresentationDAO.ATTR_FINANCED + "=?"
                + " WHERE " + DigitalRepresentationDAO.ATTR_ID + "=?";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setTimestamp(1, Utils.nowTs());
            st.setString(2, rep.getFormat());
            st.setString(3, rep.getExtent());
            st.setString(4, rep.getResolution());
            st.setString(5, rep.getColorDepth());
            st.setString(6, rep.getAccessibility());
            st.setString(7, rep.getFinancedFrom());
            st.setLong(8, rep.getId());
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}
