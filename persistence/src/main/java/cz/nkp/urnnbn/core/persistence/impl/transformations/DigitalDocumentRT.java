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
        DigitalDocument doc = new DigitalDocument();
        doc.setId(resultSet.getLong(DigitalDocumentDAO.ATTR_ID));
        if (resultSet.wasNull()) {
            doc.setId(null);
        }
        doc.setIntEntId(resultSet.getLong(DigitalDocumentDAO.ATTR_INT_ENT_ID));
        if (resultSet.wasNull()) {
            doc.setIntEntId(null);
        }
        doc.setRegistrarId(resultSet.getLong(DigitalDocumentDAO.ATTR_REGISTRAR_ID));
        if (resultSet.wasNull()) {
            doc.setRegistrarId(null);
        }
        doc.setArchiverId(resultSet.getLong(DigitalDocumentDAO.ATTR_ARCHIVER_ID));
        if (resultSet.wasNull()) {
            doc.setArchiverId(null);
        }
        Timestamp created = resultSet.getTimestamp(DigitalDocumentDAO.ATTR_CREATED);
        doc.setCreated(DateTimeUtils.timestampToDatetime(created));
        Timestamp updated = resultSet.getTimestamp(DigitalDocumentDAO.ATTR_UPDATED);
        doc.setModified(DateTimeUtils.timestampToDatetime(updated));
        doc.setExtent(resultSet.getString(DigitalDocumentDAO.ATTR_EXTENT));
        doc.setFinancedFrom(resultSet.getString(DigitalDocumentDAO.ATTR_FINANCED));
        doc.setContractNumber(resultSet.getString(DigitalDocumentDAO.ATTR_CONTRACT_NUMBER));
        doc.setFormat(resultSet.getString(DigitalDocumentDAO.ATTR_FORMAT));
        doc.setFormatVersion(resultSet.getString(DigitalDocumentDAO.ATTR_FORMAT_VERSION));
        doc.setResolutionHorizontal(resultSet.getInt(DigitalDocumentDAO.ATTR_RES_HORIZONTAL));
        if (resultSet.wasNull()) {
            doc.setResolutionHorizontal(null);
        }
        doc.setResolutionVertical(resultSet.getInt(DigitalDocumentDAO.ATTR_RES_VERTICAL));
        if (resultSet.wasNull()) {
            doc.setResolutionVertical(null);
        }
        doc.setCompression(resultSet.getString(DigitalDocumentDAO.ATTR_COMPRESSION));
        doc.setCompressionRatio(resultSet.getDouble(DigitalDocumentDAO.ATTR_COMPRESSION_RATIO));
        if (resultSet.wasNull()) {
            doc.setCompressionRatio(null);
        }
        doc.setColorModel(resultSet.getString(DigitalDocumentDAO.ATTR_COLOR_MODEL));
        doc.setColorDepth(resultSet.getInt(DigitalDocumentDAO.ATTR_COLOR_DEPTH));
        if (resultSet.wasNull()) {
            doc.setColorDepth(null);
        }
        doc.setIccProfile(resultSet.getString(DigitalDocumentDAO.ATTR_ICC_PROFILE));
        doc.setPictureWidth(resultSet.getInt(DigitalDocumentDAO.ATTR_PIC_WIDTH));
        if (resultSet.wasNull()) {
            doc.setPictureWidth(null);
        }
        doc.setPictureHeight(resultSet.getInt(DigitalDocumentDAO.ATTR_PIC_HEIGHT));
        if (resultSet.wasNull()) {
            doc.setPictureHeight(null);
        }
        return doc;
    }
}
