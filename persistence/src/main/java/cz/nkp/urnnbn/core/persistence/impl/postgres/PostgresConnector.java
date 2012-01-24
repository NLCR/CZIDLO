/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.persistence.DatabaseConnector;

/**
 *
 * @author Martin Řehánek
 */
public class PostgresConnector extends DatabaseConnector {

    private static final Integer DEFAULT_PORT = 5432;
    private final String host;
    private final String database;
    private final int port;

    private PostgresConnector() {
        this(null, null, null, null);
    }

    /**
     * Should only be used by connetor factory. Otherwise multiple instances can alloc
     * multiple connections/pools
     * @param host
     * @param database
     * @param login
     * @param password 
     */
    public PostgresConnector(String host, String database, String login, String password) {
        this(host, database, DEFAULT_PORT, login, password);
    }

    /**
     * Should only be used by connetor factory. Otherwise multiple instances can alloc
     * multiple connections/pools
     * @param host
     * @param database
     * @param port
     * @param login
     * @param password 
     */
    public PostgresConnector(String host, String database, int port, String login, String password) {
        super(login, password);
        this.host = host;
        this.database = database;
        this.port = port;
    }

    @Override
    public String getDriver() {
        return "org.postgresql.Driver";
    }

    @Override
    public String buildUrl() {
        return "jdbc:postgresql://" + host + ":" + port + "/" + database;
    }
}
