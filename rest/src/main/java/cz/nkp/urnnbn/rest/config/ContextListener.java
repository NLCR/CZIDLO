/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.rest.config;

import cz.nkp.urnnbn.utils.PropertyLoader;
import cz.nkp.urnnbn.webcommon.config.ResourceUtilizer;
import cz.nkp.urnnbn.xml.config.WebModuleConfiguration;
import java.io.InputStream;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 * @author Martin Řehánek
 */
public class ContextListener implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(ContextListener.class.getName());
    private static final String PROPERTIES_FILE = "api.properties";
    private static final String RECORD_IMPORT_XSD = "importRecord.xsd";
    private static final String INSTANCE_IMPORT_XSD = "importDigitalInstance.xsd";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        loadPropertiesFile();
        loadRecordImportXsd();
        loadInstanceImportXsd();
    }

    private void loadPropertiesFile() {
        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                PropertyLoader loader = new PropertyLoader(in);
                ApiModuleConfiguration.instanceOf().initialize(loader);
                WebModuleConfiguration.instanceOf().initialize(loader);
            }
        }.run(PROPERTIES_FILE);
    }

    private void loadRecordImportXsd() {
        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initRecordImportSchema(in);
            }
        }.run(RECORD_IMPORT_XSD);
    }

    private void loadInstanceImportXsd() {
        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initInstanceImportSchema(in);
            }
        }.run(INSTANCE_IMPORT_XSD);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        //nothing
    }
}
