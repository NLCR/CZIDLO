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
import cz.nkp.urnnbn.oaiadapter.cli.DefinedProperties;
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
    private static String digDocRegistrationXsdUrl;
    private static String digInstImportXsdUrl;
    //solr indexer
    private static String solrBaseUrl;
    private static String solrCollection;
    private static Boolean solrUseHttps;
    private static String solrLogin;
    private static String solrPassword;
    private static File czidloToSolrXslt;

    public static void init(InputStream in) throws IOException {
        init(new PropertyLoader(in));
    }

    public static void init(File configFile) throws IOException {
        init(new PropertyLoader(configFile));
    }

    public static void init(PropertyLoader loader) throws IOException {
        logger.info("init");
        Services.init(new PostgresPooledConnector());
        jobsDir = loader.loadDir(PropertyKeys.JOBS_DATA_DIR);
        maxRunningAdminProcesses = loader.loadInt(PropertyKeys.MAX_ADMIN_JOBS);
        maxRunningUserProcesses = loader.loadInt(PropertyKeys.MAX_USER_JOBS);
        // processes - common
        czidloApiBaseUrl = loader.loadString(DefinedProperties.CZIDLO_API_BASE_URL);
        // oai adapter
        digDocRegistrationXsdUrl = loader.loadString(DefinedProperties.DD_REGISTRATION_XSD_URL);
        digInstImportXsdUrl = loader.loadString(DefinedProperties.DI_IMPORT_XSD_URL);
        // solr indexer
        // TODO: 24.1.18 loadStringOrDefault with documented default value "localhost:8983/solr/"
        solrBaseUrl = loader.loadString(DefinedProperties.SOLR_BASE_URL);
        // TODO: 24.1.18 loadStringOrDefault with documented default value "czidlo"
        solrCollection = loader.loadString(DefinedProperties.SOLR_COLLECTION);
        // TODO: 24.1.18 document default value "false"
        solrUseHttps = loader.loadBoolean(DefinedProperties.SOLR_USE_HTTPS, false);
        solrLogin = loader.loadString(DefinedProperties.SOLR_LOGIN);
        solrPassword = loader.loadString(DefinedProperties.SOLR_PASSWORD);
        // TODO: 24.1.18 xslt File
    }

    public static void initFileResources(File czidloToSolrXslt) {
        logger.info("initFileResources");
        Configuration.czidloToSolrXslt = czidloToSolrXslt;
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

    public static String getDigDocRegistrationXsdUrl() {
        return digDocRegistrationXsdUrl;
    }

    public static String getDigInstImportXsdUrl() {
        return digInstImportXsdUrl;
    }

    public static String getCzidloApiBaseUrl() {
        return czidloApiBaseUrl;
    }

    public static String getSolrBaseUrl() {
        return solrBaseUrl;
    }

    public static String getSolrCollection() {
        return solrCollection;
    }

    public static Boolean getSolrUseHttps() {
        return solrUseHttps;
    }

    public static String getSolrLogin() {
        return solrLogin;
    }

    public static String getSolrPassword() {
        return solrPassword;
    }

    public static String getSolrXsltFilename() {
        return czidloToSolrXslt.getAbsolutePath();
    }

}
