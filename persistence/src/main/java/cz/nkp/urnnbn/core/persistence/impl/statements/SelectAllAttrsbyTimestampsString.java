/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.statements;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.joda.time.DateTime;

import cz.nkp.urnnbn.core.persistence.DateTimeUtils;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;

/**
 *
 * @author Martin Řehánek
 */
public class SelectAllAttrsbyTimestampsString implements StatementWrapper {

    private final String tableName;
    private final String timstampAttrName;
    private final DateTime from;
    private final DateTime until;
    private final String stringAttrName;
    private final String stringAttrValue;

    public SelectAllAttrsbyTimestampsString(String tableName, String timstampAttrName, DateTime from, DateTime until, String stringAttrName,
            String stringAttrValue) {
        this.tableName = tableName;
        this.timstampAttrName = timstampAttrName;
        this.from = from;
        this.until = until;
        this.stringAttrName = stringAttrName;
        this.stringAttrValue = stringAttrValue;
    }

    @Override
    public String preparedStatement() {
        if (from != null && until != null) {
            return "SELECT * from " + tableName + " WHERE " + stringAttrName + "=?" + " AND " + "(date_trunc('second'," + timstampAttrName
                    + ")=date_trunc('second',?::timestamp)" + " OR " + "date_trunc('second'," + timstampAttrName
                    + ")=date_trunc('second',?::timestamp)" + " OR " + "(extract(seconds from AGE(" + timstampAttrName + ",?)) >1" + " AND "
                    + "extract (seconds from AGE(?," + timstampAttrName + ")) >1" + "))";
        } else if (from != null) { // until == null
            return "SELECT * from " + tableName + " WHERE " + stringAttrName + "=?" + " AND " + "(date_trunc('second'," + timstampAttrName
                    + ")=date_trunc('second',?::timestamp)" + " OR " + "AGE(" + timstampAttrName + ",?) >interval '1 seconds'" + ")";
        } else if (until != null) {// from == null
            return "SELECT * from " + tableName + " WHERE " + stringAttrName + "=?" + " AND " + "(date_trunc('second'," + timstampAttrName
                    + ")=date_trunc('second',?::timestamp)" + " OR " + "AGE(?," + timstampAttrName + ") >interval '1 seconds'" + ")";
        } else { // both null - select all records
            return "SELECT * from " + tableName;
        }
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            if (from != null && until != null) {
                st.setString(1, stringAttrValue);
                Timestamp fromTs = DateTimeUtils.datetimeToTimestamp(from);
                Timestamp untilTs = DateTimeUtils.datetimeToTimestamp(until);
                st.setTimestamp(2, fromTs);
                st.setTimestamp(3, untilTs);
                st.setTimestamp(4, fromTs);
                st.setTimestamp(5, untilTs);
            } else if (from != null) { // until == null
                st.setString(1, stringAttrValue);
                Timestamp fromTs = DateTimeUtils.datetimeToTimestamp(from);
                st.setTimestamp(2, fromTs);
                st.setTimestamp(3, fromTs);
            } else if (until != null) { // from == null
                st.setString(1, stringAttrValue);
                Timestamp untilTs = DateTimeUtils.datetimeToTimestamp(until);
                st.setTimestamp(2, untilTs);
                st.setTimestamp(3, untilTs);
            } else {
            } // both null - nothing to populate
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
}
