/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;

/**
 *
 * @author Martin Řehánek
 */
public class SelectAllAttrs implements StatementWrapper {

    private final String tableName;

    public SelectAllAttrs(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String preparedStatement() {
        return "SELECT * from " + tableName;
    }

    @Override
    public void populate(PreparedStatement st) {
        //nothing to populate
    }
}
