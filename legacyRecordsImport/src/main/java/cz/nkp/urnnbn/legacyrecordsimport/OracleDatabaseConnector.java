/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.legacyrecordsimport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The connector is effectively disabled
 * @author Martin Řehánek
 */
public class OracleDatabaseConnector {

    private static final Logger logger = Logger.getLogger(OracleDatabaseConnector.class.getName());
    private static final String DRIVER_CLASS = "oracle.jdbc.OracleDriver";
    private final String host;
    private final String database;
    private final int port;
    private final String login;
    private final String password;
    private String jdbcUrl;

    public OracleDatabaseConnector(Configuration conf) {
        this.host = conf.getOracleHost();
        this.database = conf.getOracleDb();
        this.port = conf.getOraclePort();
        this.login = conf.getOracleLogin();
        this.password = conf.getOraclePass();
    }

    public Connection getConnection() throws DatabaseException {
        /*String url = getJdbcUrl();
        logger.log(Level.INFO, "jdbc url: {0}", url);
        try {
            Class.forName(DRIVER_CLASS);
            return DriverManager.getConnection(url, login, password);
        } catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, "Driver {0} not found", DRIVER_CLASS);
            throw new DatabaseException(ex);
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Cannot obtain connection from {0}: {1}", new Object[]{url, ex.getMessage()});
            throw new DatabaseException(ex);
        }*/
        return null;
    }

    /*private String getJdbcUrl() {
        if (jdbcUrl == null) {
            jdbcUrl = "jdbc:oracle:thin:@" + host + ":" + port + ":" + database;
        }
        return jdbcUrl;
    }

    public void releaseConnection(Connection conn) throws DatabaseException {
        try {
            if (conn != null) {
                logger.log(Level.FINE, "Closing database connection");
                conn.close();
            } else {
                logger.log(Level.WARNING, "conn==null");
            }
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }*/
}
