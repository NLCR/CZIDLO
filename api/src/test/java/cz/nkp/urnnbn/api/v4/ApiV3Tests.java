package cz.nkp.urnnbn.api.v4;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.config.RestAssuredConfig.newConfig;
import static com.jayway.restassured.config.SSLConfig.sslConfig;
import static com.jayway.restassured.config.XmlConfig.xmlConfig;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static com.jayway.restassured.path.xml.config.XmlPathConfig.xmlPathConfig;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.xml.namespace.NamespaceContext;

import org.joda.time.DateTime;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.RedirectConfig;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;
import com.jayway.restassured.path.xml.config.XmlPathConfig;
import com.jayway.restassured.response.Response;

import cz.nkp.urnnbn.api.Utils;
import cz.nkp.urnnbn.api.pojo.Credentials;
import cz.nkp.urnnbn.api.pojo.Predecessor;
import cz.nkp.urnnbn.api.pojo.RsId;
import cz.nkp.urnnbn.api.pojo.Successor;
import cz.nkp.urnnbn.api.pojo.UrnNbnReservations;
import cz.nkp.urnnbn.api.v4.xml.DigDocRegistrationXmlBuilder;
import cz.nkp.urnnbn.api.v4.xml.DigInstImportXmlBuilder;
import cz.nkp.urnnbn.core.CountryCode;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.UrnNbn;

public abstract class ApiV3Tests {

    private final String LANG_CODE = "cz";
    private final String BASE_URI = "http://localhost";
    private final int PORT = 8080;
    private final String BASE_PATH = "/api/v4";
    final String HTTPS_API_URL = "https://localhost:8443" + BASE_PATH;

    final String REGISTRAR = "tst01"; // must exist, have at least 1 digital library and all 3 registration modes enabled
    final String REGISTRAR2 = "tst02";// must exist and have at least 1 digital library
    final String REGISTRAR_NO_MODES_ENABLED = "tst03";// must exist and and have all 3 registration modes disabled
    final Long ARCHIVER = 134l; // must existe and not be registrar
    final Credentials USER = new Credentials("api_test_user", "0aFukuC"); // must exist and have access rights to REGISTRAR, REGISTRAR2
    final Credentials USER_NO_RIGHTS = new Credentials("nobody", "skgo1dukg");// must exist and not have access rights to REGISTRAR, REGISTRAR2
    final String WORKING_URL = "https://www.seznam.cz/";
    final int UNKNOWN_DIG_LIB_DI = -1; // digital library with this id must not exist

    private final String NAMESPACE = "http://resolver.nkp.cz/v4/";
    private final String NAMESPACE_PREFIX = "c";
    private final String RESPONSE_XSD = "http://localhost:8080/api/v4/response.xsd";
    private final String IMPORT_DI_XSD = "http://localhost:8080/api/v4/digInstImport.xsd";
    private final String REGISTER_DI_XSD = "http://localhost:8080/api/v4/digDocRegistration.xsd";

    static int MAX_URN_NBN_RESERVATIONS_RETURNED = 30;// in api.properties (api.getReseravations.maxReservedToPrint)

    // examples of valid and invalid registrar codes according to regexp ^[a-zA-Z0-9]{2,6}$
    // all valid codes must not identify existing registrar in database
    final String[] REGISTRAR_CODES_VALID = new String[] { "ab", "AB", "01", "aB0", "abcdef", "ABCDEF", "012345", "aB0cD1" };
    final String[] REGISTRAR_CODES_INVALID = new String[] {//
            // incorrect length
            "a", "A", "0",
            "aaaaaa7",//
            ":", "?", "#", "[", "]", "@", "!", "$", "&", "'", "(", ")", "*", "+", ",", ";", "=", "-", ".", "_",
            "~",//
            // starts with invalid character
            ":x", "?x", "#x", "[x", "]x", "@x", "!x", "$x", "&x", "'x", "(x", ")x", "*x", "+x", ",x", ";x", "=x", "-x", ".x", "_x",
            "~x", //
            // ends with invalid character
            "x:", "x?", "x#", "x[", "x]", "x@", "x!", "x$", "x&", "x'", "x(", "x)", "x*", "x+", "x,", "x;", "x=", "x-", "x.", "x_",
            "x~",//
            // contains invalid character
            "x:x"/* , "x/x" */, "x?x", "x#x", "x[x", "x]x", "x@x", "x!x", "x$x", "x&x", "x'x", "x(x", "x)x", "x*x", "x+x", "x,x", "x;x", "x=x",
            "x-x", "x.x", "x_x", "x~x" };

