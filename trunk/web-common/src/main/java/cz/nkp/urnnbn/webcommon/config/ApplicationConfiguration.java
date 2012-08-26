/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.webcommon.config;

import cz.nkp.urnnbn.core.CountryCode;
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
    private String languageCode;
    private String adminName;
    private String adminEmail;

    static public ApplicationConfiguration instanceOf() {
        if (instance == null) {
            instance = new ApplicationConfiguration();
        }
        return instance;
    }

    public void initialize(PropertyLoader loader) throws IOException {
        appLogger.fine("Loading configuration");
        serverReadOnly = loader.loadBoolean(PropertyKeys.SERVER_READ_ONLY);
        develMode = loader.loadBoolean(PropertyKeys.DEVEL);
        languageCode = loader.loadString(PropertyKeys.LANGUAGE_CODE);
        adminName = loader.loadStringOrNull(PropertyKeys.ADMIN_NAME);
        adminEmail = loader.loadStringOrNull(PropertyKeys.ADMIN_EMAIL);
        CountryCode.initialize(languageCode);
        Services.init(develMode);
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
