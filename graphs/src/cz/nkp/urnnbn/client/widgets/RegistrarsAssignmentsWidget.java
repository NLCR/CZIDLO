package cz.nkp.urnnbn.client.widgets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

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

import cz.nkp.urnnbn.client.IntegerSelectionHandler;
import cz.nkp.urnnbn.client.Utils;

public class RegistrarsAssignmentsWidget extends TopLevelStatisticsWidget {

	private static final Logger LOGGER = Logger.getLogger(RegistrarsAssignmentsWidget.class.getSimpleName());

	// fixed data
	private final List<Integer> years;
	private final List<Integer> months = initMonths();
	private Map<String, String> registraNames = null;

	// data
	private Map<Integer, Map<String, Integer>> currentData;// period(year/month) -> registrar_code -> assignments_in_period
	private Integer currentYear = null;
	private Map<Integer, Map<String, Integer>> accumulatedVolumeBeforeYear; // year -> registrar_code -> all_assignments_before_this_year 
	//TODO: neni to VCETNE tohohle roku?


	// widgets
	private final Label title;
	private final ListBox timePeriods;
	private final RadioButton stateAll;
	private final RadioButton stateActiveOnly;
	private final RadioButton stateDeactivatedOnly;
	private final CheckBox accumulated;
	private final SingleItemColumnChart totalColumnChart;
	private final TopNRegistrarsPieChart registrarsRatioPiechart;
	private final TopNRegistrarsAccumulatedAreaChart registrarsAccumulatedAreaChart;

	public RegistrarsAssignmentsWidget(List<Integer> years) {
		this.years = years;

		// container
		VerticalPanel container = new VerticalPanel();
		container.setSpacing(5);
		container.setWidth("100%");
		RootLayoutPanel.get().add(container);

		// header
		VerticalPanel header = new VerticalPanel();
		header.setSpacing(10);
		// header.setWidth("100%");
		header.setWidth("1300px");
		container.add(header);

		title = new Label("Statistiky přiřazených URN:NBN");
		header.add(title);

		// year filter
		timePeriods = createTimePeriods();
		HorizontalPanel headerYears = new HorizontalPanel();
		headerYears.add(timePeriods);
		header.add(headerYears);

		// urn state filter
		// TODO: i18n
		stateAll = createUrnStateRadibutton("all", true);
		stateActiveOnly = createUrnStateRadibutton("active only", false);
		stateDeactivatedOnly = createUrnStateRadibutton("decativated only", false);
		HorizontalPanel urnStateFilterPanel = new HorizontalPanel();
		urnStateFilterPanel.add(stateAll);
		urnStateFilterPanel.add(stateActiveOnly);
		urnStateFilterPanel.add(stateDeactivatedOnly);
		header.add(urnStateFilterPanel);

		// accumulated filter
		accumulated = createAccumulatedCheckbox();
		header.add(accumulated);

		// registrar ratio chart
		registrarsRatioPiechart = new TopNRegistrarsPieChart();
		container.add(registrarsRatioPiechart);

		// registrar accumulated volume area chart
		registrarsAccumulatedAreaChart = new TopNRegistrarsAccumulatedAreaChart();
		container.add(registrarsAccumulatedAreaChart);

		// total chart
		totalColumnChart = createTotalChart();
		container.add(totalColumnChart);

		initWidget(container);
		setStyleName("RegistrarssGraph");
		// TODO: mozna posilat v konstruktoru jmena registratoru
		service.getRegistrarNames(new AsyncCallback<Map<String, String>>() {

			@Override
			public void onSuccess(Map<String, String> result) {
				registraNames = result;
				loadData(currentYear);
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}
		});

	}

