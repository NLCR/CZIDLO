package cz.nkp.urnnbn.core.persistence.impl.statements;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;

public class SelectAllAttrsByStringIntIntBoolean extends SelectAllAttrsByStringIntInt {

    final String whereAttr4Name;
    final boolean whereAttr4Value;

    public SelectAllAttrsByStringIntIntBoolean(String tableName, String whereAttr1Name, String whereAttr1Value, String whereAttr2Name,
            int whereAttr2Value, String whereAttr3Name, int whereAttr3Value, String whereAttr4Name, boolean whereAttr4Value) {
        super(tableName, whereAttr1Name, whereAttr1Value, whereAttr2Name, whereAttr2Value, whereAttr3Name, whereAttr3Value);
        this.whereAttr4Name = whereAttr4Name;
        this.whereAttr4Value = whereAttr4Value;
    }

    @Override
    public String preparedStatement() {
        return "SELECT * from " + tableName + " WHERE "//
                + whereAttr1Name + "=? AND " //
                + whereAttr2Name + "=? AND "//
                + whereAttr3Name + "=? AND "//
                + whereAttr4Name + "=?;";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        super.populate(st);
        try {
            st.setBoolean(4, whereAttr4Value);
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }

}
