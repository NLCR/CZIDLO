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
public class SelectSingleAttrByString implements StatementWrapper {

    private final String tableName;
    private final String whereAttrName;
    private final String whereAttrValue;
    private final String selectAttrName;

    public SelectSingleAttrByString(String tableName, String whereAttrName, String whereAttrValue, String selectAttrName) {
        this.tableName = tableName;
        this.whereAttrName = whereAttrName;
        this.whereAttrValue = whereAttrValue;
        this.selectAttrName = selectAttrName;
    }

    @Override
    public String preparedStatement() {
        return "SELECT " + selectAttrName + " from " + tableName + " WHERE " + whereAttrName + "=?";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setString(1, whereAttrValue);
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}
