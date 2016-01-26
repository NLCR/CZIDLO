package cz.nkp.urnnbn.client.charts.widgets;

import java.util.List;
import java.util.Map;

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

public class SingleRegistrarAccumulatedAreaChart extends Composite {

	// fixed data
	private final int width;
	private final int height;

	// data
	private List<Integer> periods;
	private Map<Integer, Integer> dataAccumulated; // period -> registrar_code -> registrars_volume_for_period_(accumulated)

	// labels
	private String title;
	private String xAxisLabel;
	private String yAxisLabel;
	private String valueLabel;
	private Map<Integer, String> columnLabels;
	// colors
	private String color;

	// widgets
	private AreaChart chart;
	// callbacks
	private IntegerSelectionHandler yearSelectionHandler;

	public SingleRegistrarAccumulatedAreaChart(int width, int height) {
		this.width = width;
		this.height = height;
		chart = new AreaChart();
		chart.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				if (yearSelectionHandler != null) {
					Selection selection = chart.getSelection().get(0);
					if (selection != null) {
						Integer row = selection.getRow();
						if (row == null) {
							yearSelectionHandler.onSelected(null);
						} else {
							yearSelectionHandler.onSelected(periods.get(row));
						}
					}
				}
			}
		});
		initWidget(chart);
	}

	public void setDataAndDraw(List<Integer> periods, Integer volumeBeforeFistPeriod, Map<Integer, Integer> currentData, String title,
			String xAxisLabel, String yAxisLabel, String valueLabel, Map<Integer, String> columnLabels, String color) {
		this.periods = periods;
		this.dataAccumulated = Utils.accumulate(periods, volumeBeforeFistPeriod, currentData);
		this.title = title;
		this.xAxisLabel = xAxisLabel;
		this.yAxisLabel = yAxisLabel;
		this.valueLabel = valueLabel;
		this.columnLabels = columnLabels;
		this.color = color;
		draw();
	}

	public void draw() {
		// Prepare the data
		DataTable dataTable = DataTable.create();
		dataTable.addColumn(ColumnType.STRING, "Period");
		dataTable.addColumn(ColumnType.NUMBER, valueLabel);

		// dataTable.addRows(1);
		dataTable.addRows(periods.size());
		for (int i = 0; i < periods.size(); i++) {
			String label = columnLabels == null ? periods.get(i).toString() : columnLabels.get(periods.get(i));
			dataTable.setValue(i, 0, label);
		}

		for (int col = 0; col < periods.size(); col++) {
			int period = periods.get(col);
			dataTable.setValue(col, 1, dataAccumulated.get(period));
		}

		// Set options
		AreaChartOptions options = AreaChartOptions.create();
		options.setWidth(width);
		options.setHeight(height);
		options.setTitle(title);
		options.setIsStacked(true);
		options.setHAxis(HAxis.create(xAxisLabel));
		options.setVAxis(VAxis.create(yAxisLabel));
		if (color != null) {
			options.setColors(new String[] { color });
		}

		// Draw the chart
		chart.draw(dataTable, options);
	}

	public void setYearSelectionHandler(IntegerSelectionHandler yearSelectionHandler) {
		this.yearSelectionHandler = yearSelectionHandler;
	}

}
