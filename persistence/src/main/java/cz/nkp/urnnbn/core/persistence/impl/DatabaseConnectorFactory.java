/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import cz.nkp.urnnbn.config.PropertyKeys;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.impl.postgres.PostgresPooledConnector;
import cz.nkp.urnnbn.core.persistence.impl.postgres.PostgresSimpleConnector;
import cz.nkp.urnnbn.utils.PropertyLoader;

/**
 * @author Martin Řehánek
 */
public class DatabaseConnectorFactory {

    private static final Logger logger = Logger.getLogger(DatabaseConnectorFactory.class.getName());

    /**
     * Returns database connector form properties file.
     *
     * @param properties Properties file
     * @return DatabaseConnector object
     * @throws IOException when reading configuration properties file failes.
     */
    public static DatabaseConnector getConnector(File properties) throws IOException {
        PropertyLoader loader = new PropertyLoader(properties);
        String driver = loader.loadString(PropertyKeys.DB_DRIVER);
        String host = loader.loadString(PropertyKeys.DB_HOST);
        Integer port = loader.loadIntOrNull(PropertyKeys.DB_PORT);
        String database = loader.loadString(PropertyKeys.DB_DATABASE);
        String login = loader.loadString(PropertyKeys.DB_LOGIN);
        String password = loader.loadString(PropertyKeys.DB_PASSWORD);
        return getConnector(driver, host, database, port, login, password);
    }

    /**
     * Returns database connector from given properties.
     *
     * @param driver
     * @param host
     * @param database
     * @param port
     * @param login
     * @param password
     * @return DatabaseConnector object
     */
    public static DatabaseConnector getConnector(String driver, String host, String database, Integer port, String login, String password) {
        if (DatabaseDriver.POSTGRES.equals(driver)) {
            if (port == null) {
                return new PostgresSimpleConnector(host, database, login, password);
            } else {
                return new PostgresSimpleConnector(host, database, port, login, password);
            }

        } else if (DatabaseDriver.ORACLE.equals(driver)) {
            throw new UnsupportedOperationException();
        } else {
            throw new IllegalArgumentException("Unknown driver '" + driver + "'");
        }
    }

    /**
     * @return connector using resource (possibly pool) defined in application context
     */
    public static DatabaseConnector getJndiPoolledConnector() {
        logger.log(Level.INFO, "initializing {0} from jndi", DatabaseConnector.class.getName());
        return new PostgresPooledConnector();
    }

    /**
     * @return connector with hardcoded configuration from class DevelDatabaseConfig
     */
    public static DatabaseConnector getDevelConnector() {
        logger.log(Level.INFO, "initializing {0} from devel configuration", DatabaseConnector.class.getName());
        return getConnector(DevelDatabaseConfig.DRIVER, DevelDatabaseConfig.HOST, DevelDatabaseConfig.DATABASE, DevelDatabaseConfig.PORT,
                DevelDatabaseConfig.LOGIN, DevelDatabaseConfig.PASSWORD);
    }
}
