package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.api.Utils;

public class RegistrarScopeIdentifierResolvationTests extends ApiV3Tests {

    // sql to get (registrar, id_type, id_value) triplets
    // select
    // registrar.code as registrar,
    // rsid.type as type,
    // rsid.idvalue as value
    // from
    // (select registrarid, type, idvalue from registrarscopeid) as rsid join
    // (select id, code from registrar) as registrar on rsid.registrarid=registrar.id;

    private static final Logger LOGGER = Logger.getLogger(RegistrarScopeIdentifierResolvationTests.class.getName());

    private static final String USER_WITH_RIGHTS_LOGIN = "martin";
    private static final String USER_WITH_RIGHTS_PASSWORD = "i0oEhu";

    private static final String USER_NO_RIGHTS_LOGIN = "nobody";
    private static final String USER_NO_RIGHTS_PASSWORD = "skgo1dukg";

    private final RsId ID_WITHOUT_DI = new RsId("tst02", "K4_pid", "uuid:123");
    private final RsId ID_WITH_ACTIVE_DI = new RsId("p01nk", "uuid", "113e83b0-1db2-11e2-bec6-005056827e51");
    private final RsId ID_WITH_DEACTIVATED_DI = new RsId("p01mzk", "uuid", "eff774d0-24a2-11e2-a6ce-5ef3fc9ae867");
    private final RsId ID_NO_DD = new RsId("tst02", "K4_pid", "uuid:123456789");
    private final RsId ID_DD_DEACTIVATED = new RsId("tst02", "K4_pid", "uuid:38bfc69d-1d10-4822-b7a4-c7531bb4aedl");

    // testing id type
    private final String ID_TYPE_REGISTRAR = "aba001";
    private final String ID_TYPE_VALUE = "something";
    private final RsId ID_TYPE_LENGTH_2 = new RsId(ID_TYPE_REGISTRAR, "aA", ID_TYPE_VALUE);
    private final RsId ID_TYPE_LENGTH_20 = new RsId(ID_TYPE_REGISTRAR, "aaaaaaaaa1AAAAAAAAA2", ID_TYPE_VALUE);
    private final RsId ID_TYPE_RESERVED_CHARS = new RsId(ID_TYPE_REGISTRAR, "Ab9:8cD", ID_TYPE_VALUE); // only ":" allowed from reserved
    private final RsId ID_TYPE_UNRESERVED_CHARS = new RsId(ID_TYPE_REGISTRAR, "aA9_-", ID_TYPE_VALUE);// 0-9, a-z, A-Z , "_", "-" allowed from
                                                                                                      // unreserved

    // testing id value
    private final String ID_VALUE_REGISTRAR = "aba001";
    private final String ID_VALUE_TYPE = "test";
    private final RsId ID_VALUE_LENGTH_1 = new RsId(ID_VALUE_REGISTRAR, ID_VALUE_TYPE, "a");
    private final RsId ID_VALUE_LENGTH_60 = new RsId(ID_VALUE_REGISTRAR, ID_VALUE_TYPE,
            "aaaaaaaaa1aaaaaaaaa2aaaaaaaaa3aaaaaaaaa4aaaaaaaaa5aaaaaaaaa6");
    // TODO: character '/' ignored for now until this bug is fixed: https://github.com/NLCR/CZIDLO/issues/129
    // private final Id ID_VALUE_RESERVED = new Id(ID_VALUE_REGISTRAR, ID_VALUE_TYPE, "!*'();:@&=+$,/?#[]");
    private final RsId ID_VALUE_RESERVED_CHARS = new RsId(ID_VALUE_REGISTRAR, ID_VALUE_TYPE, "!*'();:@&=+$,?#[]");
    private final RsId ID_VALUE_UNRESERVED_CHARS = new RsId(ID_VALUE_REGISTRAR, ID_VALUE_TYPE, "-_.~");

    private final RsId ID_TWO_RSI_1 = new RsId("aba001", "first", "something");
    private final RsId ID_TWO_RSI_2 = new RsId("aba001", "second", "somethingElse");
    private final RsId ID_TWO_RSI_3_NOT_EXISTING = new RsId("aba001", "third", "anotherOne");

    private final String URN_FOR_DELETE_RS_TESTS = "urn:nbn:cz:aba001-0005hy";

    @BeforeSuite
    public void beforeSuite() {
        init();
    }

