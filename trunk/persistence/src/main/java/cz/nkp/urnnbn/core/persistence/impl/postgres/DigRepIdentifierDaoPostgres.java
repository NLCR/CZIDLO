/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.DigRepIdType;
import cz.nkp.urnnbn.core.dto.DigRepIdentifier;
import cz.nkp.urnnbn.core.persistence.exceptions.IdPart;
import cz.nkp.urnnbn.core.persistence.exceptions.PersistenceException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.impl.operations.DaoOperation;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.DigRepIdentifierDAO;
import cz.nkp.urnnbn.core.persistence.DigitalRepresentationDAO;
import cz.nkp.urnnbn.core.persistence.RegistrarDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.impl.AbstractDAO;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import cz.nkp.urnnbn.core.persistence.impl.operations.OperationUtils;
import cz.nkp.urnnbn.core.persistence.impl.operations.MultipleResultsOperation;
import cz.nkp.urnnbn.core.persistence.impl.operations.NoResultOperation;
import cz.nkp.urnnbn.core.persistence.impl.statements.InsertDigRepIdentifier;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectAllAttrsByLongAttr;
import cz.nkp.urnnbn.core.persistence.impl.statements.UpdateDigRepIdentifier;
import cz.nkp.urnnbn.core.persistence.impl.transformations.DigRepIdentifierRT;
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
public class DigRepIdentifierDaoPostgres extends AbstractDAO implements DigRepIdentifierDAO {

    private static final Logger logger = Logger.getLogger(DigRepIdentifierDaoPostgres.class.getName());

    public DigRepIdentifierDaoPostgres(DatabaseConnector con) {
        super(con);
    }

    @Override
    public void insertDigRepId(final DigRepIdentifier identifier) throws DatabaseException, RecordNotFoundException, AlreadyPresentException {
        checkRecordExists(RegistrarDAO.TABLE_NAME, RegistrarDAO.ATTR_ID, identifier.getRegistrarId());
        checkRecordExists(DigitalRepresentationDAO.TABLE_NAME, DigitalRepresentationDAO.ATTR_ID, identifier.getDigRepId());
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
                IdPart intEntId = new IdPart(DigRepIdentifierDAO.ATTR_DIG_REP_ID, Long.toString(identifier.getDigRepId()));
                IdPart type = new IdPart(DigRepIdentifierDAO.ATTR_TYPE, identifier.getType().toString());
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
    public List<DigRepIdentifier> getIdList(long digRepId) throws DatabaseException, RecordNotFoundException {
        checkRecordExists(DigitalRepresentationDAO.TABLE_NAME, DigitalRepresentationDAO.ATTR_ID, digRepId);
        try {
            StatementWrapper st = new SelectAllAttrsByLongAttr(TABLE_NAME, ATTR_DIG_REP_ID, digRepId);
            DaoOperation operation = new MultipleResultsOperation(st, new DigRepIdentifierRT());
            return (List<DigRepIdentifier>) runInTransaction(operation);
        } catch (PersistenceException ex) {
            //cannot happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return null;
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    @Override
    public void updateDigRepIdValue(DigRepIdentifier identifier) throws DatabaseException, RecordNotFoundException, AlreadyPresentException {
        checkRecordExists(RegistrarDAO.TABLE_NAME, RegistrarDAO.ATTR_ID, identifier.getRegistrarId());
        checkRecordExists(DigitalRepresentationDAO.TABLE_NAME, DigitalRepresentationDAO.ATTR_ID, identifier.getDigRepId());
        try {
            StatementWrapper updateSt = new UpdateDigRepIdentifier(identifier);
            DaoOperation operation = new NoResultOperation(updateSt);
            runInTransaction(operation);
        } catch (PersistenceException ex) {
            //should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Couldn't update " + TABLE_NAME + "registrarId: {0},digRepId:{1}, type:{2}", new Object[]{identifier.getRegistrarId(), identifier.getDigRepId(), identifier.getType().toString()});
            System.err.println("state:" + ex.getSQLState());
            if ("23505".equals(ex.getSQLState())) {
                IdPart intEntId = new IdPart(DigRepIdentifierDAO.ATTR_DIG_REP_ID, Long.toString(identifier.getDigRepId()));
                IdPart type = new IdPart(DigRepIdentifierDAO.ATTR_TYPE, identifier.getType().toString());
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
    public void deleteDigRepIdentifier(long digRepDbId, DigRepIdType type) throws DatabaseException, RecordNotFoundException {
        deleteRecordsByLongAndString(TABLE_NAME, ATTR_DIG_REP_ID, digRepDbId, ATTR_TYPE, type.toString());
    }

    @Override
    public void deleteAllIdentifiersOfDigRep(long digRepDbId) throws DatabaseException, RecordNotFoundException {
        checkRecordExists(DigitalRepresentationDAO.TABLE_NAME, DigitalRepresentationDAO.ATTR_ID, digRepDbId);
        try {
            deleteRecordsById(TABLE_NAME, ATTR_DIG_REP_ID, digRepDbId, false);
        } catch (RecordNotFoundException ex) {
            //should never happen
            logger.log(Level.SEVERE, null, ex);
        }
    }
}
