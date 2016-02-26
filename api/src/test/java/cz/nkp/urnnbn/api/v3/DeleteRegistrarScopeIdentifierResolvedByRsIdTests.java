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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.api.Utils;

/**
 * Tests for DELETE
 * /api/v3/registrars/${REGISTRAR_CODE}/digitalDocuments/registrarScopeIdentifier/${ID_TYPE}/${ID_VALUE}/registrarScopeIdentifiers/${ID_TYPE_2}
 *
 */
public class DeleteRegistrarScopeIdentifierResolvedByRsIdTests extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(DeleteRegistrarScopeIdentifierResolvedByRsIdTests.class.getName());

    private final String REGISTRAR = "aba001";
    private final String URNNBN = "urn:nbn:cz:aba001-0005hy";
    private final Credentials USER_WITH_RIGHTS = new Credentials("martin", "i0oEhu");
    private final Credentials USER_NO_RIGHTS = new Credentials("nobody", "skgo1dukg");

    @BeforeClass
    public void beforeClass() {
        init();
    }

    @BeforeMethod
    public void beforeMethod() {
        deleteAllRegistrarScopeIdentifiers(URNNBN, USER_WITH_RIGHTS);
    }

    @AfterMethod
    public void afterMethod() {
        deleteAllRegistrarScopeIdentifiers(URNNBN, USER_WITH_RIGHTS);
    }

    @Test
    public void registrarCodeInvalid() {
        String registrarCode = Utils.getRandomItem(REGISTRAR_CODES_INVALID);
        LOGGER.info("registrar code: " + registrarCode);
        RsId idForResolvation = new RsId(registrarCode, "type1", "value1");
        String typeToDelete = "type2";
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(idForResolvation)//
                        + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(typeToDelete))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_REGISTRAR_CODE");
    }

    @Test
    public void registrarCodeValidUnknown() {
        String registrarCode = Utils.getRandomItem(REGISTRAR_CODES_VALID);
        LOGGER.info("registrar code: " + registrarCode);
        RsId idForResolvation = new RsId(registrarCode, "type", "value");
        String typeToDelete = "type2";
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .expect()//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(idForResolvation)//
                        + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(typeToDelete))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "UNKNOWN_REGISTRAR");
    }

    @Test
    public void rsIdTypeInvalid() {
        RsId idForResolvation = new RsId(REGISTRAR, Utils.getRandomItem(RSID_TYPES_INVALID), "value");
        LOGGER.info(idForResolvation.toString());
        String typeToDelete = "type2";
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .expect() //
                .statusCode(400) //
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString)) //
                .body(hasXPath("/c:response/c:error", nsContext)) //
                .when().delete(HTTPS_API_URL + buildResolvationPath(idForResolvation)//
                        + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(typeToDelete))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        // TODO:APIv4: rename error to INVALID_ID_TYPE
        Assert.assertEquals(xmlPath.get("code"), "INVALID_DIGITAL_DOCUMENT_ID_TYPE");
    }

    @Test
    public void rsIdTypeValidValueInvalid() {
        RsId idForResolvation = new RsId(REGISTRAR, "type", Utils.getRandomItem(RSID_TYPES_INVALID));
        LOGGER.info(idForResolvation.toString());
        String typeToDelete = "type2";
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(idForResolvation)//
                        + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(typeToDelete))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        // TODO:APIv4: https://github.com/NLCR/CZIDLO/issues/132
        assertThat(xmlPath.getString("code"), equalTo("UNKNOWN_DIGITAL_DOCUMENT"));
    }

    @Test
    public void unknowDigitalDocument() {
        RsId idForResolvation = new RsId(REGISTRAR, "type", "value");
        LOGGER.info(idForResolvation.toString());
        String typeToDelete = "type2";
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(idForResolvation)//
                        + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(typeToDelete))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertThat(xmlPath.getString("code"), equalTo("UNKNOWN_DIGITAL_DOCUMENT"));
    }

    @Test
    public void typeInvalid() {
        RsId idForResolvation = new RsId(REGISTRAR, "type", "value");
        String typeInvalid = Utils.getRandomItem(RSID_TYPES_INVALID);
        LOGGER.info(String.format("resolved by: %s, delete type: %s", idForResolvation.toString(), typeInvalid));
        // insert id for resolvation
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        // try and delete rsId by type, resolved by another rsId
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_NO_RIGHTS.login, USER_NO_RIGHTS.password)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(idForResolvation)//
                        + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(typeInvalid))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        // TODO:APIv4: rename this error code
        assertThat(xmlPath.getString("error.code"), equalTo("INVALID_DIGITAL_DOCUMENT_ID_TYPE"));
        // check that id has not been deleted
        List<RsId> rsIdsFetched = getRsIds(URNNBN);
        boolean found = false;
        for (RsId id : rsIdsFetched) {
            if (id.type.equals(idForResolvation.type)) {
                assertThat(id.value, equalTo(idForResolvation.value));
                found = true;
            }
        }
        Assert.assertTrue(found);
    }

    @Test
    public void notDefined() {
        RsId idForResolvation = new RsId(REGISTRAR, "type1", "value1");
        String typeToDelete = "type2";
        // insert idForResolvation
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        // try and delete rsId by type, resolved by another rsId
        String xml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_NO_RIGHTS.login, USER_NO_RIGHTS.password)//
                .expect()//
                .statusCode(400)// TODO:APIv4: code 404
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(idForResolvation)//
                        + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(typeToDelete))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        // TODO:APIv4: INVALID_REGISTRAR_SCOPE_IDENTIFIER rename to NOT_DEFINED
        Assert.assertEquals(xmlPath.get("code"), "INVALID_REGISTRAR_SCOPE_IDENTIFIER");
    }

    @Test
    public void notAuthenticated() {
        RsId idForResolvation = new RsId(REGISTRAR, "type", "value");
        RsId idToDelete = new RsId(REGISTRAR, "type2", "value2");
        LOGGER.info(String.format("resolved by: %s, delete type: %s", idForResolvation.toString(), idToDelete.type));
        // insert ids
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idToDelete, USER_WITH_RIGHTS);
        // try and delete id
        // TODO:APIv4: return xml as well
        // String responseXml =
        with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(401)//
                // .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                // .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(idForResolvation)//
                        + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(idToDelete.type))//
                .andReturn().asString();
        // XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        // Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHENTICATED");
        // check that all ids still present
        List<RsId> rsIdsFetched = getRsIds(URNNBN);
        assertThat(rsIdsFetched.size(), equalTo(2));
        for (RsId id : rsIdsFetched) {
            if (id.type.equals(idForResolvation.type)) {
                assertThat(id.value, equalTo(idForResolvation.value));
            } else if (id.type.equals(idToDelete.type)) {
                assertThat(id.value, equalTo(idToDelete.value));
            } else {// unexpected id type
                Assert.fail();
            }
        }
    }

    @Test
    public void notAuthorized() {
        RsId idForResolvation = new RsId(REGISTRAR, "type", "value");
        RsId idToDelete = new RsId(REGISTRAR, "type2", "value2");
        LOGGER.info(String.format("resolved by: %s, delete type: %s", idForResolvation.toString(), idToDelete.type));
        // insert ids
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idToDelete, USER_WITH_RIGHTS);
        // try and delete id
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_NO_RIGHTS.login, USER_NO_RIGHTS.password)//
                .expect()//
                .statusCode(401)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(idForResolvation)//
                        + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(idToDelete.type))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHORIZED");
        // check that all ids still present
        List<RsId> rsIdsFetched = getRsIds(URNNBN);
        assertThat(rsIdsFetched.size(), equalTo(2));
        for (RsId id : rsIdsFetched) {
            if (id.type.equals(idForResolvation.type)) {
                assertThat(id.value, equalTo(idForResolvation.value));
            } else if (id.type.equals(idToDelete.type)) {
                assertThat(id.value, equalTo(idToDelete.value));
            } else {// unexpected id type
                Assert.fail();
            }
        }
    }

    @Test
    public void ok() {
        RsId idForResolvation = new RsId(REGISTRAR, "type", "value");
        RsId idToDelete = new RsId(REGISTRAR, "type2", "value2");
        LOGGER.info(String.format("resolved by: %s, delete type: %s", idForResolvation.toString(), idToDelete.type));
        // insert ids
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idToDelete, USER_WITH_RIGHTS);
        // try and delete id
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(idForResolvation)//
                        + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(idToDelete.type))//
                .andReturn().asString();
        // check that id in response
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        assertEquals(1, xmlPath.getInt("id.size()"));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idToDelete.type + "\' }"), equalTo(idToDelete.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idForResolvation.type + "\' }"), isEmptyOrNullString());
        // check that all ids have been deleted
        responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrarScopeIdentifiers", nsContext))//
                .when().get(HTTPS_API_URL + buildResolvationPath(URNNBN) + "/registrarScopeIdentifiers")//
                .andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.registrarScopeIdentifiers");
        assertEquals(1, xmlPath.getInt("id.size()"));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idToDelete.type + "\' }"), isEmptyOrNullString());
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idForResolvation.type + "\' }"), equalTo(idForResolvation.value));
    }

}
