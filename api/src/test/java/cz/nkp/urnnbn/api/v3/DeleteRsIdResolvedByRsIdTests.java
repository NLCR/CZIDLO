package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertThat;

import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.api.Utils;

/**
 * Tests for DELETE
 * /api/v3/registrars/${REGISTRAR_CODE}/digitalDocuments/registrarScopeIdentifier/${ID_TYPE}/${ID_VALUE}registrarScopeIdentifiers/${ID_TYPE_2}
 *
 */
public class DeleteRsIdResolvedByRsIdTests extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(DeleteRsIdResolvedByRsIdTests.class.getName());

    private static final Credentials USER_WITH_RIGHTS = new Credentials("martin", "i0oEhu");
    private static final Credentials USER_NO_RIGHTS = new Credentials("nobody", "skgo1dukg");
    private final String EXISTING_URNNBN = "urn:nbn:cz:aba001-0005hy";

    @BeforeSuite
    public void beforeSuite() {
        init();
    }

    @Test
    public void deleteRegistrarScopeIdentifierOk() {
        RsId id1 = new RsId("aba001", "deleteTest1", "something1");
        RsId id2 = new RsId("aba001", "deleteTest2", "something2");
        // delete all registrar-scope-identifiers
        deleteAllRegistrarScopeIdentifiers(EXISTING_URNNBN, USER_WITH_RIGHTS);
        // insert id1 and id2
        insertRegistrarScopeId(EXISTING_URNNBN, id1.type, id1.value, USER_WITH_RIGHTS);
        insertRegistrarScopeId(EXISTING_URNNBN, id2.type, id2.value, USER_WITH_RIGHTS);
        // delete id 1
        String xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).auth()
                .basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(id1) + "/registrarScopeIdentifiers/" + id1.type)//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response");
        assertThat(xmlPath.getString("id.find { it.@type == \'" + id1.type + "\' }"), equalTo(id1.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + id2.type + "\' }"), isEmptyOrNullString());
        // get all rsids by urn:nbn (should contain only id 2)
        String url = HTTPS_API_URL + buildResolvationPath(Utils.urlEncodeReservedChars(EXISTING_URNNBN)) + "/registrarScopeIdentifiers";
        xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).auth().basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrarScopeIdentifiers", nsContext))//
                .when().get(url)//
                .andReturn().asString();
        xmlPath = XmlPath.from(xml).setRoot("response.registrarScopeIdentifiers");
        assertThat(xmlPath.getString("id.find { it.@type == \'" + id1.type + "\' }"), isEmptyOrNullString());
        assertThat(xmlPath.getString("id.find { it.@type == \'" + id2.type + "\' }"), equalTo(id2.value));
        // cleanlup
        deleteAllRegistrarScopeIdentifiers(EXISTING_URNNBN, USER_WITH_RIGHTS);
    }

    @Test
    public void deleteRegistrarScopeIdentifierNotAuthenticated() {
        RsId id = new RsId("aba001", "deleteTest1", "something1");
        // delete all registrar-scope-identifiers
        deleteAllRegistrarScopeIdentifiers(EXISTING_URNNBN, USER_WITH_RIGHTS);
        // insert id
        insertRegistrarScopeId(EXISTING_URNNBN, id.type, id.value, USER_WITH_RIGHTS);
        // delete id without credentials
        // xml =
        with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false)//
                .expect()//
                .statusCode(401)//
                // .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                // .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(id) + "/registrarScopeIdentifiers/" + id.type)//
                .andReturn().asString();
        // XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        // Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHENTICATED");
        // TODO:APIv4: return xml as well
        // TODO: check that no change happened
        // cleanlup
        deleteAllRegistrarScopeIdentifiers(EXISTING_URNNBN, USER_WITH_RIGHTS);
    }

    @Test
    public void deleteRegistrarScopeIdentifierNotAuthorized() {
        RsId id = new RsId("aba001", "deleteTest1", "something1");
        // delete all registrar-scope-identifiers
        deleteAllRegistrarScopeIdentifiers(EXISTING_URNNBN, USER_WITH_RIGHTS);
        // insert id
        insertRegistrarScopeId(EXISTING_URNNBN, id.type, id.value, USER_WITH_RIGHTS);
        // try and delete id with wrong credentials
        String xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).auth().basic(USER_NO_RIGHTS.login, USER_NO_RIGHTS.password)//
                .expect()//
                .statusCode(401)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(id) + "/registrarScopeIdentifiers/" + id.type)//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHORIZED");
        // TODO: check that no change happened
        // cleanlup
        deleteAllRegistrarScopeIdentifiers(EXISTING_URNNBN, USER_WITH_RIGHTS);
    }

    @Test
    public void deleteRegistrarScopeIdentifierRsIdDoesNotExist() {
        RsId id1 = new RsId("aba001", "deleteTest1", "something1");
        RsId id2 = new RsId("aba001", "deleteTest2", "something2");
        // delete all registrar-scope-identifiers
        deleteAllRegistrarScopeIdentifiers(EXISTING_URNNBN, USER_WITH_RIGHTS);
        // insert id1
        insertRegistrarScopeId(EXISTING_URNNBN, id1.type, id1.value, USER_WITH_RIGHTS);
        // try and delete id2 without id2 being present
        String xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).auth()
                .basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(id1) + "/registrarScopeIdentifiers/" + id2.type)//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_REGISTRAR_SCOPE_IDENTIFIER");
        // cleanlup
        deleteAllRegistrarScopeIdentifiers(EXISTING_URNNBN, USER_WITH_RIGHTS);
    }

    // TODO: dodelat testy na validni rsIdType

}
