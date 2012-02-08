/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence;

import cz.nkp.urnnbn.core.DigDocIdType;
import cz.nkp.urnnbn.core.dto.DigDocIdentifier;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import java.util.List;

/**
 *
 * @author Martin Řehánek
 */
public interface DigDocIdentifierDAO {

    public String TABLE_NAME = "ddIdentifier";
    public String ATTR_REG_ID = "registrarId";
    public String ATTR_DIG_REP_ID = "digitalDocumentId";
    public String ATTR_TYPE = "type";
    public String ATTR_VALUE = "idValue";

    public void insertDigDocId(DigDocIdentifier id) throws DatabaseException, RecordNotFoundException, AlreadyPresentException;

    public List<DigDocIdentifier> getIdList(long digDocDbId) throws DatabaseException, RecordNotFoundException;

    public void updateDigRepIdValue(DigDocIdentifier id) throws DatabaseException, RecordNotFoundException, AlreadyPresentException;

    /**
     * 
     * @param digDocDbId
     * @param idType
     * @throws DatabaseException
     * @throws RecordNotFoundException if digital representation with id digRepDbId doesn't exist 
     * or the identifier for digital representation with id digRepDbId and type idType doesn't exist
     */
    public void deleteDigDocIdentifier(long digDocDbId, DigDocIdType idType) throws DatabaseException, RecordNotFoundException;

    /**
     * 
     * @param digDocDbId
     * @throws DatabaseException
     * @throws RecordNotFoundException if digital representation with id digRepDbId doesn't exist
     */
    public void deleteAllIdentifiersOfDigDoc(long digDocDbId) throws DatabaseException, RecordNotFoundException;
}