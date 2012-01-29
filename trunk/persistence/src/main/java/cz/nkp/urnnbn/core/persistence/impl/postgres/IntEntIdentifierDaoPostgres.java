/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.IntEntIdType;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.persistence.exceptions.IdPart;
import cz.nkp.urnnbn.core.persistence.exceptions.PersistenceException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.impl.operations.DaoOperation;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.IntEntIdentifierDAO;
import cz.nkp.urnnbn.core.persistence.IntelectualEntityDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.impl.AbstractDAO;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import cz.nkp.urnnbn.core.persistence.impl.operations.OperationUtils;
import cz.nkp.urnnbn.core.persistence.impl.operations.MultipleResultsOperation;
import cz.nkp.urnnbn.core.persistence.impl.statements.InsertIntEntIdentifier;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectAllAttrsByLongAttr;
import cz.nkp.urnnbn.core.persistence.impl.transformations.IntEntIdentifierRT;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Řehánek
 */
public class IntEntIdentifierDaoPostgres extends AbstractDAO implements IntEntIdentifierDAO {

    private static final Logger logger = Logger.getLogger(IntEntIdentifierDaoPostgres.class.getName());

    public IntEntIdentifierDaoPostgres(DatabaseConnector con) {
        super(con);
    }

    @Override
    public void insertIntEntId(final IntEntIdentifier identifier) throws DatabaseException, RecordNotFoundException, AlreadyPresentException {
        checkRecordExists(IntelectualEntityDAO.TABLE_NAME, IntelectualEntityDAO.ATTR_ID, identifier.getIntEntDbId());
        DaoOperation operation = new DaoOperation() {

            @Override
            public Object run(Connection connection) throws DatabaseException, SQLException {
                StatementWrapper insert = new InsertIntEntIdentifier(identifier);
                PreparedStatement insertSt = OperationUtils.preparedStatementFromWrapper(connection, insert);
                insertSt.executeUpdate();
                return null;
            }
        };
        try {
            runInTransaction(operation);
        } catch (PersistenceException ex) {
            //should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
        } catch (SQLException ex) {
            if ("23505".equals(ex.getSQLState())) {
                IdPart intEntId = new IdPart(IntEntIdentifierDAO.ATTR_IE_ID, Long.toString(identifier.getIntEntDbId()));
                IdPart type = new IdPart(IntEntIdentifierDAO.ATTR_TYPE, identifier.getType().toString());
                throw new AlreadyPresentException(new IdPart[]{intEntId, type});
            } else {
                throw new DatabaseException(ex);
            }
        }
    }

    @Override
    public List<IntEntIdentifier> getIdList(long intEntDbId) throws DatabaseException, RecordNotFoundException {
        checkRecordExists(IntelectualEntityDAO.TABLE_NAME, IntelectualEntityDAO.ATTR_ID, intEntDbId);
        try {
            StatementWrapper st = new SelectAllAttrsByLongAttr(TABLE_NAME, ATTR_IE_ID, intEntDbId);
            DaoOperation operation = new MultipleResultsOperation(st, new IntEntIdentifierRT());
            return (List<IntEntIdentifier>) runInTransaction(operation);
        } catch (PersistenceException ex) {
            //cannot happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return null;
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    @Override
    public void updateIntEntIdValue(IntEntIdentifier id) throws DatabaseException, RecordNotFoundException {
        //TODO
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteIntEntIdentifier(long intEntDbId, IntEntIdType type) throws DatabaseException, RecordNotFoundException {
        deleteRecordsByLongAndString(TABLE_NAME,
                IntEntIdentifierDAO.ATTR_IE_ID, intEntDbId,
                IntEntIdentifierDAO.ATTR_TYPE, type.name());
    }

    @Override
    public void deleteAllIntEntIdsOfEntity(long intEntDbId) throws DatabaseException, RecordNotFoundException {
        checkRecordExists(IntelectualEntityDAO.TABLE_NAME, IntelectualEntityDAO.ATTR_ID, intEntDbId);
        try {
            deleteRecordsById(TABLE_NAME, ATTR_IE_ID, intEntDbId, false);
        } catch (RecordNotFoundException ex) {
            //should never happen
            logger.log(Level.SEVERE, null, ex);
        }
    }
}
