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
 * /api/v3/registrars/${REGISTRAR_CODE}/digitalDocuments/registrarScopeIdentifier/${ID_TYPE}/${ID_VALUE}/registrarScopeIdentifiers/${ID_TYPE_2}
 *
 */
public class DeleteRsIdResolvedByRsIdTests extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(DeleteRsIdResolvedByRsIdTests.class.getName());

    private final Credentials USER_WITH_RIGHTS = new Credentials("martin", "i0oEhu");
    private final Credentials USER_NO_RIGHTS = new Credentials("nobody", "skgo1dukg");
    private final String REGISTRAR_CODE = "aba001";
    private final String URN_NBN = "urn:nbn:cz:aba001-0005hy";

    @BeforeSuite
    public void beforeSuite() {
        init();
    }

    @Test
    public void deleteRegistrarScopeIdentifierOk() {
        RsId id1 = new RsId("aba001", "deleteTest1", "something1");
        RsId id2 = new RsId("aba001", "deleteTest2", "something2");
        // delete all registrar-scope-identifiers
        deleteAllRegistrarScopeIdentifiers(URN_NBN, USER_WITH_RIGHTS);
        // insert id1 and id2
        insertRegistrarScopeId(URN_NBN, id1, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URN_NBN, id2, USER_WITH_RIGHTS);
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
        String url = HTTPS_API_URL + buildResolvationPath(Utils.urlEncodeReservedChars(URN_NBN)) + "/registrarScopeIdentifiers";
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
        deleteAllRegistrarScopeIdentifiers(URN_NBN, USER_WITH_RIGHTS);
    }

    @Test
    public void deleteRegistrarScopeIdentifierEdgeExamples() {
        // delete all registrar-scope-identifiers
        deleteAllRegistrarScopeIdentifiers(URN_NBN, USER_WITH_RIGHTS);

        deleteRegistrarScopeIdentifierEdgeExample(new RsId(REGISTRAR_CODE, "test2", RSID_VALUE_MIN_LENGTH));
        deleteRegistrarScopeIdentifierEdgeExample(new RsId(REGISTRAR_CODE, "test3", RSID_VALUE_MAX_LENGTH));
        // TODO: enable after fixed https://github.com/NLCR/CZIDLO/issues/131
        // deleteRegistrarScopeIdentifierEdgeExample(new RsId(REGISTRAR_CODE, "test4", RSID_VALUE_RESERVED_CHARS));
        deleteRegistrarScopeIdentifierEdgeExample(new RsId(REGISTRAR_CODE, "test5", RSID_VALUE_UNRESERVED_CHARS));

        deleteRegistrarScopeIdentifierEdgeExample(new RsId(REGISTRAR_CODE, RSID_TYPE_MAX_LENGTH, "something"));
        deleteRegistrarScopeIdentifierEdgeExample(new RsId(REGISTRAR_CODE, RSID_TYPE_MAX_LENGTH, "something"));
        deleteRegistrarScopeIdentifierEdgeExample(new RsId(REGISTRAR_CODE, RSID_TYPE_RESERVED_CHARS, "something"));
        deleteRegistrarScopeIdentifierEdgeExample(new RsId(REGISTRAR_CODE, RSID_TYPE_UNRESERVED_CHARS, "something"));
        // clean up
        deleteAllRegistrarScopeIdentifiers(URN_NBN, USER_WITH_RIGHTS);
    }

    private void deleteRegistrarScopeIdentifierEdgeExample(RsId id) {
        // insert id
        insertRegistrarScopeId(URN_NBN, id, USER_WITH_RIGHTS);
        // delete id
        String xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).auth()
                .basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(id) + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(id.type))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response");
        assertThat(xmlPath.getString("id.find { it.@type == \'" + id.type + "\' }"), equalTo(id.value));
        // try and get deleted rsid
        xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildResolvationPath(Utils.urlEncodeReservedChars(URN_NBN))//
                        + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(id.type))//
                .andReturn().asString();
        xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "NOT_DEFINED");
    }

    @Test
    public void deleteRegistrarScopeIdentifierNotAuthenticated() {
        RsId id = new RsId("aba001", "deleteTest1", "something1");
        // delete all registrar-scope-identifiers
        deleteAllRegistrarScopeIdentifiers(URN_NBN, USER_WITH_RIGHTS);
        // insert id
        insertRegistrarScopeId(URN_NBN, id, USER_WITH_RIGHTS);
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
        deleteAllRegistrarScopeIdentifiers(URN_NBN, USER_WITH_RIGHTS);
    }

    @Test
    public void deleteRegistrarScopeIdentifierNotAuthorized() {
        RsId id = new RsId("aba001", "deleteTest1", "something1");
        // delete all registrar-scope-identifiers
        deleteAllRegistrarScopeIdentifiers(URN_NBN, USER_WITH_RIGHTS);
        // insert id
        insertRegistrarScopeId(URN_NBN, id, USER_WITH_RIGHTS);
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
        deleteAllRegistrarScopeIdentifiers(URN_NBN, USER_WITH_RIGHTS);
    }

    @Test
    public void deleteRegistrarScopeIdentifierRsIdDoesNotExist() {
        RsId id1 = new RsId("aba001", "deleteTest1", "something1");
        RsId id2 = new RsId("aba001", "deleteTest2", "something2");
        // delete all registrar-scope-identifiers
        deleteAllRegistrarScopeIdentifiers(URN_NBN, USER_WITH_RIGHTS);
        // insert id1
        insertRegistrarScopeId(URN_NBN, id1, USER_WITH_RIGHTS);
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
        deleteAllRegistrarScopeIdentifiers(URN_NBN, USER_WITH_RIGHTS);
    }

    // TODO: dodelat testy na nepovolene reserved/unreserved znaky v rsIdType a prilis kratkou/dlouhou hodnotu idType

}
