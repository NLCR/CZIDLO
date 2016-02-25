package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.api.Utils;

/**
 * Tests for GET /api/v3/registrars/${REGISTRAR_CODE}/digitalDocuments/registrarScopeIdentifier/${ID_TYPE}/${ID_VALUE}
 *
 */
public class ResolveByRsIdTests extends ApiV3Tests {

    // sql to get (registrar, id_type, id_value) triplets
    // select
    // registrar.code as registrar,
    // rsid.type as type,
    // rsid.idvalue as value
    // from
    // (select registrarid, type, idvalue from registrarscopeid) as rsid join
    // (select id, code from registrar) as registrar on rsid.registrarid=registrar.id;

    private static final Logger LOGGER = Logger.getLogger(ResolveByRsIdTests.class.getName());

    private final Credentials USER = new Credentials("martin", "i0oEhu");
    private final String REGISTRAR = "aba001";
    private final String URNNBN = "urn:nbn:cz:aba001-000000";
    private final String URNNBN_WITHOUT_DI = "urn:nbn:cz:aba001-000004";
    private final String URNNBN_WITH_DI_ACTIVE = "urn:nbn:cz:aba001-00000t";
    private final String URNNBN_WITH_DI_DEACTIVATED = "urn:nbn:cz:aba001-000006";
    private final String URNNBN_DD_DEACTIVATED = "urn:nbn:cz:aba001-00000d"; // TODO:APIv4: should throw error when trying to insert rsid

    @BeforeClass
    public void beforeClass() {
        init();
    }

    @BeforeMethod
    public void beforeMethod() {
        deleteAllRegistrarScopeIdentifiers(URNNBN, USER);
        deleteAllRegistrarScopeIdentifiers(URNNBN_WITHOUT_DI, USER);
        deleteAllRegistrarScopeIdentifiers(URNNBN_WITH_DI_ACTIVE, USER);
        deleteAllRegistrarScopeIdentifiers(URNNBN_WITH_DI_DEACTIVATED, USER);
        // deleteAllRegistrarScopeIdentifiers(URNNBN_NO_DD, USER);
        deleteAllRegistrarScopeIdentifiers(URNNBN_DD_DEACTIVATED, USER);
    }

