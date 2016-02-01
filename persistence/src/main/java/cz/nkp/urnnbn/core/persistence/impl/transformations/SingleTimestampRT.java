package cz.nkp.urnnbn.core.persistence.impl.transformations;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class SingleTimestampRT implements ResultsetTransformer {

    @Override
    public Object transform(ResultSet resultSet) throws SQLException {
        Timestamp result = resultSet.getTimestamp(1);
        if (resultSet.wasNull()) {
            return null;
        } else {
            return result;
        }
    }

}
