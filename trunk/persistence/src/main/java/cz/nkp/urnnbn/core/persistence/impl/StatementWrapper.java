/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl;

import cz.nkp.urnnbn.core.persistence.exceptions.SyntaxException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public interface StatementWrapper {

    public String preparedStatement();

    /**
     * TODO: nepouzivat tam, kde se da pouzit OperationUtils.preparedStatementFromWrapper
     * zadne argumenty krome preparedStatement se v metode populate nepredavaji
     * vsechno v konstruktoru
     * @param st
     * @throws SQLException
     */
    public void populate(PreparedStatement st) throws SyntaxException;
}
