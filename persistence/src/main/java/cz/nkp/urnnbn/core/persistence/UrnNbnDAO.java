/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence;

import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.exceptions.AlreadyPresentException;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import java.util.List;
import org.joda.time.DateTime;

/**
 *
 * @author Martin Řehánek
 */
public interface UrnNbnDAO {

    public String TABLE_NAME = "UrnNbn";
    public String ATTR_DIG_DOC_ID = "digitalDocumentId";
    public String ATTR_CREATED = "created";
    public String ATTR_UPDATED = "modified";
    public String ATTR_REGISTRAR_CODE = "registrarCode";
    public String ATTR_DOCUMENT_CODE = "documentCode";
    public String ATTR_ACTIVE = "active";
    //predecessor/successor
    public String SUCCESSOR_TABLE_NAME = "UrnNbnSuccessors";
    public String ATTR_PRECESSOR_REGISTRAR_CODE = "predecessorRegCode";
    public String ATTR_PRECESSOR_DOCUMENT_CODE = "predecessorDocCode";
    public String ATTR_SUCCESSOR_REGISTRAR_CODE = "successorRegCode";
    public String ATTR_SUCCESSOR_DOCUMENT_CODE = "successorDocCode";

    public void insertUrnNbn(UrnNbn urn) throws DatabaseException, AlreadyPresentException, RecordNotFoundException;

    /**
     *
     * @param urn
     * @param created creation timestamp to be set to newly created urn:nbn.
     * This should be used when urn:nbn from urnNbnReserved is used and we wish
     * to keep as "created" the timestamp of reservation.
     * @throws DatabaseException
     * @throws AlreadyPresentException
     * @throws RecordNotFoundException
     */
    public void insertUrnNbn(UrnNbn urn, DateTime created) throws DatabaseException, AlreadyPresentException, RecordNotFoundException;

    public void insertUrnNbnPredecessor(UrnNbn predecessor, UrnNbn successor) throws DatabaseException, AlreadyPresentException, RecordNotFoundException;

    public UrnNbn getUrnNbnByDigDocId(Long digDocId) throws DatabaseException, RecordNotFoundException;

    //this will be used when resolving or searching for new not assigned urn:nbn
    //todo: mozna optimalizovat a vyhnout se vyjimkam, pokud budu hledat neexistujici urn:nbn
    //tj. vytvorit pro tohle novou metodu tady
    public UrnNbn getUrnNbnByRegistrarCodeAndDocumentCode(RegistrarCode registrarCode, String documentCode) throws DatabaseException, RecordNotFoundException;

    public List<UrnNbn> getUrnNbnsByTimestamps(DateTime from, DateTime until) throws DatabaseException;

    public List<UrnNbn> getUrnNbnsByRegistrarCodeAndTimestamps(RegistrarCode registrarCode, DateTime from, DateTime until) throws DatabaseException;

    public List<UrnNbn> getPredecessors(UrnNbn urn) throws DatabaseException;

    public List<UrnNbn> getSuccessors(UrnNbn urn) throws DatabaseException;

    public boolean isPredecessesor(UrnNbn precessor, UrnNbn successor) throws DatabaseException;

    public void deactivateUrnNbn(RegistrarCode registrarCode, String documentCode) throws DatabaseException;

    //only for tests
    public void deleteUrnNbn(RegistrarCode registrarCode, String documentCode) throws DatabaseException;

    //only for tests
    public void deleteAllUrnNbns() throws DatabaseException;

    //only for tests
    public void deletePredecessors(UrnNbn urn) throws DatabaseException;

    //only for tests
    public void deleteSuccessors(UrnNbn urn) throws DatabaseException;

    //only for tests
    public void deleteAllPredecessors() throws DatabaseException;
}
