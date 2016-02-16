package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasXPath;//.greaterThanOrEqualTo;
import static org.junit.Assert.assertThat;

import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

public class DigitalInstanceTests extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(DigitalInstanceTests.class.getName());
    private static final int ID = 5;// TODO: ziskat existujici
    private static final int UNKNOWN_ID = 9999999;// should be unassigned in testing instance of czidlo

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
                .when().get("/digitalInstances/id/abc").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).using(namespaceAwareXmlpathConfig()).setRoot("c:response.c:error");
        Assert.assertEquals(xmlPath.getString("c:code"), "INVALID_DIGITAL_INSTANCE_ID");
    }

    @Test
    public void getDigitalInstanceByInvalidId() {
        String xml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(404).contentType(ContentType.XML)//
                .body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .when().get("/digitalInstances/id/" + UNKNOWN_ID).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).using(namespaceAwareXmlpathConfig()).setRoot("c:response.c:error");
        Assert.assertEquals(xmlPath.getString("c:code"), "UNKNOWN_DIGITAL_INSTANCE");
    }

    @Test
    public void getExistingDigitalInstanceStatusCode() {
        expect().statusCode(200).when().get("/digitalInstances/id/" + ID);
    }

    @Test
    public void getExistingDigitalInstanceContentType() {
        expect().contentType(ContentType.XML).when().get("/digitalInstances/id/" + ID);
    }

    @Test
    public void getDigitalInstanceValidByXsd() {
        expect().body(matchesXsd(responseXsdString)).when().get("/digitalInstances/id/" + ID);
    }

    @Test
    public void getDigitalInstancesData() {
        with().config(namespaceAwareXmlConfig()).when().get("/digitalInstances/id/" + ID).then()//
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
    public void deactivateDigitalInstancesNotAuthenticated() {
        //TODO: body seems to be empty
        String xml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(401).contentType(ContentType.XML)//
                .body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .when().delete("/digitalInstances/id/" + ID).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).using(namespaceAwareXmlpathConfig()).setRoot("c:response.c:error");
        Assert.assertEquals(xmlPath.getString("c:code"), "NOT_AUTHENTICATED");
    }
    
    //TODO: errors NOT_AUTHORIZED, ALREADY_DEACTIVATED

}
