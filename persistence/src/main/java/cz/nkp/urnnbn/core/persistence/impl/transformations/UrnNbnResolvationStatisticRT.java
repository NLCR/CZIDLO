package cz.nkp.urnnbn.core.persistence.impl.transformations;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.nkp.urnnbn.core.dto.Statistic;
import cz.nkp.urnnbn.core.persistence.UrnNbnStatisticDAO;

public class UrnNbnResolvationStatisticRT implements ResultsetTransformer {

    @Override
    public Statistic transform(ResultSet resultSet) throws SQLException {
        Statistic result = new Statistic();
        result.setRegistrarCode(resultSet.getString(UrnNbnStatisticDAO.ATTR_REGISTRAR_CODE));
        result.setYear(resultSet.getInt(UrnNbnStatisticDAO.ATTR_YEAR));
        result.setMonth(resultSet.getInt(UrnNbnStatisticDAO.ATTR_MONTH));
        result.setVolume(resultSet.getInt(UrnNbnStatisticDAO.ATTR_SUM));
        return result;
    }
}
