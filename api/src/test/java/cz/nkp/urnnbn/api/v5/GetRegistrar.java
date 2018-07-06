package cz.nkp.urnnbn.api.v5;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.path.xml.XmlPath;
import cz.nkp.urnnbn.api.Utils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.logging.Logger;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for GET /api/v5/registrars/${REGISTRAR_CODE}
 *
 */
public class GetRegistrar extends ApiV5Tests {

    private static final Logger LOGGER = Logger.getLogger(GetRegistrar.class.getName());

    @BeforeClass
    public void beforeClass() {
        init();
    }

    private String buildUrl(String registrarCode) {
        return "/registrars/" + Utils.urlEncodeReservedChars(registrarCode);
    }

    @Test
    public void registrarCodeInvalidAll() {
        for (String registrarCode : REGISTRAR_CODES_INVALID) {
            LOGGER.info(String.format("registrar code: %s", registrarCode));
            String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                    .statusCode(400)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:error", nsContext))//
                    .when().get(buildUrl(registrarCode)).andReturn().asString();
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
                    .when().get(buildUrl(registrarCode)).andReturn().asString();
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
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESOLVER']", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_REGISTRAR']", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESERVATION']", nsContext))//
                    .when().get(buildUrl(registrarCode));
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
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESOLVER']", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_REGISTRAR']", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESERVATION']", nsContext))//
                    .when().get(buildUrl(registrarCode));
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
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESOLVER']", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_REGISTRAR']", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESERVATION']", nsContext))//
                    .when().get(buildUrl(registrarCode));
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
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESOLVER']", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_REGISTRAR']", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESERVATION']", nsContext))//
                    .when().get(buildUrl(registrarCode));
            // TODO: possibly check timestamps (created, modified) like this:
            // assertTrue(DateTime.parse(xmlPath.getString(String.format("registrar[%d].created", i))).isBeforeNow());
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
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESOLVER']", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_REGISTRAR']", nsContext))//
                    .body(hasXPath("/c:response/c:registrar/c:registrationModes/c:mode[@name='BY_RESERVATION']", nsContext))//
                    .when().get(buildUrl(registrarCode));
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
                    .when().get(buildUrl(registrarCode.toLowerCase())).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.registrar");
            int idByUpperCase = xmlPath.getInt("@id");
            // fetch by code in lower case
            responseXml = with().config(namespaceAwareXmlConfig())//
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:registrar", nsContext))//
                    .when().get(buildUrl(registrarCode.toUpperCase())).andReturn().asString();
            xmlPath = XmlPath.from(responseXml).setRoot("response.registrar");
            int idByLowerCase = xmlPath.getInt("@id");
            // check both have same registrar id
            Assert.assertEquals(idByLowerCase, idByUpperCase);
        } else {
            LOGGER.warning("no registrar available");
        }
    }

    @Test
    public void okFormatXml() {
        String registrarCode = getRandomExistingRegistrarCode();
        if (registrarCode != null) {
            LOGGER.info(String.format("registrar code: %s", registrarCode));
            with().config(namespaceAwareXmlConfig()).queryParam("format", "xml")//
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath(String.format("/c:response/c:registrar[@code='%s']", registrarCode), nsContext))//
                    .when().get(buildUrl(registrarCode));
        } else {
            LOGGER.warning("no registrars available");
        }
    }

    @Test
    public void okFormatNotSpecified() {
        String registrarCode = getRandomExistingRegistrarCode();
        if (registrarCode != null) {
            LOGGER.info(String.format("registrar code: %s", registrarCode));
            with().config(namespaceAwareXmlConfig())//
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath(String.format("/c:response/c:registrar[@code='%s']", registrarCode), nsContext))//
                    .when().get(buildUrl(registrarCode));
        } else {
            LOGGER.warning("no registrars available");
        }
    }

    @Test
    public void okFormatEmpty() {
        String registrarCode = getRandomExistingRegistrarCode();
        if (registrarCode != null) {
            LOGGER.info(String.format("registrar code: %s", registrarCode));
            with().config(namespaceAwareXmlConfig()).queryParam("format", "")//
                    .expect()//
                    .statusCode(400)//
                    .contentType(ContentType.HTML)//
                    .when().get(buildUrl(registrarCode));
        } else {
            LOGGER.warning("no registrars available");
        }
    }

    @Test
    public void formatInvalid() {
        String registrarCode = getRandomExistingRegistrarCode();
        if (registrarCode != null) {
            LOGGER.info(String.format("registrar code: %s", registrarCode));
            with().config(namespaceAwareXmlConfig()).queryParam("format", "pdf")//
                    .expect()//
                    .statusCode(400)//
                    .contentType(ContentType.HTML)//
                    .when().get(buildUrl(registrarCode));
        } else {
            LOGGER.warning("no registrars available");
        }
    }

    @Test
    public void formatJson() {
        String registrarCode = getRandomExistingRegistrarCode();
        if (registrarCode != null) {
            LOGGER.info(String.format("registrar code: %s", registrarCode));
            String responseJson = with().config(namespaceAwareXmlConfig()).queryParam("digitalLibraries", "true").queryParam("catalogs", "true")//
                    .queryParam("format", "json")//
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.JSON)//
                    .when().get(buildUrl(registrarCode)).andReturn().asString();
            JsonPath path = from(responseJson).setRoot("registrar");
            assertEquals(registrarCode, path.getString("code"));
            assertNotNull(path.getString("digitalLibraries"));
            assertNotNull(path.getString("catalogs"));
            Utils.booleanValue(path.getString("registrationModes.BY_RESOLVER"));
            Utils.booleanValue(path.getString("registrationModes.BY_REGISTRAR"));
            Utils.booleanValue(path.getString("registrationModes.BY_RESERVATION"));
        } else {
            LOGGER.warning("no registrars available");
        }
    }
}
