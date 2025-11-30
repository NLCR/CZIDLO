package cz.nkp.urnnbn.czidlo_web_api.api.resources;

import cz.nkp.urnnbn.czidlo_web_api.api.ApiError;
import cz.nkp.urnnbn.czidlo_web_api.api.AuthenticatedUserPrincipal;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.BadArgumentException;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.UnauthorizedException;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.util.function.Function;

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
        if (sc.getUserPrincipal() == null) {
            throw new UnauthorizedException("User must be authenticated for this operation");
        }
        if (!(sc.getUserPrincipal() instanceof AuthenticatedUserPrincipal principal)) {
            throw new UnauthorizedException("Invalid security context principal: " +
                    (sc.getUserPrincipal() == null ? "null" : sc.getUserPrincipal().getClass().getName())
            );
        }
        return principal;
    }

    protected <T> T readParam(String paramName, Function<String, T> funk) throws BadArgumentException {
        try {
            return funk.apply(paramName);
        } catch (NullPointerException e) {
            throw new BadArgumentException("Missing mandatory parameter: " + paramName);
        } catch (ClassCastException e) {
            throw new BadArgumentException("Invalid type for parameter: " + paramName);
        }
    }

    protected Response mandatoryBodyMissingResponse() {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ApiError("Missing mandatory body"))
                .build();
    }

    protected Response internalErrorResponse(Exception e) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ApiError("Internal server error: " + e.getMessage()))
                .build();
    }

    protected Response mandatoryParamMissingResponse(String paramName) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ApiError("Missing mandatory parameter: " + paramName))
                .build();
    }

    protected Response invalidParamValueResponse(String parameterName, String value) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new ApiError("Invalid value for parameter " + parameterName + ": " + value))
                .build();
    }
}
