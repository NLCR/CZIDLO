/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.persistence.ContentDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author xrosecky
 */
public class SelectContentByLangAndName implements StatementWrapper {
    
    private String language;
    
    private String name;

    public SelectContentByLangAndName(String language, String name) {
        this.language = language;
        this.name = name;
    }

    public String preparedStatement() {
         return "SELECT *"
                + " from " + ContentDAO.TABLE_NAME
                + " WHERE "
                + ContentDAO.ATTR_LANG + "=? AND "
                + ContentDAO.ATTR_NAME + "=?";
    }

    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setString(1, language);
            st.setString(2, name);
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
    
    
    
    
}
