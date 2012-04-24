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
public class DeleteRecordsByLongAndLongAttr implements StatementWrapper {

    private final String tableName;
    private final String firstAttrName;
    private final long firstAttrValue;
    private final String secondAttrName;
    private final long secondAttrValue;

    public DeleteRecordsByLongAndLongAttr(String tableName, String firstAttrName, long firstAttrValue, String secondAttrName, long secondAttrValue) {
        this.tableName = tableName;
        this.firstAttrName = firstAttrName;
        this.firstAttrValue = firstAttrValue;
        this.secondAttrName = secondAttrName;
        this.secondAttrValue = secondAttrValue;
    }

    @Override
    public String preparedStatement() {
        return "DELETE from " + tableName + " WHERE "
                + firstAttrName + "=? AND "
                + secondAttrName + "=?";
    }

    @Override
    public void populate(PreparedStatement st) {
        try {
            st.setLong(1, firstAttrValue);
            st.setLong(2, secondAttrValue);
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}
