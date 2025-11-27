package cz.nkp.urnnbn.czidlo_web_api.api;

import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;

// Base class for all resources to set the default response content types and their priorities (quality factors).
// The default content type is JSON (quality factor 1.0), XML is secondary
@Produces({"application/json;qs=1.0", "application/xml;qs=0.5"})
public abstract class AbstractResource {

    @OPTIONS
    @Path("{path: .*}")
    public Response options() {
        return Response.ok().build();
    }
}
