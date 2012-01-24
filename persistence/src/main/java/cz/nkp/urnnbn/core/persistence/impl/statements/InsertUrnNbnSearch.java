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
public class InsertUrnNbnSearch implements StatementWrapper {

    private final UrnNbnSearch search;

    public InsertUrnNbnSearch(UrnNbnSearch search) {
        this.search = search;
    }

    @Override
    public String preparedStatement() {
        return "INSERT into " + UrnNbnSearchDAO.TABLE_NAME
                + "(" + UrnNbnSearchDAO.ATTR_REGISTRAR_ID
                + "," + UrnNbnSearchDAO.ATTR_LAST_DOCUMENT_CODE
                + ") values(?,?)";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, search.getRegistrarId());
            st.setString(2, search.getLastFoundDocumentCode());
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}
