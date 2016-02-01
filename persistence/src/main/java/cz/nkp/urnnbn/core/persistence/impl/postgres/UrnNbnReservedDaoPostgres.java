/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.RegistrarDAO;
import cz.nkp.urnnbn.core.persistence.UrnNbnReservedDAO;
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
import cz.nkp.urnnbn.core.persistence.impl.operations.SingleResultOperation;
import cz.nkp.urnnbn.core.persistence.impl.statements.InsertUrnNbnReserved;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectAllAttrsByLongAttr;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectAllAttrsByStringString;
import cz.nkp.urnnbn.core.persistence.impl.transformations.UrnNbnReservedRT;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Řehánek
 */
public class UrnNbnReservedDaoPostgres extends AbstractDAO implements UrnNbnReservedDAO {

    private static final Logger logger = Logger.getLogger(UrnNbnReservedDaoPostgres.class.getName());

    public UrnNbnReservedDaoPostgres(DatabaseConnector connector) {
        super(connector);
    }

    @Override
    public void insertUrnNbn(UrnNbn urn, long registrarId) throws DatabaseException, AlreadyPresentException, RecordNotFoundException {
        checkRecordExists(RegistrarDAO.TABLE_NAME, RegistrarDAO.ATTR_ID, registrarId);
        // checkRecordExists(UrnNbnGeneratorDAO.TABLE_NAME, UrnNbnGeneratorDAO.ATTR_REGISTRAR_ID, urn.getRegistrarCode());
        StatementWrapper st = new InsertUrnNbnReserved(urn, registrarId);
        DaoOperation operation = new NoResultOperation(st);
        try {
            runInTransaction(operation);
        } catch (PersistenceException ex) {
            // should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
        } catch (SQLException ex) {
            if ("23505".equals(ex.getSQLState())) {
                IdPart registrarCode = new IdPart(ATTR_REGISTRAR_CODE, urn.getRegistrarCode().toString());
                IdPart documentCode = new IdPart(ATTR_DOCUMENT_CODE, urn.getDocumentCode());
                throw new AlreadyPresentException(new IdPart[] { registrarCode, documentCode });
            } else {
                throw new DatabaseException(ex);
            }
        }
    }

    @Override
    public UrnNbn getUrn(RegistrarCode code, String documentCode) throws DatabaseException, RecordNotFoundException {
        StatementWrapper wrapper = new SelectAllAttrsByStringString(TABLE_NAME, ATTR_REGISTRAR_CODE, code.toString(), ATTR_DOCUMENT_CODE,
                documentCode);
        DaoOperation operation = new SingleResultOperation(wrapper, new UrnNbnReservedRT());
        try {
            return (UrnNbn) runInTransaction(operation);
        } catch (RecordNotFoundException e) {
            logger.log(Level.INFO, "No reserved urn:nbn with registrar code {0} and document code {1}",
                    new Object[] { code.toString(), documentCode });
            throw (RecordNotFoundException) e;
        } catch (PersistenceException e) {
            // should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", e);
            return null;
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    @Override
    public List<UrnNbn> getUrnNbnList(long registrarId) throws DatabaseException, RecordNotFoundException {
        StatementWrapper wrapper = new SelectAllAttrsByLongAttr(TABLE_NAME, ATTR_REGISTRAR_ID, registrarId);
        DaoOperation operation = new MultipleResultsOperation(wrapper, new UrnNbnReservedRT());
        try {
            return (List<UrnNbn>) runInTransaction(operation);
        } catch (RecordNotFoundException e) {
            logger.log(Level.SEVERE, "No such registrar with id {0} ", registrarId);
            throw (RecordNotFoundException) e;
        } catch (PersistenceException e) {
            // should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", e);
            return null;
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    @Override
    public void deleteUrn(UrnNbn urn) throws DatabaseException, RecordNotFoundException {
        try {
            // TODO: recordNotFoundException to nikdy nehaze, poresit
            deleteRecordsByStringAndString(TABLE_NAME, ATTR_REGISTRAR_CODE, urn.getRegistrarCode().toString(), ATTR_DOCUMENT_CODE,
                    urn.getDocumentCode());
        } catch (RecordReferencedException ex) {
            // should never happen
            logger.log(Level.SEVERE, null, ex);
        }
    }
}
