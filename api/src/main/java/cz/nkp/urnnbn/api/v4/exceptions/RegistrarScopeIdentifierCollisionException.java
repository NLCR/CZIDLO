package cz.nkp.urnnbn.api.v4.exceptions;

import javax.ws.rs.core.Response.Status;

import cz.nkp.urnnbn.api.v4.ResponseFormat;

public class RegistrarScopeIdentifierCollisionException extends ApiV4Exception {

    public RegistrarScopeIdentifierCollisionException(ResponseFormat format, String errorMessage) {
        super(format, Status.CONFLICT, "REGISTRAR_SCOPE_IDENTIFIER_COLLISION", errorMessage);
    }

}
