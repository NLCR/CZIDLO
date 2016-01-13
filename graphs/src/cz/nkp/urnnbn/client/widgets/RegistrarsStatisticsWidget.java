package cz.nkp.urnnbn.client.widgets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import cz.nkp.urnnbn.shared.Registrar;

public class RegistrarsStatisticsWidget extends TopLevelStatisticsWidget {

	private static final Logger LOGGER = Logger.getLogger(RegistrarsStatisticsWidget.class.getSimpleName());

	// fixed data
	private final List<Integer> years;
	private final List<Integer> months = initMonths();
	private Map<String, String> registraNames = null;

	// data
	private Map<Integer, Map<Integer, Map<String, Integer>>> data; // year -> month -> registrar_code -> statistics
	private Integer selectedYear = null;

	// widgets
	private final Label title;
	private final ListBox timePeriods;
	private final RadioButton stateAll;
	private final RadioButton stateActiveOnly;
	private final RadioButton stateDeactivatedOnly;
	private final SingleItemColumnChart currentStatisticsColumnChart;
	private final TopNRegistrarsPieChart registrarsRatioPiechart;
	private final TopNRegistrarsAccumulatedAreaChart accumulatedStatisticsAreaChart;

	public RegistrarsStatisticsWidget(List<Integer> years, Set<Registrar> registrars, StringSelectionHandler registrarSelectionHandler) {
		this.years = years;
		this.registraNames = extractRegistrarNames(registrars);

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

		title = new Label("Vizualizace přiřazení URN:NBN");
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

		// registrar ratio chart
		registrarsRatioPiechart = new TopNRegistrarsPieChart();
		registrarsRatioPiechart.setRegistrarSelectionHandler(registrarSelectionHandler);
		container.add(registrarsRatioPiechart);

		// assignments per period chart
		currentStatisticsColumnChart = new SingleItemColumnChart();
		currentStatisticsColumnChart.setYearSelectionHandler(createYearSelectionHandler());
		container.add(currentStatisticsColumnChart);

		// registrar accumulated volume area chart
		accumulatedStatisticsAreaChart = new TopNRegistrarsAccumulatedAreaChart();
		accumulatedStatisticsAreaChart.setRegistrarSelectionHandler(registrarSelectionHandler);
		container.add(accumulatedStatisticsAreaChart);

		initWidget(container);
		setStyleName("RegistrarssGraph");

		loadData(selectedYear);
	}

