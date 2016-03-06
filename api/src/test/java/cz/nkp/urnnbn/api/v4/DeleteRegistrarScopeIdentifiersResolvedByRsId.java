package cz.nkp.urnnbn.api.v4;

import static com.jayway.restassured.RestAssured.delete;
import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
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
 * Tests for DELETE /api/v4/registrars/${REGISTRAR_CODE}/digitalDocuments/registrarScopeIdentifier/${ID_TYPE}/${ID_VALUE}/registrarScopeIdentifiers
 *
 */
public class DeleteRegistrarScopeIdentifiersResolvedByRsId extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(DeleteRegistrarScopeIdentifiersResolvedByRsId.class.getName());

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

    private String buildUrl(RsId idForResolvation) {
        return HTTPS_API_URL + buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers";
    }

    @Test
    public void registrarCodeInvalid() {
        String registrarCode = Utils.getRandomItem(REGISTRAR_CODES_INVALID);
        LOGGER.info("registrar code: " + registrarCode);
        RsId idForResolvation = new RsId(registrarCode, "type", "value");
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(buildUrl(idForResolvation)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_REGISTRAR_CODE");
    }

    @Test
    public void registrarCodeValidUnknown() {
        String registrarCode = Utils.getRandomItem(REGISTRAR_CODES_VALID);
        LOGGER.info("registrar code: " + registrarCode);
        RsId idForResolvation = new RsId(registrarCode, "type", "value");
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(buildUrl(idForResolvation)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "UNKNOWN_REGISTRAR");
    }

    @Test
    public void rsIdTypeInvalid() {
        RsId idForResolvation = new RsId(REGISTRAR, Utils.getRandomItem(RSID_TYPES_INVALID), "value");
        LOGGER.info(idForResolvation.toString());
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect() //
                .statusCode(400) //
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString)) //
                .body(hasXPath("/c:response/c:error", nsContext)) //
                .when().delete(buildUrl(idForResolvation)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        // TODO:APIv4: rename error to INVALID_REGISTRAR_SCOPE_ID_TYPE
        Assert.assertEquals(xmlPath.get("code"), "INVALID_DIGITAL_DOCUMENT_ID_TYPE");
    }

    @Test
    public void rsIdTypeValidValueInvalid() {
        RsId idForResolvation = new RsId(REGISTRAR, "type", Utils.getRandomItem(RSID_VALUES_INVALID));
        LOGGER.info(idForResolvation.toString());
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(buildUrl(idForResolvation)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        // TODO:APIv4: rename to INVALID_REGISTRAR_SCOPE_ID_VALUE
        assertThat(xmlPath.getString("code"), equalTo("INVALID_DIGITAL_DOCUMENT_ID_VALUE"));
    }

    @Test
    public void unknowDigitalDocument() {
        RsId idForResolvation = new RsId(REGISTRAR, "type", "value");
        LOGGER.info(idForResolvation.toString());
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(buildUrl(idForResolvation)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertThat(xmlPath.getString("code"), equalTo("UNKNOWN_DIGITAL_DOCUMENT"));
    }

    @Test
    public void notAuthenticated() {
        RsId idForResolvation = new RsId(REGISTRAR, "type", "value");
        LOGGER.info(idForResolvation.toString());
        RsId idOther = new RsId(REGISTRAR, "type2", "value2");
        // insert ids
        insertRegistrarScopeId(urnNbn, idForResolvation, USER);
        insertRegistrarScopeId(urnNbn, idOther, USER);
        // try and delete all ids
        delete(buildUrl(idForResolvation)).then().assertThat().statusCode(401);
        // check that all ids still present
        List<RsId> rsIdsFetched = getRsIds(urnNbn);
        assertThat(rsIdsFetched.size(), equalTo(2));
        for (RsId id : rsIdsFetched) {
            if (id.type.equals(idForResolvation.type)) {
                assertThat(id.value, equalTo(idForResolvation.value));
            } else if (id.type.equals(idOther.type)) {
                assertThat(id.value, equalTo(idOther.value));
            } else {// unexpected id type
                Assert.fail();
            }
        }
    }

    @Test
    public void notAuthorized() {
        RsId idForResolvation = new RsId(REGISTRAR, "type", "value");
        LOGGER.info(idForResolvation.toString());
        RsId idOther = new RsId(REGISTRAR, "type2", "value2");
        // insert ids
        insertRegistrarScopeId(urnNbn, idForResolvation, USER);
        insertRegistrarScopeId(urnNbn, idOther, USER);
        // try and delete all ids
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_NO_RIGHTS.login, USER_NO_RIGHTS.password)//
                .expect()//
                .statusCode(403)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(buildUrl(idForResolvation)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "NO_ACCESS_RIGHS");
        // check that all ids still present
        List<RsId> rsIdsFetched = getRsIds(urnNbn);
        assertThat(rsIdsFetched.size(), equalTo(2));
        for (RsId id : rsIdsFetched) {
            if (id.type.equals(idForResolvation.type)) {
                assertThat(id.value, equalTo(idForResolvation.value));
            } else if (id.type.equals(idOther.type)) {
                assertThat(id.value, equalTo(idOther.value));
            } else {// unexpected id type
                Assert.fail();
            }
        }
    }

    @Test
    public void ok() {
        RsId idForResolvation = new RsId(REGISTRAR, "type1", "value1");
        LOGGER.info(idForResolvation.toString());
        // create ids
        List<RsId> ids = new ArrayList<>();
        ids.add(idForResolvation);
        ids.add(new RsId(REGISTRAR, "type2", "value2"));
        ids.add(new RsId(REGISTRAR, "type3", "value3"));
        // insert ids
        for (RsId id : ids) {
            insertRegistrarScopeId(urnNbn, id, USER);
        }
        // delete all ids
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrarScopeIdentifiers", nsContext))//
                .when().delete(buildUrl(idForResolvation)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.registrarScopeIdentifiers");
        System.err.println(responseXml);
        // check that all ids in response
        for (RsId id : ids) {
            assertThat(xmlPath.getString("id.find { it.@type == \'" + id.type + "\' }"), equalTo(id.value));
        }
        // check that all ids have been deleted
        responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrarScopeIdentifiers", nsContext))//
                .when().get(HTTPS_API_URL + buildResolvationPath(urnNbn) + "/registrarScopeIdentifiers")//
                .andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.registrarScopeIdentifiers");
        assertThat(xmlPath.getString("id"), isEmptyOrNullString());
    }
}
