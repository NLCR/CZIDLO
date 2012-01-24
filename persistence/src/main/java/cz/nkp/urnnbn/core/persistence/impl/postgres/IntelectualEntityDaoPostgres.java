/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.IntEntIdType;
import cz.nkp.urnnbn.core.EntityType;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.persistence.impl.postgres.statements.SelectNewIdFromSequence;
import cz.nkp.urnnbn.core.persistence.exceptions.PersistenceException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.impl.operations.DaoOperation;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.IntEntIdentifierDAO;
import cz.nkp.urnnbn.core.persistence.IntelectualEntityDAO;
import cz.nkp.urnnbn.core.persistence.impl.AbstractDAO;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import cz.nkp.urnnbn.core.persistence.impl.operations.OperationUtils;
import cz.nkp.urnnbn.core.persistence.impl.operations.MultipleResultsOperation;
import cz.nkp.urnnbn.core.persistence.impl.operations.SingleResultOperation;
import cz.nkp.urnnbn.core.persistence.impl.statements.InsertIntelectualEntity;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectIdentnifiersByStringAndStringAttrs;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectRecordsCount;
import cz.nkp.urnnbn.core.persistence.impl.statements.UpdateIntEntity;
import cz.nkp.urnnbn.core.persistence.impl.transformations.singleLongRT;
import cz.nkp.urnnbn.core.persistence.impl.transformations.IntEntityRT;
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
public class IntelectualEntityDaoPostgres extends AbstractDAO implements IntelectualEntityDAO {

    private static final Logger logger = Logger.getLogger(IntelectualEntityDaoPostgres.class.getName());

    public IntelectualEntityDaoPostgres(DatabaseConnector con) {
        super(con);
    }

    @Override
    public Long insertIntelectualEntity(final IntelectualEntity entity) throws DatabaseException {
        DaoOperation operation = new DaoOperation() {

            @Override
            public Object run(Connection connection) throws DatabaseException, SQLException {
                //get new id
                StatementWrapper newId = new SelectNewIdFromSequence(SEQ_NAME);
                PreparedStatement newIdSt = connection.prepareStatement(newId.preparedStatement());
                newId.populate(newIdSt);
                ResultSet idResultSet = newIdSt.executeQuery();
                Long id = OperationUtils.resultSet2Long(idResultSet);
                //insert with id
                entity.setId(id);
                StatementWrapper insert = new InsertIntelectualEntity(entity);
                PreparedStatement insertSt = OperationUtils.preparedStatementFromWrapper(connection, insert);
                insertSt.executeUpdate();
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
            throw new DatabaseException(ex);
        }
    }

    @Override
    public IntelectualEntity getEntityByDbId(long dbId) throws DatabaseException, RecordNotFoundException {
        return (IntelectualEntity) getRecordById(TABLE_NAME, ATTR_ID, dbId, new IntEntityRT());
    }

    @Override
    public List<Long> getEntitiesDbIdByIdentifier(IntEntIdType type, String idValue) throws DatabaseException {
        try {
            StatementWrapper st = new SelectIdentnifiersByStringAndStringAttrs(
                    IntEntIdentifierDAO.TABLE_NAME,
                    IntEntIdentifierDAO.ATTR_IE_ID,
                    IntEntIdentifierDAO.ATTR_TYPE, type.name(),
                    IntEntIdentifierDAO.ATTR_VALUE, idValue);
            DaoOperation operation = new MultipleResultsOperation(st, new singleLongRT());
            return (List<Long>) runInTransaction(operation);
        } catch (PersistenceException ex) {
            //cannot happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return null;
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    @Override
    public Long getEntitiesCount() throws DatabaseException {
        StatementWrapper statement = new SelectRecordsCount(TABLE_NAME);
        DaoOperation operation = new SingleResultOperation(statement, new singleLongRT());
        try {
            return (Long) runInTransaction(operation);
        } catch (PersistenceException ex) {
            //should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return null;
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    @Override
    public Long getEntitiesCount(EntityType type) throws DatabaseException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updateEntity(IntelectualEntity entity) throws DatabaseException, RecordNotFoundException {
        updateRecordWithLongPK(entity, TABLE_NAME, ATTR_ID, new UpdateIntEntity(entity));
    }

    @Override
    public void deleteEntity(long id) throws DatabaseException, RecordNotFoundException {
        deleteRecordsById(TABLE_NAME, ATTR_ID, id);
    }

    @Override
    public void deleteAllEntities() throws DatabaseException {
        deleteAllRecords(TABLE_NAME);
    }
}
