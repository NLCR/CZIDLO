/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.operations;

import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import cz.nkp.urnnbn.core.persistence.impl.transformations.ResultsetTransformer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Martin Řehánek
 */
public class MultipleResultsOperation implements DaoOperation {

    private final StatementWrapper statement;
    private final ResultsetTransformer transformer;

    public MultipleResultsOperation(StatementWrapper statement, ResultsetTransformer transformer) {
        this.statement = statement;
        this.transformer = transformer;
    }

    @Override
    public Object run(Connection connection) throws SQLException {
        PreparedStatement st = OperationUtils.preparedStatementFromWrapper(connection, statement);
        ResultSet resultSet = st.executeQuery();
        List operationResult = new ArrayList();
        //int resultCounter = 0;
        while (resultSet.next()) {
            //resultCounter++;
            operationResult.add(transformer.transform(resultSet));
        }
        //System.out.println("results:" + resultCounter);
        return operationResult;
    }
}
