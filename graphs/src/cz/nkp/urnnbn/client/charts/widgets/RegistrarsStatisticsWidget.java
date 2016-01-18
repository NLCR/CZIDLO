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
import cz.nkp.urnnbn.shared.charts.Statistic.Option;
import cz.nkp.urnnbn.shared.charts.Statistic.Type;

public class RegistrarsStatisticsWidget extends TopLevelStatisticsWidget {

	private static final Logger LOGGER = Logger.getLogger(RegistrarsStatisticsWidget.class.getSimpleName());

	// fixed data
	private final List<Integer> years;
	private final List<Integer> months = initMonths();
	private Map<String, String> registraNames = null;
	private final Type statisticType;
	private final String singlevalueGraphColor;
	private final String[] multivaluedGraphColors;

	// data
	private Map<Integer, Map<Integer, Map<String, Integer>>> data; // year -> month -> registrar_code -> statistics
	private Integer selectedYear = null;
	private String selectedRegistrarColor;

	// widgets
	private final Label titleLabel;
	private final ListBox timePeriods;
	private final RadioButton stateAll;
	private final RadioButton stateActiveOnly;
	private final RadioButton stateDeactivatedOnly;
	private final SingleItemColumnChart columnChart;
	private final TopNRegistrarsPieChart pieChart;
	private final TopNRegistrarsAccumulatedAreaChart areaChart;

	public RegistrarsStatisticsWidget(List<Integer> years, Set<Registrar> registrars, Type statisticType,
			RegistrarSelectionHandler registrarSelectionHandler) {
		this.years = years;
		this.registraNames = extractRegistrarNames(registrars);
		this.statisticType = statisticType;
		this.singlevalueGraphColor = buildSingleValueGraphColor();
		this.multivaluedGraphColors = buildMultivaluedGraphColors();

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
		if (statisticType == Type.URN_NBN_ASSIGNMENTS) {
			stateAll = createUrnStateRadibutton("všechno", true);
			stateActiveOnly = createUrnStateRadibutton("jen aktivní", false);
			stateDeactivatedOnly = createUrnStateRadibutton("jen deaktivované", false);
			HorizontalPanel urnStateFilterPanel = new HorizontalPanel();
			urnStateFilterPanel.add(stateAll);
			urnStateFilterPanel.add(stateActiveOnly);
			urnStateFilterPanel.add(stateDeactivatedOnly);
			header.add(urnStateFilterPanel);
		} else {
			stateAll = null;
			stateActiveOnly = null;
			stateDeactivatedOnly = null;
		}

		// registrar ratio chart
		pieChart = new TopNRegistrarsPieChart();
		pieChart.setRegistrarSelectionHandler(registrarSelectionHandler);
		container.add(pieChart);

		// assignments per period chart
		columnChart = new SingleItemColumnChart();
		columnChart.setYearSelectionHandler(createYearSelectionHandler());
		container.add(columnChart);

		// registrar accumulated volume area chart
		areaChart = new TopNRegistrarsAccumulatedAreaChart();
		areaChart.setRegistrarSelectionHandler(registrarSelectionHandler);
		container.add(areaChart);

		initWidget(container);
		setStyleName(buildStyleName());

		loadData(selectedYear);
	}

	private String buildStyleName() {
		switch (statisticType) {
		case URN_NBN_ASSIGNMENTS:
			return "czidloChartRegistrarsAssignments";
		case URN_NBN_RESOLVATIONS:
			return "czidloChartRegistrarsResolvations";
		default:
			return null;
		}
	}

	private String buildSingleValueGraphColor() {
		switch (statisticType) {
		case URN_NBN_ASSIGNMENTS:
			// http://paletton.com/#uid=7030u0kw0vSjzD3oSy0y9oLDhjs
			// primary-2
			return "FE1300";
		case URN_NBN_RESOLVATIONS:
			// http://paletton.com/#uid=73k0X0kHRr1r0CGEE-YLClVQkgi
			// primary-2
			return "#017883";
		default:
			return null;
		}
	}

	private String[] buildMultivaluedGraphColors() {
		switch (statisticType) {
		case URN_NBN_ASSIGNMENTS:
			// http://paletton.com/#uid=7030u0kw0vSjzD3oSy0y9oLDhjs
			// primary-0, complement-2, secondery2-2, secondery-2
			return new String[] { "#FF6F63", "#00C222", "#03899C", "#FE7A00" };
		case URN_NBN_RESOLVATIONS:
			// http://paletton.com/#uid=73k0X0kHRr1r0CGEE-YLClVQkgi
			// primary-0, complement-2, secondery2-2, secondery-2
			return new String[] { "#1EA6B3", "#D76500", "#D7B500", "#2E0A94" };
		default:
			return null;
		}
	}

