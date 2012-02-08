/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.dto.UrnNbnGenerator;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.UrnNbnGeneratorDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.IdPart;
import cz.nkp.urnnbn.core.persistence.exceptions.PersistenceException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.impl.AbstractDAO;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import cz.nkp.urnnbn.core.persistence.impl.operations.DaoOperation;
import cz.nkp.urnnbn.core.persistence.impl.operations.NoResultOperation;
import cz.nkp.urnnbn.core.persistence.impl.operations.SingleResultOperation;
import cz.nkp.urnnbn.core.persistence.impl.statements.InsertUrnNbnSearch;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectAllAttrsByLongAttr;
import cz.nkp.urnnbn.core.persistence.impl.statements.UpdateUrnNbnGenerator;
import cz.nkp.urnnbn.core.persistence.impl.transformations.UrnNbnGeneratorRT;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Řehánek
 */
public class UrnNbnGeneratorDaoPostgres extends AbstractDAO implements UrnNbnGeneratorDAO {

    private static final Logger logger = Logger.getLogger(UrnNbnGeneratorDaoPostgres.class.getName());

    public UrnNbnGeneratorDaoPostgres(DatabaseConnector connector) {
        super(connector);
    }

    @Override
    public void insertUrnNbnSearch(UrnNbnGenerator search) throws DatabaseException, AlreadyPresentException, RecordNotFoundException {
        StatementWrapper st = new InsertUrnNbnSearch(search);
        DaoOperation operation = new NoResultOperation(st);
        try {
            runInTransaction(operation);
        } catch (PersistenceException ex) {
            //should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Couldn't insert {0} {1}", new Object[]{TABLE_NAME, search.toString()});
            if ("23505".equals(ex.getSQLState())) {
                IdPart registrarId = new IdPart(ATTR_REGISTRAR_ID, search.getRegistrarId().toString());
                IdPart documentCode = new IdPart(ATTR_LAST_DOCUMENT_CODE, search.getLastDocumentCode());
                throw new AlreadyPresentException(new IdPart[]{registrarId, documentCode});
            } else if ("23503".equals(ex.getSQLState())) {
                logger.log(Level.SEVERE, "Referenced record doesn't exist", ex);
                throw new RecordNotFoundException();
            } else {
                throw new DatabaseException(ex);
            }
        }
    }

    @Override
    public UrnNbnGenerator getSearchByRegistrarId(long registrarId) throws DatabaseException, RecordNotFoundException {
        StatementWrapper wrapper = new SelectAllAttrsByLongAttr(TABLE_NAME, ATTR_REGISTRAR_ID, registrarId);
        //new SelectAllAttrsByStringAttr(TABLE_NAME, ATTR_REGISTRAR_ID, sigla.toString());
        DaoOperation operation = new SingleResultOperation(wrapper, new UrnNbnGeneratorRT());
        try {
            return (UrnNbnGenerator) runInTransaction(operation);
        } catch (PersistenceException e) {
            if (e instanceof RecordNotFoundException) {
                logger.log(Level.SEVERE, "No such urn:nbn booking for registrar with id {0}", registrarId);
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

    @Override
    public void updateUrnNbnSearch(UrnNbnGenerator search) throws DatabaseException, RecordNotFoundException {
        checkRecordExists(TABLE_NAME, ATTR_REGISTRAR_ID, search.getRegistrarId());
        try {
            StatementWrapper updateSt = new UpdateUrnNbnGenerator(search);
            DaoOperation operation = new NoResultOperation(updateSt);
            runInTransaction(operation);
        } catch (PersistenceException ex) {
            //should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Couldn't update {0}", search);
            System.err.println("state:" + ex.getSQLState());
            if ("23503".equals(ex.getSQLState())) {
                logger.log(Level.SEVERE, "Referenced record doesn't exist", ex);
                throw new RecordNotFoundException();
            } else {
                throw new DatabaseException(ex);
            }
        }
    }
}