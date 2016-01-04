package cz.nkp.urnnbn.client;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.PieChart;

import cz.nkp.urnnbn.shared.Registrar;

public class Top3PieChart {

	private static final Logger logger = Logger.getLogger(Top3PieChart.class.getSimpleName());
	private static final int MAX_REGISTRARS = 3;

	private final PieChart chart;
	private List<String> keys;
	private Map<Registrar, Integer> data;

	public Top3PieChart() {
		chart = new PieChart();
		initData();
	}

	private void initData() {

		DataTable pieNewData = DataTable.create();
		// pieNewData.addColumn(ColumnType.STRING, "Major");
		// pieNewData.addColumn(ColumnType.NUMBER, "Degrees");
		// pieNewData.addColumn(ColumnType.NUMBER, "Test");

		pieNewData.addColumn(ColumnType.STRING, "Major");
		pieNewData.addColumn(ColumnType.NUMBER, "Degrees");

		pieNewData.addRow("Business", 358293);
		pieNewData.addRow("Education", 101265);
		pieNewData.addRow("Social Sciences & History", 172780);
		pieNewData.addRow("Health", 129634);
		pieNewData.addRow("Psychology", 97216);
		pieNewData.addRow("Sociology", 123456);
		pieNewData.addRow("Blabla1", 12345);
		pieNewData.addRow("Blabla2", 12345);
		pieNewData.addRow("Blabla3", 12345);
		pieNewData.addRow("Blabla4", 12345);
		pieNewData.addRow("Blabla5", 12345);
		pieNewData.addRow("Blabla6", 12345);
		pieNewData.addRow("Blabla7", 12345);
		pieNewData.addRow("Blabla8", 12345);
		pieNewData.addRow("Blabla9", 12345);
		pieNewData.addRow("Blabla10", 555);
		pieNewData.addRow("Blabla11", 555);
		pieNewData.addRow("Blabla12", 555);
		pieNewData.addRow("Blabla13", 555);
		pieNewData.addRow("Blabla14", 555);
		pieNewData.addRow("Blabla15", 555);

		// Draw the chart
		// chart.draw(chart.computeDiff(pieOldData, pieNewData));

		// DataTable dataTable = DataTable.create();
		// dataTable.addColumn(ColumnType.STRING, "Name");
		// dataTable.addColumn(ColumnType.NUMBER, "Donuts eaten");
		//
		// dataTable.addRows(4);
		// dataTable.setValue(0, 0, "Michael");
		// dataTable.setValue(1, 0, "Elisa");
		// dataTable.setValue(2, 0, "Robert");
		// dataTable.setValue(3, 0, "John");
		// dataTable.setValue(0, 1, 5);
		// dataTable.setValue(1, 1, 7);
		// dataTable.setValue(2, 1, 3);
		// dataTable.setValue(3, 1, 2);
		//
		//
		// chart.draw(dataTable);

		chart.draw(pieNewData);

	}

	public void setDataAndDraw(List<Integer> keysSorted, Map<Integer, Integer> data, Map<Integer, String> columnLabels, String title,
			String xAxisDescription, String yAxisDescription, String valueDescription, boolean agregate) {
	}

	public void setDataAndDraw(Map<Registrar, Integer> data) {
		extractData(data);
		draw();
	}

	private void extractData(Map<Registrar, Integer> data) {

	}

	private void draw() {
		DataTable pieNewData = DataTable.create();
		pieNewData.addColumn(ColumnType.STRING, "Registrar");
		pieNewData.addColumn(ColumnType.NUMBER, "registrations");
		pieNewData.addRow("Business", 358293);
		pieNewData.addRow("Education", 101265);
		pieNewData.addRow("Social Sciences & History", 172780);
		pieNewData.addRow("Health", 129634);
		pieNewData.addRow("Psychology", 97216);
	}

	public Widget getWidget() {
		return chart;
	}

}
