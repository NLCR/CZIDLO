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
public class DeleteRecordsByLongAttr implements StatementWrapper {

    private final String tableName;
    private final String attributeName;
    private final Long idValue;

    public DeleteRecordsByLongAttr(String tableName, String attributeName, Long id) {
        this.tableName = tableName;
        this.attributeName = attributeName;
        this.idValue = id;
    }

    @Override
    public String preparedStatement() {
        return "DELETE from " + tableName + " WHERE " + attributeName + "=?";
    }

    @Override
    public void populate(PreparedStatement st) {
        try {
            st.setLong(1, idValue);
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}
