package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertThat;

import java.util.logging.Logger;

import org.testng.Assert;
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

    @Test
    public void registrarCodeInvalid() {
        String typeToBeFetched = "toBeFetched";
        String registrarCode = Utils.getRandomItem(REGISTRAR_CODES_INVALID);
        RsId idForResolvation = new RsId(registrarCode, "type", "value");
        LOGGER.info("registrar code: " + registrarCode);
        String responseXml = with().config(namespaceAwareXmlConfig()).expect() //
                .statusCode(400) //
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString)) //
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers/"//
                        + Utils.urlEncodeReservedChars(typeToBeFetched))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_REGISTRAR_CODE");
    }

    @Test
    public void registrarCodeValidUnknown() {
        String typeToBeFetched = "toBeFetched";
        String registrarCode = Utils.getRandomItem(REGISTRAR_CODES_VALID);
        LOGGER.info("registrar code: " + registrarCode);
        RsId idForResolvation = new RsId(registrarCode, "type", "value");
        String responseXml = with().config(namespaceAwareXmlConfig()).expect() //
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString)) //
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers/"//
                        + Utils.urlEncodeReservedChars(typeToBeFetched))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "UNKNOWN_REGISTRAR");
    }

    @Test
    public void rsIdTypeInvalid() {
        String typeToBeFetched = "toBeFetched";
        RsId idForResolvation = new RsId(REGISTRAR_CODE, Utils.getRandomItem(RSID_TYPES_INVALID), "value");
        LOGGER.info(idForResolvation.toString());
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

    @Test
    public void rsIdTypeValidValueInvalid() {
        String typeToBeFetched = "toBeFetched";
        RsId idForResolvation = new RsId(REGISTRAR_CODE, "type", Utils.getRandomItem(RSID_VALUES_INVALID));
        LOGGER.info(idForResolvation.toString());
        // try and get rsId by type, resolved by another rsId
        String xml = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers"//
                        + Utils.urlEncodeReservedChars(typeToBeFetched))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        // TODO:APIv4: https://github.com/NLCR/CZIDLO/issues/132 (INVALID_REGISTRAR_SCOPE_ID_VALUE, code 400)
        assertThat(xmlPath.getString("code"), equalTo("UNKNOWN_DIGITAL_DOCUMENT"));
    }

    @Test
    public void unknowDigitalDocument() {
        String typeToBeFetched = "toBeFetched";
        RsId idForResolvation = new RsId(REGISTRAR_CODE, "type", "value");
        LOGGER.info("resolved by: " + idForResolvation.toString() + ", type: " + typeToBeFetched);
        String xml = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers"//
                        + Utils.urlEncodeReservedChars(typeToBeFetched))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        assertThat(xmlPath.getString("code"), equalTo("UNKNOWN_DIGITAL_DOCUMENT"));
    }

    @Test
    public void typeInvalid() {
        RsId idForResolvation = new RsId(REGISTRAR_CODE, "forResolvation", "something");
        String typeToBeFetched = Utils.getRandomItem(RSID_TYPES_INVALID);
        LOGGER.info("resolved by: " + idForResolvation.toString() + ", type: " + typeToBeFetched);
        // insert id for resolvation
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        // try and get rsId by type, resolved by another rsId
        String responseXml = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(typeToBeFetched))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        // TODO:APIv4: rename this error code
        assertThat(xmlPath.getString("error.code"), equalTo("INVALID_DIGITAL_DOCUMENT_ID_TYPE"));
    }

    @Test
    public void notDefined() {
        RsId idForResolvation = new RsId(REGISTRAR_CODE, "type1", "value1");
        RsId idToBeFetched = new RsId(REGISTRAR_CODE, "type2", "value2");
        // insert idForResolvation
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        // try and get rsId by type, resolved by another rsId
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
    public void ok() {
        RsId idForResolvation = new RsId(REGISTRAR_CODE, "type1", "value1");
        RsId idToBeFetched = new RsId(REGISTRAR_CODE, "type2", "value2");
        LOGGER.info(String.format("for resolvation: %s, to be fetched: %s", idForResolvation.type, idToBeFetched.toString()));
        // insert ids
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idToBeFetched, USER_WITH_RIGHTS);
        // try and get rsId by type, resolved by another rsId
        String xml = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().get(buildResolvationPath(idForResolvation) //
                        + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(idToBeFetched.type))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response");
        // check that only requested id found in response
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idToBeFetched.type + "\' }"), equalTo(idToBeFetched.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idForResolvation.type + "\' }"), isEmptyOrNullString());
    }

}