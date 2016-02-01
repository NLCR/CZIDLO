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
public class SelectAllAttrsByStringAttr implements StatementWrapper {

    private final String tableName;
    private final String whereAttrName;
    private final String whereAttrValue;

    public SelectAllAttrsByStringAttr(String tableName, String whereAttrName, String whereAttrValue) {
        this.tableName = tableName;
        this.whereAttrName = whereAttrName;
        this.whereAttrValue = whereAttrValue;
    }

    @Override
    public String preparedStatement() {
        return "SELECT * from " + tableName + " WHERE " + whereAttrName + "=?";
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
