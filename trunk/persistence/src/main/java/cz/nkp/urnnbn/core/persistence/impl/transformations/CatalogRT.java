/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.transformations;

import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.persistence.CatalogDAO;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class CatalogRT implements ResultsetTransformer {

    @Override
    public Object transform(ResultSet resultSet) throws SQLException {
        Catalog catalog = new Catalog();
        catalog.setId(resultSet.getLong(CatalogDAO.ATTR_ID));
        catalog.setRegistrarId(resultSet.getLong(CatalogDAO.ATTR_REG_ID));
        catalog.setName(resultSet.getString(CatalogDAO.ATTR_NAME));
        catalog.setDescription(resultSet.getString(CatalogDAO.ATTR_DESC));
        catalog.setUrlPrefix(resultSet.getString(CatalogDAO.ATTR_URL_PREFIX));
        return catalog;
    }
}
