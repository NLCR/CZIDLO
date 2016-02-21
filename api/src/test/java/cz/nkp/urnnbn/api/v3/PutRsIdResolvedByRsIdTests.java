package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

/**
 * Tests for PUT
 * /api/v3/registrars/${REGISTRAR_CODE}/digitalDocuments/registrarScopeIdentifier/${ID_TYPE}/${ID_VALUE}/registrarScopeIdentifiers/${ID_TYPE_2}
 *
 */
public class PutRsIdResolvedByRsIdTests extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(PutRsIdResolvedByRsIdTests.class.getName());

    private final Credentials USER_WITH_RIGHTS = new Credentials("martin", "i0oEhu");
    private final Credentials USER_NO_RIGHTS = new Credentials("nobody", "skgo1dukg");
    private final String URNNBN = "urn:nbn:cz:aba001-0005hy";

    @BeforeSuite
    public void beforeSuite() {
        init();
    }

    @BeforeMethod
    public void beforeMethod() {
        // delete all registrar-scope-ids
        deleteAllRegistrarScopeIdentifiers(URNNBN, USER_WITH_RIGHTS);
    }

    @AfterMethod
    public void afterMethod() {
        // delete all registrar-scope-ids
        deleteAllRegistrarScopeIdentifiers(URNNBN, USER_WITH_RIGHTS);
    }

    @Test
    public void putRegistrarScopeIdentifierInsertNotAuthenticated() {
        RsId idForResolvation = new RsId("aba001", "deleteTest1", "something1");
        RsId idToBeInserted = new RsId("aba001", "deleteTest2", "something2");
        // insert idForResolvation
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        // try and insert idToBeInserted
        String url = HTTPS_API_URL + buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers/" + idToBeInserted.type;
        // TODO:APIv4: return xml as well
        // String responseXml =
        with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false)//
                .body(idToBeInserted.value).expect()//
                .statusCode(401)//
                // .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                // .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(url)//
                .andReturn().asString();
        // XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        // Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHENTICATED");
        // check that idToBeInserted was not actually inserted
        List<RsId> rsIds = getRsIds(URNNBN);
        assertThat(rsIds.size(), equalTo(1));
        assertThat(idForResolvation.type, equalTo(rsIds.get(0).type));
        assertThat(idForResolvation.value, equalTo(rsIds.get(0).value));
    }

    @Test
    public void putRegistrarScopeIdentifierInsertNotAuthorized() {
        RsId idForResolvation = new RsId("aba001", "deleteTest1", "something1");
        RsId idToBeInserted = new RsId("aba001", "deleteTest2", "something2");
        // insert idForResolvation
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        // try and insert idToBeInserted
        String url = HTTPS_API_URL + buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers/" + idToBeInserted.type;
        String responseXml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).auth()
                .basic(USER_NO_RIGHTS.login, USER_NO_RIGHTS.password)//
                .body(idToBeInserted.value).expect()//
                .statusCode(401)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(url)//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHORIZED");
        // check that idToBeInserted was not actually inserted
        List<RsId> rsIds = getRsIds(URNNBN);
        assertThat(1, equalTo(rsIds.size()));
        assertThat(idForResolvation.type, equalTo(rsIds.get(0).type));
        assertThat(idForResolvation.value, equalTo(rsIds.get(0).value));
    }

    @Test
    public void putRegistrarScopeIdentifierInsertOk() {
        RsId idForResolvation = new RsId("aba001", "deleteTest1", "something1");
        RsId idToBeInserted = new RsId("aba001", "deleteTest2", "something2");
        // insert idForResolvation
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        // insert idToBeInserted
        String url = HTTPS_API_URL + buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers/" + idToBeInserted.type;
        String responseXml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).auth()
                .basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .body(idToBeInserted.value).expect()//
                .statusCode(201)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().put(url)//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        assertThat(xmlPath.getInt("id.size()"), equalTo(1));
        assertThat(xmlPath.getString("id[0].@type"), equalTo(idToBeInserted.type));
        assertThat(xmlPath.getString("id[0]"), equalTo(idToBeInserted.value));
        assertThat(xmlPath.getString("id[0].@previousValue"), equalTo("[]"));
        // check that both ids present
        List<RsId> rsIdsFetched = getRsIds(URNNBN);
        assertThat(rsIdsFetched.size(), equalTo(2));
        for (RsId id : rsIdsFetched) {
            if (id.type.equals(idForResolvation.type)) {
                assertThat(id.value, equalTo(idForResolvation.value));
            } else if (id.type.equals(idToBeInserted.type)) {
                assertThat(id.value, equalTo(idToBeInserted.value));
            } else {// unexpected id type
                Assert.fail();
            }
        }
    }

    @Test
    public void putRegistrarScopeIdentifierUpdateNotAuthenticated() {
        RsId idForResolvation = new RsId("aba001", "deleteTest1", "something1");
        RsId idToBeUpdated = new RsId("aba001", "deleteTest2", "something2");
        String newValue = idToBeUpdated.value + "new";
        // insert idForResolvation, idToBeUpdated
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idToBeUpdated, USER_WITH_RIGHTS);
        // try and update idToBeUpdated with newValue
        String url = HTTPS_API_URL + buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers/" + idToBeUpdated.type;
        // TODO:APIv4: return xml as well
        // String responseXml =
        with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false)//
                .body(newValue).expect()//
                .statusCode(401)//
                // .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                // .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(url)//
                .andReturn().asString();
        // XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        // Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHENTICATED");
        // check that both ids present and idToBeUpdated with old value
        List<RsId> rsIdsFetched = getRsIds(URNNBN);
        assertThat(rsIdsFetched.size(), equalTo(2));
        for (RsId id : rsIdsFetched) {
            if (id.type.equals(idForResolvation.type)) {
                assertThat(id.value, equalTo(idForResolvation.value));
            } else if (id.type.equals(idToBeUpdated.type)) {
                assertThat(id.value, equalTo(idToBeUpdated.value));
            } else {// unexpected id type
                Assert.fail();
            }
        }
    }

    @Test
    public void putRegistrarScopeIdentifierUpdateNotAuthorized() {
        RsId idForResolvation = new RsId("aba001", "deleteTest1", "something1");
        RsId idToBeUpdated = new RsId("aba001", "deleteTest2", "something2");
        String newValue = idToBeUpdated.value + "new";
        // insert idForResolvation, idToBeUpdated
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idToBeUpdated, USER_WITH_RIGHTS);
        // try and update idToBeUpdated with newValue
        String url = HTTPS_API_URL + buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers/" + idToBeUpdated.type;
        String responseXml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).auth()
                .basic(USER_NO_RIGHTS.login, USER_NO_RIGHTS.password)//
                .body(newValue).expect()//
                .statusCode(401)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(url)//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHORIZED");
        // check that both ids present and idToBeUpdated with old value
        List<RsId> rsIdsFetched = getRsIds(URNNBN);
        assertThat(rsIdsFetched.size(), equalTo(2));
        for (RsId id : rsIdsFetched) {
            if (id.type.equals(idForResolvation.type)) {
                assertThat(id.value, equalTo(idForResolvation.value));
            } else if (id.type.equals(idToBeUpdated.type)) {
                assertThat(id.value, equalTo(idToBeUpdated.value));
            } else {// unexpected id type
                Assert.fail();
            }
        }
    }

    @Test
    public void putRegistrarScopeIdentifierUpdateOk() {
        RsId idForResolvation = new RsId("aba001", "deleteTest1", "something1");
        RsId idToBeUpdated = new RsId("aba001", "deleteTest2", "something2");
        String newValue = idToBeUpdated.value + "new";
        // insert idForResolvation, idToBeUpdated
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idToBeUpdated, USER_WITH_RIGHTS);
        // update idToBeUpdated with newValue
        String url = HTTPS_API_URL + buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers/" + idToBeUpdated.type;
        String responseXml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).auth()
                .basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .body(newValue).expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().put(url)//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        assertThat(xmlPath.getString("id.@type"), equalTo(idToBeUpdated.type));
        assertThat(xmlPath.getString("id"), equalTo(newValue));
        assertThat(xmlPath.getString("id.@previousValue"), equalTo(idToBeUpdated.value));
        // check that both ids present and idToBeUpdated with new value
        List<RsId> rsIdsFetched = getRsIds(URNNBN);
        assertThat(rsIdsFetched.size(), equalTo(2));
        for (RsId id : rsIdsFetched) {
            if (id.type.equals(idForResolvation.type)) {
                assertThat(id.value, equalTo(idForResolvation.value));
            } else if (id.type.equals(idToBeUpdated.type)) {
                assertThat(id.value, equalTo(newValue));
            } else {// unexpected id type
                Assert.fail();
            }
        }
    }

    // TODO: test values of idType in url and idValue in body for both operation versions

    // TODO:APIv4: rename INVALID_REGISTRAR_SCOPE_IDENTIFIER to REGISTRAR_SCOPE_COLLISION and change code to 403
    // TODO: test the collisions here

}
