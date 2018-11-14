/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence;

import cz.nkp.urnnbn.core.RegistrarScopeIdType;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import org.joda.time.DateTime;

import java.util.List;

/**
 * @author Martin Řehánek
 */
public interface RegistrarScopeIdentifierDAO {

    public String TABLE_NAME = "registrarScopeId";
    public String ATTR_REG_ID = "registrarId";
    public String ATTR_DIG_DOC_ID = "digitalDocumentId";
    public String ATTR_CREATED = "created";
    public String ATTR_UPDATED = "modified";
    public String ATTR_TYPE = "type";
    public String ATTR_VALUE = "idValue";

    /**
     * @param rsId
     * @throws DatabaseException
     * @throws RecordNotFoundException if registrar or digital document doesn't exist
     * @throws AlreadyPresentException if combination of registrar-digDoc-idType or registrar-idType-idValue already exists
     */
    public void insertRegistrarScopeId(RegistrarScopeIdentifier rsId) throws DatabaseException, RecordNotFoundException, AlreadyPresentException;

    /**
     * @param digDocId
     * @return
     * @throws DatabaseException
     * @throws RecordNotFoundException if digital document doesn't exist
     */
    public List<RegistrarScopeIdentifier> getRegistrarScopeIds(long digDocId) throws DatabaseException, RecordNotFoundException;

    /**
     * @param digDocId id of digital document
     * @param type     type of registrar-scope identifier
     * @return
     * @throws DatabaseException
     * @throws RecordNotFoundException if digital document or registrar-scope identifier doesn't exist
     */
    public RegistrarScopeIdentifier getRegistrarScopeId(long digDocId, RegistrarScopeIdType type) throws DatabaseException, RecordNotFoundException;

    /**
     * @param from
     * @param until
     * @return
     * @throws DatabaseException
     */
    public List<RegistrarScopeIdentifier> getRegistrarScopeIdsByTimestamps(DateTime from, DateTime until) throws DatabaseException;

    /**
     * @param id
     * @throws DatabaseException
     * @throws RecordNotFoundException if registrar, digital document or registrarScope id doesn't exist
     * @throws AlreadyPresentException if such registrarScope id already exists (for another digital document)
     */
    public void updateRegistrarScopeIdValue(RegistrarScopeIdentifier id) throws DatabaseException, RecordNotFoundException, AlreadyPresentException;

    /**
     * @param digDocId
     * @param idType
     * @throws DatabaseException
     * @throws RecordNotFoundException if digital document or registrar-scope identifier doesn't exist
     */
    public void deleteRegistrarScopeId(long digDocId, RegistrarScopeIdType idType) throws DatabaseException, RecordNotFoundException;

    /**
     * @param digDocDbId
     * @throws DatabaseException
     * @throws RecordNotFoundException if digital document with id digDocDbId doesn't exist
     */
    public void deleteRegistrarScopeIds(long digDocDbId) throws DatabaseException, RecordNotFoundException;
}
