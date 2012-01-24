/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.SourceDocumentDAO;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class UpdateSourceDocument implements StatementWrapper {

    private final SourceDocument srcDoc;

    public UpdateSourceDocument(SourceDocument srcDoc) {
        this.srcDoc = srcDoc;
    }

    @Override
    public String preparedStatement() {
        return "UPDATE " + SourceDocumentDAO.TABLE_NAME + " SET "
                + SourceDocumentDAO.ATTR_CCNB + "=?,"
                + SourceDocumentDAO.ATTR_ISBN + "=?,"
                + SourceDocumentDAO.ATTR_ISSN + "=?,"
                + SourceDocumentDAO.ATTR_OTHER_ID + "=?,"
                + SourceDocumentDAO.ATTR_TITLE + "=?,"
                + SourceDocumentDAO.ATTR_PER_VOL + "=?,"
                + SourceDocumentDAO.ATTR_PER_NUM + "=?,"
                + SourceDocumentDAO.ATTR_PUB_PLACE + "=?,"
                + SourceDocumentDAO.ATTR_PUBLISHER + "=?,"
                + SourceDocumentDAO.ATTR_PUB_YEAR + "=?"
                + " WHERE " + SourceDocumentDAO.ATTR_INT_ENT_ID + "=?";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setString(1, srcDoc.getCcnb());
            st.setString(2, srcDoc.getIsbn());
            st.setString(3, srcDoc.getIssn());
            st.setString(4, srcDoc.getOtherId());
            st.setString(5, srcDoc.getTitle());
            st.setString(6, srcDoc.getPeriodicalVolume());
            st.setString(7, srcDoc.getPeriodicalNumber());
            st.setString(8, srcDoc.getPublicationPlace());
            st.setString(9, srcDoc.getPublisher());
            st.setInt(10, srcDoc.getPublicationYear());
            st.setLong(11, srcDoc.getIntEntId());
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}
