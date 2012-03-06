/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.legacyrecordsimport;

import cz.nkp.urnnbn.utils.PropertyLoader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 *
 * @author Martin Řehánek
 */
public class Configuration {

    private static final Logger logger = Logger.getLogger(Configuration.class.getName());
    public final String oracleHost;
    public final int oraclePort;
    public final String oracleDb;
    public final String oracleLogin;
    public final String oraclePass;
    public final File resultXmlDir;
    public final File updateDatastampsDir;

    /**
     * 
     * @param properties InputStream containing properties
     * @throws IOException 
     */
    public Configuration(InputStream properties) throws IOException {
        logger.info("Loading application configuration");
        PropertyLoader loader = new PropertyLoader(properties);
        oracleHost = loader.loadString("oracle.host");
        oracleDb = loader.loadString("oracle.db");
        oraclePort = loader.loadInt("oracle.port");
        oracleLogin = loader.loadString("oracle.login");
        oraclePass = loader.loadString("oracle.pass");
        resultXmlDir = loader.loadDir("result.dir");
        updateDatastampsDir = loader.loadDir("update.dir");
    }

    public String getOracleDb() {
        return oracleDb;
    }

    public String getOracleHost() {
        return oracleHost;
    }

    public String getOracleLogin() {
        return oracleLogin;
    }

    public String getOraclePass() {
        return oraclePass;
    }

    public int getOraclePort() {
        return oraclePort;
    }

    public File getResultXmlDir() {
        return resultXmlDir;
    }

    public File getUpdateDatastampsDir() {
        return updateDatastampsDir;
    }
}
