/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.webcommon.config;

import cz.nkp.urnnbn.api_client.v5.utils.XmlTools;
import cz.nkp.urnnbn.config.PropertyKeys;
import cz.nkp.urnnbn.core.AdminLogger;
import cz.nkp.urnnbn.core.CountryCode;
import cz.nkp.urnnbn.core.persistence.impl.DatabaseConnectorFactory;
import cz.nkp.urnnbn.services.Services;
import cz.nkp.urnnbn.solr_indexer.IndexerConfig;
import cz.nkp.urnnbn.utils.PropertyLoader;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Martin Řehánek
 */
public abstract class ApplicationConfiguration {

    private static final Logger appLogger = Logger.getLogger(ApplicationConfiguration.class.getName());
    private static final String CZIDL_TO_SOLR_XSLT = "czidlo-to-solr.xslt";
    private Boolean serverReadOnly;
    private Boolean develMode;
    private String languageCode;
    private String adminName;
    private String adminEmail;

    public void initialize(String webAppName, PropertyLoader loader) throws IOException {
        appLogger.fine("Loading configuration");
        serverReadOnly = loader.loadBoolean(PropertyKeys.SERVER_READ_ONLY);
        develMode = loader.loadBoolean(PropertyKeys.DEVEL);
        languageCode = loader.loadString(PropertyKeys.LANGUAGE_CODE);
        adminName = loader.loadStringOrNull(PropertyKeys.ADMIN_NAME);
        adminEmail = loader.loadStringOrNull(PropertyKeys.ADMIN_EMAIL);
        String adminLogFile = loader.loadString(PropertyKeys.ADMIN_LOG_FILE);
        appLogger.log(Level.INFO, "initializing admin logger to file {0}", adminLogFile);
        AdminLogger.initializeLogger(webAppName, adminLogFile);
        CountryCode.initialize(languageCode);

        IndexerConfig indexerConfig = new IndexerConfig();
        indexerConfig.setCzidloApiBaseUrl(loader.loadString(PropertyKeys.INDEXER_CZIDLO_API_BASE_URL));
        indexerConfig.setCzidloApiUseHttps(false);
        indexerConfig.setSolrApiBaseUrl(loader.loadString(PropertyKeys.INDEXER_SOLR_BASE_URL));
        indexerConfig.setSolrApiCollection(loader.loadString(PropertyKeys.INDEXER_SOLR_COLLECTION));
        indexerConfig.setSolrApiUseHttps(loader.loadBoolean(PropertyKeys.INDEXER_SOLR_USE_HTTPS));
        indexerConfig.setSolrApiLogin(loader.loadString(PropertyKeys.INDEXER_SOLR_LOGIN));
        indexerConfig.setSolrApiPassword(loader.loadString(PropertyKeys.INDEXER_SOLR_PASSWORD));

        try {
            indexerConfig.setCzidloToSolrXsltFile(new File(getClass().getClassLoader().getResource(CZIDL_TO_SOLR_XSLT).toURI()));
            indexerConfig.setCzidloToSolrXslt(XmlTools.loadXmlFromFile(indexerConfig.getCzidloToSolrXsltFile().getAbsolutePath()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (develMode) {
            Services.init(DatabaseConnectorFactory.getDevelConnector(), indexerConfig);
        } else {
            Services.init(DatabaseConnectorFactory.getJndiPoolledConnector(), indexerConfig);
        }
    }

    public Boolean isServerReadOnly() {
        return serverReadOnly;
    }

    public Boolean isDevelMode() {
        return develMode;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public String getAdminName() {
        return adminName;
    }

    public String getAdminEmail() {
        return adminEmail;
    }
}
