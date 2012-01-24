/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence;

import cz.nkp.urnnbn.core.dto.Originator;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;

/**
 *
 * @author Martin Řehánek
 */
public interface OriginatorDAO {

    public String TABLE_NAME = "Originator";
    public String ATTR_INT_ENT_ID = "intelectualEntityId";
    public String ATTR_TYPE = "originType";
    public String ATTR_VALUE = "originValue";

    /**
     * 
     * @param originator
     * @throws DatabaseException 
     * @throws RecordNotFoundException if IntelectualEntity referenced in originator doesn't exist
     * @throws AlreadyPresentException if Originator with same intEntId is present
     */
    public void insertOriginator(Originator originator) throws DatabaseException, RecordNotFoundException, AlreadyPresentException;

    public Originator getOriginatorById(long id) throws DatabaseException, RecordNotFoundException;

    public void updateOriginator(Originator originator) throws DatabaseException, RecordNotFoundException;
    //mazani resi intelektualni entita kaskadove  
}
