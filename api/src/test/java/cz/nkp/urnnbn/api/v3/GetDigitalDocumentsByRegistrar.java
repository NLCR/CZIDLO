package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertThat;

import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.api.Utils;

/**
 * Tests for GET /api/v3/registrars/${REGISTRARS_CODE}/digitalDocuments
 *
 */
public class GetDigitalDocumentsByRegistrar extends ApiV3Tests {
    private static final Logger LOGGER = Logger.getLogger(GetDigitalDocumentsByRegistrar.class.getName());

    @BeforeSuite
    public void beforeSuite() {
        init();
    }

    @Test
    public void registrarCodeInvalid() {
        String registrarCode = Utils.getRandomItem(REGISTRAR_CODES_INVALID);
        LOGGER.info("registrar code: " + registrarCode);
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get("/registrars/" + Utils.urlEncodeReservedChars(registrarCode) + "/digitalDocuments")//
                .andReturn().asString();
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
                .when().get("/registrars/" + Utils.urlEncodeReservedChars(registrarCode) + "/digitalDocuments").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertTrue("UNKNOWN_REGISTRAR".equals(xmlPath.getString("error.code")) || xmlPath.get("digitalDocuments") != null);
        Assert.assertEquals(xmlPath.getString("code"), "UNKNOWN_REGISTRAR");
    }

    @Test
    public void ok() {
        String registrarCode = getRandomExistingRegistrarCode();
        if (registrarCode != null) {
            LOGGER.info("registrar code: " + registrarCode);
            String responseXml = with().config(namespaceAwareXmlConfig()) //
                    .expect() //
                    .statusCode(200) //
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString)) //
                    .body(hasXPath("/c:response/c:digitalDocuments/@count", nsContext)) //
                    .when().get("/registrars/" + Utils.urlEncodeReservedChars(registrarCode) + "/digitalDocuments")//
                    .andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocuments");
            assertThat(xmlPath.getInt("@count"), greaterThanOrEqualTo(0));
        } else {
            LOGGER.warning("no registrars available");
        }
    }

}
