package cz.nkp.urnnbn.client.widgets;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.Composite;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.AreaChart;
import com.googlecode.gwt.charts.client.corechart.AreaChartOptions;
import com.googlecode.gwt.charts.client.options.HAxis;
import com.googlecode.gwt.charts.client.options.VAxis;

import cz.nkp.urnnbn.client.Utils;

public class SingleRegistrarAreaChart extends Composite {

	// data
	private List<Integer> periods;
	private Map<String, String> registrarNames;
	//private List<String> topNRegistrarCodes;
	private Map<Integer, Integer> dataAccumulated; // period -> registrar_code -> registrars_volume_for_period_(accumulated)
	//private Map<Integer, Integer> remainingDataAccumulated; // period -> remaining_registrars_total_volume_for_period_(accumulated)
	// labels
	private String title;
	private String xAxisLabel;
	private String yAxisLabel;
	private Map<Integer, String> columnLabels;
	// widgets
	private AreaChart chart;

	public SingleRegistrarAreaChart() {
		chart = new AreaChart();
		initWidget(chart);
		// setStyleName("RegistrarAssignmentsGraph");
		// draw();
	}

	public void setDataAndDraw(List<Integer> periods, Integer volumeBeforeFistPeriod, Map<Integer, Integer> currentData, String title,
			String xAxisLabel, String yAxisLabel, Map<Integer, String> columnLabels) {
		this.periods = periods;
		// this.registrarNames = registrarNames;
		// this.topNRegistrarCodes = extractTopRegistrarCodes(volumePerPeriod, volumeBeforeFirstPeriod);
		this.dataAccumulated = Utils.accumulate(periods, volumeBeforeFistPeriod, currentData);
		// this.dataAccumulated = Utils.accumulate(periods, topNRegistrarCodes, volumeBeforeFirstPeriod, volumePerPeriod);
		// this.remainingDataAccumulated = extractAndAccumulateRemainingData(periods, topNRegistrarCodes, volumePerPeriod, volumeBeforeFirstPeriod);
		this.title = title;
		this.xAxisLabel = xAxisLabel;
		this.yAxisLabel = yAxisLabel;
		this.columnLabels = columnLabels;
		// TODO
		draw();
	}

	public void draw() {
		// String[] countries = new String[] { "Bolivia", "Ecuador", "Madagascar", "Papua Guinea", "Rwanda" };
		// String[] months = new String[] { "2004/05", "2005/06", "2006/07", "2007/08", "2008/09" };
		// int[][] values = new int[][] { { 165, 135, 157, 139, 136 }, { 938, 1120, 1167, 1110, 691 }, { 522, 599, 587, 615, 629 },
		// { 998, 1268, 807, 968, 1026 }, { 450, 288, 397, 215, 366 } };
		//
		// // Prepare the data
		// DataTable dataTable = DataTable.create();
		// dataTable.addColumn(ColumnType.STRING, "Year");
		// for (int i = 0; i < countries.length; i++) {
		// dataTable.addColumn(ColumnType.NUMBER, countries[i]);
		// }
		// dataTable.addRows(months.length);
		// for (int i = 0; i < months.length; i++) {
		// dataTable.setValue(i, 0, months[i]);
		// }
		// for (int col = 0; col < values.length; col++) {
		// for (int row = 0; row < values[col].length; row++) {
		// dataTable.setValue(row, col + 1, values[col][row]);
		// }
		// }
		//
		// // Set options
		// AreaChartOptions options = AreaChartOptions.create();
		// options.setTitle("Monthly Coffee Production by Country");
		// options.setIsStacked(true);
		// options.setHAxis(HAxis.create("Cups"));
		// options.setVAxis(VAxis.create("Year"));
		//
		// // Draw the chart
		// chart.draw(dataTable, options);

		// Prepare the data
		DataTable dataTable = DataTable.create();
		dataTable.addColumn(ColumnType.STRING, "Period");
		dataTable.addColumn(ColumnType.NUMBER, "Value");

		// dataTable.addColumn(ColumnType.NUMBER, "Ostatní");
		// for (int i = 0; i < topNRegistrarCodes.size(); i++) {
		// String registrarCode = topNRegistrarCodes.get(i);
		// // String label = registrarNames == null ? registrarCode : registrarCode + " - " + registrarNames.get(registrarCode);
		// String label = registrarNames == null ? registrarCode : registrarCode + "-" + registrarNames.get(registrarCode);
		// dataTable.addColumn(ColumnType.NUMBER, label);
		// }

		//dataTable.addRows(1);
		 dataTable.addRows(periods.size());
		for (int i = 0; i < periods.size(); i++) {
			String label = columnLabels == null ? periods.get(i).toString() : columnLabels.get(periods.get(i));
			dataTable.setValue(i, 0, label);
		}

		for (int col = 0; col < periods.size(); col++) {
			int period = periods.get(col);
			// other (second column)
			dataTable.setValue(col, 1, dataAccumulated.get(period));
			
//			//dataTable.setValue(col, 1, remainingDataAccumulated.get(period));
//			Map<String, Integer> periodData = dataAccumulated.get(period);
//			for (int row = 0; row < topNRegistrarCodes.size(); row++) {
//				String registrarCode = topNRegistrarCodes.get(row);
//				Integer value = 0;
//				if (periodData != null) {
//					Integer registrarData = periodData.get(registrarCode);
//					if (registrarData != null) {
//						value = registrarData;
//					}
//				}
//				dataTable.setValue(col, row + 2, value);
//			}
		}

		// Set options
		AreaChartOptions options = AreaChartOptions.create();
		options.setTitle(title);
		options.setIsStacked(true);
		options.setHAxis(HAxis.create(xAxisLabel));
		options.setVAxis(VAxis.create(yAxisLabel));

		// Draw the chart
		chart.draw(dataTable, options);

	}

}
