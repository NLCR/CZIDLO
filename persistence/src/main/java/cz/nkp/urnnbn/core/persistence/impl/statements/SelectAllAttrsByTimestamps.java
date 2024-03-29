/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import org.joda.time.DateTime;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * @author Martin Řehánek
 */
public class SelectAllAttrsByTimestamps implements StatementWrapper {

    private final String tableName;
    private final String timstampAttrName;
    private final DateTime from;
    private final DateTime until;

    public SelectAllAttrsByTimestamps(String tableName, String timstampAttrName, DateTime from, DateTime until) {
        this.tableName = tableName;
        this.timstampAttrName = timstampAttrName;
        this.from = from;
        this.until = until;
    }

    @Override
    public String preparedStatement() {
        if (from != null && until != null) {
            return "SELECT * from " + tableName + " WHERE "
                    + "date_trunc('second'," + timstampAttrName + ")=date_trunc('second',?::timestamp)"
                    + " OR " + "date_trunc('second'," + timstampAttrName + ")=date_trunc('second',?::timestamp)" + " OR "
                    + "("
                    + "AGE(" + timstampAttrName + ",?) >interval '1 seconds'"
                    + " AND "
                    + "AGE(?," + timstampAttrName + ") >interval '1 seconds'"
                    + ")";
        } else if (from != null) { // until == null
            return "SELECT * from " + tableName + " WHERE "
                    + "date_trunc('second'," + timstampAttrName + ")=date_trunc('second',?::timestamp)"
                    + " OR "
                    + "AGE(" + timstampAttrName + ",?) >interval '1 seconds'";
        } else if (until != null) {// from == null
            return "SELECT * from " + tableName + " WHERE "
                    + "date_trunc('second'," + timstampAttrName + ")=date_trunc('second',?::timestamp)"
                    + " OR "
                    + "AGE(?," + timstampAttrName + ") >interval '1 seconds'";
        } else { // both null - select all records
            return "SELECT * from " + tableName;
        }
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            if (from != null && until != null) {
                Timestamp fromTs = DateTimeUtils.datetimeToTimestamp(from);
                Timestamp untilTs = DateTimeUtils.datetimeToTimestamp(until);
                st.setTimestamp(1, fromTs);
                st.setTimestamp(2, untilTs);
                st.setTimestamp(3, fromTs);
                st.setTimestamp(4, untilTs);
            } else if (from != null) { // until == null
                Timestamp fromTs = DateTimeUtils.datetimeToTimestamp(from);
                st.setTimestamp(1, fromTs);
                st.setTimestamp(2, fromTs);
            } else if (until != null) { // from == null
                Timestamp untilTs = DateTimeUtils.datetimeToTimestamp(until);
                st.setTimestamp(1, untilTs);
                st.setTimestamp(2, untilTs);
            } else {
            } // both null - nothing to populate
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}
