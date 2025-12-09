package cz.nkp.urnnbn.czidlo_web_api;

import cz.nkp.urnnbn.utils.PropertyLoader;
import cz.nkp.urnnbn.webcommon.config.ApplicationConfiguration;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WebApiModuleConfiguration extends ApplicationConfiguration {

    private static final Logger logger = Logger.getLogger(WebApiModuleConfiguration.class.getName());
    private static WebApiModuleConfiguration instance = null;
    private String webApiBaseUrl;

    static public WebApiModuleConfiguration instanceOf() {
        if (instance == null) {
            instance = new WebApiModuleConfiguration();
        }
        return instance;
    }

    @Override
    public void initialize(String appName, PropertyLoader loader) throws IOException {
        super.initialize(appName, loader);
        logger.log(Level.INFO, "Initializing {0}", appName);
        webApiBaseUrl = loader.loadString("web-api.baseUrl");
        cz.nkp.urnnbn.processmanager.conf.Configuration.init(loader); // initialize process manager configuration
    }

    public String getWebApiBaseUrl() {
        return webApiBaseUrl;
    }
}
