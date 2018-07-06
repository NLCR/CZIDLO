package cz.nkp.urnnbn.api.v5;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;
import cz.nkp.urnnbn.api.Utils;
import cz.nkp.urnnbn.api.pojo.Metadata;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.logging.Logger;

import static com.jayway.restassured.RestAssured.with;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for PUT /api/v5/resolver/${URN_NBN}
 *
 */
public class PutDigitalDocumentResolvedByUrnNbn extends ApiV5Tests {

    private static final Logger LOGGER = Logger.getLogger(PutDigitalDocumentResolvedByUrnNbn.class.getName());

    @BeforeClass
    public void beforeClass() {
        init();
    }

    private String buildUrl(String urnNbn) {
        return HTTPS_API_URL + buildResolvationPath(urnNbn);
    }

    @Test
    public void notAuthenticated() {
        String urnNbn = registerUrnNbn(REGISTRAR, ddRegistrationBuilder.withMetadata(Metadata.monographMinimal()), USER);
        LOGGER.info(urnNbn);
        String bodyXml = ddRegistrationBuilder.withMetadata(Metadata.monographFull1());
        with().config(namespaceAwareXmlConfig())//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(401)//
                .when().put(buildUrl(urnNbn));
    }

    @Test
    public void notAuthorized() {
        String urnNbn = registerUrnNbn(REGISTRAR, ddRegistrationBuilder.withMetadata(Metadata.monographMinimal()), USER);
        LOGGER.info(urnNbn);
        String bodyXml = ddRegistrationBuilder.withMetadata(Metadata.monographFull2());
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER_NO_RIGHTS.login, USER_NO_RIGHTS.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(403)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:error", nsContext))//
                .when().put(buildUrl(urnNbn)).andReturn().asString();
        XmlPath xmlPath = XmlPath.from(responseXml).setRoot("response.error");
        Assert.assertEquals(xmlPath.get("code"), "NO_ACCESS_RIGHTS");
    }

    @Test
    public void urnbnInvalid() {
        String urnNbn = Utils.getRandomItem(URNNBN_INVALID);
        LOGGER.info(urnNbn);
        String bodyXml = ddRegistrationBuilder.minimal();
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .when().put(buildUrl(urnNbn)).andReturn().asString();
        Assert.assertEquals(XmlPath.from(responseXml).getString("response.error.code"), "INVALID_URN_NBN");
    }

