package cz.nkp.urnnbn.client.widgets;

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

import cz.nkp.urnnbn.client.Utils;

public class SingleRegistrarAreaChart extends Composite {

	// data
	private List<Integer> periods;
	private Map<Integer, Integer> dataAccumulated; // period -> registrar_code -> registrars_volume_for_period_(accumulated)
	// labels
	private String title;
	private String xAxisLabel;
	private String yAxisLabel;
	private Map<Integer, String> columnLabels;
	// widgets
	private AreaChart chart;
	// callbacks
	private IntegerSelectionHandler handler;

	public SingleRegistrarAreaChart() {
		chart = new AreaChart();
		chart.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				if (handler != null) {
					Selection selection = chart.getSelection().get(0);
					if (selection != null) {
						Integer year = periods.get(selection.getRow());
						handler.onSelected(year);
					}
				}
			}
		});
		initWidget(chart);
		// setStyleName("RegistrarAssignmentsGraph");
	}

	public void setDataAndDraw(List<Integer> periods, Integer volumeBeforeFistPeriod, Map<Integer, Integer> currentData, String title,
			String xAxisLabel, String yAxisLabel, Map<Integer, String> columnLabels) {
		this.periods = periods;
		this.dataAccumulated = Utils.accumulate(periods, volumeBeforeFistPeriod, currentData);
		this.title = title;
		this.xAxisLabel = xAxisLabel;
		this.yAxisLabel = yAxisLabel;
		this.columnLabels = columnLabels;
		draw();
	}

	public void draw() {
		// Prepare the data
		DataTable dataTable = DataTable.create();
		dataTable.addColumn(ColumnType.STRING, "Period");
		dataTable.addColumn(ColumnType.NUMBER, "Value");

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
		options.setTitle(title);
		options.setIsStacked(true);
		options.setHAxis(HAxis.create(xAxisLabel));
		options.setVAxis(VAxis.create(yAxisLabel));

		// Draw the chart
		chart.draw(dataTable, options);
	}

	public void setHandler(IntegerSelectionHandler handler) {
		this.handler = handler;
	}

}
