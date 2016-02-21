package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertThat;

import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.api.Utils;

/**
 * Tests for DELETE
 * /api/v3/registrars/${REGISTRAR_CODE}/digitalDocuments/registrarScopeIdentifier/${ID_TYPE}/${ID_VALUE}/registrarScopeIdentifiers/${ID_TYPE_2}
 *
 */
public class DeleteRsIdResolvedByRsIdTests extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(DeleteRsIdResolvedByRsIdTests.class.getName());

    private final Credentials USER_WITH_RIGHTS = new Credentials("martin", "i0oEhu");
    private final Credentials USER_NO_RIGHTS = new Credentials("nobody", "skgo1dukg");
    private final String REGISTRAR_CODE = "aba001";
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
    public void deleteRegistrarScopeIdentifierOk() {
        RsId idForResolvation = new RsId(REGISTRAR_CODE, "resolvation", "something");
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        // types
        deleteRegistrarScopeIdentifierOk(idForResolvation, new RsId(REGISTRAR_CODE, RSID_TYPE_OK_MAX_LENGTH, "typeMinLength"));
        deleteRegistrarScopeIdentifierOk(idForResolvation, new RsId(REGISTRAR_CODE, RSID_TYPE_OK_MAX_LENGTH, "typeMaxLength"));
        for (String type : RSID_TYPE_OK_RESERVED) {
            deleteRegistrarScopeIdentifierOk(idForResolvation, new RsId(REGISTRAR_CODE, type, "typereserved"));
        }
        for (String type : RSID_TYPE_OK_UNRESERVED) {
            deleteRegistrarScopeIdentifierOk(idForResolvation, new RsId(REGISTRAR_CODE, type, "typeUnreserved"));
        }
        // values
        deleteRegistrarScopeIdentifierOk(idForResolvation, new RsId(REGISTRAR_CODE, "valueMinLength", RSID_VALUE_OK_MIN_LENGTH));
        deleteRegistrarScopeIdentifierOk(idForResolvation, new RsId(REGISTRAR_CODE, "valueMaxLength", RSID_VALUE_OK_MAX_LENGTH));
        for (int i = 0; i < RSID_VALUE_OK_RESERVED.length; i++) {
            String value = RSID_VALUE_OK_RESERVED[i];
            deleteRegistrarScopeIdentifierOk(idForResolvation, new RsId(REGISTRAR_CODE, "valueReserved" + i, value));
        }
        for (int i = 0; i < RSID_VALUE_OK_UNRESERVED.length; i++) {
            String value = RSID_VALUE_OK_UNRESERVED[i];
            deleteRegistrarScopeIdentifierOk(idForResolvation, new RsId(REGISTRAR_CODE, "valueUnreserved" + i, value));
        }
    }

    private void deleteRegistrarScopeIdentifierOk(RsId idForResolvation, RsId idToBeDeleted) {
        // insert id
        insertRegistrarScopeId(URNNBN, idToBeDeleted, USER_WITH_RIGHTS);
        // delete id
        String xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).auth()
                .basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(idForResolvation)//
                        + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(idToBeDeleted.type))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response");
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idToBeDeleted.type + "\' }"), equalTo(idToBeDeleted.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idForResolvation.type + "\' }"), isEmptyOrNullString());
        // try and get deleted rsid
        xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildResolvationPath(Utils.urlEncodeReservedChars(URNNBN))//
                        + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(idToBeDeleted.type))//
                .andReturn().asString();
        xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "NOT_DEFINED");
    }

    @Test
    public void deleteRegistrarScopeIdentifierNotAuthenticated() {
        RsId id = new RsId("aba001", "deleteTest1", "something1");
        // insert id
        insertRegistrarScopeId(URNNBN, id, USER_WITH_RIGHTS);
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
    }

    @Test
    public void deleteRegistrarScopeIdentifierNotAuthorized() {
        RsId id = new RsId("aba001", "deleteTest1", "something1");
        // insert id
        insertRegistrarScopeId(URNNBN, id, USER_WITH_RIGHTS);
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
    }

    @Test
    public void deleteRegistrarScopeIdentifierRsIdDoesNotExist() {
        RsId id1 = new RsId("aba001", "deleteTest1", "something1");
        RsId id2 = new RsId("aba001", "deleteTest2", "something2");
        // insert id1
        insertRegistrarScopeId(URNNBN, id1, USER_WITH_RIGHTS);
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
    }

    // TODO: dodelat testy na nepovolene reserved/unreserved znaky v rsIdType a prilis kratkou/dlouhou hodnotu idType

}
