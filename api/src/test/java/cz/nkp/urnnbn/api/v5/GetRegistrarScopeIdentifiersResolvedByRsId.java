package cz.nkp.urnnbn.api.v5;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.path.xml.XmlPath;
import cz.nkp.urnnbn.api.Utils;
import cz.nkp.urnnbn.api.pojo.RsId;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.logging.Logger;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Tests for GET /api/v5/registrars/${REGISTRAR_CODE}/digitalDocuments/registrarScopeIdentifier/${ID_TYPE}/${ID_VALUE}/registrarScopeIdentifiers
 *
 */
public class GetRegistrarScopeIdentifiersResolvedByRsId extends ApiV5Tests {

    private static final Logger LOGGER = Logger.getLogger(GetRegistrarScopeIdentifiersResolvedByRsId.class.getName());

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
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(idForResolvation)).andReturn().asString();
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
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(idForResolvation)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "UNKNOWN_REGISTRAR");
    }

    @Test
    public void rsIdTypeInvalid() {
        RsId idForResolvation = new RsId(REGISTRAR, Utils.getRandomItem(RSID_TYPES_INVALID), "value");
        LOGGER.info(idForResolvation.toString());
        String responseXml = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(idForResolvation)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertThat(xmlPath.getString("code"), equalTo("INVALID_REGISTRAR_SCOPE_ID_TYPE"));
    }

    @Test
    public void rsIdTypeValidValueInvalid() {
        RsId idForResolvation = new RsId(REGISTRAR, "type", Utils.getRandomItem(RSID_VALUES_INVALID));
        LOGGER.info(idForResolvation.toString());
        String responseXml = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(idForResolvation)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertThat(xmlPath.getString("code"), equalTo("INVALID_REGISTRAR_SCOPE_ID_VALUE"));
    }

    @Test
    public void unknowDigitalDocument() {
        RsId idForResolvation = new RsId(REGISTRAR, "type", "value");
        LOGGER.info(idForResolvation.toString());
        // get all ids
        String responseXml = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(idForResolvation)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertThat(xmlPath.getString("code"), equalTo("UNKNOWN_DIGITAL_DOCUMENT"));
    }

    @Test
    public void ok() {
        RsId idForResolvation = new RsId(REGISTRAR, "type1", "value1");
        LOGGER.info(idForResolvation.toString());
        RsId other = new RsId(REGISTRAR, "type2", "value2");
        // insert ids
        insertRegistrarScopeId(urnNbn, idForResolvation, USER);
        insertRegistrarScopeId(urnNbn, other, USER);
        // get all ids
        String responseXml = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrarScopeIdentifiers", nsContext))//
                .when().get(buildUrl(idForResolvation)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.registrarScopeIdentifiers");
        // check that ids found in response
        assertEquals(2, xmlPath.getInt("id.size()"));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + idForResolvation.type + "\' }"), equalTo(idForResolvation.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + other.type + "\' }"), equalTo(other.value));
    }

    @Test
    public void formatXml() {
        RsId idForResolvation = new RsId(REGISTRAR, "type1", "value1");
        LOGGER.info(idForResolvation.toString());
        insertRegistrarScopeId(urnNbn, idForResolvation, USER);
        with().config(namespaceAwareXmlConfig()).queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrarScopeIdentifiers", nsContext))//
                .when().get(buildUrl(idForResolvation));
    }

    @Test
    public void formatNotSpecified() {
        RsId idForResolvation = new RsId(REGISTRAR, "type1", "value1");
        LOGGER.info(idForResolvation.toString());
        insertRegistrarScopeId(urnNbn, idForResolvation, USER);
        with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrarScopeIdentifiers", nsContext))//
                .when().get(buildUrl(idForResolvation));
    }

    @Test
    public void formatEmpty() {
        RsId idForResolvation = new RsId(REGISTRAR, "type1", "value1");
        LOGGER.info(idForResolvation.toString());
        insertRegistrarScopeId(urnNbn, idForResolvation, USER);
        with().config(namespaceAwareXmlConfig()).queryParam("format", "")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.HTML)//
                .when().get(buildUrl(idForResolvation));
    }

    @Test
    public void formatInvalid() {
        RsId idForResolvation = new RsId(REGISTRAR, "type1", "value1");
        LOGGER.info(idForResolvation.toString());
        insertRegistrarScopeId(urnNbn, idForResolvation, USER);
        with().config(namespaceAwareXmlConfig()).queryParam("format", "pdf")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.HTML)//
                .when().get(buildUrl(idForResolvation));
    }

    @Test
    public void formatJson() {
        RsId idForResolvation = new RsId(REGISTRAR, "type1", "value1");
        LOGGER.info(idForResolvation.toString());
        RsId other = new RsId(REGISTRAR, "type2", "value2");
        // insert ids
        insertRegistrarScopeId(urnNbn, idForResolvation, USER);
        insertRegistrarScopeId(urnNbn, other, USER);
        // get all ids
        String responseJson = with().config(namespaceAwareXmlConfig()).queryParam("format", "json")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.JSON)//
                .when().get(buildUrl(idForResolvation)).andReturn().asString();
        JsonPath path = from(responseJson);
        int size = path.getInt("size()");
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                String type = path.getString(String.format("registrarScopeIdentifiers[%d].type", i));
                String value = path.getString(String.format("registrarScopeIdentifiers[%d].value", i));
                if (idForResolvation.type.equals(type)) {
                    assertEquals(idForResolvation.value, value);
                } else if (other.type.equals(type)) {
                    assertEquals(other.value, value);
                }
            }
        } else {
            LOGGER.warning("no registrar-scope identifiers found");
        }
    }

}
