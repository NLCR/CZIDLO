package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertThat;

import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

/**
 * Tests for GET
 * /api/v3/registrars/${REGISTRAR_CODE}/digitalDocuments/registrarScopeIdentifier/${ID_TYPE}/${ID_VALUE}registrarScopeIdentifiers/${ID_TYPE_2}
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

    @Test
    public void getRegistrarScopeIdentifier() {
        RsId idForResolvation = new RsId(REGISTRAR_CODE, "getTest1", "something");
        RsId idToGet = new RsId(REGISTRAR_CODE, "getTest2", "something2");
        RsId idOther = new RsId(REGISTRAR_CODE, "getTest3", "something3");
        // delete all idForResolvation, id2
        deleteAllRegistrarScopeIdentifiers(URNNBN, USER_WITH_RIGHTS);
        // insert ids
        insertRegistrarScopeId(URNNBN, idForResolvation.type, idForResolvation.value, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idToGet.type, idToGet.value, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idOther.type, idOther.value, USER_WITH_RIGHTS);
        // get
        String xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().get(buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers/" + idToGet.type)//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response");
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idToGet.type + "\' }"), equalTo(idToGet.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idForResolvation.type + "\' }"), isEmptyOrNullString());
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idOther.type + "\' }"), isEmptyOrNullString());
        // clean up
        deleteAllRegistrarScopeIdentifiers(URNNBN, USER_WITH_RIGHTS);
    }

    @Test
    public void getRegistrarScopeIdentifierNotDefined() {
        RsId idForResolvation = new RsId(REGISTRAR_CODE, "getTest1", "something");
        RsId idToGet = new RsId(REGISTRAR_CODE, "getTest2", "something2");
        // delete all idForResolvation, id2
        deleteAllRegistrarScopeIdentifiers(URNNBN, USER_WITH_RIGHTS);
        // insert idForResolvation
        insertRegistrarScopeId(URNNBN, idForResolvation.type, idForResolvation.value, USER_WITH_RIGHTS);
        // try and get
        String xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers/" + idToGet.type)//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "NOT_DEFINED");
        // clean up
        deleteAllRegistrarScopeIdentifiers(URNNBN, USER_WITH_RIGHTS);
    }

    // TODO: check characters in idToGet

}
