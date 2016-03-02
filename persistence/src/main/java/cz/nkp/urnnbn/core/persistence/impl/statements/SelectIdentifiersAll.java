/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import java.sql.PreparedStatement;

import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;

/**
 *
 * @author Martin Řehánek
 */
public class SelectIdentifiersAll implements StatementWrapper {

    private final String tableName;
    private final String attrName;

    public SelectIdentifiersAll(String tableName, String attrName) {
        this.tableName = tableName;
        this.attrName = attrName;
    }

    @Override
    public String preparedStatement() {
        return "SELECT " + attrName + " from " + tableName;
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        // nothing to populate
    }
}
