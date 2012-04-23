/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.SourceDocumentDAO;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordReferencedException;
import cz.nkp.urnnbn.core.persistence.impl.AbstractDAO;
import cz.nkp.urnnbn.core.persistence.impl.statements.InsertSourceDocument;
import cz.nkp.urnnbn.core.persistence.impl.statements.UpdateSourceDocument;
import cz.nkp.urnnbn.core.persistence.impl.transformations.SourceDocumentRT;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Řehánek
 */
public class SourceDocumentDaoPostgres extends AbstractDAO implements SourceDocumentDAO {

    private static Logger logger = Logger.getLogger(SourceDocumentDaoPostgres.class.getName());

    public SourceDocumentDaoPostgres(DatabaseConnector con) {
        super(con);
    }

    @Override
    public void insertSrcDoc(SourceDocument srcDoce) throws DatabaseException, AlreadyPresentException, RecordNotFoundException {
        insertRecordWithLongPK(srcDoce, TABLE_NAME, ATTR_INT_ENT_ID, new InsertSourceDocument(srcDoce));
    }

    @Override
    public SourceDocument getSrcDocById(long id) throws DatabaseException, RecordNotFoundException {
        return (SourceDocument) getRecordById(TABLE_NAME, ATTR_INT_ENT_ID, id, new SourceDocumentRT());
    }

    @Override
    public void updateSrcDoc(SourceDocument srcDoc) throws DatabaseException, RecordNotFoundException {
        updateRecordWithLongPK(srcDoc, TABLE_NAME, ATTR_INT_ENT_ID, new UpdateSourceDocument(srcDoc));
    }

    public boolean srcDocExists(long entityId) throws DatabaseException {
        return recordExists(TABLE_NAME, ATTR_INT_ENT_ID, entityId);
    }

    public void removeSrcDoc(long id) throws DatabaseException {
        try {
            deleteRecordsById(TABLE_NAME, ATTR_INT_ENT_ID, id, false);
        } catch (RecordNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (RecordReferencedException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
}
