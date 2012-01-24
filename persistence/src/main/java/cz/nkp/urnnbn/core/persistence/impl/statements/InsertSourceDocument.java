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
public class InsertSourceDocument implements StatementWrapper {

    private final SourceDocument s;

    public InsertSourceDocument(SourceDocument s) {
        this.s = s;
    }

    @Override
    public String preparedStatement() {
        return "INSERT into " + SourceDocumentDAO.TABLE_NAME
                + "(" + SourceDocumentDAO.ATTR_INT_ENT_ID
                + "," + SourceDocumentDAO.ATTR_CCNB
                + "," + SourceDocumentDAO.ATTR_ISBN
                + "," + SourceDocumentDAO.ATTR_ISSN
                + "," + SourceDocumentDAO.ATTR_OTHER_ID
                + "," + SourceDocumentDAO.ATTR_TITLE
                + "," + SourceDocumentDAO.ATTR_PER_VOL
                + "," + SourceDocumentDAO.ATTR_PER_NUM
                + "," + SourceDocumentDAO.ATTR_PUB_PLACE
                + "," + SourceDocumentDAO.ATTR_PUBLISHER
                + "," + SourceDocumentDAO.ATTR_PUB_YEAR
                + ") values(?,?,?,?,?,?,?,?,?,?,?)";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, s.getIntEntId());
            st.setString(2, s.getCcnb());
            st.setString(3, s.getIsbn());
            st.setString(4, s.getIssn());
            st.setString(5, s.getOtherId());
            st.setString(6, s.getTitle());
            st.setString(7, s.getPeriodicalVolume());
            st.setString(8, s.getPeriodicalNumber());
            st.setString(9, s.getPublicationPlace());
            st.setString(10, s.getPublisher());
            st.setInt(11, s.getPublicationYear());
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}
