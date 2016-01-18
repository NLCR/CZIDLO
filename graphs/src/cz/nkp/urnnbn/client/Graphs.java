package cz.nkp.urnnbn.client;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;

import cz.nkp.urnnbn.client.charts.StatisticsService;
import cz.nkp.urnnbn.client.charts.StatisticsServiceAsync;
import cz.nkp.urnnbn.client.charts.widgets.RegistrarStatisticsWidget;
import cz.nkp.urnnbn.client.charts.widgets.RegistrarsStatisticsWidget;
import cz.nkp.urnnbn.client.charts.widgets.RegistrarSelectionHandler;
import cz.nkp.urnnbn.shared.charts.Registrar;
import cz.nkp.urnnbn.shared.charts.Statistic;

public class Graphs implements EntryPoint {

	private static final Logger LOGGER = Logger.getLogger(Graphs.class.getSimpleName());

	private final StatisticsServiceAsync service = GWT.create(StatisticsService.class);

	// data
	private List<Integer> years;
	private Set<Registrar> registrars;

	// widgets
	private TabLayoutPanel tabPanel;

	// urn:nbn assignments widgets
	private VerticalPanel assignmentsPanel;
	private RegistrarsStatisticsWidget assignmentsGlobalWidget;
	private RegistrarStatisticsWidget assignmentsRegistrarWidget;

	// urn:nbn resolvations widgets
	private VerticalPanel resolvationsPanel;
	private RegistrarsStatisticsWidget resolvationsGlobalWidget;
	private RegistrarStatisticsWidget resolvationsRegistrarWidget;

	// random registrar widgets
	private VerticalPanel randomRegistrarPanel;
	private RegistrarStatisticsWidget randomRegistrarAssignmentsWidget;
	private RegistrarStatisticsWidget randomRegistrarResolvationsWidget;

	@Override
	public void onModuleLoad() {
		// logger.info("onModuleLoad");
		Window.enableScrolling(true);
		Window.setMargin("0px");
		tabPanel = new TabLayoutPanel(1.5, Unit.EM);
		RootLayoutPanel.get().add(tabPanel);

		// URN:NBN assignments
		ScrollPanel assignmentsScrollContainer = new ScrollPanel();
		// TODO: i18n
		tabPanel.add(assignmentsScrollContainer, "Přiřazení");
		assignmentsPanel = new VerticalPanel();
		assignmentsPanel.setWidth("100%");
		assignmentsScrollContainer.add(assignmentsPanel);

		// URN:NBN resolvations
		ScrollPanel resolvationsScrollContainer = new ScrollPanel();
		// TODO: i18n
		tabPanel.add(resolvationsScrollContainer, "Rezolvování");
		resolvationsPanel = new VerticalPanel();
		resolvationsPanel.setWidth("100%");
		resolvationsScrollContainer.add(resolvationsPanel);
		// tabPanel.add(new Label("todo: resolvation statistics"), "resolvations");

		// random registrar
		ScrollPanel randomRegistrarContainer = new ScrollPanel();
		tabPanel.add(randomRegistrarContainer, "random registrar");
		randomRegistrarPanel = new VerticalPanel();
		randomRegistrarPanel.setWidth("100%");
		randomRegistrarContainer.add(randomRegistrarPanel);

		initData();
	}

	private void initData() {
		// years
		service.getAvailableYearsSorted(new AsyncCallback<List<Integer>>() {

			@Override
			public void onSuccess(List<Integer> result) {
				years = result;
				initRegistrarsAndChartLoader();
			}

			private void initRegistrarsAndChartLoader() {
				service.getRegistrars(new AsyncCallback<Set<Registrar>>() {

					@Override
					public void onSuccess(Set<Registrar> result) {
						Graphs.this.registrars = result;
						initChartLoader();
					}

					private void initChartLoader() {
						ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
						chartLoader.loadApi(new Runnable() {

							@Override
							public void run() {
								LOGGER.info("chart api loaded");
								RegistrarSelectionHandler registrarSelectionHandler = buildRegistrarSelectionHandler();

								// assignments
								assignmentsGlobalWidget = new RegistrarsStatisticsWidget(years, registrars, Statistic.Type.URN_NBN_ASSIGNMENTS,
										registrarSelectionHandler);
								assignmentsPanel.add(assignmentsGlobalWidget);

								// resolvations
								resolvationsGlobalWidget = new RegistrarsStatisticsWidget(years, registrars, Statistic.Type.URN_NBN_RESOLVATIONS,
										registrarSelectionHandler);
								resolvationsPanel.add(resolvationsGlobalWidget);

								// random registrar
								Set<Registrar> singleRegistrarSet = new HashSet<>();
								singleRegistrarSet.add((Registrar) registrars.toArray()[0]);
								// assignments
								randomRegistrarAssignmentsWidget = new RegistrarStatisticsWidget(years, singleRegistrarSet,
										Statistic.Type.URN_NBN_ASSIGNMENTS, null, null);
								randomRegistrarAssignmentsWidget.setWidth("100%");
								randomRegistrarPanel.add(randomRegistrarAssignmentsWidget);
								// resolvations
								randomRegistrarResolvationsWidget = new RegistrarStatisticsWidget(years, singleRegistrarSet,
										Statistic.Type.URN_NBN_RESOLVATIONS, null, null);
								randomRegistrarResolvationsWidget.setWidth("100%");
								randomRegistrarPanel.add(randomRegistrarResolvationsWidget);

								// tabPanel.add(randomRegistrarAssignmentsWidget, "random registrar");
							}
						});
					}

					@Override
					public void onFailure(Throwable caught) {
						LOGGER.severe(caught.getMessage());
					}
				});

			}

			@Override
			public void onFailure(Throwable caught) {
				LOGGER.severe(caught.getMessage());
			}
		});
	}

	private RegistrarSelectionHandler buildRegistrarSelectionHandler() {
		return new RegistrarSelectionHandler() {

			@Override
			public void onSelected(String code, String color) {
				for (Registrar registrar : registrars) {
					if (registrar.getCode().equals(code)) {
						setRegistrar(registrar, color);
					}
				}
			}
		};
	}

	private void setRegistrar(Registrar registrar, String color) {
		if (assignmentsRegistrarWidget == null) {
			assignmentsRegistrarWidget = new RegistrarStatisticsWidget(years, registrars, Statistic.Type.URN_NBN_ASSIGNMENTS, null, null);
			assignmentsPanel.add(assignmentsRegistrarWidget);
		}
		// assignmentsRegistrarWidget.setColor(color);
		assignmentsRegistrarWidget.setRegistrar(registrar);
		if (resolvationsRegistrarWidget == null) {
			resolvationsRegistrarWidget = new RegistrarStatisticsWidget(years, registrars, Statistic.Type.URN_NBN_RESOLVATIONS, null, null);
			resolvationsPanel.add(resolvationsRegistrarWidget);
		}
		// resolvationsRegistrarWidget.setColor(color);
		resolvationsRegistrarWidget.setRegistrar(registrar);
	}

}
