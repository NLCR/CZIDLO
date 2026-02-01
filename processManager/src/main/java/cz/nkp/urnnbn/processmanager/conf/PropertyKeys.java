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
 * Process manager specific property keys.
 *
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
    //INDEXER (Elasticsearch)
    static String INDEXER_ES_BASE_URL = "process.indexer.es.baseUrl";
    static String INDEXER_ES_LOGIN = "process.indexer.es.login";
    static String INDEXER_ES_PASSWORD = "process.indexer.es.password";
    static String INDEXER_ES_INDEX_SEARCH_NAME = "process.indexer.es.index_search";
    static String INDEXER_ES_INDEX_ASSIGN_NAME = "process.indexer.es.index_assign";
    static String INDEXER_ES_INDEX_RESOLVE_NAME = "process.indexer.es.index_resolve";
    static String INDEXER_DB_URL = "process.indexer.db.url";
    static String INDEXER_DB_LOGIN = "process.indexer.db.login";
    static String INDEXER_DB_PASSWORD = "process.indexer.db.password";
}
