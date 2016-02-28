package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.api.Utils;

/**
 * Tests for GET /api/v3/registrars/${REGISTRAR_CODE}/urnNbnReservations
 *
 */
public class GetUrnNbnReservationsByRegistrar extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(GetUrnNbnReservationsByRegistrar.class.getName());

    @BeforeClass
    public void beforeClass() {
        init();
    }

    private String buildUrl(String registrarCode) {
        return "/registrars/" + Utils.urlEncodeReservedChars(registrarCode) + "/urnNbnReservations";
    }

    @Test
    public void registrarCodeInvalid() {
        String registrarCode = Utils.getRandomItem(REGISTRAR_CODES_INVALID);
        LOGGER.info("registrar code: " + registrarCode);
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(registrarCode)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_REGISTRAR_CODE");
    }

    @Test
    public void registrarCodeValidUnknown() {
        String registrarCode = Utils.getRandomItem(REGISTRAR_CODES_VALID);
        LOGGER.info("registrar code: " + registrarCode);
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(registrarCode)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "UNKNOWN_REGISTRAR");
    }

    @Test
    public void ok() {
        // make sure that at least some reservations exist
        UrnNbnReservations reservations = getUrnNbnReservations(REGISTRAR);
        if (reservations.totalReserved == 0) {
            reserveUrnNbns(REGISTRAR, USER);
        }
        // check all registrars' reservations
        List<String> registrarCodes = getAllRegistrarCodes();
        if (registrarCodes.isEmpty()) {
            LOGGER.warning("no registrars available");
        } else {
            for (String registrarCode : registrarCodes) {
                LOGGER.info("registrar code: " + registrarCode);
                String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                        .statusCode(200)//
                        .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                        .body(hasXPath("/c:response/c:urnNbnReservations/c:maxReservationSize", nsContext))//
                        .body(hasXPath("/c:response/c:urnNbnReservations/c:defaultReservationSize", nsContext))//
                        .body(hasXPath("/c:response/c:urnNbnReservations/c:reserved", nsContext))//
                        .body(hasXPath("/c:response/c:urnNbnReservations/c:reserved/@totalSize", nsContext))//
                        .when().get(buildUrl(registrarCode)).andReturn().asString();
                XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbnReservations");
                // reservation size
                int maxReservationSize = xmlPath.getInt("maxReservationSize");
                int defaultReservationSize = xmlPath.getInt("defaultReservationSize");
                assertThat(maxReservationSize, greaterThanOrEqualTo(0));
                assertThat(defaultReservationSize, lessThanOrEqualTo(maxReservationSize));
                // reservations total/returned
                int reservedTotal = xmlPath.getInt("reserved.@totalSize");
                int reservedReturned = xmlPath.getInt("reserved.urnNbn.size()");
                assertThat(reservedReturned, greaterThanOrEqualTo(0));
                assertThat(reservedTotal, greaterThanOrEqualTo(reservedReturned));
                assertThat(reservedReturned, lessThanOrEqualTo(MAX_URN_NBN_RESERVATIONS_RETURNED));
            }
        }
    }

}
