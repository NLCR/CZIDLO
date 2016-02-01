/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class SelectIdentifiersByStringString implements StatementWrapper {

    private final String tableName;
    private final String idAttrName;
    private final String whereAttrName1;
    private final String whereAttrValue1;
    private final String whereAttrName2;
    private final String whereAttrValue2;

    public SelectIdentifiersByStringString(String tableName, String idAttrName, String whereAttrName1, String whereAttrValue1, String whereAttrName2,
            String whereAttrValue2) {
        this.tableName = tableName;
        this.idAttrName = idAttrName;
        this.whereAttrName1 = whereAttrName1;
        this.whereAttrValue1 = whereAttrValue1;
        this.whereAttrName2 = whereAttrName2;
        this.whereAttrValue2 = whereAttrValue2;
    }

    @Override
    public String preparedStatement() {
        return "SELECT " + idAttrName + " from " + tableName + " WHERE " + whereAttrName1 + "=? AND " + whereAttrName2 + "=?";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setString(1, whereAttrValue1);
            st.setString(2, whereAttrValue2);
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}
