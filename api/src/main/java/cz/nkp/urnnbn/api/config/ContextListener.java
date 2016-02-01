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

    private static final String WEB_APP_NAME = "API";
    private static final Logger logger = Logger.getLogger(ContextListener.class.getName());
    private static final String PROPERTIES_FILE = "api.properties";
    // API v3
    private static final String REGISTER_DD_XSD_V3 = "v3/registerDigitalDocument.xsd";
    private static final String IMPORT_DI_XSD_V3 = "v3/importDigitalInstance.xsd";
    private static final String API_RESPONSE_V3 = "v3/response.xsd";
    // API v2
    private static final String REGISTER_DD_XSD_V2 = "v2/request/registerDigitalDocument.xsd";
    private static final String IMPORT_DI_XSD_V2 = "v2/request/importDigitalInstance.xsd";
    // API V2 requests -> API V3 requests transformations
    private static final String REGISTER_DIG_DOC_V2_TO_V3_XSLT = "v2/request/digDocRegistrationV2ToV3.xsl";
    private static final String IMPORT_DI_V2_TO_V3_XSLT = "v2/request/digInstImportV2ToV3.xsl";
    // API V3 responses -> API V2 responses transformations
    private static final String ERROR_V3_TO_V2_XSLT = "v2/response/v3ToV2Transformation/error.xsl";
    private static final String DEACTIVATE_DIG_INST_V3_TO_V2_XSLT = "v2/response/v3ToV2Transformation/deactivateDigitalInstance.xsl";
    private static final String DELETE_REG_SCOPE_ID_V3_TO_V2_XSLT = "v2/response/v3ToV2Transformation/deleteRegistrarScopeIdentifier.xsl";
    private static final String DELETE_REG_SCOPE_IDS_V3_TO_V2_XSLT = "v2/response/v3ToV2Transformation/deleteRegistrarScopeIdentifiers.xsl";
    private static final String GET_DIG_DOC_V3_TO_V2_XSLT = "v2/response/v3ToV2Transformation/getDigitalDocument.xsl";
    private static final String GET_DIG_DOCS_V3_TO_V2_XSLT = "v2/response/v3ToV2Transformation/getDigitalDocuments.xsl";
    private static final String GET_DIG_INST_V3_TO_V2_XSLT = "v2/response/v3ToV2Transformation/getDigitalInstance.xsl";
    private static final String GET_DIG_INSTS_V3_TO_V2_XSLT = "v2/response/v3ToV2Transformation/getDigitalInstances.xsl";
    private static final String GET_REGISTRAR_V3_TO_V2_XSLT = "v2/response/v3ToV2Transformation/getRegistrar.xsl";
    private static final String GET_REGISTRARS_V3_TO_V2_XSLT = "v2/response/v3ToV2Transformation/getRegistrars.xsl";
    private static final String GET_REG_SCOPE_ID_V3_TO_V2_XSLT = "v2/response/v3ToV2Transformation/getRegistrarScopeIdentifierValue.xsl";
    private static final String GET_REG_SCOPE_IDS_V3_TO_V2_XSLT = "v2/response/v3ToV2Transformation/getRegistrarScopeIdentifiers.xsl";
    private static final String GET_URN_NBN_V3_TO_V2_XSLT = "v2/response/v3ToV2Transformation/getUrnNbn.xsl";
    private static final String GET_URN_NBN_RESERVATIONS_V3_TO_V2_XSLT = "v2/response/v3ToV2Transformation/getUrnNbnReservations.xsl";
    private static final String IMPORT_DI_V3_TO_V2_XSLT = "v2/response/v3ToV2Transformation/importDigitalInstance.xsl";
    private static final String REGISTER_DIG_DOC_V3_TO_V2_XSLT = "v2/response/v3ToV2Transformation/registerDigitalDocument.xsl";
    private static final String RESERVE_URN_NBN_V3_TO_V2_XSLT = "v2/response/v3ToV2Transformation/reserveUrnNbnBlock.xsl";
    private static final String SET_OR_UPDATE_REG_SCOPE_ID_V3_TO_V2_XSLT = "v2/response/v3ToV2Transformation/setOrUpdateRegistrarScopeIdentifier.xsl";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        loadPropertiesFile();
        loadApiV3Resources();
        loadApiV2Resources();
        loadApiV2RequestsToApiV3RequestTransformers();
        loadApiV3ResponsesToApiV2ResponsesTransformers();
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

    private void loadApiV2Resources() {
        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initDigDocRegistrationXsdV2(in);
            }
        }.run(REGISTER_DD_XSD_V2);

        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initDigInstImportXsdV2(in);
            }
        }.run(IMPORT_DI_XSD_V2);
    }

    private void loadApiV2RequestsToApiV3RequestTransformers() {
        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initDigDocRegistrationV2ToV3DataTransformer(in);
            }
        }.run(REGISTER_DIG_DOC_V2_TO_V3_XSLT);

        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initDigInstImportV2ToV3DataTransformer(in);
            }
        }.run(IMPORT_DI_V2_TO_V3_XSLT);
    }

    private void loadApiV3ResponsesToApiV2ResponsesTransformers() {
        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initErrorResponseV3ToV2Transformer(in);
            }
        }.run(ERROR_V3_TO_V2_XSLT);

        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initDeactivateDigInstResponseV3ToV2Transformer(in);
            }
        }.run(DEACTIVATE_DIG_INST_V3_TO_V2_XSLT);

        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initDeleteRegScopeIdResponseV3ToV2Transformer(in);
            }
        }.run(DELETE_REG_SCOPE_ID_V3_TO_V2_XSLT);

        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initDeleteRegScopeIdsResponseV3ToV2Transformer(in);
            }
        }.run(DELETE_REG_SCOPE_IDS_V3_TO_V2_XSLT);

        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initGetDigDocResponseV3ToV2Transformer(in);
            }
        }.run(GET_DIG_DOC_V3_TO_V2_XSLT);

        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initGetDigDocsResponseV3ToV2Transformer(in);
            }
        }.run(GET_DIG_DOCS_V3_TO_V2_XSLT);

        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initGetDigInstResponseV3ToV2Transformer(in);
            }
        }.run(GET_DIG_INST_V3_TO_V2_XSLT);

        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initGetDigInstsResponseV3ToV2Transformer(in);
            }
        }.run(GET_DIG_INSTS_V3_TO_V2_XSLT);

        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initGetRegistrarResponseV3ToV2Transformer(in);
            }
        }.run(GET_REGISTRAR_V3_TO_V2_XSLT);

        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initGetRegistrarsResponseV3ToV2Transformer(in);
            }
        }.run(GET_REGISTRARS_V3_TO_V2_XSLT);

        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initGetRegScopeIdResponseV3ToV2Transformer(in);
            }
        }.run(GET_REG_SCOPE_ID_V3_TO_V2_XSLT);

        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initGetRegScopeIdsResponseV3ToV2Transformer(in);
            }
        }.run(GET_REG_SCOPE_IDS_V3_TO_V2_XSLT);

        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initGetUrnNbnResponseV3ToV2Transformer(in);
            }
        }.run(GET_URN_NBN_V3_TO_V2_XSLT);

        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initGetUrnNbnReservationsResponseV3ToV2Transformer(in);
            }
        }.run(GET_URN_NBN_RESERVATIONS_V3_TO_V2_XSLT);

        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initRegisterDigDocResponseV3ToV2Transformer(in);
            }
        }.run(REGISTER_DIG_DOC_V3_TO_V2_XSLT);

        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initImportDigitalInstanceResponseV3ToV2Transformer(in);
            }
        }.run(IMPORT_DI_V3_TO_V2_XSLT);

        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initReserveUrnNbnResponseV3ToV2Transformer(in);
            }
        }.run(RESERVE_URN_NBN_V3_TO_V2_XSLT);

        new ResourceUtilizer(logger) {
            @Override
            public void processResource(InputStream in) throws Exception {
                ApiModuleConfiguration.instanceOf().initSetOrUpdateRegScopeIdResponseV3ToV2Transformer(in);
            }
        }.run(SET_OR_UPDATE_REG_SCOPE_ID_V3_TO_V2_XSLT);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // nothing
    }
}
