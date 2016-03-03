package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertThat;

import java.util.logging.Logger;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

/**
 * Tests for GET /api/v3/digitalInstances
 *
 */
public class GetDigitalInstances extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(GetDigitalInstances.class.getName());

    @BeforeClass
    public void beforeClass() {
        init();
    }

    private String buildUrl() {
        return "/digitalInstances";
    }

    @Test
    public void ok() {
        String responseXml = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalInstances/@count", nsContext))//
                .when().get(buildUrl()).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.digitalInstances");
        assertThat(xmlPath.getInt("@count"), greaterThanOrEqualTo(0));
    }

}
