/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter.cli;

/**
 *
 * @author rehan
 */
public class DefinedProperties {

    static String REPORT_FILE = "oaiAdapter.reportFile";
    //oai provider
    static String OAI_BASE_URL = "oaiAdapter.oai.baseUrl";
    static String OAI_SET = "oaiAdapter.oai.setSpec";
    static String OAI_METADATA_PREFIX = "oaiAdapter.oai.metadataPrefix";
    //resolver
    public static String RESOLVER_API_URL = "oaiAdapter.resolver.apiUrl";
    static String RESOLVER_LOGIN = "oaiAdapter.resolver.login";
    static String RESOLVER_PASSWORD = "oaiAdapter.resolver.password";
    static String RESOLVER_REGISTRAR_CODE = "oaiAdapter.resolver.registrarCode";
    static String RESOLVER_REGISTRATION_MODE = "oaiAdapter.resolver.registrationMode";
    //xsl transformations
    static String DD_STYLESHEET = "oaiAdapter.digDocRegistrationXsl";
    static String DI_STYLESHEET = "oaiAdapter.digInstImportXsl";
    //xsd for transformation results
    public static String DD_REGISTRATION_XSD_URL = "oaiAdapter.digDocRegistrationXsdUrl";
    public static String DI_IMPORT_XSD_URL = "oaiAdapter.digInstImportXsdUrl";
}
