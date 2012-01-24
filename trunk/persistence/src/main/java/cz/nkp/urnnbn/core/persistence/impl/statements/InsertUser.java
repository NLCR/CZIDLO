/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import cz.nkp.urnnbn.core.persistence.UserDAO;

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
        return "INSERT into " + UserDAO.TABLE_NAME
                + "(" + UserDAO.ATTR_ID
                + "," + UserDAO.ATTR_LOGIN
                + "," + UserDAO.ATTR_PASS
                + "," + UserDAO.ATTR_IS_ADMIN
                + "," + UserDAO.ATTR_EMAIL
                + ") values(?,?,?,?,?)";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, user.getId());
            st.setString(2, user.getLogin());
            st.setString(3, user.getPassword());
            st.setBoolean(4, user.isAdmin());
            st.setString(5, user.getEmail());
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}
