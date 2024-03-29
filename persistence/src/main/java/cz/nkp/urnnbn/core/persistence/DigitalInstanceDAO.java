/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence;

import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import org.joda.time.DateTime;

import java.util.List;

/**
 * @author Martin Řehánek
 */
public interface DigitalInstanceDAO {

    public String TABLE_NAME = "DigitalInstance";
    public String SEQ_NAME = "seq_DigitalInstance";
    public String ATTR_ID = "id";
    public String ATTR_DIG_DOC_ID = "digitalDocumentId";
    public String ATTR_LIB_ID = "digitalLibraryId";
    public String ATTR_CREATED = "created";
    public String ATTR_DEACTIVATED = "deactivated";
    public String ATTR_ACTIVE = "active";
    public String ATTR_URL = "url";
    public String ATTR_FORMAT = "format";
    public String ATTR_ACCESS = "accessibility";
    public String ATTR_ACCESS_RESTRICTION = "accessRestriction";

    /**
     * @param instance
     * @return new id
     * @throws DatabaseException
     * @throws RecordNotFoundException if digital document or digital library was not found
     */
    public Long insertDigInstance(DigitalInstance instance) throws DatabaseException, RecordNotFoundException;

    public DigitalInstance getDigInstanceById(long id) throws DatabaseException, RecordNotFoundException;

    public List<DigitalInstance> getDigitalInstancesOfDigDoc(long digDocId) throws DatabaseException, RecordNotFoundException;

    public List<DigitalInstance> getDigitalInstancesByTimestamps(DateTime from, DateTime until) throws DatabaseException;

    public List<DigitalInstance> getDigitalInstancesByUrl(String url) throws DatabaseException;

    public long getTotalCount() throws DatabaseException;

    public void updateDigInstance(DigitalInstance instance) throws DatabaseException, RecordNotFoundException;

    public void deactivateDigInstance(long digInstId) throws DatabaseException, RecordNotFoundException;

}
