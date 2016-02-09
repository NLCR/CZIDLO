package cz.nkp.urnnbn.core.persistence.impl.transformations;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EmptyRT implements ResultsetTransformer {

    @Override
    public Object transform(ResultSet resultSet) throws SQLException {
        return null;
    }

}
