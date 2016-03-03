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
 * Tests for GET /api/v3/registrars/${REGISTRAR_CODE}/digitalDocuments/registrarScopeIdentifier/${ID_TYPE}/${ID_VALUE}/digitalInstances
 *
 */
public class GetDigitalInstancesResolvedByRsId extends ApiV3Tests {

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
        // TODO:APIv4: rename error to INVALID_REGISTRAR_SCOPE_ID_TYPE
        assertThat(xmlPath.getString("code"), equalTo("INVALID_DIGITAL_DOCUMENT_ID_TYPE"));
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
        // TODO:APIv4: rename to INVALID_REGISTRAR_SCOPE_ID_VALUE
        assertThat(xmlPath.getString("code"), equalTo("INVALID_DIGITAL_DOCUMENT_ID_VALUE"));
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
}
