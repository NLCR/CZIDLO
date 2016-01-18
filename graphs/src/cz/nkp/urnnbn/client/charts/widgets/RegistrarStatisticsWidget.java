package cz.nkp.urnnbn.client.charts.widgets;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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

public class RegistrarStatisticsWidget extends TopLevelStatisticsWidget {

	private static final Logger LOGGER = Logger.getLogger(RegistrarStatisticsWidget.class.getSimpleName());

	// fixed data
	private final List<Integer> years;
	private final List<Integer> months = initMonths();
	private final List<Registrar> registrars;
	private final Type statisticType;

	// data
	private Registrar selectedRegistrar;
	private Map<Integer, Map<Integer, Integer>> registrarData; // year -> month -> statistics
	private Integer selectedYear = null;

	// widgets
	private final Label titleLabel;
	private final Label registrarNameLabel;
	private final ListBox registrarsListbox;

	private final ListBox timePeriods;
	private final RadioButton stateAll;
	private final RadioButton stateActiveOnly;
	private final RadioButton stateDeactivatedOnly;
	private final SingleItemColumnChart columnChart;
	private final SingleRegistrarAccumulatedAreaChart areaChart;

	public RegistrarStatisticsWidget(List<Integer> years, Set<Registrar> registrars, Type statisticType) {
		this.years = years;
		this.registrars = toListSortedByName(registrars);
		this.statisticType = statisticType;

		// container
		VerticalPanel container = new VerticalPanel();
		container.setSpacing(5);
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

		if (registrars.isEmpty() || registrars.size() == 1) {
			registrarsListbox = null;
			registrarNameLabel = new Label();
			registrarNameLabel.setStyleName("czidloChartRegistrarTitle");
			header.add(registrarNameLabel);
		} else {
			registrarNameLabel = null;
			registrarsListbox = createRegistrarsListBox();
			header.add(registrarsListbox);
		}

		// year filter
		timePeriods = createTimePeriods();
		HorizontalPanel headerYears = new HorizontalPanel();
		headerYears.add(timePeriods);
		header.add(headerYears);

		// urn state filter
		if (statisticType == Type.URN_NBN_ASSIGNEMNTS) {
			// TODO: i18n
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

		IntegerSelectionHandler yearSelectionHandler = createYearSelectionHandler();

		// column chart
		columnChart = new SingleItemColumnChart();
		columnChart.setYearSelectionHandler(yearSelectionHandler);
		container.add(columnChart);

		// area chart
		areaChart = new SingleRegistrarAccumulatedAreaChart();
		areaChart.setYearSelectionHandler(yearSelectionHandler);
		container.add(areaChart);

		initWidget(container);
		setStyleName("RegistrarAssignmentsGraph");
		if (!registrars.isEmpty()) {
			setRegistrar(this.registrars.get(0));
		}
	}

	private String buildTitle() {
		// TODO: i18n
		switch (statisticType) {
		case URN_NBN_ASSIGNEMNTS:
			return "Statistiky přiřazení URN:NBN podle registrátora";
		case URN_NBN_RESOLVATIONS:
			return "Statistiky rezolvování URN:NBN podle registrátora";
		default:
			return "";
		}
	}

	private ListBox createRegistrarsListBox() {
		ListBox result = new ListBox();
		for (Registrar registrar : registrars) {
			// result.addItem(registrar.getName() + " (" + registrar.getCode() + ")");
			result.addItem(registrar.getName());
		}

		result.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				int index = registrarsListbox.getSelectedIndex();
				Registrar registrar = registrars.get(index);
				setRegistrar(registrar);
			}
		});
		return result;
	}

	private List<Registrar> toListSortedByName(Set<Registrar> input) {
		List<Registrar> result = new ArrayList<>();
		if (input != null && !input.isEmpty()) {

			result.addAll(input);
			Collections.sort(result, new Comparator<Registrar>() {
				@Override
				public int compare(Registrar o1, Registrar o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
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
				loadData(selectedRegistrar, selectedYear);
			}
		});
		return result;
	}

	private RadioButton createUrnStateRadibutton(String title, boolean selected) {
		// TODO: filtrovat instance
		RadioButton result = new RadioButton("registrar-urn-state", title);
		result.setValue(selected);
		result.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if (event.getValue()) {
					loadData(selectedRegistrar, selectedYear);
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
					timePeriods.setSelectedIndex(0);
					selectedYear = null;
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
				loadData(selectedRegistrar, selectedYear);
			}
		};
	}

	private void loadData(final Registrar registrar, final Integer year) {
		if (registrar != null) {
			service.getStatistics(registrar.getCode(), statisticType, buildOptions(), new AsyncCallback<Map<Integer, Map<Integer, Integer>>>() {

				@Override
				public void onSuccess(Map<Integer, Map<Integer, Integer>> result) {
					selectedRegistrar = registrar;
					selectedYear = year;
					registrarData = result;
					redrawCharts();
				}

				@Override
				public void onFailure(Throwable caught) {
					LOGGER.severe(caught.getMessage());
				}
			});
		}
	}

	private HashMap<Option, Serializable> buildOptions() {
		HashMap<Statistic.Option, Serializable> result = new HashMap<>();
		switch (statisticType) {
		case URN_NBN_ASSIGNEMNTS:
			boolean includeActive = stateAll.getValue() || stateActiveOnly.getValue();
			boolean includeDeactivated = stateAll.getValue() || stateDeactivatedOnly.getValue();
			result.put(Statistic.Option.URN_NBN_ASSIGNEMNTS_INCLUDE_ACTIVE, includeActive);
			result.put(Statistic.Option.URN_NBN_ASSIGNEMNTS_INCLUDE_DEACTIVATED, includeDeactivated);
			break;
		case URN_NBN_RESOLVATIONS:
			// TODO: options if needed
			break;

		}
		return result;
	}

	private void redrawCharts() {
		if (registrarData != null) {
			Map<Integer, Integer> periodData = extractPeriodData();
			Integer volumeBeforeFirstPeriod = extractVolumeBeforeFirstPeriod();
			if (columnChart != null) {
				List<Integer> keys = selectedYear != null ? months : years;
				// TODO: i18n
				String chartTitle = buildColumnChartTitle();
				String valueDesc = selectedRegistrar != null ? getRegistrarName(selectedRegistrar.getCode()) : "";
				String xAxisLabel = selectedYear != null ? "měsíc v roce " + selectedYear : "rok";
				String yAxisLabel = buildColumnChartYAxisLabel();
				Map<Integer, String> columnDesc = selectedYear == null ? null : getMonthLabels();
				columnChart.setDataAndDraw(keys, periodData, chartTitle, valueDesc, xAxisLabel, yAxisLabel, columnDesc);
			}
			if (areaChart != null) {
				List<Integer> keys = selectedYear != null ? months : years;
				// TODO: i18n
				String title = buildAreaChartTitle();
				String xAxisLabel = selectedYear != null ? "měsíc v roce " + selectedYear : "rok";
				String yAxisLabel = buildAreaChartYAxisLabel();
				Map<Integer, String> columnLabels = selectedYear == null ? null : getMonthLabels();
				String valueLabel = getRegistrarName(selectedRegistrar.getCode());
				areaChart.setDataAndDraw(keys, volumeBeforeFirstPeriod, periodData, title, xAxisLabel, yAxisLabel, valueLabel, columnLabels);
				areaChart.draw();
			}
		}
	}

	private String buildColumnChartYAxisLabel() {
		// TODO: i18n
		switch (statisticType) {
		case URN_NBN_ASSIGNEMNTS:
			return "Nových přiřazení";
		case URN_NBN_RESOLVATIONS:
			return "Nových rezolvování";
		default:
			return "";
		}
	}

	private String buildAreaChartYAxisLabel() {
		// TODO: i18n
		switch (statisticType) {
		case URN_NBN_ASSIGNEMNTS:
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
		case URN_NBN_ASSIGNEMNTS:
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

	private String buildColumnChartTitle() {
		// TODO: i18n
		String title = "";
		switch (statisticType) {
		case URN_NBN_ASSIGNEMNTS:
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

	private String getRegistrarName(String code) {
		for (Registrar registrar : registrars) {
			if (registrar.getCode().equals(code)) {
				return registrar.getName();
			}
		}
		return "";
	}

	private Integer extractVolumeBeforeFirstPeriod() {
		if (selectedYear == null) {
			return 0;
		} else {
			int sum = 0;
			for (Integer year : years) {
				if (year < selectedYear) {
					Map<Integer, Integer> annualStatistics = registrarData.get(year);
					for (Integer monthlyStatistics : annualStatistics.values()) {
						sum += monthlyStatistics;
					}
				}
			}
			return sum;
		}
	}

	private Map<Integer, Integer> extractPeriodData() {
		if (selectedYear != null) {
			return registrarData.get(selectedYear);
		} else {
			Map<Integer, Integer> result = new HashMap<>();
			for (Integer year : years) {
				Map<Integer, Integer> annualStatistics = registrarData.get(year);
				Integer annualStatisticsSum = 0;
				for (Integer monthlyStatistics : annualStatistics.values()) {
					annualStatisticsSum += monthlyStatistics;
				}
				result.put(year, annualStatisticsSum);
			}
			return result;
		}
	}

	public void setRegistrar(Registrar newRegistrar) {
		String registrarName = newRegistrar != null ? newRegistrar.getName() : null;
		if (registrarNameLabel != null) {
			registrarNameLabel.setText(registrarName);
		}
		if (registrarsListbox != null) {
			for (int i = 0; i < registrars.size(); i++) {
				if (newRegistrar.getCode().equals(registrars.get(i).getCode())) {
					registrarsListbox.setSelectedIndex(i);
				}
			}
		}
		selectedRegistrar = newRegistrar;
		loadData(selectedRegistrar, selectedYear);
	}

}
