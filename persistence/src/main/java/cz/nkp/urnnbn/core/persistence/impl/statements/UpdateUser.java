/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.persistence.UserDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;

/**
 *
 * @author Martin Řehánek
 */
public class UpdateUser implements StatementWrapper {

    private final User user;

    public UpdateUser(User user) {
        this.user = user;
    }

    @Override
    public String preparedStatement() {
        return "UPDATE " + UserDAO.TABLE_NAME + " SET " + UserDAO.ATTR_EMAIL + "=?," + UserDAO.ATTR_IS_ADMIN + "=?," + UserDAO.ATTR_UPDATED + "=?"
                + " WHERE " + UserDAO.ATTR_ID + "=?";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setString(1, user.getEmail());
            st.setBoolean(2, user.isAdmin() == null ? false : user.isAdmin());
            st.setTimestamp(3, DateTimeUtils.nowTs());
            st.setLong(4, user.getId());
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}
