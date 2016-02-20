package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.not;

import java.util.logging.Logger;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;

public class GetRegistrarsTests extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(GetRegistrarsTests.class.getName());

    @BeforeSuite
    public void beforeSuite() {
        init();
    }

    @Test
    public void getRegistrarsStatusCode() {
        expect().statusCode(200).when().get("/registrars");
    }

    @Test
    public void getRegistrarsResponseValidXml() {
        expect().contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .when().get("/registrars");
    }

    @Test
    public void getRegistrarsWithDigitalLibraries() {
        with().config(namespaceAwareXmlConfig()).queryParam("digitalLibraries", "true")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrars", nsContext))//
                .body(hasXPath("//c:digitalLibraries", nsContext))//
                .when().get("/registrars");
    }

    @Test
    public void getRegistrarsWithoutDigitalLibraries() {
        with().config(namespaceAwareXmlConfig()).queryParam("digitalLibraries", "false")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrars", nsContext))//
                .body(not(hasXPath("//c:digitalLibraries", nsContext)))//
                .when().get("/registrars");
    }

    @Test
    public void getRegistrarsWithCatalogs() {
        with().config(namespaceAwareXmlConfig()).queryParam("catalogs", "true")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrars", nsContext))//
                .body(hasXPath("//c:catalogs", nsContext))//
                .when().get("/registrars");
    }

    @Test
    public void getRegistrarsWithoutCatalogs() {
        with().config(namespaceAwareXmlConfig()).queryParam("catalogs", "false")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrars", nsContext))//
                .body(not(hasXPath("//c:catalogs", nsContext)))//
                .when().get("/registrars");
    }

    @Test
    public void getRegistrarsRandomRegistrarData() {
        String code = getRandomRegistrarCode();
        with().config(namespaceAwareXmlConfig()).when().get("/registrars").then()//
                .assertThat().statusCode(200)//
                .assertThat().contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .assertThat().body(hasXPath("/c:response/c:registrars", nsContext))//
                .assertThat().body(hasXPath("//c:registrar[@code='" + code + "']", nsContext))//
                .assertThat().body(hasXPath("//c:registrar[@code='" + code + "']/c:name", nsContext))//
                .assertThat().body(hasXPath("//c:registrar[@code='" + code + "']/c:created", nsContext))//
                .assertThat().body(hasXPath("//c:registrar[@code='" + code + "']/c:registrationModes", nsContext))//
                .assertThat().body(hasXPath("//c:registrar[@code='" + code + "']/c:registrationModes/c:mode[@name='BY_RESOLVER']", nsContext))//
                .assertThat().body(hasXPath("//c:registrar[@code='" + code + "']/c:registrationModes/c:mode[@name='BY_REGISTRAR']", nsContext))//
                .assertThat().body(hasXPath("//c:registrar[@code='" + code + "']/c:registrationModes/c:mode[@name='BY_RESERVATION']", nsContext))//
        ;
    }

    @BeforeMethod
    public void beforeMethod() {
    }

    @AfterMethod
    public void afterMethod() {
    }

    @BeforeClass
    public void beforeClass() {
    }

    @AfterClass
    public void afterClass() {
    }

    @BeforeTest
    public void beforeTest() {
    }

    @AfterTest
    public void afterTest() {
    }

    @AfterSuite
    public void afterSuite() {
    }

}
