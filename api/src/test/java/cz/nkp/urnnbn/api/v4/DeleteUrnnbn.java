package cz.nkp.urnnbn.api.v4;

import static com.jayway.restassured.RestAssured.delete;
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
 * Tests for DELETE /api/v4/urnnbn/${URN_NBN}
 *
 */
public class DeleteUrnnbn extends ApiV4Tests {

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
        delete(buildUrl(urnNbn)).then().assertThat().statusCode(401);
        // check state hasn't changed
        assertEquals("ACTIVE", getUrnNbnStatus(urnNbn));
    }

    @Test
    public void notAuthorized() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_NO_RIGHTS.login, USER_NO_RIGHTS.password)//
                .expect()//
                .statusCode(403)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "NO_ACCESS_RIGHTS");
        // check state hasn't changed
        assertEquals("ACTIVE", getUrnNbnStatus(urnNbn));
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
        assertEquals("DEACTIVATED", getUrnNbnStatus(urnNbn));
    }

    @Test
    public void errorStateFree() {
        String urnNbn = getRandomFreeUrnNbnOrNull(REGISTRAR);
        if (urnNbn == null) {
            LOGGER.warning("no free urn:nbn found, ignoring");
        } else {
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
            assertEquals("FREE", getUrnNbnStatus(urnNbn));
        }
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
        assertEquals("RESERVED", getUrnNbnStatus(urnNbn));
    }

    @Test
    public void withoutNote() {
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
        assertEquals("DEACTIVATED", getUrnNbnStatus(urnNbn));
    }

    @Test
    public void withtNote() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        DateTime registeredDt = getUrnNbnRegisteredOrNull(urnNbn);
        String note = "something";
        LOGGER.info(String.format("%s, note: %s", urnNbn.toString(), note));
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("note", note).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbn", nsContext))//
                .when().delete(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbn");
        assertEquals("DEACTIVATED", xmlPath.getString("status"));
        assertEquals(note, xmlPath.getString("deactivationNote"));
        assertEquals(urnNbn.toLowerCase(), xmlPath.getString("value"));
        String[] splitUrnNbn = Utils.splitUrnNbn(urnNbn);
        assertEquals(splitUrnNbn[0], xmlPath.getString("countryCode"));
        assertEquals(splitUrnNbn[1], xmlPath.getString("registrarCode"));
        assertEquals(splitUrnNbn[2], xmlPath.getString("documentCode"));
        assertThat(xmlPath.getInt("digitalDocumentId"), greaterThanOrEqualTo(0));
        assertEquals(registeredDt, DateTime.parse(xmlPath.getString("registered")));
        DateTime.parse(xmlPath.getString("deactivated"));
        // check state has changed
        assertEquals("DEACTIVATED", getUrnNbnStatus(urnNbn));
    }

    @Test
    public void withtNoteEmpty() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        DateTime registeredDt = getUrnNbnRegisteredOrNull(urnNbn);
        String note = "";
        LOGGER.info(String.format("%s, note: %s", urnNbn.toString(), note));
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("note", note).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbn", nsContext))//
                .when().delete(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbn");
        assertEquals("DEACTIVATED", xmlPath.getString("status"));
        assertEquals("", xmlPath.getString("deactivationNote"));
        assertEquals(urnNbn.toLowerCase(), xmlPath.getString("value"));
        String[] splitUrnNbn = Utils.splitUrnNbn(urnNbn);
        assertEquals(splitUrnNbn[0], xmlPath.getString("countryCode"));
        assertEquals(splitUrnNbn[1], xmlPath.getString("registrarCode"));
        assertEquals(splitUrnNbn[2], xmlPath.getString("documentCode"));
        assertThat(xmlPath.getInt("digitalDocumentId"), greaterThanOrEqualTo(0));
        assertEquals(registeredDt, DateTime.parse(xmlPath.getString("registered")));
        DateTime.parse(xmlPath.getString("deactivated"));
        // check state has changed
        assertEquals("DEACTIVATED", getUrnNbnStatus(urnNbn));
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
        assertEquals("DEACTIVATED", getUrnNbnStatus(urnNbn));

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
        assertEquals("DEACTIVATED", getUrnNbnStatus(urnNbn));
    }
}
