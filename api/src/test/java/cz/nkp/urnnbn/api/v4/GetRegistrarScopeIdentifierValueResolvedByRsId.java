package cz.nkp.urnnbn.api.v4;

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
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.api.Utils;
import cz.nkp.urnnbn.api.pojo.RsId;

/**
 * Tests for GET
 * /api/v4/registrars/${REGISTRAR_CODE}/digitalDocuments/registrarScopeIdentifier/${ID_TYPE}/${ID_VALUE}/registrarScopeIdentifiers/${ID_TYPE_2}
 *
 */
public class GetRegistrarScopeIdentifierValueResolvedByRsId extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(GetRegistrarScopeIdentifierValueResolvedByRsId.class.getName());

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
        String typeToBeFetched = "toBeFetched";
        String registrarCode = Utils.getRandomItem(REGISTRAR_CODES_INVALID);
        RsId idForResolvation = new RsId(registrarCode, "type", "value");
        LOGGER.info("registrar code: " + registrarCode);
        String responseXml = with().config(namespaceAwareXmlConfig()).expect() //
                .statusCode(400) //
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString)) //
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(idForResolvation, typeToBeFetched)).andReturn().asString();
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
                .when().get(buildUrl(idForResolvation, typeToBeFetched)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "UNKNOWN_REGISTRAR");
    }

    @Test
    public void rsIdTypeInvalid() {
        String typeToBeFetched = "toBeFetched";
        RsId idForResolvation = new RsId(REGISTRAR, Utils.getRandomItem(RSID_TYPES_INVALID), "value");
        LOGGER.info(idForResolvation.toString());
        String responseXml = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(idForResolvation, typeToBeFetched)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        // TODO:APIv4: rename error to INVALID_ID_TYPE
        assertThat(xmlPath.getString("code"), equalTo("INVALID_REGISTRAR_SCOPE_ID_TYPE"));
    }

    @Test
    public void rsIdTypeValidValueInvalid() {
        String typeToBeFetched = "toBeFetched";
        RsId idForResolvation = new RsId(REGISTRAR, "type", Utils.getRandomItem(RSID_VALUES_INVALID));
        LOGGER.info(idForResolvation.toString());
        // try and get rsId by type, resolved by another rsId
        String responseXml = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(idForResolvation, typeToBeFetched)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertThat(xmlPath.getString("code"), equalTo("INVALID_REGISTRAR_SCOPE_ID_VALUE"));
    }

    @Test
    public void unknowDigitalDocument() {
        String typeToBeFetched = "toBeFetched";
        RsId idForResolvation = new RsId(REGISTRAR, "type", "value");
        LOGGER.info("resolved by: " + idForResolvation.toString() + ", type: " + typeToBeFetched);
        String responseXml = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(idForResolvation, typeToBeFetched)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertThat(xmlPath.getString("code"), equalTo("UNKNOWN_DIGITAL_DOCUMENT"));
    }

    @Test
    public void typeInvalid() {
        RsId idForResolvation = new RsId(REGISTRAR, "forResolvation", "something");
        String typeToBeFetched = Utils.getRandomItem(RSID_TYPES_INVALID);
        LOGGER.info("resolved by: " + idForResolvation.toString() + ", type: " + typeToBeFetched);
        // insert id for resolvation
        insertRegistrarScopeId(urnNbn, idForResolvation, USER);
        // try and get rsId by type, resolved by another rsId
        String responseXml = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(idForResolvation, typeToBeFetched)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertThat(xmlPath.getString("code"), equalTo("INVALID_REGISTRAR_SCOPE_ID_TYPE"));
    }

    @Test
    public void notDefined() {
        RsId idForResolvation = new RsId(REGISTRAR, "type1", "value1");
        RsId idToBeFetched = new RsId(REGISTRAR, "type2", "value2");
        // insert idForResolvation
        insertRegistrarScopeId(urnNbn, idForResolvation, USER);
        // try and get rsId by type, resolved by another rsId
        String responseXml = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(idForResolvation, idToBeFetched.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "NOT_DEFINED");
    }

    @Test
    public void ok() {
        RsId idForResolvation = new RsId(REGISTRAR, "type1", "value1");
        RsId idToBeFetched = new RsId(REGISTRAR, "type2", "value2");
        LOGGER.info(String.format("for resolvation: %s, to be fetched: %s", idForResolvation.type, idToBeFetched.toString()));
        // insert ids
        insertRegistrarScopeId(urnNbn, idForResolvation, USER);
        insertRegistrarScopeId(urnNbn, idToBeFetched, USER);
        // get rsId by type, resolved by another rsId
        String responseXml = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().get(buildUrl(idForResolvation, idToBeFetched.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        // check that only requested id found in response
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idToBeFetched.type + "\' }"), equalTo(idToBeFetched.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idForResolvation.type + "\' }"), isEmptyOrNullString());
    }

}
