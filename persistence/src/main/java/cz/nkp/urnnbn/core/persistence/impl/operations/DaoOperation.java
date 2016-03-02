/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.operations;

import java.sql.Connection;
import java.sql.SQLException;

import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.exceptions.PersistenceException;

/**
 * 
 * @author Martin Řehánek
 */
public interface DaoOperation {

    public Object run(Connection connection) throws SQLException, DatabaseException, PersistenceException;

}
