/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.RegistrarScopeIdType;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.DigitalDocumentDAO;
import cz.nkp.urnnbn.core.persistence.RegistrarDAO;
import cz.nkp.urnnbn.core.persistence.RegistrarScopeIdentifierDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.IdPart;
import cz.nkp.urnnbn.core.persistence.exceptions.PersistenceException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordReferencedException;
import cz.nkp.urnnbn.core.persistence.impl.AbstractDAO;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import cz.nkp.urnnbn.core.persistence.impl.operations.DaoOperation;
import cz.nkp.urnnbn.core.persistence.impl.operations.MultipleResultsOperation;
import cz.nkp.urnnbn.core.persistence.impl.operations.NoResultOperation;
import cz.nkp.urnnbn.core.persistence.impl.operations.OperationUtils;
import cz.nkp.urnnbn.core.persistence.impl.statements.InsertRegistrarScopeIdentifier;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectAllAttrsByLongAttr;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectAllAttrsByTimestamps;
import cz.nkp.urnnbn.core.persistence.impl.statements.UpdateRegistrarScopeIdentifier;
import cz.nkp.urnnbn.core.persistence.impl.transformations.RegistrarScopeIdentifierRT;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joda.time.DateTime;

/**
 *
 * @author Martin Řehánek
 */
public class RegistrarScopeIdentifierDaoPostgres extends AbstractDAO implements RegistrarScopeIdentifierDAO {

    private static final Logger logger = Logger.getLogger(RegistrarScopeIdentifierDaoPostgres.class.getName());

    public RegistrarScopeIdentifierDaoPostgres(DatabaseConnector con) {
        super(con);
    }

    @Override
    public void insertRegistrarScopeId(final RegistrarScopeIdentifier identifier) throws DatabaseException, RecordNotFoundException,
            AlreadyPresentException {
        checkRecordExists(RegistrarDAO.TABLE_NAME, RegistrarDAO.ATTR_ID, identifier.getRegistrarId());
        checkRecordExists(DigitalDocumentDAO.TABLE_NAME, DigitalDocumentDAO.ATTR_ID, identifier.getDigDocId());
        DaoOperation operation = new DaoOperation() {

            @Override
            public Object run(Connection connection) throws DatabaseException, SQLException {
                StatementWrapper insert = new InsertRegistrarScopeIdentifier(identifier);
                PreparedStatement insertSt = OperationUtils.preparedStatementFromWrapper(connection, insert);
                insertSt.executeUpdate();
                return null;
            }
        };
        try {
            runInTransaction(operation);
        } catch (PersistenceException ex) {
            // should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
        } catch (SQLException ex) {
            if ("23505".equals(ex.getSQLState())) {
                IdPart intEntId = new IdPart(RegistrarScopeIdentifierDAO.ATTR_DIG_DOC_ID, Long.toString(identifier.getDigDocId()));
                IdPart type = new IdPart(RegistrarScopeIdentifierDAO.ATTR_TYPE, identifier.getType().toString());
                throw new AlreadyPresentException(new IdPart[] { intEntId, type });
            } else if ("23503".equals(ex.getSQLState())) {
                logger.log(Level.SEVERE, "Referenced record doesn't exist", ex);
                throw new RecordNotFoundException();
            } else {
                throw new DatabaseException(ex);
            }
        }
    }

