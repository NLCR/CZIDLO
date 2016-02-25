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
public class GetRegistrar extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(GetRegistrar.class.getName());

    @BeforeSuite
    public void beforeSuite() {
        init();
    }

    @Test
    public void registrarCodeInvalidAll() {
        for (String registrarCode : REGISTRAR_CODES_INVALID) {
            LOGGER.info(String.format("registrar code: %s", registrarCode));
            String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                    .statusCode(400)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:error", nsContext))//
                    .when().get("/registrars/" + Utils.urlEncodeReservedChars(registrarCode))//
                    .andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
            Assert.assertEquals(xmlPath.getString("code"), "INVALID_REGISTRAR_CODE");
        }
    }

    @Test
    public void registrarCodeValidUnknownAll() {
        for (String registrarCode : REGISTRAR_CODES_VALID) {
            LOGGER.info(String.format("registrar code: %s", registrarCode));
            String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:error", nsContext))//
                    .when().get("/registrars/" + Utils.urlEncodeReservedChars(registrarCode))//
                    .andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
            Assert.assertEquals(xmlPath.getString("code"), "UNKNOWN_REGISTRAR");
        }
    }

    @Test
    public void okNoQueryParams() {
        // digitalLibraries=true, catalogs=true by default
        String registrarCode = getRandomExistingRegistrarCode();
        if (registrarCode != null) {
            LOGGER.info(String.format("registrar code: %s", registrarCode));
            with().config(namespaceAwareXmlConfig())//
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath(String.format("/c:response/c:registrar[@code='%s']", registrarCode), nsContext))//
                    .body(hasXPath("//c:catalogs", nsContext))//
                    .body(hasXPath("//c:digitalLibraries", nsContext))//
                    // TODO:APIv4: until this fixed: https://github.com/NLCR/CZIDLO/issues/134
                    .body(hasXPath("/c:response/c:registrar/c:created", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESOLVER']", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_REGISTRAR']", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESERVATION']", nsContext))//
                    .when().get("/registrars/" + Utils.urlEncodeReservedChars(registrarCode));
        } else {
            LOGGER.warning("no registrars available");
        }
    }

    @Test
    public void okWithDigitalLibraries() {
        String registrarCode = getRandomExistingRegistrarCode();
        if (registrarCode != null) {
            LOGGER.info(String.format("registrar code: %s", registrarCode));
            with().config(namespaceAwareXmlConfig()).queryParam("digitalLibraries", "true")//
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath(String.format("/c:response/c:registrar[@code='%s']", registrarCode), nsContext))//
                    .body(hasXPath("//c:digitalLibraries", nsContext))//
                    // TODO:APIv4: until this fixed: https://github.com/NLCR/CZIDLO/issues/134
                    .body(hasXPath("/c:response/c:registrar/c:created", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESOLVER']", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_REGISTRAR']", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESERVATION']", nsContext))//
                    .when().get("/registrars/" + Utils.urlEncodeReservedChars(registrarCode));
        } else {
            LOGGER.warning("no registrars available");
        }
    }

    @Test
    public void okWithoutDigitalLibraries() {
        String registrarCode = getRandomExistingRegistrarCode();
        if (registrarCode != null) {
            LOGGER.info(String.format("registrar code: %s", registrarCode));
            with().config(namespaceAwareXmlConfig()).queryParam("digitalLibraries", "false")//
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath(String.format("/c:response/c:registrar[@code='%s']", registrarCode), nsContext))//
                    .body(not(hasXPath("//c:digitalLibraries", nsContext)))//
                    // TODO:APIv4: until this fixed: https://github.com/NLCR/CZIDLO/issues/134
                    .body(hasXPath("/c:response/c:registrar/c:created", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESOLVER']", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_REGISTRAR']", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESERVATION']", nsContext))//
                    .when().get("/registrars/" + Utils.urlEncodeReservedChars(registrarCode));
        } else {
            LOGGER.warning("no registrars available");
        }
    }

    @Test
    public void okWithCatalogs() {
        String registrarCode = getRandomExistingRegistrarCode();
        if (registrarCode != null) {
            LOGGER.info(String.format("registrar code: %s", registrarCode));
            with().config(namespaceAwareXmlConfig()).queryParam("catalogs", "true")//
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath(String.format("/c:response/c:registrar[@code='%s']", registrarCode), nsContext))//
                    .body(hasXPath("//c:catalogs", nsContext))//
                    // TODO:APIv4: until this fixed: https://github.com/NLCR/CZIDLO/issues/134
                    .body(hasXPath("/c:response/c:registrar/c:created", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESOLVER']", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_REGISTRAR']", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESERVATION']", nsContext))//
                    .when().get("/registrars/" + Utils.urlEncodeReservedChars(registrarCode));
        } else {
            LOGGER.warning("no registrars available");
        }
    }

    @Test
    public void okWithoutCatalogs() {
        String registrarCode = getRandomExistingRegistrarCode();
        if (registrarCode != null) {
            with().config(namespaceAwareXmlConfig()).queryParam("catalogs", "false")//
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath(String.format("/c:response/c:registrar[@code='%s']", registrarCode), nsContext))//
                    .body(not(hasXPath("//c:catalogs", nsContext)))//
                    // TODO:APIv4: until this fixed: https://github.com/NLCR/CZIDLO/issues/134
                    .body(hasXPath("/c:response/c:registrar/c:created", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESOLVER']", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_REGISTRAR']", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESERVATION']", nsContext))//
                    .when().get("/registrars/" + Utils.urlEncodeReservedChars(registrarCode));
        } else {
            LOGGER.warning("no registrars available");
        }
    }

    @Test
    public void registrarCodeCaseInsensitive() {
        String registrarCode = getRandomExistingRegistrarCode();
        if (registrarCode != null) {
            LOGGER.info(String.format("registrar code: %s", registrarCode));
            // fetch by code in upper case
            String responseXml = with().config(namespaceAwareXmlConfig())//
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:registrar", nsContext))//
                    .when().get("/registrars/" + Utils.urlEncodeReservedChars(registrarCode.toUpperCase())).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.registrar");
            int idByUpperCase = xmlPath.getInt("@id");
            // fetch by code in lower case
            responseXml = with().config(namespaceAwareXmlConfig())//
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:registrar", nsContext))//
                    .when().get("/registrars/" + Utils.urlEncodeReservedChars(registrarCode.toLowerCase())).andReturn().asString();
            xmlPath = XmlPath.from(responseXml).setRoot("response.registrar");
            int idByLowerCase = xmlPath.getInt("@id");
            // check
            Assert.assertEquals(idByLowerCase, idByUpperCase);
        } else {
            LOGGER.warning("no registrar available");
        }
    }
}
