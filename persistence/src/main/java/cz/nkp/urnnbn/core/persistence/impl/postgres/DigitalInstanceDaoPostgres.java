/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.nkp.urnnbn.core.persistence.impl.statements.*;
import org.joda.time.DateTime;

import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.DigitalDocumentDAO;
import cz.nkp.urnnbn.core.persistence.DigitalInstanceDAO;
import cz.nkp.urnnbn.core.persistence.DigitalLibraryDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.PersistenceException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.impl.AbstractDAO;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import cz.nkp.urnnbn.core.persistence.impl.operations.DaoOperation;
import cz.nkp.urnnbn.core.persistence.impl.operations.MultipleResultsOperation;
import cz.nkp.urnnbn.core.persistence.impl.operations.NoResultOperation;
import cz.nkp.urnnbn.core.persistence.impl.operations.OperationUtils;
import cz.nkp.urnnbn.core.persistence.impl.postgres.statements.SelectNewIdFromSequence;
import cz.nkp.urnnbn.core.persistence.impl.transformations.DigitalInstanceRT;

/**
 * 
 * @author Martin Řehánek
 */
public class DigitalInstanceDaoPostgres extends AbstractDAO implements DigitalInstanceDAO {

    private static final Logger logger = Logger.getLogger(DigitalInstanceDaoPostgres.class.getName());

    public DigitalInstanceDaoPostgres(DatabaseConnector con) {
        super(con);
    }

    @Override
    public Long insertDigInstance(final DigitalInstance instance) throws DatabaseException, RecordNotFoundException {
        checkRecordExists(DigitalDocumentDAO.TABLE_NAME, DigitalDocumentDAO.ATTR_ID, instance.getDigDocId());
        checkRecordExists(DigitalLibraryDAO.TABLE_NAME, DigitalLibraryDAO.ATTR_ID, instance.getLibraryId());
        DaoOperation operation = new DaoOperation() {
            @Override
            public Object run(Connection connection) throws DatabaseException, SQLException {
                // get new id from sequence
                StatementWrapper newId = new SelectNewIdFromSequence(SEQ_NAME);
                PreparedStatement newIdSt = connection.prepareStatement(newId.preparedStatement());
                newId.populate(newIdSt);
                ResultSet idResultSet = newIdSt.executeQuery();
                Long id = OperationUtils.resultSet2Long(idResultSet);
                // insert record with id from sequence
                instance.setId(id);
                PreparedStatement insert = OperationUtils.preparedStatementFromWrapper(connection, new InsertDigInstance(instance));
                insert.executeUpdate();
                return id;
            }
        };
        try {
            return (Long) runInTransaction(operation);
        } catch (PersistenceException ex) {
            // should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return null;
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    @Override
    public DigitalInstance getDigInstanceById(long id) throws DatabaseException, RecordNotFoundException {
        return (DigitalInstance) getRecordById(TABLE_NAME, ATTR_ID, id, new DigitalInstanceRT());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DigitalInstance> getDigitalInstancesOfDigDoc(long digDocId) throws DatabaseException, RecordNotFoundException {
        checkRecordExists(DigitalDocumentDAO.TABLE_NAME, DigitalDocumentDAO.ATTR_ID, digDocId);
        try {
            StatementWrapper st = new SelectAllAttrsByLongAttr(TABLE_NAME, ATTR_DIG_DOC_ID, digDocId);
            DaoOperation operation = new MultipleResultsOperation(st, new DigitalInstanceRT());
            return (List<DigitalInstance>) runInTransaction(operation);
        } catch (PersistenceException ex) {
            // cannot happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return null;
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    // TODO: test
    @Override
    @SuppressWarnings("unchecked")
    public List<DigitalInstance> getDigitalInstancesByTimestamps(DateTime from, DateTime until) throws DatabaseException {
        try {
            StatementWrapper st = new SelectAllAttrsByTimestamps(TABLE_NAME, ATTR_DEACTIVATED, from, until);
            DaoOperation operation = new MultipleResultsOperation(st, new DigitalInstanceRT());
            return (List<DigitalInstance>) runInTransaction(operation);
        } catch (PersistenceException ex) {
            // cannot happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return null;
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    // TODO: test
    @Override
    public List<DigitalInstance> getDigitalInstancesByUrl(String url) throws DatabaseException {
        try {
            StatementWrapper st = new SelectAllAttrsByStringAttr(TABLE_NAME, ATTR_URL, url);
            DaoOperation operation = new MultipleResultsOperation(st, new DigitalInstanceRT());
            return (List<DigitalInstance>) runInTransaction(operation);
        } catch (PersistenceException ex) {
            // cannot happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return null;
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    // TODO: test
    @Override
    public long getTotalCount() throws DatabaseException {
        return getAllRecordsCount(TABLE_NAME);
    }

    @Override
    public void deactivateDigInstance(long digInstId) throws DatabaseException, RecordNotFoundException {
        try {
            StatementWrapper st = new DeactivateDigitalInstance(digInstId);
            DaoOperation operation = new NoResultOperation(st);
            runInTransaction(operation);
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        } catch (PersistenceException ex) {
            // should never happen
            logger.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void updateDigInstance(DigitalInstance instance) throws DatabaseException, RecordNotFoundException {
        checkRecordExists(DigitalDocumentDAO.TABLE_NAME, DigitalDocumentDAO.ATTR_ID, instance.getDigDocId());
        checkRecordExists(DigitalLibraryDAO.TABLE_NAME, DigitalLibraryDAO.ATTR_ID, instance.getLibraryId());
        updateRecordWithLongPK(instance, TABLE_NAME, ATTR_ID, new UpdateDigitalInstance(instance));
    }
}
