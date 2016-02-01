/*
 * Copyright (C) 2013 Martin Řehánek
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.nkp.urnnbn.processdataserver.conf;

import cz.nkp.urnnbn.config.PropertyKeys;
import cz.nkp.urnnbn.processmanager.conf.Configuration;
import cz.nkp.urnnbn.webcommon.config.ApplicationConfiguration;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Řehánek
 */
public class ProcessDataServerConfiguration extends ApplicationConfiguration {

    private static final Logger logger = Logger.getLogger(ProcessDataServerConfiguration.class.getName());
    private static ProcessDataServerConfiguration instance;
    private String adminLogFile;

    @Override
    public void initialize(String appName, cz.nkp.urnnbn.utils.PropertyLoader loader) throws IOException {
        // super.initialize(appName, loader);
        logger.log(Level.INFO, "Initializing {0}", ProcessDataServerConfiguration.class.getName());
        Configuration.init(loader);
        adminLogFile = loader.loadString(PropertyKeys.ADMIN_LOG_FILE);
    }

    public static ProcessDataServerConfiguration instanceOf() {
        if (instance == null) {
            instance = new ProcessDataServerConfiguration();
        }
        return instance;
    }

    public String getAdminLogFile() {
        return adminLogFile;
    }
}
