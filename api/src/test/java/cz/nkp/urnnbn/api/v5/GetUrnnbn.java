package cz.nkp.urnnbn.api.v5;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.path.xml.XmlPath;
import cz.nkp.urnnbn.api.Utils;
import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.logging.Logger;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.*;

/**
 * Tests for GET /api/v5/urnnbn/${URN_NBN}
 *
 */
public class GetUrnnbn extends ApiV5Tests {

    private static final Logger LOGGER = Logger.getLogger(GetUrnnbn.class.getName());

    @BeforeClass
    public void beforeClass() {
        init();
    }

    private String buildUrl(String urnNbn) {
        return "/urnnbn/" + Utils.urlEncodeReservedChars(urnNbn);
    }

    @Test
    public void urnnbnInvalidAll() {
        for (String urnNbn : URNNBN_INVALID) {
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
    public void urnnbnValidAll() {
        for (String urnNbn : URNNBN_VALID) {
            LOGGER.info(urnNbn);
            String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                    .statusCode(200)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:urnNbn", nsContext))//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbn");
            assertEquals(urnNbn.toLowerCase(), xmlPath.getString("value").toLowerCase());
            String[] urnSplit = Utils.splitUrnNbn(urnNbn);
            assertEquals(urnSplit[0], xmlPath.getString("countryCode").toLowerCase());
            assertEquals(urnSplit[1], xmlPath.getString("registrarCode").toLowerCase());
            assertEquals(urnSplit[2], xmlPath.getString("documentCode").toLowerCase());
        }
    }

    @Test
    public void urnnbnValidFree() {
        String urnNbn = getRandomFreeUrnNbnOrNull(REGISTRAR);
        if (urnNbn == null) {
            LOGGER.warning("no free urn:nbn found, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                    .statusCode(200)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:urnNbn", nsContext))//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbn");
            assertEquals(urnNbn.toLowerCase(), xmlPath.getString("value").toLowerCase());
            assertEquals("FREE", xmlPath.getString("status"));
            String[] urnSplit = Utils.splitUrnNbn(urnNbn);
            assertEquals(urnSplit[0], xmlPath.getString("countryCode").toLowerCase());
            assertEquals(urnSplit[1], xmlPath.getString("registrarCode").toLowerCase());
            assertEquals(urnSplit[2], xmlPath.getString("documentCode").toLowerCase());
            assertTrue("".equals(xmlPath.getString("digitalDocumentId")));
            assertTrue("".equals(xmlPath.getString("registered")));
            assertTrue("".equals(xmlPath.getString("reserved")));
            assertTrue("".equals(xmlPath.getString("deactivated")));
        }
    }

