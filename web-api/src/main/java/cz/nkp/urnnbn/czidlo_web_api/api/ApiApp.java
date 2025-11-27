package cz.nkp.urnnbn.czidlo_web_api.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.ws.rs.ApplicationPath;
//import jakarta.ws.rs.core.Application;

@OpenAPIDefinition(
        info = @Info(title = "CZIDLO Web API", version = "0.9.1",
                description = "API for accessing the CZIDLO system by web applications."),
        servers = {
                // relativní URL je přenositelné (Tomcat doplní host/port):
                @Server(url = "/web-api", description = "Local Tomcat (dev)"),
                // absolutní URL pro produkci:
                @Server(url = "https://czidlo-web.test.api.trinera.cloud", description = "Production")
        }
)
@ApplicationPath("/api")
public class ApiApp extends org.glassfish.jersey.server.ResourceConfig {

    public ApiApp() {
        //This configuration enables content negotiation based on suffixes:
        //GET http://localhost:8080/web-api/api/processes - XML (default)
        //GET http://localhost:8080/web-api/api/processes.json - JSON
        //GET http://localhost:8080/web-api/api/processes.xml - XML
        packages("cz.nkp.urnnbn.czidlo_web_api.api");
        property(
                org.glassfish.jersey.server.ServerProperties.MEDIA_TYPE_MAPPINGS,
                "json:application/json, xml:application/xml"
        );

        // Register Swagger resources
        register(io.swagger.v3.jaxrs2.integration.resources.OpenApiResource.class);
        register(io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource.class);
        // Enable CORS
        register(CORSResponseFilter.class);
    }
}
