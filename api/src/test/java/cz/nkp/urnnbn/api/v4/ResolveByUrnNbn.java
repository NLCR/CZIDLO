package cz.nkp.urnnbn.api.v4;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

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
 * Tests for GET /api/v4/resolver/${URN_NBN}
 *
 */
public class ResolveByUrnNbn extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(ResolveByUrnNbn.class.getName());

    private String urnNbnInvalid;
    private String urnNbnFree;
    private String urnNbnReserved;

    private String urnNbnDeactivatedDiNone;
    private String urnNbnDeactivatedDiDeactivated;
    private String urnNbnDeactivatedDiActive;

    private String urnNbnActiveDiNone;
    private String urnNbnActiveDiDeactivated;
    private String urnNbnActiveDiActive;

    private List<String> urns;

    @BeforeClass
    public void beforeClass() {
        init();
        urnNbnInvalid = Utils.getRandomItem(URNNBN_INVALID);
        urnNbnFree = getRandomFreeUrnNbnOrNull(REGISTRAR);
        urnNbnReserved = getReservedUrnNbn(REGISTRAR, USER);

        Long digLibId = getDigitalLibraryIdOrNull(REGISTRAR);
        Long diId = null;

        // urn deactivated
        urnNbnDeactivatedDiNone = registerUrnNbn(REGISTRAR, USER);
        deactivateUrnNbn(urnNbnDeactivatedDiNone, USER);
        urnNbnDeactivatedDiDeactivated = registerUrnNbn(REGISTRAR, USER);
        diId = insertDigitalInstance(urnNbnDeactivatedDiDeactivated, digLibId, WORKING_URL, USER);
        deactivateDigitalInstance(diId, USER);
        deactivateUrnNbn(urnNbnDeactivatedDiDeactivated, USER);
        urnNbnDeactivatedDiActive = registerUrnNbn(REGISTRAR, USER);
        insertDigitalInstance(urnNbnDeactivatedDiActive, digLibId, WORKING_URL, USER);
        deactivateUrnNbn(urnNbnDeactivatedDiActive, USER);

        // urnnbn active
        urnNbnActiveDiNone = registerUrnNbn(REGISTRAR, USER);
        urnNbnActiveDiDeactivated = registerUrnNbn(REGISTRAR, USER);
        diId = insertDigitalInstance(urnNbnActiveDiDeactivated, digLibId, WORKING_URL, USER);
        deactivateDigitalInstance(diId, USER);
        urnNbnActiveDiActive = registerUrnNbn(REGISTRAR, USER);
        insertDigitalInstance(urnNbnActiveDiActive, digLibId, WORKING_URL, USER);

        urns = new ArrayList<String>();
        urns.add("urnNbnInvalid");
        urns.add("urnNbnFree");
        urns.add("urnNbnReserved");
        urns.add("urnNbnDeactivatedDiNone");
        urns.add("urnNbnDeactivatedDiDeactivated");
        urns.add("urnNbnDeactivatedDiActive");
        urns.add("urnNbnActiveDiNone");
        urns.add("urnNbnActiveDiDeactivated");
        urns.add("urnNbnActiveDiActive");
    }

    private String buildUrl(String urnNbn) {
        return buildResolvationPath(urnNbn);
    }

    // format invalid and empty

    @Test
    public void formatEmpty() {
        for (String urnNbn : urns) {
            LOGGER.info(urnNbn);
            String responseStr = with().config(namespaceAwareXmlConfig()).queryParam("format", "")//
                    .expect()//
                    .statusCode(400)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            Assert.assertEquals(XmlPath.from(responseStr).getString("response.error.code"), "ILLEGAL_FORMAT");
        }
    }

    @Test
    public void formatInvalid() {
        for (String urnNbn : urns) {
            LOGGER.info(urnNbn);
            String responseStr = with().config(namespaceAwareXmlConfig()).queryParam("format", "pdf")//
                    .expect()//
                    .statusCode(400)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            Assert.assertEquals(XmlPath.from(responseStr).getString("response.error.code"), "ILLEGAL_FORMAT");
        }
    }

    // format not specified

    @Test
    public void formatNotSpecifiedUrnbnInvalid() {
        String urnNbn = urnNbnInvalid;
        if (urnNbn == null) {
            LOGGER.warning("no urn:nbn available, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String responseStr = with().config(namespaceAwareXmlConfig())//
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.HTML)//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            assertThat(responseStr, containsString("<title>CZIDLO</title>"));
        }
    }

    @Test
    public void formatNotSpecifiedUrnbnFree() {
        String urnNbn = urnNbnFree;
        if (urnNbn == null) {
            LOGGER.warning("no urn:nbn available, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String responseStr = with().config(namespaceAwareXmlConfig())//
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.HTML)//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            assertThat(responseStr, containsString("<title>CZIDLO</title>"));
        }
    }

    @Test
    public void formatNotSpecifiedUrnbnReserved() {
        String urnNbn = urnNbnReserved;
        if (urnNbn == null) {
            LOGGER.warning("no urn:nbn available, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String responseStr = with().config(namespaceAwareXmlConfig())//
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.HTML)//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            assertThat(responseStr, containsString("<title>CZIDLO</title>"));
        }
    }

    @Test
    public void formatNotSpecifiedUrnbnDeactivatedDiNone() {
        String urnNbn = urnNbnDeactivatedDiNone;
        if (urnNbn == null) {
            LOGGER.warning("no urn:nbn available, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String responseStr = with().config(namespaceAwareXmlConfig())//
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.HTML)//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            assertThat(responseStr, containsString("<title>CZIDLO</title>"));
        }
    }

    @Test
    public void formatNotSpecifiedUrnbnDeactivatedDiDeactivated() {
        String urnNbn = urnNbnDeactivatedDiDeactivated;
        if (urnNbn == null) {
            LOGGER.warning("no urn:nbn available, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String responseStr = with().config(namespaceAwareXmlConfig())//
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.HTML)//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            assertThat(responseStr, containsString("<title>CZIDLO</title>"));
        }
    }

    @Test
    public void formatNotSpecifiedUrnbnDeactivatedDiActive() {
        String urnNbn = urnNbnDeactivatedDiActive;
        if (urnNbn == null) {
            LOGGER.warning("no urn:nbn available, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String responseStr = with().config(namespaceAwareXmlConfig())//
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.HTML)//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            assertThat(responseStr, containsString("<title>CZIDLO</title>"));
        }
    }

    // format=xml

    @Test
    public void formatXmlUrnbnInvalid() {
        String urnNbn = urnNbnInvalid;
        if (urnNbn == null) {
            LOGGER.warning("no urn:nbn available, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String responseStr = with().config(namespaceAwareXmlConfig()).queryParam("format", "xml")//
                    .expect()//
                    .statusCode(400)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            Assert.assertEquals(XmlPath.from(responseStr).getString("response.error.code"), "INVALID_URN_NBN");
        }
    }

    @Test
    public void formatXmlUrnbnFree() {
        String urnNbn = urnNbnFree;
        if (urnNbn == null) {
            LOGGER.warning("no urn:nbn available, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String responseStr = with().config(namespaceAwareXmlConfig()).queryParam("format", "xml")//
                    .expect()//
                    .statusCode(404)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            Assert.assertEquals(XmlPath.from(responseStr).getString("response.error.code"), "UNKNOWN_URN_NBN");
        }
    }

    @Test
    public void formatXmlUrnbnReserved() {
        String urnNbn = urnNbnReserved;
        if (urnNbn == null) {
            LOGGER.warning("no urn:nbn available, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String responseStr = with().config(namespaceAwareXmlConfig()).queryParam("format", "xml")//
                    .expect()//
                    .statusCode(404)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            Assert.assertEquals(XmlPath.from(responseStr).getString("response.error.code"), "UNKNOWN_DIGITAL_DOCUMENT");
        }
    }

    @Test
    public void formatXmlUrnbnDeactivatedDiNone() {
        String urnNbn = urnNbnDeactivatedDiNone;
        if (urnNbn == null) {
            LOGGER.warning("no urn:nbn available, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String responseStr = with().config(namespaceAwareXmlConfig()).queryParam("format", "xml")//
                    .expect()//
                    .statusCode(403)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            Assert.assertEquals(XmlPath.from(responseStr).getString("response.error.code"), "URN_NBN_DEACTIVATED");
        }
    }

    @Test
    public void formatXmlUrnbnDeactivatedDiDeactivated() {
        String urnNbn = urnNbnDeactivatedDiDeactivated;
        if (urnNbn == null) {
            LOGGER.warning("no urn:nbn available, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String responseStr = with().config(namespaceAwareXmlConfig()).queryParam("format", "xml")//
                    .expect()//
                    .statusCode(403)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            Assert.assertEquals(XmlPath.from(responseStr).getString("response.error.code"), "URN_NBN_DEACTIVATED");
        }
    }

    @Test
    public void formatXmlUrnbnDeactivatedDiActive() {
        String urnNbn = urnNbnDeactivatedDiActive;
        if (urnNbn == null) {
            LOGGER.warning("no urn:nbn available, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String responseStr = with().config(namespaceAwareXmlConfig()).queryParam("format", "xml")//
                    .expect()//
                    .statusCode(403)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            LOGGER.info(responseStr);
            Assert.assertEquals(XmlPath.from(responseStr).getString("response.error.code"), "URN_NBN_DEACTIVATED");
        }
    }

    @Test
    public void formatXmlUrnbnActiveDiNone() {
        String urnNbn = urnNbnActiveDiNone;
        if (urnNbn == null) {
            LOGGER.warning("no urn:nbn available, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String responseStr = with().config(namespaceAwareXmlConfig()).queryParam("format", "xml")//
                    .expect()//
                    .statusCode(200)//
                    .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseStr).setRoot("response.digitalDocument");
            assertEquals(0, xmlPath.getInt("digitalInstances.@count"));
        }
    }

    @Test
    public void formatXmlUrnbnActiveDiDeactivated() {
        String urnNbn = urnNbnActiveDiDeactivated;
        if (urnNbn == null) {
            LOGGER.warning("no urn:nbn available, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String responseStr = with().config(namespaceAwareXmlConfig()).queryParam("format", "xml")//
                    .expect()//
                    .statusCode(200)//
                    .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseStr).setRoot("response.digitalDocument");
            assertEquals(1, xmlPath.getInt("digitalInstances.@count"));
            assertEquals(false, Utils.booleanValue(xmlPath.getString("digitalInstances.digitalInstance[0].@active")));
        }
    }

    @Test
    public void formatXmlUrnbnActiveDiActive() {
        String urnNbn = urnNbnActiveDiActive;
        if (urnNbn == null) {
            LOGGER.warning("no urn:nbn available, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String responseStr = with().config(namespaceAwareXmlConfig()).queryParam("format", "xml")//
                    .expect()//
                    .statusCode(200)//
                    .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseStr).setRoot("response.digitalDocument");
            assertEquals(1, xmlPath.getInt("digitalInstances.@count"));
            assertEquals(true, Utils.booleanValue(xmlPath.getString("digitalInstances.digitalInstance[0].@active")));
        }
    }

    // format=json

    @Test
    public void formatJsonUrnbnInvalid() {
        String urnNbn = urnNbnInvalid;
        if (urnNbn == null) {
            LOGGER.warning("no urn:nbn available, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String responseStr = with().config(namespaceAwareXmlConfig()).queryParam("format", "json")//
                    .expect()//
                    .statusCode(400)//
                    .contentType(ContentType.JSON)//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            // Assert.assertEquals(XmlPath.from(responseStr).getString("response.code"), "INVALID_URN_NBN");
            // TODO: check data
        }
    }

    @Test
    public void formatJsonUrnbnFree() {
        String urnNbn = urnNbnFree;
        if (urnNbn == null) {
            LOGGER.warning("no urn:nbn available, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String response = with().config(namespaceAwareXmlConfig()).queryParam("format", "json")//
                    .expect()//
                    .statusCode(404)//
                    .contentType(ContentType.JSON)//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            assertEquals("UNKNOWN_URN_NBN", from(response).getString("error.code"));
        }
    }

    @Test
    public void formatJsonUrnbnReserved() {
        String urnNbn = urnNbnReserved;
        if (urnNbn == null) {
            LOGGER.warning("no urn:nbn available, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String response = with().config(namespaceAwareXmlConfig()).queryParam("format", "json")//
                    .expect()//
                    .statusCode(404)//
                    .contentType(ContentType.JSON)//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            assertEquals("UNKNOWN_DIGITAL_DOCUMENT", from(response).getString("error.code"));
        }
    }

    @Test
    public void formatJsonUrnbnDeactivatedDiNone() {
        String urnNbn = urnNbnDeactivatedDiNone;
        if (urnNbn == null) {
            LOGGER.warning("no urn:nbn available, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String response = with().config(namespaceAwareXmlConfig()).queryParam("format", "json")//
                    .expect()//
                    .statusCode(403)//
                    .contentType(ContentType.JSON)//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            assertEquals("URN_NBN_DEACTIVATED", from(response).getString("error.code"));
        }
    }

    @Test
    public void formatJsonUrnbnDeactivatedDiDeactivated() {
        String urnNbn = urnNbnDeactivatedDiDeactivated;
        if (urnNbn == null) {
            LOGGER.warning("no urn:nbn available, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String response = with().config(namespaceAwareXmlConfig()).queryParam("format", "json")//
                    .expect()//
                    .statusCode(403)//
                    .contentType(ContentType.JSON)//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            assertEquals("URN_NBN_DEACTIVATED", from(response).getString("error.code"));
        }
    }

    @Test
    public void formatJsonUrnbnDeactivatedDiActive() {
        String urnNbn = urnNbnDeactivatedDiActive;
        if (urnNbn == null) {
            LOGGER.warning("no urn:nbn available, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String response = with().config(namespaceAwareXmlConfig()).queryParam("format", "json")//
                    .expect()//
                    .statusCode(403)//
                    .contentType(ContentType.JSON)//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            assertEquals("URN_NBN_DEACTIVATED", from(response).getString("error.code"));
        }
    }

    @Test
    public void formatJsonUrnbnActiveDiNone() {
        String urnNbn = urnNbnActiveDiNone;
        if (urnNbn == null) {
            LOGGER.warning("no urn:nbn available, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String responseStr = with().config(namespaceAwareXmlConfig()).queryParam("format", "json")//
                    .expect()//
                    .statusCode(200)//
                    // .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                    .contentType(ContentType.JSON)//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            // XmlPath xmlPath = XmlPath.from(responseStr).setRoot("response.digitalDocument");
            // assertEquals(0, xmlPath.getInt("digitalInstances.@count"));
            // TODO: check data
        }
    }

    @Test
    public void formatJsonUrnbnActiveDiDeactivated() {
        String urnNbn = urnNbnActiveDiDeactivated;
        if (urnNbn == null) {
            LOGGER.warning("no urn:nbn available, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String responseStr = with().config(namespaceAwareXmlConfig()).queryParam("format", "json")//
                    .expect()//
                    .statusCode(200)//
                    // .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                    .contentType(ContentType.JSON)//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            // XmlPath xmlPath = XmlPath.from(responseStr).setRoot("response.digitalDocument");
            // assertEquals(1, xmlPath.getInt("digitalInstances.@count"));
            // assertEquals(false, Utils.booleanValue(xmlPath.getString("digitalInstances.digitalInstance[0].@active")));
            // TODO: check data
        }
    }

    @Test
    public void formatJsonUrnbnActiveDiActive() {
        String urnNbn = urnNbnActiveDiActive;
        if (urnNbn == null) {
            LOGGER.warning("no urn:nbn available, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String responseStr = with().config(namespaceAwareXmlConfig()).queryParam("format", "json")//
                    .expect()//
                    .statusCode(200)//
                    // .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                    .contentType(ContentType.JSON)//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            // XmlPath xmlPath = XmlPath.from(responseStr).setRoot("response.digitalDocument");
            // assertEquals(1, xmlPath.getInt("digitalInstances.@count"));
            // assertEquals(true, Utils.booleanValue(xmlPath.getString("digitalInstances.digitalInstance[0].@active")));
            // TODO: check data
        }
    }

}