	private Map<String, String> extractRegistrarNames(Set<Registrar> registrars) {
		Map<String, String> result = new HashMap<>();
		if (registrars != null) {
			for (Registrar registrar : registrars) {
				result.put(registrar.getCode(), registrar.getName());
			}
		}
		return result;
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
					selectedYear = null;
				} else {
					selectedYear = years.get(index - 1);
				}
				loadData(selectedYear);
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
					loadData(selectedYear);
				}
			}
		});
		return result;
	}

	private IntegerSelectionHandler createYearSelectionHandler() {
		return new IntegerSelectionHandler() {

			@Override
			public void onSelected(Integer key) {
				if (key == null || key <= 12) { // not year
					selectedYear = null;
					timePeriods.setSelectedIndex(0);
				} else {
					for (int position = 0; position < years.size(); position++) {
						int year = years.get(position);
						if (year == key) {
							selectedYear = key;
							timePeriods.setSelectedIndex(position + 1);
							break;
						}
					}
				}
				loadData(selectedYear);
			}
		};
	}

	private void loadData(final Integer year) {
		boolean includeActive = stateAll.getValue() || stateActiveOnly.getValue();
		boolean includeDeactivated = stateAll.getValue() || stateDeactivatedOnly.getValue();
		service.getStatistics(includeActive, includeDeactivated, new AsyncCallback<Map<String, Map<Integer, Map<Integer, Integer>>>>() {

			@Override
			public void onSuccess(Map<String, Map<Integer, Map<Integer, Integer>>> result) {
				selectedYear = year;
				data = transform(result);
				redrawCharts();
			}

			private Map<Integer, Map<Integer, Map<String, Integer>>> transform(Map<String, Map<Integer, Map<Integer, Integer>>> input) {
				Map<Integer, Map<Integer, Map<String, Integer>>> result = new HashMap<>();
				for (Integer year : years) {
					Map<Integer, Map<String, Integer>> anualData = new HashMap<>();
					for (Integer month : months) {
						Map<String, Integer> monthData = new HashMap<>();
						for (String registrarCode : input.keySet()) {
							Integer statistics = input.get(registrarCode).get(year).get(month);
							monthData.put(registrarCode, statistics);
						}
						anualData.put(month, monthData);
					}
					result.put(year, anualData);
				}
				return result;
			}

			@Override
			public void onFailure(Throwable caught) {
				LOGGER.severe(caught.getMessage());
			}
		});
	}

	private void redrawCharts() {
		if (data != null) {
			Set<String> registrarCodes = extractRegistrarCodes(data);
			Map<Integer, Map<String, Integer>> currentData = selectedYear != null ? data.get(selectedYear) : aggregateYearlyData(registrarCodes);
			List<Integer> periods = selectedYear != null ? months : years;
			if (currentStatisticsColumnChart != null) {
				Map<Integer, Integer> aggregatedData = agregate(periods, currentData);
				// TODO: i18n
				String title = selectedYear != null ? "Přiřazení URN:NBN za rok " + selectedYear : "Přiřazení URN:NBN za celé období";
				// String valueLabel = currentRegistrar != null ? currentRegistrar.getCode() : "celkově";
				// String valueLabel = "TODO:valueLabel";
				String valueLabel = null;
				String xAxisLabel = selectedYear != null ? "měsíc v roce " + selectedYear : "rok";
				String yAxisLabel = "Přiřazení";
				Map<Integer, String> columnLabels = selectedYear == null ? null : getMonthLabels();
				currentStatisticsColumnChart.setDataAndDraw(periods, aggregatedData, title, valueLabel, xAxisLabel, yAxisLabel, columnLabels);
			}
			if (registrarsRatioPiechart != null) {
				int totalVolume = selectedYear == null ? sumAllStatistics() : sumStatistics(selectedYear);
				Map<String, Integer> volumeByRegistrar = computeStatisticsByRegistrar(currentData, registrarCodes);
				// TODO: i18n
				String title = selectedYear != null ? "Poměr přiřazení URN:NBN za rok " + selectedYear : "Poměr přiřazení URN:NBN za celé období";
				registrarsRatioPiechart.setDataAndDraw(totalVolume, volumeByRegistrar, title, registraNames);
			}
			if (accumulatedStatisticsAreaChart != null) {
				Map<String, Integer> volumesBeforeFistPeriod = selectedYear != null ? aggregateYearlyData(registrarCodes).get(selectedYear - 1)
						: null;
				// TODO: i18n
				String title = selectedYear != null ? "Měsíčný vývoj počtu URN:NBN v roce " + selectedYear : "Roční vývoj počtu URN:NBN";
				String xAxisLabel = selectedYear != null ? "měsíc v roce " + selectedYear : "rok";
				String yAxisLabel = "Počet";
				Map<Integer, String> columnLabels = selectedYear == null ? null : getMonthLabels();
				accumulatedStatisticsAreaChart.setDataAndDraw(periods, registraNames, volumesBeforeFistPeriod, currentData, title, xAxisLabel,
						yAxisLabel, columnLabels);
			}
		}
	}

	private Map<String, Integer> computeStatisticsByRegistrar(Map<Integer, Map<String, Integer>> currentData, Set<String> registrarCodes) {
		Map<String, Integer> result = new HashMap<>();
		for (String registrarCode : registrarCodes) {
			int sum = 0;
			for (Map<String, Integer> value : currentData.values()) {
				sum += value.get(registrarCode);
			}
			result.put(registrarCode, sum);
		}
		return result;
	}

	private int sumAllStatistics() {
		int sum = 0;
		for (Map<Integer, Map<String, Integer>> monthData : data.values()) {
			for (Map<String, Integer> registrarsMonthData : monthData.values()) {
				for (Integer registrarMonthValue : registrarsMonthData.values()) {
					sum += registrarMonthValue;
				}
			}
		}
		return sum;
	}

	private int sumStatistics(int year) {
		int sum = 0;
		Map<Integer, Map<String, Integer>> monthData = data.get(year);
		for (Map<String, Integer> registrarsMonthData : monthData.values()) {
			for (Integer registrarMonthValue : registrarsMonthData.values()) {
				sum += registrarMonthValue;
			}
		}
		return sum;
	}

	private Map<Integer, Map<String, Integer>> aggregateYearlyData(Set<String> registrarCodes) {
		Map<Integer, Map<String, Integer>> result = new HashMap<>();
		for (Integer year : years) {
			Map<Integer, Map<String, Integer>> anualData = data.get(year);
			result.put(year, sumOverMonths(anualData, registrarCodes));
		}
		return result;
	}

	// month -> reg_code -> volume => reg_code -> total_volume_over_all_months
	private Map<String, Integer> sumOverMonths(Map<Integer, Map<String, Integer>> anualData, Set<String> registrarCodes) {
		Map<String, Integer> result = new HashMap<>();
		for (String registrarCode : registrarCodes) {
			int sum = 0;
			for (Map<String, Integer> monthData : anualData.values()) {
				sum += monthData.get(registrarCode);
			}
			result.put(registrarCode, sum);
		}
		return result;
	}

	private Set<String> extractRegistrarCodes(Map<Integer, Map<Integer, Map<String, Integer>>> dataByYears) {
		Set<String> result = new HashSet<>();
		for (Map<Integer, Map<String, Integer>> dataByMonths : dataByYears.values()) {
			for (Map<String, Integer> dataByRegistrar : dataByMonths.values()) {
				for (String registrarCode : dataByRegistrar.keySet()) {
					result.add(registrarCode);
				}
			}
		}
		return result;
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
