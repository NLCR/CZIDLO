package cz.nkp.urnnbn.api.v3.exceptions;

import javax.ws.rs.core.Response.Status;

import cz.nkp.urnnbn.core.UrnNbnWithStatus;

/**
 * 
 * @author Martin Řehánek
 */
public class IncorrectUrnStateException extends ApiV3Exception {

    private static final long serialVersionUID = -3179637427845166321L;

    public IncorrectUrnStateException(UrnNbnWithStatus urnNbnWithStatus) {
        super(Status.FORBIDDEN, "INCORRECT_URN_NBN_STATE", urnNbnWithStatus.getUrn().toString() + ": " + urnNbnWithStatus.getStatus().toString());
    }
}
