package cz.nkp.urnnbn.core.persistence.impl.statements;

import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 
 * @author xrosecky
 */
public class SelectEntitiesDbIdListByIdentifierValueWithFullTextSearch implements StatementWrapper {

	private static final String TABLE_NAME = "IE_TITLE";
	private static final String ATTR_ID = "ID";
	private static final String ATTR_TITLE = "TITLE";

	private final String query;
	private final Integer offset;
	private final Integer limit;

	public SelectEntitiesDbIdListByIdentifierValueWithFullTextSearch(String query, Integer offset, Integer limit) {
		this.query = query;
		this.offset = offset;
		this.limit = limit;
	}

	public SelectEntitiesDbIdListByIdentifierValueWithFullTextSearch(String query, Integer limit) {
		this(query, null, limit);
	}

	public SelectEntitiesDbIdListByIdentifierValueWithFullTextSearch(String query) {
		this(query, null, null);
	}

	public String preparedStatement() {
		StringBuilder builder = new StringBuilder();
		builder.append("SELECT %s FROM %s WHERE TO_TSVECTOR('simple', LOWER(%s)) @@ to_tsquery('simple', lower(?))");
		if (offset != null) {
			builder.append(" offset ?");
		}
		if (limit != null) {
			builder.append(" limit ?");
		}
		return String.format(builder.toString(), ATTR_ID, TABLE_NAME, ATTR_TITLE);
	}

	public void populate(PreparedStatement st) throws SyntaxException {
		try {
			st.setString(1, query);
			if (offset == null & limit != null) {
				st.setLong(2, limit);
			} else if (limit == null & offset != null) {
				st.setLong(2, offset);
			} else if (offset != null & limit != null) {
				st.setLong(2, limit);
				st.setLong(3, limit);
			}
		} catch (SQLException e) {
			// chyba je v prepared statementu nebo v tranfsformaci resultSetu
			throw new SyntaxException(e);
		}
	}

}
