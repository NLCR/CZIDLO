package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.api.Utils;

/**
 * Tests for GET /api/v3/resolver/${URN_NBN}
 *
 */
public class ResolveByUrnNbn extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(ResolveByUrnNbn.class.getName());

    private String urnNbnActiveDiNone;
    private String urnNbnActiveDiActive;
    private String urnNbnActiveDiDeactivated;
    private String urnNbnDeactivatedDiNone;
    private String urnNbnDeactivatedDiActive;
    private String urnNbnDeactivatedDiDeactivated;
    private List<String> urnNbnList;

    @BeforeClass
    public void beforeClass() {
        init();
        Long digLibId = getDigitalLibraryIdOrNull(REGISTRAR);
        Long diId = null;
        // urn active, no di
        urnNbnActiveDiNone = registerUrnNbn(REGISTRAR, USER);
        // urn active di active
        urnNbnActiveDiActive = registerUrnNbn(REGISTRAR, USER);
        insertDigitalInstance(urnNbnActiveDiActive, digLibId, WORKING_URL, USER);
        // urn active, di deactivated
        urnNbnActiveDiDeactivated = registerUrnNbn(REGISTRAR, USER);
        diId = insertDigitalInstance(urnNbnActiveDiDeactivated, digLibId, WORKING_URL, USER);
        deactivateDigitalInstance(diId, USER);
        // urn deactivated no di
        urnNbnDeactivatedDiNone = registerUrnNbn(REGISTRAR, USER);
        deactivateUrnNbn(urnNbnDeactivatedDiNone, USER);
        // urn deactivated, di active
        urnNbnDeactivatedDiActive = registerUrnNbn(REGISTRAR, USER);
        insertDigitalInstance(urnNbnDeactivatedDiActive, digLibId, WORKING_URL, USER);
        deactivateUrnNbn(urnNbnDeactivatedDiActive, USER);
        // urn deactivated, di deactivated
        urnNbnDeactivatedDiDeactivated = registerUrnNbn(REGISTRAR, USER);
        diId = insertDigitalInstance(urnNbnDeactivatedDiDeactivated, digLibId, WORKING_URL, USER);
        deactivateDigitalInstance(diId, USER);
        deactivateUrnNbn(urnNbnDeactivatedDiDeactivated, USER);
        // list
        urnNbnList = new ArrayList<>();
        urnNbnList.add(urnNbnActiveDiNone);
        urnNbnList.add(urnNbnActiveDiActive);
        urnNbnList.add(urnNbnActiveDiDeactivated);
        urnNbnList.add(urnNbnDeactivatedDiNone);
        urnNbnList.add(urnNbnDeactivatedDiActive);
        urnNbnList.add(urnNbnDeactivatedDiDeactivated);
    }

    private String buildUrl(String urnNbn) {
        return buildResolvationPath(urnNbn);
    }

    @Test
    public void wrongUrnNbnInvalid() {
        String urnNbn = Utils.getRandomItem(URNNBN_INVALID);
        if (urnNbn == null) {
            LOGGER.warning("no invalid urn:nbn found, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                    .statusCode(400)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:error", nsContext))//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
            Assert.assertEquals(xmlPath.getString("code"), "INVALID_URN_NBN");
        }
    }

    @Test
    public void wrongUrnNbnStateFree() {
        String urnNbn = getRandomFreeUrnNbnOrNull(REGISTRAR);
        if (urnNbn == null) {
            LOGGER.warning("no free urn:nbn found, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                    .statusCode(404)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:error", nsContext))//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
            assertEquals("UNKNOWN_URN", xmlPath.getString("code"));
        }
    }

    @Test
    public void wrongUrnNbnReserved() {
        String urnNbn = getReservedUrnNbn(REGISTRAR, USER);
        if (urnNbn == null) {
            LOGGER.warning("no reserved urn:nbn found, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                    .statusCode(404)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:error", nsContext))//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
            assertEquals("UNKNOWN_DIGITAL_DOCUMENT", xmlPath.getString("code"));
        }
    }

    @Test
    public void wrongParamActionEmpty() {
        String urnNbn = urnNbnActiveDiActive;
        if (urnNbn == null) {
            LOGGER.warning("no active urn:nbn with active digital instance found, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "")//
                    .expect()//
                    .statusCode(400)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:error", nsContext))//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
            Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
        }
    }

    @Test
    public void wrongParamActionInvalidValue() {
        String urnNbn = urnNbnActiveDiActive;
        if (urnNbn == null) {
            LOGGER.warning("no active urn:nbn with active digital instance found, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "nonsense")//
                    .expect()//
                    .statusCode(400)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:error", nsContext))//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
            Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
        }
    }

    @Test
    public void wrongParamFormatEmpty() {
        String urnNbn = urnNbnActiveDiActive;
        if (urnNbn == null) {
            LOGGER.warning("no active urn:nbn with active digital instance found, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("format", "")//
                    .expect()//
                    .statusCode(400)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:error", nsContext))//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
            Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
        }
    }

    @Test
    public void wrongParamFormatInvalidValue() {
        String urnNbn = urnNbnActiveDiActive;
        if (urnNbn == null) {
            LOGGER.warning("no active urn:nbn with active digital instance found, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("format", "nonsense")//
                    .expect()//
                    .statusCode(400)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:error", nsContext))//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
            Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
        }
    }

    @Test
    public void wrongParamDigitalInstancesEmpty() {
        String urnNbn = urnNbnActiveDiActive;
        if (urnNbn == null) {
            LOGGER.warning("no active urn:nbn with active digital instance found, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("digitalInstances", "")//
                    .expect()//
                    .statusCode(400)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:error", nsContext))//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
            Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
        }
    }

    @Test
    public void wrongParamDigitalInstancesInvalidValue() {
        String urnNbn = urnNbnActiveDiActive;
        if (urnNbn == null) {
            LOGGER.warning("no active urn:nbn with active digital instance found, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("digitalInstances", "notBoolean")//
                    .expect()//
                    .statusCode(400)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:error", nsContext))//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
            Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
        }
    }

    // action=show

    /**
     * Allways redirect to czidlo web, as if format=html.
     */
    @Test
    public void actionShowFormatNotSpecified() {
        for (String urnNbn : urnNbnList) {
            LOGGER.info(urnNbn);
            String responseStr = with().config(namespaceAwareXmlConfig()).queryParam("action", "show")//
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.HTML)//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            assertThat(responseStr, containsString("<title>CZIDLO</title>"));
        }
    }

    /**
     * allways redirect to czidlo web
     */
    @Test
    public void actionShowFormatHtml() {
        for (String urnNbn : urnNbnList) {
            LOGGER.info(urnNbn);
            String responseStr = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "html")//
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.HTML)//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            assertThat(responseStr, containsString("<title>CZIDLO</title>"));
        }
    }

    /**
     * always return xml record
     */
    @Test
    public void actionShowFormatXml() {
        // urn active id active
        String urnNbn = urnNbnActiveDiActive;
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        assertEquals(1, xmlPath.getInt("digitalInstances.@count"));
        assertTrue(xmlPath.getBoolean("digitalInstances.digitalInstance[0].@active"));

        // urn active id deactivated
        urnNbn = urnNbnActiveDiDeactivated;
        LOGGER.info(urnNbn);
        responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(urnNbn)).andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        assertEquals(1, xmlPath.getInt("digitalInstances.@count"));
        assertFalse(xmlPath.getBoolean("digitalInstances.digitalInstance[0].@active"));

        // urn active id none
        urnNbn = urnNbnActiveDiNone;
        LOGGER.info(urnNbn);
        responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(urnNbn)).andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        assertEquals(0, xmlPath.getInt("digitalInstances.@count"));

        // urn deactivated di active
        urnNbn = urnNbnDeactivatedDiActive;
        LOGGER.info(urnNbn);
        responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(urnNbn)).andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        assertEquals(1, xmlPath.getInt("digitalInstances.@count"));
        assertTrue(xmlPath.getBoolean("digitalInstances.digitalInstance[0].@active"));

        // urn deactivated di deactivated
        urnNbn = urnNbnDeactivatedDiDeactivated;
        LOGGER.info(urnNbn);
        responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(urnNbn)).andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        assertEquals(1, xmlPath.getInt("digitalInstances.@count"));
        assertFalse(xmlPath.getBoolean("digitalInstances.digitalInstance[0].@active"));

        // urn deactivated di none
        urnNbn = urnNbnDeactivatedDiNone;
        LOGGER.info(urnNbn);
        responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(urnNbn)).andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        assertEquals(0, xmlPath.getInt("digitalInstances.@count"));
    }

    // action decide

    /**
     * Redirect to di url if urn:nbn is active and has active di. Otherwise redirect to czidlo web, as if format=html.
     */
    @Test
    public void actionDecideFormatUnspecified() {
        for (String urnNbn : urnNbnList) {
            LOGGER.info(urnNbn);
            String responseStr = with().config(namespaceAwareXmlConfig()).queryParam("action", "decide")//
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.HTML)//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            if (urnNbnActiveDiActive.equals(urnNbn)) {
                assertThat(responseStr, not(containsString("<title>CZIDLO</title>")));
            } else {
                assertThat(responseStr, containsString("<title>CZIDLO</title>"));
            }
        }
    }

    /**
     * Redirect to di url if urn:nbn is active and has active di. Otherwise redirect to czidlo web.
     */
    @Test
    public void actionDecideFormatHtml() {
        for (String urnNbn : urnNbnList) {
            LOGGER.info(urnNbn);
            String responseStr = with().config(namespaceAwareXmlConfig()).queryParam("action", "decide").queryParam("format", "html")//
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.HTML)//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            if (urnNbnActiveDiActive.equals(urnNbn)) {
                assertThat(responseStr, not(containsString("<title>CZIDLO</title>")));
            } else {
                assertThat(responseStr, containsString("<title>CZIDLO</title>"));
            }
        }
    }

    /**
     * Redirect to di url if urn:nbn is active and has active di. Otherwise show xml record.
     */
    @Test
    public void actionDecideFormatXml() {
        // urn active id active
        String urnNbn = urnNbnActiveDiActive;
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "decide").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.HTML)//
                .body(not(containsString("<title>CZIDLO</title>")))//
                .when().get(buildUrl(urnNbn)).andReturn().asString();

        // urn active id deactivated
        urnNbn = urnNbnActiveDiDeactivated;
        LOGGER.info(urnNbn);
        responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "decide").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        assertEquals(1, xmlPath.getInt("digitalInstances.@count"));
        assertFalse(xmlPath.getBoolean("digitalInstances.digitalInstance[0].@active"));

        // urn active id none
        urnNbn = urnNbnActiveDiNone;
        LOGGER.info(urnNbn);
        responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "decide").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(urnNbn)).andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        assertEquals(0, xmlPath.getInt("digitalInstances.@count"));

        // urn deactivated di active
        urnNbn = urnNbnDeactivatedDiActive;
        LOGGER.info(urnNbn);
        responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "decide").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(urnNbn)).andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        assertEquals(1, xmlPath.getInt("digitalInstances.@count"));
        assertTrue(xmlPath.getBoolean("digitalInstances.digitalInstance[0].@active"));

        // urn deactivated di deactivated
        urnNbn = urnNbnDeactivatedDiDeactivated;
        LOGGER.info(urnNbn);
        responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "decide").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(urnNbn)).andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        assertEquals(1, xmlPath.getInt("digitalInstances.@count"));
        assertFalse(xmlPath.getBoolean("digitalInstances.digitalInstance[0].@active"));

        // urn deactivated di none
        urnNbn = urnNbnDeactivatedDiNone;
        LOGGER.info(urnNbn);
        responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "decide").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(urnNbn)).andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        assertEquals(0, xmlPath.getInt("digitalInstances.@count"));
    }

    @Test
    public void urnNbnCaseInsensitive() {
        // lower case
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(urnNbn.toLowerCase())).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        assertEquals(urnNbn, xmlPath.getString("urnNbn.value"));

        // upper case
        urnNbn = registerUrnNbn(REGISTRAR, USER);
        LOGGER.info(urnNbn);
        responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(urnNbn.toUpperCase())).andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        assertEquals(urnNbn, xmlPath.getString("urnNbn.value"));
    }

}
