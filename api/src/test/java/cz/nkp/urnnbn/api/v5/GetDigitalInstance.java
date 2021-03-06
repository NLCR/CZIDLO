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
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.*;

/**
 * Tests for GET /api/v5/digitalInstances/id/${DIGITAL_INSTANCE_ID}
 *
 */
public class GetDigitalInstance extends ApiV5Tests {

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
            assertEquals(true, Utils.booleanValue(xmlPath.getString("@active")));
            assertFalse("".equals(xmlPath.getString("url")));
            assertTrue(DateTime.parse(xmlPath.getString("created")).isBeforeNow());
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
            assertEquals(false, Utils.booleanValue(xmlPath.getString("@active")));
            assertFalse("".equals(xmlPath.getString("url")));
            assertTrue(DateTime.parse(xmlPath.getString("created")).isBeforeNow());
        } else {
            LOGGER.warning("id not defined, ignoring");
        }
    }

    @Test
    public void okFormatXml() {
        Long id = diIdActive;
        if (id != null) {
            LOGGER.info("id: " + id);
            with().config(namespaceAwareXmlConfig()).queryParam("format", "xml") //
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:digitalInstance", nsContext))//
                    .when().get(buildUrl(id));
        } else {
            LOGGER.warning("id not defined, ignoring");
        }
    }

    @Test
    public void okFormatJson() {
        Long id = diIdActive;
        if (id != null) {
            LOGGER.info("id: " + id);
            String responseJson = with().config(namespaceAwareXmlConfig()).queryParam("format", "json") //
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.JSON)//
                    .when().get(buildUrl(id)).andReturn().asString();
            JsonPath path = from(responseJson).setRoot("digitalInstance");
            assertEquals(id.longValue(), path.getLong("id"));
            assertEquals(true, Utils.booleanValue(path.getString("active")));
            assertFalse("".equals(path.getString("url")));
            assertNotNull(path.getString("digitalLibrary"));
            assertNotNull(path.getString("digitalLibrary.registrar"));
            assertNotNull(path.getString("digitalDocument"));
        } else {
            LOGGER.warning("id not defined, ignoring");
        }
    }

    @Test
    public void okFormatNotSpecified() {
        Long id = diIdActive;
        if (id != null) {
            LOGGER.info("id: " + id);
            with().config(namespaceAwareXmlConfig()) //
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:digitalInstance", nsContext))//
                    .when().get(buildUrl(id));
        } else {
            LOGGER.warning("id not defined, ignoring");
        }
    }

    @Test
    public void okFormatEmpty() {
        Long id = diIdActive;
        if (id != null) {
            LOGGER.info("id: " + id);
            with().config(namespaceAwareXmlConfig()).queryParam("format", "") //
                    .expect()//
                    .statusCode(400)//
                    .contentType(ContentType.HTML)//
                    .when().get(buildUrl(id));//
        } else {
            LOGGER.warning("id not defined, ignoring");
        }
    }

    @Test
    public void okFormatInvalid() {
        Long id = diIdActive;
        if (id != null) {
            LOGGER.info("id: " + id);
            with().config(namespaceAwareXmlConfig()).queryParam("format", "pdf") //
                    .expect()//
                    .statusCode(400)//
                    .contentType(ContentType.HTML)//
                    .when().get(buildUrl(id));//
        } else {
            LOGGER.warning("id not defined, ignoring");
        }
    }

}
