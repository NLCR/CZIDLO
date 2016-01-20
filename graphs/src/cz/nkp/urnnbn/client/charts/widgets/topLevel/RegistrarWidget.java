package cz.nkp.urnnbn.client.charts.widgets.topLevel;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import cz.nkp.urnnbn.client.charts.widgets.RegistrarStatisticsWidget;
import cz.nkp.urnnbn.shared.charts.Registrar;
import cz.nkp.urnnbn.shared.charts.Statistic;

public class RegistrarWidget extends Composite {

	private static final Logger LOGGER = Logger.getLogger(RegistrarWidget.class.getSimpleName());

	// data
	private final List<Integer> years;
	private final Registrar registrar;

	// widgets
	private VerticalPanel container;
	private RegistrarStatisticsWidget assignmentsWidget;
	private RegistrarStatisticsWidget resolvationsWidget;

	public RegistrarWidget(List<Integer> years, Registrar registrar) {
		this.years = years;
		this.registrar = registrar;
		initWidgets();
	}

	private void initWidgets() {
		ScrollPanel scrollContainer = new ScrollPanel();
		container = new VerticalPanel();
		//container.setWidth("100%");
		scrollContainer.add(container);

		Set<Registrar> singleRegistrarSet = new HashSet<>();
		singleRegistrarSet.add(registrar);
		// assignments
		assignmentsWidget = new RegistrarStatisticsWidget(years, singleRegistrarSet, Statistic.Type.URN_NBN_ASSIGNMENTS,
				ColorConstants.ASSIGNMENTS_VALUE_ALL);
		//assignmentsWidget.setWidth("100%");
		container.add(assignmentsWidget);
		// resolvations
		resolvationsWidget = new RegistrarStatisticsWidget(years, singleRegistrarSet, Statistic.Type.URN_NBN_RESOLVATIONS,
				ColorConstants.RESOLVATIONS_VALUE_ALL);
		//resolvationsWidget.setWidth("100%");
		container.add(resolvationsWidget);
		initWidget(scrollContainer);
	}

}
