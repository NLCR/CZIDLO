package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.api.Utils;

/**
 * Tests for DELETE /api/v3/resolver/${URN_NBN}/registrarScopeIdentifiers/${ID_TYPE_2}
 *
 */
public class DeleteRegistrarScopeIdentifierResolvedByUrnNbn extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(DeleteRegistrarScopeIdentifierResolvedByUrnNbn.class.getName());

    @BeforeClass
    public void beforeClass() {
        init();
    }

    private String buildUrl(String urnNbn, String type) {
        return HTTPS_API_URL + buildResolvationPath(urnNbn) + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(type);
    }

    @Test
    public void notAuthenticated() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        RsId idToBeDeleted = new RsId(REGISTRAR, "type", "value");
        LOGGER.info(urnNbn + ", type: " + idToBeDeleted.type);
        // insert id
        insertRegistrarScopeId(urnNbn, idToBeDeleted, USER);
        // try and delete id
        // TODO:APIv4: return xml as well
        // String responseXml =
        with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(401)//
                // .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                // .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(buildUrl(urnNbn, idToBeDeleted.type)).andReturn().asString();
        // XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        // Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHENTICATED");
        // check that id still present
        List<RsId> rsIdsFetched = getRsIds(urnNbn);
        boolean found = false;
        for (RsId id : rsIdsFetched) {
            if (id.type.equals(idToBeDeleted.type) && id.value.equals(idToBeDeleted.value)) {
                found = true;
            }
        }
        assertTrue(found);
        // cleanup
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
    }

    @Test
    public void notAuthorized() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        RsId idToBeDeleted = new RsId(REGISTRAR, "type", "value");
        LOGGER.info(urnNbn + ", type: " + idToBeDeleted.type);
        // insert id
        insertRegistrarScopeId(urnNbn, idToBeDeleted, USER);
        // try and delete id
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_NO_RIGHTS.login, USER_NO_RIGHTS.password)//
                .expect()//
                .statusCode(401)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(buildUrl(urnNbn, idToBeDeleted.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHORIZED");
        // check that id still present
        List<RsId> rsIdsFetched = getRsIds(urnNbn);
        boolean found = false;
        for (RsId id : rsIdsFetched) {
            if (id.type.equals(idToBeDeleted.type) && id.value.equals(idToBeDeleted.value)) {
                found = true;
            }
        }
        assertTrue(found);
        // cleanup
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
    }

    @Test
    public void urnnbnInvalid() {
        String urnNbn = Utils.getRandomItem(URNNBN_INVALID);
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(buildUrl(urnNbn, "type")).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_URN_NBN");
    }

    @Test
    public void urnnbnValidFree() {
        String urnNbn = URN_NBN_FREE;
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(buildUrl(urnNbn, "type")).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertEquals("UNKNOWN_URN", xmlPath.getString("code"));
    }

    @Test
    public void urnnbnValidReserved() {
        String urnNbn = getReservedUrnNbn(REGISTRAR, USER);
        LOGGER.info(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(buildUrl(urnNbn, "type")).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertEquals("UNKNOWN_DIGITAL_DOCUMENT", xmlPath.getString("code"));
    }

    @Test
    public void urnnbnValidTypeInvalid() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        String type = Utils.getRandomItem(RSID_TYPES_INVALID);
        LOGGER.info(urnNbn + ", type: " + type);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(buildUrl(urnNbn, type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        // TODO:APIv4: rename this error code
        assertThat(xmlPath.getString("error.code"), equalTo("INVALID_DIGITAL_DOCUMENT_ID_TYPE"));
    }

    @Test
    public void urnnbnValidTypeValidNotDefined() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        String type = Utils.getRandomItem(RSID_TYPES_VALID);
        LOGGER.info(urnNbn + ", type: " + type);
        String xml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(buildUrl(urnNbn, type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        // TODO:APIv4: INVALID_REGISTRAR_SCOPE_IDENTIFIER rename to NOT_DEFINED
        Assert.assertEquals(xmlPath.get("code"), "INVALID_REGISTRAR_SCOPE_IDENTIFIER");
    }

    @Test
    public void urnnbnValidTypeValidDefined() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        RsId idToBeDeleted = new RsId(REGISTRAR, "type1", "value1");
        RsId idOther = new RsId(REGISTRAR, "type2", "value2");
        LOGGER.info(urnNbn + ", type: " + idToBeDeleted.type);
        // insert ids
        insertRegistrarScopeId(urnNbn, idToBeDeleted, USER);
        insertRegistrarScopeId(urnNbn, idOther, USER);
        // try and delete id
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().delete(buildUrl(urnNbn, idToBeDeleted.type)).andReturn().asString();
        // check that id in response
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        assertEquals(1, xmlPath.getInt("id.size()"));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idToBeDeleted.type + "\' }"), equalTo(idToBeDeleted.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idOther.type + "\' }"), isEmptyOrNullString());
        // check that id deleted
        List<RsId> rsIdsFetched = getRsIds(urnNbn);
        boolean found = false;
        for (RsId id : rsIdsFetched) {
            if (id.type.equals(idToBeDeleted.type)) {
                found = true;
            }
        }
        assertFalse(found);
        // cleanup
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
    }

    @Test
    public void urnnbnValidTypeValidDefinedCaseInsensitive() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        RsId idToBeDeleted = new RsId(REGISTRAR, "type1", "value1");
        RsId idOther = new RsId(REGISTRAR, "type2", "value2");
        LOGGER.info(urnNbn + ", type: " + idToBeDeleted.type);

        // UPPER CASE
        // insert ids
        insertRegistrarScopeId(urnNbn, idToBeDeleted, USER);
        insertRegistrarScopeId(urnNbn, idOther, USER);
        // try and delete id
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().delete(buildUrl(urnNbn.toUpperCase(), idToBeDeleted.type)).andReturn().asString();
        // check that only idToBeDeleted in response
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        assertEquals(1, xmlPath.getInt("id.size()"));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idToBeDeleted.type + "\' }"), equalTo(idToBeDeleted.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idOther.type + "\' }"), isEmptyOrNullString());
        // check that id deleted
        List<RsId> rsIdsFetched = getRsIds(urnNbn);
        boolean found = false;
        for (RsId id : rsIdsFetched) {
            if (id.type.equals(idToBeDeleted.type)) {
                found = true;
            }
        }
        assertFalse(found);
        // cleanup
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);

        // LOWER CASE
        // insert ids
        insertRegistrarScopeId(urnNbn, idToBeDeleted, USER);
        insertRegistrarScopeId(urnNbn, idOther, USER);
        // try and delete id
        responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().delete(buildUrl(urnNbn.toLowerCase(), idToBeDeleted.type)).andReturn().asString();
        // check that only idToBeDeleted in response
        xmlPath = XmlPath.from(responseXml).setRoot("response");
        assertEquals(1, xmlPath.getInt("id.size()"));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idToBeDeleted.type + "\' }"), equalTo(idToBeDeleted.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idOther.type + "\' }"), isEmptyOrNullString());
        // check that id deleted
        rsIdsFetched = getRsIds(urnNbn);
        found = false;
        for (RsId id : rsIdsFetched) {
            if (id.type.equals(idToBeDeleted.type)) {
                found = true;
            }
        }
        assertFalse(found);
        // cleanup
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
    }

}
