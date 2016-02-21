package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertThat;

import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.core.dto.UrnNbn;

/**
 * Tests for POST /api/v3/registrars/${REGISTRAR_CODE}/urnNbnReservations
 *
 */
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
    public void postReservationsNotAuthenticated() {
        // check total reservations before
        UrnNbnReservations reservationsBefore = getUrnNbnReservations(REGISTRAR_CODE_OK);
        // try and reserve
        // TODO:APIv4: return xml
        // String responseXml =
        with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(401)//
                // .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                // .body(matchesXsd(responseXsdString))//
                // .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                // .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .when().post(HTTPS_API_URL + "/registrars/" + REGISTRAR_CODE_OK + "/urnNbnReservations")
        // .andReturn().asString()
        ;
        // XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        // Assert.assertEquals(xmlPath.getString("code"), "NOT_AUTHENTICATED");
        // check that no more reservations
        UrnNbnReservations reservationsAfter = getUrnNbnReservations(REGISTRAR_CODE_OK);
        assertThat(reservationsAfter.totalReserved, equalTo(reservationsBefore.totalReserved));
    }

    @Test
    public void postReservationsNotAuthorized() {
        // check total reservations before
        UrnNbnReservations reservationsBefore = getUrnNbnReservations(REGISTRAR_CODE_OK);
        // try and reserve
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(401)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .when().post(HTTPS_API_URL + "/registrars/" + REGISTRAR_CODE_NO_ACCESS_RIGHTS + "/urnNbnReservations").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "NOT_AUTHORIZED");
        // check that no more reservations
        UrnNbnReservations reservationsAfter = getUrnNbnReservations(REGISTRAR_CODE_OK);
        assertThat(reservationsAfter.totalReserved, equalTo(reservationsBefore.totalReserved));
    }

    @Test
    public void postReservationsWithoutSize() {
        // check total reserved before
        UrnNbnReservations reservationsBefore = getUrnNbnReservations(REGISTRAR_CODE_OK);
        // reserve
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(201)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbnReservation/c:urnNbn", nsContext))//
                .when().post(HTTPS_API_URL + "/registrars/" + REGISTRAR_CODE_OK + "/urnNbnReservations").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbnReservation");
        int nowReserved = xmlPath.getInt("urnNbn.size()");
        assertThat(nowReserved, equalTo(reservationsBefore.defaultReservationSize));
        for (int i = 0; i < nowReserved; i++) {
            UrnNbn.valueOf(xmlPath.getString("urnNbn[" + i + "]"));
        }
        // check total reserved after reservations
        UrnNbnReservations reservationsAfter = getUrnNbnReservations(REGISTRAR_CODE_OK);
        assertThat(reservationsAfter.totalReserved, equalTo(reservationsBefore.totalReserved + nowReserved));
    }

    @Test
    public void postReservationsWithSize() {
        // check total reservations before
        UrnNbnReservations reservationsBefore = getUrnNbnReservations(REGISTRAR_CODE_OK);
        // reserve
        int size = 3;
        String xml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password).queryParam("size", size)//
                .expect()//
                .statusCode(201)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbnReservation/c:urnNbn", nsContext))//
                .when().post(HTTPS_API_URL + "/registrars/" + REGISTRAR_CODE_OK + "/urnNbnReservations").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.urnNbnReservation");
        int nowReserved = xmlPath.getInt("urnNbn.size()");
        for (int i = 0; i < nowReserved; i++) {
            UrnNbn.valueOf(xmlPath.getString("urnNbn[" + i + "]"));
        }
        Assert.assertEquals(nowReserved, size);
        // check that no more reservations
        UrnNbnReservations reservationsAfter = getUrnNbnReservations(REGISTRAR_CODE_OK);
        assertThat(reservationsAfter.totalReserved, equalTo(reservationsBefore.totalReserved + nowReserved));
    }

    @Test
    public void postReservationsSizeNegative() {
        // check total reservations before
        UrnNbnReservations reservationsBefore = getUrnNbnReservations(REGISTRAR_CODE_OK);
        // try and reserve
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
        // check that no more reservations
        UrnNbnReservations reservationsAfter = getUrnNbnReservations(REGISTRAR_CODE_OK);
        assertThat(reservationsAfter.totalReserved, equalTo(reservationsBefore.totalReserved));
    }

    @Test
    public void postReservationsSizeToHigh() {
        // check total reservations before
        UrnNbnReservations reservationsBefore = getUrnNbnReservations(REGISTRAR_CODE_OK);
        // try and reserve
        int size = reservationsBefore.maxReservationSize + 1;
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password).queryParam("size", size)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .when().post(HTTPS_API_URL + "/registrars/" + REGISTRAR_CODE_OK + "/urnNbnReservations").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_QUERY_PARAM_VALUE");
        // check that no more reservations
        UrnNbnReservations reservationsAfter = getUrnNbnReservations(REGISTRAR_CODE_OK);
        assertThat(reservationsAfter.totalReserved, equalTo(reservationsBefore.totalReserved));
    }

    @Test
    public void postReservationsSizeNan() {
        // check total reservations before
        UrnNbnReservations reservationsBefore = getUrnNbnReservations(REGISTRAR_CODE_OK);
        // try and reserve
        String size = "blabla";
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password).queryParam("size", size)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .when().post(HTTPS_API_URL + "/registrars/" + REGISTRAR_CODE_OK + "/urnNbnReservations").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_QUERY_PARAM_VALUE");
        // check that no more reservations
        UrnNbnReservations reservationsAfter = getUrnNbnReservations(REGISTRAR_CODE_OK);
        assertThat(reservationsAfter.totalReserved, equalTo(reservationsBefore.totalReserved));
    }

    @Test
    public void postReservationsSizeEmpty() {
        // check total reservations before
        UrnNbnReservations reservationsBefore = getUrnNbnReservations(REGISTRAR_CODE_OK);
        // try and reserve
        String size = "";
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password).queryParam("size", size)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .when().post(HTTPS_API_URL + "/registrars/" + REGISTRAR_CODE_OK + "/urnNbnReservations").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_QUERY_PARAM_VALUE");
        // check that no more reservations
        UrnNbnReservations reservationsAfter = getUrnNbnReservations(REGISTRAR_CODE_OK);
        assertThat(reservationsAfter.totalReserved, equalTo(reservationsBefore.totalReserved));
    }

}
