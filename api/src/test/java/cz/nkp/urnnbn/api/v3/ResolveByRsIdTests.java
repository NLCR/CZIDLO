package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.not;

import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

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

    private static final Credentials USER = new Credentials("martin", "i0oEhu");

    private final String REGISTRAR = "aba001";
    private final String URNNBN = "urn:nbn:cz:aba001-000000";

    private final RsId ID_WITHOUT_DI = new RsId("tst02", "K4_pid", "uuid:123");
    private final RsId ID_WITH_ACTIVE_DI = new RsId("p01nk", "uuid", "113e83b0-1db2-11e2-bec6-005056827e51");
    private final RsId ID_WITH_DEACTIVATED_DI = new RsId("p01mzk", "uuid", "eff774d0-24a2-11e2-a6ce-5ef3fc9ae867");
    private final RsId ID_NO_DD = new RsId("tst02", "K4_pid", "uuid:123456789");
    private final RsId ID_DD_DEACTIVATED = new RsId("tst02", "K4_pid", "uuid:38bfc69d-1d10-4822-b7a4-c7531bb4aedl");

    @BeforeSuite
    public void beforeSuite() {
        init();
    }

    @BeforeMethod
    public void beforeMethod() {
        deleteAllRegistrarScopeIdentifiers(URNNBN, USER);
    }

    @AfterMethod
    public void afterMethod() {
        deleteAllRegistrarScopeIdentifiers(URNNBN, USER);
    }

    @Test
    public void resolveIdTypeInvalid() {
        resolveIdTypeInvalid(new RsId(REGISTRAR, RSID_TYPE_INVALID_TO_SHORT, "value"));
        resolveIdTypeInvalid(new RsId(REGISTRAR, RSID_TYPE_INVALID_TO_LONG, "value"));
        // reserved
        for (String type : RSID_TYPE_INVALID_RESERVED) {
            resolveIdTypeInvalid(new RsId(REGISTRAR, type, "value"));
        }
        // unreserved
        for (String type : RSID_TYPE_INVALID_UNRESERVED) {
            resolveIdTypeInvalid(new RsId(REGISTRAR, type, "value"));
        }
    }

    private void resolveIdTypeInvalid(RsId id) {
        String responseXml = with().config(namespaceAwareXmlConfig()) //
                .expect() //
                .statusCode(400) //
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString)) //
                .body(hasXPath("/c:response/c:error", nsContext)) //
                .when().get(buildResolvationPath(id))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        // TODO:APIv4: rename error to INVALID_ID_TYPE
        Assert.assertEquals(xmlPath.get("code"), "INVALID_DIGITAL_DOCUMENT_ID_TYPE");
    }

    @Test
    public void resolveIdValueInvalid() {
        // TODO: enable when after this bug is fixed: https://github.com/NLCR/CZIDLO/issues/132
        // resolveIdValueInvalid(new RsId(REGISTRAR, "toShort", RSID_VALUE_INVALID_TO_SHORT));
        // resolveIdValueInvalid(new RsId(REGISTRAR, "toLong", RSID_VALUE_INVALID_TO_LONG));
    }

    private void resolveIdValueInvalid(RsId id) {
        String responseXml = with().config(namespaceAwareXmlConfig()) //
                .expect() //
                .statusCode(400) //
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString)) //
                .body(hasXPath("/c:response/c:error", nsContext)) //
                .when().get(buildResolvationPath(id))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        // TODO:APIv4: rename error to INVALID_ID_VALUE
        Assert.assertEquals(xmlPath.get("code"), "TODO");
    }

    @Test
    public void resolveIdTypeAndValueOk() {
        String urnNbn = URNNBN;
        // values
        resolveIdTypeAndValueOk(urnNbn, new RsId(REGISTRAR, "inLength", RSID_VALUE_OK_MIN_LENGTH));
        resolveIdTypeAndValueOk(urnNbn, new RsId(REGISTRAR, "maxLength", RSID_VALUE_OK_MAX_LENGTH));
        for (int i = 0; i < RSID_VALUE_OK_RESERVED.length; i++) {
            resolveIdTypeAndValueOk(urnNbn, new RsId(REGISTRAR, "reserved" + i, RSID_VALUE_OK_RESERVED[i]));
        }
        for (int i = 0; i < RSID_VALUE_OK_UNRESERVED.length; i++) {
            resolveIdTypeAndValueOk(urnNbn, new RsId(REGISTRAR, "unreserved" + i, RSID_VALUE_OK_UNRESERVED[i]));
        }

        // types
        resolveIdTypeAndValueOk(urnNbn, new RsId(REGISTRAR, RSID_TYPE_OK_MIN_LENGTH, "value"));
        resolveIdTypeAndValueOk(urnNbn, new RsId(REGISTRAR, RSID_TYPE_OK_MAX_LENGTH, "value"));
        for (int i = 0; i < RSID_TYPE_OK_RESERVED.length; i++) {
            resolveIdTypeAndValueOk(urnNbn, new RsId(REGISTRAR, RSID_TYPE_OK_RESERVED[i], "value"));
        }
        for (int i = 0; i < RSID_TYPE_OK_UNRESERVED.length; i++) {
            resolveIdTypeAndValueOk(urnNbn, new RsId(REGISTRAR, RSID_TYPE_OK_UNRESERVED[i], "value"));
        }
    }

    private void resolveIdTypeAndValueOk(String urnNbn, RsId id) {
        // insert id
        insertRegistrarScopeId(urnNbn, id, USER);
        // resolve
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildResolvationPath(id))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.digitalDocument");
        Assert.assertEquals(xmlPath.get("registrarScopeIdentifiers.id.find { it.@type == \'" + id.type + "\' }"), id.value);
        // remove id
        deleteRegistrarScopeId(urnNbn, id, USER);
    }

    @Test
    public void resolveNoSuchDocument() {
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "")//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().get(buildResolvationPath(ID_NO_DD))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "UNKNOWN_DIGITAL_DOCUMENT");
    }

    @Test
    public void resolveDocumentDeactivatedAndActionRedirect() {
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "redirect")//
                .expect()//
                .statusCode(403)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().get(buildResolvationPath(ID_DD_DEACTIVATED))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "URN_NBN_DEACTIVATED");
    }

    // TODO: projit odsud dal

    @Test
    public void resolveActionEmpty() {
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().get(buildResolvationPath(ID_WITHOUT_DI))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
    }

    @Test
    public void resolveActionInvalid() {
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "nonsense")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().get(buildResolvationPath(ID_WITHOUT_DI))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
    }

    @Test
    public void resolveActionShow() {
        with().config(namespaceAwareXmlConfig()).queryParam("action", "show")//
                .expect().statusCode(200).contentType(ContentType.HTML)//
                .body(containsString("<title>CZIDLO</title>"))// should redirect to web search
                .when().get(buildResolvationPath(ID_WITHOUT_DI));
    }

    @Test
    public void resolveActionShowFormatInvalid() {
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "nonsense")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().get(buildResolvationPath(ID_WITHOUT_DI))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
    }

    @Test
    public void resolveActionShowFormatEmpty() {
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().get(buildResolvationPath(ID_WITHOUT_DI))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
    }

    @Test
    public void resolveActionShowFormatHtml() {
        with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "html")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.HTML)//
                .body(containsString("<title>CZIDLO</title>"))// should redirect to web search
                .when().get(buildResolvationPath(ID_WITHOUT_DI));
    }

    @Test
    public void resolveActionShowFormatXml() {
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))// checking for correct type of response and namespace
                .when().get(buildResolvationPath(ID_WITHOUT_DI)).andReturn().asString();
        // xml path handles namespaces incorrectly, but it does work without prefixes (which is incorrect)
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.digitalDocument");
        String xmlPathExp = "registrarScopeIdentifiers.id.find { it.@type == '" + ID_WITHOUT_DI.type + "' }";
        Assert.assertEquals(xmlPath.get(xmlPathExp), ID_WITHOUT_DI.value);
    }

    @Test
    public void resolveActionShowFormatXmlWithDigitalInstances() {
        with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml").queryParam("digitalInstances", "true")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument/c:digitalInstances", nsContext))// checking for correct type of response and namespace
                .when().get(buildResolvationPath(ID_WITHOUT_DI)).andReturn().asString();
    }

    @Test
    public void resolveActionShowFormatXmlWithoutDigitalInstances() {
        with().config(namespaceAwareXmlConfig())//
                .queryParam("action", "show").queryParam("format", "xml").queryParam("digitalInstances", "false")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))// checking for correct type of response and namespace
                .body(not(hasXPath("/c:response/c:digitalDocument/c:digitalInstances", nsContext)))//
                .when().get(buildResolvationPath(ID_WITHOUT_DI)).andReturn().asString();
    }

    @Test
    public void resolveActionShowFormatXmlWithDigitalInstancesEmpty() {
        String xml = with().config(namespaceAwareXmlConfig())//
                .queryParam("action", "show").queryParam("format", "xml").queryParam("digitalInstances", "")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().get(buildResolvationPath(ID_WITHOUT_DI))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
    }

    @Test
    public void resolveActionShowFormatXmlWithDigitalInstancesInvalid() {
        String xml = with().config(namespaceAwareXmlConfig())//
                .queryParam("action", "show").queryParam("format", "xml").queryParam("digitalInstances", "nope")//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().get(buildResolvationPath(ID_WITHOUT_DI))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
    }

    @Test
    public void resolveActionRedirectWithoutDi() {
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "redirect")//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))// checking for correct type of response and namespace
                .when().get(buildResolvationPath(ID_WITHOUT_DI))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "UNKNOWN_DIGITAL_INSTANCE");
    }

    @Test
    public void resolveActionRedirectWithActiveDi() {
        with().config(namespaceAwareXmlConfig()).queryParam("action", "redirect")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.HTML)//
                .body(not(containsString("<title>CZIDLO</title>")))// should redirect to digital instance url, not web search
                .when().get(buildResolvationPath(ID_WITH_ACTIVE_DI));
    }

    @Test
    public void resolveActionRedirectWithDeactivatedDi() {
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "redirect")//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))// checking for correct type of response and namespace
                .when().get(buildResolvationPath(ID_WITH_DEACTIVATED_DI))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "UNKNOWN_DIGITAL_INSTANCE");
    }

    @Test
    public void resolveActionDecideWithoutDi() {
        with().config(namespaceAwareXmlConfig()).queryParam("action", "decide")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.HTML)//
                .body(containsString("<title>CZIDLO</title>"))// should redirect to web search
                .when().get(buildResolvationPath(ID_WITHOUT_DI));
    }

    @Test
    public void resolveActionDecideWithActiveDi() {
        with().config(namespaceAwareXmlConfig()).queryParam("action", "decide")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.HTML)//
                .body(not(containsString("<title>CZIDLO</title>")))// should redirect to digital instance url, not web search
                .when().get(buildResolvationPath(ID_WITH_ACTIVE_DI));
    }

    @Test
    public void resolveActionDecideWithDeactivatedDi() {
        with().config(namespaceAwareXmlConfig()).queryParam("action", "decide")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.HTML)//
                .body(containsString("<title>CZIDLO</title>"))// should redirect to web search
                .when().get(buildResolvationPath(ID_WITH_DEACTIVATED_DI));
    }

}
