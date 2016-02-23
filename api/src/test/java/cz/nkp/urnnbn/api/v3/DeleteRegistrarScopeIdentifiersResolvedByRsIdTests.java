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

/**
 * Tests for DELETE /api/v3/registrars/${REGISTRAR_CODE}/digitalDocuments/registrarScopeIdentifier/${ID_TYPE}/${ID_VALUE}/registrarScopeIdentifiers
 *
 */
public class DeleteRegistrarScopeIdentifiersResolvedByRsIdTests extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(DeleteRegistrarScopeIdentifiersResolvedByRsIdTests.class.getName());

    private final Credentials USER_WITH_RIGHTS = new Credentials("martin", "i0oEhu");
    private final Credentials USER_NO_RIGHTS = new Credentials("nobody", "skgo1dukg");
    private final String REGISTRAR = "aba001";
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
        RsId idInserted1 = new RsId(REGISTRAR, "deleteTest1", "something1");
        RsId idInserted2 = new RsId(REGISTRAR, "deleteTest2", "something2");
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
        RsId idInserted1 = new RsId(REGISTRAR, "deleteTest1", "something1");
        RsId idInserted2 = new RsId(REGISTRAR, "deleteTest2", "something2");
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
        RsId idForResolvation = new RsId(REGISTRAR, "resolvation", "something");
        // rs-id types
        RsId[] idTypes = new RsId[RSID_TYPES_VALID.length];
        for (int i = 0; i < RSID_TYPES_VALID.length; i++) {
            idTypes[i] = new RsId(REGISTRAR, RSID_TYPES_VALID[i], "valueUnreserved");
        }
        // rs-id values
        RsId[] idValues = new RsId[RSID_VALUES_VALID.length];
        for (int i = 0; i < RSID_VALUES_VALID.length; i++) {
            idValues[i] = new RsId(REGISTRAR, "reserved" + i, RSID_VALUES_VALID[i]);
        }

        // insert ids
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        for (RsId id : idTypes) {
            insertRegistrarScopeId(URNNBN, id, USER_WITH_RIGHTS);
        }
        for (RsId id : idValues) {
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
        for (RsId id : idTypes) {
            assertIdFoundInResponse(xmlPath, id);
        }
        for (RsId id : idValues) {
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

    @Test
    public void deleteRegistrarScopeIdentifiersInvalidResolvationType() {
        for (String type : RSID_TYPES_INVALID) {
            deleteRegistrarScopeIdentifiersInvalidResolvationType(new RsId(REGISTRAR, type, "value"));
        }
    }

    private void deleteRegistrarScopeIdentifiersInvalidResolvationType(RsId id) {
        LOGGER.info(id.toString());
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .expect() //
                .statusCode(400) //
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString)) //
                .body(hasXPath("/c:response/c:error", nsContext)) //
                .when().delete(HTTPS_API_URL + buildResolvationPath(id) + "/registrarScopeIdentifiers")//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        // TODO:APIv4: rename error to INVALID_ID_TYPE
        Assert.assertEquals(xmlPath.get("code"), "INVALID_DIGITAL_DOCUMENT_ID_TYPE");
    }

    @Test
    public void deleteRegistrarScopeIdentifiersInvalidResolvationValue() {
        for (int i = 0; i < RSID_VALUES_INVALID.length; i++) {
            deleteRegistrarScopeIdentifiersInvalidResolvationValue(new RsId(REGISTRAR, "type" + i, RSID_VALUES_INVALID[i]));
        }
    }

    private void deleteRegistrarScopeIdentifiersInvalidResolvationValue(RsId id) {
        // TODO: enable after this bug is fixed: https://github.com/NLCR/CZIDLO/issues/132
        // String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
        // .expect() //
        // .statusCode(400) //
        // .contentType(ContentType.XML).body(matchesXsd(responseXsdString)) //
        // .body(hasXPath("/c:response/c:error", nsContext)) //
        // .when().get(buildResolvationPath(id))//
        // .andReturn().asString();
        // XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        // // TODO:APIv4: rename error to INVALID_ID_VALUE
        // Assert.assertEquals(xmlPath.get("code"), "TODO");
        LOGGER.info("deleteRegistrarScopeIdentifiersInvalidResolvationValue ignored untill issue 132 is fixed");
    }

}