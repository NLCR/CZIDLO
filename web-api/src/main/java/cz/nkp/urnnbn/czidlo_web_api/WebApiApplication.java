package cz.nkp.urnnbn.czidlo_web_api;

import cz.nkp.urnnbn.czidlo_web_api.api.CORSResponseFilter;
import cz.nkp.urnnbn.utils.PropertyLoader;
import cz.nkp.urnnbn.webcommon.config.ResourceUtilizer;
import cz.nkp.urnnbn.xml.config.XmlModuleConfiguration;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.ws.rs.ApplicationPath;

import java.io.InputStream;
import java.util.logging.Logger;

@OpenAPIDefinition(
        info = @Info(title = "CZIDLO Web API", version = "1.13.0",
                description = "API for accessing the CZIDLO system by web applications."),
        servers = {
                // relativní URL je přenositelné (Tomcat doplní host/port):
                @Server(url = "/web-api", description = "Local Tomcat (dev)"),
                // absolutní URL pro produkci:
                @Server(url = "https://czidlo-web.test.api.trinera.cloud", description = "Production")
        }
)
@ApplicationPath("/api")
public class WebApiApplication extends org.glassfish.jersey.server.ResourceConfig {

    private static final Logger LOGGER = Logger.getLogger(WebApiApplication.class.getName());
    private static final String WEB_APP_NAME = "WEB-API";
    private static final String PROPERTIES_FILE = "web-api.properties";


    public WebApiApplication() {
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

        //load configuration from file web-api.properties
        new ResourceUtilizer(LOGGER) {
            @Override
            public void processResource(InputStream in) throws Exception {
                PropertyLoader loader = new PropertyLoader(in);
                WebApiModuleConfiguration.instanceOf().initialize(WEB_APP_NAME, loader);
                //XmlModuleConfiguration.instanceOf().initialize(loader);
            }
        }.run(PROPERTIES_FILE);
    }
}
