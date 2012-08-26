/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.conf;

import cz.nkp.urnnbn.oaipmhprovider.repository.Repository;
import cz.nkp.urnnbn.oaipmhprovider.repository.impl.RepositoryImpl;
import cz.nkp.urnnbn.webcommon.config.ApplicationConfiguration;
import cz.nkp.urnnbn.xml.commons.XOMUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.ValidityException;

/**
 *
 * @author Martin Řehánek (rehan at mzk.cz)
 */
public class OaiPmhConfiguration extends ApplicationConfiguration {

    private static final Logger logger = Logger.getLogger(OaiPmhConfiguration.class.getName());
    //private static final String PROPERTIES_FILE = "provider.properties";
    private static OaiPmhConfiguration instance = null;
    private Repository repository;
    private String repositoryName;
    private String baseUrl;
    private String webUrl;
    private String earliestDatestamp;
    private int listIdentifiersMaxSize;
    private int listRecordsMaxSize;
    private int listRequestsMinutesValid;
    private Document resolverToOaidcTemplate;

    /**
     *
     * @param properties InputStream containing properties
     * @throws IOException
     */
    @Override
    public void initialize(cz.nkp.urnnbn.utils.PropertyLoader loader) throws IOException {
        super.initialize(loader);
        logger.log(Level.INFO, "Initializing {0}", OaiPmhConfiguration.class.getName());
        repository = RepositoryImpl.instanceOf();
        repositoryName = loader.loadString(PropertyKeys.REPOSITORY_NAME);
        baseUrl = loader.loadString(PropertyKeys.BASE_URL);
        webUrl = loader.loadString(PropertyKeys.WEB_URL);
        earliestDatestamp = loader.loadString(PropertyKeys.EARLIEST_DATESTAMP);
        //adminEmail = loader.loadString(PropertyKeys.ADMIN_EMAIL);
        listIdentifiersMaxSize = loader.loadInt(PropertyKeys.LIST_IDENTIFIERS_MAX_SIZE);
        listRecordsMaxSize = loader.loadInt(PropertyKeys.LIST_RECORDS_MAX_SIZE);
        listRequestsMinutesValid = loader.loadInt(PropertyKeys.LIST_REQUESTS_MINUTES_VALID);
    }

    void initResolverToOaidcTemplate(InputStream in) throws ParsingException, ValidityException, IOException {
        resolverToOaidcTemplate = XOMUtils.loadDocumentWithoutValidation(in);
    }

    public static OaiPmhConfiguration instanceOf() {
        if (instance == null) {
            instance = new OaiPmhConfiguration();
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

    public Document getResolverToOaidcTemplate() {
        return resolverToOaidcTemplate;
    }
}
