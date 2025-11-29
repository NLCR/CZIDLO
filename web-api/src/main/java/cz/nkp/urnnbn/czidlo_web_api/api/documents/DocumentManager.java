package cz.nkp.urnnbn.czidlo_web_api.api.documents;

import cz.nkp.urnnbn.czidlo_web_api.api.documents.core.Record;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException;

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
     * @param note                           reason for deactivation
     * @param loginOfUserPerformingOperation login of user performing the operation
     * @return true if record was found and deactivated, false if already inactive
     * @throws UnknownRecordException if record with given URN:NBN does not exist
     */
    public boolean deactivateRecord(UrnNbn urnNbn, String note, String loginOfUserPerformingOperation) throws UnknownRecordException;
}
