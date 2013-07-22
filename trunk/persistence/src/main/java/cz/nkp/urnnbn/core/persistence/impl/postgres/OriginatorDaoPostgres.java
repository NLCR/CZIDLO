/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.OriginatorDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordReferencedException;
import cz.nkp.urnnbn.core.persistence.impl.AbstractDAO;
import cz.nkp.urnnbn.core.persistence.impl.statements.InsertOriginator;
import cz.nkp.urnnbn.core.persistence.impl.statements.UpdateOriginator;
import cz.nkp.urnnbn.core.persistence.impl.transformations.OriginatorRT;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Řehánek
 */
public class OriginatorDaoPostgres extends AbstractDAO implements OriginatorDAO {

    private static Logger logger = Logger.getLogger(OriginatorDaoPostgres.class.getName());

    public OriginatorDaoPostgres(DatabaseConnector con) {
        super(con);
    }

    @Override
    public void insertOriginator(Originator originator) throws DatabaseException, AlreadyPresentException, RecordNotFoundException {
        insertRecordWithLongPK(originator, TABLE_NAME, ATTR_INT_ENT_ID, new InsertOriginator(originator));
    }

    @Override
    public Originator getOriginatorById(long id) throws DatabaseException, RecordNotFoundException {
        return (Originator) getRecordById(TABLE_NAME, ATTR_INT_ENT_ID, id, new OriginatorRT(), false);
    }

    @Override
    public void updateOriginator(Originator originator) throws DatabaseException, RecordNotFoundException {
        updateRecordWithLongPK(originator, TABLE_NAME, ATTR_INT_ENT_ID, new UpdateOriginator(originator));
    }

    public void removeOriginator(long id) throws DatabaseException {
        try {
            deleteRecordsById(TABLE_NAME, ATTR_INT_ENT_ID, id, false);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (RecordReferencedException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public boolean originatorExists(long entityId) throws DatabaseException {
        return recordExists(TABLE_NAME, ATTR_INT_ENT_ID, entityId);
    }
}
