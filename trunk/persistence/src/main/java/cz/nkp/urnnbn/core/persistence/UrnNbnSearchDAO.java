/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence;

import cz.nkp.urnnbn.core.Sigla;
import cz.nkp.urnnbn.core.dto.UrnNbnSearch;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;

/**
 *
 * @author Martin Řehánek
 */
public interface UrnNbnSearchDAO {

    public String TABLE_NAME = "UrnNbnSearch";
    public String ATTR_REGISTRAR_ID = "registrarId";
    public String ATTR_LAST_DOCUMENT_CODE = "lastFoundDocumentCode";

    public void insertUrnNbnSearch(UrnNbnSearch search) throws DatabaseException, AlreadyPresentException, RecordNotFoundException;

    public UrnNbnSearch getSearchByRegistrarId(long registrarId) throws DatabaseException, RecordNotFoundException;

    public void updateUrnNbnSearch(UrnNbnSearch search) throws DatabaseException, RecordNotFoundException;
    //mazani by melo byt kaskadove pri smazani registratora
}
