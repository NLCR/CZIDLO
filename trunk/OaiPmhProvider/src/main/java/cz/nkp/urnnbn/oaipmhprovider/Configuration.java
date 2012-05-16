/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider;

import cz.nkp.urnnbn.oaipmhprovider.repository.Repository;
import cz.nkp.urnnbn.oaipmhprovider.repository.impl.RepositoryImpl;
import cz.nkp.urnnbn.oaipmhprovider.tools.PropertyLoader;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Martin Řehánek (rehan at mzk.cz)
 */
public class Configuration {

    //private static String PROPERTIES_FILE = "provider-work.properties";
    //private static String PROPERTIES_FILE = "provider-laptop.properties";
    private static final String PROPERTIES_FILE = "provider.properties";
    public static final String PROP_REPOSITORY_NAME = "provider.repositoryName";
    public static final String PROP_BASE_URL = "provider.baseUrl";
    public static final String PROP_WEB_URL = "provider.webUrl";
    public static final String PROP_EARLIEST_DATESTAMP = "provider.earliestDatestamp";
    public static final String PROP_ADMIN_EMAIL = "provider.adminEmail";
    public static final String PROP_LIST_IDENTIFIERS_MAX_SIZE = "provider.ListIdentifiers.maxSize";
    public static final String PROP_LIST_RECORDS_MAX_SIZE = "provider.ListRecords.maxSize";
    public static final String PROP_LIST_REQUESTS_MINUTES_VALID = "provider.ListRequests.minutesValid";
    private static Configuration instance = null;
    private final Repository repository;
    private final String repositoryName;
    private final String baseUrl;
    private final String webUrl;
    private final String earliestDatestamp;
    private final String adminEmail;
    private final int listIdentifiersMaxSize;
    private final int listRecordsMaxSize;
    private final int listRequestsMinutesValid;

    private Configuration() throws IOException {
        //PropertyLoader loader = Configuration.getPropertyLoader();
        //repository = RepositoryImpl.instanceOf(loader.getProperties());
        repository = RepositoryImpl.instanceOf(null);
//        repositoryName = loader.loadString(PROP_REPOSITORY_NAME);
//        baseUrl = loader.loadString(PROP_BASE_URL);
//        webUrl = loader.loadString(PROP_WEB_URL);
//        earliestDatestamp = loader.loadString(PROP_EARLIEST_DATESTAMP);
//        adminEmail = loader.loadString(PROP_ADMIN_EMAIL);
//        listIdentifiersMaxSize = loader.loadInt(PROP_LIST_IDENTIFIERS_MAX_SIZE);
//        listRecordsMaxSize = loader.loadInt(PROP_LIST_RECORDS_MAX_SIZE);
//        listRequestsMinutesValid = loader.loadInt(PROP_LIST_REQUESTS_MINUTES_VALID);
        
        repositoryName = "rep";
        baseUrl = "http://baseUrl.cz";
        webUrl = "http://webUrl.cz";
        earliestDatestamp = "2010-10-06T00:00:00Z";
        adminEmail = "rehan@mzk.cz";
        listIdentifiersMaxSize = 10;
        listRecordsMaxSize = 5;
        listRequestsMinutesValid = 5;

    }

    public static PropertyLoader getPropertyLoader() throws IOException {
        InputStream inputStream = Configuration.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE);
        return new PropertyLoader(inputStream);
    }

    public static Configuration instanceOf() throws IOException {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }

    /**
     * @return the repositoryName
     */
    public String getRepositoryName() {
        return repositoryName;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * @return the earliestDatestamp
     */
    public String getEarliestDatestamp() {
        return earliestDatestamp;
    }

    /**
     * @return the adminEmail
     */
    public String getAdminEmail() {
        return adminEmail;
    }

    /**
     * @return the repository
     */
    public Repository getRepository() {
        return repository;
    }

    /**
     * @return the listIdentifiersMaxSize
     */
    public int getListIdentifiersMaxSize() {
        return listIdentifiersMaxSize;
    }

    /**
     * @return the listRecordsMaxSize
     */
    public int getListRecordsMaxSize() {
        return listRecordsMaxSize;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public int getListRequestsMinutesValid() {
        return listRequestsMinutesValid;
    }
}
