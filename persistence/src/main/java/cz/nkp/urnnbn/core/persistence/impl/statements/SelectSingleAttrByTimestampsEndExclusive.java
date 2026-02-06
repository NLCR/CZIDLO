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
 * Select selectAttrName from table where timestampAttr >= from AND timestampAttr < until
 * More index-friendly than SelectSingleAttrByTimestampsString
 */
public class SelectSingleAttrByTimestampsEndExclusive implements StatementWrapper {

    private final String tableName;
    private final String timestampAttrName;
    private final DateTime from;
    private final DateTime until;
    private final String selectAttrName;

    public SelectSingleAttrByTimestampsEndExclusive(String tableName,
                                                    String timestampAttrName,
                                                    DateTime from,
                                                    DateTime until,
                                                    String selectAttrName) {
        this.tableName = tableName;
        this.timestampAttrName = timestampAttrName;
        this.from = from;
        this.until = until;
        this.selectAttrName = selectAttrName;
    }

    @Override
    public String preparedStatement() {
        if (from != null && until != null) {
            return "SELECT " + selectAttrName +
                    " FROM " + tableName +
                    " WHERE " + timestampAttrName + " >= ?" +
                    " AND " + timestampAttrName + " < ?";
        } else if (from != null) {
            return "SELECT " + selectAttrName +
                    " FROM " + tableName +
                    " WHERE " + timestampAttrName + " >= ?";
        } else if (until != null) {
            return "SELECT " + selectAttrName +
                    " FROM " + tableName +
                    " WHERE " + timestampAttrName + " < ?";
        } else {
            return "SELECT " + selectAttrName + " FROM " + tableName;
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
            } else if (from != null) {
                Timestamp fromTs = DateTimeUtils.datetimeToTimestamp(from);
                st.setTimestamp(1, fromTs);
            } else if (until != null) {
                Timestamp untilTs = DateTimeUtils.datetimeToTimestamp(until);
                st.setTimestamp(1, untilTs);
            } else {
                // nothing to populate
            }
        } catch (SQLException e) {
            throw new SyntaxException(e);
        }
    }
}
