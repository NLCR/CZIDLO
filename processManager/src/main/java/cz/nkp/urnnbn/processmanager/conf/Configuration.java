/*
 * Copyright (C) 2013 Martin Řehánek
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.nkp.urnnbn.processmanager.conf;

import cz.nkp.urnnbn.core.persistence.impl.postgres.PostgresPooledConnector;
import cz.nkp.urnnbn.services.Services;
import cz.nkp.urnnbn.utils.PropertyLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * @author Martin Řehánek
 */
public class Configuration {

    private static final Logger logger = Logger.getLogger(Configuration.class.getName());

    private static File jobsDir;
    private static Integer maxRunningAdminProcesses;
    private static Integer maxRunningUserProcesses;
    // configuration for processes
    private static String czidloApiBaseUrl;
    //oai adapter
    private static String oaiAdapterDigDocRegistrationXsdUrl;
    private static String oaiAdapterDigInstImportXsdUrl;
    //solr indexer
    private static String solrIndexerSolrBaseUrl;
    private static String solrIndexerSolrCollection;
    private static Boolean solrIndexerSolrUseHttps;
    private static String solrIndexerSolrLogin;
    private static String solrIndexerSolrPassword;
    private static File solrIndexerCzidloToSolrXslt;

    public static void init(InputStream in) throws IOException {
        init(new PropertyLoader(in));
    }

    public static void init(File configFile) throws IOException {
        init(new PropertyLoader(configFile));
    }

    public static void init(PropertyLoader loader) throws IOException {
        logger.info("init");
        jobsDir = loader.loadDir(PropertyKeys.JOBS_DATA_DIR);
        maxRunningAdminProcesses = loader.loadInt(PropertyKeys.MAX_ADMIN_JOBS);
        maxRunningUserProcesses = loader.loadInt(PropertyKeys.MAX_USER_JOBS);
        // processes - common
        czidloApiBaseUrl = loader.loadString(PropertyKeys.CZIDLO_API_BASE_URL);
        // oai adapter
        oaiAdapterDigDocRegistrationXsdUrl = loader.loadString(PropertyKeys.OAI_ADAPTER_DD_REGISTRATION_XSD_URL);
        oaiAdapterDigInstImportXsdUrl = loader.loadString(PropertyKeys.OAI_ADAPTER_DI_IMPORT_XSD_URL);
        // solr indexer
        solrIndexerSolrBaseUrl = loader.loadString(PropertyKeys.SOLR_INDEXER_SOLR_BASE_URL);
        solrIndexerSolrCollection = loader.loadString(PropertyKeys.SOLR_INDEXER_SOLR_COLLECTION);
        solrIndexerSolrUseHttps = loader.loadBoolean(PropertyKeys.SOLR_INDEXER_SOLR_USE_HTTPS, false);
        solrIndexerSolrLogin = loader.loadString(PropertyKeys.SOLR_INDEXER_SOLR_LOGIN);
        solrIndexerSolrPassword = loader.loadString(PropertyKeys.SOLR_INDEXER_SOLR_PASSWORD);

        /*IndexerConfig indexerConfig = new IndexerConfig();
        indexerConfig.setCzidloApiBaseUrl(czidloApiBaseUrl);
        indexerConfig.setCzidloApiUseHttps(false);
        */

        Services.init(new PostgresPooledConnector(), null);
    }

    public static void initFileResources(File czidloToSolrXslt) {
        logger.info("initFileResources");
        Configuration.solrIndexerCzidloToSolrXslt = czidloToSolrXslt;
    }

    public static File getJobsDir() {
        return jobsDir;
    }

    public static Integer getMaxRunningAdminProcesses() {
        return maxRunningAdminProcesses;
    }

    public static Integer getMaxRunningUserProcesses() {
        return maxRunningUserProcesses;
    }

    public static String getOaiAdapterDigDocRegistrationXsdUrl() {
        return oaiAdapterDigDocRegistrationXsdUrl;
    }

    public static String getOaiAdapterDigInstImportXsdUrl() {
        return oaiAdapterDigInstImportXsdUrl;
    }

    public static String getCzidloApiBaseUrl() {
        return czidloApiBaseUrl;
    }

    public static String getSolrIndexerSolrBaseUrl() {
        return solrIndexerSolrBaseUrl;
    }

    public static String getSolrIndexerSolrCollection() {
        return solrIndexerSolrCollection;
    }

    public static Boolean getSolrIndexerSolrUseHttps() {
        return solrIndexerSolrUseHttps;
    }

    public static String getSolrIndexerSolrLogin() {
        return solrIndexerSolrLogin;
    }

    public static String getSolrIndexerSolrPassword() {
        return solrIndexerSolrPassword;
    }

    public static String getSolrXsltFilename() {
        return solrIndexerCzidloToSolrXslt.getAbsolutePath();
    }

}