    // registrar-scope-id valid/invalid type examples according to regex ^[a-zA-Z0-9]{1}[a-zA-Z0-9_:\\-]{0,18}[a-zA-Z0-9]{1}$
    final String[] RSID_TYPES_VALID = new String[] { "aa", "AA", "00", "aaaaaaaaaaaaaaaaaaaa", "AAAAAAAAAAAAAAAAAAAA", "01234567890123456789", //
            "aB0:0Ba", "aB0_0Ba", "aB0-0Ba" };
    final String[] RSID_TYPES_INVALID = new String[] {
            // incorrect length
            "a", "A", "0", "aaaaaaaaa1aaaaaaaaa2X",//
            // starts with valid character
            ":x", "_x", "-x",//
            // ends with valid character
            "x:", "x_", "x-",//
            // contains invalid character
            "x?x", "x#x", "x[x", "x]x", "x@x", "x!x", "x$x", "x&x", "x'x", "x(x", "x)x", "x*x", "x+x", "x,x", "x;x", "x=x", "x.x", "x~x" };

    // registrar-scope-id valid/invalid value examples according to regex
    // ^[a-zA-Z0-9]{1}[a-zA-Z0-9:\\?#\\[\\]@!\\$&'\\(\\)\\*\\+,;=\\-\\._~]{0,58}[a-zA-Z0-9]{1}$
    final String[] RSID_VALUES_VALID = new String[] { "a", "A", "0", "aA0",
            "aaaaaaaaa1aaaaaaaaa2aaaaaaaaa3aaaaaaaaa4aaaaaaaaa5aaaaaaaaa6",//
            // contains valid character
            "x:x", "x?x", "x#x", "x[x", "x]x", "x@x", "x!x", "x$x", "x&x", "x'x", "x(x", "x)x", "x*x", "x+x", "x,x", "x;x", "x=x", "x-x", "x.x",
            "x_x", "x~x" };

    final String[] RSID_VALUES_INVALID = new String[] {
            // to long
            "aaaaaaaaa1bbbbbbbbb2ccccccccc3ddddddddd4eeeeeeeee5eeeeeeeee6x",//
            // starts with valid character
            ":x", "?x", "#x", "[x", "]x", "@x", "!x", "$x", "&x", "'x", "(x", ")x", "*x", "+x", ",x", ";x", "=x", "-x", ".x", "_x", "~x",//
            // ends with valid character
            "x:", "x?", "x#", "x[", "x]", "x@", "x!", "x$", "x&", "x'", "x(", "x)", "x*", "x+", "x,", "x;", "x=", "x-", "x.", "x_", "x~" };

    // urn:nbn valid/invalid examples according to regex urn:nbn:cz:[a-zA-z0-9]{2,6}\\-[a-zA-Z0-9]{6}
    final String[] URNNBN_VALID = new String[] { "urn:nbn:cz:ab-aaaaaa", "urn:NBN:cZ:00-012345", "URN:NBN:CZ:AB-AAAAAA", "urn:nbn:cz:1aABb2-1A2a3b",
            "urn:NBN:Cz:ABCDEF-1A2a3b", "URN:NBN:cz:123456-1A2a3b" };
    final String[] URNNBN_INVALID = new String[] { "cz:abc012-123456", "urn:isbn:cz:abc012-123456", "nbn:cz:abc012-123456",
            "urn:nbn:cs:abc012-123456", "urn:nbn:abc012-123456", "urn:nbn:cz:a-123456", "urn:nbn:cz:abc123X-123456", "urn:nbn:cz:abc012-12345",
            "urn:nbn:cz:abc012-1234567" };

