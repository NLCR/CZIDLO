/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.transformations;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.persistence.RegistrarDAO;
import java.sql.ResultSet;
import java.sql.SQLException;

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
        registrar.setAllowedToRegisterFreeUrnNbn(resultSet.getBoolean(RegistrarDAO.ATTR_CAN_REGISTER_FREE_URN));
        if (resultSet.wasNull()) {
            registrar.setAllowedToRegisterFreeUrnNbn(null);
        }
        return registrar;
    }
}
