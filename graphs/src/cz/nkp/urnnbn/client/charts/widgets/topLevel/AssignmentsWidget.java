package cz.nkp.urnnbn.client.charts.widgets.topLevel;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import cz.nkp.urnnbn.client.charts.widgets.RegistrarColorMapChangeListener;
import cz.nkp.urnnbn.client.charts.widgets.RegistrarSelectionHandler;
import cz.nkp.urnnbn.client.charts.widgets.RegistrarStatisticsWidget;
import cz.nkp.urnnbn.client.charts.widgets.RegistrarsStatisticsWidget;
import cz.nkp.urnnbn.shared.charts.Registrar;
import cz.nkp.urnnbn.shared.charts.Statistic;

public class AssignmentsWidget extends Composite {

	private static final Logger LOGGER = Logger.getLogger(AssignmentsWidget.class.getSimpleName());

	// data
	// http://paletton.com/#uid=7030u0kw0vSjzD3oSy0y9oLDhjs
	// primary-0, complement-2, secondery2-2, secondery-2
	// private final String[] graphValueColors = new String[] { "#FF6F63", "#00C222", "#03899C", "#FE7A00" };
	// complement-2, secondery2-2, secondery-2
	private final String[] graphValueColors = new String[] { "#00C222", "#03899C", "#FE7A00" };
	// primary-2
	// private final String neutralGraphValueColor = "FE1300";;
	// primary-0
	private final String neutralGraphValueColor = "#FF6F63";
	private final List<Integer> years;
	private final Set<Registrar> registrars;
	private Map<String, String> registrarColorMapUntilRegistrarWidgetInitialized;

	// widgets
	private VerticalPanel container;
	private RegistrarsStatisticsWidget summaryWidget;
	private RegistrarStatisticsWidget registrarWidget;

	public AssignmentsWidget(List<Integer> years, Set<Registrar> registrars) {
		this.years = years;
		this.registrars = registrars;
		initWidgets();
	}

	private void initWidgets() {
		ScrollPanel assignmentsScrollContainer = new ScrollPanel();
		// TODO: i18n
		container = new VerticalPanel();
		container.setWidth("100%");
		summaryWidget = new RegistrarsStatisticsWidget(years, registrars, Statistic.Type.URN_NBN_ASSIGNMENTS, graphValueColors,
				neutralGraphValueColor, buildRegistrarSelectionHandler(), buildRegistrarColorMapChangeListener());
		container.add(summaryWidget);
		assignmentsScrollContainer.add(container);
		initWidget(assignmentsScrollContainer);
	}

	private RegistrarColorMapChangeListener buildRegistrarColorMapChangeListener() {
		return new RegistrarColorMapChangeListener() {

			@Override
			public void onChanged(Map<String, String> map) {
				if (registrarWidget != null) {
					registrarWidget.setRegistrarColorMap(map);
					registrarWidget.redraw();
				} else {
					registrarColorMapUntilRegistrarWidgetInitialized = map;
				}
			}
		};
	}

	private RegistrarSelectionHandler buildRegistrarSelectionHandler() {
		return new RegistrarSelectionHandler() {

			@Override
			public void onSelected(String code) {
				LOGGER.info("onSelected");
				// TODO

				for (Registrar registrar : registrars) {
					if (registrar.getCode().equals(code)) {
						setRegistrar(registrar);
					}
				}
			}
		};
	}

	private void setRegistrar(Registrar registrar) {
		if (registrarWidget == null) {
			registrarWidget = new RegistrarStatisticsWidget(years, registrars, Statistic.Type.URN_NBN_ASSIGNMENTS, neutralGraphValueColor);
			registrarWidget.setRegistrarColorMap(registrarColorMapUntilRegistrarWidgetInitialized);
			container.add(registrarWidget);
		}
		registrarWidget.setRegistrar(registrar);
	}

}
