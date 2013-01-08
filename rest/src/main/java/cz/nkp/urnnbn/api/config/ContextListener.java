/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.api.config;

import cz.nkp.urnnbn.utils.PropertyLoader;
import cz.nkp.urnnbn.webcommon.config.ResourceUtilizer;
import cz.nkp.urnnbn.xml.config.XmlModuleConfiguration;
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
    private static final String REGISTER_DD_XSD_V2 = "v2/registerDigitalDocument.xsd";
    private static final String REGISTER_DD_V2_TO_V3_XSLT = "v2/digDocRegistrationV2ToV3.xsl";
    private static final String REGISTER_DD_XSD_V3 = "v3/registerDigitalDocument.xsd";
    private static final String IMPORT_DI_XSD_V2 = "v2/importDigitalInstance.xsd";
    private static final String IMPORT_DI_V2_TO_V3_XSLT = "v2/digInstImportV2ToV3.xsl";
    private static final String IMPORT_DI_XSD_V3 = "v3/importDigitalInstance.xsd";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        loadPropertiesFile();
        loadDigDocRegistrationResources();
        loadDigInstImportResources();
    }

    private void loadPropertiesFile() {
        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                PropertyLoader loader = new PropertyLoader(in);
                ApiModuleConfiguration.instanceOf().initialize(loader);
                XmlModuleConfiguration.instanceOf().initialize(loader);
            }
        }.run(PROPERTIES_FILE);
    }

    private void loadDigDocRegistrationResources() {
        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initDigDocRegistrationXsdV2(in);
            }
        }.run(REGISTER_DD_XSD_V2);

        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initDigDocRegistrationV2ToV3DataTransformer(in);
            }
        }.run(REGISTER_DD_V2_TO_V3_XSLT);

        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initDigDocRegistrationXsdV3(in);
            }
        }.run(REGISTER_DD_XSD_V3);
    }

    private void loadDigInstImportResources() {
        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initDigInstImportXsdV2(in);
            }
        }.run(IMPORT_DI_XSD_V2);

        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initDigInstImportV2ToV3DataTransformer(in);
            }
        }.run(IMPORT_DI_V2_TO_V3_XSLT);

        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initDigInstImportXsdV3(in);
            }
        }.run(IMPORT_DI_XSD_V3);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        //nothing
    }
}
