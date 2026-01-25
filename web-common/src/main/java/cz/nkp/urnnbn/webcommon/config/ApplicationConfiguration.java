/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.webcommon.config;

import cz.nkp.urnnbn.apiClient.v5.utils.XmlTools;
import cz.nkp.urnnbn.config.PropertyKeys;
import cz.nkp.urnnbn.core.AdminLogger;
import cz.nkp.urnnbn.core.CountryCode;
import cz.nkp.urnnbn.core.persistence.impl.DatabaseConnectorFactory;
import cz.nkp.urnnbn.services.Services;
import cz.nkp.urnnbn.indexer.IndexerConfig;
import cz.nkp.urnnbn.utils.PropertyLoader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Martin Řehánek
 */
public abstract class ApplicationConfiguration {

    private static final Logger appLogger = Logger.getLogger(ApplicationConfiguration.class.getName());
    private static final String CZIDLO_TO_SOLR_XSLT = "czidlo-to-solr.xslt";
    private Boolean serverReadOnly;
    private Boolean develMode;
    private String languageCode;
    private String adminName;
    private String adminEmail;
    private File adminLogFile;

    public void initialize(String webAppName, PropertyLoader loader) throws IOException {
        appLogger.fine("Loading configuration");
        serverReadOnly = loader.loadBoolean(PropertyKeys.SERVER_READ_ONLY);
        develMode = loader.loadBoolean(PropertyKeys.DEVEL);
        languageCode = loader.loadString(PropertyKeys.LANGUAGE_CODE);
        adminName = loader.loadStringOrNull(PropertyKeys.ADMIN_NAME);
        adminEmail = loader.loadStringOrNull(PropertyKeys.ADMIN_EMAIL);
        adminLogFile = new File(loader.loadString(PropertyKeys.ADMIN_LOG_FILE));
        appLogger.log(Level.INFO, "initializing admin logger to file {0}", adminLogFile);
        AdminLogger.initializeLogger(webAppName, adminLogFile);
        CountryCode.initialize(languageCode);

        boolean initIndexer = loader.loadBoolean("indexer.enabled", true);
        IndexerConfig indexerConfig = initIndexer ? new IndexerConfig() : null;
        if (!initIndexer) {
            appLogger.log(Level.WARNING, "Indexer will not be initialized");
        } else {
            appLogger.log(Level.INFO, "Indexer will be initialized");
            indexerConfig.setCzidloApiBaseUrl(loader.loadString(PropertyKeys.INDEXER_CZIDLO_API_BASE_URL));
            indexerConfig.setCzidloApiUseHttps(false);
            indexerConfig.setSolrApiBaseUrl(loader.loadString(PropertyKeys.INDEXER_SOLR_BASE_URL));
            indexerConfig.setSolrApiCollection(loader.loadString(PropertyKeys.INDEXER_SOLR_COLLECTION));
            indexerConfig.setSolrApiUseHttps(loader.loadBoolean(PropertyKeys.INDEXER_SOLR_USE_HTTPS));
            indexerConfig.setSolrApiLogin(loader.loadString(PropertyKeys.INDEXER_SOLR_LOGIN));
            indexerConfig.setSolrApiPassword(loader.loadString(PropertyKeys.INDEXER_SOLR_PASSWORD));
            //ES
            indexerConfig.setEsApiBaseUrl(loader.loadStringOrNull(PropertyKeys.INDEXER_ES_BASE_URL));
            indexerConfig.setEsApiLogin(loader.loadStringOrNull(PropertyKeys.INDEXER_ES_LOGIN));
            indexerConfig.setEsApiPassword(loader.loadStringOrNull(PropertyKeys.INDEXER_ES_PASSWORD));
            indexerConfig.setEsApiIndexSearchName(loader.loadStringOrNull(PropertyKeys.INDEXER_ES_INDEX_SEARCH_NAME));
            indexerConfig.setEsApiIndexAssignName(loader.loadStringOrNull(PropertyKeys.INDEXER_ES_INDEX_ASSIGN_NAME));
            indexerConfig.setEsApiIndexResolveName(loader.loadStringOrNull(PropertyKeys.INDEXER_ES_INDEX_RESOLVE_NAME));
            //DB
            indexerConfig.setDbUrl(loader.loadString(PropertyKeys.INDEXER_DB_URL));
            indexerConfig.setDbLogin(loader.loadString(PropertyKeys.INDEXER_DB_LOGIN));
            indexerConfig.setDbPassword(loader.loadString(PropertyKeys.INDEXER_DB_PASSWORD));
            //TODO: fail here, if some required properties are missing

            try {
                URL czidloToSolrXsltFileResource = getClass().getClassLoader().getResource(CZIDLO_TO_SOLR_XSLT);
                if (czidloToSolrXsltFileResource != null) { //because not all web modules contain this file
                    try {
                        File czidloToSolrXsltFile = new File(czidloToSolrXsltFileResource.toURI());
                        indexerConfig.setCzidloToSolrXsltFile(czidloToSolrXsltFile);
                        indexerConfig.setCzidloToSolrXslt(XmlTools.loadXmlFromFile(czidloToSolrXsltFile.getAbsolutePath()));
                    } catch (IllegalArgumentException e) {
                        //initIndexer = false;
                        appLogger.log(Level.WARNING, "Resource not found: {0}", CZIDLO_TO_SOLR_XSLT);
                    }
                } else {
                    //initIndexer = false;
                    appLogger.log(Level.WARNING, "Resource not found: {0}", CZIDLO_TO_SOLR_XSLT);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    public File getAdminLogFile() {
        return adminLogFile;
    }
}
