package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertThat;

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
 * Tests for GET /api/v3/registrars/${REGISTRAR_CODE}/digitalDocuments/registrarScopeIdentifier/${ID_TYPE}/${ID_VALUE}/registrarScopeIdentifiers
 *
 */
public class GetRegistrarScopeIdentifiersResolvedByRsIdTests extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(GetRegistrarScopeIdentifiersResolvedByRsIdTests.class.getName());

    private final Credentials USER_WITH_RIGHTS = new Credentials("martin", "i0oEhu");
    private final String URNNBN = "urn:nbn:cz:aba001-0005hy";
    private final String REGISTRAR_CODE = "aba001";

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
        RsId idForResolvation = new RsId(registrarCode, "type", "value");
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .when().get(buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers/")//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_REGISTRAR_CODE");
    }

    @Test
    public void registrarCodeValidUnknown() {
        String registrarCode = Utils.getRandomItem(REGISTRAR_CODES_VALID);
        LOGGER.info("registrar code: " + registrarCode);
        RsId idForResolvation = new RsId(registrarCode, "type", "value");
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .when().get(buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers/")//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "UNKNOWN_REGISTRAR");
    }

    @Test
    public void rsIdInvalidType() {
        String type = Utils.getRandomItem(RSID_TYPES_INVALID);
        RsId id = new RsId(REGISTRAR_CODE, type, "value");
        LOGGER.info(id.toString());
        // won't be inserted by error TODO should be returnd anyway
        // get all ids
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildResolvationPath(id) + "/registrarScopeIdentifiers")//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        // test ids found in response
        assertThat(xmlPath.getString("code"), equalTo("INVALID_DIGITAL_DOCUMENT_ID_TYPE"));
    }

    @Test
    public void rsIdInvalidValue() {
        int counter = 0;
        String value = Utils.getRandomItem(RSID_VALUES_INVALID);
        RsId id = new RsId(REGISTRAR_CODE, "reserved" + ++counter, value);
        LOGGER.info(id.toString());
        // same id but with valid value
        RsId idValidValue = new RsId(id.registrarCode, id.type, "value");
        insertRegistrarScopeId(URNNBN, idValidValue, USER_WITH_RIGHTS);
        // get all ids
        // expected http response code 404 and app error code UNKNOWN_DIGITAL_DOCUMENT until this bug fixed:
        // https://github.com/NLCR/CZIDLO/issues/132
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildResolvationPath(id) + "/registrarScopeIdentifiers")//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        // test ids found in response
        assertThat(xmlPath.getString("code"), equalTo("UNKNOWN_DIGITAL_DOCUMENT"));
        // cleanup
        deleteRegistrarScopeId(URNNBN, idValidValue, USER_WITH_RIGHTS);
    }

    @Test
    public void rsIdValid() {
        RsId other = new RsId(REGISTRAR_CODE, "other", "value");
        insertRegistrarScopeId(URNNBN, other, USER_WITH_RIGHTS);
        rsIdValid(new RsId(REGISTRAR_CODE, Utils.getRandomItem(RSID_TYPES_VALID), "value"), other);
        rsIdValid(new RsId(REGISTRAR_CODE, "type", Utils.getRandomItem(RSID_TYPES_VALID)), other);
    }

    private void rsIdValid(RsId id, RsId idOtherInserted) {
        LOGGER.info(id.toString());
        // insert id
        insertRegistrarScopeId(URNNBN, id, USER_WITH_RIGHTS);
        // get all ids
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrarScopeIdentifiers", nsContext))//
                .when().get(buildResolvationPath(id) + "/registrarScopeIdentifiers")//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.registrarScopeIdentifiers");
        // check that ids found in response
        assertThat(xmlPath.getString("id.find { it.@type == \'" + id.type + "\' }"), equalTo(id.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idOtherInserted.type + "\' }"), equalTo(idOtherInserted.value));
        // cleanup
        deleteRegistrarScopeId(URNNBN, id, USER_WITH_RIGHTS);
    }

}
