/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.config;

import java.io.InputStream;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import cz.nkp.urnnbn.utils.PropertyLoader;
import cz.nkp.urnnbn.webcommon.config.ResourceUtilizer;
import cz.nkp.urnnbn.xml.config.XmlModuleConfiguration;

/**
 * @author Martin Řehánek
 */
public class ContextListener implements ServletContextListener {

    private static final String WEB_APP_NAME = "API";
    private static final Logger logger = Logger.getLogger(ContextListener.class.getName());
    private static final String PROPERTIES_FILE = "api.properties";
    // API v6
    private static final String REGISTER_DD_XSD_V6 = "v6/registerDigitalDocument.xsd";
    private static final String IMPORT_DI_XSD_V6 = "v6/importDigitalInstance.xsd";
    private static final String API_RESPONSE_V6 = "v6/response.xsd";
    // API v5
    private static final String REGISTER_DD_XSD_V5 = "v5/registerDigitalDocument.xsd";
    private static final String IMPORT_DI_XSD_V5 = "v5/importDigitalInstance.xsd";
    private static final String API_RESPONSE_V5 = "v5/response.xsd";
    // API v4
    private static final String REGISTER_DD_XSD_V4 = "v4/registerDigitalDocument.xsd";
    private static final String IMPORT_DI_XSD_V4 = "v4/importDigitalInstance.xsd";
    private static final String API_RESPONSE_V4 = "v4/response.xsd";
    // API v3
    private static final String REGISTER_DD_XSD_V3 = "v3/registerDigitalDocument.xsd";
    private static final String IMPORT_DI_XSD_V3 = "v3/importDigitalInstance.xsd";
    private static final String API_RESPONSE_V3 = "v3/response.xsd";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        loadPropertiesFile();
        loadApiV6Resources();
        loadApiV5Resources();
        loadApiV4Resources();
        loadApiV3Resources();
    }

    private void loadPropertiesFile() {
        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                PropertyLoader loader = new PropertyLoader(in);
                ApiModuleConfiguration.instanceOf().initialize(WEB_APP_NAME, loader);
                XmlModuleConfiguration.instanceOf().initialize(loader);
            }
        }.run(PROPERTIES_FILE);
    }

    // API v6
    private void loadApiV6Resources() {
        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initDigDocRegistrationXsdV6(in);
            }
        }.run(REGISTER_DD_XSD_V6);

        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initDigInstImportXsdV6(in);
            }
        }.run(IMPORT_DI_XSD_V6);

        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initResponseV6Xsd(in);
            }
        }.run(API_RESPONSE_V6);
    }

    // API v5
    private void loadApiV5Resources() {
        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initDigDocRegistrationXsdV5(in);
            }
        }.run(REGISTER_DD_XSD_V5);

        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initDigInstImportXsdV5(in);
            }
        }.run(IMPORT_DI_XSD_V5);

        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initResponseV5Xsd(in);
            }
        }.run(API_RESPONSE_V5);
    }

    // API v4
    private void loadApiV4Resources() {
        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initDigDocRegistrationXsdV4(in);
            }
        }.run(REGISTER_DD_XSD_V4);

        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initDigInstImportXsdV4(in);
            }
        }.run(IMPORT_DI_XSD_V4);

        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initResponseV4Xsd(in);
            }
        }.run(API_RESPONSE_V4);
    }

    // API v3
    private void loadApiV3Resources() {
        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initDigDocRegistrationXsdV3(in);
            }
        }.run(REGISTER_DD_XSD_V3);

        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initDigInstImportXsdV3(in);
            }
        }.run(IMPORT_DI_XSD_V3);

        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initResponseV3Xsd(in);
            }
        }.run(API_RESPONSE_V3);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // nothing
    }
}
