package cz.nkp.urnnbn.czidlo_web_api.api.documents;

import cz.nkp.urnnbn.czidlo_web_api.api.documents.core.Record;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.czidlo_web_api.api.documents.core.RecordToBeCreatedOrUpdated;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.BadArgumentException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.ConflictException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.InsufficientRightsException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException;
import cz.nkp.urnnbn.services.exceptions.*;

public interface DocumentManager {

    /**
     * Retrieves record identified by given URN:NBN.
     *
     * @param urnNbn
     * @return record
     */
    public Record getRecord(UrnNbn urnNbn);

    /**
     * Deactivates record identified by given URN:NBN.
     *
     * @param urnNbn
     * @param note   reason for deactivation
     * @param login  login of user performing the operation
     * @return true if record was found and deactivated, false if already inactive
     * @throws UnknownRecordException if record with given URN:NBN does not exist
     */
    public boolean deactivateRecord(UrnNbn urnNbn, String note, String login) throws UnknownRecordException, InsufficientRightsException;

    /**
     * Reactivates record identified by given URN:NBN.
     *
     * @param urnNbn
     * @param login  login of user performing the operation
     * @return true if record was found and reactivated, false if already active
     * @throws UnknownRecordException if record with given URN:NBN does not exist
     */
    public boolean reactivateRecord(UrnNbn urnNbn, String login) throws UnknownRecordException, InsufficientRightsException;


    /**
     * Creates new record along with assigning or confirming URN:NBN.
     *
     * @param record record to be created
     * @param login  login of user performing the operation
     * @return
     * @throws BadArgumentException
     * @throws UnknownUserException
     * @throws RegistrarScopeIdentifierCollisionException
     * @throws UnknownArchiverException
     * @throws ArchiverIsRegistrarException
     * @throws IncorrectPredecessorStatus
     * @throws UnknownRecordException
     * @throws InsufficientRightsException
     */
    public UrnNbn createRecord(RecordToBeCreatedOrUpdated record, String login) throws
            BadArgumentException, UnknownUserException, RegistrarScopeIdentifierCollisionException, UnknownArchiverException, ArchiverIsRegistrarException,
            IncorrectPredecessorStatus, UnknownRecordException, InsufficientRightsException,;

    /**
     * Updates existing record identified by URN:NBN.
     *
     * @param record to be updated
     * @param login  login of user performing the operation
     * @throws BadArgumentException
     * @throws UnknownUserException
     * @throws InsufficientRightsException
     * @throws UnknownRecordException
     */
    public void updateRecord(RecordToBeCreatedOrUpdated record, String login) throws BadArgumentException,
            UnknownUserException, InsufficientRightsException, UnknownRecordException;


    /**
     * Adds predecessor -> successor relation. Also deactivates the predecessor record if it is active.
     *
     * @param predecessor
     * @param successor
     * @param note
     * @param login
     * @throws UnknownRecordException
     * @throws InsufficientRightsException
     * @throws IncorrectPredecessorStatus
     * @throws ConflictException           Adding predecessor-successor relation would create a cycle in predecessor-successor graph
     */
    public void addPredecessorSuccessorRelation(UrnNbn predecessor, UrnNbn successor, String note, String login) throws UnknownRecordException, InsufficientRightsException, IncorrectPredecessorStatus, ConflictException;

    /**
     * Removes predecessor -> successor relation.
     *
     * @param predecessor
     * @param successor
     * @param login
     * @throws UnknownRecordException
     * @throws InsufficientRightsException
     */
    public void removePredecessorSuccessorRelation(UrnNbn predecessor, UrnNbn successor, String login) throws UnknownRecordException, InsufficientRightsException;
}
