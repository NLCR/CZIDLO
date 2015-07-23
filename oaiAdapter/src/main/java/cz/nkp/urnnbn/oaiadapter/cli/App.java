package cz.nkp.urnnbn.oaiadapter.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.nkp.urnnbn.core.UrnNbnRegistrationMode;
import cz.nkp.urnnbn.oaiadapter.OaiAdapter;
import cz.nkp.urnnbn.oaiadapter.XsdProvider;
import cz.nkp.urnnbn.oaiadapter.czidlo.Credentials;
import cz.nkp.urnnbn.oaiadapter.czidlo.CzidloApiConnector;
import cz.nkp.urnnbn.oaiadapter.utils.XmlTools;
import cz.nkp.urnnbn.utils.PropertyLoader;

/**
 * Main class
 * 
 */
public class App {

	private static final String USAGE = "USAGE: java -jar oaiAdapter.jar $CONFIGURATION_FILE";

	public static void main(String[] args) {
		try {
			// for testing - comment out before commiting changes
			// args = new String[] {
			// "/home/martin/zakazky/czidlo2015/oaiAdapter-testovani/4.3/config-monographs.properties"
			// };
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
		// czidlo api
		Credentials credentials = new Credentials(properties.loadString(DefinedProperties.CZIDLO_API_LOGIN),
				properties.loadString(DefinedProperties.CZIDLO_API_PASSWORD));
		boolean ignoreInvalidCertificate = properties
				.loadBooleanFalseIfNullOrEmpty(DefinedProperties.CZIDLO_API_IGNORE_INVALID_CERTIFICATE);
		String czidloApiBaseUrl = properties.loadString(DefinedProperties.CZIDLO_API_BASE_URL);
		adapter.setCzidloConnector(new CzidloApiConnector(czidloApiBaseUrl, credentials, ignoreInvalidCertificate));
		adapter.setRegistrarCode(properties.loadString(DefinedProperties.CZIDLO_API_REGISTRAR_CODE));
		adapter.setRegistrationMode(UrnNbnRegistrationMode.valueOf(properties.loadString(DefinedProperties.CZIDLO_API_REGISTRATION_MODE)));
		// adapter.setRegistrationMode(UrnNbnRegistrationMode.valueOf(properties.loadString(DefinedProperties.CZIDLO_API_REGISTRATION_MODE)));

		// oai harvester
		adapter.setOaiBaseUrl(properties.loadString(DefinedProperties.OAI_BASE_URL));
		adapter.setMetadataPrefix(properties.loadString(DefinedProperties.OAI_METADATA_PREFIX));
		adapter.setSetSpec(properties.loadStringOrNull(DefinedProperties.OAI_SET));
		// xsl
		adapter.setMetadataToDigDocRegistrationTemplate(XmlTools.loadXmlFromFile(properties.loadString(DefinedProperties.DD_STYLESHEET)));
		adapter.setMetadataToDigInstImportTemplate(XmlTools.loadXmlFromFile(properties.loadString(DefinedProperties.DI_STYLESHEET)));
		// xsd for transformation results
		URL digDocRegistrationDataXsdUrl = properties.loadUrl(DefinedProperties.DD_REGISTRATION_XSD_URL);
		URL digitalInstanceImportDataXsdUrl = properties.loadUrl(DefinedProperties.DI_IMPORT_XSD_URL);
		adapter.setXsdProvider(new XsdProvider(digDocRegistrationDataXsdUrl, digitalInstanceImportDataXsdUrl));

		// report
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