    @Test
    public void urnnbnValidReserved() {
        String urnNbn = getReservedUrnNbn(REGISTRAR, USER);
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbn", nsContext))//
                .when().get(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbn");
        assertEquals(urnNbn.toLowerCase(), xmlPath.getString("value").toLowerCase());
        assertEquals("RESERVED", xmlPath.getString("status"));
        String[] urnSplit = Utils.splitUrnNbn(urnNbn);
        assertEquals(urnSplit[0], xmlPath.getString("countryCode").toLowerCase());
        assertEquals(urnSplit[1], xmlPath.getString("registrarCode").toLowerCase());
        assertEquals(urnSplit[2], xmlPath.getString("documentCode").toLowerCase());
        assertTrue("".equals(xmlPath.getString("digitalDocumentId")));
        assertTrue("".equals(xmlPath.getString("registered")));
        assertTrue(DateTime.parse(xmlPath.getString("reserved")).isBeforeNow());
        assertTrue("".equals(xmlPath.getString("deactivated")));
    }

    @Test
    public void urnnbnValidActiveWasNotReserved() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbn", nsContext))//
                .when().get(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbn");
        assertEquals(urnNbn.toLowerCase(), xmlPath.getString("value").toLowerCase());
        assertEquals("ACTIVE", xmlPath.getString("status"));
        String[] urnSplit = Utils.splitUrnNbn(urnNbn);
        assertEquals(urnSplit[0], xmlPath.getString("countryCode").toLowerCase());
        assertEquals(urnSplit[1], xmlPath.getString("registrarCode").toLowerCase());
        assertEquals(urnSplit[2], xmlPath.getString("documentCode").toLowerCase());
        assertThat(xmlPath.getInt("digitalDocumentId"), greaterThanOrEqualTo(0));
        assertTrue("".equals(xmlPath.getString("reserved")));
        assertTrue(DateTime.parse(xmlPath.getString("registered")).isBeforeNow());
        assertTrue("".equals(xmlPath.getString("deactivated")));
    }

    @Test
    public void urnnbnValidActiveWasReserved() {
        String urnNbn = getReservedUrnNbn(REGISTRAR, USER);
        registerUrnNbnByRegistrar(REGISTRAR, urnNbn, USER);
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbn", nsContext))//
                .when().get(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbn");
        assertEquals(urnNbn.toLowerCase(), xmlPath.getString("value").toLowerCase());
        assertEquals("ACTIVE", xmlPath.getString("status"));
        String[] urnSplit = Utils.splitUrnNbn(urnNbn);
        assertEquals(urnSplit[0], xmlPath.getString("countryCode").toLowerCase());
        assertEquals(urnSplit[1], xmlPath.getString("registrarCode").toLowerCase());
        assertEquals(urnSplit[2], xmlPath.getString("documentCode").toLowerCase());
        assertThat(xmlPath.getInt("digitalDocumentId"), greaterThanOrEqualTo(0));
        DateTime reserved = DateTime.parse(xmlPath.getString("reserved"));
        DateTime registered = DateTime.parse(xmlPath.getString("registered"));
        assertTrue("".equals(xmlPath.getString("deactivated")));
        assertTrue(registered.isBeforeNow());
        assertTrue(reserved.isBefore(registered));
    }

    @Test
    public void urnnbnValidDeactivatedWasNotReserved() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        deactivateUrnNbn(urnNbn, USER);
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbn", nsContext))//
                .when().get(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbn");
        assertEquals(urnNbn.toLowerCase(), xmlPath.getString("value").toLowerCase());
        assertEquals("DEACTIVATED", xmlPath.getString("status"));
        String[] urnSplit = Utils.splitUrnNbn(urnNbn);
        assertEquals(urnSplit[0], xmlPath.getString("countryCode").toLowerCase());
        assertEquals(urnSplit[1], xmlPath.getString("registrarCode").toLowerCase());
        assertEquals(urnSplit[2], xmlPath.getString("documentCode").toLowerCase());
        assertThat(xmlPath.getInt("digitalDocumentId"), greaterThanOrEqualTo(0));
        assertTrue("".equals(xmlPath.getString("reserved")));
        DateTime registered = DateTime.parse(xmlPath.getString("registered"));
        DateTime deactivated = DateTime.parse(xmlPath.getString("deactivated"));
        assertTrue(deactivated.isBeforeNow());
        assertTrue(registered.isBefore(deactivated));
    }

    @Test
    public void urnnbnValidDeactivatedWasReserved() {
        String urnNbn = getReservedUrnNbn(REGISTRAR, USER);
        registerUrnNbnByRegistrar(REGISTRAR, urnNbn, USER);
        deactivateUrnNbn(urnNbn, USER);
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbn", nsContext))//
                .when().get(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbn");
        assertEquals(urnNbn.toLowerCase(), xmlPath.getString("value").toLowerCase());
        assertEquals("DEACTIVATED", xmlPath.getString("status"));
        String[] urnSplit = Utils.splitUrnNbn(urnNbn);
        assertEquals(urnSplit[0], xmlPath.getString("countryCode").toLowerCase());
        assertEquals(urnSplit[1], xmlPath.getString("registrarCode").toLowerCase());
        assertEquals(urnSplit[2], xmlPath.getString("documentCode").toLowerCase());
        assertThat(xmlPath.getInt("digitalDocumentId"), greaterThanOrEqualTo(0));
        DateTime reserved = DateTime.parse(xmlPath.getString("reserved"));
        DateTime registered = DateTime.parse(xmlPath.getString("registered"));
        DateTime deactivated = DateTime.parse(xmlPath.getString("deactivated"));
        assertTrue(deactivated.isBeforeNow());
        assertTrue(registered.isBefore(deactivated));
        assertTrue(reserved.isBefore(registered));
    }

    @Test
    public void urnnbnCaseInsensitive() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbn", nsContext))//
                .when().get(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbn");
        long ddId = xmlPath.getLong("digitalDocumentId");
        // upper case
        responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbn", nsContext))//
                .when().get(buildUrl(urnNbn.toUpperCase())).andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbn");
        long ddIdUpperCase = xmlPath.getLong("digitalDocumentId");
        assertEquals(ddId, ddIdUpperCase);
        // lower case
        responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbn", nsContext))//
                .when().get(buildUrl(urnNbn.toLowerCase())).andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbn");
        long ddIdLowerCase = xmlPath.getLong("digitalDocumentId");
        assertEquals(ddId, ddIdLowerCase);
    }

    @Test
    public void formatXml() {
        String urnNbn = Utils.getRandomItem(URNNBN_VALID);
        LOGGER.info(urnNbn);
        with().config(namespaceAwareXmlConfig()).queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbn", nsContext))//
                .when().get(buildUrl(urnNbn));
    }

    @Test
    public void formatNotSpecified() {
        String urnNbn = Utils.getRandomItem(URNNBN_VALID);
        LOGGER.info(urnNbn);
        with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbn", nsContext))//
                .when().get(buildUrl(urnNbn));
    }

    @Test
    public void formatEmpty() {
        String urnNbn = Utils.getRandomItem(URNNBN_VALID);
        LOGGER.info(urnNbn);
        with().config(namespaceAwareXmlConfig()).queryParam("format", "")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.HTML)//
                .when().get(buildUrl(urnNbn));
    }

    @Test
    public void formatInvalid() {
        String urnNbn = Utils.getRandomItem(URNNBN_VALID);
        LOGGER.info(urnNbn);
        with().config(namespaceAwareXmlConfig()).queryParam("format", "pdf")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.HTML)//
                .when().get(buildUrl(urnNbn));
    }

    @Test
    public void formatJson() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        LOGGER.info(urnNbn);
        String responseJson = with().config(namespaceAwareXmlConfig()).queryParam("format", "json")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.JSON)//
                .when().get(buildUrl(urnNbn)).andReturn().asString();
        JsonPath path = from(responseJson).setRoot("urnNbn");
        assertEquals(urnNbn, path.getString("value"));
        assertEquals("ACTIVE", path.getString("status"));
        assertTrue(DateTime.parse(path.getString("registered")).isBeforeNow());
    }
}
