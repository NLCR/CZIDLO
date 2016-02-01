/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.dto.Content;
import cz.nkp.urnnbn.core.persistence.ContentDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author xrosecky
 */
public class InsertContent implements StatementWrapper {

    private final Content content;

    public InsertContent(Content content) {
        this.content = content;
    }

    public String preparedStatement() {
        return "INSERT into " + ContentDAO.TABLE_NAME + "(" + ContentDAO.ATTR_ID + "," + ContentDAO.ATTR_LANG + "," + ContentDAO.ATTR_NAME + ","
                + ContentDAO.ATTR_CONTENT + ") values(?, ?, ?, ?)";
    }

    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, content.getId());
            st.setString(2, content.getLanguage());
            st.setString(3, content.getName());
            st.setString(4, content.getContent());
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}
