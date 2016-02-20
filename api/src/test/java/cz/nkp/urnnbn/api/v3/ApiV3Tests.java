package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.config.RestAssuredConfig.newConfig;
import static com.jayway.restassured.config.SSLConfig.sslConfig;
import static com.jayway.restassured.config.XmlConfig.xmlConfig;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static com.jayway.restassured.path.xml.config.XmlPathConfig.xmlPathConfig;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertThat;

import java.util.Random;

import javax.xml.namespace.NamespaceContext;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.RedirectConfig;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;
import com.jayway.restassured.path.xml.config.XmlPathConfig;

import cz.nkp.urnnbn.api.Utils;
import cz.nkp.urnnbn.core.CountryCode;

public abstract class ApiV3Tests {

    private static final String LANG_CODE = "cz";
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
        CountryCode.initialize(LANG_CODE);
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
        XmlPath xmlPath = XmlPath.from(xml);
        int registrarsCount = xmlPath.getInt("response.registrars.registrar.size()");
        int registrarPosition = rand.nextInt(registrarsCount);
        // registrar not prefixed because of this bug: https://github.com/jayway/rest-assured/issues/647
        String registrarCode = xmlPath.getString("response.registrars.registrar[" + registrarPosition + "].@code");
        // LOGGER.info(String.format("position: %d, code: %s", registrarPosition, registrarCode));
        return registrarCode;
    }

    static class RsId {
        final String registrarCode;
        final String type;
        final String value;

        public RsId(String registrarCode, String type, String value) {
            this.registrarCode = registrarCode;
            this.type = type;
            this.value = value;
        }
    }

    String buildResolvationPath(RsId id) {
        return "/registrars/" + id.registrarCode + "/digitalDocuments/registrarScopeIdentifier/" + id.type + "/" + id.value;
    }

    String buildResolvationPath(String urnNbnString) {
        return "/resolver/" + urnNbnString;
    }

    void deleteAllRegistrarScopeIdentifiers(String urnNbn, String login, String password) {
        String url = HTTPS_API_URL + buildResolvationPath(Utils.urlEncodeReservedChars(urnNbn)) + "/registrarScopeIdentifiers";
        with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).auth().basic(login, password) //
                .expect() //
                .statusCode(200) //
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString)) //
                .body(hasXPath("/c:response/c:registrarScopeIdentifiers", nsContext)) //
                .when().delete(url);
    }

    void insertRegistrarScopeId(String urnNbn, String type, String newValue, String login, String password) {
        String url = HTTPS_API_URL + buildResolvationPath(Utils.urlEncodeReservedChars(urnNbn)) + "/registrarScopeIdentifiers/" + type;
        String responseXml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).auth().basic(login, password)//
                .body(newValue).expect()//
                .statusCode(201)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().put(url)//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        assertThat(xmlPath.getString("id"), equalTo(newValue));
        assertThat(xmlPath.getString("id.@type"), equalTo(type));
    }

}
