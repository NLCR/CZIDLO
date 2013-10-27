/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.services.impl;

import cz.nkp.urnnbn.core.dto.Content;
import cz.nkp.urnnbn.core.persistence.DAOFactory;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.services.ContentService;
import cz.nkp.urnnbn.services.exceptions.ContentNotFoundException;
import java.util.logging.Logger;

/**
 *
 * @author xrosecky
 */
public class ContentServiceImpl implements ContentService {

    private static final Logger logger = Logger.getLogger(ContentServiceImpl.class.getName());
    final DAOFactory factory;

    public ContentServiceImpl(DatabaseConnector conn) {
        this(new DAOFactory(conn));
    }

    public ContentServiceImpl(DAOFactory factory) {
        this.factory = factory;
    }

    @Override
    public void init() {
        //FIXME: TODO
    }

    @Override
    public Content getContentByNameAndLanguage(String name, String language) throws ContentNotFoundException {
        try {
            return factory.contentDao().getContentByNameAndLanguage(name, language);
        } catch (DatabaseException ex) {
            throw new RuntimeException("database error", ex);
        } catch (RecordNotFoundException ex) {
            throw new ContentNotFoundException(String.format("Content with language='%s' and name='%s' not found", language, name), ex);
        }
    }

    @Override
    public void updateContent(Content content) {
        try {
            factory.contentDao().updateContent(content);
        } catch (DatabaseException ex) {
            throw new RuntimeException("database error", ex);
        } catch (RecordNotFoundException ex) {
            throw new RuntimeException("Record not found", ex);
        }
    }
}
