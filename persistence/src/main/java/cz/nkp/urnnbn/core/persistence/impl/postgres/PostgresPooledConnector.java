/*
 * Copyright (C) 2012 Martin Řehánek
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;

/**
 *
 * @author Martin Řehánek
 */
public class PostgresPooledConnector implements PostgresConnector, AutoCloseable {

    static final Logger logger = Logger.getLogger(PostgresPooledConnector.class.getName());
    /**
     * Must be the same as in the META-INF/context.xml and WEB-INF/web.xml
     */
    private static final String JNDI_DB_CONNECTION_POOL_ID = "jdbc/postgres";
    private DataSource pool;
    private HikariDataSource hikari;

    public PostgresPooledConnector() {
        try {
            InitialContext ctx = new InitialContext();
            if (ctx == null) {
                throw new Exception("No initial context available");
            }
            pool = (DataSource) ctx.lookup("java:/comp/env/" + JNDI_DB_CONNECTION_POOL_ID);
            if (pool == null) {
                logger.log(Level.SEVERE, "Datasource not found");
                throw new RuntimeException();
            } else {
                logger.log(Level.FINE, "Connection pool established");
            }
        } catch (Throwable e) {
            logger.log(Level.SEVERE, "Cannot load connection pool: {0}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Used for non-JNDI environments, e.g. when running the process manager as a standalone application.
     * In a typical Tomcat environment, the pool is managed by the container and doesn't need to be closed explicitly.
     */
    public PostgresPooledConnector(String dbUrl, String login, String password) {
        try {
            if (dbUrl == null || dbUrl.isBlank()) throw new IllegalArgumentException("dbUrl is blank");

            HikariConfig cfg = new HikariConfig();
            cfg.setPoolName("czidlo-core-dbpool");
            cfg.setJdbcUrl(dbUrl);
            cfg.setUsername(login);
            cfg.setPassword(password);

            cfg.setMaximumPoolSize(5);
            cfg.setMinimumIdle(0);
            cfg.setConnectionTimeout(10_000);
            cfg.setValidationTimeout(5_000);

            cfg.setMaxLifetime(30 * 60_000);      // 30 min
            cfg.setIdleTimeout(5 * 60_000);       // 5 min
            cfg.setLeakDetectionThreshold(60_000);// 60s (jen debug)
            cfg.setDriverClassName("org.postgresql.Driver");

            this.hikari = new HikariDataSource(cfg);
            this.pool = this.hikari;

            logger.info("Connection pool established via Hikari (non-JNDI)");
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Cannot initialize Hikari datasource", e);
        }
    }

    @Override
    public Connection getConnection() throws DatabaseException {
        try {
            return pool.getConnection();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Cannot obtain connection from pool: {0}", ex.getMessage());
            throw new DatabaseException(ex);
        }
    }

    @Override
    public void releaseConnection(Connection conn) throws DatabaseException {
        try {
            if (conn != null) {
                logger.log(Level.FINE, "Returning connection to pool");
                conn.close();
            } else {
                logger.log(Level.WARNING, "conn==null");
            }
        } catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    /**
     * Only neccessary if you use the non-JNDI constructor that uses Hikari. In a typical Tomcat environment, the pool is managed by the container and doesn't need to be closed explicitly.
     */
    @Override
    public void close() {
        if (hikari != null) {
            try {
                hikari.close();
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to close HikariDataSource: {0}", e.getMessage());
            } finally {
                hikari = null;
            }
        }
    }
}
