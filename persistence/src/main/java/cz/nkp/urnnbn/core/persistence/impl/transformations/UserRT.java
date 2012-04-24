/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.transformations;

import cz.nkp.urnnbn.core.dto.User;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.persistence.UserDAO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 *
 * @author Martin Řehánek
 */
public class UserRT implements ResultsetTransformer {

    private final boolean includePassword;

    public UserRT(boolean includePassword) {
        this.includePassword = includePassword;
    }

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
        if (includePassword) {
            user.setPassword(resultSet.getString(UserDAO.ATTR_PASS));
        }
        user.setAdmin(resultSet.getBoolean(UserDAO.ATTR_IS_ADMIN));
        if (resultSet.wasNull()) {
            user.setAdmin(null);
        }
        user.setEmail(resultSet.getString(UserDAO.ATTR_EMAIL));
        return user;
    }
}
