package cz.nkp.urnnbn.czidlo_web_api.api.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.json.JSONObject;

@Path("/")
public class RootResource extends AbstractResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/")
    public String getRoot() {
        return "API is running. For status information, access /status endpoint.";
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/status")
    public String getStatusText() {
        StringBuilder info = new StringBuilder();
        info.append("CZIDLO web API\n");
        info.append("Verze API: 1.15.0\n");
        info.append("Jersey 4 (EE11)\n");
        info.append("Status: OK\n");
        return info.toString();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/info")
    public String getApiInfoJson() {
        JSONObject root = new JSONObject();
        root.put("apiName", "CZIDLO web API");
        root.put("apiVersion", "1.15.0");
        root.put("jerseyVersion", "Jersey 4 (EE11)");
        root.put("status", "OK");
        return root.toString();
    }
}
