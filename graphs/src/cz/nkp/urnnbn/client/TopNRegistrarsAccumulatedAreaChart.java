package cz.nkp.urnnbn.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.AreaChart;
import com.googlecode.gwt.charts.client.corechart.AreaChartOptions;
import com.googlecode.gwt.charts.client.options.HAxis;
import com.googlecode.gwt.charts.client.options.VAxis;

public class TopNRegistrarsAccumulatedAreaChart {

	private static final Logger LOGGER = Logger.getLogger(TopNRegistrarsPieChart.class.getSimpleName());
	private static final int MAX_REGISTRARS = 3;

	private List<Integer> periods;
	private List<String> topNRegistrarCodes;
	private Map<Integer, Map<String, Integer>> dataAccumulated; // period -> registrar_code -> registrars_volume_for_period_(accumulated)
	private Map<Integer, Integer> remainingDataAccumulated; // period -> remaining_registrars_total_volume_for_period_(accumulated)
	private AreaChart chart;

	public TopNRegistrarsAccumulatedAreaChart() {
		chart = new AreaChart();
	}

	public Widget getWidget() {
		return chart;
	}

	public void setDataAndDraw(List<Integer> periods, Map<String, Integer> volumeBeforeFirstPeriod, Map<Integer, Map<String, Integer>> volumePerPeriod) {
		this.periods = periods;
		this.topNRegistrarCodes = extractTopRegistrarCodes(volumePerPeriod, volumeBeforeFirstPeriod);
		this.dataAccumulated = Utils.accumulate(periods, topNRegistrarCodes, volumeBeforeFirstPeriod, volumePerPeriod);
		this.remainingDataAccumulated = extractAndAccumulateRemainingData(periods, topNRegistrarCodes, volumePerPeriod, volumeBeforeFirstPeriod);
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
		dataTable.addColumn(ColumnType.NUMBER, "Other");
		for (int i = 0; i < topNRegistrarCodes.size(); i++) {
			dataTable.addColumn(ColumnType.NUMBER, topNRegistrarCodes.get(i));
		}
		// dataTable.addColumn(ColumnType.NUMBER, "Other");

		dataTable.addRows(periods.size());
		for (int i = 0; i < periods.size(); i++) {
			dataTable.setValue(i, 0, periods.get(i).toString());
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
		// TODO: in setData
		options.setTitle("Objem URN:NBN");
		options.setIsStacked(true);
		options.setHAxis(HAxis.create("Období"));
		options.setVAxis(VAxis.create("Počet"));

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
}
