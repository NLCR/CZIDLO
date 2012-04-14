/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.persistence.impl.transformations;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Martin Řehánek
 */
public class SingleIntRT implements ResultsetTransformer {

    @Override
    public Object transform(ResultSet resultSet) throws SQLException {
        Integer result = resultSet.getInt(1);
        if (resultSet.wasNull()) {
            return null;
        } else {
            return result;
        }
    }
}
