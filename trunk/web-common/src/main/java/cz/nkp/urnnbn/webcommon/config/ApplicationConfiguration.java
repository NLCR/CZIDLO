/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.webcommon.config;

import cz.nkp.urnnbn.services.Services;
import cz.nkp.urnnbn.utils.PropertyLoader;
import java.io.IOException;
import java.util.logging.Logger;

/**
 *
 * @author Martin Řehánek
 */
public class ApplicationConfiguration {
    private static final Logger appLogger = Logger.getLogger(ApplicationConfiguration.class.getName());
    private static ApplicationConfiguration instance = null;
    private Boolean serverReadOnly;
    private Boolean develMode;
    
   static public ApplicationConfiguration instanceOf() {
        if (instance == null) {
            instance = new ApplicationConfiguration();
        }
        return instance;
    }
    
    public void initialize(PropertyLoader loader) throws IOException {
        appLogger.info("Loading configuration");
        serverReadOnly = loader.loadBoolean(PropertyKeys.SERVER_READ_ONLY);
        develMode = loader.loadBooleanFalseIfNullOrEmpty(PropertyKeys.DEVEL);
        Services.init(develMode);
    }

    public Boolean isServerReadOnly(){
        return serverReadOnly;
    }

    public Boolean isDevelMode(){
        return develMode;
    }
}
