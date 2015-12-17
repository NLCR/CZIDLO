package cz.nkp.urnnbn.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.ColumnChart;
import com.googlecode.gwt.charts.client.corechart.ColumnChartOptions;
import com.googlecode.gwt.charts.client.options.HAxis;
import com.googlecode.gwt.charts.client.options.VAxis;

public class IntegerKeyColumnChart {

	private static final Logger logger = Logger.getLogger(IntegerKeyColumnChart.class.getSimpleName());

	private final ColumnChart chart;

	private IntegerSelectionHandler handler;
	private List<Integer> keys;

	public IntegerKeyColumnChart() {
		chart = new ColumnChart();
	}

	public void setDataAndDraw(Map<Integer, Integer> data, String title, String xAxisDescription, String yAxisDescription, String valueDescription,
			boolean agregate) {
		// logger.info("setDataAndDraw");
		// prepare data
		keys = toSortedList(data.keySet());
		if (agregate) {
			data = agregate(data);
		}
		DataTable dataTable = DataTable.create();

		// columns
		dataTable.addColumn(ColumnType.STRING, null);
		dataTable.addColumn(ColumnType.NUMBER, valueDescription);
		// rows
		dataTable.addRows(data.size());

		// fill data
		// key column
		for (int i = 0; i < keys.size(); i++) {
			dataTable.setValue(i, 0, keys.get(i).toString());
		}
		// value column
		for (int row = 0; row < keys.size(); row++) {
			Integer key = keys.get(row);
			Integer value = data.get(key);
			//logger.info("key: " + key + ", value: " + value);
			dataTable.setValue(row, 1, value.toString());
		}

		// Set options
		ColumnChartOptions options = ColumnChartOptions.create();
		// options.setFontName("Tahoma");
		options.setTitle(title);
		options.setHAxis(HAxis.create(xAxisDescription));
		options.setVAxis(VAxis.create(yAxisDescription));

		// Draw the chart
		chart.draw(dataTable, options);
	}

	private Map<Integer, Integer> agregate(Map<Integer, Integer> data) {
		Map<Integer, Integer> result = new HashMap<>();
		Integer sum = 0;
		for (Integer key : keys) {
			Integer value = data.get(key);
			sum += value;
			result.put(key, sum);
		}
		return result;
	}

	private List<Integer> toSortedList(Set<Integer> set) {
		List<Integer> result = new ArrayList<>();
		result.addAll(set);
		Collections.sort(result);
		return result;
	}

	public Widget getWidget() {
		return chart;
	}

	public IntegerSelectionHandler getHandler() {
		return handler;
	}

	public interface IntegerSelectionHandler {
		public void onSelected(Integer key);
	}
}
