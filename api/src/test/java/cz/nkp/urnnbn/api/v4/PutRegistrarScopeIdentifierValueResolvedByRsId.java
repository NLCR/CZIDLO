package cz.nkp.urnnbn.api.v4;

import static com.jayway.restassured.RestAssured.given;
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
import cz.nkp.urnnbn.api.pojo.RsId;

/**
 * Tests for PUT
 * /api/v4/registrars/${REGISTRAR_CODE}/digitalDocuments/registrarScopeIdentifier/${ID_TYPE}/${ID_VALUE}/registrarScopeIdentifiers/${ID_TYPE_2}
 *
 */
public class PutRegistrarScopeIdentifierValueResolvedByRsId extends ApiV3Tests {

    private static final Logger LOGGER = Logger.getLogger(PutRegistrarScopeIdentifierValueResolvedByRsId.class.getName());

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

    private String buildUrl(RsId idForResolvation, String type) {
        return HTTPS_API_URL + buildResolvationPath(idForResolvation) + "/registrarScopeIdentifiers/" + Utils.urlEncodeReservedChars(type);
    }

    @Test
    public void createNotAuthenticated() {
        RsId idForResolvation = new RsId(REGISTRAR, "type", "value");
        RsId idToBeCreated = new RsId(REGISTRAR, "type2", "value2");
        LOGGER.info(String.format("resolved by: %s, id to be created: %s", idForResolvation.toString(), idToBeCreated.type));
        // insert id for resolvation
        insertRegistrarScopeId(urnNbn, idForResolvation, USER);
        // try and create id
        given().request().body(idToBeCreated.value)//
                .expect()//
                .statusCode(401)//
                .when().put(buildUrl(idForResolvation, idToBeCreated.type));
        // check not inserted
        List<RsId> rsIdsFetched = getRsIds(urnNbn);
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
        insertRegistrarScopeId(urnNbn, idForResolvation, USER);
        insertRegistrarScopeId(urnNbn, idToBeUpdated, USER);
        // try and update id
        given().request().body(valueNew)//
                .expect()//
                .statusCode(401)//
                .when().put(buildUrl(idForResolvation, idToBeUpdated.type));
        // check not updated
        List<RsId> rsIdsFetched = getRsIds(urnNbn);
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
        insertRegistrarScopeId(urnNbn, idForResolvation, USER);
        // try and delete id
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_NO_RIGHTS.login, USER_NO_RIGHTS.password)//
                .body(idToBeCreated.value).expect()//
                .statusCode(403)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(buildUrl(idForResolvation, idToBeCreated.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "NO_ACCESS_RIGHTS");
        // check not inserted
        List<RsId> rsIdsFetched = getRsIds(urnNbn);
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
        insertRegistrarScopeId(urnNbn, idForResolvation, USER);
        insertRegistrarScopeId(urnNbn, idToBeUpdated, USER);
        // try and update id
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_NO_RIGHTS.login, USER_NO_RIGHTS.password)//
                .body(valueNew).expect()//
                .statusCode(403)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(buildUrl(idForResolvation, idToBeUpdated.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "NO_ACCESS_RIGHTS");
        // check not updated
        List<RsId> rsIdsFetched = getRsIds(urnNbn);
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
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .body(idToBeCreatedOrUpdated.value).expect()//
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
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .body(idToBeCreatedOrUpdated.value).expect()//
                .statusCode(404) //
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
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .body(idToBeCreatedOrUpdated.value).expect()//
                .statusCode(400) //
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString)) //
                .body(hasXPath("/c:response/c:error", nsContext)) //
                .when().put(buildUrl(idForResolvation, idToBeCreatedOrUpdated.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "INVALID_REGISTRAR_SCOPE_ID_TYPE");
    }

    @Test
    public void rsIdTypeValidValueInvalid() {
        RsId idForResolvation = new RsId(REGISTRAR, "type", Utils.getRandomItem(RSID_VALUES_INVALID));
        LOGGER.info(idForResolvation.toString());
        RsId idToBeCreatedOrUpdated = new RsId(REGISTRAR, "type2", "value2");
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .body(idToBeCreatedOrUpdated.value).expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(buildUrl(idForResolvation, idToBeCreatedOrUpdated.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertThat(xmlPath.getString("code"), equalTo("INVALID_REGISTRAR_SCOPE_ID_VALUE"));
    }

    @Test
    public void unknowDigitalDocument() {
        RsId idForResolvation = new RsId(REGISTRAR, "type", "value");
        LOGGER.info(idForResolvation.toString());
        RsId idToBeCreatedOrUpdated = new RsId(REGISTRAR, "type2", "value2");
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .body(idToBeCreatedOrUpdated.value).expect()//
                .statusCode(404)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(buildUrl(idForResolvation, idToBeCreatedOrUpdated.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        assertThat(xmlPath.getString("code"), equalTo("UNKNOWN_DIGITAL_DOCUMENT"));
    }

    @Test
    public void typeInvalidCreateAll() {
        RsId idForResolvation = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbn, idForResolvation, USER);
        for (String type : RSID_TYPES_INVALID) {
            RsId idToBeInserted = new RsId(REGISTRAR, type, "value");
            LOGGER.info(String.format("resolved by: %s, id to be created: %s", idForResolvation.toString(), idToBeInserted.type));
            // try and set rsId by type, resolved by another rsId
            String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                    .body(idToBeInserted.value).expect()//
                    .statusCode(400)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:error", nsContext))//
                    .when().put(buildUrl(idForResolvation, idToBeInserted.type)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
            assertThat(xmlPath.getString("code"), equalTo("INVALID_REGISTRAR_SCOPE_ID_TYPE"));
        }
    }

    @Test
    public void typeValidCreateAll() {
        RsId idForResolvation = new RsId(REGISTRAR, "typeForResolvation", "value");
        insertRegistrarScopeId(urnNbn, idForResolvation, USER);
        for (String type : RSID_TYPES_VALID) {
            typeValidValueValidCreate(idForResolvation, urnNbn, type, "value");
        }
    }

    @Test
    public void typeValidUpdateAll() {
        RsId idForResolvation = new RsId(REGISTRAR, "typeForResolvation", "value");
        insertRegistrarScopeId(urnNbn, idForResolvation, USER);
        for (String type : RSID_TYPES_VALID) {
            String valueOld = "oldValue";
            String valueNew = "newValue";
            typeValidValueValidUpdate(idForResolvation, new RsId(REGISTRAR, type, valueOld), valueNew);
        }
    }

    @Test
    public void valueInvalidCreateAll() {
        RsId idForResolvation = new RsId(REGISTRAR, "resolvation", "value");
        insertRegistrarScopeId(urnNbn, idForResolvation, USER);
        for (String value : RSID_VALUES_INVALID) {
            RsId idToBeCreated = new RsId(REGISTRAR, "create", value);
            LOGGER.info(String.format("resolved by: %s, id to be created: %s", idForResolvation.toString(), idToBeCreated.type));
            // try and set rsId by type, resolved by another rsId
            String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                    .body(idToBeCreated.value).expect()//
                    .expect()//
                    .statusCode(400)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:error", nsContext))//
                    .when().put(buildUrl(idForResolvation, idToBeCreated.type)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
            assertThat(xmlPath.getString("code"), equalTo("INVALID_REGISTRAR_SCOPE_ID_VALUE"));
            // check that only id for resolvation was created
            List<RsId> rsIds = getRsIds(urnNbn);
            assertEquals(1, rsIds.size());
        }
    }

    @Test
    public void valueInvalidUpdateAll() {
        RsId idForResolvation = new RsId(REGISTRAR, "resolvation", "value");
        insertRegistrarScopeId(urnNbn, idForResolvation, USER);
        RsId idtoBeUpdated = new RsId(REGISTRAR, "type", "value");
        insertRegistrarScopeId(urnNbn, idtoBeUpdated, USER);
        for (String value : RSID_VALUES_INVALID) {
            LOGGER.info(String.format("resolved by: %s, id to be updated: %s, new value: %s", idForResolvation.toString(), idtoBeUpdated.toString(),
                    value));
            // try and set rsId by type, resolved by another rsId
            String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                    .body(value).expect()//
                    .statusCode(400)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .body(hasXPath("/c:response/c:error", nsContext))//
                    .when().put(buildUrl(idForResolvation, idtoBeUpdated.type)).andReturn().asString();
            XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
            assertThat(xmlPath.getString("code"), equalTo("INVALID_REGISTRAR_SCOPE_ID_VALUE"));
            // check that not updated
            List<RsId> rsIds = getRsIds(urnNbn);
            assertEquals(2, rsIds.size());
            for (RsId id : rsIds) {
                if (id.type.equals(idtoBeUpdated.type)) {
                    assertEquals(id.value, idtoBeUpdated.value);
                }
            }
        }
    }

    @Test
    public void valueValidCreateAll() {
        RsId idForResolvation = new RsId(REGISTRAR, "typeForResolvation", "value");
        insertRegistrarScopeId(urnNbn, idForResolvation, USER);
        int counter = 0;
        for (String value : RSID_VALUES_VALID) {
            String type = "type" + ++counter;
            typeValidValueValidCreate(idForResolvation, urnNbn, type, value);
        }
    }

    @Test
    public void valueValidUpdateAll() {
        RsId idForResolvation = new RsId(REGISTRAR, "typeForResolvation", "value");
        insertRegistrarScopeId(urnNbn, idForResolvation, USER);
        int counter = 0;
        for (String valueNew : RSID_VALUES_VALID) {
            String type = "type" + ++counter;
            String valueOld = "oldVAlue";
            typeValidValueValidUpdate(idForResolvation, new RsId(REGISTRAR, type, valueOld), valueNew);
        }
    }

    private void typeValidValueValidCreate(RsId idForResolvation, String urnNbn, String type, String value) {
        RsId idToBeCreated = new RsId(REGISTRAR, type, value);
        LOGGER.info(String.format("resolved by: %s, created: %s", idForResolvation.toString(), idToBeCreated.toString()));
        // insert idToBeCreated
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
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

    private void typeValidValueValidUpdate(RsId idForResolvation, RsId idToBeUpdated, String valueNew) {
        LOGGER.info(String.format("resolved by: %s, created: %s, new value: %s", idForResolvation.toString(), idToBeUpdated.toString(), valueNew));
        // insert idToBeUpdated
        insertRegistrarScopeId(urnNbn, idToBeUpdated, USER);
        // update idToBeUpdated
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .body(valueNew).expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().put(buildUrl(idForResolvation, idToBeUpdated.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        assertThat(xmlPath.getInt("id.size()"), equalTo(1));
        assertThat(xmlPath.getString("id[0].@type"), equalTo(idToBeUpdated.type));
        assertThat(xmlPath.getString("id[0].@previousValue"), equalTo(idToBeUpdated.value));
        assertThat(xmlPath.getString("id[0]"), equalTo(valueNew));
        // check that id present and with new value
        List<RsId> rsIdsFetched = getRsIds(urnNbn);
        boolean found = false;
        for (RsId id : rsIdsFetched) {
            if (id.type.equals(idToBeUpdated.type)) {
                assertThat(id.value, equalTo(valueNew));
                found = true;
            }
        }
        Assert.assertTrue(found, "inserted id not found in getAllRsIds response");
    }

    @Test
    public void createCollision() {
        RsId idForResolvation = new RsId(REGISTRAR, "resolvation", "value");
        RsId idToBeCreated = new RsId(REGISTRAR, "collision", "valueColiding");
        RsId idColiding = new RsId(REGISTRAR, "collision", "valueColiding");
        LOGGER.info(String.format("resolved by: %s, id to be created: %s", idForResolvation.toString(), idToBeCreated.toString()));
        // insert id for resolvation, colliding id (same value and type but different digDoc)
        insertRegistrarScopeId(urnNbn, idForResolvation, USER);
        insertRegistrarScopeId(urnNbn2, idColiding, USER);

        // try and set rsId by type, resolved by another rsId
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .body(idToBeCreated.value).expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(buildUrl(idForResolvation, idToBeCreated.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        // TODO:APIv4: rename this error code to something like REGISTRAR_SCOPE_ID_COLLISION
        assertThat(xmlPath.getString("code"), equalTo("INVALID_REGISTRAR_SCOPE_IDENTIFIER"));
    }

    @Test
    public void updateCollision() {
        RsId idForResolvation = new RsId(REGISTRAR, "resolvation", "value");
        RsId idToBeInserted = new RsId(REGISTRAR, "collision", "value");
        RsId idColiding = new RsId(REGISTRAR, "collision", "valueColliding");
        RsId idToBeUpdated = new RsId(REGISTRAR, "collision", "valueColliding");
        LOGGER.info(String.format("resolved by: %s, id to be updated: %s", idForResolvation.toString(), idToBeUpdated.toString()));
        // insert id for resolvation, id to be updated, colliding id (same value and type but different digDoc)
        insertRegistrarScopeId(urnNbn, idForResolvation, USER);
        insertRegistrarScopeId(urnNbn, idToBeInserted, USER);
        insertRegistrarScopeId(urnNbn2, idColiding, USER);
        // try and set rsId by type, resolved by another rsId
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .body(idToBeUpdated.value).expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(buildUrl(idForResolvation, idToBeUpdated.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        // TODO:APIv4: rename this error code to something like REGISTRAR_SCOPE_ID_COLLISION
        assertThat(xmlPath.getString("code"), equalTo("INVALID_REGISTRAR_SCOPE_IDENTIFIER"));
    }

    @Test
    public void createNoCollision() {
        // init ids
        RsId idForResolvation = new RsId(REGISTRAR, "resolvation", "value");
        RsId idToBeCreated = new RsId(REGISTRAR, "typeTEST", "valueTEST");
        LOGGER.info(String.format("resolved by: %s, id to be created: %s", idForResolvation.toString(), idToBeCreated.toString()));
        RsId rsIdAnotherRegistrar = new RsId(REGISTRAR2, "typeTEST", "valueTEST");
        Map<String, RsId> examples = new HashMap<>();
        examples.put(registerUrnNbn(REGISTRAR, USER), new RsId(REGISTRAR, "TYPETEST", "valueTEST"));
        examples.put(registerUrnNbn(REGISTRAR, USER), new RsId(REGISTRAR, "typetest", "valueTEST"));
        examples.put(registerUrnNbn(REGISTRAR, USER), new RsId(REGISTRAR, "typeTEST", "valuetest"));
        examples.put(registerUrnNbn(REGISTRAR, USER), new RsId(REGISTRAR, "typeTEST", "VALUETEST"));
        // insert rsIds
        insertRegistrarScopeId(urnNbn, idForResolvation, USER);
        insertRegistrarScopeId(registrar2UrnNbn, rsIdAnotherRegistrar, USER);
        for (String urn : examples.keySet()) {
            insertRegistrarScopeId(urn, examples.get(urn), USER);
        }
        // try and set rsId by type, resolved by another rsId
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
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
        // cleanup
        for (String urn : examples.keySet()) {
            deleteAllRegistrarScopeIdentifiers(urn, USER);
        }
    }

    @Test
    public void updateNoCollision() {
        // init ids
        RsId idForResolvation = new RsId(REGISTRAR, "resolvingType", "value");
        RsId idToBeCreated = new RsId(REGISTRAR, "type", "oldValue");
        RsId idToBeUpdated = new RsId(REGISTRAR, "type", "collidingValue");
        RsId idNotColidingUpperCase = new RsId(REGISTRAR, "type", "COLLIDINGVALUE");
        RsId idNotColidingLowerCase = new RsId(REGISTRAR, "type", "collidingvalue");
        RsId idNotColidingOtherRegistrar = new RsId(REGISTRAR2, "type", "collidingValue");
        LOGGER.info(String.format("resolved by: %s, id to be updated: %s", idForResolvation.toString(), idToBeUpdated.toString()));
        // insert id for resolvation, non-colliding ids, original id
        insertRegistrarScopeId(urnNbn, idForResolvation, USER);
        insertRegistrarScopeId(urnNbn, idToBeCreated, USER);
        insertRegistrarScopeId(urnNbn2, idNotColidingUpperCase, USER);
        insertRegistrarScopeId(urnNbn3, idNotColidingLowerCase, USER);
        insertRegistrarScopeId(registrar2UrnNbn, idNotColidingOtherRegistrar, USER);
        // try and set rsId by type, resolved by another rsId
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .body(idToBeUpdated.value).expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:id", nsContext))//
                .when().put(buildUrl(idForResolvation, idToBeUpdated.type)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response");
        assertThat(xmlPath.getInt("id.size()"), equalTo(1));
        assertThat(xmlPath.getString("id[0].@type"), equalTo(idToBeUpdated.type));
        assertThat(xmlPath.getString("id[0]"), equalTo(idToBeUpdated.value));
        assertThat(xmlPath.getString("id[0].@previousValue"), equalTo("oldValue"));
    }

}
