/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.dto.DigRepIdentifier;
import cz.nkp.urnnbn.core.persistence.DigRepIdentifierDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class UpdateDigRepIdentifier implements StatementWrapper {

    private final DigRepIdentifier id;

    public UpdateDigRepIdentifier(DigRepIdentifier id) {
        this.id = id;
    }

    @Override
    public String preparedStatement() {
        return "UPDATE " + DigRepIdentifierDAO.TABLE_NAME + " SET "
                + DigRepIdentifierDAO.ATTR_VALUE + "=?"
                + " WHERE " + DigRepIdentifierDAO.ATTR_REG_ID + "=?"
                + " AND " + DigRepIdentifierDAO.ATTR_DIG_REP_ID + "=?"
                + " AND " + DigRepIdentifierDAO.ATTR_TYPE + "=?";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setString(1, id.getValue());
            st.setLong(2, id.getRegistrarId());
            st.setLong(3, id.getDigRepId());
            st.setString(4, id.getType().toString());
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}
