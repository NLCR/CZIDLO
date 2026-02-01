package cz.nkp.urnnbn.indexer;

import java.io.File;

/**
 * Created by Martin Řehánek on 29.1.18.
 */
public class IndexerConfig {
    // ES API
    private String esApiBaseUrl;
    private String esApiLogin;
    private String esApiPassword;
    private String esApiIndexSearchName;
    private String esApiIndexAssignName;
    private String esApiIndexResolveName;
    // DB
    private String dbUrl;
    private String dbLogin;
    private String dbPassword;

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

    public String getEsApiIndexSearchName() {
        return esApiIndexSearchName;
    }

    public void setEsApiIndexSearchName(String esApiIndexSearchName) {
        this.esApiIndexSearchName = esApiIndexSearchName;
    }

    public String getEsApiIndexAssignName() {
        return esApiIndexAssignName;
    }

    public void setEsApiIndexAssignName(String esApiIndexAssignName) {
        this.esApiIndexAssignName = esApiIndexAssignName;
    }

    public String getEsApiIndexResolveName() {
        return esApiIndexResolveName;
    }

    public void setEsApiIndexResolveName(String esApiIndexResolveName) {
        this.esApiIndexResolveName = esApiIndexResolveName;
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
