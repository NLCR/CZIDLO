/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.transformations;

import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.persistence.ArchiverDAO;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 *
 * @author Martin Řehánek
 */
public class ArchiverRT implements ResultsetTransformer {

    @Override
    public Object transform(ResultSet resultSet) throws SQLException {
        Archiver archiver = new Archiver();
        archiver.setId(resultSet.getLong(ArchiverDAO.ATTR_ID));
        if (resultSet.wasNull()) {
            archiver.setId(null);
        }
        Timestamp created = resultSet.getTimestamp(ArchiverDAO.ATTR_CREATED);
        archiver.setCreated(DateTimeUtils.timestampToDatetime(created));
        Timestamp updated = resultSet.getTimestamp(ArchiverDAO.ATTR_UPDATED);
        archiver.setModified(DateTimeUtils.timestampToDatetime(updated));
        archiver.setName(resultSet.getString(ArchiverDAO.ATTR_NAME));
        archiver.setDescription(resultSet.getString(ArchiverDAO.ATTR_DESCRIPTION));
        archiver.setOrder(resultSet.getLong(ArchiverDAO.ATTR_ORDER));
        archiver.setHidden(resultSet.getBoolean(ArchiverDAO.ATTR_HIDDEN));
        return archiver;
    }
}
