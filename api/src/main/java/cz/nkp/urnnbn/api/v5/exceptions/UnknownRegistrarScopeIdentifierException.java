package cz.nkp.urnnbn.api.v5.exceptions;

import cz.nkp.urnnbn.api.v5.ResponseFormat;

import javax.ws.rs.core.Response.Status;

public class UnknownRegistrarScopeIdentifierException extends ApiV5Exception {

    public UnknownRegistrarScopeIdentifierException(ResponseFormat format, String errorMessage) {
        super(format, Status.NOT_FOUND, "UNKNOWN_REGISTRAR_SCOPE_IDENTIFIER", errorMessage);
    }
}
