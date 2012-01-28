/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence;

import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author Martin Řehánek
 */
public abstract class DatabaseConnector {

    private static final Logger logger = Logger.getLogger(DatabaseConnector.class.getName());
    /** Must be the same as in the META-INF/context.xml and WEB-INF/web.xml */
    private static final String JNDI_DB_CONNECTION_POOL_ID = "jdbc/postgres";
    private DataSource pool;
    /** singleConnection is used only if pooling is disabled **/
    private Connection singleConnection;
    private final String login;
    private final String password;

    public abstract String getDriver();

    public DatabaseConnector(String login, String password) {
        this.login = login;
        this.password = password;
        init();
    }

    public final void init() {
        try {
            InitialContext ctx = new InitialContext();
            if (ctx == null) {
                throw new Exception("No initial context available");
            }
            //pool = (DataSource) ctx.lookup("java:comp/env/" + JNDI_DB_CONNECTION_POOL_ID);
            pool = (DataSource) ctx.lookup("java:/comp/env/" + JNDI_DB_CONNECTION_POOL_ID);
            if (pool == null) {
                logger.log(Level.SEVERE, "Datasource not found");
            }

//            logger.log(Level.SEVERE, "lets lookup context");
//            Context envCtx = (Context) new InitialContext().lookup("java:comp/env");
//            logger.log(Level.SEVERE, "context lookedup");
//            pool = (DataSource) envCtx.lookup(JNDI_DB_CONNECTION_POOL_ID);
//            logger.log(Level.FINE, "Connection pool established");
        } catch (Throwable e) {
            logger.log(Level.SEVERE, "Cannot load connection pool: {0}", e.getMessage());
        }
    }

    /**
     *
     * @return
     */
    public final synchronized Connection getConnection() throws DatabaseException {
        if (pool != null) {
            return getConnectionFromPool();
        } else {
            return getConnectionNoPool();
        }
    }

    private Connection getConnectionFromPool() throws DatabaseException {
        try {
            //return pool.getConnection(login, password);
            return pool.getConnection();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Cannot obtain connection from pool: {1}", ex.getMessage());
            throw new DatabaseException(ex);
        }
    }

    private Connection getConnectionNoPool() throws DatabaseException {
//        if (singleConnection == null) {
//            singleConnection = newConnectionNoPool();
//        }
//        logger.log(Level.INFO, "Single connection used");
//        return singleConnection;
        return newConnectionNoPool();
    }

    private Connection newConnectionNoPool() throws DatabaseException {
        String url = buildUrl();
        try {
            Class.forName(getDriver());
            return DriverManager.getConnection(url, login, password);
        } catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, "Driver {0} not found", getDriver());
            throw new DatabaseException(ex);
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Cannot obtain connection from {0}: {1}", new Object[]{url, ex.getMessage()});
            throw new DatabaseException(ex);
        }
    }

    public final synchronized void releaseConnection(Connection connection) {
        closeConnection(connection);
//        if (pool != null) {
//            closeConnection(connection);
//        } else {
//            //when connection poole not used the single connection is closed in finalize()
//        }
    }

    private void closeConnection(Connection conn) {
        try {
            if (conn != null) {
                logger.log(Level.INFO, "Closing database connection");
                conn.close();
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Failed to close database connection: {0}", ex.getMessage());
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        closeConnection(singleConnection);
    }

    public abstract String buildUrl();
}
