package cz.nkp.urnnbn.api.v6.exceptions;

import cz.nkp.urnnbn.api.v6.ResponseFormat;

import javax.ws.rs.core.Response.Status;

public class RegistrarScopeIdentifierCollisionException extends ApiV6Exception {

    public RegistrarScopeIdentifierCollisionException(ResponseFormat format, String errorMessage) {
        super(format, Status.CONFLICT, "REGISTRAR_SCOPE_IDENTIFIER_COLLISION", errorMessage);
    }

}
