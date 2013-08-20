/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence;

import cz.nkp.urnnbn.core.EntityType;
import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.UrnNbnRegistrationMode;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.dto.UrnNbnExport;
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
    public String ATTR_RESERVED = "reserved";
    public String ATTR_REGISTERED = "registered";
    public String ATTR_DEACTIVATED = "deactivated";
    public String ATTR_REGISTRAR_CODE = "registrarCode";
    public String ATTR_DOCUMENT_CODE = "documentCode";
    public String ATTR_ACTIVE = "active";
    public String ATTR_DEACTIVATION_NOTE = "deactivationNote";
    //predecessor/successor
    public String SUCCESSOR_TABLE_NAME = "UrnNbnSuccessors";
    public String ATTR_PRECESSOR_REGISTRAR_CODE = "predecessorRegCode";
    public String ATTR_PRECESSOR_DOCUMENT_CODE = "predecessorDocCode";
    public String ATTR_SUCCESSOR_REGISTRAR_CODE = "successorRegCode";
    public String ATTR_SUCCESSOR_DOCUMENT_CODE = "successorDocCode";
    public String ATTR_NOTE = "note";

    /**
     *
     * @param urn
     * @throws DatabaseException
     * @throws AlreadyPresentException
     * @throws RecordNotFoundException
     */
    public void insertUrnNbn(UrnNbn urn) throws DatabaseException, AlreadyPresentException, RecordNotFoundException;

    public void insertUrnNbnPredecessor(UrnNbn predecessor, UrnNbn successor, String note) throws DatabaseException, AlreadyPresentException, RecordNotFoundException;

    public UrnNbn getUrnNbnByDigDocId(Long digDocId) throws DatabaseException, RecordNotFoundException;

    //this will be used when resolving or searching for new not assigned urn:nbn
    //todo: mozna optimalizovat a vyhnout se vyjimkam, pokud budu hledat neexistujici urn:nbn
    //tj. vytvorit pro tohle novou metodu tady
    public UrnNbn getUrnNbnByRegistrarCodeAndDocumentCode(RegistrarCode registrarCode, String documentCode) throws DatabaseException, RecordNotFoundException;

    public List<UrnNbn> getUrnNbnsByTimestamps(DateTime from, DateTime until) throws DatabaseException;

    public List<UrnNbn> getUrnNbnsByRegistrarCodeAndTimestamps(RegistrarCode registrarCode, DateTime from, DateTime until) throws DatabaseException;

    public List<UrnNbn> getUrnNbnsByRegistrarCode(RegistrarCode registrarCode) throws DatabaseException;

    public List<UrnNbnWithStatus> getPredecessors(UrnNbn urn) throws DatabaseException;

    public List<UrnNbnWithStatus> getSuccessors(UrnNbn urn) throws DatabaseException;

    public boolean isPredecessesor(UrnNbn precessor, UrnNbn successor) throws DatabaseException;
    
    public List<UrnNbnExport> selectByCriteria(DateTime begin, DateTime end, List<String> registrars, UrnNbnRegistrationMode registrationMode, 
    		String entityType, Boolean cnbAssigned, Boolean issnAsigned, Boolean isbnAssigned, Boolean active) throws DatabaseException;

    //only for tests, rollbacks
    public void reactivateUrnNbn(RegistrarCode registrarCode, String documentCode) throws DatabaseException;

    public void deactivateUrnNbn(RegistrarCode registrarCode, String documentCode, String note) throws DatabaseException;

    //only for tests, rollbacks
    public void deleteUrnNbn(RegistrarCode registrarCode, String documentCode) throws DatabaseException;

    //only for tests
    public void deleteAllUrnNbns() throws DatabaseException;

    //only for tests, rollbacks
    public void deletePredecessors(UrnNbn urn) throws DatabaseException;

    //only for tests
    public void deleteSuccessors(UrnNbn urn) throws DatabaseException;

    //only for tests
    public void deleteAllPredecessors() throws DatabaseException;
}