    @Override
    public List<RegistrarScopeIdentifier> getRegistrarScopeIds(long digDocId) throws DatabaseException, RecordNotFoundException {
        checkRecordExists(DigitalDocumentDAO.TABLE_NAME, DigitalDocumentDAO.ATTR_ID, digDocId);
        try {
            StatementWrapper st = new SelectAllAttrsByLongAttr(TABLE_NAME, ATTR_DIG_DOC_ID, digDocId);
            DaoOperation operation = new MultipleResultsOperation(st, new RegistrarScopeIdentifierRT());
            return (List<RegistrarScopeIdentifier>) runInTransaction(operation);
        } catch (PersistenceException ex) {
            // cannot happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return null;
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    public RegistrarScopeIdentifier getRegistrarScopeId(Long digDocId, RegistrarScopeIdType type) throws DatabaseException, RecordNotFoundException {
        List<RegistrarScopeIdentifier> idList = getRegistrarScopeIds(digDocId);
        for (RegistrarScopeIdentifier id : idList) {
            if (id.getType().equals(type)) {
                return id;
            }
        }
        throw new RecordNotFoundException(TABLE_NAME);
    }

    public List<RegistrarScopeIdentifier> getRegistrarScopeIdsByTimestamps(DateTime from, DateTime until) throws DatabaseException {
        try {
            StatementWrapper st = new SelectAllAttrsByTimestamps(TABLE_NAME, ATTR_UPDATED, from, until);
            DaoOperation operation = new MultipleResultsOperation(st, new RegistrarScopeIdentifierRT());
            return (List<RegistrarScopeIdentifier>) runInTransaction(operation);
        } catch (PersistenceException ex) {
            // cannot happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return null;
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    @Override
    public void updateRegistrarScopeIdValue(RegistrarScopeIdentifier identifier) throws DatabaseException, RecordNotFoundException,
            AlreadyPresentException {
        checkRecordExists(RegistrarDAO.TABLE_NAME, RegistrarDAO.ATTR_ID, identifier.getRegistrarId());
        checkRecordExists(DigitalDocumentDAO.TABLE_NAME, DigitalDocumentDAO.ATTR_ID, identifier.getDigDocId());
        try {
            StatementWrapper updateSt = new UpdateRegistrarScopeIdentifier(identifier);
            DaoOperation operation = new NoResultOperation(updateSt);
            runInTransaction(operation);
        } catch (PersistenceException ex) {
            // should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Couldn't update " + TABLE_NAME + "registrarId: {0}, digDocId:{1}, type:{2}",
                    new Object[] { identifier.getRegistrarId(), identifier.getDigDocId(), identifier.getType().toString() });
            System.err.println("state:" + ex.getSQLState());
            if ("23505".equals(ex.getSQLState())) {
                IdPart intEntId = new IdPart(RegistrarScopeIdentifierDAO.ATTR_DIG_DOC_ID, Long.toString(identifier.getDigDocId()));
                IdPart type = new IdPart(RegistrarScopeIdentifierDAO.ATTR_TYPE, identifier.getType().toString());
                throw new AlreadyPresentException(new IdPart[] { intEntId, type });
            } else if ("23503".equals(ex.getSQLState())) {
                logger.log(Level.SEVERE, "Referenced record doesn't exist", ex);
                throw new RecordNotFoundException();
            } else {
                throw new DatabaseException(ex);
            }
        }
    }

    @Override
    public void deleteRegistrarScopeId(long digDocDbId, RegistrarScopeIdType type) throws DatabaseException, RecordNotFoundException {
        try {
            checkRecordExists(DigitalDocumentDAO.TABLE_NAME, DigitalDocumentDAO.ATTR_ID, digDocDbId);
            deleteRecordsByLongAndString(TABLE_NAME, ATTR_DIG_DOC_ID, digDocDbId, ATTR_TYPE, type.toString(), true);
        } catch (RecordReferencedException ex) {
            // should never happen
            logger.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void deleteRegistrarScopeIds(long digDocDbId) throws DatabaseException, RecordNotFoundException {
        checkRecordExists(DigitalDocumentDAO.TABLE_NAME, DigitalDocumentDAO.ATTR_ID, digDocDbId);
        try {
            deleteRecordsById(TABLE_NAME, ATTR_DIG_DOC_ID, digDocDbId, false);
        } catch (RecordNotFoundException ex) {
            // should never happen
            logger.log(Level.SEVERE, null, ex);
        } catch (RecordReferencedException ex) {
            // should never happen
            logger.log(Level.SEVERE, null, ex);
        }
    }
}
