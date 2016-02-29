package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.api.Utils;

/**
 * Tests for GET /api/v3/registrars/${REGISTRAR_CODE}/digitalDocuments/registrarScopeIdentifier/${ID_TYPE}/${ID_VALUE}
 *
 */
public class ResolveByRsId extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(ResolveByRsId.class.getName());

    private String urnNbnActiveDiNone;
    private String urnNbnActiveDiActive;
    private String urnNbnActiveDiDeactivated;
    private String urnNbnDeactivatedDiNone; // TODO:APIv4: should throw error when trying to insert rsid
    private String urnNbnDeactivatedDiActive;
    private String urnNbnDeactivatedDiDeactivated;

    @BeforeClass
    public void beforeClass() {
        init();
        Long digLibId = getDigitalLibraryIdOrNull(REGISTRAR);
        Long diId = null;
        // urn active, no di
        urnNbnActiveDiNone = registerUrnNbn(REGISTRAR, USER);
        // urn active di active
        urnNbnActiveDiActive = registerUrnNbn(REGISTRAR, USER);
        insertDigitalInstance(urnNbnActiveDiActive, digLibId, WORKING_URL, USER);
        // urn active, di deactivated
        urnNbnActiveDiDeactivated = registerUrnNbn(REGISTRAR, USER);
        diId = insertDigitalInstance(urnNbnActiveDiDeactivated, digLibId, WORKING_URL, USER);
        deactivateDigitalInstance(diId, USER);
        // urn deactivated no di
        urnNbnDeactivatedDiNone = registerUrnNbn(REGISTRAR, USER);
        deactivateUrnNbn(urnNbnDeactivatedDiNone, USER);
        // urn deactivated, di active
        urnNbnDeactivatedDiActive = registerUrnNbn(REGISTRAR, USER);
        insertDigitalInstance(urnNbnDeactivatedDiActive, digLibId, WORKING_URL, USER);
        deactivateUrnNbn(urnNbnDeactivatedDiActive, USER);
        // urn deactivated, di deactivated
        urnNbnDeactivatedDiDeactivated = registerUrnNbn(REGISTRAR, USER);
        diId = insertDigitalInstance(urnNbnDeactivatedDiDeactivated, digLibId, WORKING_URL, USER);
        deactivateDigitalInstance(diId, USER);
        deactivateUrnNbn(urnNbnDeactivatedDiDeactivated, USER);
    }

    private String buildUrl(RsId idForResolvation) {
        return buildResolvationPath(idForResolvation);
    }

    @AfterMethod
    public void afterMethod() {
        deleteAllRegistrarScopeIdentifiers(urnNbnActiveDiNone, USER);
        deleteAllRegistrarScopeIdentifiers(urnNbnActiveDiActive, USER);
        deleteAllRegistrarScopeIdentifiers(urnNbnActiveDiDeactivated, USER);
        deleteAllRegistrarScopeIdentifiers(urnNbnDeactivatedDiNone, USER);
        deleteAllRegistrarScopeIdentifiers(urnNbnDeactivatedDiActive, USER);
        deleteAllRegistrarScopeIdentifiers(urnNbnDeactivatedDiDeactivated, USER);
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
            // idForResolvation wasn't inserted, but INVALID_DIGITAL_DOCUMENT_ID_TYPE should be returned before this becomes relevant
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
        int counter = 0;
        for (String value : RSID_VALUES_INVALID) {
            RsId idForResolvation = new RsId(REGISTRAR, "reserved" + ++counter, value);
            LOGGER.info(idForResolvation.toString());
            // same id but with valid value
            RsId idValidValue = new RsId(idForResolvation.registrarCode, idForResolvation.type, "value");
            insertRegistrarScopeId(urnNbnActiveDiActive, idValidValue, USER);
            String responseXml = with().config(namespaceAwareXmlConfig())//
                    .expect()//
                    .statusCode(404)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:error", nsContext))//
                    .when().get(buildUrl(idForResolvation)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
            // TODO:APIv4: https://github.com/NLCR/CZIDLO/issues/132 (INVALID_REGISTRAR_SCOPE_ID_VALUE, code 400)
            assertThat(xmlPath.getString("code"), equalTo("UNKNOWN_DIGITAL_DOCUMENT"));
            // cleanup
            deleteRegistrarScopeId(urnNbnActiveDiActive, idValidValue, USER);
        }
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
        // insert id
        insertRegistrarScopeId(urnNbnActiveDiActive, id, USER);
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
        deleteRegistrarScopeId(urnNbnActiveDiActive, id, USER);
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

    @Test
    public void actionInvalid() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbnActiveDiActive, id, USER);
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
    public void actionEmpty() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbnActiveDiActive, id, USER);
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
    }

    // action=show

    @Test
    public void actionShow() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbnActiveDiActive, id, USER);
        with().config(namespaceAwareXmlConfig()).queryParam("action", "show")//
                .expect().statusCode(200).contentType(ContentType.HTML)//
                .body(containsString("<title>CZIDLO</title>"))// should redirect to web search
                .when().get(buildUrl(id)).andReturn().asString();
    }

    @Test
    public void actionShowFormatEmpty() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbnActiveDiActive, id, USER);
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
    }

    @Test
    public void actionShowFormatInvalid() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbnActiveDiActive, id, USER);
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "nonsense")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
    }

    @Test
    public void actionShowFormatHtml() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbnActiveDiActive, id, USER);
        with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "html")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.HTML)//
                .body(containsString("<title>CZIDLO</title>"))// should redirect to web search
                .when().get(buildUrl(id)).andReturn().asString();
    }

    @Test
    public void actionShowFormatXml() {
        // TODO: rozlisit na pripady ruznych stavu DD, URN, DI
        // active no id
        RsId id = new RsId(REGISTRAR, "urn_act_di_none", "value");
        insertRegistrarScopeId(urnNbnActiveDiNone, id, USER);
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        assertEquals(id.value, xmlPath.getString("registrarScopeIdentifiers.id.find { it.@type == '" + id.type + "' }"));
        assertEquals(0, xmlPath.getInt("digitalInstances.@count"));

        // active id active
        id = new RsId(REGISTRAR, "urn_act_di_act", "value");
        insertRegistrarScopeId(urnNbnActiveDiActive, id, USER);
        responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        assertEquals(id.value, xmlPath.getString("registrarScopeIdentifiers.id.find { it.@type == '" + id.type + "' }"));
        assertEquals(1, xmlPath.getInt("digitalInstances.@count"));
        assertTrue(xmlPath.getBoolean("digitalInstances.digitalInstance[0].@active"));

        // active id deactivated
        id = new RsId(REGISTRAR, "urn_act_di_deact", "value");
        insertRegistrarScopeId(urnNbnActiveDiDeactivated, id, USER);
        responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        assertEquals(id.value, xmlPath.getString("registrarScopeIdentifiers.id.find { it.@type == '" + id.type + "' }"));
        assertEquals(1, xmlPath.getInt("digitalInstances.@count"));
        assertFalse(xmlPath.getBoolean("digitalInstances.digitalInstance[0].@active"));

        // deactivated without di
        id = new RsId(REGISTRAR, "urn_deact_di_none", "value");
        insertRegistrarScopeId(urnNbnDeactivatedDiNone, id, USER);
        responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        assertEquals(id.value, xmlPath.getString("registrarScopeIdentifiers.id.find { it.@type == '" + id.type + "' }"));
        assertEquals(0, xmlPath.getInt("digitalInstances.@count"));

        // deactivated with di active
        id = new RsId(REGISTRAR, "urn_deact_di_act", "value");
        insertRegistrarScopeId(urnNbnDeactivatedDiActive, id, USER);
        responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        assertEquals(id.value, xmlPath.getString("registrarScopeIdentifiers.id.find { it.@type == '" + id.type + "' }"));
        assertEquals(1, xmlPath.getInt("digitalInstances.@count"));
        assertTrue(xmlPath.getBoolean("digitalInstances.digitalInstance[0].@active"));

        // deactivated with di deactivated
        id = new RsId(REGISTRAR, "urn_deact_di_deact", "value");
        insertRegistrarScopeId(urnNbnDeactivatedDiDeactivated, id, USER);
        responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        assertEquals(id.value, xmlPath.getString("registrarScopeIdentifiers.id.find { it.@type == '" + id.type + "' }"));
        assertEquals(1, xmlPath.getInt("digitalInstances.@count"));
        assertFalse(xmlPath.getBoolean("digitalInstances.digitalInstance[0].@active"));
    }

    @Test
    public void actionShowFormatXmlDigitalInstancesEmpty() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbnActiveDiActive, id, USER);
        String responseXml = with().config(namespaceAwareXmlConfig())//
                .queryParam("action", "show").queryParam("format", "xml").queryParam("digitalInstances", "")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
    }

    @Test
    public void actionShowFormatXmlDigitalInstancesInvalid() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbnActiveDiActive, id, USER);
        String responseXml = with().config(namespaceAwareXmlConfig())//
                .queryParam("action", "show").queryParam("format", "xml").queryParam("digitalInstances", "nope")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
    }

    @Test
    public void actionShowFormatXmlDigitalInstancesTrue() {
        // TODO: situace DD existuje, neexistuje, deaktivovane
        RsId idNoDi = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbnActiveDiActive, idNoDi, USER);
        with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml").queryParam("digitalInstances", "true")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument/c:digitalInstances", nsContext))// checking for correct type of response and namespace
                .when().get(buildUrl(idNoDi)).andReturn().asString();
    }

    @Test
    public void actionShowFormatXmlDigitalInstancesFalse() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbnActiveDiActive, id, USER);
        with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml").queryParam("digitalInstances", "false") //
                .expect() //
                .statusCode(200) //
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString)) //
                .body(hasXPath("/c:response/c:digitalDocument", nsContext)).body(//
                        not(hasXPath("/c:response/c:digitalDocument/c:digitalInstances", nsContext)))//
                .when().get(buildUrl(id)).andReturn().asString();
    }

    // action=redirect

    @Test
    public void actionRedirectUrnDeactivatedWithoutDi() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbnDeactivatedDiNone, id, USER);
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "redirect")//
                .expect()//
                .statusCode(403)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "URN_NBN_DEACTIVATED");
    }

    @Test
    public void actionRedirectUrnDeactivatedWithDiActive() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbnDeactivatedDiActive, id, USER);
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "redirect")//
                .expect()//
                .statusCode(403)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "URN_NBN_DEACTIVATED");
    }

    @Test
    public void actionRedirectUrnDeactivatedWithDiDeactivated() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbnDeactivatedDiDeactivated, id, USER);
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "redirect")//
                .expect()//
                .statusCode(403)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "URN_NBN_DEACTIVATED");
    }

    @Test
    public void actionRedirectDiNotFound() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbnActiveDiNone, id, USER);
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "redirect")//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))// checking for correct type of response and namespace
                .when().get(buildUrl(id)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "UNKNOWN_DIGITAL_INSTANCE");
    }

    @Test
    public void actionRedirectDiDeactivated() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbnActiveDiDeactivated, id, USER);
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "redirect")//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildUrl(id)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "UNKNOWN_DIGITAL_INSTANCE");
    }

    @Test
    public void actionRedirectDiActive() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbnActiveDiActive, id, USER);
        with().config(namespaceAwareXmlConfig()).queryParam("action", "redirect")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.HTML)//
                .body(not(containsString("<title>CZIDLO</title>")))// should redirect to digital instance url, not web search
                .when().get(buildUrl(id)).andReturn().asString();
    }

    // action=decide

    @Test
    public void actionDecideDiNotFound() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbnActiveDiNone, id, USER);
        with().config(namespaceAwareXmlConfig()).queryParam("action", "decide")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.HTML)//
                .body(containsString("<title>CZIDLO</title>"))// should redirect to web search
                .when().get(buildUrl(id)).andReturn().asString();
    }

    @Test
    public void actionDecideDiDeactivated() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbnActiveDiDeactivated, id, USER);
        with().config(namespaceAwareXmlConfig()).queryParam("action", "decide")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.HTML)//
                .body(containsString("<title>CZIDLO</title>"))// should redirect to web search
                .when().get(buildUrl(id)).andReturn().asString();
    }

    @Test
    public void actionDecideDiActive() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbnActiveDiActive, id, USER);
        with().config(namespaceAwareXmlConfig()).queryParam("action", "decide")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.HTML)//
                .body(not(containsString("<title>CZIDLO</title>")))// should redirect to digital instance url, not web search
                .when().get(buildUrl(id)).andReturn().asString();
    }

}
