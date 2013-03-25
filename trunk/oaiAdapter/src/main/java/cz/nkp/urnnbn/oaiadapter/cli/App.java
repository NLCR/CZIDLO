package cz.nkp.urnnbn.oaiadapter.cli;

import cz.nkp.urnnbn.oaiadapter.OaiAdapter;
import cz.nkp.urnnbn.oaiadapter.resolver.RegistrationMode;
import cz.nkp.urnnbn.oaiadapter.resolver.ResolverConnector;
import cz.nkp.urnnbn.oaiadapter.utils.XmlTools;
import cz.nkp.urnnbn.utils.PropertyLoader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Main class
 *
 */
public class App {

    private static final String USAGE = "USAGE: java -jar oaiAdapter.jar $CONFIGURATION_FILE";

    public static void main(String[] args) {
        try {
            //for testing - comment when commiting changes
            //args = new String[]{"/home/martin/NetBeansProjects/oaiAdapter/src/main/resources/oaiAdapter.properties"};
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
        //resolver
        adapter.setResolverConnector(new ResolverConnector(
                properties.loadString(DefinedProperties.RESOLVER_API_URL),
                properties.loadString(DefinedProperties.RESOLVER_LOGIN),
                properties.loadString(DefinedProperties.RESOLVER_PASSWORD)));
        adapter.setRegistrationMode(RegistrationMode.BY_REGISTRAR);
        adapter.setRegistrarCode(properties.loadString(DefinedProperties.RESOLVER_REGISTRAR_CODE));
        //oai harvester
        adapter.setOaiBaseUrl(properties.loadString(DefinedProperties.OAI_BASE_URL));
        adapter.setMetadataPrefix(properties.loadString(DefinedProperties.OAI_METADATA_PREFIX));
        adapter.setSetSpec(properties.loadStringOrNull(DefinedProperties.OAI_SET));
        //xsl
        adapter.setMetadataToImportTemplate(XmlTools.loadXmlFromFile(properties.loadString(DefinedProperties.DD_STYLESHEET)));
        adapter.setMetadataToDigitalInstanceTemplate(XmlTools.loadXmlFromFile(properties.loadString(DefinedProperties.DI_STYLESHEET)));
        initReportStream(adapter, properties);
        return adapter;
    }

    private static void initReportStream(OaiAdapter adapter, PropertyLoader properties) throws Exception {
        try {
            File reportFile = properties.loadFile(DefinedProperties.REPORT_FILE, false);
            adapter.setOutputStream(new FileOutputStream(reportFile));
        } catch (FileNotFoundException ex) {
            throw new Exception("Cannot open report file for writing", ex);
        }
    }
}