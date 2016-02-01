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
public class SelectAllAttrsByStringIntInt implements StatementWrapper {

    private final String tableName;
    private final String whereAttr1Name;
    private final String whereAttr1Value;
    private final String whereAttr2Name;
    private final int whereAttr2Value;
    private final String whereAttr3Name;
    private final int whereAttr3Value;

    public SelectAllAttrsByStringIntInt(String tableName, String whereAttr1Name, String whereAttr1Value, String whereAttr2Name, int whereAttr2Value,
            String whereAttr3Name, int whereAttr3Value) {
        this.tableName = tableName;
        this.whereAttr1Name = whereAttr1Name;
        this.whereAttr1Value = whereAttr1Value;
        this.whereAttr2Name = whereAttr2Name;
        this.whereAttr2Value = whereAttr2Value;
        this.whereAttr3Name = whereAttr3Name;
        this.whereAttr3Value = whereAttr3Value;
    }

    @Override
    public String preparedStatement() {
        return "SELECT * from " + tableName + " WHERE " + whereAttr1Name + "=? AND " + whereAttr2Name + "=? AND " + whereAttr3Name + "=?;";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setString(1, whereAttr1Value);
            st.setInt(2, whereAttr2Value);
            st.setInt(3, whereAttr3Value);
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}
