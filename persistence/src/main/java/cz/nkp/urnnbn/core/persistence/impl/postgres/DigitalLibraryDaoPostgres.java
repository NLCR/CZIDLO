/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.persistence.impl.postgres.statements.SelectNewIdFromSequence;
import cz.nkp.urnnbn.core.persistence.exceptions.PersistenceException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.impl.operations.DaoOperation;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.DigitalLibraryDAO;
import cz.nkp.urnnbn.core.persistence.RegistrarDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordReferencedException;
import cz.nkp.urnnbn.core.persistence.impl.AbstractDAO;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import cz.nkp.urnnbn.core.persistence.impl.operations.OperationUtils;
import cz.nkp.urnnbn.core.persistence.impl.operations.MultipleResultsOperation;
import cz.nkp.urnnbn.core.persistence.impl.operations.NoResultOperation;
import cz.nkp.urnnbn.core.persistence.impl.statements.InsertDigitalLibrary;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectAllAttrsByLongAttr;
import cz.nkp.urnnbn.core.persistence.impl.statements.UpdateLibrary;
import cz.nkp.urnnbn.core.persistence.impl.transformations.DigitalLibraryRT;
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
public class DigitalLibraryDaoPostgres extends AbstractDAO implements DigitalLibraryDAO {

    private static final Logger logger = Logger.getLogger(DigitalLibraryDaoPostgres.class.getName());

    public DigitalLibraryDaoPostgres(DatabaseConnector con) {
        super(con);
    }

    @Override
    public Long insertLibrary(final DigitalLibrary library) throws DatabaseException, RecordNotFoundException {
        // TODO: operace kontroly pritomonosti id neni v transakci s vkladanim!
        // v tom to pripade nevadi
        checkRecordExists(RegistrarDAO.TABLE_NAME, RegistrarDAO.ATTR_ID, library.getRegistrarId());
        DaoOperation operation = new DaoOperation() {

            @Override
            public Object run(Connection connection) throws SQLException, PersistenceException {
                // get new id
                StatementWrapper newId = new SelectNewIdFromSequence(SEQ_NAME);
                PreparedStatement newIdSt = OperationUtils.preparedStatementFromWrapper(connection, newId);
                ResultSet idResultSet = newIdSt.executeQuery();
                Long id = OperationUtils.resultSet2Long(idResultSet);
                // set id
                library.setId(id);
                // insert
                StatementWrapper insert = new InsertDigitalLibrary(library);
                PreparedStatement insertSt = OperationUtils.preparedStatementFromWrapper(connection, insert);
                insertSt.executeUpdate();
                return id;
            }
        };
        try {
            Long id = (Long) runInTransaction(operation);
            return id;
        } catch (PersistenceException ex) {
            // should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return null;
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    @Override
    public DigitalLibrary getLibraryById(long id) throws DatabaseException, RecordNotFoundException {
        return (DigitalLibrary) getRecordById(TABLE_NAME, ATTR_ID, id, new DigitalLibraryRT());
    }

    @Override
    public List<Long> getAllLibrariesId() throws DatabaseException {
        return getIdListOfAllRecords(TABLE_NAME, ATTR_ID);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DigitalLibrary> getLibraries(long registrarId) throws DatabaseException, RecordNotFoundException {
        checkRecordExists(RegistrarDAO.TABLE_NAME, RegistrarDAO.ATTR_ID, registrarId);
        try {
            StatementWrapper st = new SelectAllAttrsByLongAttr(TABLE_NAME, ATTR_REGISTRAR_ID, registrarId);
            DaoOperation operation = new MultipleResultsOperation(st, new DigitalLibraryRT());
            return (List<DigitalLibrary>) runInTransaction(operation);
        } catch (PersistenceException ex) {
            // cannot happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return null;
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    @Override
    public void updateLibrary(DigitalLibrary library) throws DatabaseException, RecordNotFoundException {
        checkRecordExists(TABLE_NAME, ATTR_ID, library.getId());
        try {
            DaoOperation operation = new NoResultOperation(new UpdateLibrary(library));
            runInTransaction(operation);
        } catch (PersistenceException ex) {
            // should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Couldn''t update digital library {0}", library.getId());
            throw new DatabaseException(ex);
        }
    }

    @Override
    public void deleteLibrary(long id) throws DatabaseException, RecordNotFoundException, RecordReferencedException {
        deleteRecordsById(TABLE_NAME, ATTR_ID, id, true);
    }

    @Override
    public void deleteAllLibraries() throws DatabaseException, RecordReferencedException {
        deleteAllRecords(TABLE_NAME);
    }
}
