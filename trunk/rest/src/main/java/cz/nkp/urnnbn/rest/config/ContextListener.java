/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest.config;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author Martin Řehánek
 */
public class ContextListener implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(ServletContextListener.class.getName());
    private static final String API_PROPERTIES = "api.properties";
    private static final String RECORD_IMPORT_XSD = "import.xsd";

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

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        loadApiProperties();
        loadRecordImportXsd();
    }

    private void loadApiProperties() {
        new ResourceUtilizer() {

            @Override
            void processResource(InputStream in) throws Exception {
                ApiConfiguration.instanceOf().initialize(in);
            }
        }.run(API_PROPERTIES);
    }

    private void loadRecordImportXsd() {
        new ResourceUtilizer() {

            @Override
            void processResource(InputStream in) throws Exception {
                ApiConfiguration.instanceOf().initRecordImportSchema(in);
            }
        }.run(RECORD_IMPORT_XSD);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        //nothing
    }
}
