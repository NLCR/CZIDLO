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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.nkp.urnnbn.utils.PropertyLoader;

/**
 * @author Martin Řehánek
 */
public class XmlModuleConfiguration {

    private static final Logger logger = Logger.getLogger(XmlModuleConfiguration.class.getName());
    private static XmlModuleConfiguration instance = null;
    private String apiV3ResponseXsdLocation;
    private String apiV4ResponseXsdLocation;
    private String apiV5ResponseXsdLocation;
    private String apiV6ResponseXsdLocation;

    static public XmlModuleConfiguration instanceOf() {
        if (instance == null) {
            instance = new XmlModuleConfiguration();
        }
        return instance;
    }

    /**
     * @param loader
     * @throws IOException
     */
    public void initialize(PropertyLoader loader) throws IOException {
        logger.log(Level.INFO, "Initializing {0}", XmlModuleConfiguration.class.getName());
        apiV3ResponseXsdLocation = loader.loadString(PropertyKeys.API_V3_RESPONSE_XSD_LOCATION);
        logger.log(Level.INFO, "APIv3 response.xsd location: {0}", apiV3ResponseXsdLocation);
        apiV4ResponseXsdLocation = loader.loadString(PropertyKeys.API_V4_RESPONSE_XSD_LOCATION);
        logger.log(Level.INFO, "APIv4 response.xsd location: {0}", apiV4ResponseXsdLocation);
        apiV5ResponseXsdLocation = loader.loadString(PropertyKeys.API_V5_RESPONSE_XSD_LOCATION);
        logger.log(Level.INFO, "APIv5 response.xsd location: {0}", apiV5ResponseXsdLocation);
        apiV6ResponseXsdLocation = loader.loadString(PropertyKeys.API_V6_RESPONSE_XSD_LOCATION);
        logger.log(Level.INFO, "APIv6 response.xsd location: {0}", apiV6ResponseXsdLocation);
    }

    public String getApiV3ResponseXsdLocation() {
        return apiV3ResponseXsdLocation;
    }

    public String getApiV4ResponseXsdLocation() {
        return apiV4ResponseXsdLocation;
    }

    public String getApiV5ResponseXsdLocation() {
        return apiV5ResponseXsdLocation;
    }

    public String getapiv6ResponseXsdLocation() {
        return apiV6ResponseXsdLocation;
    }
}
