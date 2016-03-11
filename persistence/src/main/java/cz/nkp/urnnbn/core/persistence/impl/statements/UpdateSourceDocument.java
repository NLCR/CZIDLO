/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.core.persistence.SourceDocumentDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;

/**
 *
 * @author Martin Řehánek
 */
public class UpdateSourceDocument extends AbstractStatement implements StatementWrapper {

    private final SourceDocument srcDoc;

    public UpdateSourceDocument(SourceDocument srcDoc) {
        this.srcDoc = srcDoc;
    }

    @Override
    public String preparedStatement() {
        return String.format("UPDATE %s SET %s=?, %s=?, %s=?, %s=?, %s=?, %s=?, %s=?, %s=?, %s=?, %s=? WHERE %s=?", SourceDocumentDAO.TABLE_NAME,
                SourceDocumentDAO.ATTR_TITLE, SourceDocumentDAO.ATTR_VOLUME_TITLE, SourceDocumentDAO.ATTR_ISSUE_TITLE, SourceDocumentDAO.ATTR_CCNB,
                SourceDocumentDAO.ATTR_ISBN, SourceDocumentDAO.ATTR_ISSN, SourceDocumentDAO.ATTR_OTHER_ID, SourceDocumentDAO.ATTR_PUBLISHER,
                SourceDocumentDAO.ATTR_PUB_PLACE, SourceDocumentDAO.ATTR_PUB_YEAR, SourceDocumentDAO.ATTR_INT_ENT_ID);
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setString(1, srcDoc.getTitle());
            st.setString(2, srcDoc.getVolumeTitle());
            st.setString(3, srcDoc.getIssueTitle());
            st.setString(4, srcDoc.getCcnb());
            st.setString(5, srcDoc.getIsbn());
            st.setString(6, srcDoc.getIssn());
            st.setString(7, srcDoc.getOtherId());
            st.setString(8, srcDoc.getPublisher());
            st.setString(9, srcDoc.getPublicationPlace());
            setIntOrNull(st, 10, srcDoc.getPublicationYear());
            st.setLong(11, srcDoc.getIntEntId());
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}
