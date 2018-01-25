/*
 * Copyright (C) 2012 Martin Řehánek
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

/**
 * @author Martin Řehánek
 */
public class PropertyKeys extends cz.nkp.urnnbn.config.PropertyKeys {

    static final String JOBS_DATA_DIR = "process.scheduler.jobsDataDir";
    static final String MAX_ADMIN_JOBS = "process.scheduler.maxRunning.admin";
    static final String MAX_USER_JOBS = "process.scheduler.maxRunning.user";
    //COMMON
    static final String CZIDLO_API_BASE_URL = "process.common.czidloApi.baseUrl";
    //OAI ADAPTER
    static final String OAI_ADAPTER_DD_REGISTRATION_XSD_URL = "process.oaiAdapter.digDocRegistrationXsdUrl";
    static final String OAI_ADAPTER_DI_IMPORT_XSD_URL = "process.oaiAdapter.digInstImportXsdUrl";
    //SOLR INDEXER
    static String SOLR_INDEXER_SOLR_BASE_URL = "process.solrIndexer.baseUrl";
    static String SOLR_INDEXER_SOLR_COLLECTION = "process.solrIndexer.collection";
    static String SOLR_INDEXER_SOLR_USE_HTTPS = "process.solrIndexer.useHttps";
    static String SOLR_INDEXER_SOLR_LOGIN = "process.solrIndexer.login";
    static String SOLR_INDEXER_SOLR_PASSWORD = "process.solrIndexer.password";
}
