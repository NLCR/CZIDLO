package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.core.dto.DigitalInstance;

public class DeleteDigitalInstanceTests extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(DeleteDigitalInstanceTests.class.getName());
    private static final Credentials USER = new Credentials("martin", "i0oEhu");
    // select diginst.id as diginstid from
    // (select id from registrar where code='tst01') as registrar join
    // (select id, registrarId from digitallibrary) as diglib on registrar.id=diglib.registrarid join
    // (select id, digitallibraryid from digitalinstance where active='true') as diginst on diglib.id=diginst.digitallibraryid;
    // and if needed to re-run tests:
    // update digitalinstance set active='t', deactivated=null where id=642390;
    private static final Long ID_ACTIVE_RIGHTS_1 = 642389L;// make sure it exists, is active and TEST_USER has access rights to it
    private static final Long ID_ACTIVE_RIGHTS_2 = null;// 642390L;// make sure it exists, is active and TEST_USER has access rights to it
    private static final int ID_ACTIVE_NO_RIGHTS = 27703;// make sure it exists, is active and TEST_USER has no access rights to it
    private static final int ID_DEACTIVATED = 60;// make sure it exists and is not active

    @BeforeSuite
    public void beforeSuite() {
        init();
    }

    @Test
    public void deactivateDigitalInstanceNotAuthenticated() {
        Long id = ID_ACTIVE_RIGHTS_1;
        if (id != null) {
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
                    // .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                    // .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                    .when().delete(HTTPS_API_URL + "/digitalInstances/id/" + id)//
            // .andReturn().asString()
            ;
            // XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
            // Assert.assertEquals(xmlPath.getString("code"), "NOT_AUTHENTICATED");
            // check still not deactivated
            DigitalInstance idAfter = getDigitalInstanceOrNull(id);
            assertTrue(idAfter.isActive());
        }
    }

    @Test
    public void deactivateDigitalInstanceNotAuthorized() {
        int id = ID_ACTIVE_NO_RIGHTS;
        // check not deactivated yet
        DigitalInstance idBefore = getDigitalInstanceOrNull(id);
        assertTrue(idBefore.isActive());
        // try and deactivate
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(401)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .when().delete(HTTPS_API_URL + "/digitalInstances/id/" + id)//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "NOT_AUTHORIZED");
        // check still not deactivated
        DigitalInstance idAfter = getDigitalInstanceOrNull(id);
        assertTrue(idAfter.isActive());
    }

    @Test
    public void deactivateDigitalInstanceAlreadyDeactivated() {
        int id = ID_DEACTIVATED;
        // check deactivated
        DigitalInstance idBefore = getDigitalInstanceOrNull(id);
        assertFalse(idBefore.isActive());
        // try and deactivate
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(403)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .when().delete(HTTPS_API_URL + "/digitalInstances/id/" + id).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "ALREADY_DEACTIVATED");
        // check deactivation time has not changed
        DigitalInstance idAfter = getDigitalInstanceOrNull(id);
        assertFalse(idAfter.isActive());
        assertEquals(idBefore.getDeactivated(), idAfter.getDeactivated());
    }

    @Test
    public void deactivateDigitalInstance() {
        Long id = ID_ACTIVE_RIGHTS_2;
        if (id != null) {
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
                    .when().delete(HTTPS_API_URL + "/digitalInstances/id/" + id)
                    // .when().delete("/digitalInstances/id/" + ID_ACTIVE)
                    .andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.digitalInstance");
            Assert.assertEquals((Long) xmlPath.getLong("@id"), id);
            Assert.assertEquals(xmlPath.getBoolean("active"), false);
            // check that deactivated
            DigitalInstance idAfter = getDigitalInstanceOrNull(id);
            assertFalse(idAfter.isActive());
        } else {
            LOGGER.info("ignoring test deactivateDigitalInstance - no id of digital instance to be deactivated is available");
        }
    }
}
