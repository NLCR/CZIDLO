/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.persistence.DigDocIdentifierDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class UpdateDigDocIdentifier implements StatementWrapper {

    private final DigDocIdentifier id;

    public UpdateDigDocIdentifier(DigDocIdentifier id) {
        this.id = id;
    }

    @Override
    public String preparedStatement() {
        return "UPDATE " + DigDocIdentifierDAO.TABLE_NAME + " SET "
                + DigDocIdentifierDAO.ATTR_UPDATED + "=?,"
                + DigDocIdentifierDAO.ATTR_VALUE + "=?"
                + " WHERE " + DigDocIdentifierDAO.ATTR_REG_ID + "=?"
                + " AND " + DigDocIdentifierDAO.ATTR_DIG_REP_ID + "=?"
                + " AND " + DigDocIdentifierDAO.ATTR_TYPE + "=?";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setTimestamp(1, DateTimeUtils.nowTs());
            st.setString(2, id.getValue());
            st.setLong(3, id.getRegistrarId());
            st.setLong(4, id.getDigDocId());
            st.setString(5, id.getType().toString());
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}
