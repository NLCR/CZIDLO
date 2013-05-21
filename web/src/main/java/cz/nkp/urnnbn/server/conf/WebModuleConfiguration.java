package cz.nkp.urnnbn.server.conf;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;

import cz.nkp.urnnbn.processmanager.conf.Configuration;
import cz.nkp.urnnbn.shared.ConfigurationData;
import cz.nkp.urnnbn.utils.PropertyLoader;
import cz.nkp.urnnbn.webcommon.config.ApplicationConfiguration;

/**
 * 
 * @author Martin Řehánek
 */
public class WebModuleConfiguration extends ApplicationConfiguration {

	private static final Logger logger = Logger.getLogger(WebModuleConfiguration.class.getName());
	private static WebModuleConfiguration instance = null;
	private boolean showAlephLinks;
	private String alephUrl;
	private String alephBase;
	private String rulesTabContent;
	private String infoTabContent;

	static public WebModuleConfiguration instanceOf() {
		if (instance == null) {
			instance = new WebModuleConfiguration();
		}
		return instance;
	}

	@Override
	public void initialize(String appName, PropertyLoader loader) throws IOException {
		logger.info("Loading configuration of module " + appName);
		super.initialize(appName, loader);
		showAlephLinks = loader.loadBooleanFalseIfNullOrEmpty(PropertyKeys.SHOW_ALEPH_LINKS);
		alephUrl = loader.loadStringOrNull(PropertyKeys.ALEPH_URL);
		alephBase = loader.loadStringOrNull(PropertyKeys.ALEPH_BASE);
		Configuration.init(loader);
	}

	public void loadInfoTabContent(BufferedReader in) throws IOException {
		StringBuilder builder = new StringBuilder();
		while (in.ready()) {
			builder.append(in.readLine()).append('\n');
		}
		infoTabContent = builder.toString();
	}

	public void loadRulesTabContent(BufferedReader in) throws IOException {
		StringBuilder builder = new StringBuilder();
		while (in.ready()) {
			builder.append(in.readLine()).append('\n');
		}
		rulesTabContent = builder.toString();
	}

	public static Logger getLogger() {
		return logger;
	}

	public boolean isShowAlephLinks() {
		return showAlephLinks;
	}

	public String getAlephUrl() {
		return alephUrl;
	}

	public String getAlephBase() {
		return alephBase;
	}

	public String getInfoTabContent() {
		return infoTabContent;
	}

	public String getRulesTabContent() {
		return rulesTabContent;
	}

	public ConfigurationData toConfigurationData() {
		ConfigurationData result = new ConfigurationData();
		result.setShowAlephLinks(showAlephLinks);
		result.setAlephBase(alephBase);
		result.setAlephUrl(alephUrl);
		result.setCountryCode(getLanguageCode());
		return result;
	}

}
