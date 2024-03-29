package cz.nkp.urnnbn.api.v5;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.path.xml.XmlPath;
import cz.nkp.urnnbn.api.Utils;
import cz.nkp.urnnbn.api.pojo.RsId;
import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.logging.Logger;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.*;

/**
 * Tests for GET /api/v5/registrars/${REGISTRAR_CODE}/digitalDocuments/registrarScopeIdentifier/${ID_TYPE}/${ID_VALUE}/digitalInstances
 *
 */
public class GetDigitalInstancesResolvedByRsId extends ApiV5Tests {

    private static final Logger LOGGER = Logger.getLogger(GetDigitalInstancesResolvedByRsId.class.getName());

    private Long digLibId;

    @BeforeClass
    public void beforeClass() {
        init();
        digLibId = getDigitalLibraryIdOrNull(REGISTRAR);
    }

    private String buildUrl(RsId idForResolvation) {
        return buildResolvationPath(idForResolvation) + "/digitalInstances";
    }

    @Test
    public void registrarCodeInvalid() {
        String registrarCode = Utils.getRandomItem(REGISTRAR_CODES_INVALID);
        LOGGER.info("registrar code: " + registrarCode);
        RsId idForResolvation = new RsId(registrarCode, "type", "value");
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(idForResolvation)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_REGISTRAR_CODE");
    }

