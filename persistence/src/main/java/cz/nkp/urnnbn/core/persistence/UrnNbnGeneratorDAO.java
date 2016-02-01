/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence;

import cz.nkp.urnnbn.core.dto.UrnNbnGenerator;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;

/**
 *
 * @author Martin Řehánek
 */
public interface UrnNbnGeneratorDAO {

    public String TABLE_NAME = "UrnNbnGenerator";
    public String ATTR_REGISTRAR_ID = "registrarId";
    public String ATTR_LAST_DOCUMENT_CODE = "lastDocumentCode";

    public void insertGenerator(UrnNbnGenerator generator) throws DatabaseException, AlreadyPresentException, RecordNotFoundException;

    public UrnNbnGenerator getGeneratorByRegistrarId(long registrarId) throws DatabaseException, RecordNotFoundException;

    public void updateGenerator(UrnNbnGenerator search) throws DatabaseException, RecordNotFoundException;
    // mazani by melo byt kaskadove pri smazani registratora
    // TODO: zkontrolovat, jestli to tak funguje
}
