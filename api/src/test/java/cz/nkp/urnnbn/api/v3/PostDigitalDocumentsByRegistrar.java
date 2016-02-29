package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.hasXPath;

import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.api.Utils;
import cz.nkp.urnnbn.api.v3.pojo.Predecessor;

/**
 * Tests for POST /api/v3/registrars/${REGISTRARS_CODE}/digitalDocuments
 *
 */
public class PostDigitalDocumentsByRegistrar extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(PostDigitalDocumentsByRegistrar.class.getName());

    @BeforeClass
    public void beforeClass() {
        init();
    }

    private String buildUrl(String registrarCode) {
        return HTTPS_API_URL + "/registrars/" + Utils.urlEncodeReservedChars(registrarCode) + "/digitalDocuments";
    }

    @Test
    public void notAuthenticated() {
        String registrarCode = REGISTRAR;
        LOGGER.info(registrarCode);
        String bodyXml = ddRegistrationBuilder.minimal();
        // TODO:APIv4: return xml as well
        // String responseXml =
        with().config(namespaceAwareXmlConfig())//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(401)//
                // .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                // .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(registrarCode)).andReturn().asString();
        // XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        // Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHENTICATED");
    }

    @Test
    public void notAuthorized() {
        String registrarCode = REGISTRAR;
        LOGGER.info(registrarCode);
        String bodyXml = ddRegistrationBuilder.minimal();
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_NO_RIGHTS.login, USER_NO_RIGHTS.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                // .statusCode(401)//
                // .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                // .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(registrarCode)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHORIZED");
    }

    @Test
    public void registrarCodeInvalid() {
        String registrarCode = Utils.getRandomItem(REGISTRAR_CODES_INVALID);
        LOGGER.info("registrar code: " + registrarCode);
        String bodyXml = ddRegistrationBuilder.minimal();
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(registrarCode)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_REGISTRAR_CODE");
    }

    @Test
    public void registrarCodeValidUnknown() {
        String registrarCode = Utils.getRandomItem(REGISTRAR_CODES_VALID);
        LOGGER.info("registrar code: " + registrarCode);
        String bodyXml = ddRegistrationBuilder.minimal();
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(registrarCode)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "UNKNOWN_REGISTRAR");
    }

    @Test
    public void invalidDataNoNamespace() {
        String registrarCode = REGISTRAR;
        LOGGER.info("registrar code: " + registrarCode);
        String bodyXml = ddRegistrationBuilder.noNamespace();
        // LOGGER.info(bodyXml);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(registrarCode)).andReturn().asString();
        // LOGGER.info(responseXml);
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_DATA");
    }

    @Test
    public void invalidDataInvalidUrnNbnRegistrarCode() {
        String registrarCode = REGISTRAR;
        String urnNbn = "urn:nbn:cz:" + REGISTRAR2 + "-000000";
        LOGGER.info(String.format("registrar code: %s, %s", registrarCode, urnNbn));
        String bodyXml = ddRegistrationBuilder.withUrnNbn(urnNbn);
        // LOGGER.info(bodyXml);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(registrarCode)).andReturn().asString();
        // LOGGER.info(responseXml);
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_URN_NBN");
    }

    @Test
    public void invalidDataInvalidPredecessorRegistrarCode() {
        String registrarCode = REGISTRAR;
        Predecessor predecessor = new Predecessor("urn:nbn:cz:" + REGISTRAR2 + "-000000", "blabla");
        LOGGER.info(String.format("registrar code: %s, predecessor: %s", registrarCode, predecessor.urnNbn));
        String bodyXml = ddRegistrationBuilder.withPredecessors(asList(predecessor));
        // LOGGER.info(bodyXml);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(registrarCode)).andReturn().asString();
        // LOGGER.info(responseXml);
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_URN_NBN");
    }

    @Test
    public void invalidDataUrnNbnActiveAlready() {
        String registrarCode = REGISTRAR;
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        LOGGER.info(urnNbn);
        String bodyXml = ddRegistrationBuilder.withUrnNbn(urnNbn);
        // LOGGER.info(bodyXml);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(registrarCode)).andReturn().asString();
        // LOGGER.info(responseXml);
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_URN_NBN");
    }

    @Test
    public void invalidDataUrnNbnDeactivatedAlready() {
        String registrarCode = REGISTRAR;
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        deactivateUrnNbn(urnNbn, USER);
        LOGGER.info(urnNbn);
        String bodyXml = ddRegistrationBuilder.withUrnNbn(urnNbn);
        // LOGGER.info(bodyXml);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(registrarCode)).andReturn().asString();
        // LOGGER.info(responseXml);
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_URN_NBN");
    }

    // TODO: mody by_resolver, by_registrar, by_reservation
    // TODO: predecessors, successors

}
