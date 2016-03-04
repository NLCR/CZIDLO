package cz.nkp.urnnbn.core.persistence.impl.postgres;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.SearchDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.PersistenceException;
import cz.nkp.urnnbn.core.persistence.impl.AbstractDAO;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import cz.nkp.urnnbn.core.persistence.impl.operations.DaoOperation;
import cz.nkp.urnnbn.core.persistence.impl.operations.MultipleResultsOperation;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectIdByFulltextSearch;
import cz.nkp.urnnbn.core.persistence.impl.transformations.SingleLongRT;

public class SearchDaoPostgres extends AbstractDAO implements SearchDAO {

    private static Logger LOGGER = Logger.getLogger(SearchDaoPostgres.class.getName());

    public SearchDaoPostgres(DatabaseConnector con) {
        super(con);
    }

    @Override
    public List<Long> listIeIdsByFulltextSearchOfIe(String[] queryTokens, Integer limit) throws DatabaseException {
        return searchTable(TABLE_IE_NAME, queryTokens, limit);
    }

    @Override
    public List<Long> listIeIdsByFulltextSearchOfDd(String[] queryTokens, Integer limit) throws DatabaseException {
        return searchTable(TABLE_DD_NAME, queryTokens, limit);
    }

    @Override
    public List<Long> listIeIdsByFulltextSearchOfRsi(String[] queryTokens, Integer limit) throws DatabaseException {
        return searchTable(TABLE_RSI_NAME, queryTokens, limit);
    }

    private List<Long> searchTable(String tableName, String[] queryTokens, Integer limit) throws DatabaseException {
        try {
            StatementWrapper st = new SelectIdByFulltextSearch(tableName, ATTR_ID, ATTR_VALUE, queryTokens, limit);
            DaoOperation operation = new MultipleResultsOperation(st, new SingleLongRT());
            return (List<Long>) runInTransaction(operation);
        } catch (PersistenceException ex) {
            // cannot happen
            LOGGER.log(Level.SEVERE, "Exception unexpected here", ex);
            return null;
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

}
