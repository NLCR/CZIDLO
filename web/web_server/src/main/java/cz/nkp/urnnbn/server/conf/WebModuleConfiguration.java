package cz.nkp.urnnbn.server.conf;

import cz.nkp.urnnbn.processmanager.conf.Configuration;
import cz.nkp.urnnbn.shared.ConfigurationData;
import cz.nkp.urnnbn.utils.PropertyLoader;
import cz.nkp.urnnbn.webcommon.config.ApplicationConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Martin Řehánek
 */
public class WebModuleConfiguration extends ApplicationConfiguration {

    private static final Logger logger = Logger.getLogger(WebModuleConfiguration.class.getName());
    private static WebModuleConfiguration instance = null;
    //solr
    private String solrBaseUrl;
    private String solrCollection;
    private boolean solrUseHttps;
    //other
    private boolean showAlephLinks;
    private String alephUrl;
    private String alephBase;
    private String loginPage;
    private String gaTrackingCode;
    //file resources
    private File czidloToSolrXslt;

    static public WebModuleConfiguration instanceOf() {
        if (instance == null) {
            instance = new WebModuleConfiguration();
        }
        return instance;
    }

    @Override
    public void initialize(String appName, PropertyLoader loader) throws IOException {
        logger.info("Loading configuration of module " + appName);
        super.initialize(appName, loader);
        //solr
        solrBaseUrl = loader.loadString(PropertyKeys.SOLR_BASE_URL);
        solrCollection = loader.loadString(PropertyKeys.SOLR_COLLECTION);
        solrUseHttps = loader.loadBoolean(PropertyKeys.SOLR_USE_HTTPS, false);
        //other
        showAlephLinks = loader.loadBoolean(PropertyKeys.SHOW_ALEPH_LINKS, false);
        alephUrl = loader.loadStringOrNull(PropertyKeys.ALEPH_URL);
        alephBase = loader.loadStringOrNull(PropertyKeys.ALEPH_BASE);
        loginPage = loader.loadStringOrNull(PropertyKeys.LOGIN_PAGE);
        gaTrackingCode = loader.loadStringOrNull(PropertyKeys.GA_TRACKING_CODE);
        Configuration.init(loader);
    }

    public void initializeFileResources(String appName, File czidloToSolrXslt) throws IOException {
        logger.info("Loading file resources of module " + appName);
        this.czidloToSolrXslt = czidloToSolrXslt;
        checkFileResource(czidloToSolrXslt, "czidlo-to-solr-xslt");
        Configuration.initFileResources(czidloToSolrXslt);
    }

    private void checkFileResource(File file, String resourceName) {
        if (file == null) {
            logger.warning(String.format("File resource \'%s\' is not defined!", resourceName));
        } else if (!file.exists()) {
            logger.warning(String.format("File resource \'%s\' not found: %s!", resourceName, file.getAbsolutePath()));
        } else {
            logger.info(String.format("File resource \'%s\' found: %s", resourceName, file.getAbsolutePath()));
        }
    }


    public static Logger getLogger() {
        return logger;
    }

    public boolean isShowAlephLinks() {
        return showAlephLinks;
    }

    public String getAlephUrl() {
        return alephUrl;
    }

    public String getAlephBase() {
        return alephBase;
    }

    public String getLoginPage() {
        return loginPage;
    }

    public File getCzidloToSolrXslt() {
        return czidloToSolrXslt;
    }

    public String getSolrBaseUrl() {
        return solrBaseUrl;
    }

    public String getSolrCollection() {
        return solrCollection;
    }

    public boolean getSolrUseHttps() {
        return solrUseHttps;
    }

    public ConfigurationData toConfigurationData() {
        ConfigurationData result = new ConfigurationData();
        result.setShowAlephLinks(showAlephLinks);
        result.setAlephBase(alephBase);
        result.setAlephUrl(alephUrl);
        result.setGaTrackingCode(gaTrackingCode);
        result.setCountryCode(getLanguageCode());
        result.setLoginPage(getLoginPage());
        return result;
    }
}
