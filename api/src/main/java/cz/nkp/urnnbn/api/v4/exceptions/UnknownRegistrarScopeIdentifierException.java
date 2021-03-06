package cz.nkp.urnnbn.api.v4.exceptions;

import javax.ws.rs.core.Response.Status;

import cz.nkp.urnnbn.api.v4.ResponseFormat;

public class UnknownRegistrarScopeIdentifierException extends ApiV4Exception {

    public UnknownRegistrarScopeIdentifierException(ResponseFormat format, String errorMessage) {
        super(format, Status.NOT_FOUND, "UNKNOWN_REGISTRAR_SCOPE_IDENTIFIER", errorMessage);
    }
}
