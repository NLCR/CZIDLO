/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.operations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import cz.nkp.urnnbn.core.persistence.exceptions.MultipleRecordsException;
import cz.nkp.urnnbn.core.persistence.exceptions.RecordNotFoundException;
import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import cz.nkp.urnnbn.core.persistence.impl.transformations.ResultsetTransformer;

/**
 *
 * @author Martin Řehánek
 */
public class SingleResultOperation implements DaoOperation {

    private final StatementWrapper statement;
    private final ResultsetTransformer transformer;

    public SingleResultOperation(StatementWrapper statement, ResultsetTransformer transformer) {
        this.statement = statement;
        this.transformer = transformer;
    }

    @Override
    public Object run(Connection connection) throws SQLException, RecordNotFoundException, MultipleRecordsException {
        PreparedStatement st = OperationUtils.preparedStatementFromWrapper(connection, statement);
        ResultSet resultSet = st.executeQuery();
        return getSingleResult(resultSet, transformer);
    }

    private Object getSingleResult(ResultSet resultSet, ResultsetTransformer transformer) throws SQLException, RecordNotFoundException,
            MultipleRecordsException {
        Object result = null;
        int found = 0;
        while (resultSet.next()) {
            result = transformer.transform(resultSet);
            found++;
        }
        if (found == 0) {
            throw new RecordNotFoundException();
        } else if (found != 1) {
            throw new MultipleRecordsException();
        } else {
            return result;
        }
    }
}
