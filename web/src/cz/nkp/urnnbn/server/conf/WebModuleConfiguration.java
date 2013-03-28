package cz.nkp.urnnbn.server.conf;

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

	static public WebModuleConfiguration instanceOf() {
		if (instance == null) {
			instance = new WebModuleConfiguration();
		}
		return instance;
	}

	@Override
	public void initialize(PropertyLoader loader) throws IOException {
		super.initialize(loader);
		logger.info("Loading configuration");
		showAlephLinks = loader.loadBooleanFalseIfNullOrEmpty(PropertyKeys.SHOW_ALEPH_LINKS);
		alephUrl = loader.loadStringOrNull(PropertyKeys.ALEPH_URL);
		alephBase = loader.loadStringOrNull(PropertyKeys.ALEPH_BASE);
		Configuration.init(loader);
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

	public ConfigurationData toConfigurationData() {
		ConfigurationData result = new ConfigurationData();
		result.setShowAlephLinks(showAlephLinks);
		result.setAlephBase(alephBase);
		result.setAlephUrl(alephUrl);
		result.setCountryCode(getLanguageCode());
		return result;
	}
}
