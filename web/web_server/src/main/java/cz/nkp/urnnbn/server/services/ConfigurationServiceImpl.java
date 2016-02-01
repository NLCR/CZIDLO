package cz.nkp.urnnbn.server.services;

import java.util.logging.Level;
import java.util.logging.Logger;

import cz.nkp.urnnbn.client.services.ConfigurationService;
import cz.nkp.urnnbn.server.conf.WebModuleConfiguration;
import cz.nkp.urnnbn.shared.ConfigurationData;
import cz.nkp.urnnbn.shared.exceptions.ServerException;

public class ConfigurationServiceImpl extends AbstractService implements ConfigurationService {

    private static final long serialVersionUID = 6647594318575469246L;
    private static final Logger logger = Logger.getLogger(ConfigurationServiceImpl.class.getName());

    @Override
    public ConfigurationData getConfiguration() throws ServerException {
        try {
            return WebModuleConfiguration.instanceOf().toConfigurationData();
        } catch (Throwable e) {
            logger.log(Level.SEVERE, null, e);
            throw new ServerException(e.getMessage());
        }
    }

}
