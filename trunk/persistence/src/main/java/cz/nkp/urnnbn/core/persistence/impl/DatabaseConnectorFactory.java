/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl;

import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.impl.postgres.PostgresConnector;
import cz.nkp.urnnbn.core.DefinedProperties;
import cz.nkp.urnnbn.core.utils.PropertyLoader;
import java.io.File;
import java.io.IOException;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 *
 * @author Martin Řehánek
 */
public class DatabaseConnectorFactory {

    public static DatabaseConnector getConnector(File properties) throws IOException {
        PropertyLoader loader = new PropertyLoader(properties);
        DefinedProperties defined = new DefinedProperties();
        String driver = loader.loadString(defined.DB_DRIVER);
        String host = loader.loadString(defined.DB_HOST);
        Integer port = loader.loadIntOrNull(defined.DB_PORT);
        String database = loader.loadString(defined.DB_DATABASE);
        String login = loader.loadString(defined.DB_LOGIN);
        String password = loader.loadString(defined.DB_PASSWORD);
        return getConnector(driver, host, database, port, login, password);
    }

    public static DatabaseConnector getConnector(String driver, String host, String database, String login, String password) {
        return getConnector(driver, host, database, null, login, password);
    }

    public static DatabaseConnector getConnector(String driver, String host, String database, Integer port, String login, String password) {
        if (DatabaseDriver.POSTGRES.equals(driver)) {
            if (port == null) {
                return new PostgresConnector(host, database, login, password);
            } else {
                return new PostgresConnector(host, database, port, login, password);
            }

        } else if (DatabaseDriver.ORACLE.equals(driver)) {
            throw new NotImplementedException();
        } else {
            throw new IllegalArgumentException("Unknown driver '" + driver + "'");
        }
    }
}
