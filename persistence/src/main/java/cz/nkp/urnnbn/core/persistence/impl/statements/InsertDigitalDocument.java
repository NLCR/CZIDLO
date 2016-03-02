/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.persistence.DigitalDocumentDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;

/**
 *
 * @author Martin Řehánek
 */
public class InsertDigitalDocument extends AbstractStatement implements StatementWrapper {

    private final DigitalDocument digDocument;

    public InsertDigitalDocument(DigitalDocument digDocument) {
        this.digDocument = digDocument;
    }

    @Override
    public String preparedStatement() {
        return "INSERT into " + DigitalDocumentDAO.TABLE_NAME + "(" + DigitalDocumentDAO.ATTR_ID + "," + DigitalDocumentDAO.ATTR_INT_ENT_ID + ","
                + DigitalDocumentDAO.ATTR_REGISTRAR_ID + "," + DigitalDocumentDAO.ATTR_ARCHIVER_ID + "," + DigitalDocumentDAO.ATTR_CREATED + ","
                + DigitalDocumentDAO.ATTR_UPDATED + "," + DigitalDocumentDAO.ATTR_FINANCED + "," + DigitalDocumentDAO.ATTR_CONTRACT_NUMBER + ","
                + DigitalDocumentDAO.ATTR_FORMAT + "," + DigitalDocumentDAO.ATTR_FORMAT_VERSION + "," + DigitalDocumentDAO.ATTR_EXTENT + ","
                + DigitalDocumentDAO.ATTR_RES_HORIZONTAL + "," + DigitalDocumentDAO.ATTR_RES_VERTICAL + "," + DigitalDocumentDAO.ATTR_COMPRESSION
                + "," + DigitalDocumentDAO.ATTR_COMPRESSION_RATIO + "," + DigitalDocumentDAO.ATTR_COLOR_MODEL + ","
                + DigitalDocumentDAO.ATTR_COLOR_DEPTH + "," + DigitalDocumentDAO.ATTR_ICC_PROFILE + "," + DigitalDocumentDAO.ATTR_PIC_WIDTH + ","
                + DigitalDocumentDAO.ATTR_PIC_HEIGHT + ") values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, digDocument.getId());
            st.setLong(2, digDocument.getIntEntId());
            st.setLong(3, digDocument.getRegistrarId());
            st.setLong(4, digDocument.getArchiverId());
            Timestamp now = DateTimeUtils.nowTs();
            st.setTimestamp(5, now);
            st.setTimestamp(6, now);
            st.setString(7, digDocument.getFinancedFrom());
            st.setString(8, digDocument.getContractNumber());
            st.setString(9, digDocument.getFormat());
            st.setString(10, digDocument.getFormatVersion());
            st.setString(11, digDocument.getExtent());
            setIntOrNull(st, 12, digDocument.getResolutionHorizontal());
            setIntOrNull(st, 13, digDocument.getResolutionVertical());
            st.setString(14, digDocument.getCompression());
            setDoubleOrNull(st, 15, digDocument.getCompressionRatio());
            st.setString(16, digDocument.getColorModel());
            setIntOrNull(st, 17, digDocument.getColorDepth());
            st.setString(18, digDocument.getIccProfile());
            setIntOrNull(st, 19, digDocument.getPictureWidth());
            setIntOrNull(st, 20, digDocument.getPictureHeight());
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}