    @AfterMethod
    public void afterMethod() {
        deleteAllRegistrarScopeIdentifiers(URNNBN, USER);
        deleteAllRegistrarScopeIdentifiers(URNNBN_WITHOUT_DI, USER);
        deleteAllRegistrarScopeIdentifiers(URNNBN_WITH_DI_ACTIVE, USER);
        deleteAllRegistrarScopeIdentifiers(URNNBN_WITH_DI_DEACTIVATED, USER);
        // deleteAllRegistrarScopeIdentifiers(URNNBN_NO_DD, USER);
        deleteAllRegistrarScopeIdentifiers(URNNBN_DD_DEACTIVATED, USER);
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
                .when().get(buildResolvationPath(idForResolvation))//
                .andReturn().asString();
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
                .when().get(buildResolvationPath(idForResolvation))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "UNKNOWN_REGISTRAR");
    }

    @Test
    public void rsIdTypeInvalidAll() {
        for (String type : RSID_TYPES_INVALID) {
            RsId idForResolvation = new RsId(REGISTRAR, type, "value");
            LOGGER.info(idForResolvation.toString());
            // idForResolvation wasn't inserted, but INVALID_DIGITAL_DOCUMENT_ID_TYPE should be returned before this becomes relevant
            String responseXml = with().config(namespaceAwareXmlConfig()) //
                    .expect() //
                    .statusCode(400) //
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString)) //
                    .body(hasXPath("/c:response/c:error", nsContext)) //
                    .when().get(buildResolvationPath(idForResolvation)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
            // TODO:APIv4: rename error to INVALID_ID_TYPE
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
            insertRegistrarScopeId(URNNBN, idValidValue, USER);
            // idForResolvation wasn't inserted, but INVALID_DIGITAL_DOCUMENT_ID_TYPE should be returned before this becomes relevant
            // expected http response code 404 and app error code UNKNOWN_DIGITAL_DOCUMENT until this bug fixed:
            // https://github.com/NLCR/CZIDLO/issues/132
            String xml = with().config(namespaceAwareXmlConfig())//
                    .expect()//
                    .statusCode(404)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:error", nsContext))//
                    .when().get(buildResolvationPath(idForResolvation))//
                    .andReturn().asString();
            XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
            // TODO:APIv4: https://github.com/NLCR/CZIDLO/issues/132
            assertThat(xmlPath.getString("code"), equalTo("UNKNOWN_DIGITAL_DOCUMENT"));
            // cleanup
            deleteRegistrarScopeId(URNNBN, idValidValue, USER);
        }
    }

    @Test
    public void rsIdTypeValidValueValidAll() {
        RsId idToBeFetched = new RsId(REGISTRAR, "idRequestedType", "idRequestedValue");
        insertRegistrarScopeId(URNNBN, idToBeFetched, USER);
        for (String type : RSID_TYPES_VALID) {
            rsIdTypeValidValueValid(new RsId(REGISTRAR, type, "value"));
        }
        int counter = 0;
        for (String value : RSID_VALUES_VALID) {
            rsIdTypeValidValueValid(new RsId(REGISTRAR, "typeValid" + ++counter, value));
        }
    }

    private void rsIdTypeValidValueValid(RsId idForResolvation) {
        LOGGER.info(idForResolvation.toString());
        // insert id for resolvation
        insertRegistrarScopeId(URNNBN, idForResolvation, USER);
        // get id value byt idForResolvation and type
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildResolvationPath(idForResolvation)) //
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.digitalDocument");
        Assert.assertEquals(xmlPath.get("registrarScopeIdentifiers.id.find { it.@type == \'" + idForResolvation.type + "\' }"),
                idForResolvation.value);
        // delete id
        deleteRegistrarScopeId(URNNBN, idForResolvation, USER);
    }

    @Test
    public void unknownDigitalDocument() {
        RsId id = new RsId(REGISTRAR, "unknownType", "unknownValue");
        String xml = with().config(namespaceAwareXmlConfig()) //
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildResolvationPath(id))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "UNKNOWN_DIGITAL_DOCUMENT");
    }

    @Test
    public void actionInvalid() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(URNNBN_WITHOUT_DI, id, USER);
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "nonsense")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildResolvationPath(id))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
    }

    @Test
    public void actionEmpty() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(URNNBN_WITHOUT_DI, id, USER);
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildResolvationPath(id))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
    }

    // action=show

    @Test
    public void actionShow() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(URNNBN_WITHOUT_DI, id, USER);
        with().config(namespaceAwareXmlConfig()).queryParam("action", "show")//
                .expect().statusCode(200).contentType(ContentType.HTML)//
                .body(containsString("<title>CZIDLO</title>"))// should redirect to web search
                .when().get(buildResolvationPath(id));
    }

    @Test
    public void actionShowFormatEmpty() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(URNNBN_WITHOUT_DI, id, USER);
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildResolvationPath(id))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
    }

    @Test
    public void actionShowFormatInvalid() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(URNNBN_WITHOUT_DI, id, USER);
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "nonsense")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildResolvationPath(id))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
    }

    @Test
    public void actionShowFormatHtml() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(URNNBN_WITHOUT_DI, id, USER);
        with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "html")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.HTML)//
                .body(containsString("<title>CZIDLO</title>"))// should redirect to web search
                .when().get(buildResolvationPath(id));
    }

    @Test
    public void actionShowFormatXml() {
        // TODO: rozlisit na pripady ruznych stavu DD, URN, DI
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(URNNBN_WITHOUT_DI, id, USER);
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext)).when().get(buildResolvationPath(id)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.digitalDocument");
        String xmlPathExp = "registrarScopeIdentifiers.id.find { it.@type == '" + id.type + "' }";
        Assert.assertEquals(xmlPath.get(xmlPathExp), id.value);
    }

    @Test
    public void actionShowFormatXmlDigitalInstancesEmpty() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(URNNBN_WITHOUT_DI, id, USER);
        String xml = with().config(namespaceAwareXmlConfig())//
                .queryParam("action", "show").queryParam("format", "xml").queryParam("digitalInstances", "")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildResolvationPath(id))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
    }

    @Test
    public void actionShowFormatXmlDigitalInstancesInvalid() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(URNNBN_WITHOUT_DI, id, USER);
        String xml = with().config(namespaceAwareXmlConfig())//
                .queryParam("action", "show").queryParam("format", "xml").queryParam("digitalInstances", "nope")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildResolvationPath(id))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
    }

    @Test
    public void actionShowFormatXmlDigitalInstancesTrue() {
        // TODO: situace DD existuje, neexistuje, deaktivovane
        RsId idNoDi = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(URNNBN_WITHOUT_DI, idNoDi, USER);
        with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml").queryParam("digitalInstances", "true")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument/c:digitalInstances", nsContext))// checking for correct type of response and namespace
                .when().get(buildResolvationPath(idNoDi))//
                .andReturn().asString();
    }

    @Test
    public void actionShowFormatXmlDigitalInstancesFalse() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(URNNBN_WITHOUT_DI, id, USER);
        with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml").queryParam("digitalInstances", "false") //
                .expect() //
                .statusCode(200) //
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString)) //
                .body(hasXPath("/c:response/c:digitalDocument", nsContext)).body(//
                        not(hasXPath("/c:response/c:digitalDocument/c:digitalInstances", nsContext)))//
                .when()//
                .get(buildResolvationPath(id)).andReturn().asString();
    }

    // action=redirect

    @Test
    public void actionRedirectUrnDeactivated() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(URNNBN_DD_DEACTIVATED, id, USER);
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "redirect")//
                .expect()//
                .statusCode(403)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildResolvationPath(id))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "URN_NBN_DEACTIVATED");
    }

    @Test
    public void actionRedirectDiNotFound() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(URNNBN_WITHOUT_DI, id, USER);
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "redirect")//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))// checking for correct type of response and namespace
                .when().get(buildResolvationPath(id))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "UNKNOWN_DIGITAL_INSTANCE");
    }

    @Test
    public void actionRedirectDiDeactivated() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(URNNBN_WITH_DI_DEACTIVATED, id, USER);
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "redirect")//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildResolvationPath(id))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "UNKNOWN_DIGITAL_INSTANCE");
    }

    @Test
    public void actionRedirectDiActive() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(URNNBN_WITH_DI_ACTIVE, id, USER);
        with().config(namespaceAwareXmlConfig()).queryParam("action", "redirect")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.HTML)//
                .body(not(containsString("<title>CZIDLO</title>")))// should redirect to digital instance url, not web search
                .when().get(buildResolvationPath(id));
    }

    // action=decide

    @Test
    public void actionDecideDiNotFound() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(URNNBN_WITHOUT_DI, id, USER);
        with().config(namespaceAwareXmlConfig()).queryParam("action", "decide")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.HTML)//
                .body(containsString("<title>CZIDLO</title>"))// should redirect to web search
                .when().get(buildResolvationPath(id));
    }

    @Test
    public void actionDecideDiDeactivated() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(URNNBN_WITH_DI_DEACTIVATED, id, USER);
        with().config(namespaceAwareXmlConfig()).queryParam("action", "decide")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.HTML)//
                .body(containsString("<title>CZIDLO</title>"))// should redirect to web search
                .when().get(buildResolvationPath(id));
    }

    @Test
    public void actionDecideDiActive() {
        RsId id = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(URNNBN_WITH_DI_ACTIVE, id, USER);
        with().config(namespaceAwareXmlConfig()).queryParam("action", "decide")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.HTML)//
                .body(not(containsString("<title>CZIDLO</title>")))// should redirect to digital instance url, not web search
                .when().get(buildResolvationPath(id));
    }

}