    Random rand = new Random();
    NamespaceContext nsContext;
    String responseXsdString;
    String importDiXsdString;
    String registerDdXsdString;
    DigInstImportXmlBuilder diImportBuilder = new DigInstImportXmlBuilder(NAMESPACE);
    DigDocRegistrationXmlBuilder ddRegistrationBuilder = new DigDocRegistrationXmlBuilder(NAMESPACE);

    void init() {
        CountryCode.initialize(LANG_CODE);
        RestAssured.baseURI = BASE_URI;
        RestAssured.port = PORT;
        RestAssured.basePath = BASE_PATH;
        // doesn't work, must use .relaxedHTTPSValidation() in each request
        // see https://github.com/jayway/rest-assured/issues/561
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.urlEncodingEnabled = false;
        nsContext = Utils.buildNsContext("c", NAMESPACE);
        responseXsdString = Utils.readXsd(RESPONSE_XSD);
        importDiXsdString = Utils.readXsd(IMPORT_DI_XSD);
        registerDdXsdString = Utils.readXsd(REGISTER_DI_XSD);
        // XmlConfig.xmlConfig().namespaceAware(true).declareNamespace(RESPONSE_NS_PREFIX, RESPONSE_NS);
    }

    RestAssuredConfig namespaceAwareXmlConfig() {
        return newConfig().xmlConfig(xmlConfig().with().namespaceAware(true).declareNamespace(NAMESPACE_PREFIX, NAMESPACE))//
                .sslConfig(sslConfig().relaxedHTTPSValidation())//
                // https + redirections doesn't work
                // see https://github.com/jayway/rest-assured/issues/467
                .redirect(new RedirectConfig(true, false, false, 10))//
        ;
    }

    XmlPathConfig namespaceAwareXmlpathConfig() {
        return xmlPathConfig().declaredNamespace(NAMESPACE_PREFIX, NAMESPACE);
    }

    String buildResolvationPath(RsId id) {
        return "/registrars/" + Utils.urlEncodeReservedChars(id.registrarCode) + "/digitalDocuments/registrarScopeIdentifier/"//
                + Utils.urlEncodeReservedChars(id.type) + "/" + Utils.urlEncodeReservedChars(id.value);
    }

    String buildResolvationPath(String urnNbn) {
        return "/resolver/" + Utils.urlEncodeReservedChars(urnNbn);
    }

