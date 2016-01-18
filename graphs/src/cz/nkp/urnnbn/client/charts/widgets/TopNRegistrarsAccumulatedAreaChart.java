package cz.nkp.urnnbn.client.charts.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.user.client.ui.Composite;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.Selection;
import com.googlecode.gwt.charts.client.corechart.AreaChart;
import com.googlecode.gwt.charts.client.corechart.AreaChartOptions;
import com.googlecode.gwt.charts.client.event.SelectEvent;
import com.googlecode.gwt.charts.client.event.SelectHandler;
import com.googlecode.gwt.charts.client.options.HAxis;
import com.googlecode.gwt.charts.client.options.VAxis;

import cz.nkp.urnnbn.client.charts.Utils;

public class TopNRegistrarsAccumulatedAreaChart extends Composite {

	private static final Logger LOGGER = Logger.getLogger(TopNRegistrarsPieChart.class.getSimpleName());
	private static final int MAX_REGISTRARS = 3;

	// data
	private List<Integer> periods;
	private Map<String, String> registrarNames;
	private List<String> topNRegistrarCodes;
	private Map<Integer, Map<String, Integer>> dataAccumulated; // period -> registrar_code -> registrars_volume_for_period_(accumulated)
	private Map<Integer, Integer> remainingDataAccumulated; // period -> remaining_registrars_total_volume_for_period_(accumulated)
	// labels
	private String title;
	private String xAxisLabel;
	private String yAxisLabel;
	private Map<Integer, String> columnLabels;
	// colors
	private String[] colors;
	// widgets
	private AreaChart chart;
	// callbacks
	private RegistrarSelectionHandler registrarSelectionHandler;

