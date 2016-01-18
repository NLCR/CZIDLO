package cz.nkp.urnnbn.client.charts.widgets;

import java.io.Serializable;
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

import cz.nkp.urnnbn.shared.charts.Registrar;
import cz.nkp.urnnbn.shared.charts.Statistic;
import cz.nkp.urnnbn.shared.charts.Statistic.Type;

public class RegistrarsStatisticsWidget extends TopLevelStatisticsWidget {

	private static final Logger LOGGER = Logger.getLogger(RegistrarsStatisticsWidget.class.getSimpleName());

	// fixed data
	private final List<Integer> years;
	private final List<Integer> months = initMonths();
	private Map<String, String> registraNames = null;
	private final Type statisticType;

	// data
	private Map<Integer, Map<Integer, Map<String, Integer>>> data; // year -> month -> registrar_code -> statistics
	private Integer selectedYear = null;

	// widgets
	private final Label titleLabel;
	private final ListBox timePeriods;
	private final RadioButton stateAll;
	private final RadioButton stateActiveOnly;
	private final RadioButton stateDeactivatedOnly;
	private final SingleItemColumnChart totalVolumeInPeriodColumnChart;
	private final TopNRegistrarsPieChart registrarsRatioPiechart;
	private final TopNRegistrarsAccumulatedAreaChart accumulatedStatisticsAreaChart;

	public RegistrarsStatisticsWidget(List<Integer> years, Set<Registrar> registrars, Type statisticType,
			StringSelectionHandler registrarSelectionHandler) {
		this.years = years;
		this.registraNames = extractRegistrarNames(registrars);
		this.statisticType = statisticType;

		// container
		VerticalPanel container = new VerticalPanel();
		container.setSpacing(5);// TODO: should be in css
		// container.setWidth("100%");
		// container.setWidth("1000px");
		RootLayoutPanel.get().add(container);

		// title
		titleLabel = new Label(buildTitle());
		titleLabel.setStyleName("czidloChartTitle");
		container.add(titleLabel);

		// header
		VerticalPanel header = new VerticalPanel();
		header.setSpacing(5);// TODO: should be in css
		header.setStyleName("czidloChartHeader");
		container.add(header);

		// year filter
		timePeriods = createTimePeriods();
		HorizontalPanel headerYears = new HorizontalPanel();
		headerYears.add(timePeriods);
		header.add(headerYears);

		// urn state filter
		// TODO: i18n
		stateAll = createUrnStateRadibutton("všechno", true);
		stateActiveOnly = createUrnStateRadibutton("jen aktivní", false);
		stateDeactivatedOnly = createUrnStateRadibutton("jen deaktivované", false);
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
		totalVolumeInPeriodColumnChart = new SingleItemColumnChart();
		totalVolumeInPeriodColumnChart.setYearSelectionHandler(createYearSelectionHandler());
		container.add(totalVolumeInPeriodColumnChart);

		// registrar accumulated volume area chart
		accumulatedStatisticsAreaChart = new TopNRegistrarsAccumulatedAreaChart();
		accumulatedStatisticsAreaChart.setRegistrarSelectionHandler(registrarSelectionHandler);
		container.add(accumulatedStatisticsAreaChart);

		initWidget(container);
		setStyleName("RegistrarsGraph");

		loadData(selectedYear);
	}

	private String buildTitle() {
		// TODO: i18n
		switch (statisticType) {
		case URN_NBN_ASSIGNEMNTS:
			return "Souhrnné Statistiky přiřazení URN:NBN";
		case URN_NBN_RESOLVATIONS:
			return "Souhrnné Statistiky rezolvování URN:NBN";
		default:
			return "";
		}
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
		Boolean includeActive = stateAll.getValue() || stateActiveOnly.getValue();
		Boolean includeDeactivated = stateAll.getValue() || stateDeactivatedOnly.getValue();

		Statistic.Type type = Statistic.Type.URN_NBN_ASSIGNEMNTS;
		HashMap<Statistic.Option, Serializable> options = new HashMap<>();
		options.put(Statistic.Option.URN_NBN_ASSIGNEMNTS_INCLUDE_ACTIVE, includeActive);
		options.put(Statistic.Option.URN_NBN_ASSIGNEMNTS_INCLUDE_DEACTIVATED, includeDeactivated);

		service.getStatistics(type, options, new AsyncCallback<Map<String, Map<Integer, Map<Integer, Integer>>>>() {
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
			if (totalVolumeInPeriodColumnChart != null) {
				Map<Integer, Integer> aggregatedData = agregate(periods, currentData);
				// TODO: i18n
				String title = selectedYear != null ? "Počet přiřazení URN:NBN v roce " + selectedYear
						: "Počet přiřazení URN:NBN přes jednotlivé roky";
				if (stateActiveOnly.getValue()) {
					title += " (jen aktivní)";
				} else if (stateDeactivatedOnly.getValue()) {
					title += " (jen deaktivované)";
				}
				String valueLabel = "Celkem";
				String xAxisLabel = selectedYear != null ? "měsíc v roce " + selectedYear : "rok";
				String yAxisLabel = "Nových přiřazení";
				Map<Integer, String> columnLabels = selectedYear == null ? null : getMonthLabels();
				totalVolumeInPeriodColumnChart.setDataAndDraw(periods, aggregatedData, title, valueLabel, xAxisLabel, yAxisLabel, columnLabels);
			}
			if (registrarsRatioPiechart != null) {
				int totalVolume = selectedYear == null ? sumAllStatistics() : sumStatistics(selectedYear);
				Map<String, Integer> volumeByRegistrar = computeStatisticsByRegistrar(currentData, registrarCodes);
				// TODO: i18n
				String title = selectedYear != null ? "Podíl registrátorů na objemu přiřazených URN:NBN v roce " + selectedYear
						: "Celkový podíl registrátorů na objemu přiřazených URN:NBN";
				if (stateActiveOnly.getValue()) {
					title += " (jen aktivní)";
				} else if (stateDeactivatedOnly.getValue()) {
					title += " (jen deaktivované)";
				}
				registrarsRatioPiechart.setDataAndDraw(totalVolume, volumeByRegistrar, title, registraNames);
			}
			if (accumulatedStatisticsAreaChart != null) {
				Map<String, Integer> volumesBeforeFistPeriod = selectedYear != null ? aggregateYearlyData(registrarCodes).get(selectedYear - 1)
						: null;
				// TODO: i18n
				String title = selectedYear != null ? "Měsíční vývoj objemu přiřazených URN:NBN v roce " + selectedYear
						: "Roční vývoj objemu přiřazených URN:NBN";
				if (stateActiveOnly.getValue()) {
					title += " (jen aktivní)";
				} else if (stateDeactivatedOnly.getValue()) {
					title += " (jen deaktivované)";
				}
				String xAxisLabel = selectedYear != null ? "měsíc v roce " + selectedYear : "rok";
				String yAxisLabel = "Počet URN:NBN";
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
