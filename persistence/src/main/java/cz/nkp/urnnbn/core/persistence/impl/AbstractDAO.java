/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl;

import cz.nkp.urnnbn.core.dto.IdentifiableByLongAttribute;
import cz.nkp.urnnbn.core.persistence.exceptions.PersistenceException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.impl.operations.DaoOperation;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.IdPart;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordReferencedException;
import cz.nkp.urnnbn.core.persistence.impl.operations.OperationUtils;
import cz.nkp.urnnbn.core.persistence.impl.operations.MultipleResultsOperation;
import cz.nkp.urnnbn.core.persistence.impl.operations.SingleResultOperation;
import cz.nkp.urnnbn.core.persistence.impl.operations.NoResultOperation;
import cz.nkp.urnnbn.core.persistence.impl.postgres.statements.SelectNewIdFromSequence;
import cz.nkp.urnnbn.core.persistence.impl.statements.DeleteAllRecords;
import cz.nkp.urnnbn.core.persistence.impl.statements.DeleteByStringString;
import cz.nkp.urnnbn.core.persistence.impl.statements.DeleteRecordsByLongAndLongAttr;
import cz.nkp.urnnbn.core.persistence.impl.statements.DeleteRecordsByLongAndStringAttr;
import cz.nkp.urnnbn.core.persistence.impl.statements.DeleteRecordsByLongAttr;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectAllAttrs;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectAllIdentifiers;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectAllAttrsByLongAttr;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectRecordsContByStringAndLongAttrs;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectRecordsCount;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectRecordsCountByLongAttr;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectRecordsCountByStringAttr;
import cz.nkp.urnnbn.core.persistence.impl.transformations.singleLongRT;
import cz.nkp.urnnbn.core.persistence.impl.transformations.ResultsetTransformer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Řehánek
 */
public abstract class AbstractDAO {

    private static final Logger logger = Logger.getLogger(AbstractDAO.class.getName());
    private final DatabaseConnector connector;

    public AbstractDAO(DatabaseConnector connector) {
        this.connector = connector;
    }

