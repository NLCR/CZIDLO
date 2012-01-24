/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.dto.UrnNbnSearch;
import cz.nkp.urnnbn.core.persistence.UrnNbnSearchDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class UpdateUrnNbnSearch implements StatementWrapper {

    private final UrnNbnSearch search;

    public UpdateUrnNbnSearch(UrnNbnSearch booking) {
        this.search = booking;
    }

    @Override
    public String preparedStatement() {
        return "UPDATE " + UrnNbnSearchDAO.TABLE_NAME + " SET "
                + UrnNbnSearchDAO.ATTR_LAST_DOCUMENT_CODE + "=?"
                + " WHERE " + UrnNbnSearchDAO.ATTR_REGISTRAR_ID + "=?";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setString(1, search.getLastFoundDocumentCode());
            st.setLong(2, search.getRegistrarId());
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}
