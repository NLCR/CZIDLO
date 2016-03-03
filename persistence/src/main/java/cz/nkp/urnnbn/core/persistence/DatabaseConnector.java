/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence;

import java.sql.Connection;

import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;

/**
 *
 * @author Martin Řehánek
 */
public interface DatabaseConnector {

    public Connection getConnection() throws DatabaseException;

    public void releaseConnection(Connection connection) throws DatabaseException;
}
