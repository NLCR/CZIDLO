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
        // String responseXml =
        with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(401)//
                // .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                // .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                // .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(idInserted1) + "/registrarScopeIdentifiers")//
                .andReturn().asString();
        // XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
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
                System.err.println("type: " + id.type + ", value: " + id.value);
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
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_NO_RIGHTS.login, USER_NO_RIGHTS.password)//
                .expect()//
                .statusCode(401)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(idInserted1) + "/registrarScopeIdentifiers")//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
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
    public void deleteRegistrarScopeIdentifiersOk() {
        RsId idForResolvation = new RsId(REGISTRAR_CODE, "resolvation", "something");
        // types
        RsId idTypeMinLength = new RsId(REGISTRAR_CODE, RSID_TYPE_OK_MIN_LENGTH, "typeMinLength");
        RsId idTypeMaxLength = new RsId(REGISTRAR_CODE, RSID_TYPE_OK_MAX_LENGTH, "typeMaxLength");
        RsId[] idsTypereserved = new RsId[RSID_TYPE_OK_RESERVED.length];
        for (int i = 0; i < RSID_TYPE_OK_RESERVED.length; i++) {
            idsTypereserved[i] = new RsId(REGISTRAR_CODE, RSID_TYPE_OK_RESERVED[i], "valueUnreserved");
        }
        RsId[] idsTypeUnreserved = new RsId[RSID_TYPE_OK_UNRESERVED.length];
        for (int i = 0; i < RSID_TYPE_OK_UNRESERVED.length; i++) {
            idsTypeUnreserved[i] = new RsId(REGISTRAR_CODE, RSID_TYPE_OK_UNRESERVED[i], "valueUnreserved");
        }
        // values
        RsId idValueMinLength = new RsId(REGISTRAR_CODE, "minLength", RSID_VALUE_OK_MIN_LENGTH);
        RsId idValueMaxLength = new RsId(REGISTRAR_CODE, "maxLength", RSID_VALUE_OK_MAX_LENGTH);
        RsId[] idValuesReserved = new RsId[RSID_VALUE_OK_RESERVED.length];
        for (int i = 0; i < RSID_VALUE_OK_RESERVED.length; i++) {
            idValuesReserved[i] = new RsId(REGISTRAR_CODE, "reserved" + i, RSID_VALUE_OK_RESERVED[i]);
        }
        RsId[] idValuesUnreserved = new RsId[RSID_VALUE_OK_UNRESERVED.length];
        for (int i = 0; i < RSID_VALUE_OK_UNRESERVED.length; i++) {
            idValuesUnreserved[i] = new RsId(REGISTRAR_CODE, "unreserved" + i, RSID_VALUE_OK_UNRESERVED[i]);
        }

        // insert ids
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idTypeMinLength, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idTypeMaxLength, USER_WITH_RIGHTS);
        for (RsId id : idsTypereserved) {
            insertRegistrarScopeId(URNNBN, id, USER_WITH_RIGHTS);
        }
        for (RsId id : idsTypeUnreserved) {
            insertRegistrarScopeId(URNNBN, id, USER_WITH_RIGHTS);
        }
        insertRegistrarScopeId(URNNBN, idValueMinLength, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idValueMaxLength, USER_WITH_RIGHTS);
        for (RsId id : idValuesReserved) {
            insertRegistrarScopeId(URNNBN, id, USER_WITH_RIGHTS);
        }
        for (RsId id : idValuesUnreserved) {
            insertRegistrarScopeId(URNNBN, id, USER_WITH_RIGHTS);
        }

        // delete all ids
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrarScopeIdentifiers", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers")//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.registrarScopeIdentifiers");

        // check all ids in response
        assertIdFoundInResponse(xmlPath, idForResolvation);
        assertIdFoundInResponse(xmlPath, idTypeMinLength);
        assertIdFoundInResponse(xmlPath, idTypeMaxLength);
        for (RsId id : idsTypereserved) {
            assertIdFoundInResponse(xmlPath, id);
        }
        for (RsId id : idsTypeUnreserved) {
            assertIdFoundInResponse(xmlPath, id);
        }
        assertIdFoundInResponse(xmlPath, idValueMinLength);
        assertIdFoundInResponse(xmlPath, idValueMaxLength);
        for (RsId id : idValuesReserved) {
            assertIdFoundInResponse(xmlPath, id);
        }
        for (RsId id : idValuesUnreserved) {
            assertIdFoundInResponse(xmlPath, id);
        }

        // get all ids by urn:nbn (should be empty)
        responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrarScopeIdentifiers", nsContext))//
                .when().get(HTTPS_API_URL + buildResolvationPath(URNNBN) + "/registrarScopeIdentifiers")//
                .andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.registrarScopeIdentifiers");
        assertThat(xmlPath.getString("id"), isEmptyOrNullString());
    }

    private void assertIdFoundInResponse(XmlPath xmlPath, RsId id) {
        assertThat(xmlPath.getString("id.find { it.@type == \'" + id.type + "\' }"), equalTo(id.value));
    }

}
