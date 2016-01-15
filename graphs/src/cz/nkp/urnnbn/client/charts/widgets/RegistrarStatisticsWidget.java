package cz.nkp.urnnbn.client.charts.widgets;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

public class RegistrarStatisticsWidget extends TopLevelStatisticsWidget {

	private static final Logger logger = Logger.getLogger(RegistrarStatisticsWidget.class.getSimpleName());

	// fixed data
	private final List<Integer> years;
	private final List<Integer> months = initMonths();

	// data
	private Registrar selectedRegistrar;
	private Map<Integer, Map<Integer, Integer>> registrarData; // year -> month -> statistics
	private Integer selectedYear = null;

	// widgets
	private final Label title = new Label();
	private final ListBox timePeriods;
	private final RadioButton stateAll;
	private final RadioButton stateActiveOnly;
	private final RadioButton stateDeactivatedOnly;
	private final SingleItemColumnChart assignmentsColumnChart;
	private final SingleRegistrarAccumulatedAreaChart areaChart;

	public RegistrarStatisticsWidget(List<Integer> years) {
		this.years = years;

		// container
		VerticalPanel container = new VerticalPanel();
		container.setSpacing(5);
		container.setWidth("100%");
		// container.setWidth("800px");
		RootLayoutPanel.get().add(container);

		// header
		VerticalPanel header = new VerticalPanel();
		header.setSpacing(10);
		// header.setWidth("100%");
		header.setWidth("1000px");
		container.add(header);

		// title
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

		IntegerSelectionHandler yearSelectionHandler = createYearSelectionHandler();

		// column chart
		assignmentsColumnChart = new SingleItemColumnChart();
		assignmentsColumnChart.setYearSelectionHandler(yearSelectionHandler);
		container.add(assignmentsColumnChart);

		// area chart
		areaChart = new SingleRegistrarAccumulatedAreaChart();
		areaChart.setYearSelectionHandler(yearSelectionHandler);
		container.add(areaChart);

		initWidget(container);
		setStyleName("RegistrarAssignmentsGraph");
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
			boolean includeActive = stateAll.getValue() || stateActiveOnly.getValue();
			boolean includeDeactivated = stateAll.getValue() || stateDeactivatedOnly.getValue();

			Statistic.Type type = Statistic.Type.URN_NBN_ASSIGNEMNTS;
			HashMap<Statistic.Option, Serializable> options = new HashMap<>();
			options.put(Statistic.Option.URN_NBN_ASSIGNEMNTS_INCLUDE_ACTIVE, includeActive);
			options.put(Statistic.Option.URN_NBN_ASSIGNEMNTS_INCLUDE_DEACTIVATED, includeDeactivated);

			service.getStatistics(registrar.getCode(), type, options, new AsyncCallback<Map<Integer, Map<Integer, Integer>>>() {

				@Override
				public void onSuccess(Map<Integer, Map<Integer, Integer>> result) {
					selectedRegistrar = registrar;
					selectedYear = year;
					registrarData = result;
					redrawCharts();
				}

				@Override
				public void onFailure(Throwable caught) {
					logger.severe(caught.getMessage());
				}
			});
		}
	}

	private void redrawCharts() {
		if (registrarData != null) {
			Map<Integer, Integer> periodData = extractPeriodData();
			Integer volumeBeforeFirstPeriod = extractVolumeBeforeFirstPeriod();
			if (assignmentsColumnChart != null) {
				List<Integer> keys = selectedYear != null ? months : years;
				// TODO: i18n
				String title = selectedYear != null ? "Počet přiřazení URN:NBN za rok " + selectedYear : "Počet přiřazení URN:NBN za celé období";
				String valueDesc = selectedRegistrar != null ? selectedRegistrar.getCode() : "celkově";
				String xAxisLabel = selectedYear != null ? "měsíc v roce " + selectedYear : "rok";
				String yAxisLabel = "Přiřazení";
				Map<Integer, String> columnDesc = selectedYear == null ? null : getMonthLabels();
				assignmentsColumnChart.setDataAndDraw(keys, periodData, title, valueDesc, xAxisLabel, yAxisLabel, columnDesc);
			}
			if (areaChart != null) {
				List<Integer> keys = selectedYear != null ? months : years;
				// TODO: i18n
				String title = selectedYear != null ? "Měsíčný vývoj počtu URN:NBN v roce " + selectedYear : "Roční vývoj počtu URN:NBN";
				String xAxisLabel = selectedYear != null ? "měsíc v roce " + selectedYear : "rok";
				String yAxisLabel = "Počet";
				Map<Integer, String> columnLabels = selectedYear == null ? null : getMonthLabels();
				areaChart.setDataAndDraw(keys, volumeBeforeFirstPeriod, periodData, title, xAxisLabel, yAxisLabel, selectedRegistrar.getCode(),
						columnLabels);
				areaChart.draw();
			}
		}
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

	public void setRegistrar(Registrar registrar) {
		String registrarName = registrar != null ? registrar.getName() : null;
		title.setText(registrarName);
		selectedRegistrar = registrar;
		loadData(selectedRegistrar, selectedYear);
	}

}
