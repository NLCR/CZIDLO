/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;

/**
 *
 * @author Martin Řehánek
 */
public class SelectSingleAttrByLongStringString implements StatementWrapper {

    private final String tableName;
    private final String selectAttrName;
    private final String whereAttr1Name;
    private final long whereAttr1Value;
    private final String whereAttr2Name;
    private final String whereAttr2Value;
    private final String whereAttr3Name;
    private final String whereAttr3Value;

    public SelectSingleAttrByLongStringString(String tableName, String selectAttrName, String whereAttr1Name, long whereAttr1Value,
            String whereAttr2Name, String wherAttr2Value, String whereAttr3Name, String wherAttr3Value) {
        this.tableName = tableName;
        this.selectAttrName = selectAttrName;
        this.whereAttr1Name = whereAttr1Name;
        this.whereAttr1Value = whereAttr1Value;
        this.whereAttr2Name = whereAttr2Name;
        this.whereAttr2Value = wherAttr2Value;
        this.whereAttr3Name = whereAttr3Name;
        this.whereAttr3Value = wherAttr3Value;
    }

    @Override
    public String preparedStatement() {
        return "SELECT " + selectAttrName + " from " + tableName + " WHERE " + whereAttr1Name + "=?" + " AND " + whereAttr2Name + "=?" + " AND "
                + whereAttr3Name + "=?";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, whereAttr1Value);
            st.setString(2, whereAttr2Value);
            st.setString(3, whereAttr3Value);
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}
