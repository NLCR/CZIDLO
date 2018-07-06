package cz.nkp.urnnbn.api.v5;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;
import cz.nkp.urnnbn.api.Utils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.logging.Logger;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertThat;

/**
 * Tests for GET /api/v5/registrars/${REGISTRARS_CODE}/digitalDocuments
 *
 */
public class GetDigitalDocumentsByRegistrar extends ApiV5Tests {
    private static final Logger LOGGER = Logger.getLogger(GetDigitalDocumentsByRegistrar.class.getName());

    @BeforeClass
    public void beforeClass() {
        init();
    }

    private String buildUrl(String registrarCode) {
        return "/registrars/" + Utils.urlEncodeReservedChars(registrarCode) + "/digitalDocuments";
    }

    @Test
    public void registrarCodeInvalid() {
        String registrarCode = Utils.getRandomItem(REGISTRAR_CODES_INVALID);
        LOGGER.info("registrar code: " + registrarCode);
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(registrarCode)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_REGISTRAR_CODE");
    }

    @Test
    public void registrarCodesValidUnknown() {
        String registrarCode = Utils.getRandomItem(REGISTRAR_CODES_VALID);
        LOGGER.info("registrar code: " + registrarCode);
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(registrarCode)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "UNKNOWN_REGISTRAR");
    }

    @Test
    public void okFormatXml() {
        String registrarCode = getRandomExistingRegistrarCode();
        if (registrarCode != null) {
            LOGGER.info("registrar code: " + registrarCode);
            String responseXml = with().config(namespaceAwareXmlConfig()) //
                    .expect() //
                    .statusCode(200) //
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString)) //
                    .body(hasXPath("/c:response/c:digitalDocuments/@count", nsContext)) //
                    .when().get(buildUrl(registrarCode)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocuments");
            assertThat(xmlPath.getInt("@count"), greaterThanOrEqualTo(0));
        } else {
            LOGGER.warning("no registrars available");
        }
    }

    @Test
    public void okFormatJson() {
        String registrarCode = getRandomExistingRegistrarCode();
        if (registrarCode != null) {
            LOGGER.info("registrar code: " + registrarCode);
            String responseJson = with().config(namespaceAwareXmlConfig()).queryParam("format", "json") //
                    .expect() //
                    .statusCode(200) //
                    .contentType(ContentType.JSON)//
                    .when().get(buildUrl(registrarCode)).andReturn().asString();
            assertThat(from(responseJson).getInt("digitalDocuments.count"), greaterThanOrEqualTo(0));
        } else {
            LOGGER.warning("no registrars available");
        }
    }

    @Test
    public void okFormatNotSpecified() {
        String registrarCode = getRandomExistingRegistrarCode();
        if (registrarCode != null) {
            LOGGER.info("registrar code: " + registrarCode);
            with().config(namespaceAwareXmlConfig()) //
                    .expect() //
                    .statusCode(200) //
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString)) //
                    .body(hasXPath("/c:response/c:digitalDocuments/@count", nsContext)) //
                    .when().get(buildUrl(registrarCode));
        } else {
            LOGGER.warning("no registrars available");
        }
    }

    @Test
    public void okFormatEmpty() {
        String registrarCode = getRandomExistingRegistrarCode();
        if (registrarCode != null) {
            LOGGER.info("registrar code: " + registrarCode);
            with().config(namespaceAwareXmlConfig()).queryParam("format", "") //
                    .expect() //
                    .statusCode(400) //
                    .contentType(ContentType.HTML).when().get(buildUrl(registrarCode));
        } else {
            LOGGER.warning("no registrars available");
        }
    }

    @Test
    public void okFormatInvalid() {
        String registrarCode = getRandomExistingRegistrarCode();
        if (registrarCode != null) {
            LOGGER.info("registrar code: " + registrarCode);
            with().config(namespaceAwareXmlConfig()).queryParam("format", "pdf") //
                    .expect() //
                    .statusCode(400) //
                    .contentType(ContentType.HTML).when().get(buildUrl(registrarCode));
        } else {
            LOGGER.warning("no registrars available");
        }
    }

}
