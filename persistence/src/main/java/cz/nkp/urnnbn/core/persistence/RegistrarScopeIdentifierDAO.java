/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence;

import java.util.List;

import org.joda.time.DateTime;

import cz.nkp.urnnbn.core.RegistrarScopeIdType;
import cz.nkp.urnnbn.core.dto.RegistrarScopeIdentifier;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;

/**
 *
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

    public void insertRegistrarScopeId(RegistrarScopeIdentifier id) throws DatabaseException, RecordNotFoundException, AlreadyPresentException;

    public List<RegistrarScopeIdentifier> getRegistrarScopeIds(long digDocDbId) throws DatabaseException, RecordNotFoundException;

    /**
     * 
     * @param digDocId
     *            id of digital document
     * @param type
     *            type of registrar-scope identifier
     * @return
     * @throws DatabaseException
     * @throws RecordNotFoundException
     *             if digital document or registrar-scope identifier doesn't exist
     */
    public RegistrarScopeIdentifier getRegistrarScopeId(Long digDocId, RegistrarScopeIdType type) throws DatabaseException, RecordNotFoundException;

    public List<RegistrarScopeIdentifier> getRegistrarScopeIdsByTimestamps(DateTime from, DateTime until) throws DatabaseException;

    public void updateRegistrarScopeIdValue(RegistrarScopeIdentifier id) throws DatabaseException, RecordNotFoundException, AlreadyPresentException;

    /**
     * 
     * @param digDocDbId
     * @param idType
     * @throws DatabaseException
     * @throws RecordNotFoundException
     *             if digital document with id digDocDbId doesn't exist or the identifier for digital document with id digDocDbId and type idType
     *             doesn't exist
     */
    public void deleteRegistrarScopeId(long digDocDbId, RegistrarScopeIdType idType) throws DatabaseException, RecordNotFoundException;

    /**
     * 
     * @param digDocDbId
     * @throws DatabaseException
     * @throws RecordNotFoundException
     *             if digital document with id digDocDbId doesn't exist
     */
    public void deleteRegistrarScopeIds(long digDocDbId) throws DatabaseException, RecordNotFoundException;
}