	public TopNRegistrarsAccumulatedAreaChart() {
		chart = new AreaChart();
		// TODO: timhle se to da trochu tunit
		// chart.setWidth("1200px");
		// chart.setHeight("200px");
		chart.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				if (registrarSelectionHandler != null) {
					Selection selection = chart.getSelection().get(0);
					if (selection != null) {
						int column = selection.getColumn();
						if (column >= 2) {
							int position = column - 1;
							String registrarCode = topNRegistrarCodes.get(position - 1);
							String color = colors != null ? colors[position] : null;
							// String code = topNRegistrarCodes.get(column - 2);
							registrarSelectionHandler.onSelected(registrarCode, color);
						}
					}
				}
			}
		});
		initWidget(chart);
	}

	public void setDataAndDraw(List<Integer> periods, Map<String, String> registrarNames, Map<String, Integer> volumeBeforeFirstPeriod,
			Map<Integer, Map<String, Integer>> volumePerPeriod, String title, String xAxisLabel, String yAxisLabel,
			Map<Integer, String> columnLabels, String[] colors) {
		this.periods = periods;
		this.registrarNames = registrarNames;
		this.topNRegistrarCodes = extractTopRegistrarCodes(volumePerPeriod, volumeBeforeFirstPeriod);
		this.dataAccumulated = Utils.accumulate(periods, topNRegistrarCodes, volumeBeforeFirstPeriod, volumePerPeriod);
		this.remainingDataAccumulated = extractAndAccumulateRemainingData(periods, topNRegistrarCodes, volumePerPeriod, volumeBeforeFirstPeriod);
		this.title = title;
		this.xAxisLabel = xAxisLabel;
		this.yAxisLabel = yAxisLabel;
		this.columnLabels = columnLabels;
		this.colors = colors;
		draw();
	}

	private Map<Integer, Integer> extractAndAccumulateRemainingData(List<Integer> periods, List<String> topNRegistrarCodes,
			Map<Integer, Map<String, Integer>> data, Map<String, Integer> volumeBeforeFirstPeriod) {
		Map<Integer, Integer> result = new HashMap<>();
		Integer previousPeriod = null;
		List<String> allRegistrarCodes = Utils.extractAllRegistrarCodes(data);
		for (Integer period : periods) {
			Map<String, Integer> originalPeriodData = data.get(period);
			Integer accumulatedPeriodVolume = previousPeriod == null ? 0 : result.get(previousPeriod);
			for (String registrarCode : allRegistrarCodes) {
				if (!topNRegistrarCodes.contains(registrarCode)) {
					if (previousPeriod == null && volumeBeforeFirstPeriod != null) {// before first period
						Integer registrarVolumeBeforeFirstPeriod = volumeBeforeFirstPeriod.get(registrarCode);
						if (registrarVolumeBeforeFirstPeriod != null) {
							accumulatedPeriodVolume += registrarVolumeBeforeFirstPeriod;
						}
					}
					Integer registrarVolumePerPeriod = originalPeriodData.get(registrarCode);
					if (registrarVolumePerPeriod != null) {
						accumulatedPeriodVolume += registrarVolumePerPeriod;
					}
				}
			}
			result.put(period, accumulatedPeriodVolume);
			previousPeriod = period;
		}
		return result;
	}

	private void draw() {
		// Prepare the data
		DataTable dataTable = DataTable.create();
		dataTable.addColumn(ColumnType.STRING, "Period");
		dataTable.addColumn(ColumnType.NUMBER, "Ostatní dohromady");
		for (int i = 0; i < topNRegistrarCodes.size(); i++) {
			String registrarCode = topNRegistrarCodes.get(i);
			// String label = registrarNames == null ? registrarCode : registrarCode + " - " + registrarNames.get(registrarCode);
			// String label = registrarNames == null ? registrarCode : registrarCode + "-" + registrarNames.get(registrarCode);
			String label = registrarNames == null ? registrarCode : registrarNames.get(registrarCode);
			dataTable.addColumn(ColumnType.NUMBER, label);
		}

		dataTable.addRows(periods.size());
		for (int i = 0; i < periods.size(); i++) {
			String label = columnLabels == null ? periods.get(i).toString() : columnLabels.get(periods.get(i));
			dataTable.setValue(i, 0, label);
		}

		for (int col = 0; col < periods.size(); col++) {
			int period = periods.get(col);
			// other (second column)
			dataTable.setValue(col, 1, remainingDataAccumulated.get(period));
			Map<String, Integer> periodData = dataAccumulated.get(period);
			for (int row = 0; row < topNRegistrarCodes.size(); row++) {
				String registrarCode = topNRegistrarCodes.get(row);
				Integer value = 0;
				if (periodData != null) {
					Integer registrarData = periodData.get(registrarCode);
					if (registrarData != null) {
						value = registrarData;
					}
				}
				dataTable.setValue(col, row + 2, value);
			}
		}

		// Set options
		AreaChartOptions options = AreaChartOptions.create();
		options.setTitle(title);
		options.setIsStacked(true);
		options.setHAxis(HAxis.create(xAxisLabel));
		options.setVAxis(VAxis.create(yAxisLabel));
		if (colors != null) {
			options.setColors(colors);
		}
		// options.setColors("#9B0B00", "#C50E00", "#FE1300", "#FF4739");

		// Draw the chart
		chart.draw(dataTable, options);
	}

	private List<String> extractTopRegistrarCodes(Map<Integer, Map<String, Integer>> volumePerPeriod, Map<String, Integer> volumeBeforeFirstPeriod) {
		Map<String, Integer> totalVolumeByRegistrar = new HashMap<String, Integer>();
		if (volumeBeforeFirstPeriod != null) {
			for (String registrarCode : volumeBeforeFirstPeriod.keySet()) {
				totalVolumeByRegistrar.put(registrarCode, volumeBeforeFirstPeriod.get(registrarCode));
			}
		}
		for (Map<String, Integer> periodData : volumePerPeriod.values()) {
			for (String registrarCode : periodData.keySet()) {
				int sumForRegistrar = 0;
				if (totalVolumeByRegistrar.containsKey(registrarCode)) {
					sumForRegistrar = totalVolumeByRegistrar.get(registrarCode);
				}
				Integer increment = periodData.get(registrarCode);
				if (increment != null) {
					sumForRegistrar += increment;
				}
				totalVolumeByRegistrar.put(registrarCode, sumForRegistrar);
			}
		}
		// sort
		List<RegistrarWithStatistic> withStatistics = new ArrayList<RegistrarWithStatistic>();
		for (String registrarCode : totalVolumeByRegistrar.keySet()) {
			withStatistics.add(new RegistrarWithStatistic(registrarCode, totalVolumeByRegistrar.get(registrarCode)));
		}
		Collections.sort(withStatistics);
		List<String> result = new ArrayList<>();
		for (int i = 0; i < withStatistics.size() && i < MAX_REGISTRARS; i++) {
			result.add(withStatistics.get(i).getCode());
		}
		return result;
	}

	public void setRegistrarSelectionHandler(RegistrarSelectionHandler registrarSelectionHandler) {
		this.registrarSelectionHandler = registrarSelectionHandler;
	}
}