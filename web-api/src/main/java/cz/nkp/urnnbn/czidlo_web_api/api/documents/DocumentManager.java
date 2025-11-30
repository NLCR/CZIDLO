package cz.nkp.urnnbn.czidlo_web_api.api.documents;

import cz.nkp.urnnbn.czidlo_web_api.api.documents.core.Record;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.czidlo_web_api.api.documents.core.RecordToBeImported;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.BadArgumentException;
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


    public UrnNbn createRecord(RecordToBeImported record, String login) throws
            BadArgumentException, UnknownUserException, RegistrarScopeIdentifierCollisionException, UnknownArchiverException,
            IncorrectPredecessorStatus, UnknownRecordException, InsufficientRightsException;
}
