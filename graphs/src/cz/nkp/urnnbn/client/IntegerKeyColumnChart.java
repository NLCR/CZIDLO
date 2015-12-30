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
import com.googlecode.gwt.charts.client.Selection;
import com.googlecode.gwt.charts.client.corechart.ColumnChart;
import com.googlecode.gwt.charts.client.corechart.ColumnChartOptions;
import com.googlecode.gwt.charts.client.event.SelectEvent;
import com.googlecode.gwt.charts.client.event.SelectHandler;
import com.googlecode.gwt.charts.client.options.HAxis;
import com.googlecode.gwt.charts.client.options.VAxis;

public class IntegerKeyColumnChart {

	private static final Logger logger = Logger.getLogger(IntegerKeyColumnChart.class.getSimpleName());

	private final ColumnChart chart;

	private IntegerSelectionHandler handler;
	private List<Integer> keys;

	public IntegerKeyColumnChart() {
		chart = new ColumnChart();
		chart.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				if (handler != null) {
					Selection sel = chart.getSelection().get(0);
					Integer year = keys.get(sel.getRow());
					// logger.info("selected year: " + year);
					handler.onSelected(year);
				}
			}
		});
	}

	public void setDataAndDraw(List<Integer> keysSorted, Map<Integer, Integer> data, Map<Integer, String> columnLabels, String title,
			String xAxisDescription, String yAxisDescription, String valueDescription, boolean agregate) {
		// logger.info("setDataAndDraw");
		// keys = toSortedList(data.keySet());
		keys = keysSorted;
		if (agregate) {
			data = agregate(data);
		}
		DataTable dataTable = DataTable.create();

		// columns
		dataTable.addColumn(ColumnType.STRING, null);
		dataTable.addColumn(ColumnType.NUMBER, valueDescription);
		// rows
		dataTable.addRows(keys.size());

		// fill data
		// key column
		for (int i = 0; i < keys.size(); i++) {
			String label = columnLabels == null ? keys.get(i).toString() : columnLabels.get(keys.get(i));
			dataTable.setValue(i, 0, label);
		}
		// value column
		for (int row = 0; row < keys.size(); row++) {
			Integer key = keys.get(row);
			// logger.warning("key is null for row " + row);
			Integer value = data.get(key);
			if (value == null) {
				value = 0;
			}
			// logger.info("key: " + key + ", value: " + value);
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
		logger.info("aggregate");
		Map<Integer, Integer> result = new HashMap<>();
		Integer sum = 0;
		for (Integer key : keys) {
			Integer value = data.get(key);
			if (value != null) {
				sum += value;
			}
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

	public void setHandler(IntegerSelectionHandler handler) {
		this.handler = handler;
	}

	public interface IntegerSelectionHandler {
		public void onSelected(Integer key);
	}
}
