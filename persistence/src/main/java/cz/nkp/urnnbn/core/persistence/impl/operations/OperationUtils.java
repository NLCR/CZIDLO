/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.operations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cz.nkp.urnnbn.core.persistence.impl.StatementWrapper;
import cz.nkp.urnnbn.core.persistence.impl.transformations.ResultsetTransformer;
import cz.nkp.urnnbn.core.persistence.impl.transformations.SingleLongRT;

/**
 * 
 * @author Martin Řehánek
 */
public abstract class OperationUtils {

    public static Long resultSet2Long(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            // return resultSet.getLong("nextval");
            return resultSet.getLong(1);
        }
        return null;
    }

    public static Integer resultSet2Integer(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            // return resultSet.getLong("nextval");
            return resultSet.getInt(1);
        }
        return null;
    }

    public static PreparedStatement preparedStatementFromWrapper(Connection conn, StatementWrapper wrapper) throws SQLException {
        PreparedStatement st = conn.prepareStatement(wrapper.preparedStatement());
        wrapper.populate(st);
        return st;
    }

    public static List<Long> resultSet2ListOfLong(ResultSet resultSet) throws SQLException {
        List<Long> result = new ArrayList<Long>();
        ResultsetTransformer transformer = new SingleLongRT();
        while (resultSet.next()) {
            Long id = (Long) transformer.transform(resultSet);
            result.add(id);
        }
        return result;
    }
}
