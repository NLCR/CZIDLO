/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.transformations;

import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.core.persistence.UserDAO;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class UserRT implements ResultsetTransformer {
    
    @Override
    public Object transform(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong(UserDAO.ATTR_ID));
        user.setLogin(resultSet.getString(UserDAO.ATTR_LOGIN));
        user.setPassword(resultSet.getString(UserDAO.ATTR_PASS));
        user.setAdmin(resultSet.getBoolean(UserDAO.ATTR_IS_ADMIN));
        user.setEmail(resultSet.getString(UserDAO.ATTR_EMAIL));
        return user;
    }
}
