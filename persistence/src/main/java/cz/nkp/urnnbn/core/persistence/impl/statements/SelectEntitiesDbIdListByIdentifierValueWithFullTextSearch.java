package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.persistence.IntEntIdentifierDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author xrosecky
 */
public class SelectEntitiesDbIdListByIdentifierValueWithFullTextSearch implements StatementWrapper {

    private String query;
    private int offset;
    private int limit;

    public SelectEntitiesDbIdListByIdentifierValueWithFullTextSearch(String query, int offset, int limit) {
        this.query = query;
        this.offset = offset;
        this.limit = limit;
    }
    
    public String preparedStatement() {
        return String.format("SELECT %s FROM %s WHERE TO_TSVECTOR('simple', LOWER(%s)) @@ to_tsquery(lower(?)) offset ? limit ?", 
                IntEntIdentifierDAO.ATTR_IE_ID, IntEntIdentifierDAO.TABLE_NAME,
                IntEntIdentifierDAO.ATTR_VALUE);
    }

    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            st.setString(1, query);
            st.setLong(2, offset);
            st.setLong(3, limit);
        } catch (SQLException e) {
            //chyba je v prepared statementu nebo v tranfsformaci resultSetu
            throw new SyntaxException(e);
        }
    }
    
}
