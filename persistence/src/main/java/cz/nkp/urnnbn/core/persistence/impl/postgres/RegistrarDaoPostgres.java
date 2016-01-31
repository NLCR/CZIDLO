/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.persistence.ArchiverDAO;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.RegistrarDAO;
import cz.nkp.urnnbn.core.persistence.UserDAO;
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
import cz.nkp.urnnbn.core.persistence.impl.operations.OperationUtils;
import cz.nkp.urnnbn.core.persistence.impl.operations.SingleResultOperation;
import cz.nkp.urnnbn.core.persistence.impl.postgres.statements.SelectNewIdFromSequence;
import cz.nkp.urnnbn.core.persistence.impl.statements.DeleteRecordsByLongAttr;
import cz.nkp.urnnbn.core.persistence.impl.statements.InsertArchiver;
import cz.nkp.urnnbn.core.persistence.impl.statements.InsertRegistrar;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectAllAttrsByStringAttr;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectIdentifiersAll;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectSingleAttrByLong;
import cz.nkp.urnnbn.core.persistence.impl.statements.UpdateArchiver;
import cz.nkp.urnnbn.core.persistence.impl.statements.UpdateRegistrar;
import cz.nkp.urnnbn.core.persistence.impl.transformations.ArchiverRT;
import cz.nkp.urnnbn.core.persistence.impl.transformations.RegistrarRT;
import cz.nkp.urnnbn.core.persistence.impl.transformations.SingleLongRT;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Řehánek
 */
public class RegistrarDaoPostgres extends AbstractDAO implements RegistrarDAO {

    private static final Logger logger = Logger.getLogger(RegistrarDaoPostgres.class.getName());

    public RegistrarDaoPostgres(DatabaseConnector con) {
        super(con);
    }

