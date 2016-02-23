package cz.nkp.urnnbn.api.v3;

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
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.namespace.NamespaceContext;

import org.joda.time.DateTime;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.RedirectConfig;
import com.jayway.restassured.config.RestAssuredConfig;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;
import com.jayway.restassured.path.xml.config.XmlPathConfig;

import cz.nkp.urnnbn.api.Utils;
import cz.nkp.urnnbn.core.CountryCode;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.UrnNbn;

public abstract class ApiV3Tests {

    static class RsId {
        final String registrarCode;
        final String type;
        final String value;

        public RsId(String registrarCode, String type, String value) {
            this.registrarCode = registrarCode;
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString() {
            return "RsId [registrarCode=" + registrarCode + ", type=" + type + ", value=" + value + "]";
        }

    }

    static class Credentials {
        final String login;
        final String password;

        public Credentials(String login, String password) {
            this.login = login;
            this.password = password;
        }
    }

    static class UrnNbnReservations {
        final int maxReservationSize;
        final int defaultReservationSize;
        final int totalReserved;
        final List<String> reservedOffered;

        public UrnNbnReservations(int maxReservationSize, int defaultReservationSize, int totalReserved, List<String> reservedOffered) {
            this.maxReservationSize = maxReservationSize;
            this.defaultReservationSize = defaultReservationSize;
            this.totalReserved = totalReserved;
            this.reservedOffered = reservedOffered;
        }
    }

    private final String LANG_CODE = "cz";
    private final String BASE_URI = "http://localhost";
    private final int PORT = 8080;
    private final String BASE_PATH = "/api/v3";
    final String HTTPS_API_URL = "https://localhost:8443" + BASE_PATH;

    private final String RESPONSE_NS = "http://resolver.nkp.cz/v3/";
    private final String RESPONSE_NS_PREFIX = "c";
    private final String RESPONSE_XSD = "http://localhost:8080/api/v3/response.xsd";

    static int MAX_URN_NBN_RESERVATIONS_RETURNED = 30;// in api.properties (api.getReseravations.maxReservedToPrint)

    // registrar-scope-id valid/invalid types
    // [A-Za-z0-9_\-:]{2,20}
    final String[] RSID_TYPES_VALID = new String[] { "aa", "AA", "00", "::", "__", "--", "aaaaaaaaaaaaaaaaaaaa", "AAAAAAAAAAAAAAAAAAAA",
            "01234567890123456789", ":::::::::::::::::::", "____________________", "-------------------" };
    // TODO: character '/' ignored until fixed: https://github.com/NLCR/CZIDLO/issues/129
    final String[] RSID_TYPES_INVALID = new String[] { "a", "A", "0", "aaaaaaaaaaaaaaaaaaaaa", "AAAAAAAAAAAAAAAAAAAAA", "012345678901234567890",
            "!!", "**", "''", "((", "))", ";;", "@@", "&&", "==", "++", "$$", ",,"/* , "//" */, "??", "##", "[[", "]]", "a!a", "a*a", "a'a", "a(a",
            "a)a", "a;a", "a@a", "a&a", "a=a", "a+a", "a$a", "a,a"/* , "a/a" */, "a?a", "a#a", "a[a", "a]a", "..", "~~", "a.a", "a~a" };

    // registrar-scope-id valid/invalid values
    // [A-Za-z0-9\-_\.~!\*'\(\);:@&=+$,/\?#\[\]]{1,60}
    final String[] RSID_VALUES_VALID = new String[] { "a", "A", "0", "!", "*", "'", "(", ")", ";", ":", "@", "&", "=", "+", "$", "," /* , "/" */,
            "?", "#", "[", "]", "-", "_", ".", "~", "aaaaaaaaa1aaaaaaaaa2aaaaaaaaa3aaaaaaaaa4aaaaaaaaa5aaaaaaaaa6" };
    final String[] RSID_VALUES_INVALID = new String[] { "aaaaaaaaaabbbbbbbbbbccccccccccddddddddddeeeeeeeeeeffffffffffg",
            "AAAAAAAAAABBBBBBBBBBCCCCCCCCCCDDDDDDDDDDEEEEEEEEEEFFFFFFFFFFG", "0000000000111111111122222222223333333333444444444455555555556" };

