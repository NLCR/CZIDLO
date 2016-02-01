/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.transformations;

import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.persistence.DigitalInstanceDAO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalInstanceRT implements ResultsetTransformer {

    @Override
    public Object transform(ResultSet resultSet) throws SQLException {
        DigitalInstance instance = new DigitalInstance();
        instance.setId(resultSet.getLong(DigitalInstanceDAO.ATTR_ID));
        if (resultSet.wasNull()) {
            instance.setId(null);
        }
        instance.setDigDocId(resultSet.getLong(DigitalInstanceDAO.ATTR_DIG_DOC_ID));
        if (resultSet.wasNull()) {
            instance.setDigDocId(null);
        }
        instance.setLibraryId(resultSet.getLong(DigitalInstanceDAO.ATTR_LIB_ID));
        if (resultSet.wasNull()) {
            instance.setLibraryId(null);
        }
        instance.setCreated(DateTimeUtils.timestampToDatetime(resultSet.getTimestamp(DigitalInstanceDAO.ATTR_CREATED)));
        Timestamp updatedTs = resultSet.getTimestamp(DigitalInstanceDAO.ATTR_DEACTIVATED);
        if (!resultSet.wasNull()) {
            instance.setDeactivated(DateTimeUtils.timestampToDatetime(updatedTs));
        }
        instance.setUrl(resultSet.getString(DigitalInstanceDAO.ATTR_URL));
        instance.setFormat(resultSet.getString(DigitalInstanceDAO.ATTR_FORMAT));
        instance.setAccessibility(resultSet.getString(DigitalInstanceDAO.ATTR_ACCESS));
        instance.setActive(resultSet.getBoolean(DigitalInstanceDAO.ATTR_ACTIVE));
        return instance;
    }
}
