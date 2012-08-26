/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.conf;

import cz.nkp.urnnbn.utils.PropertyLoader;
import cz.nkp.urnnbn.webcommon.config.ResourceUtilizer;
import java.io.InputStream;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author Martin Řehánek
 */
public class ContextListener implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(ServletContextListener.class.getName());
    private static final String PROPERTIES_FILE = "provider.properties";
    private static final String RESOLVER_TO_OAIDC_XSLT = "resolverToOaiDc.xsd";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        loadPropertiesFile();
        loadResolverToOaidcXslt();
    }

    private void loadPropertiesFile() {
        new ResourceUtilizer(logger) {

            @Override
            public void processResource(InputStream in) throws Exception {
                OaiPmhConfiguration.instanceOf().initialize(new PropertyLoader(in));
            }
        }.run(PROPERTIES_FILE);
    }

    private void loadResolverToOaidcXslt() {
        new ResourceUtilizer(logger) {

            @Override
            public void processResource(InputStream in) throws Exception {
                OaiPmhConfiguration.instanceOf().initResolverToOaidcTemplate(in);
            }
        }.run(RESOLVER_TO_OAIDC_XSLT);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        //nothing
    }
}
