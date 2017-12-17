package cz.nkp.urnnbn.oaiadapter.cli;

import cz.nkp.urnnbn.core.CountryCode;
import cz.nkp.urnnbn.oaiadapter.OaiAdapter;
import cz.nkp.urnnbn.oaiadapter.ReportLogger;
import cz.nkp.urnnbn.oaiadapter.czidloapi.utils.XmlTools;
import cz.nkp.urnnbn.utils.PropertyLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import static cz.nkp.urnnbn.oaiadapter.cli.DefinedProperties.*;

/**
 * Main class
 */
public class App {

    private static final String USAGE = "USAGE: java -jar oaiAdapter.jar $CONFIGURATION_FILE";

    public static void main(String[] args) {
        try {
            if (args.length != 1) {
                System.err.println(USAGE);
                return;
            }
            PropertyLoader properties = new PropertyLoader(args[0]);
            OaiAdapter adapter = initOaiAdapter(properties);
            adapter.run();
        } catch (Exception ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static OaiAdapter initOaiAdapter(PropertyLoader properties) throws Exception {
        //core
        CountryCode.initialize("CZ");
        String registrarCode = properties.loadString(CZIDLO_API_REGISTRAR_CODE);
        // oai harvester
        String oaiBaseUrl = properties.loadString(OAI_BASE_URL);
        String oaiMetadataPrefix = properties.loadString(OAI_METADATA_PREFIX);
        String oaiSetSpec = properties.loadStringOrNull(OAI_SET);
        // czidlo api
        String czidloApiBaseUrl = properties.loadString(CZIDLO_API_BASE_URL);
        String czidloApiLogin = properties.loadString(CZIDLO_API_LOGIN);
        String czidloApiPassword = properties.loadString(CZIDLO_API_PASSWORD);
        boolean czidloApiIgnoreInvalidCertificate = properties.loadBoolean(CZIDLO_API_IGNORE_INVALID_CERTIFICATE, CZIDLO_API_IGNORE_INVALID_CERTIFICATE_DEFAULT);
        // xsl
        File ddRegistrationXslFile = new File(properties.loadString(DD_STYLESHEET));
        String ddRegistrationXsl = XmlTools.loadXmlFromFile(ddRegistrationXslFile.getAbsolutePath());
        File diImportXslFile = new File(properties.loadString(DI_STYLESHEET));
        String diImportXsl = XmlTools.loadXmlFromFile(diImportXslFile.getAbsolutePath());
        // xsd for transformation results
        URL ddRegistrationDataXsdUrl = properties.loadUrl(DD_REGISTRATION_XSD_URL);
        URL diImportDataXsdUrl = properties.loadUrl(DI_IMPORT_XSD_URL);
        //dd
        boolean registerDDsWithUrn = properties.loadBoolean(DD_REGISTRATION_REGISTER_DDS_WITH_URN);
        boolean registerDDsWithoutUrn = properties.loadBoolean(DD_REGISTRATION_REGISTER_DDS_WITHOUT_URN);
        //di
        boolean mergeDigitalInstances = properties.loadBoolean(DI_IMPORT_MERGE_DIS, DI_IMPORT_MERGE_DIS_DEFAULT);
        boolean ignoreDifferenceInDiAccessibility = properties.loadBoolean(DI_IMPORT_IGNORE_DIFFERENCE_IN_ACCESSIBILITY, DI_IMPORT_IGNORE_DIFFERENCE_IN_ACCESSIBILITY_DEFAULT);
        boolean ignoreDifferenceInDiFormat = properties.loadBoolean(DI_IMPORT_IGNORE_DIFFERENCE_IN_FORMAT, DI_IMPORT_IGNORE_DIFFERENCE_IN_FORMAT_DEFAULT);
        //report
        File reportFile = properties.loadFile(REPORT_FILE, false);
        //result
        return new OaiAdapter(registrarCode,
                oaiBaseUrl, oaiMetadataPrefix, oaiSetSpec,
                czidloApiBaseUrl, czidloApiLogin, czidloApiPassword, czidloApiIgnoreInvalidCertificate,
                ddRegistrationXsl, ddRegistrationXslFile, diImportXsl, diImportXslFile,
                ddRegistrationDataXsdUrl, diImportDataXsdUrl,
                registerDDsWithUrn, registerDDsWithoutUrn,
                mergeDigitalInstances, ignoreDifferenceInDiAccessibility, ignoreDifferenceInDiFormat,
                buildReportLogger(reportFile)
        );
    }

    private static ReportLogger buildReportLogger(File reportFile) throws Exception {
        try {
            return new ReportLogger(new FileOutputStream(reportFile));
        } catch (FileNotFoundException ex) {
            throw new Exception("Cannot open report file for writing", ex);
        }
    }
}
