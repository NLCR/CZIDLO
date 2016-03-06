package cz.nkp.urnnbn.api.v4.exceptions;

import javax.ws.rs.core.Response.Status;

public class UnknownRegistrarScopeIdentifier extends ApiV4Exception {

    public UnknownRegistrarScopeIdentifier(String errorMessage) {
        // TODO: zdokumentovat
        super(Status.NOT_FOUND, "UNKNOWN_REGISTRAR_SCOPE_IDENTIFIER", errorMessage);
    }
}
