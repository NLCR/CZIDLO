/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.dto.DigRepIdentifier;
import cz.nkp.urnnbn.core.dto.DigitalRepresentation;
import cz.nkp.urnnbn.core.persistence.impl.postgres.statements.SelectNewIdFromSequence;
import cz.nkp.urnnbn.core.persistence.exceptions.PersistenceException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.ArchiverDAO;
import cz.nkp.urnnbn.core.persistence.impl.operations.DaoOperation;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.DigRepIdentifierDAO;
import cz.nkp.urnnbn.core.persistence.DigitalRepresentationDAO;
import cz.nkp.urnnbn.core.persistence.IntelectualEntityDAO;
import cz.nkp.urnnbn.core.persistence.RegistrarDAO;
import cz.nkp.urnnbn.core.persistence.impl.AbstractDAO;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import cz.nkp.urnnbn.core.persistence.impl.operations.OperationUtils;
import cz.nkp.urnnbn.core.persistence.impl.operations.SingleResultOperation;
import cz.nkp.urnnbn.core.persistence.impl.statements.InsertDigitalRepresentation;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectRecordsCountByLongAttr;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectSingleAttrByLongStringString;
import cz.nkp.urnnbn.core.persistence.impl.statements.UpdateDigitalRepresentation;
import cz.nkp.urnnbn.core.persistence.impl.transformations.DigitalRepresentationRT;
import cz.nkp.urnnbn.core.persistence.impl.transformations.SingleIntRT;
import cz.nkp.urnnbn.core.persistence.impl.transformations.singleLongRT;
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
public class DigitalRepresentationDaoPostgres extends AbstractDAO implements DigitalRepresentationDAO {

    private static final Logger logger = Logger.getLogger(DigitalRepresentationDaoPostgres.class.getName());

    public DigitalRepresentationDaoPostgres(DatabaseConnector con) {
        super(con);
    }

    @Override
    public Long insertRepresentation(final DigitalRepresentation representation) throws DatabaseException, RecordNotFoundException {
        //TODO: melo by byt vsechno v transakci
        checkRecordExists(RegistrarDAO.TABLE_NAME, RegistrarDAO.ATTR_ID, representation.getRegistrarId());
        checkRecordExists(ArchiverDAO.TABLE_NAME, ArchiverDAO.ATTR_ID, representation.getArchiverId());
        checkRecordExists(IntelectualEntityDAO.TABLE_NAME, IntelectualEntityDAO.ATTR_ID, representation.getIntEntId());
        DaoOperation operation = new DaoOperation() {

            @Override
            public Object run(Connection connection) throws DatabaseException, SQLException {
                //get new id
                StatementWrapper newId = new SelectNewIdFromSequence(SEQ_NAME);
                PreparedStatement newIdSt = connection.prepareStatement(newId.preparedStatement());
                newId.populate(newIdSt);
                ResultSet idResultSet = newIdSt.executeQuery();
                Long id = OperationUtils.resultSet2Long(idResultSet);
                //set id
                representation.setId(id);
                //insert
                StatementWrapper insert = new InsertDigitalRepresentation(representation);
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
    public DigitalRepresentation getRepresentationByDbId(long dbId) throws DatabaseException, RecordNotFoundException {
        return (DigitalRepresentation) getRecordById(TABLE_NAME, ATTR_ID, dbId, new DigitalRepresentationRT());
    }

    @Override
    public Integer getDigRepCount(long registrarId) throws RecordNotFoundException, DatabaseException {
        checkRecordExists(RegistrarDAO.TABLE_NAME, RegistrarDAO.ATTR_ID, registrarId);
        StatementWrapper statement = new SelectRecordsCountByLongAttr(TABLE_NAME, ATTR_REGISTRAR_ID, registrarId);
        DaoOperation operation = new SingleResultOperation(statement, new SingleIntRT());
        try {
            return (Integer) runInTransaction(operation);
        } catch (PersistenceException ex) {
            //should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return null;
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    @Override
    public List<DigitalRepresentation> getRepresentationsOfIntEntity(long entityId) throws DatabaseException, RecordNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Long getDigRepDbIdByIdentifier(DigRepIdentifier id) throws DatabaseException, RecordNotFoundException {
        StatementWrapper statement = new SelectSingleAttrByLongStringString(
                DigRepIdentifierDAO.TABLE_NAME, DigRepIdentifierDAO.ATTR_DIG_REP_ID,
                DigRepIdentifierDAO.ATTR_REG_ID, id.getRegistrarId(),
                DigRepIdentifierDAO.ATTR_TYPE, id.getType().toString(),
                DigRepIdentifierDAO.ATTR_VALUE, id.getValue());
        DaoOperation operation = new SingleResultOperation(statement, new singleLongRT());
        try {
            return (Long) runInTransaction(operation);
        } catch (RecordNotFoundException e) {
            throw e;
        } catch (PersistenceException ex) {
            //should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return null;
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    @Override
    public void updateRepresentation(DigitalRepresentation representation) throws DatabaseException, RecordNotFoundException {
        updateRecordWithLongPK(representation, TABLE_NAME, ATTR_ID, new UpdateDigitalRepresentation(representation));
    }

    @Override
    public void deleteRepresentation(long digRepDbId) throws DatabaseException, RecordNotFoundException {
        //TODO: test
        //todo: nesmi se smazat urn
        deleteRecordsById(TABLE_NAME, ATTR_ID, digRepDbId);
    }

    @Override
    public void deleteAllRepresentations() throws DatabaseException {
        deleteAllRecords(DigitalRepresentationDAO.TABLE_NAME);
    }
}
