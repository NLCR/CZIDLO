package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertThat;

import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.core.dto.UrnNbn;

public class PostUrnNbnReservationTests extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(PostUrnNbnReservationTests.class.getName());

    private static final Credentials USER = new Credentials("martin", "i0oEhu");
    private static final String REGISTRAR_CODE_OK = "tst01";
    private static final String REGISTRAR_CODE_NO_ACCESS_RIGHTS = "anl001";

    @BeforeSuite
    public void beforeSuite() {
        init();
    }

    @Test
    public void postReservations() {
        getUrnNbnReservations(REGISTRAR_CODE_OK);
        String xml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(201)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbnReservation/c:urnNbn", nsContext))//
                .when().post(HTTPS_API_URL + "/registrars/" + REGISTRAR_CODE_OK + "/urnNbnReservations").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.urnNbnReservation");
        int urnCount = xmlPath.getInt("urnNbn.size()");
        assertThat(urnCount, greaterThan(0));
        for (int i = 0; i < urnCount; i++) {
            UrnNbn.valueOf(xmlPath.getString("urnNbn[" + i + "]"));
        }
    }

    @Test
    public void postReservationsWithSize() {
        int size = 3;
        String xml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password).queryParam("size", size)//
                .expect()//
                .statusCode(201)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbnReservation/c:urnNbn", nsContext))//
                .when().post(HTTPS_API_URL + "/registrars/" + REGISTRAR_CODE_OK + "/urnNbnReservations").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.urnNbnReservation");
        int responseSize = xmlPath.getInt("urnNbn.size()");
        for (int i = 0; i < responseSize; i++) {
            UrnNbn.valueOf(xmlPath.getString("urnNbn[" + i + "]"));
        }
        Assert.assertEquals(responseSize, size);
    }

    @Test
    public void postReservationsSizeNegative() {
        int size = -1;
        String xml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password).queryParam("size", size)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .when().post(HTTPS_API_URL + "/registrars/" + REGISTRAR_CODE_OK + "/urnNbnReservations").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_QUERY_PARAM_VALUE");
    }

    @Test
    public void postReservationsSizeNan() {
        String size = "blabla";
        String xml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password).queryParam("size", size)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .when().post(HTTPS_API_URL + "/registrars/" + REGISTRAR_CODE_OK + "/urnNbnReservations").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_QUERY_PARAM_VALUE");
    }

    @Test
    public void postReservationsSizeEmpty() {
        String size = "";
        String xml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password).queryParam("size", size)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .when().post(HTTPS_API_URL + "/registrars/" + REGISTRAR_CODE_OK + "/urnNbnReservations").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_QUERY_PARAM_VALUE");
    }

    @Test
    public void postReservationsNotAuthenticated() {
        // TODO: check this out, seems to be the only error when response is not xml with error code
        with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(401)//
                .when().post(HTTPS_API_URL + "/registrars/" + REGISTRAR_CODE_OK + "/urnNbnReservations");
        // TODO: check that no change happened
    }

    @Test
    public void postReservationsNotAuthorized() {
        String xml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(401)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .when().post(HTTPS_API_URL + "/registrars/" + REGISTRAR_CODE_NO_ACCESS_RIGHTS + "/urnNbnReservations").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "NOT_AUTHORIZED");
        // TODO: check that no change happened
    }

}
