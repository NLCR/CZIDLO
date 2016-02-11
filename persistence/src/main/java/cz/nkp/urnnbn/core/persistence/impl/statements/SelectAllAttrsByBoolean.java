package cz.nkp.urnnbn.core.persistence.impl.statements;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;

public class SelectAllAttrsByBoolean implements StatementWrapper {

    private final String tableName;
    private final String whereAttrName;
    private final boolean whereAttrValue;

    public SelectAllAttrsByBoolean(String tableName, String whereAttrName, boolean whereAttrValue) {
        this.tableName = tableName;
        this.whereAttrName = whereAttrName;
        this.whereAttrValue = whereAttrValue;
    }

    @Override
    public String preparedStatement() {
        return "SELECT * from " + tableName + " WHERE " + whereAttrName + "=?";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setBoolean(1, whereAttrValue);
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }

}
