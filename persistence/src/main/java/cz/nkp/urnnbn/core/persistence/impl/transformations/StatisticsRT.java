package cz.nkp.urnnbn.core.persistence.impl.transformations;

import java.sql.ResultSet;
import java.sql.SQLException;

import cz.nkp.urnnbn.core.dto.Statistic;
import cz.nkp.urnnbn.core.persistence.impl.postgres.statements.SelectStatistics;

public class StatisticsRT implements ResultsetTransformer {

	@Override
	public Object transform(ResultSet resultSet) throws SQLException {
		Statistic result = new Statistic();
		result.setRegistrarCode(resultSet.getString(SelectStatistics.RESULT_REGISTRAR_CODE));
		result.setMonth(resultSet.getInt(SelectStatistics.RESULT_MONTH));
		result.setYear(resultSet.getInt(SelectStatistics.RESULT_YEAR));
		result.setVolume(resultSet.getInt(SelectStatistics.RESULT_SUM));
		return result;
	}

}
