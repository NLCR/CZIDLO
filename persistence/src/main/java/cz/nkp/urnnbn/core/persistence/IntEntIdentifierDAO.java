/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence;

import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.IntEntIdType;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import java.util.List;

/**
 *
 * @author Martin Řehánek
 */
public interface IntEntIdentifierDAO {

    public String TABLE_NAME = "ieIdentifier";
    public String ATTR_IE_ID = "intelectualEntityid";
    public String ATTR_TYPE = "type";
    public String ATTR_VALUE = "idValue";

    /**
     * 
     * @param id
     * @throws DatabaseException
     * @throws RecordNotFoundException if no intelectual entity with given dbId
     */
    public void insertIntEntId(IntEntIdentifier id) throws DatabaseException, RecordNotFoundException, AlreadyPresentException;

    public List<IntEntIdentifier> getIdList(long intEntDbId) throws DatabaseException, RecordNotFoundException;

    public void updateIntEntIdValue(IntEntIdentifier id) throws DatabaseException, RecordNotFoundException;

    public void deleteIntEntIdentifier(long intEntDbId, IntEntIdType type) throws DatabaseException, RecordNotFoundException;

    public void deleteAllIntEntIdsOfEntity(long intEntDbId) throws DatabaseException, RecordNotFoundException;
}
