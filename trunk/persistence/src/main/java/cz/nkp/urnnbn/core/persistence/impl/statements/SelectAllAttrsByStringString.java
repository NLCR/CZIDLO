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
public class SelectAllAttrsByStringString implements StatementWrapper {

    private final String tableName;
    private final String whereAttr1Name;
    private final String whereAttr1Value;
    private final String whereAttr2Name;
    private final String whereAttr2Value;

    public SelectAllAttrsByStringString(
            String tableName,
            String whereAttr1Name, String whereAttr1Value,
            String whereAttr2Name, String whereAttr2Value) {
        this.tableName = tableName;
        this.whereAttr1Name = whereAttr1Name;
        this.whereAttr1Value = whereAttr1Value;
        this.whereAttr2Name = whereAttr2Name;
        this.whereAttr2Value = whereAttr2Value;
    }

    @Override
    public String preparedStatement() {
        return "SELECT * from " + tableName
                + " WHERE " + whereAttr1Name + "=?"
                + " AND " + whereAttr2Name + "=?";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setString(1, whereAttr1Value);
            st.setString(2, whereAttr2Value);
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}
