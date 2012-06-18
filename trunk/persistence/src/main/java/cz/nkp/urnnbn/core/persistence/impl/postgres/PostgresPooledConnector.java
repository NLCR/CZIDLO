/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.postgres;

import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 *
 * @author Martin Řehánek
 */
public class PostgresPooledConnector implements PostgresConnector {

    static final Logger logger = Logger.getLogger(PostgresPooledConnector.class.getName());
    /** Must be the same as in the META-INF/context.xml and WEB-INF/web.xml */
    private static final String JNDI_DB_CONNECTION_POOL_ID = "jdbc/postgres";
    private DataSource pool;

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
}
