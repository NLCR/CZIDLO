package cz.nkp.urnnbn.api.v3;

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
 * Tests for GET /api/v3/urnnbn/${URN_NBN}/digitalInstances
 *
 */
public class GetDigitalInstancesByUrnNbn extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(GetDigitalInstancesByUrnNbn.class.getName());

    private Long digLibId;

    @BeforeClass
    public void beforeClass() {
        init();
        digLibId = getDigitalLibraryIdOrNull(REGISTRAR);
    }

    private String buildUrl(String urnNbn) {
        return buildResolvationPath(urnNbn) + "/digitalInstances";
    }

    @Test
    public void urnnbnInvalid() {
        String urnNbn = Utils.getRandomItem(URNNBN_INVALID);
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(urnNbn)).andReturn().asString();
        // LOGGER.info(responseXml);
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_URN_NBN");
    }

    @Test
    public void urnnbnValidFree() {
        String urnNbn = URN_NBN_FREE;
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertEquals("UNKNOWN_URN", xmlPath.getString("code"));
    }

    @Test
    public void urnnbnValidReserved() {
        String urnNbn = getReservedUrnNbn(REGISTRAR, USER);
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertEquals("UNKNOWN_DIGITAL_DOCUMENT", xmlPath.getString("code"));
    }

    @Test
    public void urnnbnValidActive() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        long diDeactivated = insertDigitalInstance(urnNbn, digLibId, WORKING_URL, USER);
        deactivateDigitalInstance(diDeactivated, USER);
        long diActive = insertDigitalInstance(urnNbn, digLibId, WORKING_URL, USER);
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalInstances", nsContext))//
                .when().get(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.digitalInstances");
        assertEquals(2, xmlPath.getInt("@count"));
        assertEquals(2, xmlPath.getInt("digitalInstance.size()"));
        // deactivated
        assertEquals(false, xmlPath.getBoolean(String.format("digitalInstance.find{it.@id=='%s'}.@active", diDeactivated)));
        DateTime deactivatedCreated = DateTime.parse(xmlPath.getString(String.format("digitalInstance.find{it.@id=='%s'}.created", diActive)));
        DateTime deactivatedDeactivated = DateTime.parse(xmlPath.getString(String.format("digitalInstance.find{it.@id=='%s'}.created", diActive)));
        assertTrue(deactivatedDeactivated.isAfter(deactivatedCreated) || deactivatedDeactivated.isEqual(deactivatedCreated));
        // active
        assertEquals(true, xmlPath.getBoolean(String.format("digitalInstance.find{it.@id=='%s'}.@active", diActive)));
        DateTime activeCreated = DateTime.parse(xmlPath.getString(String.format("digitalInstance.find{it.@id=='%s'}.created", diActive)));
        assertEquals("", xmlPath.getString(String.format("digitalInstance.find{it.@id=='%s'}.deactivated", diActive)));
        assertTrue(activeCreated.isAfter(deactivatedDeactivated) || activeCreated.isEqual(deactivatedDeactivated));
    }

    @Test
    public void urnnbnValidDeactivated() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        long diDeactivated = insertDigitalInstance(urnNbn, digLibId, WORKING_URL, USER);
        deactivateDigitalInstance(diDeactivated, USER);
        long diActive = insertDigitalInstance(urnNbn, digLibId, WORKING_URL, USER);
        deactivateUrnNbn(urnNbn, USER);
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalInstances", nsContext))//
                .when().get(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.digitalInstances");
        assertEquals(2, xmlPath.getInt("@count"));
        assertEquals(2, xmlPath.getInt("digitalInstance.size()"));
        // deactivated
        assertEquals(false, xmlPath.getBoolean(String.format("digitalInstance.find{it.@id=='%s'}.@active", diDeactivated)));
        DateTime deactivatedCreated = DateTime.parse(xmlPath.getString(String.format("digitalInstance.find{it.@id=='%s'}.created", diActive)));
        DateTime deactivatedDeactivated = DateTime.parse(xmlPath.getString(String.format("digitalInstance.find{it.@id=='%s'}.created", diActive)));
        assertTrue(deactivatedDeactivated.isAfter(deactivatedCreated) || deactivatedDeactivated.isEqual(deactivatedCreated));
        // active
        assertEquals(true, xmlPath.getBoolean(String.format("digitalInstance.find{it.@id=='%s'}.@active", diActive)));
        DateTime activeCreated = DateTime.parse(xmlPath.getString(String.format("digitalInstance.find{it.@id=='%s'}.created", diActive)));
        assertEquals("", xmlPath.getString(String.format("digitalInstance.find{it.@id=='%s'}.deactivated", diActive)));
        assertTrue(activeCreated.isAfter(deactivatedDeactivated) || activeCreated.isEqual(deactivatedDeactivated));
    }
}
