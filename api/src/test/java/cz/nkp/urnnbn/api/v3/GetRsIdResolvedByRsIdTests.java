package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertThat;

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
 * Tests for GET
 * /api/v3/registrars/${REGISTRAR_CODE}/digitalDocuments/registrarScopeIdentifier/${ID_TYPE}/${ID_VALUE}/registrarScopeIdentifiers/${ID_TYPE_2}
 *
 */
public class GetRsIdResolvedByRsIdTests extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(GetRsIdResolvedByRsIdTests.class.getName());

    private final Credentials USER_WITH_RIGHTS = new Credentials("martin", "i0oEhu");
    private final String URNNBN = "urn:nbn:cz:aba001-0005hy";
    private final String REGISTRAR_CODE = "aba001";

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

    // TODO: test ID_TYPE, ID_VALUE for valid/invalid type and value

    @Test
    public void getRegistrarScopeIdentifierOk() {
        RsId idForResolvation = new RsId(REGISTRAR_CODE, "resolvation", "something");
        RsId idOther = new RsId(REGISTRAR_CODE, "other", "something");
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idOther, USER_WITH_RIGHTS);
        // test id types
        getRegistrarScopeIdentifierOk(idForResolvation, idOther, new RsId(REGISTRAR_CODE, RSID_TYPE_OK_MIN_LENGTH, "value"));
        getRegistrarScopeIdentifierOk(idForResolvation, idOther, new RsId(REGISTRAR_CODE, RSID_TYPE_OK_MAX_LENGTH, "value"));
        for (int i = 0; i < RSID_TYPES_OK_RESERVED.length; i++) {
            getRegistrarScopeIdentifierOk(idForResolvation, idOther, new RsId(REGISTRAR_CODE, RSID_TYPES_OK_RESERVED[i], "value"));
        }
        for (int i = 0; i < RSID_TYPES_OK_UNRESERVED.length; i++) {
            getRegistrarScopeIdentifierOk(idForResolvation, idOther, new RsId(REGISTRAR_CODE, RSID_TYPES_OK_UNRESERVED[i], "value"));
        }
        // test id values
        getRegistrarScopeIdentifierOk(idForResolvation, idOther, new RsId(REGISTRAR_CODE, "minLength", RSID_VALUES_OK_MIN_LENGTH));
        getRegistrarScopeIdentifierOk(idForResolvation, idOther, new RsId(REGISTRAR_CODE, "maxLength", RSID_VALUES_OK_MAX_LENGTH));
        for (int i = 0; i < RSID_VALUE_OK_RESERVED.length; i++) {
            getRegistrarScopeIdentifierOk(idForResolvation, idOther, new RsId(REGISTRAR_CODE, "reserved" + i, RSID_VALUE_OK_RESERVED[i]));
        }
        for (int i = 0; i < RSID_VALUE_OK_UNRESERVED.length; i++) {
            getRegistrarScopeIdentifierOk(idForResolvation, idOther, new RsId(REGISTRAR_CODE, "unreserved" + i, RSID_VALUE_OK_UNRESERVED[i]));
        }
    }

    private void getRegistrarScopeIdentifierOk(RsId idForResolvation, RsId idInsertedOther, RsId idToGet) {
        // insert id
        insertRegistrarScopeId(URNNBN, idToGet, USER_WITH_RIGHTS);
        // get id
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().get(buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(idToGet.type))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response");
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idToGet.type + "\' }"), equalTo(idToGet.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idForResolvation.type + "\' }"), isEmptyOrNullString());
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idInsertedOther.type + "\' }"), isEmptyOrNullString());
        // remove id
        deleteRegistrarScopeId(URNNBN, idToGet, USER_WITH_RIGHTS);
    }

    @Test
    public void getRegistrarScopeIdentifierTypeInvalid() {
        RsId idForResolvation = new RsId(REGISTRAR_CODE, "forResolvation", "something");
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);

        // invalid reserved characters
        for (String type : RSID_TYPES_INVALID_RESERVED) {
            getRegistrarScopeIdentifierTypeInvalid(idForResolvation, type);
        }
        // invalid unreserved characters
        for (String type : RSID_TYPES_INVALID_UNRESERVED) {
            getRegistrarScopeIdentifierTypeInvalid(idForResolvation, type);
        }
    }

    private void getRegistrarScopeIdentifierTypeInvalid(RsId idForResolvation, String type) {
        // get
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(type))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response");
        // TODO:APIv4: rename this error code
        assertThat(xmlPath.getString("error.code"), equalTo("INVALID_DIGITAL_DOCUMENT_ID_TYPE"));
    }

    @Test
    public void getRegistrarScopeIdentifierNotDefined() {
        RsId idForResolvation = new RsId(REGISTRAR_CODE, "getTest1", "something");
        RsId idToGet = new RsId(REGISTRAR_CODE, "getTest2", "something2");
        // insert idForResolvation
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        // try and get idToGet
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(idToGet.type))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "NOT_DEFINED");
    }

    @Test
    public void getRegistrarScopeIdentifierRegistrarCodesInvalid() {
        for (String registrarCode : REGISTRAR_CODES_INVALID) {
            RsId idForResolvation = new RsId(registrarCode, "type", "value");
            RsId idToGet = new RsId(registrarCode, "type2", "value2");
            LOGGER.info("registrar code: " + registrarCode);
            String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                    .statusCode(400)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                    .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                    .when().get(buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(idToGet.type))//
                    .andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
            String errorCode = xmlPath.getString("code");
            // LOGGER.info("error code: " + errorCode);
            Assert.assertEquals(errorCode, "INVALID_REGISTRAR_CODE");
        }
    }

    @Test
    public void getRegistrarScopeIdentifierRegistrarCodesValid() {
        for (String registrarCode : REGISTRAR_CODES_VALID) {
            LOGGER.info("registrar code: " + registrarCode);
            RsId idForResolvation = new RsId(registrarCode, "type", "value");
            RsId idToGet = new RsId(registrarCode, "type2", "value2");
            String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response", nsContext))//
                    .when().get(buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(idToGet.type))//
                    .andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
            String errorCode = xmlPath.getString("error.code");
            // LOGGER.info("error code: " + errorCode);
            Assert.assertTrue("UNKNOWN_REGISTRAR".equals(errorCode) || "UNKNOWN_DIGITAL_DOCUMENT".equals(errorCode));
        }
    }

}
