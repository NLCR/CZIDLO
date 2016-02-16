package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.with;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
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

public class UrnNbnReservationTests extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(UrnNbnReservationTests.class.getName());

    private static final String TEST_USER_LOGIN = "martin";
    private static final String TEST_USER_PASSWORD = "i0oEhu";
    private static final String REGISTRAR_CODE_OK = "tst01";
    private static final String REGISTRAR_CODE_UNKNOWN = "xxx999";
    private static final String REGISTRAR_CODE_INVALID = "xxx_999";

    @BeforeSuite
    public void beforeSuite() {
        init();
    }

    @Test
    public void getReservationsStatusCode() {
        expect().statusCode(200).when().get("/registrars/" + REGISTRAR_CODE_OK + "/urnNbnReservations");
    }

    @Test
    public void getReservationsContentType() {
        expect().contentType(ContentType.XML).when().get("/registrars/" + REGISTRAR_CODE_OK + "/urnNbnReservations");
    }

    @Test
    public void getReservationsData() {
        String xml = with().config(namespaceAwareXmlConfig()).expect()//
                .body(hasXPath("/c:response/c:urnNbnReservations/c:maxReservationSize", nsContext))//
                .body(hasXPath("/c:response/c:urnNbnReservations/c:defaultReservationSize", nsContext))//
                .body(hasXPath("/c:response/c:urnNbnReservations/c:reserved", nsContext))//
                .body(hasXPath("/c:response/c:urnNbnReservations/c:reserved/@totalSize", nsContext))//
                .when().get("/registrars/" + REGISTRAR_CODE_OK + "/urnNbnReservations").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).using(namespaceAwareXmlpathConfig()).setRoot("c:response.c:urnNbnReservations");
        assertThat(xmlPath.getInt("c:maxReservationSize"), greaterThanOrEqualTo(0));
        assertThat(xmlPath.getInt("c:defaultReservationSize"), greaterThanOrEqualTo(0));
        assertThat(xmlPath.getInt("c:reserved.@totalSize"), greaterThanOrEqualTo(0));
    }

    @Test
    public void getReservationsInvalidRegistrar() {
        String xml = with().config(namespaceAwareXmlConfig()).expect()//
                .contentType(ContentType.XML).statusCode(400).body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().get("/registrars/" + REGISTRAR_CODE_INVALID + "/urnNbnReservations").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).using(namespaceAwareXmlpathConfig()).setRoot("c:response.c:error");
        Assert.assertEquals(xmlPath.getString("c:code"), "INVALID_REGISTRAR_CODE");
    }

    @Test
    public void getReservationsUnknownRegistrar() {
        String xml = with().config(namespaceAwareXmlConfig()).expect()//
                .contentType(ContentType.XML).statusCode(404).body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().get("/registrars/" + REGISTRAR_CODE_UNKNOWN + "/urnNbnReservations").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).using(namespaceAwareXmlpathConfig()).setRoot("c:response.c:error");
        Assert.assertEquals(xmlPath.getString("c:code"), "UNKNOWN_REGISTRAR");
    }

    @Test
    public void postReservations() {
        String xml = with().config(namespaceAwareXmlConfig()).auth().basic(TEST_USER_LOGIN, TEST_USER_PASSWORD)//
                .expect()//
                .contentType(ContentType.XML).statusCode(201)//
                .body(hasXPath("/c:response/c:urnNbnReservation/c:urnNbn", nsContext))//
                .when().post(HTTPS_API_URL + "/registrars/" + REGISTRAR_CODE_OK + "/urnNbnReservations").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).using(namespaceAwareXmlpathConfig()).setRoot("c:response.c:urnNbnReservation");
        int responseSize = xmlPath.getInt("c:urnNbn.size()");
        assertThat(responseSize, greaterThan(0));
        for (int i = 0; i < responseSize; i++) {
            UrnNbn.valueOf(xmlPath.getString("urnNbn[" + i + "]"));
        }
    }

    @Test
    public void postReservationsWithSize() {
        int size = 3;
        String xml = with().config(namespaceAwareXmlConfig()).auth().basic(TEST_USER_LOGIN, TEST_USER_PASSWORD).queryParam("size", size)//
                .expect()//
                .contentType(ContentType.XML).statusCode(201)//
                .body(hasXPath("/c:response/c:urnNbnReservation/c:urnNbn", nsContext))//
                .when().post(HTTPS_API_URL + "/registrars/" + REGISTRAR_CODE_OK + "/urnNbnReservations").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).using(namespaceAwareXmlpathConfig()).setRoot("c:response.c:urnNbnReservation");
        int responseSize = xmlPath.getInt("c:urnNbn.size()");
        for (int i = 0; i < responseSize; i++) {
            UrnNbn.valueOf(xmlPath.getString("urnNbn[" + i + "]"));
        }
        Assert.assertEquals(responseSize, size);
    }

    @Test
    public void postReservationsSizeNegative() {
        int size = -1;
        String xml = with().config(namespaceAwareXmlConfig()).auth().basic(TEST_USER_LOGIN, TEST_USER_PASSWORD).queryParam("size", size)//
                .expect()//
                .contentType(ContentType.XML).statusCode(400)//
                .when().post(HTTPS_API_URL + "/registrars/" + REGISTRAR_CODE_OK + "/urnNbnReservations").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).using(namespaceAwareXmlpathConfig()).setRoot("c:response.c:error");
        Assert.assertEquals(xmlPath.getString("c:code"), "INVALID_QUERY_PARAM_VALUE");
    }

    @Test
    public void postReservationsSizeNan() {
        String size = "blabla";
        String xml = with().config(namespaceAwareXmlConfig()).auth().basic(TEST_USER_LOGIN, TEST_USER_PASSWORD).queryParam("size", size)//
                .expect()//
                .contentType(ContentType.XML).statusCode(400)//
                .when().post(HTTPS_API_URL + "/registrars/" + REGISTRAR_CODE_OK + "/urnNbnReservations").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).using(namespaceAwareXmlpathConfig()).setRoot("c:response.c:error");
        Assert.assertEquals(xmlPath.getString("c:code"), "INVALID_QUERY_PARAM_VALUE");
    }

    @Test
    public void postReservationsSizeEmpty() {
        String size = "";
        String xml = with().config(namespaceAwareXmlConfig()).auth().basic(TEST_USER_LOGIN, TEST_USER_PASSWORD).queryParam("size", size)//
                .expect()//
                .contentType(ContentType.XML).statusCode(400)//
                .when().post(HTTPS_API_URL + "/registrars/" + REGISTRAR_CODE_OK + "/urnNbnReservations").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).using(namespaceAwareXmlpathConfig()).setRoot("c:response.c:error");
        Assert.assertEquals(xmlPath.getString("c:code"), "INVALID_QUERY_PARAM_VALUE");
    }

}
