package cz.nkp.urnnbn.api.v4;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static com.jayway.restassured.path.json.JsonPath.from;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.api.Utils;
import cz.nkp.urnnbn.api.pojo.RsId;

/**
 * Tests for GET /api/v4/registrars/${REGISTRAR_CODE}/digitalDocuments/registrarScopeIdentifier/${ID_TYPE}/${ID_VALUE}
 *
 */
public class ResolveByRsId extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(ResolveByRsId.class.getName());

    private RsId ddUnknown;
    private RsId ddExistsDiNone;
    private RsId ddExistsDiDeactivated;
    private RsId ddExistsDiActive;

    private List<String> urnNbns = new ArrayList<>();
    private List<RsId> ids = new ArrayList<>();

    @BeforeClass
    public void beforeClass() {
        init();
        Long digLibId = getDigitalLibraryIdOrNull(REGISTRAR);
        // no dd
        ddUnknown = new RsId(REGISTRAR, "type", "no_such_dd");
        ids.add(ddUnknown);
        // dd without di
        String urnNbnDiNone = registerUrnNbn(REGISTRAR, USER);
        ddExistsDiNone = new RsId(REGISTRAR, "type", "diNone");
        insertRegistrarScopeId(urnNbnDiNone, ddExistsDiNone, USER);
        urnNbns.add(urnNbnDiNone);
        ids.add(ddExistsDiNone);
        // dd with deactivated di
        String urnNbnDiDeactivated = registerUrnNbn(REGISTRAR, USER);
        ddExistsDiDeactivated = new RsId(REGISTRAR, "type", "diDeactivated");
        insertRegistrarScopeId(urnNbnDiDeactivated, ddExistsDiDeactivated, USER);
        long diDeactivated = insertDigitalInstance(urnNbnDiDeactivated, digLibId, WORKING_URL, USER);
        deactivateDigitalInstance(diDeactivated, USER);
        urnNbns.add(urnNbnDiDeactivated);
        ids.add(ddExistsDiDeactivated);
        // dd with active di
        String urnNbnDiActive = registerUrnNbn(REGISTRAR, USER);
        ddExistsDiActive = new RsId(REGISTRAR, "type", "diActive");
        insertRegistrarScopeId(urnNbnDiActive, ddExistsDiActive, USER);
        insertDigitalInstance(urnNbnDiActive, digLibId, WORKING_URL, USER);
        urnNbns.add(urnNbnDiActive);
        ids.add(ddExistsDiActive);
    }

    @AfterClass
    public void afterClass() {
        for (String urnNbn : urnNbns) {
            deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
        }
    }

    private String buildUrl(RsId idForResolvation) {
        return buildResolvationPath(idForResolvation);
    }

    @Test
    public void registrarCodeInvalid() {
        String registrarCode = Utils.getRandomItem(REGISTRAR_CODES_INVALID);
        LOGGER.info("registrar code: " + registrarCode);
        RsId id = new RsId(registrarCode, "type", "value");
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_REGISTRAR_CODE");
    }

    @Test
    public void registrarCodeValidUnknown() {
        String registrarCode = Utils.getRandomItem(REGISTRAR_CODES_VALID);
        LOGGER.info("registrar code: " + registrarCode);
        RsId id = new RsId(registrarCode, "type", "value");
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "UNKNOWN_REGISTRAR");
    }

    @Test
    public void rsIdTypeInvalid() {
        RsId id = new RsId(REGISTRAR, Utils.getRandomItem(RSID_TYPES_INVALID), "value");
        LOGGER.info(id.toString());
        String responseXml = with().config(namespaceAwareXmlConfig()) //
                .expect() //
                .statusCode(400) //
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString)) //
                .body(hasXPath("/c:response/c:error", nsContext)) //
                .when().get(buildUrl(id)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_REGISTRAR_SCOPE_ID_TYPE");
    }

    @Test
    public void rsIdTypeValidValueInvalid() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        LOGGER.info(urnNbn);
        int counter = 0;
        // for (String value : RSID_VALUES_INVALID) {
        String value = Utils.getRandomItem(RSID_VALUES_INVALID);
        RsId idForResolvation = new RsId(REGISTRAR, "type" + ++counter, value);
        LOGGER.info(idForResolvation.toString());
        // same id but with valid value
        RsId idValidValue = new RsId(idForResolvation.registrarCode, idForResolvation.type, "value");
        insertRegistrarScopeId(urnNbn, idValidValue, USER);
        String responseXml = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(idForResolvation)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertThat(xmlPath.getString("code"), equalTo("INVALID_REGISTRAR_SCOPE_ID_VALUE"));
        // cleanup
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
    }

    // format not specified

    @Test
    public void formatNotSpecifiedNoDd() {
        RsId id = ddUnknown;
        LOGGER.info(id.toString());
        String responseXml = with().config(namespaceAwareXmlConfig()) //
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .when().get(buildUrl(id)).andReturn().asString();
        Assert.assertEquals(XmlPath.from(responseXml).get("response.error.code"), "UNKNOWN_DIGITAL_DOCUMENT");
    }

    @Test
    public void formatNotSpecifiedDdExistsDiNone() {
        RsId id = ddExistsDiNone;
        LOGGER.info(id.toString());
        String responseStr = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.HTML)//
                .when().get(buildUrl(id)).andReturn().asString();
        assertThat(responseStr, containsString("<title>CZIDLO</title>"));
    }

    @Test
    public void formatNotSpecifiedDdExistsDiDeactivated() {
        RsId id = ddExistsDiDeactivated;
        LOGGER.info(id.toString());
        String responseStr = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.HTML)//
                .when().get(buildUrl(id)).andReturn().asString();
        assertThat(responseStr, containsString("<title>CZIDLO</title>"));
    }

    @Test
    public void formatNotSpecifiedDdExistsDiActive() {
        RsId id = ddExistsDiActive;
        LOGGER.info(id.toString());
        String responseStr = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.HTML)//
                .when().get(buildUrl(id)).andReturn().asString();
        assertThat(responseStr, not(containsString("<title>CZIDLO</title>")));
    }

    // format=xml

    @Test
    public void formatXmldNoDd() {
        RsId id = ddUnknown;
        LOGGER.info(id.toString());
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("format", "xml")//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .when().get(buildUrl(id)).andReturn().asString();
        Assert.assertEquals(XmlPath.from(responseXml).get("response.error.code"), "UNKNOWN_DIGITAL_DOCUMENT");
    }

    @Test
    public void formatXmlDiNone() {
        RsId id = ddExistsDiNone;
        LOGGER.info(id.toString());
        String responseStr = with().config(namespaceAwareXmlConfig()).queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseStr).setRoot("response.digitalDocument");
        assertEquals(0, xmlPath.getInt("digitalInstances.@count"));
    }

    @Test
    public void formatXmlDdExistsDiDeactivated() {
        RsId id = ddExistsDiDeactivated;
        LOGGER.info(id.toString());
        String responseStr = with().config(namespaceAwareXmlConfig()).queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseStr).setRoot("response.digitalDocument");
        assertEquals(1, xmlPath.getInt("digitalInstances.@count"));
        assertEquals(false, Utils.booleanValue(xmlPath.getString("digitalInstances.digitalInstance[0].@active")));
    }

    @Test
    public void formatXmlDdExistsDiActive() {
        RsId id = ddExistsDiActive;
        LOGGER.info(id.toString());
        String responseStr = with().config(namespaceAwareXmlConfig()).queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseStr).setRoot("response.digitalDocument");
        assertEquals(1, xmlPath.getInt("digitalInstances.@count"));
        assertEquals(true, Utils.booleanValue(xmlPath.getString("digitalInstances.digitalInstance[0].@active")));
    }

    // format=json

    @Test
    public void formatJsondNoDd() {
        RsId id = ddUnknown;
        LOGGER.info(id.toString());
        String response = with().config(namespaceAwareXmlConfig()).queryParam("format", "json")//
                .expect()//
                .statusCode(404).contentType(ContentType.JSON)//
                .when().get(buildUrl(id)).andReturn().asString();
        assertEquals("UNKNOWN_DIGITAL_DOCUMENT", from(response).getString("error.code"));
    }

    @Test
    public void formatJsonDiNone() {
        RsId id = ddExistsDiNone;
        LOGGER.info(id.toString());
        String responseStr = with().config(namespaceAwareXmlConfig()).queryParam("format", "json")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.JSON)//
                // .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        // XmlPath xmlPath = XmlPath.from(responseStr).setRoot("response.digitalDocument");
        // assertEquals(0, xmlPath.getInt("digitalInstances.@count"));
        // TODO: check data
    }

    @Test
    public void formatJsonDdExistsDiDeactivated() {
        RsId id = ddExistsDiDeactivated;
        LOGGER.info(id.toString());
        String responseStr = with().config(namespaceAwareXmlConfig()).queryParam("format", "json")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.JSON)//
                // .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        // XmlPath xmlPath = XmlPath.from(responseStr).setRoot("response.digitalDocument");
        // assertEquals(1, xmlPath.getInt("digitalInstances.@count"));
        // assertEquals(false, Utils.booleanValue(xmlPath.getString("digitalInstances.digitalInstance[0].@active")));
        // TODO: check data
    }

    @Test
    public void formatJsonDdExistsDiActive() {
        RsId id = ddExistsDiDeactivated;
        LOGGER.info(id.toString());
        String responseStr = with().config(namespaceAwareXmlConfig()).queryParam("format", "json")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.JSON)//
                // .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        // XmlPath xmlPath = XmlPath.from(responseStr).setRoot("response.digitalDocument");
        // assertEquals(1, xmlPath.getInt("digitalInstances.@count"));
        // assertEquals(true, Utils.booleanValue(xmlPath.getString("digitalInstances.digitalInstance[0].@active")));
        // TODO: check data
    }

    // format invalid and empty

    @Test
    public void formatInvalid() {
        for (RsId id : ids) {
            LOGGER.info(id.toString());
            String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("format", "pdf")//
                    .expect()//
                    .statusCode(400)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .when().get(buildUrl(id)).andReturn().asString();
            Assert.assertEquals(XmlPath.from(responseXml).get("response.error.code"), "ILLEGAL_FORMAT");
        }
    }

    @Test
    public void formatEmpty() {
        for (RsId id : ids) {
            LOGGER.info(id.toString());
            String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("format", "")//
                    .expect()//
                    .statusCode(400)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .when().get(buildUrl(id)).andReturn().asString();
            Assert.assertEquals(XmlPath.from(responseXml).get("response.error.code"), "ILLEGAL_FORMAT");
        }
    }

}
