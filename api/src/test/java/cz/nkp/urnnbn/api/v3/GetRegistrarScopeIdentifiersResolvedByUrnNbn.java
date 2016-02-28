package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.api.Utils;

/**
 * Tests for GET /api/v3/resolver/${URN_NBN}/registrarScopeIdentifiers
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
        String urnNbn = URN_NBN_FREE;
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertEquals("UNKNOWN_URN", xmlPath.getString("code"));
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

}
