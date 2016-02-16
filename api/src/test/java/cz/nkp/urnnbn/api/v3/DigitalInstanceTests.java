package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertThat;

import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

public class DigitalInstanceTests extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(DigitalInstanceTests.class.getName());
    private static final String TEST_USER_LOGIN = "martin";
    private static final String TEST_USER_PASSWORD = "i0oEhu";
    private static final String ID_INVALID = "abc";
    private static final int ID_UNKNOWN = 9999999;// make sure it exists and is not assigned
    private static final int ID_ACTIVE = 27703;// make sure it exists and is active
    private static final int ID_DEACTIVATED = 60;// make sure it exists and is not active
    private static final int ID_ACTIVE_NO_RIGHTS = 27703;// make sure it exists, is active and TEST_USER has no access rights to it
    // select diginst.id as diginstid from
    // (select id from registrar where code='tst01') as registrar join
    // (select id, registrarId from digitallibrary) as diglib on registrar.id=diglib.registrarid join
    // (select id, digitallibraryid from digitalinstance where active='true') as diginst on diglib.id=diginst.digitallibraryid;
    // and if needed to re-run tests:
    // update digitalinstance set active='t' where id=642390;
    private static final Integer ID_ACTIVE_RIGHTS = null;// 642390;// make sure it exists, is active and TEST_USER has access rights to it

    @BeforeSuite
    public void beforeSuite() {
        init();
    }

    @Test
    public void getUnknownDigitalInstance() {
        String xml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(400).contentType(ContentType.XML)//
                .body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .when().get("/digitalInstances/id/" + ID_INVALID).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).using(namespaceAwareXmlpathConfig()).setRoot("c:response.c:error");
        Assert.assertEquals(xmlPath.getString("c:code"), "INVALID_DIGITAL_INSTANCE_ID");
    }

    @Test
    public void getDigitalInstanceByUnknownId() {
        String xml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(404).contentType(ContentType.XML)//
                .body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .when().get("/digitalInstances/id/" + ID_UNKNOWN).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).using(namespaceAwareXmlpathConfig()).setRoot("c:response.c:error");
        Assert.assertEquals(xmlPath.getString("c:code"), "UNKNOWN_DIGITAL_INSTANCE");
    }

    @Test
    public void getExistingDigitalInstanceStatusCode() {
        expect().statusCode(200).when().get("/digitalInstances/id/" + ID_ACTIVE);
    }

    @Test
    public void getExistingDigitalInstanceContentType() {
        expect().contentType(ContentType.XML).when().get("/digitalInstances/id/" + ID_ACTIVE);
    }

    @Test
    public void getDigitalInstanceValidByXsd() {
        expect().body(matchesXsd(responseXsdString)).when().get("/digitalInstances/id/" + ID_ACTIVE);
    }

    @Test
    public void getDigitalInstancesData() {
        with().config(namespaceAwareXmlConfig()).when().get("/digitalInstances/id/" + ID_ACTIVE).then()//
                .assertThat().body(hasXPath("/c:response", nsContext))//
                .assertThat().body(hasXPath("/c:response/c:digitalInstance", nsContext))//
                .assertThat().body(hasXPath("/c:response/c:digitalInstance/@id", nsContext))//
                .assertThat().body(hasXPath("/c:response/c:digitalInstance/@active", nsContext))//
                .assertThat().body(hasXPath("/c:response/c:digitalInstance/c:url", nsContext))//
                .assertThat().body(hasXPath("/c:response/c:digitalInstance/c:created", nsContext))//
                .assertThat().body(hasXPath("/c:response/c:digitalInstance/c:digitalLibrary", nsContext))//
                .assertThat().body(hasXPath("/c:response/c:digitalInstance/c:digitalLibrary/@id", nsContext))//
                .assertThat().body(hasXPath("/c:response/c:digitalInstance/c:digitalLibrary/c:registrar", nsContext))//
                .assertThat().body(hasXPath("/c:response/c:digitalInstance/c:digitalLibrary/c:registrar/@code", nsContext))//
                .assertThat().body(hasXPath("/c:response/c:digitalInstance/c:digitalDocument", nsContext))//
                .assertThat().body(hasXPath("/c:response/c:digitalInstance/c:digitalDocument/@id", nsContext))//
                .assertThat().body(hasXPath("/c:response/c:digitalInstance/c:digitalDocument/c:urnNbn", nsContext))//
                .assertThat().body(hasXPath("/c:response/c:digitalInstance/c:digitalDocument/c:urnNbn/c:value", nsContext))//
        ;
    }

    @Test
    public void getDigitalInstanceDeactivatedActivity() {
        String xml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(200).contentType(ContentType.XML)//
                .body(matchesXsd(responseXsdString))//
                .when().get("/digitalInstances/id/" + ID_DEACTIVATED).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).using(namespaceAwareXmlpathConfig()).setRoot("c:response.c:digitalInstance");
        Assert.assertEquals(xmlPath.getBoolean("@active"), false);
    }

    public void getDigitalInstanceActiveActivity() {
        String xml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(200).contentType(ContentType.XML)//
                .body(matchesXsd(responseXsdString))//
                .when().get("/digitalInstances/id/" + ID_ACTIVE).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).using(namespaceAwareXmlpathConfig()).setRoot("c:response.c:digitalInstance");
        Assert.assertEquals(xmlPath.getBoolean("@active"), true);
    }

    @Test
    public void deactivateDigitalInstanceNotAuthenticated() {
        // TODO: check this out, seems to be the only error when response is not xml with error code
        with().config(namespaceAwareXmlConfig())//
                .expect().statusCode(401)//
                .when().delete(HTTPS_API_URL + "/digitalInstances/id/" + ID_ACTIVE_NO_RIGHTS)//
        // .when().delete("/digitalInstances/id/" + ID_ACTIVE)//
        ;
    }

    @Test
    public void deactivateDigitalInstanceNotAuthorized() {
        String xml = with().config(namespaceAwareXmlConfig()).auth().basic(TEST_USER_LOGIN, TEST_USER_PASSWORD)//
                .expect()//
                .statusCode(401).contentType(ContentType.XML)//
                .body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .when().delete(HTTPS_API_URL + "/digitalInstances/id/" + ID_ACTIVE_NO_RIGHTS)//
                // .when().delete("/digitalInstances/id/" + ID_ACTIVE)//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).using(namespaceAwareXmlpathConfig()).setRoot("c:response.c:error");
        Assert.assertEquals(xmlPath.getString("c:code"), "NOT_AUTHORIZED");
    }

    @Test
    public void deactivateDigitalInstanceAlreadyDeactivated() {
        String xml = with().config(namespaceAwareXmlConfig()).auth().basic(TEST_USER_LOGIN, TEST_USER_PASSWORD)//
                .expect()//
                .statusCode(403).contentType(ContentType.XML)//
                .body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .when().delete(HTTPS_API_URL + "/digitalInstances/id/" + ID_DEACTIVATED)
                // .when().delete("/digitalInstances/id/" + ID_ACTIVE)
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).using(namespaceAwareXmlpathConfig()).setRoot("c:response.c:error");
        Assert.assertEquals(xmlPath.getString("c:code"), "ALREADY_DEACTIVATED");
    }

    @Test
    public void deactivateDigitalInstance() {
        if (ID_ACTIVE_RIGHTS != null) {
            String xml = with().config(namespaceAwareXmlConfig()).auth().basic(TEST_USER_LOGIN, TEST_USER_PASSWORD)//
                    .expect()//
                    .statusCode(200).contentType(ContentType.XML)//
                    .body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:digitalInstance/c:url", nsContext))//
                    .body(hasXPath("/c:response/c:digitalInstance/c:created", nsContext))//
                    .body(hasXPath("/c:response/c:digitalInstance/c:deactivated", nsContext))//
                    // TODO: in api V4 should be <digitalLibrary id='12'> just like in GET. Now it is <digitalLibraryId>12</digitalLibraryId>
                    .body(hasXPath("/c:response/c:digitalInstance/c:digitalLibraryId", nsContext))//
                    .when().delete(HTTPS_API_URL + "/digitalInstances/id/" + ID_ACTIVE_RIGHTS)
                    // .when().delete("/digitalInstances/id/" + ID_ACTIVE)
                    .andReturn().asString();
            XmlPath xmlPath = XmlPath.from(xml).using(namespaceAwareXmlpathConfig()).setRoot("c:response.c:digitalInstance");
            Assert.assertEquals((Integer) xmlPath.getInt("@id"), ID_ACTIVE_RIGHTS);
            Assert.assertEquals(xmlPath.getBoolean("c:active"), false);
        } else {
            LOGGER.info("ignoring test deactivateDigitalInstance - no id of digital instance to be deactivated is available");
        }
    }
}