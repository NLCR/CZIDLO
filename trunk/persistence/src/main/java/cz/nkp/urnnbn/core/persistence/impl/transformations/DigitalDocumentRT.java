/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.transformations;

import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.persistence.DigitalDocumentDAO;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 *
 * @author Martin Řehánek
 */
public class DigitalDocumentRT implements ResultsetTransformer {

    @Override
    public Object transform(ResultSet resultSet) throws SQLException {
        DigitalDocument rep = new DigitalDocument();
        rep.setId(resultSet.getLong(DigitalDocumentDAO.ATTR_ID));
        rep.setIntEntId(resultSet.getLong(DigitalDocumentDAO.ATTR_INT_ENT_ID));
        rep.setRegistrarId(resultSet.getLong(DigitalDocumentDAO.ATTR_REGISTRAR_ID));
        rep.setArchiverId(resultSet.getLong(DigitalDocumentDAO.ATTR_ARCHIVER_ID));
        Timestamp created = resultSet.getTimestamp(DigitalDocumentDAO.ATTR_CREATED);
        rep.setCreated(DateTimeUtils.timestampToDatetime(created));
        Timestamp updated = resultSet.getTimestamp(DigitalDocumentDAO.ATTR_UPDATED);
        rep.setLastUpdated(DateTimeUtils.timestampToDatetime(updated));
        rep.setExtent(resultSet.getString(DigitalDocumentDAO.ATTR_EXTENT));
        rep.setResolution(resultSet.getString(DigitalDocumentDAO.ATTR_RESOLUTION));
        rep.setColorDepth(resultSet.getString(DigitalDocumentDAO.ATTR_COLOR_DEPTH));
        rep.setFinancedFrom(resultSet.getString(DigitalDocumentDAO.ATTR_FINANCED));
        rep.setContractNumber(resultSet.getString(DigitalDocumentDAO.ATTR_CONTRACT_NUMBER));
        return rep;
    }
}
