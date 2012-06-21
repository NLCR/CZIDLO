package cz.nkp.urnnbn.oaiadapter.cli;

import cz.nkp.urnnbn.oaiadapter.OaiAdapter;
import cz.nkp.urnnbn.utils.PropertyLoader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;

/**
 * Main class
 *
 */
public class App {

    private static final String USAGE = "USAGE: java -jar oaiAdapter.jar $CONFIGURATION_FILE";

    public static void main(String[] args) {
        try {
            //for testing - comment when commiting changes
            //args = new String[]{"/home/rehan/tmp/oaiAdapter/oaiAdapter.properties"};
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
        adapter.setLogin(properties.loadString(DefinedProperties.LOGIN));
        adapter.setPassword(properties.loadString(DefinedProperties.PASSWORD));
        adapter.setOaiBaseUrl(properties.loadString(DefinedProperties.BASE_URL));
        adapter.setMetadataPrefix(properties.loadString(DefinedProperties.METADATA_PREFIX));
        adapter.setSetSpec(properties.loadString(DefinedProperties.SET));
        adapter.setRegistrarCode(properties.loadString(DefinedProperties.REGISTRAR_CODE));

        adapter.setMetadataToImportTemplate(loadStylesheet(properties.loadString(DefinedProperties.DD_STYLESHEET)));
        adapter.setMetadataToDigitalInstanceTemplate(loadStylesheet(properties.loadString(DefinedProperties.DI_STYLESHEET)));
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

    private static String loadStylesheet(String xsltFile) throws Exception {
        try {
            Builder builder = new Builder();
            Document importStylesheet = builder.build(xsltFile);
            return importStylesheet.toXML();
        } catch (ParsingException ex) {
            throw new Exception("error parsing " + xsltFile, ex);
        } catch (IOException ex) {
            throw new Exception("error loading " + xsltFile, ex);
        }
    }
}
