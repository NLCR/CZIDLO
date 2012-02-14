/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence;

import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import java.sql.Connection;

/**
 *
 * @author Martin Řehánek
 */
public interface DatabaseConnector {

    public Connection getConnection() throws DatabaseException;

    public void releaseConnection(Connection connection) throws DatabaseException;
}
