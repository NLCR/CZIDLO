package cz.nkp.urnnbn.core.persistence.impl.transformations;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.nkp.urnnbn.core.dto.Statistic;
import cz.nkp.urnnbn.core.persistence.UrnNbnResolvationStatisticDAO;

public class UrnNbnResolvationStatisticRT implements ResultsetTransformer {

    @Override
    public Object transform(ResultSet resultSet) throws SQLException {
        Statistic result = new Statistic();
        result.setRegistrarCode(resultSet.getString(UrnNbnResolvationStatisticDAO.ATTR_REGISTRAR_CODE));
        result.setYear(resultSet.getInt(UrnNbnResolvationStatisticDAO.ATTR_YEAR));
        result.setMonth(resultSet.getInt(UrnNbnResolvationStatisticDAO.ATTR_MONTH));
        result.setVolume(resultSet.getInt(UrnNbnResolvationStatisticDAO.ATTR_RESOLVATIONS));
        return result;
    }
}
