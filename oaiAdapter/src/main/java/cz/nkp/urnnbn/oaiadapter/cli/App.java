package cz.nkp.urnnbn.oaiadapter.cli;

import cz.nkp.urnnbn.core.CountryCode;
import cz.nkp.urnnbn.oaiadapter.OaiAdapter;
import cz.nkp.urnnbn.oaiadapter.XsdProvider;
import cz.nkp.urnnbn.oaiadapter.czidlo.CzidloApiConnector;
import cz.nkp.urnnbn.oaiadapter.utils.Credentials;
import cz.nkp.urnnbn.oaiadapter.utils.XmlTools;
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
        OaiAdapter adapter = new OaiAdapter();
        //core
        CountryCode.initialize("CZ");
        // czidlo api
        Credentials credentials = new Credentials(properties.loadString(CZIDLO_API_LOGIN), properties.loadString(CZIDLO_API_PASSWORD));
        boolean ignoreInvalidCertificate = properties.loadBoolean(CZIDLO_API_IGNORE_INVALID_CERTIFICATE, CZIDLO_API_IGNORE_INVALID_CERTIFICATE_DEFAULT);
        String czidloApiBaseUrl = properties.loadString(CZIDLO_API_BASE_URL);
        adapter.setCzidloConnector(new CzidloApiConnector(czidloApiBaseUrl, credentials, ignoreInvalidCertificate));
        adapter.setRegistrarCode(properties.loadString(CZIDLO_API_REGISTRAR_CODE));
        // oai harvester
        adapter.setOaiBaseUrl(properties.loadString(OAI_BASE_URL));
        adapter.setMetadataPrefix(properties.loadString(OAI_METADATA_PREFIX));
        adapter.setSetSpec(properties.loadStringOrNull(OAI_SET));
        // xsl
        File ddRegistrationXslFile = new File(properties.loadString(DD_STYLESHEET));
        adapter.setMetadataToDdRegistrationXslt(ddRegistrationXslFile, XmlTools.loadXmlFromFile(ddRegistrationXslFile.getAbsolutePath()));
        File diImportXsdFile = new File(properties.loadString(DI_STYLESHEET));
        adapter.setMetadataToDiImportXslt(diImportXsdFile, XmlTools.loadXmlFromFile(diImportXsdFile.getAbsolutePath()));
        // xsd for transformation results
        URL digDocRegistrationDataXsdUrl = properties.loadUrl(DD_REGISTRATION_XSD_URL);
        URL digitalInstanceImportDataXsdUrl = properties.loadUrl(DI_IMPORT_XSD_URL);
        adapter.setXsdProvider(new XsdProvider(digDocRegistrationDataXsdUrl, digitalInstanceImportDataXsdUrl));
        //dd
        adapter.setRegisterDDsWithUrn(properties.loadBoolean(DD_REGISTRATION_REGISTER_DDS_WITH_URN));
        adapter.setRegisterDDsWithoutUrn(properties.loadBoolean(DD_REGISTRATION_REGISTER_DDS_WITHOUT_URN));
        //di
        adapter.setMergeDigitalInstances(properties.loadBoolean(DI_IMPORT_MERGE_DIS, DI_IMPORT_MERGE_DIS_DEFAULT));
        adapter.setIgnoreDifferenceInDiAccessibility(properties.loadBoolean(DI_IMPORT_IGNORE_DIFFERENCE_IN_ACCESSIBILITY, DI_IMPORT_IGNORE_DIFFERENCE_IN_ACCESSIBILITY_DEFAULT));
        adapter.setIgnoreDifferenceInDiFormat(properties.loadBoolean(DI_IMPORT_IGNORE_DIFFERENCE_IN_FORMAT, DI_IMPORT_IGNORE_DIFFERENCE_IN_FORMAT_DEFAULT));
        // report
        initReportStream(adapter, properties);
        return adapter;
    }

    private static void initReportStream(OaiAdapter adapter, PropertyLoader properties) throws Exception {
        try {
            File reportFile = properties.loadFile(REPORT_FILE, false);
            adapter.setOutputStream(new FileOutputStream(reportFile));
        } catch (FileNotFoundException ex) {
            throw new Exception("Cannot open report file for writing", ex);
        }
    }
}
