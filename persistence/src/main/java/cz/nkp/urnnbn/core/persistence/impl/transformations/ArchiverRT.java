/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.transformations;

import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.persistence.ArchiverDAO;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class ArchiverRT implements ResultsetTransformer {

    @Override
    public Object transform(ResultSet resultSet) throws SQLException {
        Archiver archiver = new Archiver();
        archiver.setId(resultSet.getLong(ArchiverDAO.ATTR_ID));
        archiver.setName(resultSet.getString(ArchiverDAO.ATTR_NAME));
        archiver.setDescription(resultSet.getString(ArchiverDAO.ATTR_DESCRIPTION));
        return archiver;
    }
}
