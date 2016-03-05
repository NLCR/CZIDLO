package cz.nkp.urnnbn.api.v4;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertThat;

import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.api.Utils;
import cz.nkp.urnnbn.api.pojo.UrnNbnReservations;
import cz.nkp.urnnbn.core.dto.UrnNbn;

/**
 * Tests for POST /api/v4/registrars/${REGISTRAR_CODE}/urnNbnReservations
 *
 */
public class PostUrnNbnReservationByRegistrar extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(PostUrnNbnReservationByRegistrar.class.getName());

    @BeforeClass
    public void beforeClass() {
        init();
    }

    private String buildUrl(String registrarCode) {
        return HTTPS_API_URL + "/registrars/" + Utils.urlEncodeReservedChars(registrarCode) + "/urnNbnReservations";
    }

    @Test
    public void registrarCodeInvalid() {
        String registrarCode = Utils.getRandomItem(REGISTRAR_CODES_INVALID);
        LOGGER.info("registrar code: " + registrarCode);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(registrarCode)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_REGISTRAR_CODE");
    }

    @Test
    public void registrarCodeValidUnknown() {
        String registrarCode = Utils.getRandomItem(REGISTRAR_CODES_VALID);
        LOGGER.info("registrar code: " + registrarCode);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(registrarCode)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "UNKNOWN_REGISTRAR");
    }

    @Test
    public void notAuthenticated() {
        String registrarCode = REGISTRAR;
        LOGGER.info("registrar code: " + registrarCode);
        // check total reservations before
        UrnNbnReservations reservationsBefore = getUrnNbnReservations(registrarCode);
        // try and reserve
        // TODO:APIv4: return xml
        // String responseXml =
        with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(401)//
                // .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                // .body(matchesXsd(responseXsdString))//
                // .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(registrarCode)).andReturn().asString();
        // XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        // Assert.assertEquals(xmlPath.getString("code"), "NOT_AUTHENTICATED");
        // check that no more reservations
        UrnNbnReservations reservationsAfter = getUrnNbnReservations(registrarCode);
        assertThat(reservationsAfter.totalReserved, equalTo(reservationsBefore.totalReserved));
    }

    @Test
    public void notAuthorized() {
        String registrarCode = REGISTRAR;
        LOGGER.info("registrar code: " + registrarCode);
        // check total reservations before
        UrnNbnReservations reservationsBefore = getUrnNbnReservations(registrarCode);
        // try and reserve
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_NO_RIGHTS.login, USER_NO_RIGHTS.password)//
                .expect()//
                .statusCode(401)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(registrarCode)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "NOT_AUTHORIZED");
        // check that no more reservations created
        UrnNbnReservations reservationsAfter = getUrnNbnReservations(registrarCode);
        assertThat(reservationsAfter.totalReserved, equalTo(reservationsBefore.totalReserved));
    }

    @Test
    public void sizeNan() {
        String registrarCode = REGISTRAR;
        LOGGER.info("registrar code: " + registrarCode);
        // check total reservations before
        UrnNbnReservations reservationsBefore = getUrnNbnReservations(registrarCode);
        // try and reserve
        String size = "blabla";
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password).queryParam("size", size)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(registrarCode)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_QUERY_PARAM_VALUE");
        // check that no more reservations
        UrnNbnReservations reservationsAfter = getUrnNbnReservations(registrarCode);
        assertThat(reservationsAfter.totalReserved, equalTo(reservationsBefore.totalReserved));
    }

    @Test
    public void sizeNegative() {
        String registrarCode = REGISTRAR;
        LOGGER.info("registrar code: " + registrarCode);
        // check total reservations before
        UrnNbnReservations reservationsBefore = getUrnNbnReservations(registrarCode);
        // try and reserve
        int size = -1;
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password).queryParam("size", size)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(registrarCode)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_QUERY_PARAM_VALUE");
        // check that no more reservations created
        UrnNbnReservations reservationsAfter = getUrnNbnReservations(registrarCode);
        assertThat(reservationsAfter.totalReserved, equalTo(reservationsBefore.totalReserved));
    }

    @Test
    public void sizeToBig() {
        String registrarCode = REGISTRAR;
        LOGGER.info("registrar code: " + registrarCode);
        // check total reservations before
        UrnNbnReservations reservationsBefore = getUrnNbnReservations(registrarCode);
        // try and reserve
        int size = reservationsBefore.maxReservationSize + 1;
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password).queryParam("size", size)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(registrarCode)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_QUERY_PARAM_VALUE");
        // check that no more reservations
        UrnNbnReservations reservationsAfter = getUrnNbnReservations(registrarCode);
        assertThat(reservationsAfter.totalReserved, equalTo(reservationsBefore.totalReserved));
    }

    @Test
    public void sizeEmpty() {
        String registrarCode = REGISTRAR;
        LOGGER.info("registrar code: " + registrarCode);
        // check total reservations before
        UrnNbnReservations reservationsBefore = getUrnNbnReservations(registrarCode);
        // try and reserve
        String size = "";
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password).queryParam("size", size)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().post(buildUrl(registrarCode)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_QUERY_PARAM_VALUE");
        // check that no more reservations
        UrnNbnReservations reservationsAfter = getUrnNbnReservations(registrarCode);
        assertThat(reservationsAfter.totalReserved, equalTo(reservationsBefore.totalReserved));
    }

    @Test
    public void sizeNotSpecified() {
        String registrarCode = REGISTRAR;
        LOGGER.info("registrar code: " + registrarCode);
        // check total reserved before
        UrnNbnReservations reservationsBefore = getUrnNbnReservations(registrarCode);
        // reserve
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(201)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbnReservation/c:urnNbn", nsContext))//
                .when().post(buildUrl(registrarCode)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbnReservation");
        int nowReserved = xmlPath.getInt("urnNbn.size()");
        assertThat(nowReserved, equalTo(reservationsBefore.defaultReservationSize));
        for (int i = 0; i < nowReserved; i++) {
            UrnNbn.valueOf(xmlPath.getString("urnNbn[" + i + "]"));
        }
        // check total reserved after reservations
        UrnNbnReservations reservationsAfter = getUrnNbnReservations(registrarCode);
        assertThat(reservationsAfter.totalReserved, equalTo(reservationsBefore.totalReserved + nowReserved));
    }

    @Test
    public void sizeOk() {
        String registrarCode = REGISTRAR;
        LOGGER.info("registrar code: " + registrarCode);
        // check total reservations before
        UrnNbnReservations reservationsBefore = getUrnNbnReservations(registrarCode);
        // reserve
        int size = 3;
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password).queryParam("size", size)//
                .expect()//
                .statusCode(201)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbnReservation/c:urnNbn", nsContext))//
                .when().post(buildUrl(registrarCode)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbnReservation");
        int nowReserved = xmlPath.getInt("urnNbn.size()");
        for (int i = 0; i < nowReserved; i++) {
            UrnNbn.valueOf(xmlPath.getString("urnNbn[" + i + "]"));
        }
        Assert.assertEquals(nowReserved, size);
        // check that no more reservations
        UrnNbnReservations reservationsAfter = getUrnNbnReservations(registrarCode);
        assertThat(reservationsAfter.totalReserved, equalTo(reservationsBefore.totalReserved + nowReserved));
    }

    /**
     * Even though registration mode BY_RESERVATION is disabled, registrar can stil reserve urn:nbns.
     * 
     * @see https://github.com/NLCR/CZIDLO/issues/141
     */
    @Test
    public void registrationModeByReservationDisabled() {
        String registrarCode = REGISTRAR_NO_MODES_ENABLED;
        LOGGER.info("registrar code: " + registrarCode);
        // check total reservations before
        UrnNbnReservations reservationsBefore = getUrnNbnReservations(registrarCode);
        // reserve
        int size = 3;
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password).queryParam("size", size)//
                .expect()//
                .statusCode(201)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbnReservation/c:urnNbn", nsContext))//
                .when().post(buildUrl(registrarCode)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbnReservation");
        int nowReserved = xmlPath.getInt("urnNbn.size()");
        for (int i = 0; i < nowReserved; i++) {
            UrnNbn.valueOf(xmlPath.getString("urnNbn[" + i + "]"));
        }
        Assert.assertEquals(nowReserved, size);
        // check that no more reservations
        UrnNbnReservations reservationsAfter = getUrnNbnReservations(registrarCode);
        assertThat(reservationsAfter.totalReserved, equalTo(reservationsBefore.totalReserved + nowReserved));
    }

}
