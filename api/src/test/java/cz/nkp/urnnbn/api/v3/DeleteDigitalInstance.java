package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.delete;
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
import cz.nkp.urnnbn.core.dto.DigitalInstance;

/**
 * Tests for DELETE /api/v3/digitalInstances/id/${DIGITAL_INSTANCE_ID}
 *
 */
public class DeleteDigitalInstance extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(DeleteDigitalInstance.class.getName());

    private Long digLibId;

    @BeforeClass
    public void beforeClass() {
        init();
        digLibId = getDigitalLibraryIdOrNull(REGISTRAR);
    }

    private String buildUrl(String id) {
        return HTTPS_API_URL + "/digitalInstances/id/" + Utils.urlEncodeReservedChars(id);
    }

    private String buildUrl(long id) {
        return buildUrl(String.valueOf(id));
    }

    private Long createActiveDigitalInstanceOrNull() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        if (urnNbn == null) {
            LOGGER.warning("failed to get URN:NBN by registering new digital document");
            return null;
        } else if (digLibId == null) {
            LOGGER.warning("no digital library id available");
            return null;
        } else {
            return insertDigitalInstance(urnNbn, digLibId, "http://something.com/somewhere", USER);
        }
    }

    @Test
    public void notAuthenticated() {
        Long id = createActiveDigitalInstanceOrNull();
        if (id != null) {
            LOGGER.info("id: " + id);
            // check not deactivated yet
            DigitalInstance idBefore = getDigitalInstanceOrNull(id);
            assertTrue(idBefore.isActive());
            // try and deactivate
            delete(buildUrl(id)).then().assertThat().statusCode(401);
            // check still not deactivated
            DigitalInstance idAfter = getDigitalInstanceOrNull(id);
            assertTrue(idAfter.isActive());
        } else {
            LOGGER.warning("no digital instance available, ignoring");
        }
    }

    @Test
    public void notAuthorized() {
        Long id = createActiveDigitalInstanceOrNull();
        if (id != null) {
            LOGGER.info("id: " + id);
            // check not deactivated yet
            DigitalInstance idBefore = getDigitalInstanceOrNull(id);
            assertTrue(idBefore.isActive());
            // try and deactivate
            String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_NO_RIGHTS.login, USER_NO_RIGHTS.password)//
                    .expect()//
                    .statusCode(401)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:error", nsContext))//
                    .when().delete(buildUrl(id))//
                    .andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
            Assert.assertEquals(xmlPath.getString("code"), "NOT_AUTHORIZED");
            // check still not deactivated
            DigitalInstance idAfter = getDigitalInstanceOrNull(id);
            assertTrue(idAfter.isActive());
        } else {
            LOGGER.warning("no digital instance available, ignoring");
        }
    }

    @Test
    public void idNotNumber() {
        String id = "abc";
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(buildUrl(id)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_DIGITAL_INSTANCE_ID");
    }

    @Test
    public void idUnknown() {
        Long id = getRandomFreeDigitalInstanceIdOrNull();
        if (id == null) {
            LOGGER.warning("didn't find any free digital instance, ignoring");
        } else {
            LOGGER.info("id: " + id);
            String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                    .expect()//
                    .statusCode(404)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:error", nsContext))//
                    .when().delete(buildUrl(id)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
            Assert.assertEquals(xmlPath.getString("code"), "UNKNOWN_DIGITAL_INSTANCE");
        }
    }

    @Test
    public void deactivatedAlready() {
        Long id = createActiveDigitalInstanceOrNull();
        if (id != null) {
            LOGGER.info("id: " + id);
            // deactivate
            deactivateDigitalInstance(id, USER);
            // check deactivated and store deactivation time
            DigitalInstance idBefore = getDigitalInstanceOrNull(id);
            assertFalse(idBefore.isActive());
            // try and deactivate
            String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                    .expect()//
                    .statusCode(403)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:error", nsContext))//
                    .when().delete(buildUrl(id))//
                    .andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
            Assert.assertEquals(xmlPath.getString("code"), "ALREADY_DEACTIVATED");
            // check deactivation time has not changed
            DigitalInstance idAfter = getDigitalInstanceOrNull(id);
            assertFalse(idAfter.isActive());
            assertEquals(idBefore.getDeactivated(), idAfter.getDeactivated());
        } else {
            LOGGER.warning("no digital instance available, ignoring");
        }
    }

    @Test
    public void ok() {
        Long id = createActiveDigitalInstanceOrNull();
        if (id != null) {
            LOGGER.info("id: " + id);
            // check not deactivated yet
            DigitalInstance idBefore = getDigitalInstanceOrNull(id);
            assertTrue(idBefore.isActive());
            // try and deactivate
            String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:digitalInstance/c:url", nsContext))//
                    .body(hasXPath("/c:response/c:digitalInstance/c:created", nsContext))//
                    .body(hasXPath("/c:response/c:digitalInstance/c:deactivated", nsContext))//
                    // TODO:APIv4: should be <digitalLibrary id='12'> just like in GET. Now it is <digitalLibraryId>12</digitalLibraryId>
                    .body(hasXPath("/c:response/c:digitalInstance/c:digitalLibraryId", nsContext))//
                    .when().delete(buildUrl(id))//
                    .andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.digitalInstance");
            Assert.assertEquals((Long) xmlPath.getLong("@id"), id);
            Assert.assertEquals(Utils.booleanValue(xmlPath.getString("@active")), false);
            // check that deactivated
            DigitalInstance idAfter = getDigitalInstanceOrNull(id);
            assertFalse(idAfter.isActive());
        } else {
            LOGGER.warning("no digital instance available, ignoring");
        }
    }

}
