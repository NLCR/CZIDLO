package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.not;

import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

public class RegistrarTests extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(RegistrarTests.class.getName());

    @BeforeSuite
    public void beforeSuite() {
        init();
    }

    @Test
    public void getInvalidRegistrar() {
        String xml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .when().get("/registrars/0123456789").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_REGISTRAR_CODE");
    }

    @Test
    public void getUnknownRegistrar() {
        String xml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .when().get("/registrars/xxx000").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "UNKNOWN_REGISTRAR");
    }

    @Test
    public void getRegistrarStatusCode() {
        getRandomRegistrarCode();
        expect().statusCode(200).when().get("/registrars/" + getRandomRegistrarCode());
    }

    @Test
    public void getRegistrarResponseValidXml() {
        expect()//
        .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .when().get("/registrars/" + getRandomRegistrarCode());
    }

    @Test
    public void getRegistrarWithDigitalLibraries() {
        with().config(namespaceAwareXmlConfig()).queryParam("digitalLibraries", "true")//
                .expect()//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("//c:digitalLibraries", nsContext))//
                .when().get("/registrars/" + getRandomRegistrarCode());
    }

    @Test
    public void getRegistrarWithoutDigitalLibraries() {
        with().config(namespaceAwareXmlConfig()).queryParam("digitalLibraries", "false")//
                .expect()//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(not(hasXPath("//c:digitalLibraries", nsContext)))//
                .when().get("/registrars/" + getRandomRegistrarCode());
    }

    @Test
    public void getRegistrarWithCatalogs() {
        with().config(namespaceAwareXmlConfig()).queryParam("catalogs", "true")//
                .expect()//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("//c:catalogs", nsContext))//
                .when().get("/registrars/" + getRandomRegistrarCode());
    }

    @Test
    public void getRegistrarWithoutCatalogs() {
        with().config(namespaceAwareXmlConfig()).queryParam("catalogs", "false")//
                .expect()//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(not(hasXPath("//c:catalogs", nsContext)))//
                .when().get("/registrars/" + getRandomRegistrarCode());
    }

    @Test
    public void getRegistrarData() {
        String code = getRandomRegistrarCode();
        with().config(namespaceAwareXmlConfig()).when().get("/registrars/" + code).then()//
                .assertThat().contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .assertThat().body(hasXPath("/c:response/c:registrar/c:name", nsContext))//
                .assertThat().body(hasXPath("/c:response/c:registrar/@code", nsContext))//
                .assertThat().body(hasXPath("/c:response/c:registrar/c:created", nsContext))//
                .assertThat().body(hasXPath("/c:response/c:registrar/c:registrationModes", nsContext))//
                .assertThat().body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESOLVER']", nsContext))//
                .assertThat().body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_REGISTRAR']", nsContext))//
                .assertThat().body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESERVATION']", nsContext))//
        ;
    }

}
