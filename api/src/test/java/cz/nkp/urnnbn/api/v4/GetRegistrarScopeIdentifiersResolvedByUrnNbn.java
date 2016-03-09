package cz.nkp.urnnbn.api.v4;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.api.Utils;
import cz.nkp.urnnbn.api.pojo.RsId;

/**
 * Tests for GET /api/v4/resolver/${URN_NBN}/registrarScopeIdentifiers
 *
 */
public class GetRegistrarScopeIdentifiersResolvedByUrnNbn extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(GetRegistrarScopeIdentifiersResolvedByUrnNbn.class.getName());

    @BeforeClass
    public void beforeClass() {
        init();
    }

    private String buildUrl(String urnNbn) {
        return buildResolvationPath(urnNbn) + "/registrarScopeIdentifiers";
    }

    @Test
    public void urnnbnInvalid() {
        String urnNbn = Utils.getRandomItem(URNNBN_INVALID);
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(urnNbn)).andReturn().asString();
        // LOGGER.info(responseXml);
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_URN_NBN");
    }

    @Test
    public void urnnbnValidFree() {
        String urnNbn = getRandomFreeUrnNbnOrNull(REGISTRAR);
        if (urnNbn == null) {
            LOGGER.warning("no free urn:nbn found, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                    .statusCode(404)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:error", nsContext))//
                    .when().get(buildUrl(urnNbn)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
            assertEquals("UNKNOWN_URN_NBN", xmlPath.getString("code"));
        }
    }

    @Test
    public void urnnbnValidReserved() {
        String urnNbn = getReservedUrnNbn(REGISTRAR, USER);
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertEquals("UNKNOWN_DIGITAL_DOCUMENT", xmlPath.getString("code"));
    }

    @Test
    public void urnnbnValidActive() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        RsId rsId1 = new RsId(REGISTRAR, "type1", "value1");
        RsId rsId2 = new RsId(REGISTRAR, "type2", "value2");
        insertRegistrarScopeId(urnNbn, rsId1, USER);
        insertRegistrarScopeId(urnNbn, rsId2, USER);
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrarScopeIdentifiers", nsContext))//
                .when().get(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.registrarScopeIdentifiers");
        // check that ids found in response
        assertEquals(2, xmlPath.getInt("id.size()"));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + rsId1.type + "\' }"), equalTo(rsId1.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + rsId2.type + "\' }"), equalTo(rsId2.value));
        // cleanup
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
    }

    @Test
    public void urnnbnValidDeactivated() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        RsId rsId1 = new RsId(REGISTRAR, "type1", "value1");
        RsId rsId2 = new RsId(REGISTRAR, "type2", "value2");
        insertRegistrarScopeId(urnNbn, rsId1, USER);
        insertRegistrarScopeId(urnNbn, rsId2, USER);
        deactivateUrnNbn(urnNbn, USER);
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrarScopeIdentifiers", nsContext))//
                .when().get(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.registrarScopeIdentifiers");
        // check that ids found in response
        assertEquals(2, xmlPath.getInt("id.size()"));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + rsId1.type + "\' }"), equalTo(rsId1.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + rsId2.type + "\' }"), equalTo(rsId2.value));
        // cleanup
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
    }

    @Test
    public void urnnbnValidCaseInsensitive() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        RsId rsId1 = new RsId(REGISTRAR, "type1", "value1");
        RsId rsId2 = new RsId(REGISTRAR, "type2", "value2");
        insertRegistrarScopeId(urnNbn, rsId1, USER);
        insertRegistrarScopeId(urnNbn, rsId2, USER);
        LOGGER.info(urnNbn);
        // original
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrarScopeIdentifiers", nsContext))//
                .when().get(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.registrarScopeIdentifiers");
        // check that ids found in response
        assertEquals(2, xmlPath.getInt("id.size()"));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + rsId1.type + "\' }"), equalTo(rsId1.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + rsId2.type + "\' }"), equalTo(rsId2.value));

        // lower case
        responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrarScopeIdentifiers", nsContext))//
                .when().get(buildUrl(urnNbn.toLowerCase())).andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.registrarScopeIdentifiers");
        // check that ids found in response
        assertEquals(2, xmlPath.getInt("id.size()"));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + rsId1.type + "\' }"), equalTo(rsId1.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + rsId2.type + "\' }"), equalTo(rsId2.value));

        // upper case
        responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrarScopeIdentifiers", nsContext))//
                .when().get(buildUrl(urnNbn.toUpperCase())).andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.registrarScopeIdentifiers");
        // check that ids found in response
        assertEquals(2, xmlPath.getInt("id.size()"));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + rsId1.type + "\' }"), equalTo(rsId1.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + rsId2.type + "\' }"), equalTo(rsId2.value));

        // cleanup
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
    }

    @Test
    public void formatXml() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        LOGGER.info(urnNbn);
        with().config(namespaceAwareXmlConfig()).queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrarScopeIdentifiers", nsContext))//
                .when().get(buildUrl(urnNbn));
    }

    @Test
    public void formatNotSpecified() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        LOGGER.info(urnNbn);
        with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrarScopeIdentifiers", nsContext))//
                .when().get(buildUrl(urnNbn));
    }

    @Test
    public void formatEmpty() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        LOGGER.info(urnNbn);
        with().config(namespaceAwareXmlConfig()).queryParam("format", "")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.HTML)//
                .when().get(buildUrl(urnNbn));
    }

    @Test
    public void formatInvalid() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        LOGGER.info(urnNbn);
        with().config(namespaceAwareXmlConfig()).queryParam("format", "pdf")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.HTML)//
                .when().get(buildUrl(urnNbn));
    }

    @Test
    public void formatJson() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        RsId rsId1 = new RsId(REGISTRAR, "type1", "value1");
        RsId rsId2 = new RsId(REGISTRAR, "type2", "value2");
        insertRegistrarScopeId(urnNbn, rsId1, USER);
        insertRegistrarScopeId(urnNbn, rsId2, USER);
        LOGGER.info(urnNbn);
        String responseJson = with().config(namespaceAwareXmlConfig()).queryParam("format", "json")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.JSON)//
                .when().get(buildUrl(urnNbn)).andReturn().asString();
        JsonPath path = from(responseJson);
        int size = path.getInt("registrarScopeIdentifiers.size()");
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                String type = path.getString(String.format("registrarScopeIdentifiers[%d].type", i));
                String value = path.getString(String.format("registrarScopeIdentifiers[%d].value", i));
                if (rsId1.type.equals(type)) {
                    assertEquals(rsId1.value, value);
                } else if (rsId2.type.equals(type)) {
                    assertEquals(rsId2.value, value);
                }
            }
        } else {
            LOGGER.warning("no registrar-scope identifiers found");
        }
        // cleanup
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
    }

}