    // registrar-code valid and invalid values
    // [A-Za-z0-9]{2,6}
    final String[] REGISTRAR_CODES_VALID = new String[] { "ab", "AB", "01", "aB0", "abcdef", "ABCDEF", "012345", "aB0cD1" };
    // TODO: character '/' ignored until fixed: https://github.com/NLCR/CZIDLO/issues/129
    // TODO: character '_' ignored until fixed: https://github.com/NLCR/CZIDLO/issues/133
    final String[] REGISTRAR_CODES_INVALID = new String[] { "!!", "**", "''", "((", "))", ";;", "::", "@@", "&&", "==", "++", "$$",
            ",," /* , "//" */, "??", "##", "[[", "]]", "--",/* "__", */"..", "~~", "a!a", "a*a", "a'a", "a(a", "a)a", "a;a", "a:a", "a@a", "a&a",
            "a=a", "a+a", "a$a", "a,a" /* , "a/a" */, "a?a", "a#a", "a[a", "a]a", "a-a"/* , "a_a" */, "a.a", "a~a", "a", "A", "0", "aaaaaaa",
            "AAAAAAA", "0000000" };

    Random rand = new Random();
    String responseXsdString;
    NamespaceContext nsContext;

    void init() {
        CountryCode.initialize(LANG_CODE);
        RestAssured.baseURI = BASE_URI;
        RestAssured.port = PORT;
        RestAssured.basePath = BASE_PATH;
        // doesn't work, must use .relaxedHTTPSValidation() in each request
        // see https://github.com/jayway/rest-assured/issues/561
        RestAssured.useRelaxedHTTPSValidation();
        RestAssured.urlEncodingEnabled = false;
        responseXsdString = Utils.readXsd(RESPONSE_XSD);
        nsContext = Utils.buildNsContext("c", RESPONSE_NS);
        // XmlConfig.xmlConfig().namespaceAware(true).declareNamespace(RESPONSE_NS_PREFIX, RESPONSE_NS);
    }

    RestAssuredConfig namespaceAwareXmlConfig() {
        return newConfig().xmlConfig(xmlConfig().with().namespaceAware(true).declareNamespace(RESPONSE_NS_PREFIX, RESPONSE_NS))//
                .sslConfig(sslConfig().relaxedHTTPSValidation())//
                // https + redirections doesn't work
                // see https://github.com/jayway/rest-assured/issues/467
                .redirect(new RedirectConfig(true, false, false, 10))//
        ;
    }

    XmlPathConfig namespaceAwareXmlpathConfig() {
        return xmlPathConfig().declaredNamespace(RESPONSE_NS_PREFIX, RESPONSE_NS);
    }

    String getRandomExistingRegistrarCode() {
        String xml = with().config(namespaceAwareXmlConfig()).when().get("/registrars").andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml);
        int registrarsCount = xmlPath.getInt("response.registrars.registrar.size()");
        if (registrarsCount != 0) {
            int registrarPosition = rand.nextInt(registrarsCount);
            String registrarCode = xmlPath.getString("response.registrars.registrar[" + registrarPosition + "].@code");
            return registrarCode;
        } else {
            return null;
        }
    }

    String buildResolvationPath(RsId id) {
        return "/registrars/" + Utils.urlEncodeReservedChars(id.registrarCode) + "/digitalDocuments/registrarScopeIdentifier/"//
                + Utils.urlEncodeReservedChars(id.type) + "/" + Utils.urlEncodeReservedChars(id.value);
    }

    String buildResolvationPath(String urnNbn) {
        return "/resolver/" + Utils.urlEncodeReservedChars(urnNbn);
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
            String type = xmlPath.getString("id[0].@type");
            String value = xmlPath.getString("id[0]");
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
        String xml = with().config(namespaceAwareXmlConfig()).expect()//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response", nsContext))//
                .when().get("/digitalInstances/id/" + diId).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(xml).setRoot("response");
        if (xmlPath.get("digitalInstance") != null) {
            DigitalInstance result = new DigitalInstance();
            result.setId(diId);
            result.setUrl(xmlPath.getString("digitalInstance.url"));
            result.setActive(xmlPath.getBoolean("digitalInstance.@active"));
            result.setCreated(DateTime.parse(xmlPath.getString("digitalInstance.created")));
            if (!result.isActive()) {
                result.setDeactivated(DateTime.parse(xmlPath.getString("digitalInstance.deactivated")));
            }
            return result;
        } else {
            return null;
        }
    }

}
