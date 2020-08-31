package cz.nkp.urnnbn.api.v6.exceptions;

import cz.nkp.urnnbn.api.v6.ResponseFormat;

import javax.ws.rs.core.Response.Status;

public class UnknownRegistrarScopeIdentifierException extends ApiV6Exception {

    public UnknownRegistrarScopeIdentifierException(ResponseFormat format, String errorMessage) {
        super(format, Status.NOT_FOUND, "UNKNOWN_REGISTRAR_SCOPE_IDENTIFIER", errorMessage);
    }
}
