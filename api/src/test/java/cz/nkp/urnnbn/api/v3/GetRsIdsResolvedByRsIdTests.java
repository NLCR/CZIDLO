package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertThat;

import java.util.logging.Logger;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

/**
 * Tests for GET /api/v3/registrars/${REGISTRAR_CODE}/digitalDocuments/registrarScopeIdentifier/${ID_TYPE}/${ID_VALUE}/registrarScopeIdentifiers
 *
 */
public class GetRsIdsResolvedByRsIdTests extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(GetRsIdsResolvedByRsIdTests.class.getName());

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
    public void getRegistrarScopeIdentifiers() {
        RsId idForResolvation = new RsId(REGISTRAR_CODE, "getTest1", "something");
        RsId idTest1 = new RsId(REGISTRAR_CODE, "test1", RSID_TYPE_MIN_LENGTH);
        RsId idTest2 = new RsId(REGISTRAR_CODE, "test2", RSID_TYPE_MAX_LENGTH);
        RsId idTest3 = new RsId(REGISTRAR_CODE, "test3", RSID_TYPE_RESERVED_CHARS);
        RsId idTest4 = new RsId(REGISTRAR_CODE, "test4", RSID_TYPE_UNRESERVED_CHARS);
        RsId idTest5 = new RsId(REGISTRAR_CODE, RSID_TYPE_MIN_LENGTH, "something");
        RsId idTest6 = new RsId(REGISTRAR_CODE, RSID_TYPE_MAX_LENGTH, "something");
        RsId idTest7 = new RsId(REGISTRAR_CODE, RSID_TYPE_RESERVED_CHARS, "something");
        RsId idTest8 = new RsId(REGISTRAR_CODE, RSID_TYPE_UNRESERVED_CHARS, "something");

        // insert ids
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idTest1, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idTest2, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idTest3, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idTest4, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idTest5, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idTest6, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idTest7, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idTest8, USER_WITH_RIGHTS);
        // get all ids
        String xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrarScopeIdentifiers/c:id", nsContext))//
                .when().get(buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers")//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.registrarScopeIdentifiers");
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idForResolvation.type + "\' }"), equalTo(idForResolvation.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idTest1.type + "\' }"), equalTo(idTest1.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idTest2.type + "\' }"), equalTo(idTest2.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idTest3.type + "\' }"), equalTo(idTest3.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idTest4.type + "\' }"), equalTo(idTest4.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idTest5.type + "\' }"), equalTo(idTest5.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idTest6.type + "\' }"), equalTo(idTest6.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idTest7.type + "\' }"), equalTo(idTest7.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idTest8.type + "\' }"), equalTo(idTest8.value));
    }

}
