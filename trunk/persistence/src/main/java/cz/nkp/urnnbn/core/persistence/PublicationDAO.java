/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence;

import cz.nkp.urnnbn.core.dto.Publication;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;

/**
 *
 * @author Martin Řehánek
 */
public interface PublicationDAO {

    public String TABLE_NAME = "Publication";
    public String ATTR_INT_ENT_ID = "intelectualEntityId";
    public String ATTR_YEAR = "pyear";
    public String ATTR_PLACE = "place";
    public String ATTR_PUBLISHER = "publisher";

    /**
     * 
     * @param publication
     * @throws DatabaseException
     * @throws AlreadyPresentException if publication with same intEntityId exists
     * @throws RecordNotFoundException if Intelectual entity referenced in publication doesn't exist
     */
    public void insertPublication(Publication publication) throws DatabaseException, AlreadyPresentException, RecordNotFoundException;

    public Publication getPublicationById(long id) throws DatabaseException, RecordNotFoundException;

    public boolean publicationExists(long entityId) throws DatabaseException;

    public void updatePublication(Publication publication) throws DatabaseException, RecordNotFoundException;

    public void removePublication(long id) throws DatabaseException;
}
