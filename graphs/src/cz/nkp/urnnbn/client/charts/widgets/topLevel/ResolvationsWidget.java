package cz.nkp.urnnbn.client.charts.widgets.topLevel;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import cz.nkp.urnnbn.client.charts.widgets.RegistrarSelectionHandler;
import cz.nkp.urnnbn.client.charts.widgets.RegistrarStatisticsWidget;
import cz.nkp.urnnbn.client.charts.widgets.RegistrarsStatisticsWidget;
import cz.nkp.urnnbn.shared.charts.Registrar;
import cz.nkp.urnnbn.shared.charts.Statistic;

public class ResolvationsWidget extends Composite {
	private static final Logger LOGGER = Logger.getLogger(ResolvationsWidget.class.getSimpleName());

	// data
	private final List<Integer> years;
	private final Set<Registrar> registrars;
	// private String valueColor;

	// widgets
	private VerticalPanel container;
	private RegistrarsStatisticsWidget summaryWidget;
	private RegistrarStatisticsWidget registrarWidget;

	public ResolvationsWidget(List<Integer> years, Set<Registrar> registrars) {
		this.years = years;
		this.registrars = registrars;
		initWidgets();
	}

	private void initWidgets() {
		ScrollPanel assignmentsScrollContainer = new ScrollPanel();
		// TODO: i18n
		container = new VerticalPanel();
		container.setWidth("100%");
		// TODO
		summaryWidget = new RegistrarsStatisticsWidget(years, registrars, Statistic.Type.URN_NBN_RESOLVATIONS, null, null,
				buildRegistrarSelectionHandler(), null);
		container.add(summaryWidget);
		assignmentsScrollContainer.add(container);
		initWidget(assignmentsScrollContainer);
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
			registrarWidget = new RegistrarStatisticsWidget(years, registrars, Statistic.Type.URN_NBN_RESOLVATIONS, null);
			container.add(registrarWidget);
		}
		registrarWidget.setRegistrar(registrar);
	}
}
