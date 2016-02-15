package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.not;

import java.util.logging.Logger;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;

public class RegistrarTests extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(RegistrarTests.class.getName());

    @BeforeSuite
    public void beforeSuite() {
        init();
    }

    @Test
    public void getRegistrarStatusCode() {
        getRandomRegistrarCode();
        expect().statusCode(200).when().get("/registrars/" + getRandomRegistrarCode());
    }

    @Test
    public void getInvalidRegistrarStatusCode() {
        expect().statusCode(400).when().get("/registrars/0123456789");
    }

    @Test
    public void getUnknownRegistrarStatusCode() {
        expect().statusCode(404).when().get("/registrars/xxx000");
    }

    @Test
    public void getRegistrarContentType() {
        expect().contentType(ContentType.XML).when().get("/registrars/" + getRandomRegistrarCode());
    }

    @Test
    public void getRegistrarValidByXsd() {
        expect().body(matchesXsd(responseXsdString)).when().get("/registrars/" + getRandomRegistrarCode());
    }

    @Test
    public void getRegistrarWithDigitalLibraries() {
        with().config(namespaceAwareXmlConfig()).parameters("digitalLibraries", "true")//
                .expect().body(hasXPath("//c:digitalLibraries", nsContext)).when().get("/registrars/" + getRandomRegistrarCode());
    }

    @Test
    public void getRegistrarWithoutDigitalLibraries() {
        with().config(namespaceAwareXmlConfig()).parameters("digitalLibraries", "false")//
                .expect().body(not(hasXPath("//c:digitalLibraries", nsContext))).when().get("/registrars/" + getRandomRegistrarCode());
    }

    @Test
    public void getRegistrarWithCatalogs() {
        with().config(namespaceAwareXmlConfig()).parameters("catalogs", "true")//
                .expect().body(hasXPath("//c:catalogs", nsContext)).when().get("/registrars/" + getRandomRegistrarCode());
    }

    @Test
    public void getRegistrarWithoutCatalogs() {
        with().config(namespaceAwareXmlConfig()).parameters("catalogs", "false")//
                .expect().body(not(hasXPath("//c:catalogs", nsContext))).when().get("/registrars/" + getRandomRegistrarCode());
    }

    @Test
    public void getRegistrarData() {
        String code = getRandomRegistrarCode();
        with().config(namespaceAwareXmlConfig()).when().get("/registrars/" + code).then()//
                .assertThat().body(hasXPath("/c:response/c:registrar", nsContext))//
                .assertThat().body(hasXPath("/c:response/c:registrar/c:name", nsContext))//
                .assertThat().body(hasXPath("/c:response/c:registrar/@code", nsContext))//
                .assertThat().body(hasXPath("/c:response/c:registrar/c:created", nsContext))//
                .assertThat().body(hasXPath("/c:response/c:registrar/c:modified", nsContext))//
                .assertThat().body(hasXPath("/c:response/c:registrar/c:registrationModes", nsContext))//
                .assertThat().body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESOLVER']", nsContext))//
                .assertThat().body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_REGISTRAR']", nsContext))//
                .assertThat().body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESERVATION']", nsContext))//
        ;
    }

}