	private ListBox createTimePeriods() {
		ListBox result = new ListBox();
		result.addItem("celé období");
		for (Integer year : years) {
			result.addItem(year.toString());
		}
		result.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				int index = timePeriods.getSelectedIndex();
				if (index == 0 || years.isEmpty()) {
					currentYear = null;
				} else {
					currentYear = years.get(index - 1);
				}
				loadData(currentYear);
			}
		});
		return result;
	}

	private RadioButton createUrnStateRadibutton(String title, boolean selected) {
		RadioButton result = new RadioButton("registrars-urn-state", title);
		result.setValue(selected);
		result.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if (event.getValue()) {
					loadData(currentYear);
				}
			}
		});
		return result;
	}

	private CheckBox createAccumulatedCheckbox() {
		CheckBox result = new CheckBox("kumulované");
		result.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				redrawCharts();
			}

		});
		return result;
	}

	private SingleItemColumnChart createTotalChart() {
		SingleItemColumnChart result = new SingleItemColumnChart();
		result.setHandler(new IntegerSelectionHandler() {

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
					loadData(currentYear);
				}
			}
		});
		return result;
	}

	private void loadData(final Integer year) {
		AsyncCallback<Map<Integer, Map<String, Integer>>> callback = new AsyncCallback<Map<Integer, Map<String, Integer>>>() {

			@Override
			public void onSuccess(Map<Integer, Map<String, Integer>> result) {
				currentYear = year;
				currentData = result;
				if (year == null) {
					accumulatedVolumeBeforeYear = Utils.accumulate(years, Utils.extractAllRegistrarCodes(currentData), null, currentData);
				}
				redrawCharts();
			}

			@Override
			public void onFailure(Throwable caught) {
				LOGGER.severe(caught.getMessage());
			}
		};
		boolean includeActive = stateAll.getValue() || stateActiveOnly.getValue();
		boolean includeDeactivated = stateAll.getValue() || stateDeactivatedOnly.getValue();

		if (year != null) {
			service.getAssignmentsByMonth(year, includeActive, includeDeactivated, callback);
		} else {
			service.getAssignmentsByYear(includeActive, includeDeactivated, callback);
		}
	}

	private void redrawCharts() {
		if (totalColumnChart != null) {
			List<Integer> keys = currentYear != null ? months : years;
			Map<Integer, Integer> data = agregate(keys, currentData);
			// TODO: i18n
			String title = currentYear != null ? "Počet přiřazení URN:NBN za rok " + currentYear : "Počet přiřazení URN:NBN za celé období";
			// String valueLabel = currentRegistrar != null ? currentRegistrar.getCode() : "celkově";
			// String valueLabel = "TODO:valueLabel";
			String valueLabel = null;
			String xAxisLabel = currentYear != null ? "měsíc v roce " + currentYear : "rok";
			String yAxisLabel = accumulated.getValue() ? "Přiřazení (kumulované)" : "Přiřazení";
			Map<Integer, String> columnLabels = currentYear == null ? null : getMonthLabels();
			totalColumnChart.setDataAndDraw(keys, data, accumulated.getValue(), title, valueLabel, xAxisLabel, yAxisLabel, columnLabels);
		}
		if (registrarsRatioPiechart != null) {
			int totalAssignments = computeTotalAssignments();
			Map<String, Integer> assignmentsByRegistrar = computeAssignmentsByRegistrar();
			registrarsRatioPiechart.setDataAndDraw(totalAssignments, assignmentsByRegistrar, registraNames);
		}
		if (registrarsAccumulatedAreaChart != null) {
			List<Integer> keys = currentYear != null ? months : years;
			//TODO: nema tady byt -1
			Map<String, Integer> volumeBeforeFistPeriod = currentYear == null ? null : accumulatedVolumeBeforeYear.get(currentYear);
			// TODO: i18n
			String title = currentYear != null ? "Měsíčný vývoj počtu URN:NBN v roce " + currentYear : "Roční vývoj počtu URN:NBN";
			String xAxisLabel = currentYear != null ? "měsíc v roce " + currentYear : "rok";
			String yAxisLabel = "Počet";
			Map<Integer, String> columnLabels = currentYear == null ? null : getMonthLabels();
			registrarsAccumulatedAreaChart.setDataAndDraw(keys, registraNames, volumeBeforeFistPeriod, currentData, title, xAxisLabel, yAxisLabel,
					columnLabels);
		}
	}

	private Map<String, Integer> computeAssignmentsByRegistrar() {
		Map<String, Integer> result = new HashMap<String, Integer>();
		for (Map<String, Integer> map : currentData.values()) {
			for (String registrarCode : map.keySet()) {
				int totalForRegistrar = result.containsKey(registrarCode) ? result.get(registrarCode) : 0;
				totalForRegistrar += map.get(registrarCode);
				result.put(registrarCode, totalForRegistrar);
			}
		}
		return result;
	}

	private int computeTotalAssignments() {
		int sum = 0;
		for (Map<String, Integer> map : currentData.values()) {
			for (Integer value : map.values()) {
				sum += value;
			}
		}
		return sum;
	}

	private Map<Integer, Integer> agregate(List<Integer> periods, Map<Integer, Map<String, Integer>> input) {
		Map<Integer, Integer> result = new HashMap<>();
		for (Integer period : periods) {
			Integer sum = 0;
			if (input != null) {
				Map<String, Integer> registrars = input.get(period);
				if (registrars != null) {
					for (Integer perRegistrar : registrars.values()) {
						sum += perRegistrar;
					}
				}
			}
			result.put(period, sum);
		}
		return result;
	}

}
