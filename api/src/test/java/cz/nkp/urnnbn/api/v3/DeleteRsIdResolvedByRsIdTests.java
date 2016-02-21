package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertEquals;
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
    public void deleteRegistrarScopeIdentifierNotAuthenticated() {
        RsId id = new RsId(REGISTRAR_CODE, "deleteTest1", "something1");
        // insert id
        insertRegistrarScopeId(URNNBN, id, USER_WITH_RIGHTS);
        // delete id without credentials
        // responseXml =
        with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false)//
                .expect()//
                .statusCode(401)//
                // .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                // .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(id) + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(id.type))//
                .andReturn().asString();
        // XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        // Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHENTICATED");
        // TODO:APIv4: return xml as well
        // check that id still present
        List<RsId> rsIds = getRsIds(URNNBN);
        assertEquals(1, rsIds.size());
        assertEquals(id.type, rsIds.get(0).type);
        assertEquals(id.value, rsIds.get(0).value);
    }

    @Test
    public void deleteRegistrarScopeIdentifierNotAuthorized() {
        RsId id = new RsId(REGISTRAR_CODE, "deleteTest1", "something1");
        // insert id
        insertRegistrarScopeId(URNNBN, id, USER_WITH_RIGHTS);
        // try and delete id with wrong credentials
        String responseXml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).auth()
                .basic(USER_NO_RIGHTS.login, USER_NO_RIGHTS.password)//
                .expect()//
                .statusCode(401)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(id) + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(id.type))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHORIZED");
        // check that id still present
        List<RsId> rsIds = getRsIds(URNNBN);
        assertEquals(1, rsIds.size());
        assertEquals(id.type, rsIds.get(0).type);
        assertEquals(id.value, rsIds.get(0).value);
    }

    @Test
    public void deleteRegistrarScopeIdentifierOk() {
        RsId idForResolvation = new RsId(REGISTRAR_CODE, "resolvation", "something");
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        // types
        deleteRegistrarScopeIdentifierOk(idForResolvation, new RsId(REGISTRAR_CODE, RSID_TYPE_OK_MAX_LENGTH, "typeMinLength"));
        deleteRegistrarScopeIdentifierOk(idForResolvation, new RsId(REGISTRAR_CODE, RSID_TYPE_OK_MAX_LENGTH, "typeMaxLength"));
        for (String type : RSID_TYPE_OK_RESERVED) {
            deleteRegistrarScopeIdentifierOk(idForResolvation, new RsId(REGISTRAR_CODE, type, "value"));
        }
        for (String type : RSID_TYPE_OK_UNRESERVED) {
            deleteRegistrarScopeIdentifierOk(idForResolvation, new RsId(REGISTRAR_CODE, type, "value"));
        }
        // values
        deleteRegistrarScopeIdentifierOk(idForResolvation, new RsId(REGISTRAR_CODE, "valueMinLength", RSID_VALUE_OK_MIN_LENGTH));
        deleteRegistrarScopeIdentifierOk(idForResolvation, new RsId(REGISTRAR_CODE, "valueMaxLength", RSID_VALUE_OK_MAX_LENGTH));
        for (int i = 0; i < RSID_VALUE_OK_RESERVED.length; i++) {
            String value = RSID_VALUE_OK_RESERVED[i];
            deleteRegistrarScopeIdentifierOk(idForResolvation, new RsId(REGISTRAR_CODE, "typeReserved" + i, value));
        }
        for (int i = 0; i < RSID_VALUE_OK_UNRESERVED.length; i++) {
            String value = RSID_VALUE_OK_UNRESERVED[i];
            deleteRegistrarScopeIdentifierOk(idForResolvation, new RsId(REGISTRAR_CODE, "typeUnreserved" + i, value));
        }
    }

    private void deleteRegistrarScopeIdentifierOk(RsId idForResolvation, RsId idToBeDeleted) {
        // insert id
        insertRegistrarScopeId(URNNBN, idToBeDeleted, USER_WITH_RIGHTS);
        // delete id
        String responseXml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).auth()
                .basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(idForResolvation)//
                        + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(idToBeDeleted.type))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idToBeDeleted.type + "\' }"), equalTo(idToBeDeleted.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idForResolvation.type + "\' }"), isEmptyOrNullString());
        // try and get deleted rsid
        responseXml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildResolvationPath(Utils.urlEncodeReservedChars(URNNBN))//
                        + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(idToBeDeleted.type))//
                .andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "NOT_DEFINED");
    }

    @Test
    public void deleteRegistrarScopeIdentifierTypeUnknown() {
        RsId idForResolvation = new RsId(REGISTRAR_CODE, "type1", "something1");
        String typeUnknown = "type2";
        // insert idForResolvation
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        // try and delete with type=typeUnknown
        String responseXml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).auth()
                .basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(idForResolvation)//
                        + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(typeUnknown)) //
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_REGISTRAR_SCOPE_IDENTIFIER");
    }

    @Test
    public void deleteRegistrarScopeIdentifierIdTypeInvalid() {
        RsId idForResolvation = new RsId(REGISTRAR_CODE, "resolvation", "something");
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        // test
        deleteRegistrarScopeIdentifierTypeInvalid(idForResolvation, RSID_TYPE_INVALID_TO_SHORT);
        deleteRegistrarScopeIdentifierTypeInvalid(idForResolvation, RSID_TYPE_INVALID_TO_LONG);
        for (String type : RSID_TYPE_INVALID_RESERVED) {
            deleteRegistrarScopeIdentifierTypeInvalid(idForResolvation, type);
        }
        for (String type : RSID_TYPE_INVALID_UNRESERVED) {
            deleteRegistrarScopeIdentifierTypeInvalid(idForResolvation, type);
        }
    }

    private void deleteRegistrarScopeIdentifierTypeInvalid(RsId idForResolvation, String typeInvalid) {
        // even though it was not inserted, error INVALID_DIGITAL_DOCUMENT_ID_TYPE should be returned
        // try and delete with type=typeUnknown
        String responseXml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).auth()
                .basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(idForResolvation)//
                        + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(typeInvalid))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_DIGITAL_DOCUMENT_ID_TYPE");
    }

    // TODO: test invalid values of idType
    // see DeleteRsIdResolvedByRsIdTests and DeleteRsIdsResolvedByRsIdTests

}
