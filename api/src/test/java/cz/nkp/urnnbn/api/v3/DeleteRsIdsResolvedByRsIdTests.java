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
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.api.Utils;

/**
 * Tests for DELETE /api/v3/registrars/${REGISTRAR_CODE}/digitalDocuments/registrarScopeIdentifier/${ID_TYPE}/${ID_VALUE}registrarScopeIdentifiers
 *
 */
public class DeleteRsIdsResolvedByRsIdTests extends ApiV3Tests {

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
    public void deleteRegistrarScopeIdentifiersNotAuthenticated() {
        RsId idInserted1 = new RsId(REGISTRAR_CODE, "deleteTest1", "something1");
        RsId idInserted2 = new RsId(REGISTRAR_CODE, "deleteTest2", "something2");
        // initial delete all
        deleteAllRegistrarScopeIdentifiers(URN_NBN, USER_WITH_RIGHTS);
        // insert idInserted1, idInserted2
        insertRegistrarScopeId(URN_NBN, idInserted1.type, idInserted1.value, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URN_NBN, idInserted2.type, idInserted2.value, USER_WITH_RIGHTS);
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
        List<RsId> rsIdsFetched = getRsIds(URN_NBN);
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
        // cleanup
        deleteAllRegistrarScopeIdentifiers(URN_NBN, USER_WITH_RIGHTS);
    }

    @Test
    public void deleteRegistrarScopeIdentifiersNotAuthorized() {
        RsId idInserted1 = new RsId(REGISTRAR_CODE, "deleteTest1", "something1");
        RsId idInserted2 = new RsId(REGISTRAR_CODE, "deleteTest2", "something2");
        // initial delete all
        deleteAllRegistrarScopeIdentifiers(URN_NBN, USER_WITH_RIGHTS);
        // insert idInserted1, idInserted2
        insertRegistrarScopeId(URN_NBN, idInserted1.type, idInserted1.value, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URN_NBN, idInserted2.type, idInserted2.value, USER_WITH_RIGHTS);
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
        List<RsId> rsIdsFetched = getRsIds(URN_NBN);
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
        // cleanup
        deleteAllRegistrarScopeIdentifiers(URN_NBN, USER_WITH_RIGHTS);
    }

    @Test
    public void deleteRegistrarScopeIdentifiers() {
        RsId idInserted1 = new RsId(REGISTRAR_CODE, "deleteTest1", "something1");
        RsId idInserted2 = new RsId(REGISTRAR_CODE, "deleteTest2", "something2");
        // initial delete all
        deleteAllRegistrarScopeIdentifiers(URN_NBN, USER_WITH_RIGHTS);
        // insert idInserted1, idInserted2
        insertRegistrarScopeId(URN_NBN, idInserted1.type, idInserted1.value, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URN_NBN, idInserted2.type, idInserted2.value, USER_WITH_RIGHTS);
        // delete all ids
        String xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).auth()
                .basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrarScopeIdentifiers", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(idInserted1) + "/registrarScopeIdentifiers")//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.registrarScopeIdentifiers");
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idInserted1.type + "\' }"), equalTo(idInserted1.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idInserted2.type + "\' }"), equalTo(idInserted2.value));
        // get all rsids by urn:nbn (should be empty)
        String url = HTTPS_API_URL + buildResolvationPath(Utils.urlEncodeReservedChars(URN_NBN)) + "/registrarScopeIdentifiers";
        xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).auth().basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrarScopeIdentifiers", nsContext))//
                .when().get(url)//
                .andReturn().asString();
        xmlPath = XmlPath.from(xml).setRoot("response.registrarScopeIdentifiers");
        assertThat(xmlPath.getString("id"), isEmptyOrNullString());
        // cleanup
        deleteAllRegistrarScopeIdentifiers(URN_NBN, USER_WITH_RIGHTS);
    }

}
