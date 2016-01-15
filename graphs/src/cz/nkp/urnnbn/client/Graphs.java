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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.sun.xml.internal.ws.api.ha.StickyFeature;

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
	private HorizontalPanel assignmentsRegistrarSelectionPanel;
	private RegistrarStatisticsWidget assignmentsRegistrarWidget;

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
						initAssignmentsRegistrarSelectionPanel();
						initChartLoader();
					}

					private void initAssignmentsRegistrarSelectionPanel() {
						assignmentsRegistrarSelectionPanel = new HorizontalPanel();
						for (Registrar registrar : registrars) {
							RadioButton registrarButton = new RadioButton("registrars", registrar.getCode());
							assignmentsRegistrarSelectionPanel.add(registrarButton);
							final Registrar thisRegistrar = registrar;
							registrarButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

								@Override
								public void onValueChange(ValueChangeEvent<Boolean> event) {
									boolean selected = event.getValue();
									if (selected) {
										setAssignmentsRegistrar(thisRegistrar);
									}
								}
							});
						}
					}

					private void initChartLoader() {
						ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
						chartLoader.loadApi(new Runnable() {

							@Override
							public void run() {
								LOGGER.info("chart api loaded");
								StringSelectionHandler registrarSelectionHandler = buildRegistrarSelectionHandler();
								// widgets
								assignmentsGlobalWidget = new RegistrarsStatisticsWidget(years, registrars, registrarSelectionHandler);
								assignmentsRegistrarWidget = new RegistrarStatisticsWidget(years);
								// add to panels
								assignmentsPanel.add(assignmentsGlobalWidget);
								assignmentsPanel.add(assignmentsRegistrarSelectionPanel);
								assignmentsPanel.add(assignmentsRegistrarWidget);
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
		if (assignmentsRegistrarWidget != null) {
			assignmentsRegistrarWidget.setRegistrar(registrar);
		}
	}

}
