package cz.nkp.urnnbn.api.v5;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.path.xml.XmlPath;
import cz.nkp.urnnbn.api.Utils;
import org.joda.time.DateTime;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.logging.Logger;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.*;

/**
 * Tests for GET /api/v5/registrars
 *
 */
public class GetRegistrars extends ApiV5Tests {

    private static final Logger LOGGER = Logger.getLogger(GetRegistrars.class.getName());

    @BeforeClass
    public void beforeClass() {
        init();
    }

    private String buildUrl() {
        return "/registrars";
    }

    @Test
    public void noQueryParams() {
        // digitalLibraries=false, catalogs=false by default
        String responseXml = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrars", nsContext))//
                .when().get(buildUrl()).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.registrars");
        int size = xmlPath.getInt("registrar.size()");
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                assertTrue(DateTime.parse(xmlPath.getString(String.format("registrar[%d].created", i))).isBeforeNow());
                // digital libraries and catalogs not present
                assertEquals(0, xmlPath.getList(String.format("registrar[%d].digitalLibraries", i)).size());
                assertEquals(0, xmlPath.getList(String.format("registrar[%d].catalogs", i)).size());
                // all modes defined and have boolean values
                Utils.booleanValue(xmlPath.getString(String.format("registrar[%d].registrationModes.mode.findAll{it.@name =='%s'}.@enabled", i,
                        "BY_RESOLVER")));
                Utils.booleanValue(xmlPath.getString(String.format("registrar[%d].registrationModes.mode.findAll{it.@name =='%s'}.@enabled", i,
                        "BY_REGISTRAR")));
                Utils.booleanValue(xmlPath.getString(String.format("registrar[%d].registrationModes.mode.findAll{it.@name =='%s'}.@enabled", i,
                        "BY_RESERVATION")));
            }
        } else {
            LOGGER.warning("no registrars found");
        }
    }

    @Test
    public void withDigitalLibraries() {
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("digitalLibraries", "true")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrars", nsContext))//
                .when().get(buildUrl()).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.registrars");
        int size = xmlPath.getInt("registrar.size()");
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                assertTrue(DateTime.parse(xmlPath.getString(String.format("registrar[%d].created", i))).isBeforeNow());
                // digital libraries present
                assertEquals(1, xmlPath.getList(String.format("registrar[%d].digitalLibraries", i)).size());
                // all modes defined and have boolean values
                Utils.booleanValue(xmlPath.getString(String.format("registrar[%d].registrationModes.mode.findAll{it.@name =='%s'}.@enabled", i,
                        "BY_RESOLVER")));
                Utils.booleanValue(xmlPath.getString(String.format("registrar[%d].registrationModes.mode.findAll{it.@name =='%s'}.@enabled", i,
                        "BY_REGISTRAR")));
                Utils.booleanValue(xmlPath.getString(String.format("registrar[%d].registrationModes.mode.findAll{it.@name =='%s'}.@enabled", i,
                        "BY_RESERVATION")));
            }
        } else {
            LOGGER.warning("no registrars found");
        }
    }

    @Test
    public void withoutDigitalLibraries() {
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("digitalLibraries", "false")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrars", nsContext))//
                .when().get(buildUrl()).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.registrars");
        int size = xmlPath.getInt("registrar.size()");
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                assertTrue(DateTime.parse(xmlPath.getString(String.format("registrar[%d].created", i))).isBeforeNow());
                // digital libraries not present
                assertEquals(0, xmlPath.getList(String.format("registrar[%d].digitalLibraries", i)).size());
                // all modes defined and have boolean values
                Utils.booleanValue(xmlPath.getString(String.format("registrar[%d].registrationModes.mode.findAll{it.@name =='%s'}.@enabled", i,
                        "BY_RESOLVER")));
                Utils.booleanValue(xmlPath.getString(String.format("registrar[%d].registrationModes.mode.findAll{it.@name =='%s'}.@enabled", i,
                        "BY_REGISTRAR")));
                Utils.booleanValue(xmlPath.getString(String.format("registrar[%d].registrationModes.mode.findAll{it.@name =='%s'}.@enabled", i,
                        "BY_RESERVATION")));
            }
        } else {
            LOGGER.warning("no registrars found");
        }
    }

    @Test
    public void withCatalogs() {
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("catalogs", "true")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrars", nsContext))//
                .when().get(buildUrl()).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.registrars");
        int size = xmlPath.getInt("registrar.size()");
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                assertTrue(DateTime.parse(xmlPath.getString(String.format("registrar[%d].created", i))).isBeforeNow());
                // catalogs present
                assertEquals(1, xmlPath.getList(String.format("registrar[%d].catalogs", i)).size());
                // all modes defined and have boolean values
                Utils.booleanValue(xmlPath.getString(String.format("registrar[%d].registrationModes.mode.findAll{it.@name =='%s'}.@enabled", i,
                        "BY_RESOLVER")));
                Utils.booleanValue(xmlPath.getString(String.format("registrar[%d].registrationModes.mode.findAll{it.@name =='%s'}.@enabled", i,
                        "BY_REGISTRAR")));
                Utils.booleanValue(xmlPath.getString(String.format("registrar[%d].registrationModes.mode.findAll{it.@name =='%s'}.@enabled", i,
                        "BY_RESERVATION")));
            }
        } else {
            LOGGER.warning("no registrars found");
        }
    }

    @Test
    public void withoutCatalogs() {
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("catalogs", "false")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrars", nsContext))//
                .when().get(buildUrl()).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.registrars");
        int size = xmlPath.getInt("registrar.size()");
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                assertTrue(DateTime.parse(xmlPath.getString(String.format("registrar[%d].created", i))).isBeforeNow());
                // catalogs not present
                assertEquals(0, xmlPath.getList(String.format("registrar[%d].catalogs", i)).size());
                // all modes defined and have boolean values
                Utils.booleanValue(xmlPath.getString(String.format("registrar[%d].registrationModes.mode.findAll{it.@name =='%s'}.@enabled", i,
                        "BY_RESOLVER")));
                Utils.booleanValue(xmlPath.getString(String.format("registrar[%d].registrationModes.mode.findAll{it.@name =='%s'}.@enabled", i,
                        "BY_REGISTRAR")));
                Utils.booleanValue(xmlPath.getString(String.format("registrar[%d].registrationModes.mode.findAll{it.@name =='%s'}.@enabled", i,
                        "BY_RESERVATION")));
            }
        } else {
            LOGGER.warning("no registrars found");
        }
    }

    @Test
    public void responseContainsExistingRegistrar() {
        String code = getRandomExistingRegistrarCode();
        if (code != null) {
            LOGGER.info(String.format("registrar code: %s", code));
            with().config(namespaceAwareXmlConfig()).when().get(buildUrl()).then()//
                    .assertThat().statusCode(200)//
                    .assertThat().contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .assertThat().body(hasXPath(String.format("/c:response/c:registrars/c:registrar[@code='%s']", code), nsContext))//
                    .assertThat().body(hasXPath(String.format(//
                            "/c:response/c:registrars/c:registrar[@code='%s']/c:registrationModes/c:mode[@name='BY_RESOLVER']", code), nsContext))//
                    .assertThat().body(hasXPath(String.format(//
                            "/c:response/c:registrars/c:registrar[@code='%s']/c:registrationModes/c:mode[@name='BY_REGISTRAR']", code), nsContext))//
                    .assertThat().body(hasXPath(String.format(//
                            "/c:response/c:registrars/c:registrar[@code='%s']/c:registrationModes/c:mode[@name='BY_RESERVATION']", code), nsContext))//
            ;
        } else {
            LOGGER.warning("no registrars available");
        }
    }

    @Test
    public void formatXml() {
        with().config(namespaceAwareXmlConfig()).queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrars", nsContext))//
                .when().get(buildUrl());
    }

    @Test
    public void formatNotSpecified() {
        with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrars", nsContext))//
                .when().get(buildUrl());
    }

    @Test
    public void formatEmpty() {
        with().config(namespaceAwareXmlConfig()).queryParam("format", "")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.HTML)//
                .when().get(buildUrl());
    }

    @Test
    public void formatInvalid() {
        with().config(namespaceAwareXmlConfig()).queryParam("format", "pdf")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.HTML)//
                .when().get(buildUrl());
    }

    @Test
    public void formatJson() {
        String responseJson = with().config(namespaceAwareXmlConfig()).queryParam("digitalLibraries", "true").queryParam("catalogs", "true")//
                .queryParam("format", "json")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.JSON)//
                .when().get(buildUrl()).andReturn().asString();

        JsonPath path = from(responseJson);
        int size = path.getInt("size()");
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                assertTrue(DateTime.parse(path.getString(String.format("registrars[%d].created", i))).isBeforeNow());
                // digital libraries present
                assertThat(path.getList(String.format("registrars[%d].digitalLibraries", i)).size(), greaterThanOrEqualTo(0));
                // catalogs present
                assertThat(path.getList(String.format("registrars[%d].catalogs", i)).size(), greaterThanOrEqualTo(0));
                // registration modes
                Utils.booleanValue(path.getString(String.format("registrars[%d].registrationModes.BY_RESOLVER", i)));
                Utils.booleanValue(path.getString(String.format("registrars[%d].registrationModes.BY_REGISTRAR", i)));
                Utils.booleanValue(path.getString(String.format("registrars[%d].registrationModes.BY_RESERVATION", i)));
            }
        } else {
            LOGGER.warning("no registrars found");
        }
    }

}
