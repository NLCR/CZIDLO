package cz.nkp.urnnbn.core.persistence.impl.statements;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;

/**
 * 
 * @author xrosecky
 */
public class SelectIdByFulltextSearch implements StatementWrapper {

    private final String tableName;
    private final String attrIdName;
    private final String attrSearchableName;
    private final String query;
    private final Integer limit;

    public SelectIdByFulltextSearch(String tableName, String attrIdName, String attrSearchableName, String query, Integer limit) {
        this.tableName = tableName;
        this.attrIdName = attrIdName;
        this.attrSearchableName = attrSearchableName;
        this.query = query;
        this.limit = limit;
    }

    public String preparedStatement() {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT %s FROM %s WHERE TO_TSVECTOR('simple', LOWER(%s)) @@ to_tsquery('simple', lower(?))");
        if (limit != null) {
            builder.append(" limit ?");
        }
        String result = String.format(builder.toString(), attrIdName, tableName, attrSearchableName);
        // Logger logger = Logger.getLogger(SelectIdByFulltextSearch.class.getName());
        // logger.info(result);
        // logger.info("query: " + query);
        return result;
    }

    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setString(1, query);
            if (limit != null) {
                st.setLong(2, limit);
            }
        } catch (SQLException e) {
            // chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }

}
