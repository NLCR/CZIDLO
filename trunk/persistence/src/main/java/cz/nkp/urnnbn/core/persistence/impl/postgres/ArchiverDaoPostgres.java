/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.dto.Archiver;
import cz.nkp.urnnbn.core.persistence.ArchiverDAO;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordReferencedException;
import cz.nkp.urnnbn.core.persistence.impl.AbstractDAO;
import cz.nkp.urnnbn.core.persistence.impl.statements.InsertArchiver;
import cz.nkp.urnnbn.core.persistence.impl.statements.UpdateArchiver;
import cz.nkp.urnnbn.core.persistence.impl.transformations.ArchiverRT;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Řehánek
 */
public class ArchiverDaoPostgres extends AbstractDAO implements ArchiverDAO {

    private static final Logger logger = Logger.getLogger(ArchiverDaoPostgres.class.getName());

    public ArchiverDaoPostgres(DatabaseConnector connector) {
        super(connector);
    }

    @Override
    public Long insertArchiver(final Archiver archiver) throws DatabaseException {
        try {
            return insertRecordWithIdFromSequence(archiver, TABLE_NAME, SEQ_NAME, new InsertArchiver(archiver));
        } catch (RecordNotFoundException ex) {
            //should never happen since Archiver doesn't have foreign key attributes
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return null;
        }
    }

    @Override
    public Archiver getArchiverById(long id) throws DatabaseException, RecordNotFoundException {
        return (Archiver) getRecordById(TABLE_NAME, ATTR_ID, id, new ArchiverRT());
    }

    @Override
    public List<Long> getAllArchiversId() throws DatabaseException {
        return getIdListOfAllRecords(TABLE_NAME, ATTR_ID);
    }

    @Override
    public void updateArchiver(Archiver archiver) throws DatabaseException, RecordNotFoundException {
        updateRecordWithLongPK(archiver, TABLE_NAME, ATTR_ID, new UpdateArchiver(archiver));
    }

    @Override
    public void deleteArchiver(final long id) throws DatabaseException, RecordNotFoundException, RecordReferencedException {
        deleteRecordsById(TABLE_NAME, ATTR_ID, id, true);
    }

    @Override
    public void deleteAllArchivers() throws DatabaseException, RecordReferencedException {
        deleteAllRecords(TABLE_NAME);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Archiver> getAllArchivers() throws DatabaseException {
        return (List<Archiver>) getAllRecords(TABLE_NAME, new ArchiverRT());
    }
}
