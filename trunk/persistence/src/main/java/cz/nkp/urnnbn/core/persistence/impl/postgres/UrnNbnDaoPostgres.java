/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.IdPart;
import cz.nkp.urnnbn.core.persistence.exceptions.PersistenceException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.impl.operations.DaoOperation;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.DigitalDocumentDAO;
import cz.nkp.urnnbn.core.persistence.UrnNbnDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.MultipleRecordsException;
import cz.nkp.urnnbn.core.persistence.impl.AbstractDAO;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import cz.nkp.urnnbn.core.persistence.impl.operations.NoResultOperation;
import cz.nkp.urnnbn.core.persistence.impl.operations.SingleResultOperation;
import cz.nkp.urnnbn.core.persistence.impl.statements.DeleteByStringString;
import cz.nkp.urnnbn.core.persistence.impl.statements.InsertUrnNbn;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectAllAttrsByStringAndStringAttrs;
import cz.nkp.urnnbn.core.persistence.impl.transformations.ResultsetTransformer;
import cz.nkp.urnnbn.core.persistence.impl.transformations.UrnNbnRT;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Řehánek
 */
public class UrnNbnDaoPostgres extends AbstractDAO implements UrnNbnDAO {

    private static final Logger logger = Logger.getLogger(UrnNbnDaoPostgres.class.getName());

    public UrnNbnDaoPostgres(DatabaseConnector con) {
        super(con);
    }

    @Override
    public void insertUrnNbn(UrnNbn urn) throws DatabaseException, RecordNotFoundException, AlreadyPresentException {
        checkRecordExists(DigitalDocumentDAO.TABLE_NAME, DigitalDocumentDAO.ATTR_ID, urn.getDigDocId());
        StatementWrapper st = new InsertUrnNbn(urn);
        DaoOperation operation = new NoResultOperation(st);
        try {
            runInTransaction(operation);
        } catch (PersistenceException ex) {
            //should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
        } catch (SQLException ex) {
            if ("23505".equals(ex.getSQLState())) {
                IdPart digRepId = new IdPart(ATTR_DIG_REP_ID, Long.toString(urn.getDigDocId()));
                IdPart registrarCode = new IdPart(ATTR_REGISTRAR_CODE, urn.getRegistrarCode().toString());
                IdPart documentCode = new IdPart(ATTR_DOCUMENT_CODE, urn.getDocumentCode());
                throw new AlreadyPresentException(new IdPart[]{digRepId, registrarCode, documentCode});
            } else {
                throw new DatabaseException(ex);
            }
        }
    }

    @Override
    public UrnNbn getUrnNbnByDigRegId(long digRepId) throws DatabaseException, RecordNotFoundException {
        return (UrnNbn) getRecordById(TABLE_NAME, ATTR_DIG_REP_ID, digRepId, new UrnNbnRT());
    }

    @Override
    public UrnNbn getUrnNbnByRegistrarCodeAndDocumentCode(RegistrarCode registrarCode, String documentCode) throws DatabaseException, RecordNotFoundException {
        StatementWrapper wrapper = new SelectAllAttrsByStringAndStringAttrs(TABLE_NAME,
                ATTR_REGISTRAR_CODE, registrarCode.toString(),
                ATTR_DOCUMENT_CODE, documentCode);
        DaoOperation operation = new SingleResultOperation(wrapper, new UrnNbnRT());
        try {
            return (UrnNbn) runInTransaction(operation);
        } catch (RecordNotFoundException e) {
            logger.log(Level.SEVERE, "No such urn:nbn with registrar code {0} and document code {1}", new Object[]{registrarCode, documentCode});
            throw (RecordNotFoundException) e;
        } catch (PersistenceException e) {
            //should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", e);
            return null;
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    @Override
    public void deleteUrnNbn(UrnNbn urn) throws DatabaseException {
        StatementWrapper wrapper = new DeleteByStringString(TABLE_NAME,
                ATTR_REGISTRAR_CODE, urn.getRegistrarCode().toString(),
                ATTR_DOCUMENT_CODE, urn.getDocumentCode());
        DaoOperation operation = new NoResultOperation(wrapper);
        try {
            runInTransaction(operation);
        } catch (PersistenceException e) {
            //should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", e);
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    @Override
    public void deleteAllUrnNbns() throws DatabaseException {
        deleteAllRecords(TABLE_NAME);
    }

    private Object getSingleResult(ResultSet resultSet, ResultsetTransformer transformer) throws SQLException, RecordNotFoundException, MultipleRecordsException {
        Object result = null;
        int found = 0;
        while (resultSet.next()) {
            result = transformer.transform(resultSet);
            found++;
        }
        if (found == 0) {
            throw new RecordNotFoundException();
        } else if (found != 1) {
            throw new MultipleRecordsException();
        } else {
            return result;
        }
    }
}
