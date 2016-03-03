/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import java.sql.PreparedStatement;

import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;

/**
 *
 * @author Martin Řehánek
 */
public class DeleteAllRecords implements StatementWrapper {

    private final String tableName;

    public DeleteAllRecords(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String preparedStatement() {
        return "DELETE from " + tableName;
    }

    @Override
    public void populate(PreparedStatement st) {
        // nothing to populate
    }

}
