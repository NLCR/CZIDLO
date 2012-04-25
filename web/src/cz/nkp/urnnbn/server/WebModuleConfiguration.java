package cz.nkp.urnnbn.server;

import java.io.IOException;
import java.util.logging.Logger;

import cz.nkp.urnnbn.utils.PropertyLoader;
import cz.nkp.urnnbn.webcommon.config.ApplicationConfiguration;

/**
 * 
 * @author Martin Řehánek
 */
public class WebModuleConfiguration extends ApplicationConfiguration {

	private static final Logger logger = Logger.getLogger(WebModuleConfiguration.class.getName());

	@Override
	public void initialize(PropertyLoader loader) throws IOException {
		super.initialize(loader);
		logger.info("Loading configuration");
	}
}