    @Test
    public void resolveIdIdTypeToShort() {
        String xml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().get(buildResolvationPath(new RsId(ID_TYPE_REGISTRAR, "a", ID_TYPE_VALUE)))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        // TODO:APIv4: rename error to INVALID_ID_TYPE
        Assert.assertEquals(xmlPath.get("code"), "INVALID_DIGITAL_DOCUMENT_ID_TYPE");
    }

    @Test
    public void resolveIdIdTypeToLong() {
        String xml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().get(buildResolvationPath(new RsId(ID_TYPE_REGISTRAR, "aaaaaaaaa1aaaaaaaaa2a", ID_TYPE_VALUE)))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        // TODO:APIv4: rename error to INVALID_ID_TYPE
        Assert.assertEquals(xmlPath.get("code"), "INVALID_DIGITAL_DOCUMENT_ID_TYPE");
    }

    @Test
    public void resolveIdIdTypeSizeOk() {
        // length 2
        RsId id = ID_TYPE_LENGTH_2;
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildResolvationPath(id))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.digitalDocument");
        Assert.assertEquals(xmlPath.get("registrarScopeIdentifiers.id.find { it.@type == \'" + id.type + "\' }"), id.value);

        // length 20
        id = ID_TYPE_LENGTH_20;
        xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildResolvationPath(id))//
                .andReturn().asString();
        xmlPath = XmlPath.from(xml).setRoot("response.digitalDocument");
        Assert.assertEquals(xmlPath.get("registrarScopeIdentifiers.id.find { it.@type == \'" + id.type + "\' }"), id.value);
    }

    @Test
    public void resolveIdIdTypeValidCharactersReserved() {
        RsId id = ID_TYPE_RESERVED_CHARS;
        RsId idWithReservedCharsEncoded = new RsId(id.registrarCode, Utils.urlEncodeReservedChars(id.type), id.value);
        String xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildResolvationPath(idWithReservedCharsEncoded))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.digitalDocument");
        Assert.assertEquals(xmlPath.get("registrarScopeIdentifiers.id.find { it.@type == \'" + id.type + "\' }"), id.value);
    }

    @Test
    public void resolveIdIdTypeValidCharactersUnreserved() {
        RsId id = ID_TYPE_UNRESERVED_CHARS;
        // not url encoded
        String xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildResolvationPath(id))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.digitalDocument");
        Assert.assertEquals(xmlPath.get("registrarScopeIdentifiers.id.find { it.@type == \'" + id.type + "\' }"), id.value);
        String urn = xmlPath.getString("digitalDocument.urnNbn.value");
        // url encoded
        RsId idWithReservedAndUnreservedUrlEncoded = new RsId(id.registrarCode, Utils.urlEncodeAll(id.type), id.value);
        xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildResolvationPath(idWithReservedAndUnreservedUrlEncoded))//
                .andReturn().asString();
        xmlPath = XmlPath.from(xml).setRoot("response.digitalDocument");
        Assert.assertEquals(xmlPath.get("registrarScopeIdentifiers.id.find { it.@type == \'" + id.type + "\' }"), id.value);
        // urn:nbn same
        Assert.assertEquals(xmlPath.getString("digitalDocument.urnNbn.value"), urn);
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
        RsId id = new RsId(ID_TYPE_REGISTRAR, Utils.urlEncodeReservedChars("" + c + c), ID_TYPE_VALUE);
        String xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false) //
                .expect() //
                .statusCode(400) //
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString)) //
                .body(hasXPath("/c:response/c:error/c:code", nsContext)) //
                .body(hasXPath("/c:response/c:error/c:message", nsContext)) //
                .when().get(buildResolvationPath(id))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        // TODO:APIv4: rename error to INVALID_ID_TYPE
        Assert.assertEquals(xmlPath.get("code"), "INVALID_DIGITAL_DOCUMENT_ID_TYPE");
    }

    @Test
    public void resolveIdIdTypeInvalidCharactersUnreserved() {
        // only "~" not allowed (from unreserved character set)

        // not url-encoded
        RsId id = new RsId(ID_TYPE_REGISTRAR, "~~", ID_TYPE_VALUE);
        String xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false)//
                .expect()//
                .statusCode(400).contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().get(buildResolvationPath(id))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        // TODO:APIv4: rename error to INVALID_ID_TYPE
        Assert.assertEquals(xmlPath.get("code"), "INVALID_DIGITAL_DOCUMENT_ID_TYPE");

        // url-encoded
        id = new RsId(ID_TYPE_REGISTRAR, Utils.urlEncodeReservedChars("~~"), ID_TYPE_VALUE);
        xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false) //
                .expect() //
                .statusCode(400) //
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString)) //
                .body(hasXPath("/c:response/c:error/c:code", nsContext)) //
                .body(hasXPath("/c:response/c:error/c:message", nsContext)) //
                .when().get(buildResolvationPath(id))//
                .andReturn().asString();
        xmlPath = XmlPath.from(xml).setRoot("response.error");
        // TODO:APIv4: rename error to INVALID_ID_TYPE
        Assert.assertEquals(xmlPath.get("code"), "INVALID_DIGITAL_DOCUMENT_ID_TYPE");
    }

    @Test
    public void resolveIdIdValueToLong() {
        // APIv3 doesn't actually check this
        // TODO:APIv4: check the values for length, allowed chars
        // String xml = with().config(namespaceAwareXmlConfig()).expect()//
        // .statusCode(400)//
        // .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
        // .body(hasXPath("/c:response/c:error/c:code", nsContext))//
        // .body(hasXPath("/c:response/c:error/c:message", nsContext))//
        // .when().get(buildPath(new Id(ID_VALUE_REGISTRAR, ID_VALUE_TYPE, "aaaaaaaaa1aaaaaaaaa2aaaaaaaaa3aaaaaaaaa4aaaaaaaaa5aaaaaaaaa61")))//
        // .andReturn().asString();
        // XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        // // TODO:APIv4: create new error type INVALID_ID_VALUE
        // Assert.assertEquals(xmlPath.get("code"), "INVALID_ID_VALUE");
    }

    @Test
    public void resolveIdIdValueSizeOk() {
        // length 2
        RsId id = ID_VALUE_LENGTH_1;
        String xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildResolvationPath(id))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.digitalDocument");
        Assert.assertEquals(xmlPath.get("registrarScopeIdentifiers.id.find { it.@type == \'" + id.type + "\' }"), id.value);

        // length 20
        id = ID_VALUE_LENGTH_60;
        xml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildResolvationPath(id))//
                .andReturn().asString();
        xmlPath = XmlPath.from(xml).setRoot("response.digitalDocument");
        Assert.assertEquals(xmlPath.get("registrarScopeIdentifiers.id.find { it.@type == \'" + id.type + "\' }"), id.value);
    }

    @Test
    public void resolveIdIdValueValidCharactersReserved() {
        RsId id = ID_VALUE_RESERVED_CHARS;
        RsId idWithReservedCharsEncoded = new RsId(id.registrarCode, id.type, Utils.urlEncodeReservedChars(id.value));
        String xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildResolvationPath(idWithReservedCharsEncoded))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.digitalDocument");
        Assert.assertEquals(xmlPath.get("registrarScopeIdentifiers.id.find { it.@type == \'" + id.type + "\' }"), id.value);
    }

    @Test
    public void resolveIdIdValueValidCharactersUnreserved() {
        RsId id = ID_VALUE_UNRESERVED_CHARS;
        // not url encoded
        String xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildResolvationPath(id))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.digitalDocument");
        Assert.assertEquals(xmlPath.get("registrarScopeIdentifiers.id.find { it.@type == \'" + id.type + "\' }"), id.value);
        String urn = xmlPath.getString("digitalDocument.urnNbn.value");
        // url encoded
        RsId idWithReservedAndUnreservedUrlEncoded = new RsId(id.registrarCode, id.type, Utils.urlEncodeAll(id.value));
        xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().get(buildResolvationPath(idWithReservedAndUnreservedUrlEncoded))//
                .andReturn().asString();
        xmlPath = XmlPath.from(xml).setRoot("response.digitalDocument");
        Assert.assertEquals(xmlPath.get("registrarScopeIdentifiers.id.find { it.@type == \'" + id.type + "\' }"), id.value);
        // urn:nbn same
        Assert.assertEquals(xmlPath.getString("digitalDocument.urnNbn.value"), urn);
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

    @Test
    public void getRegistrarScopeIdentifiers() {
        RsId id = ID_TWO_RSI_1;
        String xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrarScopeIdentifiers/c:id", nsContext))//
                .when().get(buildResolvationPath(id) + "/registrarScopeIdentifiers")//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.registrarScopeIdentifiers");
        assertThat(xmlPath.getString("id.find { it.@type == \'" + id.type + "\' }"), equalTo(id.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + ID_TWO_RSI_2.type + "\' }"), equalTo(ID_TWO_RSI_2.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + ID_TWO_RSI_3_NOT_EXISTING.type + "\' }"), isEmptyOrNullString());
    }

    @Test
    public void deleteRegistrarScopeIdentifiersNotAuthenticated() {
        RsId id1 = new RsId("aba001", "deleteTest1", "something1");
        // String xml =
        with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false)//
                .expect()//
                .statusCode(401)//
                // .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                // .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                // .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(id1) + "/registrarScopeIdentifiers")//
                .andReturn().asString();
        // XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        // Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHENTICATED");
        // TODO:APIv4: return xml as well
    }

    @Test
    public void deleteRegistrarScopeIdentifiersNotAuthorized() {
        RsId id = new RsId("aba001", "deleteTest1", "something1");
        // delete all registrar-scope-identifiers
        deleteAllRegistrarScopeIdentifiers(URN_FOR_DELETE_RS_TESTS, USER_WITH_RIGHTS_LOGIN, USER_WITH_RIGHTS_PASSWORD);
        // insert id by urn:nbn
        insertRegistrarScopeId(URN_FOR_DELETE_RS_TESTS, id.type, id.value, USER_WITH_RIGHTS_LOGIN, USER_WITH_RIGHTS_PASSWORD);
        // delete all identifiers by registrarScopeId with not authorized user
        String xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).auth().basic(USER_NO_RIGHTS_LOGIN, USER_NO_RIGHTS_PASSWORD)//
                .expect()//
                .statusCode(401)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error/c:code", nsContext))//
                .body(hasXPath("/c:response/c:error/c:message", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(id) + "/registrarScopeIdentifiers")//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHORIZED");
        // cleanlup
        deleteAllRegistrarScopeIdentifiers(URN_FOR_DELETE_RS_TESTS, USER_WITH_RIGHTS_LOGIN, USER_WITH_RIGHTS_PASSWORD);
    }

    @Test
    public void deleteRegistrarScopeIdentifiers() {
        RsId id1 = new RsId("aba001", "deleteTest1", "something1");
        RsId id2 = new RsId("aba001", "deleteTest2", "something2");
        RsId id3 = new RsId("aba001", "deleteTest3", "something3");
        // delete all registrar-scope-identifiers
        deleteAllRegistrarScopeIdentifiers(URN_FOR_DELETE_RS_TESTS, USER_WITH_RIGHTS_LOGIN, USER_WITH_RIGHTS_PASSWORD);
        // insert id1, id2, id3
        insertRegistrarScopeId(URN_FOR_DELETE_RS_TESTS, id1.type, id1.value, USER_WITH_RIGHTS_LOGIN, USER_WITH_RIGHTS_PASSWORD);
        insertRegistrarScopeId(URN_FOR_DELETE_RS_TESTS, id2.type, id2.value, USER_WITH_RIGHTS_LOGIN, USER_WITH_RIGHTS_PASSWORD);
        insertRegistrarScopeId(URN_FOR_DELETE_RS_TESTS, id3.type, id3.value, USER_WITH_RIGHTS_LOGIN, USER_WITH_RIGHTS_PASSWORD);
        // delete all identifiers by registrarScopeId
        String xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).auth()
                .basic(USER_WITH_RIGHTS_LOGIN, USER_WITH_RIGHTS_PASSWORD)//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrarScopeIdentifiers", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(id1) + "/registrarScopeIdentifiers")//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.registrarScopeIdentifiers");
        assertThat(xmlPath.getString("id.find { it.@type == \'" + id1.type + "\' }"), equalTo(id1.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + id2.type + "\' }"), equalTo(id2.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + id3.type + "\' }"), equalTo(id3.value));
        // get all rsids by urn:nbn (should be empty)
        String url = HTTPS_API_URL + buildResolvationPath(Utils.urlEncodeReservedChars(URN_FOR_DELETE_RS_TESTS)) + "/registrarScopeIdentifiers";
        xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).auth().basic(USER_WITH_RIGHTS_LOGIN, USER_WITH_RIGHTS_PASSWORD)//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrarScopeIdentifiers", nsContext))//
                .when().get(url)//
                .andReturn().asString();
        xmlPath = XmlPath.from(xml).setRoot("response.registrarScopeIdentifiers");
        assertThat(xmlPath.getString("id"), isEmptyOrNullString());
    }

    @Test
    public void getRegistrarScopeIdentifier() {
        RsId id = ID_TWO_RSI_1;
        String xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().get(buildResolvationPath(id) + "/registrarScopeIdentifiers/" + ID_TWO_RSI_2.type)//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response");
        assertThat(xmlPath.getString("id.find { it.@type == \'" + ID_TWO_RSI_2.type + "\' }"), equalTo(ID_TWO_RSI_2.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + ID_TWO_RSI_1.type + "\' }"), isEmptyOrNullString());
    }

    @Test
    public void getRegistrarScopeIdentifierNotDefined() {
        String xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().get(buildResolvationPath(ID_TWO_RSI_1) + "/registrarScopeIdentifiers/" + ID_TWO_RSI_3_NOT_EXISTING.type)//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "NOT_DEFINED");
    }

    @Test
    public void putRegistrarScopeIdentifierNotAuthenticated() {
        // xml =
        with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false)//
                .body("newValue")//
                .expect()//
                .statusCode(401)//
                // .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                // .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(HTTPS_API_URL + buildResolvationPath(ID_TWO_RSI_1) + "/registrarScopeIdentifiers/" + ID_TWO_RSI_1.type)//
                .andReturn().asString();
        // XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        // Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHENTICATED");
        // TODO:APIv4: return xml as well
    }

    @Test
    public void putRegistrarScopeIdentifierNotAuthorized() {
        String xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).auth().basic(USER_NO_RIGHTS_LOGIN, USER_NO_RIGHTS_PASSWORD)//
                .body("newValue")//
                .expect()//
                .statusCode(401)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(HTTPS_API_URL + buildResolvationPath(ID_TWO_RSI_1) + "/registrarScopeIdentifiers/" + ID_TWO_RSI_1.type)//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHORIZED");
    }

    // @Test
    // public void putRegistrarScopeIdentifierInsert() {
    // String urn = URN_FOR_DELETE_RS_TESTS;
    //
    // String xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).auth().basic(USER_NO_RIGHTS_LOGIN, USER_NO_RIGHTS_PASSWORD)//
    // .body("newValue")//
    // .expect()//
    // .statusCode(401)//
    // .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
    // .body(hasXPath("/c:response/c:error", nsContext))//
    // .when().put(HTTPS_API_URL + buildResolvationPathByRegistrarScopeId(ID_TWO_RSI_1) + "/registrarScopeIdentifiers/" + ID_TWO_RSI_1.type)//
    // .andReturn().asString();
    // XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
    // Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHORIZED");
    // }

    @Test
    public void deleteRegistrarScopeIdentifier() {
        RsId id1 = new RsId("aba001", "deleteTest1", "something1");
        RsId id2 = new RsId("aba001", "deleteTest2", "something2");
        // delete all registrar-scope-identifiers
        deleteAllRegistrarScopeIdentifiers(URN_FOR_DELETE_RS_TESTS, USER_WITH_RIGHTS_LOGIN, USER_WITH_RIGHTS_PASSWORD);
        // insert id1 and id2
        insertRegistrarScopeId(URN_FOR_DELETE_RS_TESTS, id1.type, id1.value, USER_WITH_RIGHTS_LOGIN, USER_WITH_RIGHTS_PASSWORD);
        insertRegistrarScopeId(URN_FOR_DELETE_RS_TESTS, id2.type, id2.value, USER_WITH_RIGHTS_LOGIN, USER_WITH_RIGHTS_PASSWORD);
        // delete id 1
        String xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).auth()
                .basic(USER_WITH_RIGHTS_LOGIN, USER_WITH_RIGHTS_PASSWORD)//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(id1) + "/registrarScopeIdentifiers/" + id1.type)//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response");
        assertThat(xmlPath.getString("id.find { it.@type == \'" + id1.type + "\' }"), equalTo(id1.value));
        assertThat(xmlPath.getString("id.find { it.@type == \'" + id2.type + "\' }"), isEmptyOrNullString());
        // get all rsids by urn:nbn (should contain only id 2)
        String url = HTTPS_API_URL + buildResolvationPath(Utils.urlEncodeReservedChars(URN_FOR_DELETE_RS_TESTS)) + "/registrarScopeIdentifiers";
        xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).auth().basic(USER_WITH_RIGHTS_LOGIN, USER_WITH_RIGHTS_PASSWORD)//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrarScopeIdentifiers", nsContext))//
                .when().get(url)//
                .andReturn().asString();
        xmlPath = XmlPath.from(xml).setRoot("response.registrarScopeIdentifiers");
        assertThat(xmlPath.getString("id.find { it.@type == \'" + id1.type + "\' }"), isEmptyOrNullString());
        assertThat(xmlPath.getString("id.find { it.@type == \'" + id2.type + "\' }"), equalTo(id2.value));
        // cleanlup
        deleteAllRegistrarScopeIdentifiers(URN_FOR_DELETE_RS_TESTS, USER_WITH_RIGHTS_LOGIN, USER_WITH_RIGHTS_PASSWORD);
    }

    @Test
    public void deleteRegistrarScopeIdentifierNotAuthenticated() {
        RsId id = new RsId("aba001", "deleteTest1", "something1");
        // delete all registrar-scope-identifiers
        deleteAllRegistrarScopeIdentifiers(URN_FOR_DELETE_RS_TESTS, USER_WITH_RIGHTS_LOGIN, USER_WITH_RIGHTS_PASSWORD);
        // insert id
        insertRegistrarScopeId(URN_FOR_DELETE_RS_TESTS, id.type, id.value, USER_WITH_RIGHTS_LOGIN, USER_WITH_RIGHTS_PASSWORD);
        // delete id without credentials
        // xml =
        with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false)//
                .expect()//
                .statusCode(401)//
                // .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                // .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(id) + "/registrarScopeIdentifiers/" + id.type)//
                .andReturn().asString();
        // XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        // Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHENTICATED");
        // TODO:APIv4: return xml as well
        // cleanlup
        deleteAllRegistrarScopeIdentifiers(URN_FOR_DELETE_RS_TESTS, USER_WITH_RIGHTS_LOGIN, USER_WITH_RIGHTS_PASSWORD);
    }

    @Test
    public void deleteRegistrarScopeIdentifierNotAuthorized() {
        RsId id = new RsId("aba001", "deleteTest1", "something1");
        // delete all registrar-scope-identifiers
        deleteAllRegistrarScopeIdentifiers(URN_FOR_DELETE_RS_TESTS, USER_WITH_RIGHTS_LOGIN, USER_WITH_RIGHTS_PASSWORD);
        // insert id
        insertRegistrarScopeId(URN_FOR_DELETE_RS_TESTS, id.type, id.value, USER_WITH_RIGHTS_LOGIN, USER_WITH_RIGHTS_PASSWORD);
        // try and delete id with wrong credentials
        String xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).auth().basic(USER_NO_RIGHTS_LOGIN, USER_NO_RIGHTS_PASSWORD)//
                .expect()//
                .statusCode(401)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(id) + "/registrarScopeIdentifiers/" + id.type)//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHORIZED");
        // cleanlup
        deleteAllRegistrarScopeIdentifiers(URN_FOR_DELETE_RS_TESTS, USER_WITH_RIGHTS_LOGIN, USER_WITH_RIGHTS_PASSWORD);
    }

    @Test
    public void deleteRegistrarScopeIdentifierInvalidRsId() {
        RsId id1 = new RsId("aba001", "deleteTest1", "something1");
        RsId id2 = new RsId("aba001", "deleteTest2", "something2");
        // delete all registrar-scope-identifiers
        deleteAllRegistrarScopeIdentifiers(URN_FOR_DELETE_RS_TESTS, USER_WITH_RIGHTS_LOGIN, USER_WITH_RIGHTS_PASSWORD);
        // insert id1
        insertRegistrarScopeId(URN_FOR_DELETE_RS_TESTS, id1.type, id1.value, USER_WITH_RIGHTS_LOGIN, USER_WITH_RIGHTS_PASSWORD);
        // try and delete id2 without id2 being present
        String xml = with().config(namespaceAwareXmlConfig()).urlEncodingEnabled(false).auth()
                .basic(USER_WITH_RIGHTS_LOGIN, USER_WITH_RIGHTS_PASSWORD)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().delete(HTTPS_API_URL + buildResolvationPath(id1) + "/registrarScopeIdentifiers/" + id2.type)//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_REGISTRAR_SCOPE_IDENTIFIER");
        // cleanlup
        deleteAllRegistrarScopeIdentifiers(URN_FOR_DELETE_RS_TESTS, USER_WITH_RIGHTS_LOGIN, USER_WITH_RIGHTS_PASSWORD);
    }

}
