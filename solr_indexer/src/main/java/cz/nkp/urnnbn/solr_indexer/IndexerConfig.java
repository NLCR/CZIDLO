package cz.nkp.urnnbn.solr_indexer;

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
}
