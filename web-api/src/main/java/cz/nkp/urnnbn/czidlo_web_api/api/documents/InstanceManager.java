package cz.nkp.urnnbn.czidlo_web_api.api.documents;

import cz.nkp.urnnbn.czidlo_web_api.api.documents.core.DigInst;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.InsufficientRightsException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnknownRecordException;

public interface InstanceManager {

    /**
     * Deactivates digital instance identified by given internal ID.
     *
     * @param instanceId internal ID of digital instance
     * @param login      login of user performing the operation
     * @return true if digital instance was found and deactivated, false if already inactive
     * @throws UnknownRecordException      if digital instance with given ID does not exist
     * @throws InsufficientRightsException if user identified by given login has insufficient rights to perform the operation,
     *                                     i.e. doesn't manage the registrar that owns the digital library that the digital instance is in
     */
    public boolean deactivateInstance(long instanceId, String login) throws UnknownRecordException, InsufficientRightsException;

    /**
     * Retrieves digital instance identified by given internal ID.
     *
     * @param instanceId internal ID of digital instance
     * @return digital instance
     * @throws UnknownRecordException if digital instance with given ID does not exist
     */
    public DigInst getDigitalInstanceById(long instanceId) throws UnknownRecordException;
}
