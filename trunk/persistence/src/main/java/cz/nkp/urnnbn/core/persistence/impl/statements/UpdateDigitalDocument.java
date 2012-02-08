/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.DigitalDocumentDAO;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class UpdateDigitalDocument implements StatementWrapper {

    private final DigitalDocument rep;
    //TODO: atribut lastUpdated

    public UpdateDigitalDocument(DigitalDocument rep) {
        this.rep = rep;
    }

    @Override
    public String preparedStatement() {
        return "UPDATE " + DigitalDocumentDAO.TABLE_NAME + " SET "
                + DigitalDocumentDAO.ATTR_UPDATED + "=?,"
                + DigitalDocumentDAO.ATTR_EXTENT + "=?,"
                + DigitalDocumentDAO.ATTR_RESOLUTION + "=?,"
                + DigitalDocumentDAO.ATTR_COLOR_DEPTH + "=?,"
                + DigitalDocumentDAO.ATTR_FINANCED + "=?"
                + DigitalDocumentDAO.ATTR_CONTRACT_NUMBER + "=?"
                + " WHERE " + DigitalDocumentDAO.ATTR_ID + "=?";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setTimestamp(1, DateTimeUtils.nowTs());
            st.setString(2, rep.getExtent());
            st.setString(3, rep.getResolution());
            st.setString(4, rep.getColorDepth());
            st.setString(5, rep.getFinancedFrom());
            st.setString(6, rep.getContractNumber());
            st.setLong(7, rep.getId());
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}
