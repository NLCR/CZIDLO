/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.transformations;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.persistence.PublicationDAO;

/**
 *
 * @author Martin Řehánek
 */
public class PublicationRT implements ResultsetTransformer {

    @Override
    public Object transform(ResultSet resultSet) throws SQLException {
        Publication publication = new Publication();
        publication.setIntEntId(resultSet.getLong(PublicationDAO.ATTR_INT_ENT_ID));
        if (resultSet.wasNull()) {
            publication.setIntEntId(null);
        }
        publication.setPlace(resultSet.getString(PublicationDAO.ATTR_PLACE));
        publication.setPublisher(resultSet.getString(PublicationDAO.ATTR_PUBLISHER));
        publication.setYear(resultSet.getInt(PublicationDAO.ATTR_YEAR));
        if (resultSet.wasNull()) {
            publication.setYear(null);
        }
        return publication;
    }
}
