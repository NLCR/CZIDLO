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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.api.Utils;

/**
 * Tests for GET
 * /api/v3/registrars/${REGISTRAR_CODE}/digitalDocuments/registrarScopeIdentifier/${ID_TYPE}/${ID_VALUE}/registrarScopeIdentifiers/${ID_TYPE_2}
 *
 */
public class GetRegistrarScopeIdentifierValueResolvedByRsId extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(GetRegistrarScopeIdentifierValueResolvedByRsId.class.getName());

    private final String URNNBN = "urn:nbn:cz:aba001-0005hy";
    private final String REGISTRAR_CODE = "aba001";
    private final Credentials USER_WITH_RIGHTS = new Credentials("martin", "i0oEhu");

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
        String typeToBeFetched = "toBeFetched";
        for (String registrarCode : REGISTRAR_CODES_INVALID) {
            RsId idForResolvation = new RsId(registrarCode, "type", "value");
            LOGGER.info("registrar code: " + registrarCode);
            String responseXml = with().config(namespaceAwareXmlConfig()).expect() //
                    .statusCode(400) //
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString)) //
                    .body(hasXPath("/c:response/c:error/c:message", nsContext)) //
                    .body(hasXPath("/c:response/c:error/c:code", nsContext)) //
                    .when().get(buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers/"//
                            + Utils.urlEncodeReservedChars(typeToBeFetched))//
                    .andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
            Assert.assertEquals(xmlPath.getString("code"), "INVALID_REGISTRAR_CODE");
        }
    }

    @Test
    public void registrarCodeValidUnknown() {
        String typeToBeFetched = "toBeFetched";
        for (String registrarCode : REGISTRAR_CODES_VALID) {
            LOGGER.info("registrar code: " + registrarCode);
            RsId idForResolvation = new RsId(registrarCode, "type", "value");
            String responseXml = with().config(namespaceAwareXmlConfig()).expect() //
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString)) //
                    .body(hasXPath("/c:response/c:error/c:message", nsContext)) //
                    .body(hasXPath("/c:response/c:error/c:code", nsContext)) //
                    .when().get(buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers/"//
                            + Utils.urlEncodeReservedChars(typeToBeFetched))//
                    .andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
            Assert.assertEquals(xmlPath.getString("code"), "UNKNOWN_REGISTRAR");
        }
    }

    @Test
    public void rsIdTypeInvalid() {
        String typeToBeFetched = "toBeFetched";
        for (int i = 0; i < RSID_TYPES_INVALID.length; i++) {
            RsId idForResolvation = new RsId(REGISTRAR_CODE, RSID_TYPES_INVALID[i], "value");
            LOGGER.info(idForResolvation.toString());
            // idForResolvation wasn't inserted, but INVALID_DIGITAL_DOCUMENT_ID_TYPE should be returned before this becomes relevant
            // String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
            String xml = with().config(namespaceAwareXmlConfig())//
                    .expect()//
                    .statusCode(400)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:error", nsContext))//
                    .when().get(buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers"//
                            + Utils.urlEncodeReservedChars(typeToBeFetched))//
                    .andReturn().asString();
            XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
            // TODO:APIv4: rename error to INVALID_ID_TYPE
            assertThat(xmlPath.getString("code"), equalTo("INVALID_DIGITAL_DOCUMENT_ID_TYPE"));
        }
    }

    @Test
    public void rsIdTypeValidValueInvalid() {
        String typeToBeFetched = "toBeFetched";
        for (int i = 0; i < RSID_VALUES_INVALID.length; i++) {
            RsId idForResolvation = new RsId(REGISTRAR_CODE, "reserved" + i, RSID_VALUES_INVALID[i]);
            LOGGER.info(idForResolvation.toString());
            // same id but with valid value
            RsId idValidValue = new RsId(idForResolvation.registrarCode, idForResolvation.type, "value");
            insertRegistrarScopeId(URNNBN, idValidValue, USER_WITH_RIGHTS);
            // idForResolvation wasn't inserted, but INVALID_DIGITAL_DOCUMENT_ID_TYPE should be returned before this becomes relevant
            // expected http response code 404 and app error code UNKNOWN_DIGITAL_DOCUMENT until this bug fixed:
            // https://github.com/NLCR/CZIDLO/issues/132
            // String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
            String xml = with().config(namespaceAwareXmlConfig())//
                    .expect()//
                    .statusCode(404)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:error", nsContext))//
                    .when().get(buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers"//
                            + Utils.urlEncodeReservedChars(typeToBeFetched))//
                    .andReturn().asString();
            XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
            // TODO:APIv4: https://github.com/NLCR/CZIDLO/issues/132
            assertThat(xmlPath.getString("code"), equalTo("UNKNOWN_DIGITAL_DOCUMENT"));
            // cleanup
            deleteRegistrarScopeId(URNNBN, idValidValue, USER_WITH_RIGHTS);
        }
    }

    @Test
    public void rsIdTypeValidValueValid() {
        RsId idToBeFetched = new RsId(REGISTRAR_CODE, "idRequestedType", "idRequestedValue");
        insertRegistrarScopeId(URNNBN, idToBeFetched, USER_WITH_RIGHTS);
        for (int i = 0; i < RSID_TYPES_VALID.length; i++) {
            rsIdTypeValidValueValid(new RsId(REGISTRAR_CODE, RSID_TYPES_VALID[i], "value"), idToBeFetched.type, idToBeFetched.value);
        }
        for (int i = 0; i < RSID_VALUES_VALID.length; i++) {
            rsIdTypeValidValueValid(new RsId(REGISTRAR_CODE, "typeValid" + i, RSID_VALUES_VALID[i]), idToBeFetched.type, idToBeFetched.value);
        }
    }

    private void rsIdTypeValidValueValid(RsId idForResolvation, String requestedType, String expectedValue) {
        LOGGER.info(idForResolvation.toString());
        // insert id for resolvation
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        // get id value byt idForResolvation and type
        // String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
        String xml = with().config(namespaceAwareXmlConfig()) //
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().get(buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(requestedType))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response");
        // check that requested id found in response
        assertThat(xmlPath.getInt("id.size()"), equalTo(1));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + requestedType + "\' }"), equalTo(expectedValue));
        // delete id for resolvation
        deleteRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
    }

    @Test
    public void typeInvalid() {
        RsId idForResolvation = new RsId(REGISTRAR_CODE, "forResolvation", "something");
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        for (String type : RSID_TYPES_INVALID) {
            LOGGER.info("resolved by: " + idForResolvation.toString() + ", type: " + type);
            // get
            // String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
            String xml = with().config(namespaceAwareXmlConfig())//
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
    }

    @Test
    public void typeValidValueNotDefined() {
        RsId idForResolvation = new RsId(REGISTRAR_CODE, "getTest1", "something");
        RsId idToBeFetched = new RsId(REGISTRAR_CODE, "getTest2", "something2");
        // insert idForResolvation
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        // try and get idToGet
        // String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
        String xml = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers/"//
                        + Utils.urlEncodeReservedChars(idToBeFetched.type))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "NOT_DEFINED");
    }

    @Test
    public void typeValidValueDefined() {
        RsId idForResolvation = new RsId(REGISTRAR_CODE, "resolvation", "something");
        RsId idOther = new RsId(REGISTRAR_CODE, "other", "something");
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idOther, USER_WITH_RIGHTS);
        // rs-id types
        for (int i = 0; i < RSID_TYPES_VALID.length; i++) {
            typeValidValueDefined(idForResolvation, idOther, new RsId(REGISTRAR_CODE, RSID_TYPES_VALID[i], "value"));
        }
        // rs-id values
        for (int i = 0; i < RSID_VALUES_VALID.length; i++) {
            typeValidValueDefined(idForResolvation, idOther, new RsId(REGISTRAR_CODE, "reserved" + i, RSID_VALUES_VALID[i]));
        }
    }

    private void typeValidValueDefined(RsId idForResolvation, RsId idInsertedOther, RsId idToGet) {
        LOGGER.info(idToGet.toString());
        // insert id
        insertRegistrarScopeId(URNNBN, idToGet, USER_WITH_RIGHTS);
        // get id
        // String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
        String xml = with().config(namespaceAwareXmlConfig())//
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

}
