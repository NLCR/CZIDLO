package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.api.Utils;

/**
 * Tests for GET /api/v3/urnnbn/${URN_NBN}
 *
 */

public class GetUrnNbn extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(GetUrnNbn.class.getName());

    private String urnNbnActive;
    private String urnNbnDeactivated;
    private String urnNbnReserved;
    private String urnNbnFree;

    @BeforeClass
    public void beforeClass() {
        init();
        urnNbnActive = registerUrnNbn(REGISTRAR, USER);
        urnNbnDeactivated = registerUrnNbn(REGISTRAR, USER);
        deactivateUrnNbn(urnNbnDeactivated, USER);
        UrnNbnReservations urnNbnReservations = getUrnNbnReservations(REGISTRAR);
        if (!urnNbnReservations.reservedOffered.isEmpty()) {
            urnNbnReserved = urnNbnReservations.reservedOffered.get(0);
        } else {
            urnNbnReserved = reserveUrnNbns(REGISTRAR, USER).get(0);
        }
        urnNbnFree = URN_NBN_FREE;
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
    public void urnnbnValidActive() {
        String urnNbn = urnNbnActive;
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
        assertFalse("".equals(xmlPath.getString("registered")));
    }

    @Test
    public void urnnbnValidDeactivated() {
        String urnNbn = urnNbnDeactivated;
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
        assertFalse("".equals(xmlPath.getString("registered")));
        assertFalse("".equals(xmlPath.getString("deactivated")));
    }

    @Test
    public void urnnbnValidFree() {
        String urnNbn = urnNbnFree;
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
    }

    @Test
    public void urnnbnValidReserved() {
        String urnNbn = urnNbnReserved;
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
        assertFalse("".equals(xmlPath.getString("reserved")));
    }

}