    protected Object runInTransaction(DaoOperation operation) throws SQLException, DatabaseException, PersistenceException {
        Connection connection = connector.getConnection();
        try {
            logger.log(Level.FINE, "Transaction started");
            connection.setAutoCommit(false);
            Object result = operation.run(connection);
            connection.commit();
            logger.log(Level.FINE, "Transaction commited");
            return result;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Transaction failed, rolling back", ex);
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex1) {
                    logger.severe("Failed to roll back transaction");
                }
            }
            if ("23502".equals(ex.getSQLState())) {
                throw new NullPointerException();
            } else {
                throw ex;
            }
            //throw new DatabaseException(ex);
        } finally {
            logger.fine("releasing connection");
            connector.releaseConnection(connection);
        }
    }

    protected void checkRecordExists(String tableName, String idAttrName, Long idValue) throws DatabaseException, RecordNotFoundException {
        StatementWrapper statement = new SelectRecordsCountByLongAttr(tableName, idAttrName, idValue);
        if (!recordExists(statement, tableName)) {
            throw new RecordNotFoundException(tableName);
        }
    }

    protected boolean recordExists(String tableName, String idAttrName, Long idValue) throws DatabaseException {
        StatementWrapper statement = new SelectRecordsCountByLongAttr(tableName, idAttrName, idValue);
        return recordExists(statement, tableName);
    }

    protected void checkRecordExists(String tableName, String idAttrName, String idValue) throws DatabaseException, RecordNotFoundException {
        StatementWrapper statement = new SelectRecordsCountByStringAttr(tableName, idAttrName, idValue);
        checkRecordExists(statement, tableName);
    }

    protected boolean recordExists(String tableName, String idAttrName, String idValue) throws DatabaseException {
        StatementWrapper statement = new SelectRecordsCountByStringAttr(tableName, idAttrName, idValue);
        return recordExists(statement, tableName);
    }

    protected void checkRecordExists(String tableName,
            String longAttrName, Long longAttrValue,
            String stringAttrName, String stringAttrValue) throws DatabaseException, RecordNotFoundException {
        StatementWrapper statement = new SelectRecordsContByStringAndLongAttrs(tableName, longAttrName, longAttrValue, stringAttrName, stringAttrValue);
        checkRecordExists(statement, tableName);
    }

    private void checkRecordExists(final StatementWrapper statement, String tableName) throws DatabaseException, RecordNotFoundException {
        if (!recordExists(statement, tableName)) {
            throw new RecordNotFoundException(tableName);
        }
    }

    private boolean recordExists(final StatementWrapper statement, String tableName) throws DatabaseException {
        DaoOperation recordCount = new DaoOperation() {

            @Override
            public Object run(Connection connection) throws SQLException, PersistenceException {
                PreparedStatement st = OperationUtils.preparedStatementFromWrapper(connection, statement);
                ResultSet resultSet = st.executeQuery();
                return OperationUtils.resultSet2Integer(resultSet);
            }
        };
        try {
            Integer count = (Integer) runInTransaction(recordCount);
            return (count != 0);
        } catch (PersistenceException e) {
            //should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", e);
            throw new DatabaseException(e);
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    protected Long insertRecordWithIdFromSequence(
            final IdentifiableByLongAttribute dto,
            final String tableName,
            final String sequence,
            final StatementWrapper insertSt) throws DatabaseException, RecordNotFoundException {
        DaoOperation operation = new DaoOperation() {

            @Override
            public Object run(Connection connection) throws DatabaseException, SQLException {
                //get new id from sequence
                StatementWrapper newId = new SelectNewIdFromSequence(sequence);
                PreparedStatement newIdSt = connection.prepareStatement(newId.preparedStatement());
                newId.populate(newIdSt);
                ResultSet idResultSet = newIdSt.executeQuery();
                Long id = OperationUtils.resultSet2Long(idResultSet);
                //insert record with id from sequence
                dto.setId(id);
                PreparedStatement insert = OperationUtils.preparedStatementFromWrapper(connection, insertSt);
                insert.executeUpdate();
                return id;
            }
        };
        try {
            Long id = (Long) runInTransaction(operation);
            return id;
        } catch (PersistenceException ex) {
            //should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return null;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Cannot insert {0} {1}", new Object[]{tableName, dto.getId()});
            if ("23503".equals(ex.getSQLState())) {
                logger.log(Level.SEVERE, "Referenced record doesn't exist", ex);
                throw new RecordNotFoundException();
            } else {
                throw new DatabaseException(ex);
            }
        }
    }

    public Object getRecordById(String tableName, String idAttrName, long idValue, ResultsetTransformer transformer) throws DatabaseException, RecordNotFoundException {
        StatementWrapper statement = new SelectAllAttrsByLongAttr(tableName, idAttrName, idValue);
        DaoOperation operation = new SingleResultOperation(statement, transformer);
        try {
            return runInTransaction(operation);
        } catch (PersistenceException e) {
            if (e instanceof RecordNotFoundException) {
                logger.log(Level.INFO, "No such " + tableName + " with id {0}", idValue);
                throw (RecordNotFoundException) e;
            } else {
                //should never happen
                logger.log(Level.SEVERE, "Exception unexpected here", e);
                return null;
            }
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    /**
     * Only for table that has single value PK of type long. 
     * We must know the value of PK in advanced so this metod mostly used for cases
     * when PK is FK to another table. 
     * @param dto
     * @param tableName
     * @param idAttrName
     * @param st
     * @throws DatabaseException
     * @throws AlreadyPresentException if such PK value already exists
     * @throws RecordNotFoundException if some referenced record doesn't exist in another table
     */
    public void insertRecordWithLongPK(IdentifiableByLongAttribute dto, String tableName, String idAttrName, StatementWrapper st) throws DatabaseException, AlreadyPresentException, RecordNotFoundException {
        DaoOperation operation = new NoResultOperation(st);
        try {
            runInTransaction(operation);
        } catch (PersistenceException ex) {
            //should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Cannot insert {0} {1}", new Object[]{tableName, dto.getId()});
            if ("23505".equals(ex.getSQLState())) {
                IdPart param = new IdPart(idAttrName, Long.toString(dto.getId()));
                throw new AlreadyPresentException(param);
            } else if ("23503".equals(ex.getSQLState())) {
                logger.log(Level.SEVERE, "Referenced record doesn't exist", ex);
                throw new RecordNotFoundException();
            } else {
                throw new DatabaseException(ex);
            }
        }
    }

    /**
     * Only for table that has single value PK of type long. 
     * @param dto
     * @param tableName
     * @param idAttrName
     * @param updateSt
     * @throws DatabaseException
     * @throws RecordNotFoundException if some referenced record doesn't exist in another table
     */
    public void updateRecordWithLongPK(IdentifiableByLongAttribute dto, String tableName, String idAttrName, StatementWrapper updateSt) throws DatabaseException, RecordNotFoundException {
        //metoda checkRecordExists se musi zavolat, protoze jinak update nevrati
        //pocet zmenenych radku. Jde to delat nejak nestandardne pro oracle i postgres
        //no ale jednodussi asi bude zjistit touhle metodou, jestli existuje
        //to, ze checkovani neni v transakci nevadi
        //pokud by byl zaznam smazany po tom, c projde checkRecordExists, tak proste update projde
        //ale zadny zaznam nezmeni
        checkRecordExists(tableName, idAttrName, dto.getId());
        try {
            DaoOperation operation = new NoResultOperation(updateSt);
            runInTransaction(operation);
        } catch (PersistenceException ex) {
            //should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Cannot update {0} {1}", new Object[]{tableName, dto.getId()});
            if ("23503".equals(ex.getSQLState())) {
                logger.log(Level.SEVERE, "Referenced record doesn't exist", ex);
                throw new RecordNotFoundException();
            } else {
                throw new DatabaseException(ex);
            }
        }
    }

    public List<Long> getIdListOfAllRecords(String tableName, String idAttributeName) throws DatabaseException {
        try {
            StatementWrapper st = new SelectAllIdentifiers(tableName, idAttributeName);
            DaoOperation operation = new MultipleResultsOperation(st, new singleLongRT());
            return (List<Long>) runInTransaction(operation);
        } catch (PersistenceException ex) {
            //should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return null;
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    public Long getAllRecordsCount(String tableName) throws DatabaseException {
        try {
            StatementWrapper st = new SelectRecordsCount(tableName);
            DaoOperation operation = new SingleResultOperation(st, new singleLongRT());
            return (Long) runInTransaction(operation);
        } catch (PersistenceException ex) {
            //should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return null;
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    public Object getAllRecords(String tableName, ResultsetTransformer transformer) throws DatabaseException {
        try {
            StatementWrapper st = new SelectAllAttrs(tableName);
            DaoOperation operation = new MultipleResultsOperation(st, transformer);
            return runInTransaction(operation);
        } catch (PersistenceException ex) {
            //should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return null;
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    public void deleteRecordsById(String tableName, String idAttrName, long idValue, boolean mustExist) throws DatabaseException, RecordNotFoundException, RecordReferencedException {
        if (mustExist) {
            checkRecordExists(tableName, idAttrName, idValue);
        }
        try {
            StatementWrapper st = new DeleteRecordsByLongAttr(tableName, idAttrName, idValue);
            DaoOperation operation = new NoResultOperation(st);
            runInTransaction(operation);
        } catch (PersistenceException ex) {
            //should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Cannot delete from {0} where {1}={2}", new Object[]{tableName, idAttrName, idValue});
            if ("23503".equals(ex.getSQLState())) {
                logger.log(Level.SEVERE, "Record is referenced from another table", ex);
                throw new RecordReferencedException();
            } else {
                throw new DatabaseException(ex);
            }
        }
    }

    public void deleteRecordsByLongAndString(
            String tableName,
            String longAttrName, long longAttrValue,
            String stringAttrName, String stringAttrValue,
            boolean mustExist) throws DatabaseException, RecordNotFoundException, RecordReferencedException {
        if (mustExist) {
            checkRecordExists(tableName, longAttrName, longAttrValue, stringAttrName, stringAttrValue);
        }
        try {
            StatementWrapper st = new DeleteRecordsByLongAndStringAttr(
                    tableName,
                    longAttrName, longAttrValue,
                    stringAttrName, stringAttrValue);
            DaoOperation operation = new NoResultOperation(st);
            runInTransaction(operation);
        } catch (PersistenceException ex) {
            //should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Cannot delete from {0} where "
                    + "{1}={2} and {3}={4}",
                    new Object[]{tableName,
                        longAttrName, longAttrValue, stringAttrName, stringAttrValue});
            if ("23503".equals(ex.getSQLState())) {
                logger.log(Level.SEVERE, "Record is referenced from another table", ex);
                throw new RecordReferencedException();
            } else {
                throw new DatabaseException(ex);
            }
        }
    }

    public void deleteRecordsByLongAndLong(
            String tableName,
            String firstAttrName, long firstAttrValue,
            String secondAttrName, long secondAttrValue) throws DatabaseException, RecordNotFoundException, RecordReferencedException {
        try {
            StatementWrapper st = new DeleteRecordsByLongAndLongAttr(
                    tableName,
                    firstAttrName, firstAttrValue,
                    secondAttrName, secondAttrValue);
            DaoOperation operation = new NoResultOperation(st);
            runInTransaction(operation);
        } catch (PersistenceException ex) {
            //should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Cannot delete from {0} where "
                    + "{1}={2} and {3}={4}",
                    new Object[]{tableName,
                        firstAttrName, firstAttrValue, secondAttrName, secondAttrValue});
            if ("23503".equals(ex.getSQLState())) {
                logger.log(Level.SEVERE, "Record is referenced from another table", ex);
                throw new RecordReferencedException();
            } else {
                throw new DatabaseException(ex);
            }
        }
    }

    public void deleteRecordsByStringAndString(
            String tableName,
            String firstAttrName, String firstAttrValue,
            String secondAttrName, String secondAttrValue) throws DatabaseException, RecordNotFoundException, RecordReferencedException {
        try {
            StatementWrapper st = new DeleteByStringString(
                    tableName,
                    firstAttrName, firstAttrValue,
                    secondAttrName, secondAttrValue);
            DaoOperation operation = new NoResultOperation(st);
            runInTransaction(operation);
        } catch (PersistenceException ex) {
            //should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Cannot delete from {0} "
                    + "where {1}={2} and {3}={4}",
                    new Object[]{tableName,
                        firstAttrName, firstAttrValue, secondAttrName, secondAttrValue});
            if ("23503".equals(ex.getSQLState())) {
                logger.log(Level.SEVERE, "Record is referenced from another table", ex);
                throw new RecordReferencedException();
            } else {
                throw new DatabaseException(ex);
            }
        }
    }

    public void deleteAllRecords(String tableName) throws DatabaseException, RecordReferencedException {
        try {
            final StatementWrapper st = new DeleteAllRecords(tableName);
            DaoOperation operation = new NoResultOperation(st);
            runInTransaction(operation);
        } catch (PersistenceException ex) {
            //cannot happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Cannot delete all records from {0}", tableName);
            if ("23503".equals(ex.getSQLState())) {
                logger.log(Level.SEVERE, "Record is referenced from another table", ex);
                throw new RecordReferencedException();
            } else {
                throw new DatabaseException(ex);
            }
        }
    }
}
