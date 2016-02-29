package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.api.Utils;
import cz.nkp.urnnbn.api.v3.pojo.RsId;

/**
 * Tests for POST /api/v3/registrars/${REGISTRAR_CODE}/digitalDocuments/registrarScopeIdentifier/${ID_TYPE}/${ID_VALUE}/digitalInstances
 *
 */
public class PostDigitalInstancesResolvedByRsId extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(PostDigitalInstancesResolvedByRsId.class.getName());

    private Long digLibId;
    private Long registrar2_digLibId;

    @BeforeClass
    public void beforeClass() {
        init();
        digLibId = getDigitalLibraryIdOrNull(REGISTRAR);
        registrar2_digLibId = getDigitalLibraryIdOrNull(REGISTRAR2);
    }

    private String buildUrl(RsId idForResolvation) {
        return HTTPS_API_URL + buildResolvationPath(idForResolvation) + "/digitalInstances";
    }

    @Test
    public void notAuthenticated() {
        String registrarCode = REGISTRAR;
        LOGGER.info("registrar code: " + registrarCode);
        RsId idForResolvation = new RsId(registrarCode, "type", "value");
        String bodyXml = diImportBuilder.minimal(digLibId, WORKING_URL);
        // TODO:APIv4: return xml as well
        // String responseXml =
        with().config(namespaceAwareXmlConfig())//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(401)//
                // .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                // .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(idForResolvation)).andReturn().asString();
        // XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        // Assert.assertEquals(xmlPath.getString("code"), "NOT_AUTHENTICATED");
    }

    @Test
    public void notAuthorized() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        RsId rsId = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbn, rsId, USER);
        LOGGER.info(rsId.toString());
        String bodyXml = diImportBuilder.minimal(digLibId, WORKING_URL);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_NO_RIGHTS.login, USER_NO_RIGHTS.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(401)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(rsId)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "NOT_AUTHORIZED");
        // cleanup
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
    }

    @Test
    public void registrarCodeInvalid() {
        String registrarCode = Utils.getRandomItem(REGISTRAR_CODES_INVALID);
        LOGGER.info("registrar code: " + registrarCode);
        RsId idForResolvation = new RsId(registrarCode, "type", "value");
        String bodyXml = diImportBuilder.minimal(digLibId, WORKING_URL);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(idForResolvation)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_REGISTRAR_CODE");
    }

    @Test
    public void registrarCodeValidUnknown() {
        String registrarCode = Utils.getRandomItem(REGISTRAR_CODES_VALID);
        LOGGER.info("registrar code: " + registrarCode);
        RsId idForResolvation = new RsId(registrarCode, "type", "value");
        String bodyXml = diImportBuilder.minimal(digLibId, WORKING_URL);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(idForResolvation)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "UNKNOWN_REGISTRAR");
    }

    @Test
    public void rsIdTypeInvalid() {
        RsId idForResolvation = new RsId(REGISTRAR, Utils.getRandomItem(RSID_TYPES_INVALID), "value");
        LOGGER.info(idForResolvation.toString());
        String bodyXml = diImportBuilder.minimal(digLibId, WORKING_URL);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(idForResolvation)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        // TODO:APIv4: https://github.com/NLCR/CZIDLO/issues/132 (INVALID_REGISTRAR_SCOPE_ID_VALUE, code 400)
        assertThat(xmlPath.getString("code"), equalTo("INVALID_DIGITAL_DOCUMENT_ID_TYPE"));
    }

    @Test
    public void rsIdTypeValidValueInvalid() {
        RsId idForResolvation = new RsId(REGISTRAR, "type", Utils.getRandomItem(RSID_VALUES_INVALID));
        LOGGER.info(idForResolvation.toString());
        String bodyXml = diImportBuilder.minimal(digLibId, WORKING_URL);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(idForResolvation)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        // TODO:APIv4: https://github.com/NLCR/CZIDLO/issues/132 (INVALID_REGISTRAR_SCOPE_ID_VALUE, code 400)
        assertThat(xmlPath.getString("code"), equalTo("UNKNOWN_DIGITAL_DOCUMENT"));
    }

    @Test
    public void unknowDigitalDocument() {
        RsId idForResolvation = new RsId(REGISTRAR, "type", "value");
        LOGGER.info(idForResolvation.toString());
        String bodyXml = diImportBuilder.minimal(digLibId, WORKING_URL);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(buildUrl(idForResolvation)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertThat(xmlPath.getString("code"), equalTo("UNKNOWN_DIGITAL_DOCUMENT"));
    }

    @Test
    public void invalidBodyIncorrectNamespace() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        RsId rsId = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbn, rsId, USER);
        LOGGER.info(rsId.toString());
        String bodyXml = diImportBuilder.noNamespace(digLibId, WORKING_URL);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(rsId)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertEquals("INVALID_DATA", xmlPath.getString("code"));
        // cleanup
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
    }

    @Test
    public void invalidBodyInvalidDiUrl() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        RsId rsId = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbn, rsId, USER);
        LOGGER.info(rsId.toString());
        // TODO: possibly test other invalid urls
        String bodyXml = diImportBuilder.minimal(digLibId, "ftp://something.com/somewhere");
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(rsId)).andReturn().asString();
        // LOGGER.info(responseXml);
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertEquals("INVALID_DATA", xmlPath.getString("code"));
        // cleanup
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
    }

    @Test
    public void unknowDigitalLibrary() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        RsId rsId = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbn, rsId, USER);
        LOGGER.info(rsId.toString());
        String bodyXml = diImportBuilder.minimal(UNKNOWN_DIG_LIB_DI, WORKING_URL);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(rsId)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertEquals("UNKNOWN_DIGITAL_LIBRARY", xmlPath.getString("code"));
        // cleanup
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
    }

    @Test
    public void diPresent() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        RsId rsId = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbn, rsId, USER);
        LOGGER.info(rsId.toString());
        insertDigitalInstance(urnNbn, digLibId, WORKING_URL, USER);
        String bodyXml = diImportBuilder.minimal(digLibId, WORKING_URL);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(403)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(rsId)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertEquals("DIGITAL_INSTANCE_ALREADY_PRESENT", xmlPath.getString("code"));
        // cleanup
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
    }

    @Test
    public void diNotPresent() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        RsId rsId = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbn, rsId, USER);
        LOGGER.info(rsId.toString());
        String bodyXml = diImportBuilder.minimal(digLibId, WORKING_URL);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(201)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalInstance", nsContext))//
                .when().post(buildUrl(rsId)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.digitalInstance");
        assertTrue(xmlPath.getBoolean("@active"));
        assertTrue(DateTime.parse(xmlPath.getString("created")).isBeforeNow());
        assertEquals(digLibId.longValue(), xmlPath.getLong("digitalLibraryId"));
        // cleanup
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
    }

    @Test
    public void diPresentDeactivated() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        RsId rsId = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbn, rsId, USER);
        LOGGER.info(rsId.toString());
        long diDeactivated = insertDigitalInstance(urnNbn, digLibId, WORKING_URL, USER);
        deactivateDigitalInstance(diDeactivated, USER);
        String bodyXml = diImportBuilder.minimal(digLibId, WORKING_URL);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(201)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalInstance", nsContext))//
                .when().post(buildUrl(rsId)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.digitalInstance");
        assertTrue(xmlPath.getBoolean("@active"));
        assertTrue(DateTime.parse(xmlPath.getString("created")).isBeforeNow());
        assertEquals(digLibId.longValue(), xmlPath.getLong("digitalLibraryId"));
        // cleanup
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
    }

    @Test
    public void twoDigitalInstances() {
        if (digLibId == null || registrar2_digLibId == null) {
            LOGGER.warning("digital library not available, ignoring");
        } else {
            String urnNbn = registerUrnNbn(REGISTRAR, USER);
            RsId rsId = new RsId(REGISTRAR, "type", "value");
            insertRegistrarScopeId(urnNbn, rsId, USER);
            LOGGER.info(rsId.toString());
            insertDigitalInstance(urnNbn, registrar2_digLibId, WORKING_URL, USER);
            String bodyXml = diImportBuilder.minimal(digLibId, WORKING_URL);
            String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                    .given().request().body(bodyXml).contentType(ContentType.XML)//
                    .expect()//
                    .statusCode(201)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:digitalInstance", nsContext))//
                    .when().post(buildUrl(rsId)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.digitalInstance");
            assertTrue(xmlPath.getBoolean("@active"));
            assertTrue(DateTime.parse(xmlPath.getString("created")).isBeforeNow());
            assertEquals(digLibId.longValue(), xmlPath.getLong("digitalLibraryId"));
            // cleanup
            deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
        }
    }

}
