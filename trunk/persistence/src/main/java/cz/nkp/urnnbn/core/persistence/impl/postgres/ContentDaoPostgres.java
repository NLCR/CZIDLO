/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.dto.Content;
import cz.nkp.urnnbn.core.persistence.ContentDAO;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.PersistenceException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordReferencedException;
import cz.nkp.urnnbn.core.persistence.impl.AbstractDAO;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import cz.nkp.urnnbn.core.persistence.impl.operations.DaoOperation;
import cz.nkp.urnnbn.core.persistence.impl.operations.SingleResultOperation;
import cz.nkp.urnnbn.core.persistence.impl.statements.InsertContent;
import cz.nkp.urnnbn.core.persistence.impl.statements.SelectContentByLangAndName;
import cz.nkp.urnnbn.core.persistence.impl.statements.UpdateContent;
import cz.nkp.urnnbn.core.persistence.impl.transformations.ContentRT;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author xrosecky
 */
public class ContentDaoPostgres extends AbstractDAO implements ContentDAO {

    private static final Logger logger = Logger.getLogger(ContentDaoPostgres.class.getName());

    public ContentDaoPostgres(DatabaseConnector connector) {
        super(connector);
    }

    public Long insertContent(final Content content) throws DatabaseException {
        try {
            return insertRecordWithIdFromSequence(content, TABLE_NAME, SEQ_NAME, new InsertContent(content));
        } catch (RecordNotFoundException ex) {
            //should never happen since Archiver doesn't have foreign key attributes
            logger.log(Level.SEVERE, "Exception unexpected here", ex);
            return null;
        }
    }

    public void updateContent(Content content) throws DatabaseException, RecordNotFoundException {
        updateRecordWithLongPK(content, TABLE_NAME, ATTR_ID, new UpdateContent(content));
    }

    public void deleteContent(long contentId) throws DatabaseException, RecordNotFoundException {
        try {
            deleteRecordsById(TABLE_NAME, ATTR_ID, contentId, true);
        } catch (RecordReferencedException ex) {
            //should never happen
            logger.log(Level.SEVERE, null, ex);
        }
    }

    public Content getContentByNameAndLanguage(String name, String lang) throws DatabaseException, RecordNotFoundException {
        StatementWrapper wrapper = new SelectContentByLangAndName(lang, name);
        DaoOperation operation = new SingleResultOperation(wrapper, new ContentRT());
        try {
            return (Content) runInTransaction(operation);
        } catch (PersistenceException e) {
            if (e instanceof RecordNotFoundException) {
                logger.log(Level.WARNING, "No such content with lang {0} and name {1}", new Object[]{lang, name});
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

    public void deleteAllContent() throws DatabaseException {
        try {
            deleteAllRecords(TABLE_NAME);
        } catch (RecordReferencedException ex) {
            //should never happen
            logger.log(Level.SEVERE, null, ex);
        }
    }
}
