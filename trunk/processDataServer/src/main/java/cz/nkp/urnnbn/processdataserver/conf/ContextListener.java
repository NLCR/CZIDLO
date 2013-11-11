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

import java.io.InputStream;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import cz.nkp.urnnbn.utils.PropertyLoader;
import cz.nkp.urnnbn.webcommon.config.ResourceUtilizer;

/**
 *
 * @author Martin Řehánek
 */
public class ContextListener implements ServletContextListener {

    private static final String WEB_APP_NAME = "processDataServer";
    private static final Logger logger = Logger.getLogger(ServletContextListener.class.getName());
    private static final String PROPERTIES_FILE = "processDataServer.properties";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        loadPropertiesFile();
    }

    private void loadPropertiesFile() {
        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                PropertyLoader loader = new PropertyLoader(in);
                ProcessDataServerConfiguration.instanceOf().initialize(WEB_APP_NAME, loader);
            }
        }.run(PROPERTIES_FILE);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        //nothing so far
    }
}
