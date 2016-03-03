/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.transformations;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.persistence.CatalogDAO;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;

/**
 *
 * @author Martin Řehánek
 */
public class CatalogRT implements ResultsetTransformer {

    @Override
    public Object transform(ResultSet resultSet) throws SQLException {
        Catalog catalog = new Catalog();
        catalog.setId(resultSet.getLong(CatalogDAO.ATTR_ID));
        if (resultSet.wasNull()) {
            catalog.setId(null);
        }
        catalog.setRegistrarId(resultSet.getLong(CatalogDAO.ATTR_REG_ID));
        if (resultSet.wasNull()) {
            catalog.setRegistrarId(null);
        }
        Timestamp created = resultSet.getTimestamp(CatalogDAO.ATTR_CREATED);
        catalog.setCreated(DateTimeUtils.timestampToDatetime(created));
        Timestamp updated = resultSet.getTimestamp(CatalogDAO.ATTR_UPDATED);
        catalog.setModified(DateTimeUtils.timestampToDatetime(updated));
        catalog.setName(resultSet.getString(CatalogDAO.ATTR_NAME));
        catalog.setDescription(resultSet.getString(CatalogDAO.ATTR_DESC));
        catalog.setUrlPrefix(resultSet.getString(CatalogDAO.ATTR_URL_PREFIX));
        return catalog;
    }
}
