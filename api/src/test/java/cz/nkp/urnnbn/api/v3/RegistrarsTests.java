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

public class RegistrarsTests extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(RegistrarsTests.class.getName());

    @BeforeSuite
    public void beforeSuite() {
        init();
    }

    @Test
    public void getRegistrarsStatusCode() {
        expect().statusCode(200).when().get("/registrars");
    }

    @Test
    public void getRegistrarsContentType() {
        expect().contentType(ContentType.XML).when().get("/registrars");
    }

    @Test
    public void getRegistrarsValidByXsd() {
        expect().body(matchesXsd(responseXsdString)).when().get("/registrars");
    }

    @Test
    public void getRegistrarsXmlContainsRegistrars() {
        with().config(namespaceAwareXmlConfig()).expect().body(hasXPath("/c:response/c:registrars", nsContext)).when().get("/registrars");
    }

    @Test
    public void getRegistrarsWithDigitalLibraries() {
        with().config(namespaceAwareXmlConfig()).parameters("digitalLibraries", "true")//
                .expect().body(hasXPath("//c:digitalLibraries", nsContext)).when().get("/registrars");
    }

    @Test
    public void getRegistrarsWithoutDigitalLibraries() {
        with().config(namespaceAwareXmlConfig()).parameters("digitalLibraries", "false")//
                .expect().body(not(hasXPath("//c:digitalLibraries", nsContext))).when().get("/registrars");
    }

    @Test
    public void getRegistrarsWithCatalogs() {
        with().config(namespaceAwareXmlConfig()).parameters("catalogs", "true")//
                .expect().body(hasXPath("//c:catalogs", nsContext)).when().get("/registrars");
    }

    @Test
    public void getRegistrarsWithoutCatalogs() {
        with().config(namespaceAwareXmlConfig()).parameters("catalogs", "false")//
                .expect().body(not(hasXPath("//c:catalogs", nsContext))).when().get("/registrars");
    }

    @Test
    public void getRegistrarsRandomRegistrarData() {
        String code = getRandomRegistrarCode();
        with().config(namespaceAwareXmlConfig()).when().get("/registrars").then()//
                .assertThat().body(hasXPath("//c:registrar[@code='" + code + "']", nsContext))//
                .assertThat().body(hasXPath("//c:registrar[@code='" + code + "']/c:name", nsContext))//
                .assertThat().body(hasXPath("//c:registrar[@code='" + code + "']/c:created", nsContext))//
                .assertThat().body(hasXPath("//c:registrar[@code='" + code + "']/c:modified", nsContext))//
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
