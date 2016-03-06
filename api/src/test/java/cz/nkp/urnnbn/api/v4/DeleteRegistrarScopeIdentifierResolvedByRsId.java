package cz.nkp.urnnbn.api.v4;

import static com.jayway.restassured.RestAssured.delete;
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
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.api.Utils;
import cz.nkp.urnnbn.api.pojo.RsId;

/**
 * Tests for DELETE
 * /api/v4/registrars/${REGISTRAR_CODE}/digitalDocuments/registrarScopeIdentifier/${ID_TYPE}/${ID_VALUE}/registrarScopeIdentifiers/${ID_TYPE_2}
 *
 */
public class DeleteRegistrarScopeIdentifierResolvedByRsId extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(DeleteRegistrarScopeIdentifierResolvedByRsId.class.getName());

    private String urnNbn;

    @BeforeClass
    public void beforeClass() {
        init();
        urnNbn = registerUrnNbn(REGISTRAR, USER);
    }

    @AfterMethod
    public void afterMethod() {
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
    }

    private String buildUrl(RsId idForResolvation, String type) {
        return HTTPS_API_URL + buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(type);
    }

    @Test
    public void registrarCodeInvalid() {
        String registrarCode = Utils.getRandomItem(REGISTRAR_CODES_INVALID);
        LOGGER.info("registrar code: " + registrarCode);
        RsId idForResolvation = new RsId(registrarCode, "type1", "value1");
        String typeToDelete = "type2";
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(buildUrl(idForResolvation, typeToDelete)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_REGISTRAR_CODE");
    }

    @Test
    public void registrarCodeValidUnknown() {
        String registrarCode = Utils.getRandomItem(REGISTRAR_CODES_VALID);
        LOGGER.info("registrar code: " + registrarCode);
        RsId idForResolvation = new RsId(registrarCode, "type", "value");
        String typeToDelete = "type2";
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(buildUrl(idForResolvation, typeToDelete)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "UNKNOWN_REGISTRAR");
    }

    @Test
    public void rsIdTypeInvalid() {
        RsId idForResolvation = new RsId(REGISTRAR, Utils.getRandomItem(RSID_TYPES_INVALID), "value");
        LOGGER.info(idForResolvation.toString());
        String typeToDelete = "type2";
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect() //
                .statusCode(400) //
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString)) //
                .body(hasXPath("/c:response/c:error", nsContext)) //
                .when().delete(buildUrl(idForResolvation, typeToDelete)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_REGISTRAR_SCOPE_ID_TYPE");
    }

    @Test
    public void rsIdTypeValidValueInvalid() {
        RsId idForResolvation = new RsId(REGISTRAR, "type", Utils.getRandomItem(RSID_VALUES_INVALID));
        LOGGER.info(idForResolvation.toString());
        String typeToDelete = "type2";
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(buildUrl(idForResolvation, typeToDelete)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertThat(xmlPath.getString("code"), equalTo("INVALID_REGISTRAR_SCOPE_ID_VALUE"));
    }

    @Test
    public void unknowDigitalDocument() {
        RsId idForResolvation = new RsId(REGISTRAR, "type", "value");
        LOGGER.info(idForResolvation.toString());
        String typeToDelete = "type2";
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(buildUrl(idForResolvation, typeToDelete)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertThat(xmlPath.getString("code"), equalTo("UNKNOWN_DIGITAL_DOCUMENT"));
    }

    @Test
    public void typeInvalid() {
        RsId idForResolvation = new RsId(REGISTRAR, "type", "value");
        String typeToDelete = Utils.getRandomItem(RSID_TYPES_INVALID);
        LOGGER.info(String.format("resolved by: %s, delete type: %s", idForResolvation.toString(), typeToDelete));
        // insert id for resolvation
        insertRegistrarScopeId(urnNbn, idForResolvation, USER);
        // try and delete rsId by type, resolved by another rsId
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(buildUrl(idForResolvation, typeToDelete)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertThat(xmlPath.getString("code"), equalTo("INVALID_REGISTRAR_SCOPE_ID_TYPE"));
    }

    @Test
    public void notDefined() {
        RsId idForResolvation = new RsId(REGISTRAR, "type1", "value1");
        String typeToDelete = "type2";
        // insert idForResolvation
        insertRegistrarScopeId(urnNbn, idForResolvation, USER);
        // try and delete rsId by type, resolved by another rsId
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(buildUrl(idForResolvation, typeToDelete)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "UNKNOWN_REGISTRAR_SCOPE_IDENTIFIER");
    }

    @Test
    public void notAuthenticated() {
        RsId idForResolvation = new RsId(REGISTRAR, "type", "value");
        RsId idToDelete = new RsId(REGISTRAR, "type2", "value2");
        LOGGER.info(String.format("resolved by: %s, delete type: %s", idForResolvation.toString(), idToDelete.type));
        // insert ids
        insertRegistrarScopeId(urnNbn, idForResolvation, USER);
        insertRegistrarScopeId(urnNbn, idToDelete, USER);
        // try and delete id
        delete(buildUrl(idForResolvation, idToDelete.type)).then().assertThat().statusCode(401);
        // check that all ids still present
        List<RsId> rsIdsFetched = getRsIds(urnNbn);
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
        insertRegistrarScopeId(urnNbn, idForResolvation, USER);
        insertRegistrarScopeId(urnNbn, idToDelete, USER);
        // try and delete id
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_NO_RIGHTS.login, USER_NO_RIGHTS.password)//
                .expect()//
                .statusCode(403)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(buildUrl(idForResolvation, idToDelete.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "NO_ACCESS_RIGHTS");
        // check that all ids still present
        List<RsId> rsIdsFetched = getRsIds(urnNbn);
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
        insertRegistrarScopeId(urnNbn, idForResolvation, USER);
        insertRegistrarScopeId(urnNbn, idToDelete, USER);
        // try and delete id
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().delete(buildUrl(idForResolvation, idToDelete.type)).andReturn().asString();
        // check that id in response
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        assertEquals(1, xmlPath.getInt("id.size()"));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idToDelete.type + "\' }"), equalTo(idToDelete.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idForResolvation.type + "\' }"), isEmptyOrNullString());
        // check that all ids have been deleted
        responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrarScopeIdentifiers", nsContext))//
                .when().get(HTTPS_API_URL + buildResolvationPath(urnNbn) + "/registrarScopeIdentifiers")//
                .andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.registrarScopeIdentifiers");
        assertEquals(1, xmlPath.getInt("id.size()"));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idToDelete.type + "\' }"), isEmptyOrNullString());
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idForResolvation.type + "\' }"), equalTo(idForResolvation.value));
    }

}
