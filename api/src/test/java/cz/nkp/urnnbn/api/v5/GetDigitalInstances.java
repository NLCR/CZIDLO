package cz.nkp.urnnbn.api.v5;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;
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
 * Tests for GET /api/v5/digitalInstances
 *
 */
public class GetDigitalInstances extends ApiV5Tests {

    private static final Logger LOGGER = Logger.getLogger(GetDigitalInstances.class.getName());

    @BeforeClass
    public void beforeClass() {
        init();
    }

    private String buildUrl() {
        return "/digitalInstances";
    }

    @Test
    public void okFormatXml() {
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("format", "xml") //
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalInstances/@count", nsContext))//
                .when().get(buildUrl()).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.digitalInstances");
        assertThat(xmlPath.getInt("@count"), greaterThanOrEqualTo(0));
    }

    @Test
    public void okFormatJson() {
        String response = with().config(namespaceAwareXmlConfig()).queryParam("format", "json") //
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.JSON)//
                .when().get(buildUrl()).andReturn().asString();
        assertThat(from(response).getInt("digitalInstances.count"), greaterThanOrEqualTo(0));
    }

    @Test
    public void okFormatNotSpecified() {
        with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalInstances/@count", nsContext))//
                .when().get(buildUrl());
    }

    @Test
    public void okFormatEmpty() {
        with().config(namespaceAwareXmlConfig()).queryParam("format", "") //
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.HTML)//
                .when().get(buildUrl());
    }

    @Test
    public void okFormatInvalid() {
        with().config(namespaceAwareXmlConfig()).queryParam("format", "pdf") //
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.HTML)//
                .when().get(buildUrl());
    }

}
