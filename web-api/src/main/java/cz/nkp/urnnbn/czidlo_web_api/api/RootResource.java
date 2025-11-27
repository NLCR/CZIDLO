package cz.nkp.urnnbn.czidlo_web_api.api;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/")
public class RootResource extends AbstractResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getApiInfo() {
        StringBuilder info = new StringBuilder();
        info.append("CZIDLO web API\n");
        info.append("Verze API: 0.9.1\n");
        info.append("Jersey 4 (EE11)\n");
        return info.toString();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/status")
    public String getStatus() {
        return "{\"status\":\"OK\"}";
    }
}
