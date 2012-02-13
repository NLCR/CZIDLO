/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.DigitalDocumentDAO;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class UpdateDigitalDocument extends AbstractStatement implements StatementWrapper {

    private final DigitalDocument doc;

    public UpdateDigitalDocument(DigitalDocument doc) {
        this.doc = doc;
    }

    @Override
    public String preparedStatement() {
        return "UPDATE " + DigitalDocumentDAO.TABLE_NAME + " SET "
                + DigitalDocumentDAO.ATTR_UPDATED + "=?,"
                + DigitalDocumentDAO.ATTR_EXTENT + "=?,"
                + DigitalDocumentDAO.ATTR_FINANCED + "=?,"
                + DigitalDocumentDAO.ATTR_CONTRACT_NUMBER + "=?,"
                + DigitalDocumentDAO.ATTR_FORMAT + "=?,"
                + DigitalDocumentDAO.ATTR_FORMAT_VERSION + "=?,"
                + DigitalDocumentDAO.ATTR_RESOLUTION_WIDTH + "=?,"
                + DigitalDocumentDAO.ATTR_RESOLUTION_HEIGHT + "=?,"
                + DigitalDocumentDAO.ATTR_COMPRESSION + "=?,"
                + DigitalDocumentDAO.ATTR_COMPRESSION_RATIO + "=?,"
                + DigitalDocumentDAO.ATTR_COLOR_MODEL + "=?,"
                + DigitalDocumentDAO.ATTR_COLOR_DEPTH + "=?,"
                + DigitalDocumentDAO.ATTR_ICC_PROFILE + "=?,"
                + DigitalDocumentDAO.ATTR_PIC_WIDTH + "=?,"
                + DigitalDocumentDAO.ATTR_PIC_HEIGHT + "=?"
                + " WHERE " + DigitalDocumentDAO.ATTR_ID + "=?";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setTimestamp(1, DateTimeUtils.nowTs());
            st.setString(2, doc.getExtent());
            st.setString(3, doc.getFinancedFrom());
            st.setString(4, doc.getContractNumber());
            st.setString(5, doc.getFormat());
            st.setString(6, doc.getFormatVersion());
            setIntOrNull(st, 7, doc.getResolutionWidth());
            setIntOrNull(st, 8, doc.getResolutionHeight());
            st.setString(9, doc.getCompression());
            setDoubleOrNull(st, 10, doc.getCompressionRatio());
            st.setString(11, doc.getColorModel());
            setIntOrNull(st, 12, doc.getColorDepth());
            st.setString(13, doc.getIccProfile());
            setIntOrNull(st, 14, doc.getPictureWidth());
            setIntOrNull(st, 15, doc.getPictureHeight());
            st.setLong(16, doc.getId());
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}
