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
public class SelectCountByLong implements StatementWrapper {

    private final String tableName;
    private final String attributeName;
    private final Long idValue;

    public SelectCountByLong(String tableName, String attributeName, Long idValue) {
        this.tableName = tableName;
        this.attributeName = attributeName;
        this.idValue = idValue;
    }

    @Override
    public String preparedStatement() {
        return "SELECT count(*) FROM " + tableName + " WHERE " + attributeName + "=?";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setLong(1, idValue);
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}
