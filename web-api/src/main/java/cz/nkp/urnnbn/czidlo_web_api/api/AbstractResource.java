package cz.nkp.urnnbn.czidlo_web_api.api;

import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnauthorizedException;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

// Base class for all resources to set the default response content types and their priorities (quality factors).
// The default content type is JSON (quality factor 1.0), XML is secondary
@Produces({"application/json;qs=1.0", "application/xml;qs=0.5"})
public abstract class AbstractResource {

    @OPTIONS
    @Path("{path: .*}")
    public Response options() {
        return Response.ok().build();
    }

    protected AuthenticatedUserPrincipal requireUserPrincipal(SecurityContext sc) throws UnauthorizedException {
        if (sc == null) {
            throw new UnauthorizedException("User must be authenticated for this operation");
        }
        if (!(sc.getUserPrincipal() instanceof AuthenticatedUserPrincipal principal)) {
            throw new UnauthorizedException("Invalid security context principal: " +
                    (sc.getUserPrincipal() == null ? "null" : sc.getUserPrincipal().getClass().getName())
            );
        }
        return principal;
    }
}
