package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.isEmptyOrNullString;
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
 * Tests for DELETE /api/v3/registrars/${REGISTRAR_CODE}/digitalDocuments/registrarScopeIdentifier/${ID_TYPE}/${ID_VALUE}/registrarScopeIdentifiers
 *
 */
public class DeleteRsIdsResolvedByRsIdTests extends ApiV3Tests {

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
    public void deleteRegistrarScopeIdentifiersNotAuthenticated() {
        RsId idInserted1 = new RsId(REGISTRAR_CODE, "deleteTest1", "something1");
        RsId idInserted2 = new RsId(REGISTRAR_CODE, "deleteTest2", "something2");
        // insert idInserted1, idInserted2
        insertRegistrarScopeId(URNNBN, idInserted1, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idInserted2, USER_WITH_RIGHTS);
        // try and delete all ids
        // TODO:APIv4: return xml as well
        // String xml =
        with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false)//
                .expect()//
                .statusCode(401)//
                // .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                // .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                // .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(idInserted1) + "/registrarScopeIdentifiers")//
                .andReturn().asString();
        // XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        // Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHENTICATED");
        // check that both ids present
        List<RsId> rsIdsFetched = getRsIds(URNNBN);
        assertThat(rsIdsFetched.size(), equalTo(2));
        for (RsId id : rsIdsFetched) {
            if (id.type.equals(idInserted1.type)) {
                assertThat(id.value, equalTo(idInserted1.value));
            } else if (id.type.equals(idInserted1.type)) {
                assertThat(id.value, equalTo(idInserted1.value));
            } else {// unexpected id type
                Assert.fail();
            }
        }
    }

    @Test
    public void deleteRegistrarScopeIdentifiersNotAuthorized() {
        RsId idInserted1 = new RsId(REGISTRAR_CODE, "deleteTest1", "something1");
        RsId idInserted2 = new RsId(REGISTRAR_CODE, "deleteTest2", "something2");
        // insert idInserted1, idInserted2
        insertRegistrarScopeId(URNNBN, idInserted1, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idInserted2, USER_WITH_RIGHTS);
        // try and delete all ids
        String xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).auth().basic(USER_NO_RIGHTS.login, USER_NO_RIGHTS.password)//
                .expect()//
                .statusCode(401)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(idInserted1) + "/registrarScopeIdentifiers")//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHORIZED");
        // check that both ids present
        List<RsId> rsIdsFetched = getRsIds(URNNBN);
        assertThat(rsIdsFetched.size(), equalTo(2));
        for (RsId id : rsIdsFetched) {
            if (id.type.equals(idInserted1.type)) {
                assertThat(id.value, equalTo(idInserted1.value));
            } else if (id.type.equals(idInserted1.type)) {
                assertThat(id.value, equalTo(idInserted1.value));
            } else {// unexpected id type
                Assert.fail();
            }
        }
    }

    @Test
    public void deleteRegistrarScopeIdentifiers() {
        RsId idForResolvation = new RsId(REGISTRAR_CODE, "resolvation", "something1");
        RsId id2 = new RsId(REGISTRAR_CODE, "test2", RSID_VALUE_MIN_LENGTH);
        RsId id3 = new RsId(REGISTRAR_CODE, "test3", RSID_VALUE_MAX_LENGTH);
        RsId id4 = new RsId(REGISTRAR_CODE, "test4", RSID_VALUE_RESERVED_CHARS);
        RsId id5 = new RsId(REGISTRAR_CODE, "test5", RSID_VALUE_UNRESERVED_CHARS);

        RsId id6 = new RsId(REGISTRAR_CODE, RSID_TYPE_MIN_LENGTH, "something");
        RsId id7 = new RsId(REGISTRAR_CODE, RSID_TYPE_MAX_LENGTH, "something");
        RsId id8 = new RsId(REGISTRAR_CODE, RSID_TYPE_RESERVED_CHARS, "something");
        RsId id9 = new RsId(REGISTRAR_CODE, RSID_TYPE_UNRESERVED_CHARS, "something");

        // insert idInserted1, idInserted2
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, id2, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, id3, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, id4, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, id5, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, id6, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, id7, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, id8, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, id9, USER_WITH_RIGHTS);
        // delete all ids
        String xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).auth()
                .basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrarScopeIdentifiers", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers")//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.registrarScopeIdentifiers");
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idForResolvation.type + "\' }"), equalTo(idForResolvation.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + id2.type + "\' }"), equalTo(id2.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + id3.type + "\' }"), equalTo(id3.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + id4.type + "\' }"), equalTo(id4.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + id5.type + "\' }"), equalTo(id5.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + id6.type + "\' }"), equalTo(id6.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + id7.type + "\' }"), equalTo(id7.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + id8.type + "\' }"), equalTo(id8.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + id9.type + "\' }"), equalTo(id9.value));
        // get all rsids by urn:nbn (should be empty)
        String url = HTTPS_API_URL + buildResolvationPath(Utils.urlEncodeReservedChars(URNNBN)) + "/registrarScopeIdentifiers";
        xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).auth().basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrarScopeIdentifiers", nsContext))//
                .when().get(url)//
                .andReturn().asString();
        xmlPath = XmlPath.from(xml).setRoot("response.registrarScopeIdentifiers");
        assertThat(xmlPath.getString("id"), isEmptyOrNullString());
    }

}
