package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.api.Utils;

/**
 * Tests for GET /api/v3/digitalInstances/id/${DIGITAL_INSTANCE_ID}
 *
 */
public class GetDigitalInstance extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(GetDigitalInstance.class.getName());

    private String urnNbn;
    private Long diIdActive;
    private Long diIdDeactivated;
    private Long diUnknown;

    @BeforeClass
    public void beforeClass() {
        init();
        urnNbn = registerUrnNbn(REGISTRAR, USER);
        Long digLibId = getDigitalLibraryIdOrNull(REGISTRAR);
        if (digLibId == null) {
            LOGGER.warning("no digital instance found");
        } else {
            diIdDeactivated = insertDigitalInstance(urnNbn, digLibId, "http://something.com/somewhere", USER);
            deactivateDigitalInstance(diIdDeactivated, USER);
            diIdActive = insertDigitalInstance(urnNbn, digLibId, "http://something.com/somewhere", USER);
        }
        diUnknown = getRandomFreeDigitalInstanceIdOrNull();
    }

    private String buildUrl(String id) {
        return "/digitalInstances/id/" + Utils.urlEncodeReservedChars(id);
    }

    private String buildUrl(long id) {
        return buildUrl(String.valueOf(id));
    }

    @Test
    public void idNotNumber() {
        String id = "abc";
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_DIGITAL_INSTANCE_ID");
    }

    @Test
    public void idUnknown() {
        Long id = diUnknown;
        if (id == null) {
            LOGGER.warning("didn't find any free digital instance, ignoring");
        } else {
            LOGGER.info("id: " + id);
            String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                    .statusCode(404)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:error", nsContext))//
                    .when().get(buildUrl(id)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
            Assert.assertEquals(xmlPath.getString("code"), "UNKNOWN_DIGITAL_INSTANCE");
        }
    }

    @Test
    public void okActive() {
        Long id = diIdActive;
        if (id != null) {
            LOGGER.info("id: " + id);
            String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                    .statusCode(200)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:digitalInstance", nsContext))//
                    .body(hasXPath("/c:response/c:digitalInstance/c:digitalLibrary", nsContext))//
                    .body(hasXPath("/c:response/c:digitalInstance/c:digitalLibrary/c:registrar", nsContext))//
                    .body(hasXPath("/c:response/c:digitalInstance/c:digitalDocument", nsContext))//
                    .when().get(buildUrl(id)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.digitalInstance");
            assertEquals(id.longValue(), xmlPath.getLong("@id"));
            assertTrue(xmlPath.getBoolean("@active"));
            assertFalse("".equals(xmlPath.getString("url")));
            assertFalse("".equals(xmlPath.getString("created")));
        } else {
            LOGGER.warning("id not defined, ignoring");
        }
    }

    @Test
    public void okDeactivated() {
        Long id = diIdDeactivated;
        if (id != null) {
            LOGGER.info("id: " + id);
            String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                    .statusCode(200)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:digitalInstance", nsContext))//
                    .body(hasXPath("/c:response/c:digitalInstance/c:digitalLibrary", nsContext))//
                    .body(hasXPath("/c:response/c:digitalInstance/c:digitalLibrary/c:registrar", nsContext))//
                    .body(hasXPath("/c:response/c:digitalInstance/c:digitalDocument", nsContext))//
                    .when().get(buildUrl(id)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.digitalInstance");
            assertEquals(id.longValue(), xmlPath.getLong("@id"));
            assertFalse(xmlPath.getBoolean("@active"));
            assertFalse("".equals(xmlPath.getString("url")));
            assertFalse("".equals(xmlPath.getString("created")));
        } else {
            LOGGER.warning("id not defined, ignoring");
        }
    }

}
