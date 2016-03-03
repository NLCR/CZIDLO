/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.transformations;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.persistence.UserDAO;

/**
 * 
 * @author Martin Řehánek
 */
public class UserRT implements ResultsetTransformer {

    @Override
    public User transform(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getLong(UserDAO.ATTR_ID));
        if (resultSet.wasNull()) {
            user.setId(null);
        }
        Timestamp created = resultSet.getTimestamp(UserDAO.ATTR_CREATED);
        user.setCreated(DateTimeUtils.timestampToDatetime(created));
        Timestamp updated = resultSet.getTimestamp(UserDAO.ATTR_UPDATED);
        user.setModified(DateTimeUtils.timestampToDatetime(updated));
        user.setLogin(resultSet.getString(UserDAO.ATTR_LOGIN));
        user.setPasswordSalt(resultSet.getString(UserDAO.ATTR_PASS_SALT));
        user.setPasswordHash(resultSet.getString(UserDAO.ATTR_PASS_HASH));
        user.setAdmin(resultSet.getBoolean(UserDAO.ATTR_IS_ADMIN));
        if (resultSet.wasNull()) {
            user.setAdmin(null);
        }
        user.setEmail(resultSet.getString(UserDAO.ATTR_EMAIL));
        return user;
    }
}
