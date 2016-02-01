/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class UpdateRecordTimestamp extends AbstractStatement implements StatementWrapper {

    private final String tableName;
    private final String idAttrName;
    private final Long idAttrValue;
    private final String timestampAttrName;

    public UpdateRecordTimestamp(String tableName, String idAttrName, Long idAttrValue, String timestampAttrName) {
        this.tableName = tableName;
        this.idAttrName = idAttrName;
        this.idAttrValue = idAttrValue;
        this.timestampAttrName = timestampAttrName;
    }

    @Override
    public String preparedStatement() {
        return "UPDATE " + tableName + " SET " + timestampAttrName + "=?" + " WHERE " + idAttrName + "=?";
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setTimestamp(1, DateTimeUtils.nowTs());
            st.setLong(2, idAttrValue);
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}
