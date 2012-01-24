/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import cz.nkp.urnnbn.core.persistence.UserDAO;

/**
 *
 * @author Martin Řehánek
 */
public class InsertUserRegistrar implements StatementWrapper {

    private final long userId;
    private final long registrarId;

    public InsertUserRegistrar(long userId, long registrarId) {
        this.userId = userId;
        this.registrarId = registrarId;
    }

    @Override
    public String preparedStatement() {
        return "INSERT into " + UserDAO.TABLE_USER_REGISTRAR_NAME
                + "(" + UserDAO.USER_REGISTRAR_ATTR_USER_ID
                + "," + UserDAO.USER_REGISTRAR_ATTR_REGISTRAR_ID
                + ") values(?,?)";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, userId);
            st.setLong(2, registrarId);
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}
