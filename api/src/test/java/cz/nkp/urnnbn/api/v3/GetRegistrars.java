package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.logging.Logger;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.api.Utils;

/**
 * Tests for GET /api/v3/registrars
 *
 */
public class GetRegistrars extends ApiV3Tests {

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
                // TODO:APIv4: until this fixed: https://github.com/NLCR/CZIDLO/issues/134
                assertFalse("".equals(xmlPath.getString(String.format("registrar[%d].created", i))));
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
                // TODO:APIv4: until this fixed: https://github.com/NLCR/CZIDLO/issues/134
                assertFalse("".equals(xmlPath.getString(String.format("registrar[%d].created", i))));
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
                // TODO:APIv4: until this fixed: https://github.com/NLCR/CZIDLO/issues/134
                assertFalse("".equals(xmlPath.getString(String.format("registrar[%d].created", i))));
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
                // TODO:APIv4: until this fixed: https://github.com/NLCR/CZIDLO/issues/134
                assertFalse("".equals(xmlPath.getString(String.format("registrar[%d].created", i))));
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
                // TODO:APIv4: until this fixed: https://github.com/NLCR/CZIDLO/issues/134
                assertFalse("".equals(xmlPath.getString(String.format("registrar[%d].created", i))));
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
                    // TODO:APIv4: until this fixed: https://github.com/NLCR/CZIDLO/issues/134
                    .assertThat().body(hasXPath(//
                            String.format("/c:response/c:registrars/c:registrar[@code='%s']/c:created", code), nsContext))//
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

}
