/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence;

import cz.nkp.urnnbn.core.dto.SourceDocument;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;

/**
 *
 * @author Martin Řehánek
 */
public interface SourceDocumentDAO {

    public String TABLE_NAME = "SourceDocument";
    public String ATTR_INT_ENT_ID = "intelectualEntityId";
    public String ATTR_TITLE = "title";
    public String ATTR_VOLUME_TITLE = "volumeTitle";
    public String ATTR_ISSUE_TITLE = "issueTitle";
    public String ATTR_CCNB = "ccnb";
    public String ATTR_ISBN = "isbn";
    public String ATTR_ISSN = "issn";
    public String ATTR_OTHER_ID = "otherId";
    public String ATTR_PUB_PLACE = "publicationPlace";
    public String ATTR_PUBLISHER = "publisher";
    public String ATTR_PUB_YEAR = "publicationYear";

    /**
     * 
     * @param srcDoce
     * @throws DatabaseException
     * @throws AlreadyPresentException
     *             if SourceDocument with same intEntityId exists
     * @throws RecordNotFoundException
     *             if IntelectualEntity referenced in srcDoc doesn't exist
     */
    public void insertSrcDoc(SourceDocument srcDoc) throws DatabaseException, AlreadyPresentException, RecordNotFoundException;

    public SourceDocument getSrcDocById(long id) throws DatabaseException, RecordNotFoundException;

    public boolean srcDocExists(long entityId) throws DatabaseException;

    public void updateSrcDoc(SourceDocument srcDoc) throws DatabaseException, RecordNotFoundException;

    public void removeSrcDoc(long id) throws DatabaseException;
}
