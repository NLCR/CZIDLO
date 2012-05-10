/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence;

import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.EntityType;
import cz.nkp.urnnbn.core.IntEntIdType;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordReferencedException;
import java.util.List;
import org.joda.time.DateTime;

/**
 *
 * @author Martin Řehánek
 */
public interface IntelectualEntityDAO {

    public String TABLE_NAME = "IntelectualEntity";
    public String SEQ_NAME = "seq_IntelectualEntity";
    public String ATTR_ID = "id";
    public String ATTR_ENTITY_TYPE = "entityType";
    public String ATTR_CREATED = "created";
    public String ATTR_UPDATED = "modified";
    public String ATTR_DOC_TYPE = "documentType";
    public String ATTR_DIGITAL_BORN = "digitalBorn";
    public String ATTR_OTHER_ORIGINATOR = "otherOriginator";
    public String ATTR_DEG_AW_INST = "degreeAwardingInstitution";

    public Long insertIntelectualEntity(IntelectualEntity entity) throws DatabaseException;

    public IntelectualEntity getEntityByDbId(long dbId) throws DatabaseException, RecordNotFoundException;

    /**
     * Finds intelectual entities that have ieIdentifier of specified type with specified value
     * @param type
     * @param idValue
     * @return list of intelectual entity db id
     * @throws DatabaseException 
     */
    public List<Long> getEntitiesDbIdListByIdentifier(IntEntIdType type, String idValue) throws DatabaseException;

    /**
     * Finds intelectual entities that have ieIdentifier of anye type with specified value
     * @param idValue
     * @return
     * @throws DatabaseException 
     */
    public List<Long> getEntitiesDbIdListByIdentifierValue(String idValue) throws DatabaseException;

    public List<Long> getEntitiesDbIdListByTimestamps(DateTime from, DateTime until) throws DatabaseException;

    public Long getEntitiesCount() throws DatabaseException;

    public Long getEntitiesCount(EntityType type) throws DatabaseException;

    public void updateEntity(IntelectualEntity entity) throws DatabaseException, RecordNotFoundException;

    public void deleteEntity(long id) throws DatabaseException, RecordNotFoundException, RecordReferencedException;

    public void deleteAllEntities() throws DatabaseException, RecordReferencedException;
}
