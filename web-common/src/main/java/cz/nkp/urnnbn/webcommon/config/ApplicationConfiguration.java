/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.webcommon.config;

import cz.nkp.urnnbn.config.PropertyKeys;
import cz.nkp.urnnbn.core.AdminLoggerSimple;
import cz.nkp.urnnbn.core.CountryCode;
import cz.nkp.urnnbn.core.persistence.impl.DatabaseConnectorFactory;
import cz.nkp.urnnbn.services.Services;
import cz.nkp.urnnbn.indexer.IndexerConfig;
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
    private Boolean serverReadOnly;
    private Boolean develMode;
    private String languageCode;
    private String adminName;
    private String adminEmail;
    private File adminLogFile;
    private IndexerConfig indexerConfig;

    public void initialize(String webAppName, PropertyLoader loader) throws IOException {
        appLogger.fine("Loading configuration");
        serverReadOnly = loader.loadBoolean(PropertyKeys.SERVER_READ_ONLY);
        develMode = loader.loadBoolean(PropertyKeys.DEVEL);
        languageCode = loader.loadString(PropertyKeys.LANGUAGE_CODE);
        adminName = loader.loadStringOrNull(PropertyKeys.ADMIN_NAME);
        adminEmail = loader.loadStringOrNull(PropertyKeys.ADMIN_EMAIL);
        CountryCode.initialize(languageCode);
        //admin log
        adminLogFile = new File(loader.loadString(PropertyKeys.ADMIN_LOG_FILE));
        appLogger.log(Level.INFO, "initializing admin logger to file {0}", adminLogFile);
        try {
            AdminLoggerSimple.initializeLogger(webAppName, adminLogFile);
        } catch (Exception e) {
            appLogger.log(Level.SEVERE, "AdminLogger init failed", e);
        }
        //indexer
        boolean initIndexer = loader.loadBoolean("indexer.enabled", true);
        this.indexerConfig = initIndexer ? new IndexerConfig() : null;
        if (!initIndexer) {
            appLogger.log(Level.WARNING, "Indexer will not be initialized");
        } else {
            appLogger.log(Level.INFO, "Indexer will be initialized");
            //Elasticsearch
            indexerConfig.setEsApiBaseUrl(loader.loadString(PropertyKeys.INDEXER_ES_BASE_URL));
            indexerConfig.setEsApiLogin(loader.loadString(PropertyKeys.INDEXER_ES_LOGIN));
            indexerConfig.setEsApiPassword(loader.loadString(PropertyKeys.INDEXER_ES_PASSWORD));
            indexerConfig.setEsApiIndexSearchName(loader.loadString(PropertyKeys.INDEXER_ES_INDEX_SEARCH_NAME));
            indexerConfig.setEsApiIndexAssignName(loader.loadString(PropertyKeys.INDEXER_ES_INDEX_ASSIGN_NAME));
            indexerConfig.setEsApiIndexResolveName(loader.loadString(PropertyKeys.INDEXER_ES_INDEX_RESOLVE_NAME));
            //Czidlo DB
            indexerConfig.setDbUrl(loader.loadString(PropertyKeys.INDEXER_DB_URL));
            indexerConfig.setDbLogin(loader.loadString(PropertyKeys.INDEXER_DB_LOGIN));
            indexerConfig.setDbPassword(loader.loadString(PropertyKeys.INDEXER_DB_PASSWORD));
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

    public IndexerConfig getIndexerConfig() {
        return indexerConfig;
    }
}
