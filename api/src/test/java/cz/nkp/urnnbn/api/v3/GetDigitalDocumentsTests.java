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
public class GetDigitalDocumentsTests extends ApiV3Tests {
    private static final Logger LOGGER = Logger.getLogger(GetDigitalDocumentsTests.class.getName());

    @BeforeSuite
    public void beforeSuite() {
        init();
    }

    @Test
    public void getDigitalDocumentsDigitalDocumentsOk() {
        String xml = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocuments/@count", nsContext))//
                .when().get("/registrars/" + Utils.urlEncodeReservedChars(getRandomRegistrarCode()) + "/digitalDocuments").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.digitalDocuments");
        assertThat(xmlPath.getInt("@count"), greaterThanOrEqualTo(0));
    }

    @Test
    public void getDigitalDocumentsDigitalDocumentRegistrarCodesInvalid() {
        for (String code : REGISTRAR_CODES_INVALID) {
            LOGGER.info("registrar code: " + code);
            String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                    .statusCode(400)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                    .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                    .when().get("/registrars/" + Utils.urlEncodeReservedChars(code) + "/digitalDocuments").andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
            Assert.assertEquals(xmlPath.getString("code"), "INVALID_REGISTRAR_CODE");
        }
    }

    @Test
    public void getDigitalDocumentsDigitalDocumentRegistrarCodesValid() {
        for (String code : REGISTRAR_CODES_VALID) {
            LOGGER.info("registrar code: " + code);
            String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response", nsContext))//
                    .when().get("/registrars/" + Utils.urlEncodeReservedChars(code) + "/digitalDocuments").andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
            Assert.assertTrue("UNKNOWN_REGISTRAR".equals(xmlPath.getString("error.code")) || xmlPath.get("digitalDocuments") != null);
        }
    }

}
