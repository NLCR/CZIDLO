/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import cz.nkp.urnnbn.core.persistence.RegistrarDAO;

/**
 *
 * @author Martin Řehánek
 */
public class InsertRegistrar implements StatementWrapper {

    private final Registrar registrar;

    public InsertRegistrar(Registrar registrar) {
        this.registrar = registrar;
    }

    @Override
    public String preparedStatement() {
        return "INSERT into " + RegistrarDAO.TABLE_NAME
                + "(" + RegistrarDAO.ATTR_ID
                + "," + RegistrarDAO.ATTR_CODE
                + "," + RegistrarDAO.ATTR_CAN_REGISTER_FREE_URN
                + ") values(?,?,?)";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, registrar.getId());
            st.setString(2, registrar.getCode());
            st.setBoolean(3, registrar.isAllowedToRegisterFreeUrnNbn());
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}
