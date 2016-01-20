package cz.nkp.urnnbn.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;

import cz.nkp.urnnbn.client.charts.StatisticsService;
import cz.nkp.urnnbn.client.charts.StatisticsServiceAsync;
import cz.nkp.urnnbn.client.charts.widgets.topLevel.AssignmentsWidget;
import cz.nkp.urnnbn.client.charts.widgets.topLevel.RegistrarWidget;
import cz.nkp.urnnbn.client.charts.widgets.topLevel.ResolvationsWidget;
import cz.nkp.urnnbn.shared.charts.Registrar;

public class Graphs implements EntryPoint {

	private static final Logger LOGGER = Logger.getLogger(Graphs.class.getSimpleName());

	private final StatisticsServiceAsync service = GWT.create(StatisticsService.class);

	// data
	private List<Integer> years;
	private Set<Registrar> registrars;

	// widgets
	private TabLayoutPanel tabPanel;
	private AssignmentsWidget assignmentsWidget;
	private ResolvationsWidget resolvationsWidget;
	private RegistrarWidget registrarWidget;

	@Override
	public void onModuleLoad() {
		// logger.info("onModuleLoad");
		Window.enableScrolling(true);
		Window.setMargin("0px");
		tabPanel = new TabLayoutPanel(1.5, Unit.EM);
		RootLayoutPanel.get().add(tabPanel);
		tabPanel.getElement().getStyle().setBackgroundColor("#eeeeee");
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

								// assignments tab
								assignmentsWidget = new AssignmentsWidget(years, registrars);
								tabPanel.add(assignmentsWidget, "Přiřazení");
								// resolvations tab
								resolvationsWidget = new ResolvationsWidget(years, registrars);
								tabPanel.add(resolvationsWidget, "Rezolvování");
								// random registrar tab
								Registrar randomRegistrar = getRandomRegistrar();
								registrarWidget = new RegistrarWidget(years, randomRegistrar);
								tabPanel.add(registrarWidget, randomRegistrar.getName());
							}

							private Registrar getRandomRegistrar() {
								List<Registrar> list = new ArrayList<>();
								list.addAll(registrars);
								int position = new Random().nextInt(list.size());
								return list.get(position);
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

}