    @Test
    public void registrarCodeValidUnknown() {
        String registrarCode = Utils.getRandomItem(REGISTRAR_CODES_VALID);
        LOGGER.info("registrar code: " + registrarCode);
        RsId idForResolvation = new RsId(registrarCode, "type", "value");
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(idForResolvation)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "UNKNOWN_REGISTRAR");
    }

    @Test
    public void rsIdTypeInvalid() {
        RsId idForResolvation = new RsId(REGISTRAR, Utils.getRandomItem(RSID_TYPES_INVALID), "value");
        LOGGER.info(idForResolvation.toString());
        String responseXml = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(idForResolvation)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertThat(xmlPath.getString("code"), equalTo("INVALID_REGISTRAR_SCOPE_ID_TYPE"));
    }

    @Test
    public void rsIdTypeValidValueInvalid() {
        RsId idForResolvation = new RsId(REGISTRAR, "type", Utils.getRandomItem(RSID_VALUES_INVALID));
        LOGGER.info(idForResolvation.toString());
        String responseXml = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(idForResolvation)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertThat(xmlPath.getString("code"), equalTo("INVALID_REGISTRAR_SCOPE_ID_VALUE"));
    }

    @Test
    public void unknowDigitalDocument() {
        RsId idForResolvation = new RsId(REGISTRAR, "type", "value");
        LOGGER.info(idForResolvation.toString());
        String responseXml = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(idForResolvation)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertThat(xmlPath.getString("code"), equalTo("UNKNOWN_DIGITAL_DOCUMENT"));
    }

    @Test
    public void active() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        long diDeactivated = insertDigitalInstance(urnNbn, digLibId, WORKING_URL, USER);
        RsId rsId = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbn, rsId, USER);
        deactivateDigitalInstance(diDeactivated, USER);
        long diActive = insertDigitalInstance(urnNbn, digLibId, WORKING_URL, USER);
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalInstances", nsContext))//
                .when().get(buildUrl(rsId)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.digitalInstances");
        assertEquals(2, xmlPath.getInt("@count"));
        assertEquals(2, xmlPath.getInt("digitalInstance.size()"));
        // deactivated
        assertEquals(false, Utils.booleanValue(xmlPath.getString(String.format("digitalInstance.find{it.@id=='%s'}.@active", diDeactivated))));
        DateTime deactivatedCreated = DateTime.parse(xmlPath.getString(String.format("digitalInstance.find{it.@id=='%s'}.created", diDeactivated)));
        DateTime deactivatedDeactivated = DateTime.parse(xmlPath.getString(String.format("digitalInstance.find{it.@id=='%s'}.deactivated",
                diDeactivated)));
        assertTrue(deactivatedCreated.isBefore(deactivatedDeactivated));
        // active
        assertEquals(true, Utils.booleanValue(xmlPath.getString(String.format("digitalInstance.find{it.@id=='%s'}.@active", diActive))));
        DateTime activeCreated = DateTime.parse(xmlPath.getString(String.format("digitalInstance.find{it.@id=='%s'}.created", diActive)));
        assertEquals("", xmlPath.getString(String.format("digitalInstance.find{it.@id=='%s'}.deactivated", diActive)));
        assertTrue(activeCreated.isAfter(deactivatedDeactivated));
        // cleanup
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
    }

    @Test
    public void deactivated() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        long diDeactivated = insertDigitalInstance(urnNbn, digLibId, WORKING_URL, USER);
        RsId rsId = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbn, rsId, USER);
        deactivateDigitalInstance(diDeactivated, USER);
        long diActive = insertDigitalInstance(urnNbn, digLibId, WORKING_URL, USER);
        deactivateUrnNbn(urnNbn, USER);
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalInstances", nsContext))//
                .when().get(buildUrl(rsId)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.digitalInstances");
        assertEquals(2, xmlPath.getInt("@count"));
        assertEquals(2, xmlPath.getInt("digitalInstance.size()"));
        // deactivated
        assertEquals(false, Utils.booleanValue(xmlPath.getString(String.format("digitalInstance.find{it.@id=='%s'}.@active", diDeactivated))));
        DateTime deactivatedCreated = DateTime.parse(xmlPath.getString(String.format("digitalInstance.find{it.@id=='%s'}.created", diDeactivated)));
        DateTime deactivatedDeactivated = DateTime.parse(xmlPath.getString(String.format("digitalInstance.find{it.@id=='%s'}.deactivated",
                diDeactivated)));
        assertTrue(deactivatedCreated.isBefore(deactivatedDeactivated));
        // active
        assertEquals(true, Utils.booleanValue(xmlPath.getString(String.format("digitalInstance.find{it.@id=='%s'}.@active", diActive))));
        DateTime activeCreated = DateTime.parse(xmlPath.getString(String.format("digitalInstance.find{it.@id=='%s'}.created", diActive)));
        assertEquals("", xmlPath.getString(String.format("digitalInstance.find{it.@id=='%s'}.deactivated", diActive)));
        assertTrue(activeCreated.isAfter(deactivatedDeactivated));
        // cleanup
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
    }

    @Test
    public void formatXml() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        RsId rsId = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbn, rsId, USER);
        LOGGER.info(urnNbn);
        with().config(namespaceAwareXmlConfig()).queryParam("format", "xml") //
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalInstances", nsContext))//
                .when().get(buildUrl(rsId));
        // cleanup
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
    }

    @Test
    public void formatNotSpecified() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        RsId rsId = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbn, rsId, USER);
        LOGGER.info(urnNbn);
        with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalInstances", nsContext))//
                .when().get(buildUrl(rsId));
        // cleanup
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
    }

    @Test
    public void formatEmpty() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        RsId rsId = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbn, rsId, USER);
        LOGGER.info(urnNbn);
        with().config(namespaceAwareXmlConfig()).queryParam("format", "") //
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.HTML).when().get(buildUrl(rsId));
        // cleanup
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
    }

    @Test
    public void formatInvalid() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        RsId rsId = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbn, rsId, USER);
        LOGGER.info(urnNbn);
        with().config(namespaceAwareXmlConfig()).queryParam("format", "pdf") //
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.HTML).when().get(buildUrl(rsId));
        // cleanup
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
    }

    @Test
    public void formatJson() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        long diDeactivated = insertDigitalInstance(urnNbn, digLibId, WORKING_URL, USER);
        RsId rsId = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbn, rsId, USER);
        deactivateDigitalInstance(diDeactivated, USER);
        long diActive = insertDigitalInstance(urnNbn, digLibId, WORKING_URL, USER);
        LOGGER.info(urnNbn);
        String responseJson = with().config(namespaceAwareXmlConfig()).queryParam("format", "json") //
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.JSON)//
                .when().get(buildUrl(rsId)).andReturn().asString();

        JsonPath path = from(responseJson).setRoot("digitalInstances");
        assertEquals(2, path.getInt("size()"));
        // TODO: check rest of data
        // // deactivated
        // assertEquals(false,
        // Utils.booleanValue(xmlPath.getString(String.format("digitalInstances.find{digitalInstances->digitalInstances.id==%s}.active",
        // diDeactivated))));
        // DateTime deactivatedCreated = DateTime.parse(xmlPath.getString(String.format("digitalInstance.find{it.@id=='%s'}.created",
        // diDeactivated)));
        // DateTime deactivatedDeactivated = DateTime.parse(xmlPath.getString(String.format("digitalInstance.find{it.@id=='%s'}.deactivated",
        // diDeactivated)));
        // assertTrue(deactivatedCreated.isBefore(deactivatedDeactivated));
        // // active
        // assertEquals(true, Utils.booleanValue(xmlPath.getString(String.format("digitalInstance.find{it.@id=='%s'}.@active", diActive))));
        // DateTime activeCreated = DateTime.parse(xmlPath.getString(String.format("digitalInstance.find{it.@id=='%s'}.created", diActive)));
        // assertEquals("", xmlPath.getString(String.format("digitalInstance.find{it.@id=='%s'}.deactivated", diActive)));
        // assertTrue(activeCreated.isAfter(deactivatedDeactivated));
        // cleanup
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
    }
}