	private String buildTitle() {
		// TODO: i18n
		switch (statisticType) {
		case URN_NBN_ASSIGNMENTS:
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
		service.getStatistics(statisticType, buildOptions(), new AsyncCallback<Map<String, Map<Integer, Map<Integer, Integer>>>>() {
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

	private HashMap<Option, Serializable> buildOptions() {
		HashMap<Statistic.Option, Serializable> options = new HashMap<>();
		switch (statisticType) {
		case URN_NBN_ASSIGNMENTS:
			Boolean includeActive = stateAll.getValue() || stateActiveOnly.getValue();
			Boolean includeDeactivated = stateAll.getValue() || stateDeactivatedOnly.getValue();
			options.put(Statistic.Option.URN_NBN_ASSIGNMENTS_INCLUDE_ACTIVE, includeActive);
			options.put(Statistic.Option.URN_NBN_ASSIGNMENTS_INCLUDE_DEACTIVATED, includeDeactivated);
			break;
		case URN_NBN_RESOLVATIONS:
			// TODO
			break;
		}
		return options;
	}

	private void redrawCharts() {
		if (data != null) {
			Set<String> registrarCodes = extractRegistrarCodes(data);
			Map<Integer, Map<String, Integer>> currentData = selectedYear != null ? data.get(selectedYear) : aggregateYearlyData(registrarCodes);
			List<Integer> periods = selectedYear != null ? months : years;
			if (columnChart != null) {
				Map<Integer, Integer> aggregatedData = agregate(periods, currentData);
				// TODO: i18n
				String title = buildColumnChartTitle();
				String valueLabel = "Celkem";
				String xAxisLabel = selectedYear != null ? "měsíc v roce " + selectedYear : "rok";
				String yAxisLabel = buildColumnChartYAxisLabel();
				Map<Integer, String> columnLabels = selectedYear == null ? null : getMonthLabels();
				//String color = buildSelectedRegistrarColor();
				columnChart.setDataAndDraw(periods, aggregatedData, title, valueLabel, xAxisLabel, yAxisLabel, columnLabels, null);
			}
			if (pieChart != null) {
				int totalVolume = selectedYear == null ? sumAllStatistics() : sumStatistics(selectedYear);
				Map<String, Integer> volumeByRegistrar = computeStatisticsByRegistrar(currentData, registrarCodes);
				// TODO: i18n
				String title = buildiPieChartTitle();
				pieChart.setDataAndDraw(totalVolume, volumeByRegistrar, title, registraNames, multivaluedGraphColors);
			}
			if (areaChart != null) {
				Map<String, Integer> volumesBeforeFistPeriod = selectedYear != null ? aggregateYearlyData(registrarCodes).get(selectedYear - 1)
						: null;
				// TODO: i18n
				String title = buildAreaChartTitle();
				String xAxisLabel = selectedYear != null ? "měsíc v roce " + selectedYear : "rok";
				String yAxisLabel = buildAreChartYAxisLabel();
				Map<Integer, String> columnLabels = selectedYear == null ? null : getMonthLabels();
				areaChart.setDataAndDraw(periods, registraNames, volumesBeforeFistPeriod, currentData, title, xAxisLabel, yAxisLabel, columnLabels,
						multivaluedGraphColors);
			}
		}
	}

	private String buildAreChartYAxisLabel() {
		// TODO: i18n
		switch (statisticType) {
		case URN_NBN_ASSIGNMENTS:
			return "Počet přiřazených URN:NBN";
		case URN_NBN_RESOLVATIONS:
			return "Agregovaný počet rezolvování";
		default:
			return "";
		}
	}

	private String buildAreaChartTitle() {
		// TODO: i18n
		String title = "";
		switch (statisticType) {
		case URN_NBN_ASSIGNMENTS:
			title = selectedYear != null ? "Měsíční vývoj počtu přiřazených URN:NBN v roce " + selectedYear : "Roční vývoj počtu přiřazených URN:NBN";
			if (stateActiveOnly.getValue()) {
				title += " (jen aktivní)";
			} else if (stateDeactivatedOnly.getValue()) {
				title += " (jen deaktivované)";
			}
			break;
		case URN_NBN_RESOLVATIONS:
			title = selectedYear != null ? "Měsíční vývoj agregovaného počtu rezolvování URN:NBN v roce " + selectedYear
					: "Roční vývoj agregovaného počtu rezolvování URN:NBN";
			break;
		}
		return title;
	}

	private String buildColumnChartYAxisLabel() {
		// TODO: i18n
		switch (statisticType) {
		case URN_NBN_ASSIGNMENTS:
			return "Nových přiřazení";
		case URN_NBN_RESOLVATIONS:
			return "Nových rezolvování";
		default:
			return "";
		}
	}

	private String buildColumnChartTitle() {
		// TODO: i18n
		String title = "";
		switch (statisticType) {
		case URN_NBN_ASSIGNMENTS:
			title = selectedYear != null ? "Počet přiřazení URN:NBN v roce " + selectedYear : "Počet přiřazení URN:NBN přes jednotlivé roky";
			if (stateActiveOnly.getValue()) {
				title += " (jen aktivní)";
			} else if (stateDeactivatedOnly.getValue()) {
				title += " (jen deaktivované)";
			}
			break;
		case URN_NBN_RESOLVATIONS:
			title = selectedYear != null ? "Počet rezolvování URN:NBN v roce " + selectedYear : "Počet rezolvování URN:NBN přes jednotlivé roky";
			break;
		}
		return title;
	}

	private String buildiPieChartTitle() {
		// TODO: i18n
		String title = "";
		switch (statisticType) {
		case URN_NBN_ASSIGNMENTS:
			title = selectedYear != null ? "Podíl registrátorů na objemu přiřazených URN:NBN v roce " + selectedYear
					: "Celkový podíl registrátorů na objemu přiřazených URN:NBN";
			if (stateActiveOnly.getValue()) {
				title += " (jen aktivní)";
			} else if (stateDeactivatedOnly.getValue()) {
				title += " (jen deaktivované)";
			}
			break;
		case URN_NBN_RESOLVATIONS:
			title = selectedYear != null ? "Podíl registrátorů na počtu rezolvovaných URN:NBN v roce " + selectedYear
					: "Celkový podíl registrátorů na počtu rezolvovaných URN:NBN";
			break;
		}
		return title;
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