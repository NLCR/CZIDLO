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

public class GetDigitalInstanceTests extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(GetDigitalInstanceTests.class.getName());

    private static final String ID_INVALID = "abc";
    private static final long ID_UNKNOWN = 9999999L;// make sure it exists and is not assigned
    private static final long ID_ACTIVE = 27703L;// make sure it exists and is active
    private static final long ID_DEACTIVATED = 60L;// make sure it exists and is not active

    @BeforeSuite
    public void beforeSuite() {
        init();
    }

    @Test
    public void getDigitalInstanceInvalidId() {
        String xml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .when().get("/digitalInstances/id/" + ID_INVALID).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_DIGITAL_INSTANCE_ID");
    }

    @Test
    public void getDigitalInstanceUnknownId() {
        String xml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .when().get("/digitalInstances/id/" + ID_UNKNOWN).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "UNKNOWN_DIGITAL_INSTANCE");
    }

    public void getDigitalInstanceActive() {
        long id = ID_ACTIVE;
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalInstance", nsContext))//
                .when().get("/digitalInstances/id/" + id).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.digitalInstance");
        assertEquals(id, xmlPath.getLong("@id"));
        assertTrue(xmlPath.getBoolean("@active"));
    }

    @Test
    public void getDigitalInstanceDeactivated() {
        long id = ID_DEACTIVATED;
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalInstance", nsContext))//
                .when().get("/digitalInstances/id/" + id).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.digitalInstance");
        assertEquals(id, xmlPath.getLong("@id"));
        assertFalse(xmlPath.getBoolean("@active"));
    }

    @Test
    public void getDigitalInstancesData() {
        with().config(namespaceAwareXmlConfig())//
                .when().get("/digitalInstances/id/" + ID_ACTIVE).then()//
                .assertThat().body(matchesXsd(responseXsdString))//
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

}
