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
public class SelectCountByStringLong implements StatementWrapper {

    private final String tableName;
    private final String longAttrName;
    private final Long longAttrValue;
    private final String stringAttrName;
    private final String stringAttrValue;

    public SelectCountByStringLong(String tableName, String longAttrName, Long longAttrValue, String stringAttrName, String stringAttrValue) {
        this.tableName = tableName;
        this.longAttrName = longAttrName;
        this.longAttrValue = longAttrValue;
        this.stringAttrName = stringAttrName;
        this.stringAttrValue = stringAttrValue;
    }

    @Override
    public String preparedStatement() {
        return "SELECT count(*) FROM " + tableName + " WHERE " + longAttrName + "=? AND " + stringAttrName + "=?";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, longAttrValue);
            st.setString(2, stringAttrValue);
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}
