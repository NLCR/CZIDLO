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
    //indexer - Elasticsearch
    private static String indexerEsBaseUrl;
    private static String indexerEsLogin;
    private static String indexerEsPassword;
    private static String indexerEsIndexSearchName;
    private static String indexerEsIndexAssignName;
    private static String indexerEsIndexResolveName;
    //indexer - czidlo db
    private static String indexerCzidloDbUrl;
    private static String indexerCzidloDbLogin;
    private static String indexerCzidloDbPassword;

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
        //indexer - Elasticsearch
        indexerEsBaseUrl = loader.loadString(PropertyKeys.INDEXER_ES_BASE_URL);
        indexerEsLogin = loader.loadString(PropertyKeys.INDEXER_ES_LOGIN);
        indexerEsPassword = loader.loadString(PropertyKeys.INDEXER_ES_PASSWORD);
        indexerEsIndexSearchName = loader.loadString(PropertyKeys.INDEXER_ES_INDEX_SEARCH_NAME);
        indexerEsIndexAssignName = loader.loadString(PropertyKeys.INDEXER_ES_INDEX_ASSIGN_NAME);
        indexerEsIndexResolveName = loader.loadString(PropertyKeys.INDEXER_ES_INDEX_RESOLVE_NAME);
        // indexer - czidlo db
        indexerCzidloDbUrl = loader.loadString(PropertyKeys.INDEXER_DB_URL);
        indexerCzidloDbLogin = loader.loadString(PropertyKeys.INDEXER_DB_LOGIN);
        indexerCzidloDbPassword = loader.loadString(PropertyKeys.INDEXER_DB_PASSWORD);

        Services.init(new PostgresPooledConnector(), null);
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

    public static String getIndexerEsBaseUrl() {
        return indexerEsBaseUrl;
    }

    public static String getIndexerEsIndexSearchName() {
        return indexerEsIndexSearchName;
    }

    public static String getIndexerEsIndexAssignName() {
        return indexerEsIndexAssignName;
    }

    public static String getIndexerEsIndexResolveName() {
        return indexerEsIndexResolveName;
    }

    public static String getIndexerEsLogin() {
        return indexerEsLogin;
    }

    public static String getIndexerEsPassword() {
        return indexerEsPassword;
    }

    public static String getIndexerCzidloDbUrl() {
        return indexerCzidloDbUrl;
    }

    public static String getIndexerCzidloDbLogin() {
        return indexerCzidloDbLogin;
    }

    public static String getIndexerCzidloDbPassword() {
        return indexerCzidloDbPassword;
    }
}
