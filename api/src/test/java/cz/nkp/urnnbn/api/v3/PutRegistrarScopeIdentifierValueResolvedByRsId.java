package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import cz.nkp.urnnbn.api.Utils;

/**
 * Tests for PUT
 * /api/v3/registrars/${REGISTRAR_CODE}/digitalDocuments/registrarScopeIdentifier/${ID_TYPE}/${ID_VALUE}/registrarScopeIdentifiers/${ID_TYPE_2}
 *
 */
public class PutRegistrarScopeIdentifierValueResolvedByRsId extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(PutRegistrarScopeIdentifierValueResolvedByRsId.class.getName());

    private final String REGISTRAR = "aba001";
    private final String REGISTRAR2 = "tst001";
    private final Credentials USER_WITH_RIGHTS = new Credentials("martin", "i0oEhu");
    private final Credentials USER_NO_RIGHTS = new Credentials("nobody", "skgo1dukg");
    private final String URNNBN = "urn:nbn:cz:aba001-0005hy";
    private final String URNNBN2 = "urn:nbn:cz:aba001-00017q";
    private final String URNNBN3 = "urn:nbn:cz:aba001-0002g3";
    private final String REGISTRAR2_URNNBN = "urn:nbn:cz:tst01-000001";

    @BeforeClass
    public void beforeClass() {
        init();
    }

    @BeforeMethod
    public void beforeMethod() {
        deleteAllRegistrarScopeIdentifiers(URNNBN, USER_WITH_RIGHTS);
    }

    private String buildUrl(RsId idForResolvation, String type) {
        return HTTPS_API_URL + buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(type);
    }

    @Test
    public void createNotAuthenticated() {
        RsId idForResolvation = new RsId(REGISTRAR, "type", "value");
        RsId idToBeCreated = new RsId(REGISTRAR, "type2", "value2");
        LOGGER.info(String.format("resolved by: %s, id to be created: %s", idForResolvation.toString(), idToBeCreated.type));
        // insert id for resolvation
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        // try and create id
        // TODO:APIv4: return xml as well
        // String responseXml =
        with().config(namespaceAwareXmlConfig())//
                .body(idToBeCreated.value)//
                .expect()//
                .statusCode(401)//
                // .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                // .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(buildUrl(idForResolvation, idToBeCreated.type)).andReturn().asString();
        // XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        // Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHENTICATED");
        // check not inserted
        List<RsId> rsIdsFetched = getRsIds(URNNBN);
        assertThat(rsIdsFetched.size(), equalTo(1));
        assertThat(rsIdsFetched.get(0).type, equalTo(idForResolvation.type));
        assertThat(rsIdsFetched.get(0).value, equalTo(idForResolvation.value));
    }

    @Test
    public void updateNotAuthenticated() {
        RsId idForResolvation = new RsId(REGISTRAR, "type", "value");
        String valueOld = "valueOld";
        String valueNew = "valueNew";
        RsId idToBeUpdated = new RsId(REGISTRAR, "type2", valueOld);
        LOGGER.info(String.format("resolved by: %s, id to be updated: %s, new value: %s", idForResolvation.toString(), idToBeUpdated.type, valueNew));
        // insert ids
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idToBeUpdated, USER_WITH_RIGHTS);
        // try and update id
        // TODO:APIv4: return xml as well
        // String responseXml =
        with().config(namespaceAwareXmlConfig())//
                .body(valueNew)//
                .expect()//
                .statusCode(401)//
                // .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                // .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(buildUrl(idForResolvation, idToBeUpdated.type)).andReturn().asString();
        // XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        // Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHENTICATED");
        // check not updated
        List<RsId> rsIdsFetched = getRsIds(URNNBN);
        assertThat(rsIdsFetched.size(), equalTo(2));
        for (RsId id : rsIdsFetched) {
            if (id.type.equals(idForResolvation.type)) {
                assertThat(id.value, equalTo(idForResolvation.value));
            } else if (id.type.equals(idToBeUpdated.type)) {
                assertThat(id.value, equalTo(valueOld));
            } else {// unexpected id type
                Assert.fail();
            }
        }
    }

    @Test
    public void createNotAuthorized() {
        RsId idForResolvation = new RsId(REGISTRAR, "type", "value");
        RsId idToBeCreated = new RsId(REGISTRAR, "type2", "value2");
        LOGGER.info(String.format("resolved by: %s, id to be created: %s", idForResolvation.toString(), idToBeCreated.type));
        // insert id for resolvation
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        // try and delete id
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_NO_RIGHTS.login, USER_NO_RIGHTS.password)//
                .body(idToBeCreated.value)//
                .expect()//
                .statusCode(401)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(buildUrl(idForResolvation, idToBeCreated.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHORIZED");
        // check not inserted
        List<RsId> rsIdsFetched = getRsIds(URNNBN);
        assertThat(rsIdsFetched.size(), equalTo(1));
        assertThat(rsIdsFetched.get(0).type, equalTo(idForResolvation.type));
        assertThat(rsIdsFetched.get(0).value, equalTo(idForResolvation.value));
    }

    @Test
    public void updateNotAuthorized() {
        RsId idForResolvation = new RsId(REGISTRAR, "type", "value");
        String valueOld = "valueOld";
        String valueNew = "valueNew";
        RsId idToBeUpdated = new RsId(REGISTRAR, "type2", valueOld);
        LOGGER.info(String.format("resolved by: %s, id to be updated: %s, new value: %s", idForResolvation.toString(), idToBeUpdated.type, valueNew));
        // insert ids
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idToBeUpdated, USER_WITH_RIGHTS);
        // try and update id
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_NO_RIGHTS.login, USER_NO_RIGHTS.password)//
                .body(valueNew)//
                .expect()//
                .statusCode(401)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(buildUrl(idForResolvation, idToBeUpdated.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHORIZED");
        // check not updated
        List<RsId> rsIdsFetched = getRsIds(URNNBN);
        assertThat(rsIdsFetched.size(), equalTo(2));
        for (RsId id : rsIdsFetched) {
            if (id.type.equals(idForResolvation.type)) {
                assertThat(id.value, equalTo(idForResolvation.value));
            } else if (id.type.equals(idToBeUpdated.type)) {
                assertThat(id.value, equalTo(valueOld));
            } else {// unexpected id type
                Assert.fail();
            }
        }
    }

    @Test
    public void registrarCodeInvalid() {
        String registrarCode = Utils.getRandomItem(REGISTRAR_CODES_INVALID);
        LOGGER.info("registrar code: " + registrarCode);
        RsId idForResolvation = new RsId(registrarCode, "type1", "value1");
        RsId idToBeCreatedOrUpdated = new RsId(registrarCode, "type2", "value2");
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .body(idToBeCreatedOrUpdated.value).expect()//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(buildUrl(idForResolvation, idToBeCreatedOrUpdated.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_REGISTRAR_CODE");
    }

    @Test
    public void registrarCodeValidUnknown() {
        String registrarCode = Utils.getRandomItem(REGISTRAR_CODES_VALID);
        LOGGER.info("registrar code: " + registrarCode);
        RsId idForResolvation = new RsId(registrarCode, "type1", "value1");
        RsId idToBeCreatedOrUpdated = new RsId(registrarCode, "type2", "value2");
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .body(idToBeCreatedOrUpdated.value).expect()//
                .expect()//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(buildUrl(idForResolvation, idToBeCreatedOrUpdated.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "UNKNOWN_REGISTRAR");
    }

    @Test
    public void rsIdTypeInvalid() {
        RsId idForResolvation = new RsId(REGISTRAR, Utils.getRandomItem(RSID_TYPES_INVALID), "value");
        LOGGER.info(idForResolvation.toString());
        RsId idToBeCreatedOrUpdated = new RsId(REGISTRAR, "type2", "value2");
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .body(idToBeCreatedOrUpdated.value).expect()//
                .expect() //
                .statusCode(400) //
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString)) //
                .body(hasXPath("/c:response/c:error", nsContext)) //
                .when().put(buildUrl(idForResolvation, idToBeCreatedOrUpdated.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        // TODO:APIv4: rename error to INVALID_ID_TYPE
        Assert.assertEquals(xmlPath.get("code"), "INVALID_DIGITAL_DOCUMENT_ID_TYPE");
    }

    @Test
    public void rsIdTypeValidValueInvalid() {
        RsId idForResolvation = new RsId(REGISTRAR, "type", Utils.getRandomItem(RSID_TYPES_INVALID));
        LOGGER.info(idForResolvation.toString());
        RsId idToBeCreatedOrUpdated = new RsId(REGISTRAR, "type2", "value2");
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .body(idToBeCreatedOrUpdated.value).expect()//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(buildUrl(idForResolvation, idToBeCreatedOrUpdated.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        // TODO:APIv4: https://github.com/NLCR/CZIDLO/issues/132 (INVALID_REGISTRAR_SCOPE_ID_VALUE, code 400)
        assertThat(xmlPath.getString("code"), equalTo("UNKNOWN_DIGITAL_DOCUMENT"));
    }

    @Test
    public void unknowDigitalDocument() {
        RsId idForResolvation = new RsId(REGISTRAR, "type", "value");
        LOGGER.info(idForResolvation.toString());
        RsId idToBeCreatedOrUpdated = new RsId(REGISTRAR, "type2", "value2");
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .body(idToBeCreatedOrUpdated.value).expect()//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(buildUrl(idForResolvation, idToBeCreatedOrUpdated.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertThat(xmlPath.getString("code"), equalTo("UNKNOWN_DIGITAL_DOCUMENT"));
    }

    @Test
    public void typeInvalid() {
        RsId idForResolvation = new RsId(REGISTRAR, "type", "value");
        RsId idToBeCreatedOrUpdated = new RsId(REGISTRAR, Utils.getRandomItem(RSID_TYPES_INVALID), "value2");
        LOGGER.info(String.format("resolved by: %s, id to be created or updated: %s", idForResolvation.toString(), idToBeCreatedOrUpdated.type));
        // insert id for resolvation
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        // try and set rsId by type, resolved by another rsId
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .body(idToBeCreatedOrUpdated.value).expect()//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(buildUrl(idForResolvation, idToBeCreatedOrUpdated.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        // TODO:APIv4: rename this error code
        assertThat(xmlPath.getString("error.code"), equalTo("INVALID_DIGITAL_DOCUMENT_ID_TYPE"));
    }

    @Test
    public void valueInvalid() {
        // TODO: enable after this is fixed: https://github.com/NLCR/CZIDLO/issues/135
        // RsId idForResolvation = new RsId(REGISTRAR, "type", "value");
        // // insert id for resolvation
        // insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        // for (String value : RSID_VALUES_INVALID) {
        // RsId idToBeCreatedOrUpdated = new RsId(REGISTRAR, "type", value);
        // LOGGER.info(String.format("resolved by: %s, id to be created or updated: %s", idForResolvation.toString(), idToBeCreatedOrUpdated.type));
        // // try and set rsId by type, resolved by another rsId
        // String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
        // .body(idToBeCreatedOrUpdated.value).expect()//
        // .expect()//
        // .statusCode(400)//
        // // .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
        // // .body(hasXPath("/c:response/c:error", nsContext))//
        // .when().put(buildUrl(idForResolvation, idToBeCreatedOrUpdated.type)).andReturn().asString();
        // XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        // // TODO:APIv4: define new error INVALID_REGISTRAR_SCOPE_ID_VALUE
        // assertThat(xmlPath.getString("error.code"), equalTo("INVALID_REGISTRAR_SCOPE_ID_VALUE"));
        // }
    }

    @Test
    public void createCollision() {
        // initial cleanup
        deleteAllRegistrarScopeIdentifiers(URNNBN2, USER_WITH_RIGHTS);
        RsId idForResolvation = new RsId(REGISTRAR, "type", "value");
        RsId idToBeCreated = new RsId(REGISTRAR, "type2", "valueColiding");
        RsId idColiding = new RsId(REGISTRAR2, "type2", "valueColiding");
        LOGGER.info(String.format("resolved by: %s, id to be created: %s", idForResolvation.toString(), idToBeCreated.toString()));
        // insert id for resolvation, colliding id (same value and type but different digDoc)
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN2, idColiding, USER_WITH_RIGHTS);

        // try and set rsId by type, resolved by another rsId
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .body(idToBeCreated.value).expect()//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(buildUrl(idForResolvation, idToBeCreated.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        // TODO:APIv4: rename this error code to something like REGISTRAR_SCOPE_ID_COLLISION
        assertThat(xmlPath.getString("error.code"), equalTo("INVALID_REGISTRAR_SCOPE_IDENTIFIER"));
        // final cleanup
        deleteAllRegistrarScopeIdentifiers(URNNBN2, USER_WITH_RIGHTS);
    }

    @Test
    public void updateCollision() {
        // initial cleanup
        deleteAllRegistrarScopeIdentifiers(URNNBN2, USER_WITH_RIGHTS);
        RsId idForResolvation = new RsId(REGISTRAR, "typeForResolvation", "value");
        RsId idToBeInserted = new RsId(REGISTRAR, "type", "value");
        RsId idColiding = new RsId(REGISTRAR2, "type", "valueColliding");
        RsId idToBeUpdated = new RsId(REGISTRAR, "type", "valueColliding");
        LOGGER.info(String.format("resolved by: %s, id to be updated: %s", idForResolvation.toString(), idToBeUpdated.toString()));
        // insert id for resolvation, id to be updated, colliding id (same value and type but different digDoc)
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idToBeInserted, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN2, idColiding, USER_WITH_RIGHTS);
        // try and set rsId by type, resolved by another rsId
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .body(idToBeUpdated.value).expect()//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(buildUrl(idForResolvation, idToBeUpdated.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        // TODO:APIv4: rename this error code to something like REGISTRAR_SCOPE_ID_COLLISION
        assertThat(xmlPath.getString("error.code"), equalTo("INVALID_REGISTRAR_SCOPE_IDENTIFIER"));
        // final cleanup
        deleteAllRegistrarScopeIdentifiers(URNNBN2, USER_WITH_RIGHTS);
    }

    @Test
    public void createNoCollision() {
        // initial cleanup
        deleteAllRegistrarScopeIdentifiers(REGISTRAR2_URNNBN, USER_WITH_RIGHTS);
        // init ids
        RsId idForResolvation = new RsId(REGISTRAR, "resolvingType", "value");
        RsId idToBeCreated = new RsId(REGISTRAR, "typeTEST", "valueTEST");
        LOGGER.info(String.format("resolved by: %s, id to be created: %s", idForResolvation.toString(), idToBeCreated.toString()));
        RsId rsIdAnotherRegistrar = new RsId(REGISTRAR, "typeTEST", "valueTEST");
        Map<String, RsId> examples = new HashMap<>();
        examples.put(registerUrnNbn(REGISTRAR, USER_WITH_RIGHTS), new RsId(REGISTRAR, "TYPETEST", "valueTEST"));
        examples.put(registerUrnNbn(REGISTRAR, USER_WITH_RIGHTS), new RsId(REGISTRAR, "typetest", "valueTEST"));
        examples.put(registerUrnNbn(REGISTRAR, USER_WITH_RIGHTS), new RsId(REGISTRAR, "typeTEST", "valuetest"));
        examples.put(registerUrnNbn(REGISTRAR, USER_WITH_RIGHTS), new RsId(REGISTRAR, "typeTEST", "VALUETEST"));
        // insert rsIds
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        insertRegistrarScopeId(REGISTRAR2_URNNBN, rsIdAnotherRegistrar, USER_WITH_RIGHTS);
        for (String urn : examples.keySet()) {
            insertRegistrarScopeId(urn, examples.get(urn), USER_WITH_RIGHTS);
        }
        // try and set rsId by type, resolved by another rsId
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .body(idToBeCreated.value).expect()//
                .expect()//
                .statusCode(201)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().put(buildUrl(idForResolvation, idToBeCreated.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        assertThat(xmlPath.getInt("id.size()"), equalTo(1));
        assertThat(xmlPath.getString("id[0].@type"), equalTo(idToBeCreated.type));
        assertThat(xmlPath.getString("id[0]"), equalTo(idToBeCreated.value));
        assertThat(xmlPath.getString("id[0].@previousValue"), equalTo("[]"));
        // cleanup
        deleteAllRegistrarScopeIdentifiers(REGISTRAR2_URNNBN, USER_WITH_RIGHTS);
        for (String urn : examples.keySet()) {
            deleteAllRegistrarScopeIdentifiers(urn, USER_WITH_RIGHTS);
        }
    }

    @Test
    public void updateNoCollision() {
        // initial cleanup
        deleteAllRegistrarScopeIdentifiers(REGISTRAR2_URNNBN, USER_WITH_RIGHTS);
        deleteAllRegistrarScopeIdentifiers(URNNBN2, USER_WITH_RIGHTS);
        deleteAllRegistrarScopeIdentifiers(URNNBN3, USER_WITH_RIGHTS);
        // init ids
        RsId idForResolvation = new RsId(REGISTRAR, "resolvingType", "value");
        RsId idToBeCreated = new RsId(REGISTRAR, "type", "oldValue");
        RsId idToBeUpdated = new RsId(REGISTRAR, "type", "collidingValue");
        RsId idNotColidingUpperCase = new RsId(REGISTRAR, "type", "COLLIDINGVALUE");
        RsId idNotColidingLowerCase = new RsId(REGISTRAR, "type", "collidingvalue");
        RsId idNotColidingOtherRegistrar = new RsId(REGISTRAR2, "type", "collidingValue");
        LOGGER.info(String.format("resolved by: %s, id to be updated: %s", idForResolvation.toString(), idToBeUpdated.toString()));
        // insert id for resolvation, non-colliding ids, original id
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN, idToBeCreated, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN2, idNotColidingUpperCase, USER_WITH_RIGHTS);
        insertRegistrarScopeId(URNNBN3, idNotColidingLowerCase, USER_WITH_RIGHTS);
        insertRegistrarScopeId(REGISTRAR2_URNNBN, idNotColidingOtherRegistrar, USER_WITH_RIGHTS);
        // try and set rsId by type, resolved by another rsId
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .body(idToBeUpdated.value).expect()//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().put(buildUrl(idForResolvation, idToBeUpdated.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        assertThat(xmlPath.getInt("id.size()"), equalTo(1));
        assertThat(xmlPath.getString("id[0].@type"), equalTo(idToBeUpdated.type));
        assertThat(xmlPath.getString("id[0]"), equalTo(idToBeUpdated.value));
        assertThat(xmlPath.getString("id[0].@previousValue"), equalTo("oldValue"));
        // final cleanup
        deleteAllRegistrarScopeIdentifiers(REGISTRAR2_URNNBN, USER_WITH_RIGHTS);
        deleteAllRegistrarScopeIdentifiers(URNNBN2, USER_WITH_RIGHTS);
        deleteAllRegistrarScopeIdentifiers(URNNBN3, USER_WITH_RIGHTS);
    }

    @Test
    public void createValidAll() {
        RsId idForResolvation = new RsId(REGISTRAR, "typeForResolvation", "value");
        // insert idForResolvation
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        for (String type : RSID_TYPES_VALID) {
            createValid(URNNBN, idForResolvation, new RsId(REGISTRAR, type, "value"));
        }
        int counter = 0;
        for (String value : RSID_VALUES_VALID) {
            createValid(URNNBN, idForResolvation, new RsId(REGISTRAR, "type" + ++counter, value));
        }
    }

    private void createValid(String urnNbn, RsId idForResolvation, RsId idToBeCreated) {
        LOGGER.info(String.format("resolved by: %s, id to be inserted: %s", idForResolvation.toString(), idToBeCreated.toString()));
        // insert idToBeInserted
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .body(idToBeCreated.value).expect()//
                .statusCode(201)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().put(buildUrl(idForResolvation, idToBeCreated.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        assertThat(xmlPath.getInt("id.size()"), equalTo(1));
        assertThat(xmlPath.getString("id[0].@type"), equalTo(idToBeCreated.type));
        assertThat(xmlPath.getString("id[0]"), equalTo(idToBeCreated.value));
        assertThat(xmlPath.getString("id[0].@previousValue"), equalTo("[]"));
        // check that id present
        List<RsId> rsIdsFetched = getRsIds(urnNbn);
        boolean found = false;
        for (RsId id : rsIdsFetched) {
            if (id.type.equals(idToBeCreated.type)) {
                assertThat(id.value, equalTo(idToBeCreated.value));
                found = true;
            }
        }
        Assert.assertTrue(found, "inserted id not found in getAllRsIds response");
    }

    @Test
    public void updateValidAll() {
        RsId idForResolvation = new RsId(REGISTRAR, "typeForResolvation", "value");
        // insert idForResolvation
        insertRegistrarScopeId(URNNBN, idForResolvation, USER_WITH_RIGHTS);
        for (String type : RSID_TYPES_VALID) {
            String valueOld = "oldValue";
            String valueNew = "newValue";
            updateValid(idForResolvation, type, valueOld, valueNew);
        }
        int counter = 0;
        for (String valueNew : RSID_VALUES_VALID) {
            String type = "type" + ++counter;
            String valueOld = "oldVAlue";
            updateValid(idForResolvation, type, valueOld, valueNew);
        }
    }

    private void updateValid(RsId idForResolvation, String type, String valueOld, String valueNew) {
        LOGGER.info(String.format("resolved by: %s, id to be updated: type: %s, old value: %s, new value: %s", idForResolvation.toString(), type,
                valueOld, valueNew));
        // insert idToBeInserted
        insertRegistrarScopeId(URNNBN, new RsId(REGISTRAR, type, valueOld), USER_WITH_RIGHTS);
        // update
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_WITH_RIGHTS.login, USER_WITH_RIGHTS.password)//
                .body(valueNew).expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().put(buildUrl(idForResolvation, type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        assertThat(xmlPath.getInt("id.size()"), equalTo(1));
        assertThat(xmlPath.getString("id[0].@type"), equalTo(type));
        assertThat(xmlPath.getString("id[0]"), equalTo(valueNew));
        assertThat(xmlPath.getString("id[0].@previousValue"), equalTo(valueOld));
        // check that id present and with new value
        List<RsId> rsIdsFetched = getRsIds(URNNBN);
        boolean found = false;
        for (RsId id : rsIdsFetched) {
            if (id.type.equals(type)) {
                assertThat(id.value, equalTo(valueNew));
                found = true;
            }
        }
        Assert.assertTrue(found, "inserted id not found in getAllRsIds response");
    }

}
