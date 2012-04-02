/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.dto.Catalog;
import cz.nkp.urnnbn.core.persistence.CatalogDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.PersistenceException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordReferencedException;
import cz.nkp.urnnbn.core.persistence.impl.operations.DaoOperation;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.RegistrarDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.impl.AbstractDAO;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import cz.nkp.urnnbn.core.persistence.impl.operations.MultipleResultsOperation;
import cz.nkp.urnnbn.core.persistence.impl.statements.InsertCatalog;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectAllAttrs;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectAllAttrsByLongAttr;
import cz.nkp.urnnbn.core.persistence.impl.statements.UpdateCatalog;
import cz.nkp.urnnbn.core.persistence.impl.transformations.CatalogRT;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Řehánek
 */
public class CatalogDaoPostgres extends AbstractDAO implements CatalogDAO {

    private static final Logger logger = Logger.getLogger(CatalogDaoPostgres.class.getName());

    public CatalogDaoPostgres(DatabaseConnector con) {
        super(con);
    }

    @Override
    public Long insertCatalog(Catalog catalog) throws DatabaseException, RecordNotFoundException, AlreadyPresentException {
        return insertRecordWithIdFromSequence(catalog, TABLE_NAME, SEQ_NAME, new InsertCatalog(catalog));
    }

    @Override
    public Catalog getCatalogById(long id) throws DatabaseException, RecordNotFoundException {
        return (Catalog) getRecordById(TABLE_NAME, ATTR_ID, id, new CatalogRT());
    }

    public List<Catalog> getCatalogs() throws DatabaseException {
        try {
            StatementWrapper st = new SelectAllAttrs(TABLE_NAME);
            DaoOperation operation = new MultipleResultsOperation(st, new CatalogRT());
            return (List<Catalog>) runInTransaction(operation);
        } catch (PersistenceException ex) {
            //should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return null;
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    @Override
    public List<Catalog> getCatalogs(long registrarId) throws DatabaseException, RecordNotFoundException {
        checkRecordExists(RegistrarDAO.TABLE_NAME, RegistrarDAO.ATTR_ID, registrarId);
        try {
            StatementWrapper st = new SelectAllAttrsByLongAttr(TABLE_NAME, ATTR_REG_ID, registrarId);
            DaoOperation operation = new MultipleResultsOperation(st, new CatalogRT());
            return (List<Catalog>) runInTransaction(operation);
        } catch (PersistenceException ex) {
            //should never happen
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return null;
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    @Override
    public void updateCatalog(Catalog catalog) throws DatabaseException, RecordNotFoundException {
        updateRecordWithLongPK(catalog, TABLE_NAME, ATTR_ID, new UpdateCatalog(catalog));
    }

    @Override
    public void deleteCatalog(long catalogId) throws DatabaseException, RecordNotFoundException {
        try {
            deleteRecordsById(TABLE_NAME, ATTR_ID, catalogId, true);
        } catch (RecordReferencedException ex) {
            //should never happen
            logger.log(Level.SEVERE, null, ex);
        }
    }
}
