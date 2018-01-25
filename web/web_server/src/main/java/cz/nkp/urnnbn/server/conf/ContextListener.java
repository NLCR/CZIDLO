package cz.nkp.urnnbn.server.conf;

import cz.nkp.urnnbn.utils.PropertyLoader;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Martin Řehánek
 */
public class ContextListener implements ServletContextListener {

    private static final String WEB_APP_NAME = "WEB";
    private static final Logger logger = Logger.getLogger(ServletContextListener.class.getName());
    private static final String WEB_PROPERTIES = "web.properties";
    private static final String CZIDL_TO_SOLR_XSLT = "czidlo-to-solr.xslt";

    abstract class ResourceUtilizer {

        abstract void processResource(InputStream in) throws Exception;

        final void run(String resourceName) {
            InputStream data = loadResource(resourceName);
            try {
                processResource(data);
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Error processing resource " + resourceName, ex);
            }
        }

        private InputStream loadResource(String resourceName) {
            InputStream in = getClass().getClassLoader().getResourceAsStream(resourceName);
            if (in == null) {
                throw new RuntimeException("Cannot find resource " + resourceName);
            } else {
                logger.log(Level.INFO, "Found resource {0}", resourceName);
                return in;
            }
        }
    }

    public void contextInitialized(ServletContextEvent sce) {
        loadWebProperties();
        loadFileResources();
    }

    private void loadFileResources() {
        try {
            WebModuleConfiguration.instanceOf().initializeFileResources(WEB_APP_NAME, new File(getClass().getClassLoader().getResource(CZIDL_TO_SOLR_XSLT).toURI()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadWebProperties() {
        new ResourceUtilizer() {

            @Override
            void processResource(InputStream in) throws Exception {
                WebModuleConfiguration.instanceOf().initialize(WEB_APP_NAME, new PropertyLoader(in));
            }
        }.run(WEB_PROPERTIES);
    }

    public void contextDestroyed(ServletContextEvent sce) {
        // nothing
    }
}
