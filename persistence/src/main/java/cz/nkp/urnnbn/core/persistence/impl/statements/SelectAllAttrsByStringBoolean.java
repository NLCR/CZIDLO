package cz.nkp.urnnbn.core.persistence.impl.statements;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;

public class SelectAllAttrsByStringBoolean extends SelectAllAttrsByStringAttr {

    final String whereAttr2Name;
    final boolean whereAttr2Value;

    public SelectAllAttrsByStringBoolean(String tableName, String whereAttrName, String whereAttrValue, String whereAttr2Name, boolean whereAttr2Value) {
        super(tableName, whereAttrName, whereAttrValue);
        this.whereAttr2Name = whereAttr2Name;
        this.whereAttr2Value = whereAttr2Value;
    }

    @Override
    public String preparedStatement() {
        return "SELECT * from " + tableName + " WHERE " + whereAttrName + "=? AND " + whereAttr2Name + "=?;";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        super.populate(st);
        try {
            st.setBoolean(2, whereAttr2Value);
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }

}