    @Override
    public Long insertRegistrar(final Registrar registrar) throws DatabaseException, AlreadyPresentException {
        DaoOperation operation = new DaoOperation() {
            @Override
            public Object run(Connection connection) throws DatabaseException, SQLException {
                //get new id
                StatementWrapper newId = new SelectNewIdFromSequence(ArchiverDAO.SEQ_NAME);
                PreparedStatement newIdSt = OperationUtils.preparedStatementFromWrapper(connection, newId);
                ResultSet idResultSet = newIdSt.executeQuery();
                Long id = OperationUtils.resultSet2Long(idResultSet);
                //insert with id into Archiver table
                registrar.setId(id);
                StatementWrapper insertArch = new InsertArchiver(registrar);
                PreparedStatement insertArchSt = OperationUtils.preparedStatementFromWrapper(connection, insertArch);
                insertArchSt.executeUpdate();
                //insert with id into Registrar table
                StatementWrapper insertReg = new InsertRegistrar(registrar);
                PreparedStatement insertRegSt = OperationUtils.preparedStatementFromWrapper(connection, insertReg);
                insertRegSt.executeUpdate();
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
            if ("23505".equals(ex.getSQLState())) {
                IdPart param = new IdPart(ATTR_CODE, registrar.getCode().toString());
                throw new AlreadyPresentException(param);
            } else {
                throw new DatabaseException(ex);
            }
        }
    }

    @Override
    public Registrar getRegistrarByCode(RegistrarCode code) throws DatabaseException, RecordNotFoundException {
        Registrar registrar = registrarByCode(code);
        addDataFromArchiver(registrar);
        return registrar;
    }

    private Registrar registrarByCode(RegistrarCode code) throws DatabaseException, RecordNotFoundException {
        StatementWrapper wrapper = new SelectAllAttrsByStringAttr(TABLE_NAME, ATTR_CODE, code.toString());
        DaoOperation operation = new SingleResultOperation(wrapper, new RegistrarRT());
        try {
            return (Registrar) runInTransaction(operation);
        } catch (PersistenceException e) {
            if (e instanceof RecordNotFoundException) {
                logger.log(Level.WARNING, "No such registrar with code {0}", code);
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

    private void addDataFromArchiver(Registrar registrar) throws DatabaseException, RecordNotFoundException {
        Archiver archiver = (Archiver) getRecordById(ArchiverDAO.TABLE_NAME, ArchiverDAO.ATTR_ID, registrar.getId(), new ArchiverRT());
        registrar.loadDataFromArchiver(archiver);
    }

    @Override
    public Registrar getRegistrarById(long id) throws DatabaseException, RecordNotFoundException {
        //pozor, dve operace nejsou v transakci. Pro tenhle pripady by to nemelo vadit
        Registrar registrar = (Registrar) getRecordById(TABLE_NAME, ATTR_ID, id, new RegistrarRT());
        addDataFromArchiver(registrar);
        return registrar;
    }

    @Override
    public List<Registrar> getAllRegistrars() throws DatabaseException {
        List<Registrar> result = (List<Registrar>) getAllRecords(TABLE_NAME, new RegistrarRT());
        for (Registrar registrar : result) {
            try {
                addDataFromArchiver(registrar);
            } catch (RecordNotFoundException ex) {
                //shouldn't happen
                logger.log(Level.SEVERE, ex.getMessage());
            }
        }
        return result;
    }

    public List<Long> getAllRegistrarsId() throws DatabaseException {
        return (List<Long>) getAllRecords(TABLE_NAME, new SingleLongRT());
    }

    public List<Registrar> getRegistrarsManagedByUser(long userId) throws DatabaseException, RecordNotFoundException {
        checkRecordExists(UserDAO.TABLE_NAME, UserDAO.ATTR_ID, userId);
        try {
            StatementWrapper st = new SelectSingleAttrByLong(
                    UserDAO.TABLE_USER_REGISTRAR_NAME,
                    UserDAO.USER_REGISTRAR_ATTR_USER_ID, userId,
                    UserDAO.USER_REGISTRAR_ATTR_REGISTRAR_ID);
            DaoOperation operation = new MultipleResultsOperation(st, new SingleLongRT());
            List<Long> identifiers = (List<Long>) runInTransaction(operation);
            List<Registrar> result = new ArrayList<Registrar>(identifiers.size());
            for (Long id : identifiers) {
                result.add(getRegistrarById(id));
            }
            return result;
        } catch (PersistenceException ex) {
            //cannot happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return null;
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    @Override
    public void updateRegistrar(final Registrar registrar) throws DatabaseException, RecordNotFoundException {
        updateRecordWithLongPK(registrar, ArchiverDAO.TABLE_NAME, ArchiverDAO.ATTR_ID, new UpdateArchiver(registrar));
        updateRecordWithLongPK(registrar, TABLE_NAME, ATTR_ID, new UpdateRegistrar(registrar));
    }

    @Override
    public void activateRegistrar(long id) throws DatabaseException, RecordNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void deleteRegistrar(long id) throws DatabaseException, RecordNotFoundException, RecordReferencedException {
        checkRecordExists(TABLE_NAME, ATTR_ID, id);
        //delete archiver - registrar is deleted in cascade
        deleteRecordsById(ArchiverDAO.TABLE_NAME, ArchiverDAO.ATTR_ID, id, true);
    }

    @Override
    public void deleteAllRegistrars() throws DatabaseException, RecordReferencedException {
        DaoOperation operation = new DaoOperation() {
            @Override
            public Object run(Connection connection) throws DatabaseException, SQLException {
                //get all identifiers from registrars
                StatementWrapper getIdsWrapper = new SelectIdentifiersAll(TABLE_NAME, ATTR_ID);
                PreparedStatement getIdsStatement = connection.prepareStatement(getIdsWrapper.preparedStatement());
                ResultSet idResultSet = getIdsStatement.executeQuery();
                List<Long> idList = OperationUtils.resultSet2ListOfLong(idResultSet);
                for (Long id : idList) {
                    //delete archivers - registrars are deleted in cascade
                    StatementWrapper delArchWrapper = new DeleteRecordsByLongAttr(ArchiverDAO.TABLE_NAME, ArchiverDAO.ATTR_ID, id);
                    PreparedStatement delArchSt = OperationUtils.preparedStatementFromWrapper(connection, delArchWrapper);
                    delArchSt.executeUpdate();
                }
                return null;
            }
        };
        try {
            runInTransaction(operation);
        } catch (PersistenceException ex) {
            //should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }
}
