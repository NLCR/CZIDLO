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
public class DeleteByStringString implements StatementWrapper {

    private final String tableName;
    private final String firstAttrName;
    private final String firstAttrValue;
    private final String secondAttrName;
    private final String secondAttrValue;

    public DeleteByStringString(String tableName, String firstAttrNambe, String firstAttrValue, String secondAttrName, String secondAttrValue) {
        this.tableName = tableName;
        this.firstAttrName = firstAttrNambe;
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
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setString(1, firstAttrValue);
            st.setString(2, secondAttrValue);
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }

    }
}
