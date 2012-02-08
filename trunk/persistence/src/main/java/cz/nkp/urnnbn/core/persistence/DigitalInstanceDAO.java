/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence;

import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import java.util.List;

/**
 *
 * @author Martin Řehánek
 */
public interface DigitalInstanceDAO {

    public String TABLE_NAME = "DigitalInstance";
    public String SEQ_NAME = "seq_DigitalInstance";
    public String ATTR_ID = "id";
    public String ATTR_DIG_REP_ID = "digitalDocumentId";
    public String ATTR_LIB_ID = "digitalLibraryId";
    public String ATTR_URL = "url";
    public String ATTR_FORMAT = "format";
    public String ATTR_ACCESS = "accessibility";
    public String ATTR_PUBLISHED = "published";

    /**
     * 
     * @param instance
     * @return
     * @throws DatabaseException
     * @throws RecordNotFoundException if digital representation or digital library was not found
     */
    public Long insertDigInstance(DigitalInstance instance) throws DatabaseException, RecordNotFoundException;

    public DigitalInstance getDigInstanceById(long id) throws DatabaseException, RecordNotFoundException;

    public List<DigitalInstance> getDigitalInstancesOfDigDoc(long digDocId) throws DatabaseException, RecordNotFoundException;

    public void deleteDigInstance(long digInstId) throws DatabaseException, RecordNotFoundException;

    public long getTotalCount() throws DatabaseException;
}
