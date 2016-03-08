package cz.nkp.urnnbn.api.v4;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertThat;

import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

/**
 * Tests for GET /api/v4/digitalInstances
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
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("format", "json") //
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.JSON)//
                .when().get(buildUrl()).andReturn().asString();
        assertThat(from(responseXml).getInt("digitalInstances.count"), greaterThanOrEqualTo(0));
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
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("format", "") //
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl()).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "ILLEGAL_FORMAT");
    }

    @Test
    public void okFormatInvalid() {
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("format", "pdf") //
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl()).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "ILLEGAL_FORMAT");
    }

}
