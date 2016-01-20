package cz.nkp.urnnbn.client.charts.widgets.topLevel;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import cz.nkp.urnnbn.shared.charts.Registrar;
import cz.nkp.urnnbn.shared.charts.Statistic.Type;

public class ResolvationsWidget extends TopLevelRegistrarsWidget {
	private static final Logger LOGGER = Logger.getLogger(ResolvationsWidget.class.getSimpleName());

	public ResolvationsWidget(List<Integer> years, Set<Registrar> registrars) {
		super(years, registrars);
	}

	@Override
	Type getStatisticsType() {
		return Type.URN_NBN_RESOLVATIONS;
	}

	@Override
	String[] getGraphValueColors() {
		return new String[] { ColorConstants.RESOLVATIONS_VALUE_1, ColorConstants.RESOLVATIONS_VALUE_2, ColorConstants.RESOLVATIONS_VALUE_3 };
	}

	@Override
	String getGraphValueColorOther() {
		return ColorConstants.RESOLVATIONS_VALUE_OTHER;
	}

	@Override
	String getGraphValueColorAll() {
		return ColorConstants.RESOLVATIONS_VALUE_ALL;
	}
	
	@Override
	String getContainerStyleName() {
		return "czidloChartResolvationsSummary";
	}

}
