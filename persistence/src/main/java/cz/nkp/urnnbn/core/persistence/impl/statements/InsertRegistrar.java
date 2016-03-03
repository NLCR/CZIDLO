/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import cz.nkp.urnnbn.core.UrnNbnRegistrationMode;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.persistence.RegistrarDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;

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
        return "INSERT into " + RegistrarDAO.TABLE_NAME + "(" + RegistrarDAO.ATTR_ID + "," + RegistrarDAO.ATTR_CODE + ","
                + RegistrarDAO.ATTR_ALLOWED_REGISTRATION_MODE_BY_REGISTRAR + "," + RegistrarDAO.ATTR_ALLOWED_REGISTRATION_MODE_BY_RESOLVER + ","
                + RegistrarDAO.ATTR_ALLOWED_REGISTRATION_MODE_BY_RESERVATION + ") values(?,?,?,?,?)";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, registrar.getId());
            st.setString(2, registrar.getCode().toString());
            st.setBoolean(3, registrar.isRegistrationModeAllowed(UrnNbnRegistrationMode.BY_REGISTRAR));
            st.setBoolean(4, registrar.isRegistrationModeAllowed(UrnNbnRegistrationMode.BY_RESOLVER));
            st.setBoolean(5, registrar.isRegistrationModeAllowed(UrnNbnRegistrationMode.BY_RESERVATION));
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}
