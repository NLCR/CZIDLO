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

    @Test
    public void getRegistrarScopeIdentifierOk() {
        RsId idForResolvation = new RsId(REGISTRAR_CODE, "forResolvation", "something");
        RsId idOther = new RsId(REGISTRAR_CODE, "other", "something");
        RsId idTest1 = new RsId(REGISTRAR_CODE, "test1", RSID_TYPE_OK_MIN_LENGTH);
        RsId idTest2 = new RsId(REGISTRAR_CODE, "test2", RSID_TYPE_OK_MAX_LENGTH);
        RsId idTest3 = new RsId(REGISTRAR_CODE, "test3", RSID_TYPE_OK_RESERVED_DEPR);
        RsId idTest4 = new RsId(REGISTRAR_CODE, "test4", RSID_TYPE_OK_UNRESERVED_DEPR);
        RsId idTest5 = new RsId(REGISTRAR_CODE, RSID_TYPE_OK_MIN_LENGTH, "something");
        RsId idTest6 = new RsId(REGISTRAR_CODE, RSID_TYPE_OK_MAX_LENGTH, "something");
        RsId idTest7 = new RsId(REGISTRAR_CODE, RSID_TYPE_OK_RESERVED_DEPR, "something");
        RsId idTest8 = new RsId(REGISTRAR_CODE, RSID_TYPE_OK_UNRESERVED_DEPR, "something");

        // insert ids
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idOther, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idTest1, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idTest2, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idTest3, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idTest4, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idTest5, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idTest6, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idTest7, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idTest8, USER_WITH_RIGHTS);

        // test
        getRegistrarScopeIdentifierOk(idForResolvation, idTest1, idOther);
        getRegistrarScopeIdentifierOk(idForResolvation, idTest2, idOther);
        getRegistrarScopeIdentifierOk(idForResolvation, idTest3, idOther);
        getRegistrarScopeIdentifierOk(idForResolvation, idTest4, idOther);
        getRegistrarScopeIdentifierOk(idForResolvation, idTest5, idOther);
        getRegistrarScopeIdentifierOk(idForResolvation, idTest6, idOther);
        getRegistrarScopeIdentifierOk(idForResolvation, idTest7, idOther);
        getRegistrarScopeIdentifierOk(idForResolvation, idTest8, idOther);
    }

    private void getRegistrarScopeIdentifierOk(RsId idForResolvation, RsId idToGet, RsId idInsertedOther) {
        // get
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
    }

    @Test
    public void getRegistrarScopeIdentifierTypeInvalid() {
        RsId idForResolvation = new RsId(REGISTRAR_CODE, "forResolvation", "something");
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);

        // invalid reserved characters
        for (String type : RSID_TYPE_INVALID_RESERVED) {
            getRegistrarScopeIdentifierTypeInvalid(idForResolvation, type);
        }
        // invalid unreserved characters
        for (String type : RSID_TYPE_INVALID_UNRESERVED) {
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

}
