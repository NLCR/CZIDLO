/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.DigDocIdType;
import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
import cz.nkp.urnnbn.core.persistence.exceptions.IdPart;
import cz.nkp.urnnbn.core.persistence.exceptions.PersistenceException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.impl.operations.DaoOperation;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.DigDocIdentifierDAO;
import cz.nkp.urnnbn.core.persistence.DigitalDocumentDAO;
import cz.nkp.urnnbn.core.persistence.RegistrarDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.impl.AbstractDAO;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import cz.nkp.urnnbn.core.persistence.impl.operations.OperationUtils;
import cz.nkp.urnnbn.core.persistence.impl.operations.MultipleResultsOperation;
import cz.nkp.urnnbn.core.persistence.impl.operations.NoResultOperation;
import cz.nkp.urnnbn.core.persistence.impl.statements.InsertDigRepIdentifier;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectAllAttrsByLongAttr;
import cz.nkp.urnnbn.core.persistence.impl.statements.UpdateDigDocIdentifier;
import cz.nkp.urnnbn.core.persistence.impl.transformations.DigDocIdentifierRT;
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
public class DigDocIdentifierDaoPostgres extends AbstractDAO implements DigDocIdentifierDAO {

    private static final Logger logger = Logger.getLogger(DigDocIdentifierDaoPostgres.class.getName());

    public DigDocIdentifierDaoPostgres(DatabaseConnector con) {
        super(con);
    }

    @Override
    public void insertDigDocId(final DigDocIdentifier identifier) throws DatabaseException, RecordNotFoundException, AlreadyPresentException {
        checkRecordExists(RegistrarDAO.TABLE_NAME, RegistrarDAO.ATTR_ID, identifier.getRegistrarId());
        checkRecordExists(DigitalDocumentDAO.TABLE_NAME, DigitalDocumentDAO.ATTR_ID, identifier.getDigDocId());
        DaoOperation operation = new DaoOperation() {

            @Override
            public Object run(Connection connection) throws DatabaseException, SQLException {
                StatementWrapper insert = new InsertDigRepIdentifier(identifier);
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
                IdPart intEntId = new IdPart(DigDocIdentifierDAO.ATTR_DIG_REP_ID, Long.toString(identifier.getDigDocId()));
                IdPart type = new IdPart(DigDocIdentifierDAO.ATTR_TYPE, identifier.getType().toString());
                throw new AlreadyPresentException(new IdPart[]{intEntId, type});
            } else if ("23503".equals(ex.getSQLState())) {
                logger.log(Level.SEVERE, "Referenced record doesn't exist", ex);
                throw new RecordNotFoundException();
            } else {
                throw new DatabaseException(ex);
            }
        }
    }

    @Override
    public List<DigDocIdentifier> getIdList(long digRepId) throws DatabaseException, RecordNotFoundException {
        checkRecordExists(DigitalDocumentDAO.TABLE_NAME, DigitalDocumentDAO.ATTR_ID, digRepId);
        try {
            StatementWrapper st = new SelectAllAttrsByLongAttr(TABLE_NAME, ATTR_DIG_REP_ID, digRepId);
            DaoOperation operation = new MultipleResultsOperation(st, new DigDocIdentifierRT());
            return (List<DigDocIdentifier>) runInTransaction(operation);
        } catch (PersistenceException ex) {
            //cannot happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return null;
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    @Override
    public void updateDigRepIdValue(DigDocIdentifier identifier) throws DatabaseException, RecordNotFoundException, AlreadyPresentException {
        checkRecordExists(RegistrarDAO.TABLE_NAME, RegistrarDAO.ATTR_ID, identifier.getRegistrarId());
        checkRecordExists(DigitalDocumentDAO.TABLE_NAME, DigitalDocumentDAO.ATTR_ID, identifier.getDigDocId());
        try {
            StatementWrapper updateSt = new UpdateDigDocIdentifier(identifier);
            DaoOperation operation = new NoResultOperation(updateSt);
            runInTransaction(operation);
        } catch (PersistenceException ex) {
            //should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Couldn't update " + TABLE_NAME + "registrarId: {0},digRepId:{1}, type:{2}", new Object[]{identifier.getRegistrarId(), identifier.getDigDocId(), identifier.getType().toString()});
            System.err.println("state:" + ex.getSQLState());
            if ("23505".equals(ex.getSQLState())) {
                IdPart intEntId = new IdPart(DigDocIdentifierDAO.ATTR_DIG_REP_ID, Long.toString(identifier.getDigDocId()));
                IdPart type = new IdPart(DigDocIdentifierDAO.ATTR_TYPE, identifier.getType().toString());
                throw new AlreadyPresentException(new IdPart[]{intEntId, type});
            } else if ("23503".equals(ex.getSQLState())) {
                logger.log(Level.SEVERE, "Referenced record doesn't exist", ex);
                throw new RecordNotFoundException();
            } else {
                throw new DatabaseException(ex);
            }
        }
    }

    @Override
    public void deleteDigDocIdentifier(long digRepDbId, DigDocIdType type) throws DatabaseException, RecordNotFoundException {
        checkRecordExists(DigitalDocumentDAO.TABLE_NAME, DigitalDocumentDAO.ATTR_ID, digRepDbId);
        deleteRecordsByLongAndString(TABLE_NAME, ATTR_DIG_REP_ID, digRepDbId, ATTR_TYPE, type.toString(), true);
    }

    @Override
    public void deleteAllIdentifiersOfDigDoc(long digRepDbId) throws DatabaseException, RecordNotFoundException {
        checkRecordExists(DigitalDocumentDAO.TABLE_NAME, DigitalDocumentDAO.ATTR_ID, digRepDbId);
        try {
            deleteRecordsById(TABLE_NAME, ATTR_DIG_REP_ID, digRepDbId, false);
        } catch (RecordNotFoundException ex) {
            //should never happen
            logger.log(Level.SEVERE, null, ex);
        }
    }
}
