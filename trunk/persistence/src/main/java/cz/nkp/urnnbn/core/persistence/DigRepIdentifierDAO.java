/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence;

import cz.nkp.urnnbn.core.DigRepIdType;
import cz.nkp.urnnbn.core.dto.DigRepIdentifier;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import java.util.List;

/**
 *
 * @author Martin Řehánek
 */
public interface DigRepIdentifierDAO {

    public String TABLE_NAME = "drIdentifier";
    public String ATTR_REG_ID = "registrarId";
    public String ATTR_TYPE = "type";
    public String ATTR_DIG_REP_ID = "digitalRepresentationId";
    public String ATTR_VALUE = "idValue";

    public void insertDigRepId(DigRepIdentifier id) throws DatabaseException, RecordNotFoundException, AlreadyPresentException;

    public List<DigRepIdentifier> getIdList(long digRepDbId) throws DatabaseException, RecordNotFoundException;

    public void updateDigRepIdValue(DigRepIdentifier id) throws DatabaseException, RecordNotFoundException, AlreadyPresentException;

    public void deleteDigRepIdentifier(long digRepDbId, DigRepIdType type) throws DatabaseException, RecordNotFoundException;

    public void deleteAllIdentifiersOfDigRep(long digRepDbId) throws DatabaseException, RecordNotFoundException;
}
