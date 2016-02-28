package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.api.Utils;

/**
 * Tests for DELETE /api/v3/urnnbn/${URN_NBN}
 *
 */
public class DeleteUrnnbn extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(DeleteUrnnbn.class.getName());

    @BeforeClass
    public void beforeClass() {
        init();
    }

    private String buildUrl(String urnNbn) {
        return HTTPS_API_URL + "/urnnbn/" + Utils.urlEncodeReservedChars(urnNbn);
    }

    @Test
    public void notAuthenticated() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        LOGGER.info(urnNbn);
        // TODO:APIv4: return xml as well
        // String responseXml =
        with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(401)//
                // .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                // .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(buildUrl(urnNbn)).andReturn().asString();
        // XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        // Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHENTICATED");
        // check state hasn't changed
        assertEquals("ACTIVE", getUrnNbnState(urnNbn));
    }

    @Test
    public void notAuthorized() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_NO_RIGHTS.login, USER_NO_RIGHTS.password)//
                .expect()//
                .statusCode(401)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHORIZED");
        // check state hasn't changed
        assertEquals("ACTIVE", getUrnNbnState(urnNbn));
    }

    @Test
    public void invalidUrnNbn() {
        String urnNbn = Utils.getRandomItem(URNNBN_INVALID);
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_URN_NBN");
    }

    @Test
    public void errorStateDeactivated() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        deactivateUrnNbn(urnNbn, USER);
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(403)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INCORRECT_URN_NBN_STATE");
        // check state hasn't changed
        assertEquals("DEACTIVATED", getUrnNbnState(urnNbn));
    }

    @Test
    public void errorStateFree() {
        String urnNbn = URN_NBN_FREE;
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(403)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INCORRECT_URN_NBN_STATE");
        // check state hasn't changed
        assertEquals("FREE", getUrnNbnState(urnNbn));
    }

    @Test
    public void errorStateReserved() {
        String urnNbn = getReservedUrnNbn(REGISTRAR, USER);
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(403)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INCORRECT_URN_NBN_STATE");
        // check state hasn't changed
        assertEquals("RESERVED", getUrnNbnState(urnNbn));
    }

    @Test
    public void ok() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        DateTime registeredDt = getUrnNbnRegisteredOrNull(urnNbn);
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbn", nsContext))//
                .when().delete(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbn");
        assertEquals("DEACTIVATED", xmlPath.getString("status"));
        assertEquals(urnNbn.toLowerCase(), xmlPath.getString("value"));
        String[] splitUrnNbn = Utils.splitUrnNbn(urnNbn);
        assertEquals(splitUrnNbn[0], xmlPath.getString("countryCode"));
        assertEquals(splitUrnNbn[1], xmlPath.getString("registrarCode"));
        assertEquals(splitUrnNbn[2], xmlPath.getString("documentCode"));
        assertThat(xmlPath.getInt("digitalDocumentId"), greaterThanOrEqualTo(0));
        assertEquals(registeredDt, DateTime.parse(xmlPath.getString("registered")));
        DateTime.parse(xmlPath.getString("deactivated"));
        // check state has changed
        assertEquals("DEACTIVATED", getUrnNbnState(urnNbn));
    }

    @Test
    public void okCaseInsensitive() {
        // lower case
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        DateTime registeredDt = getUrnNbnRegisteredOrNull(urnNbn);
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbn", nsContext))//
                .when().delete(buildUrl(urnNbn.toLowerCase())).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbn");
        assertEquals("DEACTIVATED", xmlPath.getString("status"));
        assertEquals(urnNbn.toLowerCase(), xmlPath.getString("value"));
        String[] splitUrnNbn = Utils.splitUrnNbn(urnNbn);
        assertEquals(splitUrnNbn[0], xmlPath.getString("countryCode"));
        assertEquals(splitUrnNbn[1], xmlPath.getString("registrarCode"));
        assertEquals(splitUrnNbn[2], xmlPath.getString("documentCode"));
        assertThat(xmlPath.getInt("digitalDocumentId"), greaterThanOrEqualTo(0));
        assertEquals(registeredDt, DateTime.parse(xmlPath.getString("registered")));
        DateTime.parse(xmlPath.getString("deactivated"));
        // check state has changed
        assertEquals("DEACTIVATED", getUrnNbnState(urnNbn));

        // upper case
        urnNbn = registerUrnNbn(REGISTRAR, USER);
        registeredDt = getUrnNbnRegisteredOrNull(urnNbn);
        LOGGER.info(urnNbn);
        // lower case
        responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbn", nsContext))//
                .when().delete(buildUrl(urnNbn.toUpperCase())).andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbn");
        assertEquals("DEACTIVATED", xmlPath.getString("status"));
        assertEquals(urnNbn.toLowerCase(), xmlPath.getString("value"));
        splitUrnNbn = Utils.splitUrnNbn(urnNbn);
        assertEquals(splitUrnNbn[0], xmlPath.getString("countryCode"));
        assertEquals(splitUrnNbn[1], xmlPath.getString("registrarCode"));
        assertEquals(splitUrnNbn[2], xmlPath.getString("documentCode"));
        assertThat(xmlPath.getInt("digitalDocumentId"), greaterThanOrEqualTo(0));
        assertEquals(registeredDt, DateTime.parse(xmlPath.getString("registered")));
        DateTime.parse(xmlPath.getString("deactivated"));
        // check state has changed
        assertEquals("DEACTIVATED", getUrnNbnState(urnNbn));
    }

}
