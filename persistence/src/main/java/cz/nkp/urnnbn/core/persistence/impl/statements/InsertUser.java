/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.persistence.UserDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;

/**
 *
 * @author Martin Řehánek
 */
public class InsertUser implements StatementWrapper {

    private final User user;

    public InsertUser(User user) {
        this.user = user;
    }

    @Override
    public String preparedStatement() {
        return "INSERT into " + UserDAO.TABLE_NAME + "(" + UserDAO.ATTR_ID + "," + UserDAO.ATTR_CREATED + "," + UserDAO.ATTR_UPDATED + ","
                + UserDAO.ATTR_LOGIN + "," + UserDAO.ATTR_PASS_SALT + "," + UserDAO.ATTR_PASS_HASH + "," + UserDAO.ATTR_IS_ADMIN + ","
                + UserDAO.ATTR_EMAIL + ") values(?,?,?,?,?,?,?,?)";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, user.getId());
            Timestamp now = DateTimeUtils.nowTs();
            st.setTimestamp(2, now);
            st.setTimestamp(3, now);
            st.setString(4, user.getLogin());
            st.setString(5, user.getPasswordSalt());
            st.setString(6, user.getPasswordHash());
            st.setBoolean(7, user.isAdmin());
            st.setString(8, user.getEmail());
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}
