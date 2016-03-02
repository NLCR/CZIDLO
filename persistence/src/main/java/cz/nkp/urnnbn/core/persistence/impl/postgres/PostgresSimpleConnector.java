/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;

/**
 *
 * @author Martin Řehánek
 */
public class PostgresSimpleConnector implements PostgresConnector {

    private static final Logger logger = Logger.getLogger(PostgresSimpleConnector.class.getName());
    private static final String DRIVER_CLASS = "org.postgresql.Driver";
    private static final Integer DEFAULT_PORT = 5432;
    private final String host;
    private final String database;
    private final int port;
    private final String login;
    private final String password;
    private String jdbcUrl;

    /**
     * Should only be used by connetor factory. Otherwise multiple instances can alloc multiple connections/pools
     * 
     * @param host
     * @param database
     * @param login
     * @param password
     */
    public PostgresSimpleConnector(String host, String database, String login, String password) {
        this(host, database, DEFAULT_PORT, login, password);
    }

    /**
     * Should only be used by connetor factory. Otherwise multiple instances can alloc multiple connections/pools
     * 
     * @param host
     * @param database
     * @param port
     * @param login
     * @param password
     */
    public PostgresSimpleConnector(String host, String database, int port, String login, String password) {
        this.host = host;
        this.database = database;
        this.port = port;
        this.login = login;
        this.password = password;

    }

    @Override
    public Connection getConnection() throws DatabaseException {
        String url = getJdbcUrl();
        try {
            Class.forName(DRIVER_CLASS);
            return DriverManager.getConnection(url, login, password);
        } catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, "Driver {0} not found", DRIVER_CLASS);
            throw new DatabaseException(ex);
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Cannot obtain connection from {0}: {1}", new Object[] { url, ex.getMessage() });
            throw new DatabaseException(ex);
        }
    }

    private String getJdbcUrl() {
        if (jdbcUrl == null) {
            jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + database;
        }
        return jdbcUrl;
    }

    @Override
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
    }
}