    @Test
    public void urnbnFree() {
        String urnNbn = getRandomFreeUrnNbnOrNull(REGISTRAR);
        if (urnNbn == null) {
            LOGGER.warning("no urn:nbn available, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String bodyXml = ddRegistrationBuilder.minimal();
            String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                    .given().request().body(bodyXml).contentType(ContentType.XML)//
                    .expect()//
                    .statusCode(404)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .when().put(buildUrl(urnNbn)).andReturn().asString();
            Assert.assertEquals(XmlPath.from(responseXml).getString("response.error.code"), "UNKNOWN_URN_NBN");

        }
    }

    @Test
    public void urnbnReserved() {
        String urnNbn = getReservedUrnNbn(REGISTRAR, USER);
        if (urnNbn == null) {
            LOGGER.warning("no urn:nbn available, ignoring");
        } else {
            LOGGER.info(urnNbn);
            String bodyXml = ddRegistrationBuilder.minimal();
            String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                    .given().request().body(bodyXml).contentType(ContentType.XML)//
                    .expect()//
                    .statusCode(404)//
                    .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                    .when().put(buildUrl(urnNbn)).andReturn().asString();
            Assert.assertEquals(XmlPath.from(responseXml).getString("response.error.code"), "UNKNOWN_DIGITAL_DOCUMENT");

        }
    }

    @Test
    public void urnbnDeactivated() {
        String urnNbn = registerUrnNbn(REGISTRAR, ddRegistrationBuilder.withMetadata(Metadata.monographMinimal()), USER);
        LOGGER.info(urnNbn);
        deactivateUrnNbn(urnNbn, USER);
        String bodyXml = ddRegistrationBuilder.withMetadata(Metadata.monographFull1());
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(403)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .when().put(buildUrl(urnNbn)).andReturn().asString();
        Assert.assertEquals(XmlPath.from(responseXml).getString("response.error.code"), "URN_NBN_DEACTIVATED");
    }

    @Test
    public void invalidData() {
        String urnNbn = registerUrnNbn(REGISTRAR, ddRegistrationBuilder.withMetadata(Metadata.monographMinimal()), USER);
        LOGGER.info(urnNbn);
        String bodyXml = ddRegistrationBuilder.noNamespace();
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .when().put(buildUrl(urnNbn)).andReturn().asString();
        Assert.assertEquals(XmlPath.from(responseXml).getString("response.error.code"), "INVALID_DATA");
    }

    @Test
    public void wrongIeType() {
        String urnNbn = registerUrnNbn(REGISTRAR, ddRegistrationBuilder.withMetadata(Metadata.monographMinimal()), USER);
        LOGGER.info(urnNbn);
        String bodyXml = ddRegistrationBuilder.withMetadata(Metadata.periodicalFull1());
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(400)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .when().put(buildUrl(urnNbn)).andReturn().asString();
        Assert.assertEquals(XmlPath.from(responseXml).getString("response.error.code"), "INVALID_DATA");
    }

    @Test
    public void updateAllOptionalFieldsChanged() {
        update(Metadata.monographMinimal(), Metadata.monographFull1());
        update(Metadata.monographVolumeMinimal(), Metadata.monographVolumeFull1());
        update(Metadata.periodicalMinimal(), Metadata.periodicalFull1());
        update(Metadata.periodicalVolumeMinimal(), Metadata.periodicalVolumeFull1());
        update(Metadata.periodicalIssueMinimal(), Metadata.periodicalIssueFull1());
        update(Metadata.analyticalMinimal(), Metadata.analyticalFull1());
        update(Metadata.thesisMinimal(), Metadata.thesisFull1());
        update(Metadata.otherEntityMinimal(), Metadata.otherEntityFull1());
        update(Metadata.analyticalMinimal(), Metadata.analyticalFull1());
    }

    @Test
    public void updateNoOptionalFieldChanged() {
        update(Metadata.monographFull1(), Metadata.monographFull2());
        update(Metadata.monographVolumeFull1(), Metadata.monographVolumeFull2());
        update(Metadata.periodicalFull1(), Metadata.periodicalFull2());
        update(Metadata.periodicalVolumeFull1(), Metadata.periodicalVolumeFull2());
        update(Metadata.periodicalIssueFull1(), Metadata.periodicalIssueFull2());
        update(Metadata.analyticalFull1(), Metadata.analyticalFull2());
        update(Metadata.thesisFull1(), Metadata.thesisFull2());
        update(Metadata.otherEntityFull1(), Metadata.otherEntityFull2());
        update(Metadata.analyticalFull1(), Metadata.analyticalFull2());
    }

    private void update(Metadata metadataOriginal, Metadata metadataNew) {
        String urnNbn = registerUrnNbn(REGISTRAR, ddRegistrationBuilder.withMetadata(metadataOriginal), USER);
        LOGGER.info(String.format("%s -> %s: %s", metadataOriginal.description, metadataNew.description, urnNbn));
        // check metadata
        assertMetadataEquals(getMetadata(urnNbn), metadataOriginal);
        // update
        String bodyXml = ddRegistrationBuilder.withMetadata(metadataNew);
        String responseXml = with().config(namespaceAwareXmlConfig()).auth().basic(USER.login, USER.password)//
                .given().request().body(bodyXml).contentType(ContentType.XML)//
                .expect()//
                .statusCode(200)//
                .contentType(ContentType.XML).body(matchesXsd(responseXsdString))//
                .body(hasXPath("/c:response/c:digitalDocument", nsContext))//
                .when().put(buildUrl(urnNbn)).andReturn().asString();
        // check response
        assertMetadataEquals(responseXml, metadataOriginal.fillOnlyEmptyFields(metadataNew));
        // fetch record again and check
        assertMetadataEquals(getMetadata(urnNbn), metadataOriginal.fillOnlyEmptyFields(metadataNew));
    }

    private void assertMetadataEquals(String metadataStr, Metadata metadata) {
        XmlPath xmlPath = XmlPath.from(metadataStr).setRoot("response.digitalDocument");
        for (String path : metadata.getDataByXmlPath().keySet()) {
            Object expected = metadata.getDataByXmlPath().get(path);
            String found = xmlPath.getString(path);
            String message = String.format("%s: expected: \"%s\", found: \"%s\"", path, expected, found);
            if (expected == null) {
                assertTrue(message, found == null || "[]".equals(found) || "".equals(found) || "false".equals(found));
            } else {
                assertEquals(message, expected.toString(), found);
            }
        }
    }

}
