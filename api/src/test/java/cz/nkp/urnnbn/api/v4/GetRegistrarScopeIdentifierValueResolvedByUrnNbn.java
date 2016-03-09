package cz.nkp.urnnbn.api.v4;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.api.Utils;
import cz.nkp.urnnbn.api.pojo.RsId;

/**
 * Tests for GET /api/v4/resolver/${URN_NBN}/registrarScopeIdentifiers/${ID_TYPE}
 * 
 */
public class GetRegistrarScopeIdentifierValueResolvedByUrnNbn extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(GetRegistrarScopeIdentifierValueResolvedByUrnNbn.class.getName());

    @BeforeClass
    public void beforeClass() {
        init();
    }

    private String buildUrl(String urnNbn, String type) {
        return buildResolvationPath(urnNbn) + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(type);
    }

    @Test
    public void urnnbnInvalid() {
        String urnNbn = Utils.getRandomItem(URNNBN_INVALID);
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(urnNbn, "type")).andReturn().asString();
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
                    .when().get(buildUrl(urnNbn, "type")).andReturn().asString();
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
                .when().get(buildUrl(urnNbn, "type")).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertEquals("UNKNOWN_DIGITAL_DOCUMENT", xmlPath.getString("code"));
    }

    @Test
    public void urnnbnValidTypeInvalid() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        String type = Utils.getRandomItem(RSID_TYPES_INVALID);
        LOGGER.info(urnNbn + ", type: " + type);
        String responseXml = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(urnNbn, type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertThat(xmlPath.getString("code"), equalTo("INVALID_REGISTRAR_SCOPE_ID_TYPE"));
    }

    @Test
    public void urnnbnValidTypeValidNotDefined() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        String type = Utils.getRandomItem(RSID_TYPES_VALID);
        LOGGER.info(urnNbn + ", type: " + type);
        // try and get rsId by type
        String responseXml = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(urnNbn, type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "NOT_DEFINED");
    }

    @Test
    public void urnnbnValidTypeValidDefined() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        RsId idToBeFetched = new RsId(REGISTRAR, "type1", "value1");
        RsId idOther = new RsId(REGISTRAR, "type2", "value2");
        LOGGER.info(urnNbn + ", type: " + idToBeFetched.type);
        // insert ids
        insertRegistrarScopeId(urnNbn, idToBeFetched, USER);
        insertRegistrarScopeId(urnNbn, idOther, USER);
        // get rsId by type
        String responseXml = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().get(buildUrl(urnNbn, idToBeFetched.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        // check that only requested id found in response
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idToBeFetched.type + "\' }"), equalTo(idToBeFetched.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idOther.type + "\' }"), isEmptyOrNullString());
        // cleanup
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
    }

    @Test
    public void urnnbnCaseInsensitive() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        RsId idToBeFetched = new RsId(REGISTRAR, "type1", "value1");
        RsId idOther = new RsId(REGISTRAR, "type2", "value2");
        LOGGER.info(urnNbn + ", type: " + idToBeFetched.type);
        // insert ids
        insertRegistrarScopeId(urnNbn, idToBeFetched, USER);
        insertRegistrarScopeId(urnNbn, idOther, USER);

        // lower case
        String responseXml = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().get(buildUrl(urnNbn.toLowerCase(), idToBeFetched.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        // check that only requested id found in response
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idToBeFetched.type + "\' }"), equalTo(idToBeFetched.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idOther.type + "\' }"), isEmptyOrNullString());

        // upper case
        responseXml = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().get(buildUrl(urnNbn.toUpperCase(), idToBeFetched.type)).andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response");
        // check that only requested id found in response
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idToBeFetched.type + "\' }"), equalTo(idToBeFetched.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idOther.type + "\' }"), isEmptyOrNullString());

        // cleanup
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
    }

    @Test
    public void formatXml() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        RsId idToBeFetched = new RsId(REGISTRAR, "type1", "value1");
        LOGGER.info(urnNbn + ", type: " + idToBeFetched.type);
        insertRegistrarScopeId(urnNbn, idToBeFetched, USER);
        // get rsId by type
        with().config(namespaceAwareXmlConfig()).queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().get(buildUrl(urnNbn, idToBeFetched.type));
        // cleanup
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
    }

    @Test
    public void formatNotSpecified() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        RsId idToBeFetched = new RsId(REGISTRAR, "type1", "value1");
        LOGGER.info(urnNbn + ", type: " + idToBeFetched.type);
        insertRegistrarScopeId(urnNbn, idToBeFetched, USER);
        // get rsId by type
        with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().get(buildUrl(urnNbn, idToBeFetched.type));
        // cleanup
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
    }

    @Test
    public void formatEmpty() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        RsId idToBeFetched = new RsId(REGISTRAR, "type1", "value1");
        LOGGER.info(urnNbn + ", type: " + idToBeFetched.type);
        insertRegistrarScopeId(urnNbn, idToBeFetched, USER);
        // get rsId by type
        with().config(namespaceAwareXmlConfig()).queryParam("format", "")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.HTML)//
                .when().get(buildUrl(urnNbn, idToBeFetched.type));
        // cleanup
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
    }

    @Test
    public void formatInvalid() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        RsId idToBeFetched = new RsId(REGISTRAR, "type1", "value1");
        LOGGER.info(urnNbn + ", type: " + idToBeFetched.type);
        insertRegistrarScopeId(urnNbn, idToBeFetched, USER);
        // get rsId by type
        with().config(namespaceAwareXmlConfig()).queryParam("format", "pdf")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.HTML)//
                .when().get(buildUrl(urnNbn, idToBeFetched.type));
        // cleanup
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
    }

    @Test
    public void formatJson() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        RsId idToBeFetched = new RsId(REGISTRAR, "type1", "value1");
        RsId idOther = new RsId(REGISTRAR, "type2", "value2");
        LOGGER.info(urnNbn + ", type: " + idToBeFetched.type);
        // insert ids
        insertRegistrarScopeId(urnNbn, idToBeFetched, USER);
        insertRegistrarScopeId(urnNbn, idOther, USER);
        // get rsId by type
        String responseJson = with().config(namespaceAwareXmlConfig()).queryParam("format", "json")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.JSON)//
                // .body(hasXPath("/c:response/c:id", nsContext))//
                .when().get(buildUrl(urnNbn, idToBeFetched.type)).andReturn().asString();
        // XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        // // check that only requested id found in response
        // assertThat(xmlPath.getString("id.find { it.@type == \'" + idToBeFetched.type + "\' }"), equalTo(idToBeFetched.value));
        // assertThat(xmlPath.getString("id.find { it.@type == \'" + idOther.type + "\' }"), isEmptyOrNullString());
        // // cleanup
        // TODO: check data
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
    }

}
