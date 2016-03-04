package cz.nkp.urnnbn.core.persistence.impl.statements;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;

/**
 * 
 * @author xrosecky
 */
public class SelectIdByFulltextSearch implements StatementWrapper {

    static final Logger LOGGER = Logger.getLogger(SelectIdByFulltextSearch.class.getName());

    private static final int MIN_GENERAL_TOKEN_LENGTH = 3;
    private static final int MAX_GENERAL_TOKEN_LENGTH = 100;
    private static final int MAX_NUMBER_TOKEN_LENGTH = 20;

    private final String tableName;
    private final String attrIdName;
    private final String attrSearchableName;
    private final String[] queryTokens;
    private final Integer limit;

    public SelectIdByFulltextSearch(String tableName, String attrIdName, String attrSearchableName, String[] queryTokens, Integer limit) {
        this.tableName = tableName;
        this.attrIdName = attrIdName;
        this.attrSearchableName = attrSearchableName;
        this.queryTokens = queryTokens;
        this.limit = limit;
    }

    public String preparedStatement() {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT %s FROM %s WHERE TO_TSVECTOR('simple', LOWER(%s)) @@ to_tsquery('simple', lower(?))");
        if (limit != null) {
            builder.append(" limit ?");
        }
        String result = String.format(builder.toString(), attrIdName, tableName, attrSearchableName);
        // LOGGER.info("statement: " + result);
        // LOGGER.info("tokens: " + toString(queryTokens));
        return result;
    }

    private String toQuery(String[] tokens) {
        StringBuilder query = new StringBuilder();
        boolean atLeastOneAcceptedToken = false;
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i].toLowerCase();
            // LOGGER.info("token: " + token);
            token = normalizeIfIsbn(token);
            // LOGGER.info("token (after normalizeIsbn): " + token);
            token = normalizeIfIssn(token);
            // LOGGER.info("token (after normalizeIssn): " + token);
            token = normalizeIfCcnb(token);
            // LOGGER.info("token (after normalizeCcnb): " + token);
            token = removeSpecialCharacters(token);
            // LOGGER.info("token (after removeSpecialChars): " + token);
            if (!token.isEmpty() && (isNumber(token) && token.length() <= MAX_NUMBER_TOKEN_LENGTH)
                    || (token.length() >= MIN_GENERAL_TOKEN_LENGTH && token.length() <= MAX_GENERAL_TOKEN_LENGTH)) {
                if (atLeastOneAcceptedToken) {
                    query.append('&');
                }
                query.append(token);
                atLeastOneAcceptedToken = true;
            }
        }
        String queryStr = query.toString();
        if (queryStr.isEmpty()) {
            return "' '";
        } else {
            return "'" + queryStr + "'";
        }
    }

    private String removeSpecialCharacters(String unfilteredQuery) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < unfilteredQuery.length(); i++) {
            char c = unfilteredQuery.charAt(i);
            if (Character.isLetterOrDigit(c) || isAllowedChar(c)) {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    private boolean isAllowedChar(char c) {
        char[] chars = new char[] { '.', '-', ',', '_', ':' };
        for (char allowed : chars) {
            if (c == allowed) {
                return true;
            }
        }
        return false;
    }

    private String normalizeIfCcnb(String token) {
        String[] prefixes = new String[] { "čnb", "ččnb", "ccnb" };
        String standardPrefix = "cnb";
        for (String prefix : prefixes) {
            if (token.startsWith(prefix)) {
                String normalized = standardPrefix + token.substring(prefix.length());
                // LOGGER.info("CCNB normalized: '" + normalized + "'");
                return normalized;
            }
        }
        // no prefix found
        return token;
    }

    private String normalizeIfIssn(String query) {
        String[] prefixes = new String[] { "issn", "issn:" };
        for (String prefix : prefixes) {
            if (query.startsWith(prefix)) {
                String normalized = query.substring(prefix.length());
                // LOGGER.info("ISSN normalized: '" + normalized + "'");
                return normalized;
            }
        }
        // no prefix found
        return query;
    }

    private String normalizeIfIsbn(String query) {
        // query = query.toUpperCase();
        String[] preficies = new String[] { "isbn", "isbn:" };
        for (String prefix : preficies) {
            if (query.startsWith(prefix)) {
                String normalized = query.substring(prefix.length());
                // LOGGER.info("ISBN normalized: '" + normalized + "'");
                return normalized;
            }
        }
        // no prefix found
        return query;
    }

    private boolean isNumber(String trimmed) {
        try {
            Integer.valueOf(trimmed);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private String toString(String[] words) {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        for (int i = 0; i < words.length; i++) {
            builder.append('"').append(words[i]).append('"');
            if (i < words.length - 1) {
                builder.append(',');
            }
        }
        builder.append(']');
        return builder.toString();
    }

    public void populate(PreparedStatement st) throws SyntaxException {
        try {
            String query = toQuery(queryTokens);
            // LOGGER.info("query: " + query);
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
