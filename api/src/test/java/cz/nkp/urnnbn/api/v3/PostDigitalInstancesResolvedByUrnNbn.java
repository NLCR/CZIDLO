package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.api.Utils;

/**
 * Tests for POST /api/v3/urnnbn/${URN_NBN}/digitalInstances
 *
 */
public class PostDigitalInstancesResolvedByUrnNbn extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(PostDigitalInstancesResolvedByUrnNbn.class.getName());

    private Long digLibId;
    private Long registrar2_digLibId;

    @BeforeClass
    public void beforeClass() {
        init();
        digLibId = getDigitalLibraryIdOrNull(REGISTRAR);
        registrar2_digLibId = getDigitalLibraryIdOrNull(REGISTRAR2);
    }

    private String buildUrl(String urnNbn) {
        return HTTPS_API_URL + buildResolvationPath(urnNbn) + "/digitalInstances";
    }

    @Test
    public void notAuthenticated() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        LOGGER.info(urnNbn);
        String bodyXml = diImportBuilder.minimal(digLibId, WORKING_URL);
        given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(401)//
                .when().post(buildUrl(urnNbn)).andReturn().asString();
    }

    @Test
    public void notAuthorized() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        LOGGER.info(urnNbn);
        String bodyXml = diImportBuilder.minimal(digLibId, WORKING_URL);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_NO_RIGHTS.login, USER_NO_RIGHTS.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(401)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHORIZED");
    }

    @Test
    public void urnnbnInvalid() {
        String urnNbn = Utils.getRandomItem(URNNBN_INVALID);
        LOGGER.info(urnNbn);
        String bodyXml = diImportBuilder.minimal(digLibId, WORKING_URL);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_URN_NBN");
    }

    @Test
    public void urnnbnValidFree() {
        String urnNbn = getRandomFreeUrnNbnOrNull(REGISTRAR);
        if (urnNbn == null) {
            LOGGER.warning("no free urn:nbn found, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String bodyXml = diImportBuilder.minimal(digLibId, WORKING_URL);
            String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                    .given().request().body(bodyXml).contentType(ContentType.XML)//
                    .expect()//
                    .statusCode(404)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:error", nsContext))//
                    .when().post(buildUrl(urnNbn)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
            assertEquals("UNKNOWN_URN", xmlPath.getString("code"));
        }
    }

    @Test
    public void urnnbnValidReserved() {
        String urnNbn = getReservedUrnNbn(REGISTRAR, USER);
        LOGGER.info(urnNbn);
        String bodyXml = diImportBuilder.minimal(digLibId, WORKING_URL);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertEquals("UNKNOWN_DIGITAL_DOCUMENT", xmlPath.getString("code"));
    }

    @Test
    public void invalidBodyIncorrectNamespace() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        LOGGER.info(urnNbn);
        String bodyXml = diImportBuilder.noNamespace(digLibId, WORKING_URL);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertEquals("INVALID_DATA", xmlPath.getString("code"));
    }

    @Test
    public void invalidBodyInvalidDiUrl() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        LOGGER.info(urnNbn);
        // TODO: possibly test other invalid urls
        String bodyXml = diImportBuilder.minimal(digLibId, "ftp://something.com/somewhere");
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(urnNbn)).andReturn().asString();
        // LOGGER.info(responseXml);
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertEquals("INVALID_DATA", xmlPath.getString("code"));
    }

    @Test
    public void unknowDigitalLibrary() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        LOGGER.info(urnNbn);
        String bodyXml = diImportBuilder.minimal(UNKNOWN_DIG_LIB_DI, WORKING_URL);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertEquals("UNKNOWN_DIGITAL_LIBRARY", xmlPath.getString("code"));
    }

    @Test
    public void diPresent() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        LOGGER.info(urnNbn);
        insertDigitalInstance(urnNbn, digLibId, WORKING_URL, USER);
        String bodyXml = diImportBuilder.minimal(digLibId, WORKING_URL);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(403)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertEquals("DIGITAL_INSTANCE_ALREADY_PRESENT", xmlPath.getString("code"));
    }

    @Test
    public void diNotPresent() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        LOGGER.info(urnNbn);
        String bodyXml = diImportBuilder.minimal(digLibId, WORKING_URL);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(201)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalInstance", nsContext))//
                .when().post(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.digitalInstance");
        assertEquals(true, Utils.booleanValue(xmlPath.getString("@active")));
        assertTrue(DateTime.parse(xmlPath.getString("created")).isBeforeNow());
        assertEquals(digLibId.longValue(), xmlPath.getLong("digitalLibraryId"));
    }

    @Test
    public void diPresentDeactivated() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        LOGGER.info(urnNbn);
        long diDeactivated = insertDigitalInstance(urnNbn, digLibId, WORKING_URL, USER);
        deactivateDigitalInstance(diDeactivated, USER);
        String bodyXml = diImportBuilder.minimal(digLibId, WORKING_URL);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(201)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalInstance", nsContext))//
                .when().post(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.digitalInstance");
        assertEquals(true, Utils.booleanValue(xmlPath.getString("@active")));
        assertTrue(DateTime.parse(xmlPath.getString("created")).isBeforeNow());
        assertEquals(digLibId.longValue(), xmlPath.getLong("digitalLibraryId"));
    }

    @Test
    public void twoDigitalInstances() {
        if (digLibId == null || registrar2_digLibId == null) {
            LOGGER.warning("digital library not available, ignoring");
        } else {
            String urnNbn = registerUrnNbn(REGISTRAR, USER);
            insertDigitalInstance(urnNbn, registrar2_digLibId, WORKING_URL, USER);
            LOGGER.info(urnNbn);
            String bodyXml = diImportBuilder.minimal(digLibId, WORKING_URL);
            String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                    .given().request().body(bodyXml).contentType(ContentType.XML)//
                    .expect()//
                    .statusCode(201)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:digitalInstance", nsContext))//
                    .when().post(buildUrl(urnNbn)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.digitalInstance");
            assertEquals(true, Utils.booleanValue(xmlPath.getString("@active")));
            assertTrue(DateTime.parse(xmlPath.getString("created")).isBeforeNow());
            assertEquals(digLibId.longValue(), xmlPath.getLong("digitalLibraryId"));
        }
    }

}
