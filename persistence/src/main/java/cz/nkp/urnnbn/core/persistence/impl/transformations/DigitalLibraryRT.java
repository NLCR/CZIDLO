/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.transformations;

import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.persistence.DigitalLibraryDAO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalLibraryRT implements ResultsetTransformer {

    @Override
    public Object transform(ResultSet resultSet) throws SQLException {
        DigitalLibrary library = new DigitalLibrary();
        library.setId(resultSet.getLong(DigitalLibraryDAO.ATTR_ID));
        if (resultSet.wasNull()) {
            library.setId(null);
        }
        library.setRegistrarId(resultSet.getLong(DigitalLibraryDAO.ATTR_REGISTRAR_ID));
        if (resultSet.wasNull()) {
            library.setRegistrarId(null);
        }
        Timestamp created = resultSet.getTimestamp(DigitalLibraryDAO.ATTR_CREATED);
        library.setCreated(DateTimeUtils.timestampToDatetime(created));
        Timestamp updated = resultSet.getTimestamp(DigitalLibraryDAO.ATTR_UPDATED);
        library.setModified(DateTimeUtils.timestampToDatetime(updated));
        library.setName(resultSet.getString(DigitalLibraryDAO.ATTR_NAME));
        library.setDescription(resultSet.getString(DigitalLibraryDAO.ATTR_DESCRIPTION));
        library.setUrl(resultSet.getString(DigitalLibraryDAO.ATTR_URL));
        return library;
    }
}
