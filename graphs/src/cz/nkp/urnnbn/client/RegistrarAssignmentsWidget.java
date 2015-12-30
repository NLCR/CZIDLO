package cz.nkp.urnnbn.client;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import cz.nkp.urnnbn.client.IntegerKeyColumnChart.IntegerSelectionHandler;
import cz.nkp.urnnbn.shared.Registrar;

public class RegistrarAssignmentsWidget extends AbstractStatisticsWidget {

	private static final Logger logger = Logger.getLogger(RegistrarAssignmentsWidget.class.getSimpleName());

	private final StatisticsServiceAsync service = GWT.create(StatisticsService.class);

	// data
	private Registrar currentRegistrar;
	private List<Integer> years = Collections.emptyList();
	private List<Integer> months = initMonths();
	private Map<Integer, Integer> currentData;
	private Integer currentYear = null;

	// widgets
	private Label title = new Label();
	private ListBox timePeriods;
	private RadioButton stateAll;
	private RadioButton stateActiveOnly;
	private RadioButton stateDeactivatedOnly;
	private CheckBox accumulated;
	private IntegerKeyColumnChart chart;

	public RegistrarAssignmentsWidget() {
		// container
		VerticalPanel container = new VerticalPanel();
		container.setWidth("100%");
		RootLayoutPanel.get().add(container);

		// header
		VerticalPanel header = new VerticalPanel();
		header.setSpacing(10);
		header.setWidth("100%");
		container.add(header);

		// label
		header.add(title);

		// year selection
		HorizontalPanel headerYears = new HorizontalPanel();
		header.add(headerYears);
		fetchAvaliableYears(headerYears);

		// state filter - all, active, deactivated
		HorizontalPanel urnStateFilterPanel = new HorizontalPanel();
		header.add(urnStateFilterPanel);
		initUrnStateFilter(urnStateFilterPanel);

		// accumulated
		accumulated = new CheckBox("kumulované");
		accumulated.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				redrawChart();
			}
		});
		header.add(accumulated);

		// chart
		chart = new IntegerKeyColumnChart();
		chart.setHandler(new IntegerSelectionHandler() {

			@Override
			public void onSelected(Integer key) {
				if (key > 12) { // year
					for (int position = 0; position < years.size(); position++) {
						int year = years.get(position);
						if (year == key) {
							currentYear = key;
							timePeriods.setSelectedIndex(position + 1);
							break;
						}
					}
					loadData(currentRegistrar, currentYear);
				}
			}
		});
		container.add(chart.getWidget());

		initWidget(container);
		setStyleName("RegistrarAssignmentsGraph");

	}

	private void initUrnStateFilter(HorizontalPanel urnStateFilter) {
		ValueChangeHandler<Boolean> handler = new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if (event.getValue()) {
					loadData(currentRegistrar, currentYear);
				}

			}
		};
		// all
		stateAll = new RadioButton("urn-state-filter", "všechny");
		stateAll.addValueChangeHandler(handler);
		stateAll.setValue(true);
		urnStateFilter.add(stateAll);
		// active only
		stateActiveOnly = new RadioButton("urn-state-filter", "jen aktivní");
		stateActiveOnly.addValueChangeHandler(handler);
		urnStateFilter.add(stateActiveOnly);
		// deactivated only
		stateDeactivatedOnly = new RadioButton("urn-state-filter", "jen deaktivované");
		stateDeactivatedOnly.addValueChangeHandler(handler);
		urnStateFilter.add(stateDeactivatedOnly);
	}

	private void fetchAvaliableYears(final HorizontalPanel headerYears) {
		service.getYearsSorted(new AsyncCallback<List<Integer>>() {

			@Override
			public void onSuccess(List<Integer> result) {
				years = result;
				// views
				timePeriods = new ListBox();
				timePeriods.addItem("celé období");
				for (Integer year : result) {
					timePeriods.addItem(year.toString());
				}
				timePeriods.addChangeHandler(new ChangeHandler() {

					@Override
					public void onChange(ChangeEvent event) {
						int index = timePeriods.getSelectedIndex();
						if (index == 0 || years.isEmpty()) {
							currentYear = null;
						} else {
							currentYear = years.get(index - 1);
						}
						loadData(currentRegistrar, currentYear);
					}
				});

				headerYears.add(timePeriods);
			}

			@Override
			public void onFailure(Throwable caught) {
				logger.severe(caught.getMessage());
			}
		});

	}

	private void loadData(final Registrar registrar, final Integer year) {
		AsyncCallback<Map<Integer, Integer>> callback = new AsyncCallback<Map<Integer, Integer>>() {

			@Override
			public void onSuccess(Map<Integer, Integer> result) {
				currentRegistrar = registrar;
				currentYear = year;
				currentData = result;
				redrawChart();
			}

			@Override
			public void onFailure(Throwable caught) {
				logger.severe(caught.getMessage());
			}
		};
		boolean includeActive = stateAll.getValue() || stateActiveOnly.getValue();
		boolean includeDeactivated = stateAll.getValue() || stateDeactivatedOnly.getValue();

		if (registrar != null) {
			if (year != null) {
				service.getAssignmentsByMonth(registrar.getCode(), year, includeActive, includeDeactivated, callback);
			} else {
				service.getAssignmentsByYear(registrar.getCode(), includeActive, includeDeactivated, callback);
			}
		} else {
			if (year != null) {
				service.getTotalAssignmentsByMonth(year, includeActive, includeDeactivated, callback);
			} else {
				service.getTotalAssignmentsByYear(includeActive, includeDeactivated, callback);
			}
		}
	}

	private void redrawChart() {
		if (chart != null) {
			// TODO: i18n
			String yLabel = accumulated.getValue() ? "přiřazení (kumulované)" : "přiřazení";
			String xLabel = currentYear != null ? "rok" : "měsíc";
			String valueLabel = currentRegistrar != null ? currentRegistrar.getCode() : "celkově";
			String title = currentYear != null ? "Počet přiřazení URN:NBN za rok " + currentYear : "Počet přiřazení URN:NBN za celé období";
			List<Integer> keys = currentYear != null ? months : years;
			chart.setDataAndDraw(keys, currentData, title, xLabel, yLabel, valueLabel, accumulated.getValue());
		}
	}

	public void setRegistrar(Registrar registrar) {
		String registrarName = registrar != null ? registrar.getName() : null;
		title.setText(registrarName);
		currentRegistrar = registrar;
		loadData(currentRegistrar, currentYear);
	}

}
