package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertThat;

import java.util.logging.Logger;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

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
    public void getDigitalDocumentsDigitalDocuments() {
        String xml = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocuments/@count", nsContext))//
                .when().get("/registrars/" + getRandomRegistrarCode() + "/digitalDocuments").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.digitalDocuments");
        assertThat(xmlPath.getInt("@count"), greaterThanOrEqualTo(0));
    }

}
