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
public class DeleteByStringStringStringString implements StatementWrapper {

    private final String tableName;
    private final String attr1Name;
    private final String attr1Value;
    private final String attr2Name;
    private final String attr2Value;
    private final String attr3Name;
    private final String attr3Value;
    private final String attr4Name;
    private final String attr4Value;


    public DeleteByStringStringStringString(String tableName, String attr1Name, String attr1Value, String attr2Name, String attr2Value, String attr3Name, String attr3Value, String attr4Name, String attr4Value) {
        this.tableName = tableName;
        this.attr1Name = attr1Name;
        this.attr1Value = attr1Value;
        this.attr2Name = attr2Name;
        this.attr2Value = attr2Value;
        this.attr3Name = attr3Name;
        this.attr3Value = attr3Value;
        this.attr4Name = attr4Name;
        this.attr4Value = attr4Value;
    }

    @Override
    public String preparedStatement() {
        return "DELETE from " + tableName + " WHERE " + attr1Name + "=? AND " + attr2Name + "=? AND " + attr3Name + "=? AND " + attr4Name + "=?";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setString(1, attr1Value);
            st.setString(2, attr2Value);
            st.setString(3, attr3Value);
            st.setString(4, attr4Value);
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }

    }
}
