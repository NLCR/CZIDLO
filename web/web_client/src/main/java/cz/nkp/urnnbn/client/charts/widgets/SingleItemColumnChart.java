package cz.nkp.urnnbn.client.charts.widgets;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.user.client.ui.Composite;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.Selection;
import com.googlecode.gwt.charts.client.corechart.ColumnChart;
import com.googlecode.gwt.charts.client.corechart.ColumnChartOptions;
import com.googlecode.gwt.charts.client.event.SelectEvent;
import com.googlecode.gwt.charts.client.event.SelectHandler;
import com.googlecode.gwt.charts.client.options.HAxis;
import com.googlecode.gwt.charts.client.options.VAxis;

public class SingleItemColumnChart extends Composite {

    private static final Logger LOGGER = Logger.getLogger(SingleItemColumnChart.class.getSimpleName());

    // fixed data
    private final int width;
    private final int height;

    // data
    private List<Integer> periods;
    private Map<Integer, Integer> data; // period -> volume_per_period
    private String valueColor;

    // labels
    private String title;
    private String valueDescription;
    private String xAxisLabel;
    private String yAxisLabel;
    private Map<Integer, String> columnLabels;

    // widgets
    private final ColumnChart chart;
    // callbacks
    private IntegerSelectionHandler yearSelectionHandler;

    public SingleItemColumnChart(int width, int height) {
        this.width = width;
        this.height = height;
        chart = new ColumnChart();
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

    public void setDataAndDraw(List<Integer> periods, Map<Integer, Integer> data, String title, String valueDescription, String xAxisLabel,
            String yAxisLabel, Map<Integer, String> columnLabels, String valueColor) {
        this.periods = periods;
        this.data = data;
        this.title = title;
        this.valueDescription = valueDescription;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
        this.columnLabels = columnLabels;
        this.valueColor = valueColor;
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
        options.setWidth(width);
        options.setHeight(height);
        options.setTitle(title);
        options.setHAxis(HAxis.create(xAxisLabel));
        options.setVAxis(VAxis.create(yAxisLabel));
        if (valueColor != null) {
            options.setColors(new String[] { valueColor });
        }

        // Draw the chart
        chart.draw(dataTable, options);
    }

    public void setYearSelectionHandler(IntegerSelectionHandler yearSelectionHandler) {
        this.yearSelectionHandler = yearSelectionHandler;
    }

}
