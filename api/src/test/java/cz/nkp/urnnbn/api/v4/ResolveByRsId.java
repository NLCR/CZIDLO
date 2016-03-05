package cz.nkp.urnnbn.api.v4;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private RsId urnNbnActiveDiNone;
    private RsId urnNbnActiveDiActive;
    private RsId urnNbnActiveDiDeactivated;
    private RsId urnNbnDeactivatedDiNone;
    private RsId urnNbnDeactivatedDiActive;
    private RsId urnNbnDeactivatedDiDeactivated;
    private List<RsId> rsIds;
    private List<String> urnNbns;

    @BeforeClass
    public void beforeClass() {
        init();
        Long digLibId = getDigitalLibraryIdOrNull(REGISTRAR);
        Long diId = null;
        urnNbns = new ArrayList<>();
        String urnNbn;

        // urn active, no di
        urnNbn = registerUrnNbn(REGISTRAR, USER);
        urnNbnActiveDiNone = insertId(urnNbn, "urnActiveDiNone");
        urnNbns.add(urnNbn);
        // urn active di active
        urnNbn = registerUrnNbn(REGISTRAR, USER);
        urnNbnActiveDiActive = insertId(urnNbn, "urnNbnActiveDiActive");
        diId = insertDigitalInstance(urnNbn, digLibId, WORKING_URL, USER);
        urnNbns.add(urnNbn);
        // urn active, di deactivated
        urnNbn = registerUrnNbn(REGISTRAR, USER);
        diId = insertDigitalInstance(urnNbn, digLibId, WORKING_URL, USER);
        deactivateDigitalInstance(diId, USER);
        urnNbnActiveDiDeactivated = insertId(urnNbn, "urnNbnActiveDiDeactivated");
        urnNbns.add(urnNbn);
        // urn deactivated no di
        urnNbn = registerUrnNbn(REGISTRAR, USER);
        deactivateUrnNbn(urnNbn, USER);
        urnNbnDeactivatedDiNone = insertId(urnNbn, "urnNbnDeactivatedDiNone");
        urnNbns.add(urnNbn);
        // urn deactivated, di active
        urnNbn = registerUrnNbn(REGISTRAR, USER);
        insertDigitalInstance(urnNbn, digLibId, WORKING_URL, USER);
        deactivateUrnNbn(urnNbn, USER);
        urnNbnDeactivatedDiActive = insertId(urnNbn, "urnNbnDeactivatedDiActive");
        urnNbns.add(urnNbn);
        // urn deactivated, di deactivated
        urnNbn = registerUrnNbn(REGISTRAR, USER);
        diId = insertDigitalInstance(urnNbn, digLibId, WORKING_URL, USER);
        deactivateDigitalInstance(diId, USER);
        deactivateUrnNbn(urnNbn, USER);
        urnNbnDeactivatedDiDeactivated = insertId(urnNbn, "urnNbnDeactivatedDiDeactivated");
        urnNbns.add(urnNbn);
        // list
        rsIds = new ArrayList<>();
        rsIds.add(urnNbnActiveDiNone);
        rsIds.add(urnNbnActiveDiActive);
        rsIds.add(urnNbnActiveDiDeactivated);
        rsIds.add(urnNbnDeactivatedDiNone);
        rsIds.add(urnNbnDeactivatedDiActive);
        rsIds.add(urnNbnDeactivatedDiDeactivated);
    }

    private RsId insertId(String urnNbn, String idValue) {
        RsId id = new RsId(REGISTRAR, ResolveByRsId.class.getSimpleName(), idValue);
        insertRegistrarScopeId(urnNbn, id, USER);
        return id;
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
    public void rsIdTypeInvalidAll() {
        for (String type : RSID_TYPES_INVALID) {
            RsId id = new RsId(REGISTRAR, type, "value");
            LOGGER.info(id.toString());
            String responseXml = with().config(namespaceAwareXmlConfig()) //
                    .expect() //
                    .statusCode(400) //
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString)) //
                    .body(hasXPath("/c:response/c:error", nsContext)) //
                    .when().get(buildUrl(id)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
            // TODO:APIv4: rename error to INVALID_REGISTRAR_SCOPE_ID_TYPE
            Assert.assertEquals(xmlPath.get("code"), "INVALID_DIGITAL_DOCUMENT_ID_TYPE");
        }
    }

    @Test
    public void rsIdTypeValidValueInvalidAll() {
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        LOGGER.info(urnNbn);
        int counter = 0;
        for (String value : RSID_VALUES_INVALID) {
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
            // TODO:APIv4: rename to INVALID_REGISTRAR_SCOPE_ID_VALUE
            assertThat(xmlPath.getString("code"), equalTo("INVALID_DIGITAL_DOCUMENT_ID_VALUE"));
        }
        // cleanup
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
    }

    @Test
    public void rsIdTypeValidValueValidAll() {
        for (String type : RSID_TYPES_VALID) {
            rsIdTypeValidValueValid(new RsId(REGISTRAR, type, "value"));
        }
        int counter = 0;
        for (String value : RSID_VALUES_VALID) {
            rsIdTypeValidValueValid(new RsId(REGISTRAR, "typeValid" + ++counter, value));
        }
    }

    private void rsIdTypeValidValueValid(RsId id) {
        LOGGER.info(id.toString());
        String urnNbn = registerUrnNbn(REGISTRAR, USER);
        // insert id
        insertRegistrarScopeId(urnNbn, id, USER);
        // get dd by id
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        Assert.assertEquals(xmlPath.get("registrarScopeIdentifiers.id.find { it.@type == \'" + id.type + "\' }"), id.value);
        // delete id
        deleteRegistrarScopeId(urnNbn, id, USER);
    }

    @Test
    public void rsIdTypeValidValueValidCaseSensitive() {
        Map<String, RsId> examples = new HashMap<>();
        // create urns and rsIds
        examples.put(registerUrnNbn(REGISTRAR, USER), new RsId(REGISTRAR, "typeTESTS", "valueTEST"));
        examples.put(registerUrnNbn(REGISTRAR, USER), new RsId(REGISTRAR, "TYPETESTS", "valueTEST"));
        examples.put(registerUrnNbn(REGISTRAR, USER), new RsId(REGISTRAR, "typetest", "valueTEST"));
        examples.put(registerUrnNbn(REGISTRAR, USER), new RsId(REGISTRAR, "typeTESTS", "valuetest"));
        examples.put(registerUrnNbn(REGISTRAR, USER), new RsId(REGISTRAR, "typeTESTS", "VALUETEST"));
        // insert rsIds
        for (String urn : examples.keySet()) {
            insertRegistrarScopeId(urn, examples.get(urn), USER);
        }
        // check
        for (String urn : examples.keySet()) {
            RsId rsId = examples.get(urn);
            String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:digitalDocument", nsContext))// checking for correct type of response and namespace
                    .when().get(buildUrl(rsId)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
            String urnFetched = xmlPath.getString("urnNbn.value");
            assertEquals(urn, urnFetched);
        }
        // cleanup
        for (String urn : examples.keySet()) {
            deleteAllRegistrarScopeIdentifiers(urn, USER);
        }
    }

    @Test
    public void unknownDigitalDocument() {
        RsId id = new RsId(REGISTRAR, "unknownType", "unknownValue");
        String responseXml = with().config(namespaceAwareXmlConfig()) //
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "UNKNOWN_DIGITAL_DOCUMENT");
    }

    // action=show

    @Test
    public void wrongParamActionEmpty() {
        RsId id = urnNbnActiveDiActive;
        LOGGER.info(id.toString());
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
    }

    @Test
    public void wrongParamActionInvalidValue() {
        RsId id = urnNbnActiveDiActive;
        LOGGER.info(id.toString());
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "nonsense")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
    }

    @Test
    public void wrongParamFormatEmpty() {
        RsId id = urnNbnActiveDiActive;
        LOGGER.info(id.toString());
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("format", "")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
    }

    @Test
    public void wrongParamFormatInvalidValue() {
        RsId id = urnNbnActiveDiActive;
        LOGGER.info(id.toString());
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("format", "nonsense")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
    }

    @Test
    public void wrongParamDigitalInstancesEmpty() {
        RsId id = urnNbnActiveDiActive;
        LOGGER.info(id.toString());
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("digitalInstances", "")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
    }

    @Test
    public void wrongParamDigitalInstancesInvalidValue() {
        RsId id = urnNbnActiveDiActive;
        LOGGER.info(id.toString());
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("digitalInstances", "notBoolean")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
    }

    /**
     * Allways redirect to czidlo web, as if format=html.
     */
    @Test
    public void actionShowFormatNotSpecified() {
        int i = 0;
        for (RsId id : rsIds) {
            LOGGER.info(id.toString());
            String responseStr = with().config(namespaceAwareXmlConfig()).queryParam("action", "show")//
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.HTML)//
                    .when().get(buildUrl(id)).andReturn().asString();
            assertThat(responseStr, containsString("<title>CZIDLO</title>"));
        }
    }

    /**
     * allways redirect to czidlo web
     */
    @Test
    public void actionShowFormatHtml() {
        int i = 0;
        for (RsId id : rsIds) {
            LOGGER.info(id.toString());
            String responseStr = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "html")//
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.HTML)//
                    .when().get(buildUrl(id)).andReturn().asString();
            assertThat(responseStr, containsString("<title>CZIDLO</title>"));
        }
    }

    /**
     * always return xml record
     */
    @Test
    public void actionShowFormatXml() {
        int i = 0;

        // urn active id active
        RsId id = urnNbnActiveDiActive;
        LOGGER.info(id.toString());
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        assertEquals(1, xmlPath.getInt("digitalInstances.@count"));
        assertEquals(true, Utils.booleanValue(xmlPath.getString("digitalInstances.digitalInstance[0].@active")));

        // urn active id deactivated
        id = urnNbnActiveDiDeactivated;
        LOGGER.info(id.toString());
        responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        assertEquals(1, xmlPath.getInt("digitalInstances.@count"));
        assertEquals(false, Utils.booleanValue(xmlPath.getString("digitalInstances.digitalInstance[0].@active")));

        // urn active id none
        id = urnNbnActiveDiNone;
        LOGGER.info(id.toString());
        responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        assertEquals(0, xmlPath.getInt("digitalInstances.@count"));

        // urn deactivated di active
        id = urnNbnDeactivatedDiActive;
        LOGGER.info(id.toString());
        responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        assertEquals(1, xmlPath.getInt("digitalInstances.@count"));
        assertEquals(true, Utils.booleanValue(xmlPath.getString("digitalInstances.digitalInstance[0].@active")));

        // urn deactivated di deactivated
        id = urnNbnDeactivatedDiDeactivated;
        LOGGER.info(id.toString());
        responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        assertEquals(1, xmlPath.getInt("digitalInstances.@count"));
        assertEquals(false, Utils.booleanValue(xmlPath.getString("digitalInstances.digitalInstance[0].@active")));

        // urn deactivated di none
        id = urnNbnDeactivatedDiNone;
        LOGGER.info(id.toString());
        responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        assertEquals(0, xmlPath.getInt("digitalInstances.@count"));
    }

    // action decide

    /**
     * Redirect to di url if urn:nbn is active and has active di. Otherwise redirect to czidlo web, as if format=html.
     */
    @Test
    public void actionDecideFormatUnspecified() {
        for (RsId id : rsIds) {
            LOGGER.info(id.toString());
            String responseStr = with().config(namespaceAwareXmlConfig()).queryParam("action", "decide")//
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.HTML)//
                    .when().get(buildUrl(id)).andReturn().asString();
            if (urnNbnActiveDiActive.equals(id)) {
                assertThat(responseStr, not(containsString("<title>CZIDLO</title>")));
            } else {
                assertThat(responseStr, containsString("<title>CZIDLO</title>"));
            }
        }
    }

    /**
     * Redirect to di url if urn:nbn is active and has active di. Otherwise redirect to czidlo web.
     */
    @Test
    public void actionDecideFormatHtml() {
        for (RsId id : rsIds) {
            LOGGER.info(id.toString());
            String responseStr = with().config(namespaceAwareXmlConfig()).queryParam("action", "decide").queryParam("format", "html")//
                    .expect()//
                    .statusCode(200)//
                    .contentType(ContentType.HTML)//
                    .when().get(buildUrl(id)).andReturn().asString();
            if (urnNbnActiveDiActive.equals(id)) {
                assertThat(responseStr, not(containsString("<title>CZIDLO</title>")));
            } else {
                assertThat(responseStr, containsString("<title>CZIDLO</title>"));
            }
        }
    }

    /**
     * Redirect to di url if urn:nbn is active and has active di. Otherwise show xml record.
     */
    @Test
    public void actionDecideFormatXml() {
        int i = 0;

        // urn active id active
        RsId id = urnNbnActiveDiActive;
        LOGGER.info(id.toString());
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "decide").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.HTML)//
                .body(not(containsString("<title>CZIDLO</title>")))//
                .when().get(buildUrl(id)).andReturn().asString();

        // urn active id deactivated
        id = urnNbnActiveDiDeactivated;
        LOGGER.info(id.toString());
        responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "decide").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        assertEquals(1, xmlPath.getInt("digitalInstances.@count"));
        assertEquals(false, Utils.booleanValue(xmlPath.getString("digitalInstances.digitalInstance[0].@active")));

        // urn active id none
        id = urnNbnActiveDiNone;
        LOGGER.info(id.toString());
        responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "decide").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        assertEquals(0, xmlPath.getInt("digitalInstances.@count"));

        // urn deactivated di active
        id = urnNbnDeactivatedDiActive;
        LOGGER.info(id.toString());
        responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "decide").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        assertEquals(1, xmlPath.getInt("digitalInstances.@count"));
        assertEquals(true, Utils.booleanValue(xmlPath.getString("digitalInstances.digitalInstance[0].@active")));

        // urn deactivated di deactivated
        id = urnNbnDeactivatedDiDeactivated;
        LOGGER.info(id.toString());
        responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "decide").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        assertEquals(1, xmlPath.getInt("digitalInstances.@count"));
        assertEquals(false, Utils.booleanValue(xmlPath.getString("digitalInstances.digitalInstance[0].@active")));

        // urn deactivated di none
        id = urnNbnDeactivatedDiNone;
        LOGGER.info(id.toString());
        responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "decide").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        assertEquals(0, xmlPath.getInt("digitalInstances.@count"));
    }

}
