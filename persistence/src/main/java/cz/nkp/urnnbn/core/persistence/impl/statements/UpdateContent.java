/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import cz.nkp.urnnbn.core.dto.Content;
import cz.nkp.urnnbn.core.persistence.ContentDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;

/**
 *
 * @author xrosecky
 */
public class UpdateContent implements StatementWrapper {

    private final Content content;

    public UpdateContent(Content content) {
        this.content = content;
    }

    public String preparedStatement() {
        return "UPDATE " + ContentDAO.TABLE_NAME + " SET " + ContentDAO.ATTR_LANG + "=?," + ContentDAO.ATTR_NAME + "=?," + ContentDAO.ATTR_CONTENT
                + "=?" + " WHERE " + ContentDAO.ATTR_ID + "=?";
    }

    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setString(1, content.getLanguage());
            st.setString(2, content.getName());
            st.setString(3, content.getContent());
            st.setLong(4, content.getId());
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }

}
