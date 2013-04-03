/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.webcommon.config;

import cz.nkp.urnnbn.config.PropertyKeys;
import cz.nkp.urnnbn.core.AdminLogger;
import cz.nkp.urnnbn.core.CountryCode;
import cz.nkp.urnnbn.core.persistence.impl.DatabaseConnectorFactory;
import cz.nkp.urnnbn.services.Services;
import cz.nkp.urnnbn.utils.PropertyLoader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Řehánek
 */
public abstract class ApplicationConfiguration {

    private static final Logger appLogger = Logger.getLogger(ApplicationConfiguration.class.getName());
    private Boolean serverReadOnly;
    private Boolean develMode;
    private String languageCode;
    private String adminName;
    private String adminEmail;

    public void initialize(String webAppName, PropertyLoader loader) throws IOException {
        appLogger.fine("Loading configuration");
        serverReadOnly = loader.loadBoolean(PropertyKeys.SERVER_READ_ONLY);
        develMode = loader.loadBoolean(PropertyKeys.DEVEL);
        languageCode = loader.loadString(PropertyKeys.LANGUAGE_CODE);
        adminName = loader.loadStringOrNull(PropertyKeys.ADMIN_NAME);
        adminEmail = loader.loadStringOrNull(PropertyKeys.ADMIN_EMAIL);
        String adminLogFile = loader.loadString(PropertyKeys.ADMIN_LOG_FILE);
        appLogger.log(Level.INFO, "initializing admin logger to file {0}", adminLogFile);
        AdminLogger.initializeLogger(webAppName, adminLogFile);
        CountryCode.initialize(languageCode);
        if (develMode) {
            Services.init(DatabaseConnectorFactory.getDevelConnector());
        } else {
            Services.init(DatabaseConnectorFactory.getJndiPoolledConnector());
        }
    }

    public Boolean isServerReadOnly() {
        return serverReadOnly;
    }

    public Boolean isDevelMode() {
        return develMode;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public String getAdminName() {
        return adminName;
    }

    public String getAdminEmail() {
        return adminEmail;
    }
}
