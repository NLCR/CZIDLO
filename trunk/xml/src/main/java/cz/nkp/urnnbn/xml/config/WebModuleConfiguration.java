/*
 * Copyright (C) 2012 Martin Řehánek
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
package cz.nkp.urnnbn.xml.config;

import cz.nkp.urnnbn.utils.PropertyLoader;
import cz.nkp.urnnbn.webcommon.config.ApplicationConfiguration;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Martin Řehánek
 */
public class WebModuleConfiguration extends ApplicationConfiguration {

    private static final Logger logger = Logger.getLogger(WebModuleConfiguration.class.getName());
    private static WebModuleConfiguration instance = null;
    private String responseXsdLocation;

    static public WebModuleConfiguration instanceOf() {
        if (instance == null) {
            instance = new WebModuleConfiguration();
        }
        return instance;
    }

    /**
     *
     * @param properties InputStream containing properties
     * @throws IOException
     */
    @Override
    public void initialize(PropertyLoader loader) throws IOException {
        super.initialize(loader);
        logger.log(Level.INFO, "Initializing {0}", WebModuleConfiguration.class.getName());
        responseXsdLocation = loader.loadString(PropertyKeys.RENSPONSE_XSD_LOCATION);
    }

    public String getResponseXsdLocation() {
        return responseXsdLocation;
    }
}