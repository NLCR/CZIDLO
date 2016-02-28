package cz.nkp.urnnbn.api.v3;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.List;
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
 * Tests for PUT /api/v3/resolver/${URN_NBN}/registrarScopeIdentifiers/${ID_TYPE_2}
 *
 */
public class PutRegistrarScopeIdentifierValueResolvedByUrnNbn extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(PutRegistrarScopeIdentifierValueResolvedByUrnNbn.class.getName());

    private String urnNbn;
    private String urnNbn2;
    private String urnNbn3;
    private String registrar2UrnNbn;

    @BeforeClass
    public void beforeClass() {
        init();
        urnNbn = registerUrnNbn(REGISTRAR, USER);
        urnNbn2 = registerUrnNbn(REGISTRAR, USER);
        urnNbn3 = registerUrnNbn(REGISTRAR, USER);
        registrar2UrnNbn = registerUrnNbn(REGISTRAR2, USER);
    }

    @AfterMethod
    public void afterMethod() {
        deleteAllRegistrarScopeIdentifiers(urnNbn, USER);
        deleteAllRegistrarScopeIdentifiers(urnNbn2, USER);
        deleteAllRegistrarScopeIdentifiers(urnNbn3, USER);
        deleteAllRegistrarScopeIdentifiers(registrar2UrnNbn, USER);
    }

    private String buildUrl(String urnNbn, String type) {
        return HTTPS_API_URL + buildResolvationPath(urnNbn) + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(type);
    }

    @Test
    public void createNotAuthenticated() {
        RsId idToBeCreated = new RsId(REGISTRAR, "type", "value");
        LOGGER.info(String.format("resolved by: %s, id to be created: %s", urnNbn, idToBeCreated.toString()));
        // try and create id
        // TODO:APIv4: return xml as well
        // String responseXml =
        with().config(namespaceAwareXmlConfig())//
                .body(idToBeCreated.value)//
                .expect()//
                .statusCode(401)//
                // .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                // .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(buildUrl(urnNbn, idToBeCreated.type)).andReturn().asString();
        // XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        // Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHENTICATED");
        // check not inserted
        List<RsId> rsIdsFetched = getRsIds(urnNbn);
        assertThat(rsIdsFetched.size(), equalTo(0));
    }

    @Test
    public void updateNotAuthenticated() {
        String valueOld = "valueOld";
        String valueNew = "valueNew";
        RsId idToBeUpdated = new RsId(REGISTRAR, "type", valueOld);
        LOGGER.info(String.format("resolved by: %s, id to be updated: %s, new value: %s", urnNbn, idToBeUpdated.toString(), valueNew));
        // insert id
        insertRegistrarScopeId(urnNbn, idToBeUpdated, USER);
        // try and update id
        // TODO:APIv4: return xml as well
        // String responseXml =
        with().config(namespaceAwareXmlConfig())//
                .body(valueNew)//
                .expect()//
                .statusCode(401)//
                // .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                // .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(buildUrl(urnNbn, idToBeUpdated.type)).andReturn().asString();
        // XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        // Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHENTICATED");
        // check not updated
        List<RsId> rsIdsFetched = getRsIds(urnNbn);
        assertEquals(1, rsIdsFetched.size());
        assertEquals(idToBeUpdated.type, rsIdsFetched.get(0).type);
        assertEquals(idToBeUpdated.value, valueOld);
    }

    @Test
    public void createNotAuthorized() {
        RsId idToBeCreated = new RsId(REGISTRAR, "type", "value");
        LOGGER.info(String.format("resolved by: %s, id to be created: %s", urnNbn.toString(), idToBeCreated.type));
        // try and delete id
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_NO_RIGHTS.login, USER_NO_RIGHTS.password)//
                .body(idToBeCreated.value)//
                .expect()//
                .statusCode(401)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(buildUrl(urnNbn, idToBeCreated.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHORIZED");
        // check not inserted
        List<RsId> rsIdsFetched = getRsIds(urnNbn);
        assertThat(rsIdsFetched.size(), equalTo(0));
    }

    @Test
    public void updateNotAuthorized() {
        String valueOld = "valueOld";
        String valueNew = "valueNew";
        RsId idToBeUpdated = new RsId(REGISTRAR, "type", valueOld);
        LOGGER.info(String.format("resolved by: %s, id to be updated: %s, new value: %s", urnNbn.toString(), idToBeUpdated.toString(), valueNew));
        // insert id
        insertRegistrarScopeId(urnNbn, idToBeUpdated, USER);
        // try and update id
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_NO_RIGHTS.login, USER_NO_RIGHTS.password)//
                .body(valueNew)//
                .expect()//
                .statusCode(401)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(buildUrl(urnNbn, idToBeUpdated.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "NOT_AUTHORIZED");
        // check not updated
        List<RsId> rsIdsFetched = getRsIds(urnNbn);
        assertEquals(1, rsIdsFetched.size());
        assertEquals(idToBeUpdated.type, rsIdsFetched.get(0).type);
        assertEquals(idToBeUpdated.value, valueOld);
    }

    @Test
    public void urnnbnInvalid() {
        String urnNbn = Utils.getRandomItem(URNNBN_INVALID);
        LOGGER.info(urnNbn);
        RsId idToBeCreatedOrUpdated = new RsId(REGISTRAR, "type", "value");
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_NO_RIGHTS.login, USER_NO_RIGHTS.password)//
                .body(idToBeCreatedOrUpdated.value).expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(buildUrl(urnNbn, idToBeCreatedOrUpdated.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.getString("code"), "INVALID_URN_NBN");
    }

    @Test
    public void urnNbnValidFree() {
        String urnNbn = URN_NBN_FREE;
        LOGGER.info(urnNbn);
        RsId idToBeCreatedOrUpdated = new RsId(REGISTRAR, "type", "value");
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .body(idToBeCreatedOrUpdated.value).expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(buildUrl(urnNbn, idToBeCreatedOrUpdated.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertEquals("UNKNOWN_URN", xmlPath.getString("code"));
    }

    @Test
    public void urnnbnValidReserved() {
        String urnNbn = getReservedUrnNbn(REGISTRAR, USER);
        LOGGER.info(urnNbn);
        RsId idToBeCreatedOrUpdated = new RsId(REGISTRAR, "type", "value");
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .body(idToBeCreatedOrUpdated.value).expect()//
                .expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(buildUrl(urnNbn, idToBeCreatedOrUpdated.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertEquals("UNKNOWN_DIGITAL_DOCUMENT", xmlPath.getString("code"));
    }

    @Test
    public void typeInvalidCreateAll() {
        for (String type : RSID_TYPES_INVALID) {
            RsId idToBeCreatedOrUpdated = new RsId(REGISTRAR, type, "value");
            LOGGER.info(urnNbn + ", type: " + idToBeCreatedOrUpdated.type);
            String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                    .body(idToBeCreatedOrUpdated.value).expect()//
                    .statusCode(400)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:error", nsContext))//
                    .when().put(buildUrl(urnNbn, idToBeCreatedOrUpdated.type)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
            // TODO:APIv4: rename this error code
            assertThat(xmlPath.getString("error.code"), equalTo("INVALID_DIGITAL_DOCUMENT_ID_TYPE"));
        }
    }

    @Test
    public void valueInvalidCreate() {
        // TODO: enable after this is fixed: https://github.com/NLCR/CZIDLO/issues/135
        // for (String value : RSID_VALUES_INVALID) {
        // RsId idToBeCreated = new RsId(REGISTRAR, "type", value);
        // LOGGER.info(String.format("resolved by: %s, id to be created: %s", urnNbn, idToBeCreated.type));
        // // try and set rsId by type, resolved by another rsId
        // String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
        // .body(idToBeCreated.value).expect()//
        // .expect()//
        // .statusCode(400)//
        // .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
        // .body(hasXPath("/c:response/c:error", nsContext))//
        // .when().put(buildUrl(urnNbn, idToBeCreated.type)).andReturn().asString();
        // XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        // // TODO:APIv4: define new error INVALID_REGISTRAR_SCOPE_ID_VALUE
        // assertThat(xmlPath.getString("error.code"), equalTo("INVALID_REGISTRAR_SCOPE_ID_VALUE"));
        // }
        // // check that nothing was created
        // List<RsId> rsIds = getRsIds(urnNbn);
        // assertEquals(0, rsIds.size());
    }

    @Test
    public void valueInvalidUpdate() {
        // TODO: enable after this is fixed: https://github.com/NLCR/CZIDLO/issues/135
        // RsId idtoBeUpdated = new RsId(REGISTRAR, "type", "value");
        // insertRegistrarScopeId(urnNbn, idtoBeUpdated, USER);
        // for (String value : RSID_VALUES_INVALID) {
        // LOGGER.info(String.format("resolved by: %s, id to be updated: %s, new value: %s", urnNbn, idtoBeUpdated.toString(), value));
        // // try and set rsId by type, resolved by another rsId
        // String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
        // .body(value).expect()//
        // .statusCode(400)//
        // .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
        // .body(hasXPath("/c:response/c:error", nsContext))//
        // .when().put(buildUrl(urnNbn, idtoBeUpdated.type)).andReturn().asString();
        // XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        // // TODO:APIv4: define new error INVALID_REGISTRAR_SCOPE_ID_VALUE
        // assertThat(xmlPath.getString("error.code"), equalTo("INVALID_REGISTRAR_SCOPE_ID_VALUE"));
        // // check that not updated
        // List<RsId> rsIds = getRsIds(urnNbn);
        // assertEquals(1, rsIds.size());
        // assertEquals(idtoBeUpdated.type, rsIds.get(0).type);
        // assertEquals(idtoBeUpdated.value, rsIds.get(0).value);
        // }
    }

    @Test
    public void createCollision() {
        RsId idToBeCreated = new RsId(REGISTRAR, "collision", "valueColiding");
        RsId idColiding = new RsId(REGISTRAR, "collision", "valueColiding");
        LOGGER.info(String.format("resolved by: %s, id to be created: %s", urnNbn, idToBeCreated.toString()));
        // insert colliding id (same value and type but different digDoc)
        insertRegistrarScopeId(urnNbn2, idColiding, USER);
        // try and set rsId by type, resolved by urnNbn
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .body(idToBeCreated.value).expect()//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(buildUrl(urnNbn, idToBeCreated.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        // TODO:APIv4: rename this error code to something like REGISTRAR_SCOPE_ID_COLLISION
        assertThat(xmlPath.getString("error.code"), equalTo("INVALID_REGISTRAR_SCOPE_IDENTIFIER"));
    }

    @Test
    public void updateCollision() {
        RsId idColiding = new RsId(REGISTRAR, "collision", "valueColliding");
        RsId idToBeInserted = new RsId(REGISTRAR, "collision", "value");
        RsId idToBeUpdated = new RsId(REGISTRAR, "collision", "valueColliding");
        LOGGER.info(String.format("resolved by: %s, id to be updated: %s", urnNbn, idToBeUpdated.toString()));
        // insert id, colliding id (same value and type but different digDoc)
        insertRegistrarScopeId(urnNbn, idToBeInserted, USER);
        insertRegistrarScopeId(urnNbn2, idColiding, USER);
        // try and set rsId by type, resolved by urnNbn
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .body(idToBeUpdated.value).expect()//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(buildUrl(urnNbn, idToBeUpdated.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        // TODO:APIv4: rename this error code to something like REGISTRAR_SCOPE_ID_COLLISION
        assertThat(xmlPath.getString("error.code"), equalTo("INVALID_REGISTRAR_SCOPE_IDENTIFIER"));
    }

    @Test
    public void createNoCollision() {
        RsId idToBeCreated = new RsId(REGISTRAR, "typeTEST", "valueTEST");
        LOGGER.info(String.format("resolved by: %s, id to be created: %s", urnNbn, idToBeCreated.toString()));
        RsId rsIdAnotherRegistrar = new RsId(REGISTRAR2, "typeTEST", "valueTEST");
        Map<String, RsId> examples = new HashMap<>();
        examples.put(registerUrnNbn(REGISTRAR, USER), new RsId(REGISTRAR, "TYPETEST", "valueTEST"));
        examples.put(registerUrnNbn(REGISTRAR, USER), new RsId(REGISTRAR, "typetest", "valueTEST"));
        examples.put(registerUrnNbn(REGISTRAR, USER), new RsId(REGISTRAR, "typeTEST", "valuetest"));
        examples.put(registerUrnNbn(REGISTRAR, USER), new RsId(REGISTRAR, "typeTEST", "VALUETEST"));
        // insert rsIds
        insertRegistrarScopeId(registrar2UrnNbn, rsIdAnotherRegistrar, USER);
        for (String urn : examples.keySet()) {
            insertRegistrarScopeId(urn, examples.get(urn), USER);
        }
        // try and set rsId by type, resolved by another rsId
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .body(idToBeCreated.value).expect()//
                .expect()//
                .statusCode(201)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().put(buildUrl(urnNbn, idToBeCreated.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        assertThat(xmlPath.getInt("id.size()"), equalTo(1));
        assertThat(xmlPath.getString("id[0].@type"), equalTo(idToBeCreated.type));
        assertThat(xmlPath.getString("id[0]"), equalTo(idToBeCreated.value));
        assertThat(xmlPath.getString("id[0].@previousValue"), equalTo("[]"));
        // cleanup
        for (String urn : examples.keySet()) {
            deleteAllRegistrarScopeIdentifiers(urn, USER);
        }
    }

    @Test
    public void updateNoCollision() {
        // init ids
        RsId idToBeCreated = new RsId(REGISTRAR, "type", "oldValue");
        RsId idToBeUpdated = new RsId(REGISTRAR, "type", "collidingValue");
        RsId idNotColidingUpperCase = new RsId(REGISTRAR, "type", "COLLIDINGVALUE");
        RsId idNotColidingLowerCase = new RsId(REGISTRAR, "type", "collidingvalue");
        RsId idNotColidingOtherRegistrar = new RsId(REGISTRAR2, "type", "collidingValue");
        LOGGER.info(String.format("resolved by: %s, id to be updated: %s", urnNbn, idToBeUpdated.toString()));
        // insert id, non-colliding ids
        insertRegistrarScopeId(urnNbn, idToBeCreated, USER);
        insertRegistrarScopeId(urnNbn2, idNotColidingUpperCase, USER);
        insertRegistrarScopeId(urnNbn3, idNotColidingLowerCase, USER);
        insertRegistrarScopeId(registrar2UrnNbn, idNotColidingOtherRegistrar, USER);
        // try and set rsId by type, resolved by another rsId
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .body(idToBeUpdated.value).expect()//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().put(buildUrl(urnNbn, idToBeUpdated.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        assertThat(xmlPath.getInt("id.size()"), equalTo(1));
        assertThat(xmlPath.getString("id[0].@type"), equalTo(idToBeUpdated.type));
        assertThat(xmlPath.getString("id[0]"), equalTo(idToBeUpdated.value));
        assertThat(xmlPath.getString("id[0].@previousValue"), equalTo("oldValue"));
    }

    @Test
    public void validCreateAll() {
        for (String type : RSID_TYPES_VALID) {
            typeValidValueValidCreate(urnNbn, type, "value");
        }
        int counter = 0;
        for (String value : RSID_VALUES_VALID) {
            typeValidValueValidCreate(urnNbn, "type" + ++counter, value);
        }
    }

    private void typeValidValueValidCreate(String urnNbn, String type, String value) {
        RsId idToBeCreate = new RsId(REGISTRAR, type, value);
        LOGGER.info(urnNbn + ", type: " + idToBeCreate.type);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .body(idToBeCreate.value).expect()//
                .statusCode(201)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().put(buildUrl(urnNbn, idToBeCreate.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.id");
        assertEquals(type, xmlPath.getString("@type"));
        assertEquals("[]", xmlPath.getString("@previousValue"));
        assertEquals(value, xmlPath.getString(""));
    }

    @Test
    public void typeValidUpdateAll() {
        for (String type : RSID_TYPES_VALID) {
            String valueOld = "valueOld";
            insertRegistrarScopeId(urnNbn, new RsId(REGISTRAR, type, valueOld), USER);
            typeValidValueValidUpdate(urnNbn, type, valueOld, "valueNew");
        }
        int counter = 0;
        for (String valueNew : RSID_VALUES_VALID) {
            String valueOld = "valueOld";
            String type = "type" + ++counter;
            insertRegistrarScopeId(urnNbn, new RsId(REGISTRAR, type, valueOld), USER);
            typeValidValueValidUpdate(urnNbn, type, valueOld, valueNew);
        }
    }

    private void typeValidValueValidUpdate(String urnNbn, String type, String valueOld, String valueNew) {
        RsId idToBeUpdated = new RsId(REGISTRAR, type, valueOld);
        LOGGER.info(String.format("%s, id: %s, new value: %s", urnNbn, idToBeUpdated, valueNew));
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .body(valueNew).expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().put(buildUrl(urnNbn, idToBeUpdated.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.id");
        assertEquals(type, xmlPath.getString("@type"));
        assertEquals(valueOld, xmlPath.getString("@previousValue"));
        assertEquals(valueNew, xmlPath.getString(""));
    }

}
