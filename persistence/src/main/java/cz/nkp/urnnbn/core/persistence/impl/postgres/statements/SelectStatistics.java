package cz.nkp.urnnbn.core.persistence.impl.postgres.statements;

import java.sql.PreparedStatement;

import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;

public class SelectStatistics implements StatementWrapper {

    public static final String RESULT_REGISTRAR_CODE = "registrarCode";
    public static final String RESULT_YEAR = "year";
    public static final String RESULT_MONTH = "month";
    public static final String RESULT_SUM = "sum";

    private final String tableName;
    private final String registrarCodeAttrName;
    private final String dateTimeAttrName;
    private final String activeAttrName;

    private final String registrarCode;
    private final boolean includeActive;
    private final boolean includeDeactivated;

    public SelectStatistics(String tableName, String registrarCodeAttrName, String dateTimeAttrName, String activeAttrName, String registrarCode,
            boolean includeActive, boolean includeDeactivated) {
        this.tableName = tableName;
        this.registrarCodeAttrName = registrarCodeAttrName;
        this.dateTimeAttrName = dateTimeAttrName;
        this.activeAttrName = activeAttrName;
        this.registrarCode = registrarCode;
        this.includeActive = includeActive;
        this.includeDeactivated = includeDeactivated;
    }

    @Override
    public String preparedStatement() {
        boolean filterActivityState = (includeActive && !includeDeactivated) || (!includeActive && includeDeactivated);
        boolean filterRegistrar = registrarCode != null;
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");
        builder.append(registrarCodeAttrName + " AS " + RESULT_REGISTRAR_CODE);
        builder.append(",to_number(to_char(" + dateTimeAttrName + ",'YYYY'),'9999') AS " + RESULT_YEAR);
        builder.append(",to_number(to_char(" + dateTimeAttrName + ",'MM'),'99') AS " + RESULT_MONTH);
        builder.append(",count(*) AS " + RESULT_SUM);
        builder.append(" FROM " + tableName);
        if (filterActivityState && filterRegistrar) {
            builder.append(" WHERE ");
            builder.append(registrarCodeAttrName + "='" + registrarCode + "'");
            builder.append(" AND ");
            boolean active = includeActive;
            builder.append(activeAttrName + "='" + active + "'");
        } else if (filterRegistrar) {
            builder.append(" WHERE ");
            builder.append(registrarCodeAttrName + "='" + registrarCode + "'");
        } else if (filterActivityState) {
            builder.append(" WHERE ");
            boolean active = includeActive;
            builder.append(activeAttrName + "='" + active + "'");
        }
        builder.append(" GROUP BY " + RESULT_REGISTRAR_CODE + "," + RESULT_YEAR + "," + RESULT_MONTH + ";");
        return builder.toString();
    }

    @Override
    public void populate(PreparedStatement st) throws SyntaxException {
        // nothing to populate
    }

}
