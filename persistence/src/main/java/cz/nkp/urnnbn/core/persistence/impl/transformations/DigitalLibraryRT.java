/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.transformations;

import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.persistence.DigitalLibraryDAO;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalLibraryRT implements ResultsetTransformer {

    @Override
    public Object transform(ResultSet resultSet) throws SQLException {
        DigitalLibrary library = new DigitalLibrary();
        library.setId(resultSet.getLong(DigitalLibraryDAO.ATTR_ID));
        library.setRegistrarId(resultSet.getLong(DigitalLibraryDAO.ATTR_REGISTRAR_ID));
        library.setName(resultSet.getString(DigitalLibraryDAO.ATTR_NAME));
        library.setDescription(resultSet.getString(DigitalLibraryDAO.ATTR_DESCRIPTION));
        library.setUrl(resultSet.getString(DigitalLibraryDAO.ATTR_URL));
        return library;
    }
}
