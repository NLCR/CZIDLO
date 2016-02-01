package cz.nkp.urnnbn.core.persistence.impl.statements;

import java.sql.PreparedStatement;

import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;

public class SelectMinDateStatement implements StatementWrapper {

    private final String tableName;
    private final String dateTimeAttrName;

    public SelectMinDateStatement(String tableName, String dateTimeAttrName) {
        this.tableName = tableName;
        this.dateTimeAttrName = dateTimeAttrName;
    }

    @Override
    public String preparedStatement() {
        return "select min(" + dateTimeAttrName + ") FROM " + tableName + ";";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        // nothing to populate
    }

}
