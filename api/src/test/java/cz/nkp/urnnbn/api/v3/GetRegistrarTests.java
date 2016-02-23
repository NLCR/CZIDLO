package cz.nkp.urnnbn.api.v3;

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

import cz.nkp.urnnbn.api.Utils;

/**
 * Tests for GET /api/v3/registrars/${REGISTRAR_CODE}
 *
 */
public class GetRegistrarTests extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(GetRegistrarTests.class.getName());

    @BeforeSuite
    public void beforeSuite() {
        init();
    }

    @Test
    public void registrarCodeInvalid() {
        for (String code : REGISTRAR_CODES_INVALID) {
            LOGGER.info(String.format("registrar code: %s", code));
            String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                    .statusCode(400)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                    .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                    .when().get("/registrars/" + Utils.urlEncodeReservedChars(code)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
            Assert.assertEquals(xmlPath.getString("code"), "INVALID_REGISTRAR_CODE");
        }
    }

    @Test
    public void registrarCodeValid() {
        for (String code : REGISTRAR_CODES_VALID) {
            LOGGER.info(String.format("registrar code: %s", code));
            String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response", nsContext))//
                    .when().get("/registrars/" + Utils.urlEncodeReservedChars(code)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
            Assert.assertTrue("UNKNOWN_REGISTRAR".equals(xmlPath.getString("error.code"))
                    || code.toLowerCase().equals(xmlPath.getString("registrar.@code")));
        }
    }

    @Test
    public void registrarCodeUnknown() {
        String code = "xxx000";
        LOGGER.info(String.format("registrar code: %s", code));
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .when().get("/registrars/" + Utils.urlEncodeReservedChars(code)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "UNKNOWN_REGISTRAR");
    }

    @Test
    public void okNoQueryParams() {
        // digitalLibraries=true, catalogs=true by default
        String code = getRandomExistingRegistrarCode();
        if (code != null) {
            LOGGER.info(String.format("registrar code: %s", code));
            with().config(namespaceAwareXmlConfig())//
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath(String.format("/c:response/c:registrar[@code='%s']", code), nsContext))//
                    .body(hasXPath("//c:catalogs", nsContext))//
                    .body(hasXPath("//c:digitalLibraries", nsContext))//
                    // TODO:APIv4: until this fixed: https://github.com/NLCR/CZIDLO/issues/134
                    .body(hasXPath("/c:response/c:registrar/c:created", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESOLVER']", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_REGISTRAR']", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESERVATION']", nsContext))//
                    .when().get("/registrars/" + Utils.urlEncodeReservedChars(code));
        } else {
            LOGGER.warning("no registrars available");
        }
    }

    @Test
    public void okWithDigitalLibraries() {
        String code = getRandomExistingRegistrarCode();
        if (code != null) {
            LOGGER.info(String.format("registrar code: %s", code));
            with().config(namespaceAwareXmlConfig()).queryParam("digitalLibraries", "true")//
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath(String.format("/c:response/c:registrar[@code='%s']", code), nsContext))//
                    .body(hasXPath("//c:digitalLibraries", nsContext))//
                    // TODO:APIv4: until this fixed: https://github.com/NLCR/CZIDLO/issues/134
                    .body(hasXPath("/c:response/c:registrar/c:created", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESOLVER']", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_REGISTRAR']", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESERVATION']", nsContext))//
                    .when().get("/registrars/" + Utils.urlEncodeReservedChars(code));
        } else {
            LOGGER.warning("no registrars available");
        }
    }

    @Test
    public void okWithoutDigitalLibraries() {
        String code = getRandomExistingRegistrarCode();
        if (code != null) {
            LOGGER.info(String.format("registrar code: %s", code));
            with().config(namespaceAwareXmlConfig()).queryParam("digitalLibraries", "false")//
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath(String.format("/c:response/c:registrar[@code='%s']", code), nsContext))//
                    .body(not(hasXPath("//c:digitalLibraries", nsContext)))//
                    // TODO:APIv4: until this fixed: https://github.com/NLCR/CZIDLO/issues/134
                    .body(hasXPath("/c:response/c:registrar/c:created", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESOLVER']", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_REGISTRAR']", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESERVATION']", nsContext))//
                    .when().get("/registrars/" + Utils.urlEncodeReservedChars(code));
        } else {
            LOGGER.warning("no registrars available");
        }
    }

    @Test
    public void okWithCatalogs() {
        String code = getRandomExistingRegistrarCode();
        if (code != null) {
            LOGGER.info(String.format("registrar code: %s", code));
            with().config(namespaceAwareXmlConfig()).queryParam("catalogs", "true")//
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath(String.format("/c:response/c:registrar[@code='%s']", code), nsContext))//
                    .body(hasXPath("//c:catalogs", nsContext))//
                    // TODO:APIv4: until this fixed: https://github.com/NLCR/CZIDLO/issues/134
                    .body(hasXPath("/c:response/c:registrar/c:created", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESOLVER']", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_REGISTRAR']", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESERVATION']", nsContext))//
                    .when().get("/registrars/" + Utils.urlEncodeReservedChars(code));
        } else {
            LOGGER.warning("no registrars available");
        }
    }

    @Test
    public void okWithoutCatalogs() {
        String code = getRandomExistingRegistrarCode();
        if (code != null) {
            with().config(namespaceAwareXmlConfig()).queryParam("catalogs", "false")//
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath(String.format("/c:response/c:registrar[@code='%s']", code), nsContext))//
                    .body(not(hasXPath("//c:catalogs", nsContext)))//
                    // TODO:APIv4: until this fixed: https://github.com/NLCR/CZIDLO/issues/134
                    .body(hasXPath("/c:response/c:registrar/c:created", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESOLVER']", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_REGISTRAR']", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESERVATION']", nsContext))//
                    .when().get("/registrars/" + Utils.urlEncodeReservedChars(code));
        } else {
            LOGGER.warning("no registrars available");
        }
    }

}
