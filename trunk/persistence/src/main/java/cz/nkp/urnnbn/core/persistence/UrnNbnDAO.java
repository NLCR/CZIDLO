/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence;

import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;

/**
 *
 * @author Martin Řehánek
 */
public interface UrnNbnDAO {

    public String TABLE_NAME = "UrnNbn";
    public String ATTR_DIG_REP_ID = "digitalRepresentationId";
    public String ATTR_REGISTRAR_CODE = "registrarCode";
    public String ATTR_DOCUMENT_CODE = "documentCode";
    public String ATTR_CREATED = "created";

    public void insertUrnNbn(UrnNbn urn) throws DatabaseException, AlreadyPresentException, RecordNotFoundException;

    public UrnNbn getUrnNbnByDigRegId(long digRepId) throws DatabaseException, RecordNotFoundException;

    //this will be used when resolving or searching for new not assigned urn:nbn
    //todo: mozna optimalizovat a vyhnout se vyjimkam, pokud budu hledat neexistujici urn:nbn
    //tj. vytvorit pro tohle novou metodu tady
    public UrnNbn getUrnNbnByRegistrarCodeAndDocumentCode(String registrarCode, String documentCode) throws DatabaseException, RecordNotFoundException;

    //TODO: test
    //booked->active nebo active->abandoned
    //misto atributu urn tam je registrarCode a documentCode
    //jinak by to mohlo svadet implementace k tomu, aby hledaly podle registrarId
    //ktery ale nemusi existovat - napr. pokud je stav booked
    //only digitalRepresentationId and Status will be updated
    public void deleteUrnNbn(UrnNbn urn) throws DatabaseException;

    //just for tests
    public void deleteAllUrnNbns() throws DatabaseException;
}
