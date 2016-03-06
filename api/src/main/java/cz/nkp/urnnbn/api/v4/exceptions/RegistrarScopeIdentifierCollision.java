package cz.nkp.urnnbn.api.v4.exceptions;

import javax.ws.rs.core.Response.Status;

public class RegistrarScopeIdentifierCollision extends ApiV4Exception {

    public RegistrarScopeIdentifierCollision(String errorMessage) {
        // TODO: zdokumentovat
        super(Status.CONFLICT, "REGISTRAR_SCOPE_IDENTIFIER_COLLISION", errorMessage);
    }

}
