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
import org.testng.annotations.BeforeSuite;
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

    private final String REGISTRAR = "aba001";// must exist and have registration mode BY_RESOLVER enabled
    private final Credentials USER = new Credentials("martin", "i0oEhu"); // must exist and have access rights to REGISTRAR
    private final Credentials USER_NO_RIGHTS = new Credentials("nobody", "skgo1dukg");// must exist and not have access rights to REGISTRAR

    private Long digLibId;

    @BeforeSuite
    public void beforeSuite() {
        init();
    }

    @BeforeClass
    public void beforeClass() {
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
            // TODO:APIv4: return xml
            // String responseXml =
            with().config(namespaceAwareXmlConfig()).expect()//
                    .statusCode(401)//
                    // .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    // .body(matchesXsd(responseXsdString))//
                    // .body(hasXPath("/c:response/c:error", nsContext))//
                    .when().delete(buildUrl(id))//
            // .andReturn().asString()
            ;
            // XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
            // Assert.assertEquals(xmlPath.getString("code"), "NOT_AUTHENTICATED");

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
    public void alreadyDeactivated() {
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
            Assert.assertEquals(xmlPath.getBoolean("active"), false);
            // check that deactivated
            DigitalInstance idAfter = getDigitalInstanceOrNull(id);
            assertFalse(idAfter.isActive());
        } else {
            LOGGER.warning("no digital instance available, ignoring");
        }
    }

}
