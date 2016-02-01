/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;

/**
 *
 * @author Martin Řehánek
 */
public class SelectCount implements StatementWrapper {

    private final String tableName;

    public SelectCount(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String preparedStatement() {
        return "SELECT count(*) FROM " + tableName;
    }

    @Override
    public void populate(PreparedStatement st) {
        // nothing to populate
    }
}
