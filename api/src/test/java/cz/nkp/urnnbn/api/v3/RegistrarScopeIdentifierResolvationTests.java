package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.not;

import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.api.Utils;

public class RegistrarScopeIdentifierResolvationTests extends ApiV3Tests {

    static class Id {
        final String registrarCode;
        final String type;
        final String value;

        public Id(String registrarCode, String type, String value) {
            this.registrarCode = registrarCode;
            this.type = type;
            this.value = value;
        }
    }

    private static final Logger LOGGER = Logger.getLogger(RegistrarScopeIdentifierResolvationTests.class.getName());

    private final Id ID_WITHOUT_DI = new Id("tst02", "K4_pid", "uuid:123");
    private final Id ID_WITH_ACTIVE_DI = new Id("p01nk", "uuid", "113e83b0-1db2-11e2-bec6-005056827e51");
    private final Id ID_WITH_DEACTIVATED_DI = new Id("p01mzk", "uuid", "eff774d0-24a2-11e2-a6ce-5ef3fc9ae867");
    private final Id ID_NO_DD = new Id("tst02", "K4_pid", "uuid:123456789");
    private final Id ID_DD_DEACTIVATED = new Id("tst02", "K4_pid", "uuid:38bfc69d-1d10-4822-b7a4-c7531bb4aedl");

    private final String DEFAULT_ID_VALUE = "something";
    private final String DEFAULT_REGISTRAR = "aba001";

    // select
    // registrar.code as registrar,
    // rsid.type as type,
    // rsid.idvalue as value
    // from
    // (select registrarid, type, idvalue from registrarscopeid) as rsid join
    // (select id, code from registrar) as registrar on rsid.registrarid=registrar.id;

    @BeforeSuite
    public void beforeSuite() {
        init();
    }

    private String buildPath(Id id) {
        return "/registrars/" + id.registrarCode + "/digitalDocuments/registrarScopeIdentifier/" + id.type + "/" + id.value;
    }

    @Test
    public void resolveIdIdTypeToShort() {
        String xml = with().config(namespaceAwareXmlConfig())//
                .expect().statusCode(400).contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().get(buildPath(new Id(DEFAULT_REGISTRAR, "a", DEFAULT_ID_VALUE)))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        // TODO:APIv4: rename error to INVALID_ID_TYPE
        Assert.assertEquals(xmlPath.get("code"), "INVALID_DIGITAL_DOCUMENT_ID_TYPE");
    }

    @Test
    public void resolveIdIdTypeToLong() {
        String xml = with().config(namespaceAwareXmlConfig())//
                .expect().statusCode(400).contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().get(buildPath(new Id(DEFAULT_REGISTRAR, "aaaaaaaaa1aaaaaaaaa2a", DEFAULT_ID_VALUE)))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        // TODO:APIv4: rename error to INVALID_ID_TYPE
        Assert.assertEquals(xmlPath.get("code"), "INVALID_DIGITAL_DOCUMENT_ID_TYPE");
    }