    String getRandomExistingRegistrarCode() {
        String responseXml = with().config(namespaceAwareXmlConfig()).when().get("/registrars").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml);
        int registrarsCount = xmlPath.getInt("response.registrars.registrar.size()");
        if (registrarsCount != 0) {
            int registrarPosition = rand.nextInt(registrarsCount);
            String registrarCode = xmlPath.getString("response.registrars.registrar[" + registrarPosition + "].@code");
            return registrarCode;
        } else {
            return null;
        }
    }

    List<String> getAllRegistrarCodes() {
        String responseXml = with().config(namespaceAwareXmlConfig()).when().get("/registrars").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml);
        int registrarsCount = xmlPath.getInt("response.registrars.registrar.size()");
        List<String> result = new ArrayList<String>(registrarsCount);
        for (int i = 0; i < registrarsCount; i++) {
            String registrarCode = xmlPath.getString("response.registrars.registrar[" + i + "].@code");
            result.add(registrarCode);
        }
        return result;
    }

    List<String> reserveUrnNbns(String registrarCode, Credentials credentials) {
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(credentials.login, credentials.password)//
                .expect()//
                .statusCode(201)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbnReservation/c:urnNbn", nsContext))//
                .when().post(HTTPS_API_URL + "/registrars/" + Utils.urlEncodeReservedChars(registrarCode) + "/urnNbnReservations")//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbnReservation");
        int nowReserved = xmlPath.getInt("urnNbn.size()");
        List<String> result = new ArrayList<>(nowReserved);
        for (int i = 0; i < nowReserved; i++) {
            result.add(xmlPath.getString("urnNbn[" + i + "]"));
        }
        return result;
    }

    void deleteAllRegistrarScopeIdentifiers(String urnNbn, Credentials credentials) {
        String url = HTTPS_API_URL + buildResolvationPath(urnNbn) + "/registrarScopeIdentifiers";
        with().config(namespaceAwareXmlConfig()).auth().basic(credentials.login, credentials.password) //
                .expect() //
                .statusCode(200) //
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString)) //
                .body(hasXPath("/c:response/c:registrarScopeIdentifiers", nsContext)) //
                .when().delete(url);
    }

    void deleteRegistrarScopeId(String urnNbn, RsId id, Credentials credentials) {
        String url = HTTPS_API_URL + buildResolvationPath(urnNbn) + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(id.type);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(credentials.login, credentials.password)//
                .body(id.value).expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().delete(url)//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        assertThat(xmlPath.getString("id"), equalTo(id.value));
        assertThat(xmlPath.getString("id.@type"), equalTo(id.type));
    }

    void insertRegistrarScopeId(String urnNbn, RsId id, Credentials credentials) {
        String url = HTTPS_API_URL + buildResolvationPath(urnNbn) + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(id.type);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(credentials.login, credentials.password)//
                .body(id.value).expect()//
                .statusCode(201)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().put(url)//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        assertThat(xmlPath.getString("id"), equalTo(id.value));
        assertThat(xmlPath.getString("id.@type"), equalTo(id.type));
    }

    List<RsId> getRsIds(String urnNbn) {
        String url = buildResolvationPath(urnNbn) + "/registrarScopeIdentifiers";
        String responseXml = with().config(namespaceAwareXmlConfig()).queryParam("action", "show").queryParam("format", "xml")//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrarScopeIdentifiers", nsContext))//
                .when().get(url)//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.registrarScopeIdentifiers");
        int size = xmlPath.getInt("id.size()");
        List<RsId> result = new ArrayList<>(size);
        String registrarCode = UrnNbn.valueOf(urnNbn).getRegistrarCode().toString();
        for (int i = 0; i < size; i++) {
            String type = xmlPath.getString(String.format("id[%d].@type", i));
            String value = xmlPath.getString(String.format("id[%d]", i));
            result.add(new RsId(registrarCode, type, value));
        }
        return result;
    }

    UrnNbnReservations getUrnNbnReservations(String registrarCode) {
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .body(hasXPath("/c:response/c:urnNbnReservations/c:maxReservationSize", nsContext))//
                .body(hasXPath("/c:response/c:urnNbnReservations/c:defaultReservationSize", nsContext))//
                .body(hasXPath("/c:response/c:urnNbnReservations/c:reserved", nsContext))//
                .body(hasXPath("/c:response/c:urnNbnReservations/c:reserved/@totalSize", nsContext))//
                .when().get("/registrars/" + registrarCode + "/urnNbnReservations")//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbnReservations");
        // reservation size
        int maxReservationSize = xmlPath.getInt("maxReservationSize");
        int defaultReservationSize = xmlPath.getInt("defaultReservationSize");
        assertThat(maxReservationSize, greaterThanOrEqualTo(0));
        assertThat(defaultReservationSize, lessThanOrEqualTo(maxReservationSize));
        // reservations total/returned
        int reservedTotal = xmlPath.getInt("reserved.@totalSize");
        int reservedReturned = xmlPath.getInt("reserved.urnNbn.size()");
        assertThat(reservedTotal, greaterThanOrEqualTo(reservedReturned));
        assertThat(reservedReturned, lessThanOrEqualTo(reservedTotal));
        assertThat(reservedReturned, lessThanOrEqualTo(MAX_URN_NBN_RESERVATIONS_RETURNED));
        // to be returned
        List<String> reservedReturnedUrns = new ArrayList<>(reservedReturned);
        for (int i = 0; i < reservedReturned; i++) {
            reservedReturnedUrns.add(xmlPath.getString(String.format("reserved.urnNbn[%d]", i)));
        }
        return new UrnNbnReservations(maxReservationSize, defaultReservationSize, reservedTotal, reservedReturnedUrns);
    }

    DigitalInstance getDigitalInstanceOrNull(long diId) {
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response", nsContext))//
                .when().get("/digitalInstances/id/" + diId).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        if (xmlPath.get("digitalInstance") != null) {
            DigitalInstance result = new DigitalInstance();
            result.setId(diId);
            result.setUrl(xmlPath.getString("digitalInstance.url"));
            result.setActive(Utils.booleanValue(xmlPath.getString("digitalInstance.@active")));
            result.setCreated(DateTime.parse(xmlPath.getString("digitalInstance.created")));
            if (!result.isActive()) {
                result.setDeactivated(DateTime.parse(xmlPath.getString("digitalInstance.deactivated")));
            }
            return result;
        } else {
            return null;
        }
    }

    Long getDigitalLibraryIdOrNull(String registrarCode) {
        String responseXml = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrar", nsContext))//
                .when().get("/registrars/" + Utils.urlEncodeReservedChars(registrarCode)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.registrar");
        String digLibId = xmlPath.getString("digitalLibraries.digitalLibrary[0].@id");
        try {
            return Long.valueOf(digLibId);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    String registerUrnNbn(String registrarCode, Credentials credentials) {
        String bodyXml = ddRegistrationBuilder.minimal();
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(credentials.login, credentials.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)// .body(matchesXsd(registerDdXsdString))//
                .expect()//
                .statusCode(201)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbn", nsContext))//
                .when().post(HTTPS_API_URL + "/registrars/" + registrarCode + "/digitalDocuments")//
                .andReturn().asString();
        System.err.println(responseXml);
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbn");
        return xmlPath.getString("value");
    }

    void registerUrnNbn(String registrarCode, String urnNbn, Credentials credentials) {
        String bodyXml = ddRegistrationBuilder.withUrnNbn(urnNbn);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(credentials.login, credentials.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)// .body(matchesXsd(registerDdXsdString))//
                .expect()//
                // .statusCode(201)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                // .body(hasXPath("/c:response/c:urnNbn", nsContext))//
                .when().post(HTTPS_API_URL + "/registrars/" + registrarCode + "/digitalDocuments")//
                .andReturn().asString();
        // XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbn");
    }

    long insertDigitalInstance(String urnNbn, long digLibId, String diUrl, Credentials credentials) {
        String bodyXml = diImportBuilder.minimal(digLibId, diUrl);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(credentials.login, credentials.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)// .body(matchesXsd(importDiXsdString))//
                .expect()//
                .statusCode(201)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalInstance", nsContext))//
                .when().post(HTTPS_API_URL + buildResolvationPath(urnNbn) + "/digitalInstances")//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.digitalInstance");
        assertEquals(true, Utils.booleanValue(xmlPath.getString("@active")));
        return xmlPath.getLong("@id");
    }

    void deactivateDigitalInstance(long id, Credentials credentials) {
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(credentials.login, credentials.password)//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalInstance", nsContext))//
                .when().delete(HTTPS_API_URL + "/digitalInstances/id/" + id)//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.digitalInstance");
        assertEquals(false, Utils.booleanValue(xmlPath.getString("@active")));
    }

    void deactivateUrnNbn(String urnNbn, Credentials credentials) {
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(credentials.login, credentials.password)//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbn", nsContext))//
                .when().delete(HTTPS_API_URL + "/urnnbn/" + urnNbn)//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbn");
        assertEquals(urnNbn, xmlPath.getString("value"));
        assertEquals("DEACTIVATED", xmlPath.getString("status"));
    }

    DateTime getUrnNbnRegisteredOrNull(String urnNbn) {
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbn", nsContext))//
                .when().get("/urnnbn/" + Utils.urlEncodeReservedChars(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbn");
        String registeredStr = xmlPath.getString("registered");
        if (registeredStr == null || registeredStr.isEmpty()) {
            return null;
        } else {
            return DateTime.parse(registeredStr);
        }
    }

    String getUrnNbnStatus(String urnNbn) {
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbn", nsContext))//
                .when().get("/urnnbn/" + Utils.urlEncodeReservedChars(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbn");
        return xmlPath.getString("status");
    }

    String getReservedUrnNbn(String registrarCode, Credentials credentials) {
        UrnNbnReservations urnNbnReservations = getUrnNbnReservations(registrarCode);
        if (!urnNbnReservations.reservedOffered.isEmpty()) {
            return urnNbnReservations.reservedOffered.get(0);
        } else {
            return reserveUrnNbns(registrarCode, USER).get(0);
        }
    }

    Long getRegistrarId(String registrarCode) {
        String responseXml = with().config(namespaceAwareXmlConfig()).expect()//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:registrar", nsContext))//
                .when().get("/registrars/" + Utils.urlEncodeReservedChars(registrarCode))//
                .andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.registrar");
        return xmlPath.getLong("@id");
    }

    String getRandomFreeUrnNbnOrNull(String registrarCode) {
        int trials = 10;
        while (trials-- >= 1) {
            String urnNbn = "urn:nbn:" + CountryCode.getCode() + ":" + registrarCode + "-" + Utils.generateRandomDocumentCode();
            String state = getUrnNbnStatus(urnNbn);
            if ("FREE".equals(state)) {
                return urnNbn;
            }
        }
        return null;
    }

    Long getRandomFreeDigitalInstanceIdOrNull() {
        int trials = 10;
        while (trials-- >= 1) {
            int id = rand.nextInt();
            Response response = with().config(namespaceAwareXmlConfig())//
                    .expect()//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .when().get("/digitalInstances/id/" + id)//
                    .andReturn();
            if (response.getStatusCode() == 404) {
                return Long.valueOf(id);
            }
        }
        return null;
    }

    List<Predecessor> asList(Predecessor... array) {
        List<Predecessor> result = new ArrayList<Predecessor>(array.length);
        for (int i = 0; i < array.length; i++) {
            result.add(array[i]);
        }
        return result;
    }

    List<RsId> asList(RsId... array) {
        List<RsId> result = new ArrayList<RsId>(array.length);
        for (int i = 0; i < array.length; i++) {
            result.add(array[i]);
        }
        return result;
    }

    void assertHasPredecessors(String urnNbn, List<Predecessor> predecessors) {
        String responseXml = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbn", nsContext))//
                .when().get("/urnnbn/" + urnNbn).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbn");
        assertEquals(predecessors.size(), xmlPath.getInt("predecessor.size()"));
        for (int i = 0; i < predecessors.size(); i++) {
            String foundUrn = xmlPath.getString(String.format("predecessor[%d].@value", i));
            String foundNote = xmlPath.getString(String.format("predecessor[%d].@note", i));
            Predecessor predecessor = null;
            for (Predecessor p : predecessors) {
                if (foundUrn.equals(p.urnNbn)) {
                    predecessor = p;
                    break;
                }
            }
            // TODO: no need after upgrading rest-assured (current version 2.8)
            // see see https://github.com/jayway/rest-assured/issues/650
            if ("[]".equals(foundNote)) {
                assertTrue(predecessor.note == null);
            } else {
                assertEquals(predecessor.note.trim(), foundNote);
            }
        }
    }

    void assertHasSuccessors(String urnNbn, List<Successor> successors) {
        String responseXml = with().config(namespaceAwareXmlConfig())//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:urnNbn", nsContext))//
                .when().get("/urnnbn/" + urnNbn).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.urnNbn");
        assertEquals(successors.size(), xmlPath.getInt("successor.size()"));
        for (int i = 0; i < successors.size(); i++) {
            String foundUrn = xmlPath.getString(String.format("successor[%d].@value", i));
            String foundNote = xmlPath.getString(String.format("successor[%d].@note", i));
            Successor successor = null;
            for (Successor s : successors) {
                if (foundUrn.equals(s.urnNbn)) {
                    successor = s;
                    break;
                }
            }
            // TODO: no need this after upgrading rest-assured (current version 2.8)
            // see see https://github.com/jayway/rest-assured/issues/650
            if ("[]".equals(foundNote)) {
                assertTrue(successor.note == null);
            } else {
                assertEquals(successor.note, foundNote);
            }
        }
    }

    Map<String, List<Successor>> toSuccessors(String urnNbn, List<Predecessor> predecessors) {
        Map<String, List<Successor>> result = new HashMap<>();
        for (Predecessor p : predecessors) {
            Successor s = new Successor(urnNbn, p.note);
            List<Successor> list = new ArrayList<>();
            list.add(s);
            result.put(p.urnNbn, list);
        }
        return result;
    }

}
