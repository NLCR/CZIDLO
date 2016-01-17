package cz.nkp.urnnbn.client;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
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
import cz.nkp.urnnbn.client.charts.widgets.StringSelectionHandler;
import cz.nkp.urnnbn.shared.charts.Registrar;

public class Graphs implements EntryPoint {

	private static final Logger LOGGER = Logger.getLogger(Graphs.class.getSimpleName());

	private final StatisticsServiceAsync service = GWT.create(StatisticsService.class);

	// data
	private List<Integer> years;
	private Set<Registrar> registrars;

	// widgets
	private TabLayoutPanel tabPanel;

	// urn:nbn assignment widgets
	private VerticalPanel assignmentsPanel;
	private RegistrarsStatisticsWidget assignmentsGlobalWidget;
	// private HorizontalPanel assignmentsRegistrarSelectionPanel;
	private RegistrarStatisticsWidget assignmentsRegistrarWidget;

	// random registrar
	private RegistrarStatisticsWidget randomRegistrarAssignmentsWidget;

	@Override
	public void onModuleLoad() {
		// logger.info("onModuleLoad");

		Window.enableScrolling(true);
		Window.setMargin("0px");

		// tabs
		tabPanel = new TabLayoutPanel(1.5, Unit.EM);
		RootLayoutPanel.get().add(tabPanel);

		// URN:NBN assignments
		ScrollPanel assignmentsScrollContainer = new ScrollPanel();
		tabPanel.add(assignmentsScrollContainer, "assignments");
		assignmentsPanel = new VerticalPanel();
		assignmentsPanel.setWidth("100%");
		assignmentsScrollContainer.add(assignmentsPanel);

		// URN:NB resolvations
		tabPanel.add(new Label("todo: resolvation statistics"), "resolvations");

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
								StringSelectionHandler registrarSelectionHandler = buildRegistrarSelectionHandler();
								// assignments
								assignmentsGlobalWidget = new RegistrarsStatisticsWidget(years, registrars, registrarSelectionHandler);
								assignmentsPanel.add(assignmentsGlobalWidget);

								// resolvations
								// TODO

								// random registrar
								Set<Registrar> singleRegistrarSet = new HashSet<>();
								singleRegistrarSet.add((Registrar) registrars.toArray()[0]);
								randomRegistrarAssignmentsWidget = new RegistrarStatisticsWidget(years, singleRegistrarSet);
								tabPanel.add(randomRegistrarAssignmentsWidget, "random registrar");
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

	private StringSelectionHandler buildRegistrarSelectionHandler() {
		return new StringSelectionHandler() {

			@Override
			public void onSelected(String code) {
				for (Registrar registrar : registrars) {
					if (registrar.getCode().equals(code)) {
						setAssignmentsRegistrar(registrar);
					}
				}
			}
		};
	}

	private void setAssignmentsRegistrar(Registrar registrar) {
		if (assignmentsRegistrarWidget == null) {
			assignmentsRegistrarWidget = new RegistrarStatisticsWidget(years, registrars);
			assignmentsPanel.add(assignmentsRegistrarWidget);
		}
		assignmentsRegistrarWidget.setRegistrar(registrar);
	}

}
