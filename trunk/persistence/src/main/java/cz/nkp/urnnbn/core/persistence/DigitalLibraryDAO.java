/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence;

import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.dto.DigitalLibrary;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordReferencedException;
import java.util.List;

/**
 *
 * @author Martin Řehánek
 */
public interface DigitalLibraryDAO {

    public String TABLE_NAME = "DigitalLibrary";
    public String SEQ_NAME = "seq_DigitalLibrary";
    public String ATTR_ID = "id";
    public String ATTR_REGISTRAR_ID = "registrarId";
    public String ATTR_CREATED = "created";
    public String ATTR_UPDATED = "modified";
    public String ATTR_NAME = "name";
    public String ATTR_DESCRIPTION = "description";
    public String ATTR_URL = "url";

    public Long insertLibrary(DigitalLibrary library) throws DatabaseException, RecordNotFoundException;

    public DigitalLibrary getLibraryById(long id) throws DatabaseException, RecordNotFoundException;

    public List<DigitalLibrary> getLibraries(long registrarId) throws DatabaseException, RecordNotFoundException;

    public List<Long> getAllLibrariesId() throws DatabaseException;

    /**
     * Digital library data will be updated. I.e. not id nor registrarId
     * @param library
     * @throws DatabaseException
     * @throws RecordNotFoundException 
     */
    public void updateLibrary(DigitalLibrary library) throws DatabaseException, RecordNotFoundException;

    public void deleteLibrary(long id) throws DatabaseException, RecordNotFoundException, RecordReferencedException;

    public void deleteAllLibraries() throws DatabaseException, RecordReferencedException;
}
