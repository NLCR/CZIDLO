package cz.nkp.urnnbn.indexer;

import java.io.File;

/**
 * Created by Martin Řehánek on 29.1.18.
 */
public class IndexerConfig {
    // CZIDLO API
    private String czidloApiBaseUrl;
    private Boolean czidloApiUseHttps;
    // SOLR API
    private String solrApiBaseUrl;
    private String solrApiCollection;
    private Boolean solrApiUseHttps;
    private String solrApiLogin;
    private String solrApiPassword;
    // ES API
    private String esApiBaseUrl;
    private String esApiLogin;
    private String esApiPassword;
    private String esApiIndexName;
    // DB
    private String dbUrl;
    private String dbLogin;
    private String dbPassword;
    // XSLT
    private String czidloToSolrXslt;
    private File czidloToSolrXsltFile;

    public String getCzidloApiBaseUrl() {
        return czidloApiBaseUrl;
    }

    public void setCzidloApiBaseUrl(String czidloApiBaseUrl) {
        this.czidloApiBaseUrl = czidloApiBaseUrl;
    }

    public Boolean getCzidloApiUseHttps() {
        return czidloApiUseHttps;
    }

    public void setCzidloApiUseHttps(Boolean czidloApiUseHttps) {
        this.czidloApiUseHttps = czidloApiUseHttps;
    }

    public String getSolrApiBaseUrl() {
        return solrApiBaseUrl;
    }

    public void setSolrApiBaseUrl(String solrApiBaseUrl) {
        this.solrApiBaseUrl = solrApiBaseUrl;
    }

    public String getSolrApiCollection() {
        return solrApiCollection;
    }

    public void setSolrApiCollection(String solrApiCollection) {
        this.solrApiCollection = solrApiCollection;
    }

    public Boolean getSolrApiUseHttps() {
        return solrApiUseHttps;
    }

    public void setSolrApiUseHttps(Boolean solrApiUseHttps) {
        this.solrApiUseHttps = solrApiUseHttps;
    }

    public String getSolrApiLogin() {
        return solrApiLogin;
    }

    public void setSolrApiLogin(String solrApiLogin) {
        this.solrApiLogin = solrApiLogin;
    }

    public String getSolrApiPassword() {
        return solrApiPassword;
    }

    public void setSolrApiPassword(String solrApiPassword) {
        this.solrApiPassword = solrApiPassword;
    }

    public String getCzidloToSolrXslt() {
        return czidloToSolrXslt;
    }

    public void setCzidloToSolrXslt(String czidloToSolrXslt) {
        this.czidloToSolrXslt = czidloToSolrXslt;
    }

    public File getCzidloToSolrXsltFile() {
        return czidloToSolrXsltFile;
    }

    public void setCzidloToSolrXsltFile(File czidloToSolrXsltFile) {
        this.czidloToSolrXsltFile = czidloToSolrXsltFile;
    }

    public String getEsApiBaseUrl() {
        return esApiBaseUrl;
    }

    public void setEsApiBaseUrl(String esApiBaseUrl) {
        this.esApiBaseUrl = esApiBaseUrl;
    }

    public String getEsApiLogin() {
        return esApiLogin;
    }

    public void setEsApiLogin(String esApiLogin) {
        this.esApiLogin = esApiLogin;
    }

    public String getEsApiPassword() {
        return esApiPassword;
    }

    public void setEsApiPassword(String esApiPassword) {
        this.esApiPassword = esApiPassword;
    }

    public String getEsApiIndexName() {
        return esApiIndexName;
    }

    public void setEsApiIndexName(String esApiIndexName) {
        this.esApiIndexName = esApiIndexName;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDbLogin() {
        return dbLogin;
    }

    public void setDbLogin(String dbLogin) {
        this.dbLogin = dbLogin;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }
}
