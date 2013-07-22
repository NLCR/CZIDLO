/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.PublicationDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordReferencedException;
import cz.nkp.urnnbn.core.persistence.impl.AbstractDAO;
import cz.nkp.urnnbn.core.persistence.impl.statements.InsertPublication;
import cz.nkp.urnnbn.core.persistence.impl.statements.UpdatePublication;
import cz.nkp.urnnbn.core.persistence.impl.transformations.PublicationRT;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Řehánek
 */
public class PublicationDaoPostgres extends AbstractDAO implements PublicationDAO {

    private static final Logger logger = Logger.getLogger(PublicationDaoPostgres.class.getName());

    public PublicationDaoPostgres(DatabaseConnector con) {
        super(con);
    }

    @Override
    public void insertPublication(Publication publication) throws DatabaseException, AlreadyPresentException, RecordNotFoundException {
        insertRecordWithLongPK(publication, TABLE_NAME, ATTR_INT_ENT_ID, new InsertPublication(publication));
    }

    @Override
    public Publication getPublicationById(long idValue) throws DatabaseException, RecordNotFoundException {
        return (Publication) getRecordById(TABLE_NAME, ATTR_INT_ENT_ID, idValue, new PublicationRT(), false);
    }

    @Override
    public void updatePublication(final Publication publication) throws DatabaseException, RecordNotFoundException {
        updateRecordWithLongPK(publication, TABLE_NAME, ATTR_INT_ENT_ID, new UpdatePublication(publication));
    }

    public boolean publicationExists(long entityId) throws DatabaseException {
        return recordExists(TABLE_NAME, ATTR_INT_ENT_ID, entityId);
    }

    public void removePublication(long id) throws DatabaseException {
        try {
            deleteRecordsById(TABLE_NAME, ATTR_INT_ENT_ID, id, false);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (RecordReferencedException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
}
