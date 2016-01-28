package cz.nkp.urnnbn.client.charts.widgets.topLevel;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import cz.nkp.urnnbn.client.charts.widgets.RegistrarColorMapChangeListener;
import cz.nkp.urnnbn.client.charts.widgets.RegistrarSelectionHandler;
import cz.nkp.urnnbn.client.charts.widgets.RegistrarStatisticsWidget;
import cz.nkp.urnnbn.client.charts.widgets.RegistrarsStatisticsWidget;
import cz.nkp.urnnbn.shared.charts.Registrar;
import cz.nkp.urnnbn.shared.charts.Statistic.Type;

public abstract class TopLevelRegistrarsWidget extends Composite {

	// fixed data
	private final List<Integer> years;
	private final Set<Registrar> registrars;

	// data
	private Map<String, String> registrarColorMapUntilRegistrarWidgetInitialized;

	// widgets
	private VerticalPanel container;
	private RegistrarsStatisticsWidget summaryWidget;
	private RegistrarStatisticsWidget registrarWidget;

	public TopLevelRegistrarsWidget(List<Integer> years, Set<Registrar> registrars) {
		this.years = years;
		this.registrars = registrars;
		initWidgets();
	}

	private void initWidgets() {
		ScrollPanel assignmentsScrollContainer = new ScrollPanel();
		container = new VerticalPanel();
		container.setStyleName(getContainerStyleName());
		summaryWidget = new RegistrarsStatisticsWidget(years, registrars, getStatisticsType(),//
				getGraphValueColors(), getGraphValueColorOther(), getGraphValueColorAll(),//
				buildRegistrarSelectionHandler(), buildRegistrarColorMapChangeListener());
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
			int widgetHeight = 300;
			int totalWidth = RegistrarsStatisticsWidget.LEFT_PANEL_WIDTH + RegistrarsStatisticsWidget.RIGHT_PANEL_WIDTH + 3
					* RegistrarsStatisticsWidget.WIDGET_SEPARATOR_SIZE;
			registrarWidget = new RegistrarStatisticsWidget(years, registrars, getStatisticsType(), getGraphValueColorOther(), totalWidth,
					widgetHeight);
			registrarWidget.setRegistrarColorMap(registrarColorMapUntilRegistrarWidgetInitialized);
			container.add(registrarWidget);
		}
		registrarWidget.setRegistrar(registrar);
	}

	abstract Type getStatisticsType();

	abstract String[] getGraphValueColors();

	abstract String getGraphValueColorOther();

	abstract String getGraphValueColorAll();

	abstract String getContainerStyleName();

}
