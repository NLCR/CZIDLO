/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence;

import cz.nkp.urnnbn.core.dto.Content;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;

/**
 *
 * @author xrosecky
 */
public interface ContentDAO {

    public String TABLE_NAME = "Content";
    public String SEQ_NAME = "seq_Content";
    public String ATTR_ID = "id";
    public String ATTR_LANG = "language";
    public String ATTR_NAME = "name";
    public String ATTR_CONTENT = "content";

    public Long insertContent(Content content) throws DatabaseException;

    public Content getContentByNameAndLanguage(String name, String lang) throws DatabaseException, RecordNotFoundException;

    public void updateContent(Content content) throws DatabaseException, RecordNotFoundException;

    public void deleteContent(long contentId) throws DatabaseException, RecordNotFoundException;

    public void deleteAllContent() throws DatabaseException;
}