    @Test
    public void resolveIdIdTypeSizeOk() {
        // length 2
        String type = "aA";
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200).contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildPath(new Id(DEFAULT_REGISTRAR, type, DEFAULT_ID_VALUE)))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.digitalDocument");
        Assert.assertEquals(xmlPath.get("registrarScopeIdentifiers.id.find { it.@type == \'" + type + "\' }"), DEFAULT_ID_VALUE);

        // length 20
        type = "aaaaaaaaa1AAAAAAAAA2";
        xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200).contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildPath(new Id(DEFAULT_REGISTRAR, type, DEFAULT_ID_VALUE)))//
                .andReturn().asString();
        xmlPath = XmlPath.from(xml).setRoot("response.digitalDocument");
        Assert.assertEquals(xmlPath.get("registrarScopeIdentifiers.id.find { it.@type == \'" + type + "\' }"), DEFAULT_ID_VALUE);
    }

    @Test
    public void resolveIdIdTypeValidCharactersReserved() {
        // TODO: vytvorit existujici id
        String xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false)//
                .expect().statusCode(404).contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().get(buildPath(new Id(DEFAULT_REGISTRAR, Utils.urlEncodeReservedCharacters("Ab9:8cD"), DEFAULT_ID_VALUE)))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "UNKNOWN_DIGITAL_DOCUMENT");
    }

    @Test
    public void resolveIdIdTypeValidCharactersUnreserved() {
        // TODO: vytvorit existujici id a otestovat, ze se to chova stejne, at url encoded, nebo ne
        String xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false)//
                .expect().statusCode(404).contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().get(buildPath(new Id(DEFAULT_REGISTRAR, "aA9_-:", DEFAULT_ID_VALUE)))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "UNKNOWN_DIGITAL_DOCUMENT");
    }

    @Test
    public void resolveIdIdTypeInvalidCharactersReserved() {
        // only ":" allowed from reserved character set
        // TODO: character '/' ignored for now until this bug is fixed: https://github.com/NLCR/CZIDLO/issues/129
        // resolveIdIdTypeInvalidCharactersReserved('/');
        resolveIdIdTypeInvalidCharactersReserved('?');
        resolveIdIdTypeInvalidCharactersReserved('#');
        resolveIdIdTypeInvalidCharactersReserved('[');
        resolveIdIdTypeInvalidCharactersReserved(']');
        resolveIdIdTypeInvalidCharactersReserved('@');
        resolveIdIdTypeInvalidCharactersReserved('!');
        resolveIdIdTypeInvalidCharactersReserved('$');
        resolveIdIdTypeInvalidCharactersReserved('&');
        resolveIdIdTypeInvalidCharactersReserved('\'');
        resolveIdIdTypeInvalidCharactersReserved('(');
        resolveIdIdTypeInvalidCharactersReserved(')');
        resolveIdIdTypeInvalidCharactersReserved('*');
        resolveIdIdTypeInvalidCharactersReserved('+');
        resolveIdIdTypeInvalidCharactersReserved(',');
        resolveIdIdTypeInvalidCharactersReserved(';');
        resolveIdIdTypeInvalidCharactersReserved('=');
    }

    private void resolveIdIdTypeInvalidCharactersReserved(char c) {
        String xml = with().config(namespaceAwareXmlConfig())//
                .urlEncodingEnabled(false)//
                .expect().statusCode(400).contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().get(buildPath(new Id(DEFAULT_REGISTRAR, Utils.urlEncodeReservedCharacters("" + c + c), DEFAULT_ID_VALUE)))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        // TODO:APIv4: rename error to INVALID_ID_TYPE
        Assert.assertEquals(xmlPath.get("code"), "INVALID_DIGITAL_DOCUMENT_ID_TYPE");
    }

    @Test
    public void resolveIdIdTypeInvalidCharactersUnreserved() {
        // only ~ not allowed (from unreserved character set)

        // not url-encoded
        String xml = with().config(namespaceAwareXmlConfig())//
                .urlEncodingEnabled(false)//
                .expect().statusCode(400).contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().get(buildPath(new Id(DEFAULT_REGISTRAR, "~~", DEFAULT_ID_VALUE)))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        // TODO:APIv4: rename error to INVALID_ID_TYPE
        Assert.assertEquals(xmlPath.get("code"), "INVALID_DIGITAL_DOCUMENT_ID_TYPE");

        // url-encoded
        xml = with().config(namespaceAwareXmlConfig())//
                .urlEncodingEnabled(false)//
                .expect().statusCode(400).contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().get(buildPath(new Id(DEFAULT_REGISTRAR, Utils.urlEncodeReservedCharacters("~~"), DEFAULT_ID_VALUE)))//
                .andReturn().asString();
        xmlPath = XmlPath.from(xml).setRoot("response.error");
        // TODO:APIv4: rename error to INVALID_ID_TYPE
        Assert.assertEquals(xmlPath.get("code"), "INVALID_DIGITAL_DOCUMENT_ID_TYPE");
    }

    // TODO: test id value similarly as id type (reserved, unreserved characters, urlencoded or not)

    @Test
    public void resolveNoSuchDocument() {
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "")//
                .expect().statusCode(404).contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().get(buildPath(ID_NO_DD))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "UNKNOWN_DIGITAL_DOCUMENT");
    }

    @Test
    public void resolveDocumentDeactivatedAndActionRedirect() {
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "redirect")//
                .expect().statusCode(403).contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().get(buildPath(ID_DD_DEACTIVATED))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "URN_NBN_DEACTIVATED");
    }

    @Test
    public void resolveActionEmpty() {
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "")//
                .expect().statusCode(400).contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().get(buildPath(ID_WITHOUT_DI))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
    }

    @Test
    public void resolveActionInvalid() {
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "nonsense")//
                .expect().statusCode(400).contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().get(buildPath(ID_WITHOUT_DI))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
    }

    @Test
    public void resolveActionShow() {
        //
        with().config(namespaceAwareXmlConfig()).queryParam("action", "show")//
                .expect().statusCode(200).contentType(ContentType.HTML)//
                .body(containsString("<title>CZIDLO</title>"))// should redirect to web search
                .when().get(buildPath(ID_WITHOUT_DI));
    }

    @Test
    public void resolveActionShowFormatInvalid() {
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "nonsense")//
                .expect().statusCode(400).contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().get(buildPath(ID_WITHOUT_DI))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
    }

    @Test
    public void resolveActionShowFormatEmpty() {
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "")//
                .expect().statusCode(400).contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().get(buildPath(ID_WITHOUT_DI))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
    }

    @Test
    public void resolveActionShowFormatHtml() {
        with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "html")//
                .expect().statusCode(200).contentType(ContentType.HTML)//
                .body(containsString("<title>CZIDLO</title>"))// should redirect to web search
                .when().get(buildPath(ID_WITHOUT_DI));
    }

    @Test
    public void resolveActionShowFormatXml() {
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200).contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))// checking for correct type of response and namespace
                .when().get(buildPath(ID_WITHOUT_DI)).andReturn().asString();
        // xml path handles namespaces incorrectly, but it does work without prefixes (which is incorrect)
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.digitalDocument");
        String xmlPathExp = "registrarScopeIdentifiers.id.find { it.@type == '" + ID_WITHOUT_DI.type + "' }";
        Assert.assertEquals(xmlPath.get(xmlPathExp), ID_WITHOUT_DI.value);
    }

    @Test
    public void resolveActionShowFormatXmlWithDigitalInstances() {
        with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml").queryParam("digitalInstances", "true")//
                .expect()//
                .statusCode(200).contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument/c:digitalInstances", nsContext))// checking for correct type of response and namespace
                .when().get(buildPath(ID_WITHOUT_DI)).andReturn().asString();
    }

    @Test
    public void resolveActionShowFormatXmlWithoutDigitalInstances() {
        with().config(namespaceAwareXmlConfig())//
                .queryParam("action", "show").queryParam("format", "xml").queryParam("digitalInstances", "false")//
                .expect()//
                .statusCode(200).contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))// checking for correct type of response and namespace
                .body(not(hasXPath("/c:response/c:digitalDocument/c:digitalInstances", nsContext)))//
                .when().get(buildPath(ID_WITHOUT_DI)).andReturn().asString();
    }

    @Test
    public void resolveActionShowFormatXmlWithDigitalInstancesEmpty() {
        String xml = with().config(namespaceAwareXmlConfig())//
                .queryParam("action", "show").queryParam("format", "xml").queryParam("digitalInstances", "")//
                .expect().statusCode(400).contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().get(buildPath(ID_WITHOUT_DI))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
    }

    @Test
    public void resolveActionShowFormatXmlWithDigitalInstancesInvalid() {
        String xml = with().config(namespaceAwareXmlConfig())//
                .queryParam("action", "show").queryParam("format", "xml").queryParam("digitalInstances", "nope")//
                .expect().statusCode(400).contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().get(buildPath(ID_WITHOUT_DI))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_QUERY_PARAM_VALUE");
    }

    @Test
    public void resolveActionRedirectWithoutDi() {
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "redirect")//
                .expect()//
                .statusCode(404).contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))// checking for correct type of response and namespace
                .when().get(buildPath(ID_WITHOUT_DI))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "UNKNOWN_DIGITAL_INSTANCE");
    }

    @Test
    public void resolveActionRedirectWithActiveDi() {
        with().config(namespaceAwareXmlConfig()).queryParam("action", "redirect")//
                .expect()//
                .statusCode(200).contentType(ContentType.HTML)//
                .body(not(containsString("<title>CZIDLO</title>")))// should redirect to digital instance url, not web search
                .when().get(buildPath(ID_WITH_ACTIVE_DI));
    }

    @Test
    public void resolveActionRedirectWithDeactivatedDi() {
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "redirect")//
                .expect()//
                .statusCode(404).contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))// checking for correct type of response and namespace
                .when().get(buildPath(ID_WITH_DEACTIVATED_DI))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "UNKNOWN_DIGITAL_INSTANCE");
    }

    @Test
    public void resolveActionDecideWithoutDi() {
        with().config(namespaceAwareXmlConfig()).queryParam("action", "decide")//
                .expect()//
                .statusCode(200).contentType(ContentType.HTML)//
                .body(containsString("<title>CZIDLO</title>"))// should redirect to web search
                .when().get(buildPath(ID_WITHOUT_DI));
    }

    @Test
    public void resolveActionDecideWithActiveDi() {
        with().config(namespaceAwareXmlConfig()).queryParam("action", "decide")//
                .expect()//
                .statusCode(200).contentType(ContentType.HTML)//
                .body(not(containsString("<title>CZIDLO</title>")))// should redirect to digital instance url, not web search
                .when().get(buildPath(ID_WITH_ACTIVE_DI));
    }

    @Test
    public void resolveActionDecideWithDeactivatedDi() {
        with().config(namespaceAwareXmlConfig()).queryParam("action", "decide")//
                .expect()//
                .statusCode(200).contentType(ContentType.HTML)//
                .body(containsString("<title>CZIDLO</title>"))// should redirect to web search
                .when().get(buildPath(ID_WITH_DEACTIVATED_DI));
    }

}
