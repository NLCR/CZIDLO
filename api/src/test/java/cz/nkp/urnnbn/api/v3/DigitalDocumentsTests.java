package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.expect;
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

public class DigitalDocumentsTests extends ApiV3Tests {
    private static final Logger LOGGER = Logger.getLogger(DigitalDocumentsTests.class.getName());

    @BeforeSuite
    public void beforeSuite() {
        init();
    }

    @Test
    public void getDigitalDocumentsStatusCode() {
        expect().statusCode(200).when().get("/registrars/" + getRandomRegistrarCode() + "/digitalDocuments");
    }

    @Test
    public void getDigitalDocumentsContentType() {
        expect().contentType(ContentType.XML).when().get("/registrars/" + getRandomRegistrarCode() + "/digitalDocuments");
    }

    @Test
    public void getDigitalDocumentsValidByXsd() {
        expect().body(matchesXsd(responseXsdString)).when().get("/registrars/" + getRandomRegistrarCode() + "/digitalDocuments");
    }

    @Test
    public void getDigitalInstancesData() {
        String xml = with().config(namespaceAwareXmlConfig()).expect()//
                .body(hasXPath("/c:response/c:digitalDocuments", nsContext))//
                .body(hasXPath("/c:response/c:digitalDocuments/@count", nsContext))//
                .when().get("/registrars/" + getRandomRegistrarCode() + "/digitalDocuments").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).using(namespaceAwareXmlpathConfig()).setRoot("c:response.c:digitalDocuments");
        assertThat(xmlPath.getInt("@count"), greaterThanOrEqualTo(0));
    }

}
