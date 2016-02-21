package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
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
        List<RsId> ids = new ArrayList<>();
        // test id types
        ids.add(new RsId(REGISTRAR_CODE, RSID_TYPE_OK_MIN_LENGTH, "value"));
        ids.add(new RsId(REGISTRAR_CODE, RSID_TYPE_OK_MAX_LENGTH, "value"));
        for (int i = 0; i < RSID_TYPE_OK_RESERVED.length; i++) {
            ids.add(new RsId(REGISTRAR_CODE, RSID_TYPE_OK_RESERVED[i], "value"));
        }
        for (int i = 0; i < RSID_TYPE_OK_UNRESERVED.length; i++) {
            ids.add(new RsId(REGISTRAR_CODE, RSID_TYPE_OK_UNRESERVED[i], "value"));
        }
        // test id values
        ids.add(new RsId(REGISTRAR_CODE, "minLength", RSID_VALUE_OK_MIN_LENGTH));
        ids.add(new RsId(REGISTRAR_CODE, "maxLength", RSID_VALUE_OK_MAX_LENGTH));
        for (int i = 0; i < RSID_VALUE_OK_RESERVED.length; i++) {
            ids.add(new RsId(REGISTRAR_CODE, "reserved" + i, RSID_VALUE_OK_RESERVED[i]));
        }
        for (int i = 0; i < RSID_VALUE_OK_UNRESERVED.length; i++) {
            ids.add(new RsId(REGISTRAR_CODE, "unreserved" + i, RSID_VALUE_OK_UNRESERVED[i]));
        }

        // insert ids
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        for (RsId id : ids) {
            insertRegistrarScopeId(URNNBN, id, USER_WITH_RIGHTS);
        }

        // get all ids
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrarScopeIdentifiers/c:id", nsContext))//
                .when().get(buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers")//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.registrarScopeIdentifiers");
        // test ids found in response
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idForResolvation.type + "\' }"), equalTo(idForResolvation.value));
        for (RsId id : ids) {
            assertThat(xmlPath.getString("id.find { it.@type == \'" + id.type + "\' }"), equalTo(id.value));
        }
    }

}
