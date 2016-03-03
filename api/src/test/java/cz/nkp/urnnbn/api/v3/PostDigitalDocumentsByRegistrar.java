package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.api.Utils;
import cz.nkp.urnnbn.api.v3.pojo.Predecessor;
import cz.nkp.urnnbn.api.v3.pojo.RsId;
import cz.nkp.urnnbn.api.v3.pojo.Successor;
import cz.nkp.urnnbn.core.CountryCode;

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
        LOGGER.info(registrarCode);
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
        LOGGER.info(registrarCode);
        String bodyXml = ddRegistrationBuilder.minimal();
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(registrarCode)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "UNKNOWN_REGISTRAR");
    }

    @Test
    public void invalidDataNoNamespace() {
        String registrarCode = REGISTRAR;
        LOGGER.info(registrarCode);
        String bodyXml = ddRegistrationBuilder.noNamespace();
        // LOGGER.info(bodyXml);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(400)//
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
        LOGGER.info(registrarCode);
        String urnNbn = "urn:nbn:cz:" + REGISTRAR2 + "-000000";
        LOGGER.info(urnNbn);
        String bodyXml = ddRegistrationBuilder.withUrnNbn(urnNbn);
        // LOGGER.info(bodyXml);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(400)//
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
        LOGGER.info(registrarCode);
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        LOGGER.info(urnNbn);
        String bodyXml = ddRegistrationBuilder.withUrnNbn(urnNbn);
        // LOGGER.info(bodyXml);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(400)//
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
        LOGGER.info(registrarCode);
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        deactivateUrnNbn(urnNbn, USER);
        LOGGER.info(urnNbn);
        String bodyXml = ddRegistrationBuilder.withUrnNbn(urnNbn);
        // LOGGER.info(bodyXml);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(registrarCode)).andReturn().asString();
        // LOGGER.info(responseXml);
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_URN_NBN");
    }

    // REGISTRATION MODES

    @Test
    public void invalidDataRsIdCollision() {
        String registrarCode = REGISTRAR;
        LOGGER.info(registrarCode);
        RsId rsId = new RsId(REGISTRAR, "type", "value");
        LOGGER.info(rsId.toString());
        String urnNbnWithRsId = registerUrnNbn(REGISTRAR, USER);
        insertRegistrarScopeId(urnNbnWithRsId, rsId, USER);
        String bodyXml = ddRegistrationBuilder.withRsIds(asList(rsId));
        // LOGGER.info(bodyXml);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(registrarCode)).andReturn().asString();
        // LOGGER.info(responseXml);
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_REGISTRAR_SCOPE_IDENTIFIER");
        // cleanup
        deleteAllRegistrarScopeIdentifiers(urnNbnWithRsId, USER);
    }

    @Test
    public void registrationModeByResolverForbidden() {
        String registrarCode = REGISTRAR_NO_MODES_ENABLED;
        LOGGER.info(registrarCode);
        String bodyXml = ddRegistrationBuilder.minimal();
        // LOGGER.info(bodyXml);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(registrarCode)).andReturn().asString();
        // LOGGER.info(responseXml);
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "UNAUTHORIZED_REGISTRATION_MODE");
    }

    @Test
    public void registrationModeByResolverAllowed() {
        String registrarCode = REGISTRAR;
        LOGGER.info(registrarCode);
        String bodyXml = ddRegistrationBuilder.minimal();
        // LOGGER.info(bodyXml);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(201)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbn", nsContext))//
                .when().post(buildUrl(registrarCode)).andReturn().asString();
        // LOGGER.info(responseXml);
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbn");
        assertEquals("ACTIVE", xmlPath.getString("status"));
        String urnNbn = xmlPath.getString("value");
        LOGGER.info(urnNbn);
        String[] urnSplit = Utils.splitUrnNbn(urnNbn);
        assertEquals(urnSplit[0], xmlPath.getString("countryCode"));
        assertEquals(urnSplit[1], xmlPath.getString("registrarCode"));
        assertEquals(urnSplit[2], xmlPath.getString("documentCode"));
        assertThat(xmlPath.getInt("digitalDocumentId"), greaterThanOrEqualTo(0));
        assertTrue("".equals(xmlPath.getString("deactivated")));
        assertTrue(DateTime.parse(xmlPath.getString("registered")).isBeforeNow());
        assertTrue("".equals(xmlPath.getString("deactivated")));
        // check urn:nbn state
        assertEquals("ACTIVE", getUrnNbnStatus(urnNbn));
    }

    @Test
    public void registrationModeByRegistrarForbidden() {
        String registrarCode = REGISTRAR_NO_MODES_ENABLED;
        String urnNbn = getRandomFreeUrnNbnOrNull(registrarCode);
        if (urnNbn == null) {
            LOGGER.warning("no free urn:nbn found, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String bodyXml = ddRegistrationBuilder.withUrnNbn(urnNbn);
            // LOGGER.info(bodyXml);
            String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                    .given().request().body(bodyXml).contentType(ContentType.XML)//
                    .expect()//
                    .statusCode(400)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:error", nsContext))//
                    .when().post(buildUrl(registrarCode)).andReturn().asString();
            // LOGGER.info(responseXml);
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
            Assert.assertEquals(xmlPath.getString("code"), "UNAUTHORIZED_REGISTRATION_MODE");
            // check urn:nbn state
            assertEquals("FREE", getUrnNbnStatus(urnNbn));
        }
    }

    @Test
    public void registrationModeByRegistrarAllowed() {
        String registrarCode = REGISTRAR;
        String urnNbn = getRandomFreeUrnNbnOrNull(registrarCode);
        if (urnNbn == null) {
            LOGGER.warning("no free urn:nbn found, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String bodyXml = ddRegistrationBuilder.withUrnNbn(urnNbn);
            // LOGGER.info(bodyXml);
            String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                    .given().request().body(bodyXml).contentType(ContentType.XML)//
                    .expect()//
                    .statusCode(201)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:urnNbn", nsContext))//
                    .when().post(buildUrl(registrarCode)).andReturn().asString();
            // LOGGER.info(responseXml);
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbn");
            assertEquals("ACTIVE", xmlPath.getString("status"));
            String[] urnSplit = Utils.splitUrnNbn(urnNbn);
            assertEquals(urnSplit[0], xmlPath.getString("countryCode"));
            assertEquals(urnSplit[1], xmlPath.getString("registrarCode"));
            assertEquals(urnSplit[2], xmlPath.getString("documentCode"));
            assertThat(xmlPath.getInt("digitalDocumentId"), greaterThanOrEqualTo(0));
            assertTrue("".equals(xmlPath.getString("reserved")));
            assertTrue(DateTime.parse(xmlPath.getString("registered")).isBeforeNow());
            assertTrue("".equals(xmlPath.getString("deactivated")));
            // check urn:nbn state
            assertEquals("ACTIVE", getUrnNbnStatus(urnNbn));
        }
    }

    @Test
    public void registrationModeByReservationForbidden() {
        String registrarCode = REGISTRAR_NO_MODES_ENABLED;
        String urnNbn = getReservedUrnNbn(registrarCode, USER);
        if (urnNbn == null) {
            // see https://github.com/NLCR/CZIDLO/issues/141
            LOGGER.warning("no reserved urn:nbn available, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String bodyXml = ddRegistrationBuilder.withUrnNbn(urnNbn);
            // LOGGER.info(bodyXml);
            String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                    .given().request().body(bodyXml).contentType(ContentType.XML)//
                    .expect()//
                    .statusCode(400)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:error", nsContext))//
                    .when().post(buildUrl(registrarCode)).andReturn().asString();
            // LOGGER.info(responseXml);
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
            Assert.assertEquals(xmlPath.getString("code"), "UNAUTHORIZED_REGISTRATION_MODE");
        }
    }

    @Test
    public void registrationModeByReservationAllowed() {
        String registrarCode = REGISTRAR;
        String urnNbn = getReservedUrnNbn(registrarCode, USER);
        if (urnNbn == null) {
            LOGGER.warning("no reserved urn:nbn available, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String bodyXml = ddRegistrationBuilder.withUrnNbn(urnNbn);
            // LOGGER.info(bodyXml);
            String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                    .given().request().body(bodyXml).contentType(ContentType.XML)//
                    .expect()//
                    .statusCode(201)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:urnNbn", nsContext))//
                    .when().post(buildUrl(registrarCode)).andReturn().asString();
            // LOGGER.info(responseXml);
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbn");
            assertEquals("ACTIVE", xmlPath.getString("status"));
            String[] urnSplit = Utils.splitUrnNbn(urnNbn);
            assertEquals(urnSplit[0], xmlPath.getString("countryCode"));
            assertEquals(urnSplit[1], xmlPath.getString("registrarCode"));
            assertEquals(urnSplit[2], xmlPath.getString("documentCode"));
            assertThat(xmlPath.getInt("digitalDocumentId"), greaterThanOrEqualTo(0));
            DateTime reserved = DateTime.parse(xmlPath.getString("reserved"));
            DateTime registered = DateTime.parse(xmlPath.getString("registered"));
            assertTrue(registered.isBeforeNow());
            assertTrue(reserved.isBefore(registered));
            assertTrue("".equals(xmlPath.getString("deactivated")));
            // check urn:nbn state
            assertEquals("ACTIVE", getUrnNbnStatus(urnNbn));
        }
    }

    // PREDECESSORS

    @Test
    public void predecessorInvalidRegistrarCode() {
        String registrarCode = REGISTRAR;
        Predecessor predecessor = new Predecessor("urn:nbn:cz:" + REGISTRAR2 + "-000000", "blabla");
        LOGGER.info(String.format("registrar code: %s, predecessor: %s", registrarCode, predecessor.urnNbn));
        String bodyXml = ddRegistrationBuilder.withPredecessors(asList(predecessor));
        // LOGGER.info(bodyXml);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(registrarCode)).andReturn().asString();
        // LOGGER.info(responseXml);
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_URN_NBN");
    }

    @Test
    public void predecessorIncorrectStateReserved() {
        String registrarCode = REGISTRAR;
        Predecessor predecessor = new Predecessor(getReservedUrnNbn(registrarCode, USER), "reserved");
        LOGGER.info(String.format("registrar code: %s, predecessor: %s", registrarCode, predecessor.urnNbn));
        String bodyXml = ddRegistrationBuilder.withPredecessors(asList(predecessor));
        // LOGGER.info(bodyXml);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(registrarCode)).andReturn().asString();
        // LOGGER.info(responseXml);
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INCORRECT_PREDECESSOR_RESERVED");
    }

    @Test
    public void predecessorIncorrectStateFree() {
        String registrarCode = REGISTRAR;
        Predecessor predecessor = new Predecessor(String.format("urn:nbn:%s:%s-010101", CountryCode.getCode(), registrarCode), "free");
        LOGGER.info(String.format("registrar code: %s, predecessor: %s", registrarCode, predecessor.urnNbn));
        String bodyXml = ddRegistrationBuilder.withPredecessors(asList(predecessor));
        // LOGGER.info(bodyXml);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(registrarCode)).andReturn().asString();
        // LOGGER.info(responseXml);
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INCORRECT_PREDECESSOR_FREE");
    }

    @Test
    public void predecessorsOk() {
        String registrarCode = REGISTRAR;
        LOGGER.info(registrarCode);
        List<Predecessor> predecessors = new ArrayList<>();
        // note content examples
        predecessors.add(new Predecessor(registerUrnNbn(REGISTRAR, USER), "predecessor1"));
        predecessors.add(new Predecessor(registerUrnNbn(REGISTRAR, USER), null));
        predecessors.add(new Predecessor(registerUrnNbn(REGISTRAR, USER), ""));
        predecessors.add(new Predecessor(registerUrnNbn(REGISTRAR, USER), "       "));
        predecessors.add(new Predecessor(registerUrnNbn(REGISTRAR, USER), "   surrounded_with_ws    "));
        predecessors.add(new Predecessor(registerUrnNbn(REGISTRAR, USER), "   starts_with_ws"));
        predecessors.add(new Predecessor(registerUrnNbn(REGISTRAR, USER), "ends_with_ws     "));
        // predecessor deactivated
        String urnNbnAlreadyDeactivated = registerUrnNbn(REGISTRAR, USER);
        deactivateUrnNbn(urnNbnAlreadyDeactivated, USER);
        predecessors.add(new Predecessor(urnNbnAlreadyDeactivated, "deactivated"));

        String bodyXml = ddRegistrationBuilder.withPredecessors(predecessors);
        // LOGGER.info(bodyXml);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(201)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbn", nsContext))//
                .when().post(buildUrl(registrarCode)).andReturn().asString();
        // LOGGER.info(responseXml);
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbn");
        assertEquals("ACTIVE", xmlPath.getString("status"));
        String urnNbn = xmlPath.getString("value");
        LOGGER.info(urnNbn);
        assertEquals(predecessors.size(), xmlPath.getInt("predecessor.size()"));
        // check predecessors in response match
        // see https://github.com/NLCR/CZIDLO/issues/142
        for (int i = 0; i < predecessors.size(); i++) {
            String urnFound = xmlPath.getString(String.format("predecessor[%d].@value", i));
            Predecessor predecessor = null;
            for (Predecessor p : predecessors) {
                if (p.urnNbn.equals(urnFound)) {
                    predecessor = p;
                    break;
                }
            }
            String noteFound = xmlPath.getString(String.format("predecessor[%d].@note", i));
            if (predecessor.note == null) {
                assertEquals("[]", noteFound);
            } else {
                assertEquals(predecessor.note, noteFound);
            }
        }

        // check data again from separate requests
        // all predecessors must be deactivated
        for (Predecessor p : predecessors) {
            assertEquals("DEACTIVATED", getUrnNbnStatus(p.urnNbn));
        }
        // check predecessors
        assertHasPredecessors(urnNbn, predecessors);
        // check sucessors (inversion)
        Map<String, List<Successor>> urnSuccessorsMap = toSuccessors(urnNbn, predecessors);
        for (String urn : urnSuccessorsMap.keySet()) {
            assertHasSuccessors(urn, urnSuccessorsMap.get(urn));
        }
    }

    // ARCHIVER

    @Test
    public void archiverInvalidDoesNotExist() {
        String registrarCode = REGISTRAR;
        long archiverId = -1;
        LOGGER.info(registrarCode + ", archiver_id: " + archiverId);
        String bodyXml = ddRegistrationBuilder.withArchiver(archiverId);
        // LOGGER.info(bodyXml);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(registrarCode)).andReturn().asString();
        // LOGGER.info(responseXml);
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_ARCHIVER_ID");
    }

    @Test
    public void archiverOk() {
        String registrarCode = REGISTRAR;
        long archiverId = ARCHIVER;
        LOGGER.info(registrarCode + ", archiver_id: " + archiverId);
        String bodyXml = ddRegistrationBuilder.withArchiver(archiverId);
        // LOGGER.info(bodyXml);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(201)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbn", nsContext))//
                .when().post(buildUrl(registrarCode)).andReturn().asString();
        // LOGGER.info(responseXml);
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbn");
        assertEquals("ACTIVE", xmlPath.getString("status"));
        String urnNbn = xmlPath.getString("value");
        LOGGER.info(urnNbn);
        String[] urnSplit = Utils.splitUrnNbn(urnNbn);
        assertEquals(urnSplit[0], xmlPath.getString("countryCode"));
        assertEquals(urnSplit[1], xmlPath.getString("registrarCode"));
        assertEquals(urnSplit[2], xmlPath.getString("documentCode"));
        assertThat(xmlPath.getInt("digitalDocumentId"), greaterThanOrEqualTo(0));
        assertTrue(DateTime.parse(xmlPath.getString("registered")).isBeforeNow());
        assertTrue("".equals(xmlPath.getString("deactivated")));

        // check archiver id
        responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildResolvationPath(urnNbn)).andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        assertEquals(archiverId, xmlPath.getInt("archiver.@id"));
    }

    @Test
    public void archiverOkIsRegistrar() {
        String registrarCode = REGISTRAR;
        long archiverId = getRegistrarId(REGISTRAR2);
        LOGGER.info(registrarCode + ", archiver_id: " + archiverId);
        String bodyXml = ddRegistrationBuilder.withArchiver(archiverId);
        // LOGGER.info(bodyXml);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(201)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbn", nsContext))//
                .when().post(buildUrl(registrarCode)).andReturn().asString();
        // LOGGER.info(responseXml);
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbn");
        assertEquals("ACTIVE", xmlPath.getString("status"));
        String urnNbn = xmlPath.getString("value");
        LOGGER.info(urnNbn);
        String[] urnSplit = Utils.splitUrnNbn(urnNbn);
        assertEquals(urnSplit[0], xmlPath.getString("countryCode"));
        assertEquals(urnSplit[1], xmlPath.getString("registrarCode"));
        assertEquals(urnSplit[2], xmlPath.getString("documentCode"));
        assertThat(xmlPath.getInt("digitalDocumentId"), greaterThanOrEqualTo(0));
        assertTrue(DateTime.parse(xmlPath.getString("registered")).isBeforeNow());
        assertTrue("".equals(xmlPath.getString("deactivated")));

        // check archiver id
        responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildResolvationPath(urnNbn)).andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        assertEquals(archiverId, xmlPath.getInt("archiver.@id"));
    }

}
