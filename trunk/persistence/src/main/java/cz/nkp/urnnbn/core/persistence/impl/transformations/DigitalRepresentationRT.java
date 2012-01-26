/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.transformations;

import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.dto.DigitalRepresentation;
import cz.nkp.urnnbn.core.persistence.DigitalRepresentationDAO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalRepresentationRT implements ResultsetTransformer {

    @Override
    public Object transform(ResultSet resultSet) throws SQLException {
        DigitalRepresentation rep = new DigitalRepresentation();
        rep.setId(resultSet.getLong(DigitalRepresentationDAO.ATTR_ID));
        rep.setIntEntId(resultSet.getLong(DigitalRepresentationDAO.ATTR_INT_ENT_ID));
        rep.setRegistrarId(resultSet.getLong(DigitalRepresentationDAO.ATTR_REGISTRAR_ID));
        rep.setArchiverId(resultSet.getLong(DigitalRepresentationDAO.ATTR_ARCHIVER_ID));
        Timestamp created = resultSet.getTimestamp(DigitalRepresentationDAO.ATTR_CREATED);
        rep.setCreated(DateTimeUtils.timestampToDatetime(created));
        Timestamp updated = resultSet.getTimestamp(DigitalRepresentationDAO.ATTR_UPDATED);
        rep.setLastUpdated(DateTimeUtils.timestampToDatetime(updated));
        rep.setFormat(resultSet.getString(DigitalRepresentationDAO.ATTR_FORMAT));
        rep.setExtent(resultSet.getString(DigitalRepresentationDAO.ATTR_EXTENT));
        rep.setResolution(resultSet.getString(DigitalRepresentationDAO.ATTR_RESOLUTION));
        rep.setColorDepth(resultSet.getString(DigitalRepresentationDAO.ATTR_COLOR_DEPTH));
        rep.setAccessibility(resultSet.getString(DigitalRepresentationDAO.ATTR_ACCESS));
        rep.setFinancedFrom(resultSet.getString(DigitalRepresentationDAO.ATTR_FINANCED));
        return rep;
    }
}
