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

import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.impl.postgres.PostgresSimpleConnector;
import cz.nkp.urnnbn.oaiadapter.cli.DefinedProperties;
import cz.nkp.urnnbn.services.Services;
import cz.nkp.urnnbn.utils.PropertyLoader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Martin Řehánek
 */
public class Configuration {

    private static File jobsDir;
    private static ResolverDatabaseCreditentials resolverDbCreditentials;
    private static Integer maxRunningAdminProcesses;
    private static Integer maxRunningUserProcesses;
    private static String resolverApiUrl;
    //configuration for processes
    private static String digDocRegistrationXsdUrl;
    private static String digInstImportXsdUrl;

    public static void init(InputStream in) throws IOException {
        init(new PropertyLoader(in));
    }

    public static void init(File configFile) throws IOException {
        init(new PropertyLoader(configFile));
    }

    public static void init(PropertyLoader loader) throws IOException {
        jobsDir = loader.loadDir(PropertyKeys.JOBS_DATA_DIR);
        maxRunningAdminProcesses = loader.loadInt(PropertyKeys.MAX_ADMIN_JOBS);
        maxRunningUserProcesses = loader.loadInt(PropertyKeys.MAX_USER_JOBS);
        resolverDbCreditentials = new ResolverDatabaseCreditentials(
                loader.loadString(PropertyKeys.DB_HOST),
                loader.loadInt(PropertyKeys.DB_PORT),
                loader.loadString(PropertyKeys.DB_DATABASE),
                loader.loadString(PropertyKeys.DB_LOGIN),
                loader.loadString(PropertyKeys.DB_PASSWORD));
        Services.init(initDatabaseConnector());
        //oai adapter
        digDocRegistrationXsdUrl = loader.loadString(DefinedProperties.DD_REGISTRATION_XSD_URL);
        digInstImportXsdUrl = loader.loadString(DefinedProperties.DI_IMPORT_XSD_URL);
        resolverApiUrl = loader.loadString(DefinedProperties.RESOLVER_API_URL);
    }

    private static DatabaseConnector initDatabaseConnector() {
        return new PostgresSimpleConnector(
                resolverDbCreditentials.getHost(),
                resolverDbCreditentials.getDatabase(),
                resolverDbCreditentials.getPort().intValue(),
                resolverDbCreditentials.getLogin(),
                resolverDbCreditentials.getPassword());
    }

    public static File getJobsDir() {
        return jobsDir;
    }

    public static ResolverDatabaseCreditentials getResolverDbCreditentials() {
        return resolverDbCreditentials;
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

    public static String getResolverApiUrl() {
        return resolverApiUrl;
    }
}
