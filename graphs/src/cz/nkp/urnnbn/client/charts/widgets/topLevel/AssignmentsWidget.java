package cz.nkp.urnnbn.client.charts.widgets.topLevel;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import cz.nkp.urnnbn.shared.charts.Registrar;
import cz.nkp.urnnbn.shared.charts.Statistic.Type;

public class AssignmentsWidget extends TopLevelRegistrarsWidget {

	private static final Logger LOGGER = Logger.getLogger(AssignmentsWidget.class.getSimpleName());

	public AssignmentsWidget(List<Integer> years, Set<Registrar> registrars) {
		super(years, registrars);
	}

	@Override
	Type getStatisticsType() {
		return Type.URN_NBN_ASSIGNMENTS;
	}

	@Override
	String[] getGraphValueColors() {
		return new String[] { ColorConstants.ASSIGNMENTS_VALUE_1, ColorConstants.ASSIGNMENTS_VALUE_2, ColorConstants.ASSIGNMENTS_VALUE_3 };
	}

	@Override
	String getGraphValueColorOther() {
		return ColorConstants.ASSIGNMENTS_VALUE_OTHER;
	}

	@Override
	String getGraphValueColorAll() {
		return ColorConstants.ASSIGNMENTS_VALUE_ALL;
	}

}
