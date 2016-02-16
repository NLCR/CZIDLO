package cz.nkp.urnnbn.api.v3;

//import static com.jayway.restassured.RestAssured.*;
//import static com.jayway.restassured.matcher.RestAssuredMatchers.*;
//import static com.jayway.restassured.config.RestAssuredConfig.*;
//import static com.jayway.restassured.config.XmlConfig.*;
//import static org.hamcrest.Matchers.*;
//import static com.jayway.restassured.path.xml.XmlPath.*;
//import static com.jayway.restassured.path.xml.config.XmlPathConfig.*;
//import static com.jayway.restassured.RestAssured.expect;
//import static com.jayway.restassured.RestAssured.given;
//import static com.jayway.restassured.RestAssured.with;
//import static com.jayway.restassured.config.RestAssuredConfig.newConfig;
//import static com.jayway.restassured.config.XmlConfig.xmlConfig;
//import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
//import static org.hamcrest.Matchers.equalTo;
//import static org.hamcrest.Matchers.hasXPath;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.config.RestAssuredConfig.newConfig;
import static com.jayway.restassured.config.XmlConfig.xmlConfig;
import static com.jayway.restassured.path.xml.config.XmlPathConfig.xmlPathConfig;
import static com.jayway.restassured.config.RedirectConfig.redirectConfig;
import static com.jayway.restassured.config.SSLConfig.sslConfig;

import java.util.Random;

import javax.xml.namespace.NamespaceContext;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.RedirectConfig;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.path.xml.XmlPath;
import com.jayway.restassured.path.xml.config.XmlPathConfig;

import cz.nkp.urnnbn.api.Utils;

public abstract class ApiV3Tests {

    private static final String BASE_URI = "http://localhost";
    private static final int PORT = 8080;
    private static final String BASE_PATH = "/api/v3";
    static final String HTTPS_API_URL = "https://localhost:8443" + BASE_PATH;

    private static final String RESPONSE_NS = "http://resolver.nkp.cz/v3/";
    private static final String RESPONSE_NS_PREFIX = "c";
    private static final String RESPONSE_XSD = "http://localhost:8080/api/v3/response.xsd";

    Random rand = new Random();
    String responseXsdString;
    NamespaceContext nsContext;

    void init() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.port = PORT;
        RestAssured.basePath = BASE_PATH;
        // doesn't work, must use .relaxedHTTPSValidation() in each request
        // see https://github.com/jayway/rest-assured/issues/561
        RestAssured.useRelaxedHTTPSValidation();
        // RestAssured.authentication = basic("username", "password");
        // RestAssured.rootPath = "x.y.z";
        responseXsdString = Utils.readXsd(RESPONSE_XSD);
        nsContext = Utils.buildNsContext("c", RESPONSE_NS);
        // XmlConfig.xmlConfig().namespaceAware(true).declareNamespace(RESPONSE_NS_PREFIX, RESPONSE_NS);
    }

    RestAssuredConfig namespaceAwareXmlConfig() {
        return newConfig().xmlConfig(xmlConfig().with().namespaceAware(true).declareNamespace(RESPONSE_NS_PREFIX, RESPONSE_NS))//
                .sslConfig(sslConfig().relaxedHTTPSValidation())//
                // https + redirections doesn't work
                // see https://github.com/jayway/rest-assured/issues/467
                .redirect(new RedirectConfig(true, false, false, 10))//
        ;
    }

    XmlPathConfig namespaceAwareXmlpathConfig() {
        return xmlPathConfig().declaredNamespace(RESPONSE_NS_PREFIX, RESPONSE_NS);
    }

    String getRandomRegistrarCode() {
        String xml = with().config(namespaceAwareXmlConfig()).when().get("/registrars").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).using(namespaceAwareXmlpathConfig());
        int registrarsCount = xmlPath.getInt("c:response.c:registrars.c:registrar.size()");
        int registrarPosition = rand.nextInt(registrarsCount);
        // registrar not prefixed because of this bug: https://github.com/jayway/rest-assured/issues/647
        String registrarCode = xmlPath.getString("c:response.c:registrars.registrar[" + registrarPosition + "].@code");
        // LOGGER.info(String.format("position: %d, code: %s", registrarPosition, registrarCode));
        return registrarCode;
    }

}
