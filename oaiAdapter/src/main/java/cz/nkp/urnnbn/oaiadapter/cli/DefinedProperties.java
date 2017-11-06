/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter.cli;

/**
 * @author Martin Řehánek
 */
public class DefinedProperties {

    static String REPORT_FILE = "oaiAdapter.reportFile";
    // oai provider
    static String OAI_BASE_URL = "oaiAdapter.oai.baseUrl";
    static String OAI_SET = "oaiAdapter.oai.setSpec";
    static String OAI_METADATA_PREFIX = "oaiAdapter.oai.metadataPrefix";
    // czidlo api
    public static String CZIDLO_API_BASE_URL = "oaiAdapter.czidlo_api.baseUrl";
    static String CZIDLO_API_LOGIN = "oaiAdapter.czidlo_api.login";
    static String CZIDLO_API_PASSWORD = "oaiAdapter.czidlo_api.password";
    static String CZIDLO_API_REGISTRAR_CODE = "oaiAdapter.czidlo_api.registrarCode";
    static String CZIDLO_API_IGNORE_INVALID_CERTIFICATE = "oaiAdapter.czidlo_api.ignoreInvalidCertificate";
    static boolean CZIDLO_API_IGNORE_INVALID_CERTIFICATE_DEFAULT = false;

    // xsl transformations
    static String DD_STYLESHEET = "oaiAdapter.digDocRegistrationXsl";
    static String DI_STYLESHEET = "oaiAdapter.digInstImportXsl";
    // xsd for transformation results
    public static String DD_REGISTRATION_XSD_URL = "oaiAdapter.digDocRegistrationXsdUrl";
    public static String DI_IMPORT_XSD_URL = "oaiAdapter.digInstImportXsdUrl";
    // dd registration
    public static String DD_REGISTRATION_REGISTER_DDS_WITH_URN = "oaiAdapter.ddRegistration.registerDigitalDocumentsWithUrnNbn";
    public static final boolean DD_REGISTRATION_REGISTER_DDS_WITH_URN_DEFAULT = true;
    public static String DD_REGISTRATION_REGISTER_DDS_WITHOUT_URN = "oaiAdapter.ddRegistration.registerDigitalDocumentsWithoutUrnNbn";
    public static final boolean DD_REGISTRATION_REGISTER_DDS_WITHOUT_URN_DEFAULT = false;
    // di import
    public static String DI_IMPORT_MERGE_DIS = "oaiAdapter.diImport.mergeDigitalInstances";
    public static boolean DI_IMPORT_MERGE_DIS_DEFAULT = false;
    public static String DI_IMPORT_IGNORE_DIFFERENCE_IN_ACCESSIBILITY = "oaiAdapter.diImport.ignoreDifferenceInAccessibility";
    public static boolean DI_IMPORT_IGNORE_DIFFERENCE_IN_ACCESSIBILITY_DEFAULT = false;
    public static String DI_IMPORT_IGNORE_DIFFERENCE_IN_FORMAT = "oaiAdapter.diImport.ignoreDifferenceInFormat";
    public static boolean DI_IMPORT_IGNORE_DIFFERENCE_IN_FORMAT_DEFAULT = false;

}
