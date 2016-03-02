/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.transformations;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.UrnNbnRegistrationMode;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.persistence.RegistrarDAO;

/**
 * 
 * @author Martin Řehánek
 */
public class RegistrarRT implements ResultsetTransformer {

    @Override
    public Object transform(ResultSet resultSet) throws SQLException {
        Registrar registrar = new Registrar();
        registrar.setId(resultSet.getLong(RegistrarDAO.ATTR_ID));
        if (resultSet.wasNull()) {
            registrar.setId(null);
        }
        registrar.setCode(RegistrarCode.valueOf(resultSet.getString(RegistrarDAO.ATTR_CODE)));
        registrar.setRegistrationModeAllowed(UrnNbnRegistrationMode.BY_REGISTRAR,
                resultSet.getBoolean(RegistrarDAO.ATTR_ALLOWED_REGISTRATION_MODE_BY_REGISTRAR));
        registrar.setRegistrationModeAllowed(UrnNbnRegistrationMode.BY_RESOLVER,
                resultSet.getBoolean(RegistrarDAO.ATTR_ALLOWED_REGISTRATION_MODE_BY_RESOLVER));
        registrar.setRegistrationModeAllowed(UrnNbnRegistrationMode.BY_RESERVATION,
                resultSet.getBoolean(RegistrarDAO.ATTR_ALLOWED_REGISTRATION_MODE_BY_RESERVATION));
        return registrar;
    }
}
