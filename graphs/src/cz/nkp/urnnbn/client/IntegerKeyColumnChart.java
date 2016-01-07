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

	private static final Logger LOGGER = Logger.getLogger(IntegerKeyColumnChart.class.getSimpleName());

	// data
	private List<Integer> periods;
	private Map<Integer, Integer> data; // period -> volume_per_period
	// labels
	private String title;
	private String valueDescription;
	private String xAxisLabel;
	private String yAxisLabel;
	private Map<Integer, String> columnLabels;
	// widgets
	private final ColumnChart chart;
	// callbacks
	private IntegerSelectionHandler handler;

	public IntegerKeyColumnChart() {
		chart = new ColumnChart();
		chart.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				if (handler != null) {
					Selection sel = chart.getSelection().get(0);
					Integer year = periods.get(sel.getRow());
					handler.onSelected(year);
				}
			}
		});
	}

	public void setDataAndDraw(List<Integer> periods, Map<Integer, Integer> data, boolean agregate, String title, String valueDescription,
			String xAxisLabel, String yAxisLabel, Map<Integer, String> columnLabels) {
		this.periods = periods;
		this.data = agregate ? agregate(data) : data;
		this.title = title;
		this.valueDescription = valueDescription;
		this.xAxisLabel = xAxisLabel;
		this.yAxisLabel = yAxisLabel;
		this.columnLabels = columnLabels;
		draw();
	}

	private void draw() {
		DataTable dataTable = DataTable.create();

		// columns
		dataTable.addColumn(ColumnType.STRING, null);
		dataTable.addColumn(ColumnType.NUMBER, valueDescription);
		// rows
		dataTable.addRows(periods.size());

		// fill data
		// key column
		for (int i = 0; i < periods.size(); i++) {
			String label = columnLabels == null ? periods.get(i).toString() : columnLabels.get(periods.get(i));
			dataTable.setValue(i, 0, label);
		}
		// value column
		for (int row = 0; row < periods.size(); row++) {
			Integer key = periods.get(row);
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
		options.setHAxis(HAxis.create(xAxisLabel));
		options.setVAxis(VAxis.create(yAxisLabel));

		// Draw the chart
		chart.draw(dataTable, options);
	}

	private Map<Integer, Integer> agregate(Map<Integer, Integer> data) {
		LOGGER.info("aggregate");
		Map<Integer, Integer> result = new HashMap<>();
		Integer sum = 0;
		for (Integer key : periods) {
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
