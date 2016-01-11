package cz.nkp.urnnbn.client;

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
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;

import cz.nkp.urnnbn.client.widgets.RegistrarAssignmentsWidget;
import cz.nkp.urnnbn.client.widgets.RegistrarsAssignmentsWidget;
import cz.nkp.urnnbn.shared.Registrar;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Graphs implements EntryPoint {

	private static final Logger LOGGER = Logger.getLogger(Graphs.class.getSimpleName());

	private final StatisticsServiceAsync service = GWT.create(StatisticsService.class);

	// data
	private List<Integer> years;

	// widgets
	private TabLayoutPanel tabPanel;

	private VerticalPanel registrarsContainer;
	private VerticalPanel registrarContainer;

	// private VerticalPanel header;
	private HorizontalPanel registrarSelection;
	private RegistrarAssignmentsWidget registrarGraph;
	private RegistrarsAssignmentsWidget registrarsGraph;

	/**
	 * This is the entry point method.
	 */
	@Override
	public void onModuleLoad() {
		// logger.info("onModuleLoad");

		Window.enableScrolling(true);
		Window.setMargin("0px");

		tabPanel = new TabLayoutPanel(1.5, Unit.EM);
		RootLayoutPanel.get().add(tabPanel);

		// registrars
		registrarsContainer = new VerticalPanel();
		registrarsContainer.setWidth("100%");
		tabPanel.add(registrarsContainer, "registrars");

		// registrar
		registrarContainer = new VerticalPanel();
		registrarContainer.setWidth("100%");
		tabPanel.add(registrarContainer, "registrar");
		// registrar selection
		registrarSelection = new HorizontalPanel();
		registrarContainer.add(registrarSelection);

		initData();
	}

	private void initData() {
		// years
		service.getYearsSorted(new AsyncCallback<List<Integer>>() {

			@Override
			public void onSuccess(List<Integer> result) {
				years = result;
				initRegistrarsAndChartLoader();
			}

			private void initRegistrarsAndChartLoader() {
				service.getRegistrars(new AsyncCallback<Set<Registrar>>() {

					@Override
					public void onSuccess(Set<Registrar> result) {

						for (Registrar registrar : result) {
							RadioButton registrarButton = new RadioButton("registrars", registrar.getCode());
							registrarSelection.add(registrarButton);
							final Registrar thisRegistrar = registrar;
							registrarButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

								@Override
								public void onValueChange(ValueChangeEvent<Boolean> event) {
									boolean selected = event.getValue();
									if (selected) {
										if (registrarGraph != null) {
											registrarGraph.setRegistrar(thisRegistrar);
										}
										// loadData(thisRegistrar, currentYear);
									}
								}
							});
						}
						initChartLoader();
					}

					private void initChartLoader() {
						ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
						chartLoader.loadApi(new Runnable() {

							@Override
							public void run() {
								LOGGER.info("chart api loaded");
								// charts
								registrarGraph = new RegistrarAssignmentsWidget(years);
								registrarContainer.add(registrarGraph);
								registrarsGraph = new RegistrarsAssignmentsWidget(years);
								registrarsContainer.add(registrarsGraph);
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
