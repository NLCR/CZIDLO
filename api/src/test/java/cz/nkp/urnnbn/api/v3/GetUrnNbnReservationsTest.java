package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;

import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.api.Utils;

/**
 * Tests for GET /api/v3/registrars/${REGISTRAR_CODE}/urnNbnReservations
 *
 */
public class GetUrnNbnReservationsTest extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(GetUrnNbnReservationsTest.class.getName());

    private final String REGISTRAR_CODE_OK = "tst01";
    private final String REGISTRAR_CODE_UNKNOWN = "xxx999";
    private final String REGISTRAR_CODE_INVALID = "xxx_999";

    @BeforeSuite
    public void beforeSuite() {
        init();
    }

    @Test
    public void getUrnNbnReservations() {
        String xml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbnReservations/c:maxReservationSize", nsContext))//
                .body(hasXPath("/c:response/c:urnNbnReservations/c:defaultReservationSize", nsContext))//
                .body(hasXPath("/c:response/c:urnNbnReservations/c:reserved", nsContext))//
                .body(hasXPath("/c:response/c:urnNbnReservations/c:reserved/@totalSize", nsContext))//
                .when().get("/registrars/" + Utils.urlEncodeReservedChars(REGISTRAR_CODE_OK) + "/urnNbnReservations").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.urnNbnReservations");
        // reservation size
        int maxReservationSize = xmlPath.getInt("maxReservationSize");
        int defaultReservationSize = xmlPath.getInt("defaultReservationSize");
        assertThat(maxReservationSize, greaterThanOrEqualTo(0));
        assertThat(defaultReservationSize, lessThanOrEqualTo(maxReservationSize));
        // reservations total/returned
        int reservedTotal = xmlPath.getInt("reserved.@totalSize");
        int reservedReturned = xmlPath.getInt("reserved.urnNbn.size()");
        assertThat(reservedTotal, greaterThanOrEqualTo(reservedReturned));
        assertThat(reservedReturned, lessThanOrEqualTo(reservedTotal));
        assertThat(reservedReturned, lessThanOrEqualTo(MAX_URN_NBN_RESERVATIONS_RETURNED));
    }

    @Test
    public void getUrnNbnReservationsInvalidRegistrar() {
        // TODO: properly test registrarCodes
        String xml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().get("/registrars/" + Utils.urlEncodeReservedChars(REGISTRAR_CODE_INVALID) + "/urnNbnReservations").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_REGISTRAR_CODE");
    }

    @Test
    public void getUrnNbnReservationsUnknownRegistrar() {
        String xml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().get("/registrars/" + Utils.urlEncodeReservedChars(REGISTRAR_CODE_UNKNOWN) + "/urnNbnReservations").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "UNKNOWN_REGISTRAR");
    }

}
