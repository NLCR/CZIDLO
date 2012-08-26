package cz.nkp.urnnbn.server.services;

import cz.nkp.urnnbn.client.services.ConfigurationService;
import cz.nkp.urnnbn.server.conf.WebModuleConfiguration;
import cz.nkp.urnnbn.shared.ConfigurationData;

public class ConfigurationServiceImpl extends AbstractService implements ConfigurationService {

	private static final long serialVersionUID = 6647594318575469246L;

	@Override
	public ConfigurationData getConfiguration() {
		return WebModuleConfiguration.instanceOf().toConfigurationData();
	}

}
