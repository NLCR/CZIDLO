/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.operations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;

/**
 *
 * @author Martin Řehánek
 */
public class NoResultOperation implements DaoOperation {

    private final StatementWrapper statement;

    public NoResultOperation(StatementWrapper statement) {
        this.statement = statement;
    }

    @Override
    public Object run(Connection connection) throws SQLException {
        PreparedStatement st = OperationUtils.preparedStatementFromWrapper(connection, statement);
        st.executeUpdate();
        return null;
    }
}
