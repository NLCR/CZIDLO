/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter.cli;

/**
 * @author Martin Řehánek
 */
public class CliProperties {
    static String REPORT_FILE = "oaiAdapter.reportFile";
    // oai provider
    static String OAI_BASE_URL = "oaiAdapter.oai.baseUrl";
    static String OAI_SET = "oaiAdapter.oai.setSpec";
    static String OAI_METADATA_PREFIX = "oaiAdapter.oai.metadataPrefix";
    // czidlo api
    static String CZIDLO_API_BASE_URL = "oaiAdapter.czidloApi.baseUrl";
    static String CZIDLO_API_LOGIN = "oaiAdapter.czidloApi.login";
    static String CZIDLO_API_PASSWORD = "oaiAdapter.czidloApi.password";
    static String CZIDLO_API_REGISTRAR_CODE = "oaiAdapter.czidloApi.registrarCode";
    static String CZIDLO_API_IGNORE_INVALID_CERTIFICATE = "oaiAdapter.czidloApi.ignoreInvalidCertificate";
    static boolean CZIDLO_API_IGNORE_INVALID_CERTIFICATE_DEFAULT = false;
    // xsl transformations
    static String DD_STYLESHEET = "oaiAdapter.digDocRegistrationXsl";
    static String DI_STYLESHEET = "oaiAdapter.digInstImportXsl";
    // xsd for transformation results
    static String DD_REGISTRATION_XSD_URL = "oaiAdapter.digDocRegistrationXsdUrl";
    static String DI_IMPORT_XSD_URL = "oaiAdapter.digInstImportXsdUrl";
    // dd registration
    static String DD_REGISTRATION_REGISTER_DDS_WITH_URN = "oaiAdapter.ddRegistration.registerDigitalDocumentsWithUrnNbn";
    static String DD_REGISTRATION_REGISTER_DDS_WITHOUT_URN = "oaiAdapter.ddRegistration.registerDigitalDocumentsWithoutUrnNbn";
    // di import
    static String DI_IMPORT_MERGE_DIS = "oaiAdapter.diImport.mergeDigitalInstances";
    static boolean DI_IMPORT_MERGE_DIS_DEFAULT = false;
    static String DI_IMPORT_IGNORE_DIFFERENCE_IN_ACCESSIBILITY = "oaiAdapter.diImport.ignoreDifferenceInAccessibility";
    static boolean DI_IMPORT_IGNORE_DIFFERENCE_IN_ACCESSIBILITY_DEFAULT = false;
    static String DI_IMPORT_IGNORE_DIFFERENCE_IN_FORMAT = "oaiAdapter.diImport.ignoreDifferenceInFormat";
    static boolean DI_IMPORT_IGNORE_DIFFERENCE_IN_FORMAT_DEFAULT = false;
}
