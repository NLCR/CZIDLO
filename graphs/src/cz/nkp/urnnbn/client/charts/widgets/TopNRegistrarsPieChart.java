package cz.nkp.urnnbn.client.charts.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.user.client.ui.Composite;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.Selection;
import com.googlecode.gwt.charts.client.corechart.PieChart;
import com.googlecode.gwt.charts.client.corechart.PieChartOptions;
import com.googlecode.gwt.charts.client.event.SelectEvent;
import com.googlecode.gwt.charts.client.event.SelectHandler;

import cz.nkp.urnnbn.client.charts.Utils;

public class TopNRegistrarsPieChart extends Composite {

	private static final Logger LOGGER = Logger.getLogger(TopNRegistrarsPieChart.class.getSimpleName());

	// fixed data
	private static final int MAX_REGISTRARS = 3;

	// fixed data
	private final int width;
	private final int height;
	// data
	private List<RegistrarWithStatistic> topNRecords = null;
	private int remainingAmount = 0;
	private Map<String, String> registrarNames = null;
	// labels
	private String title;
	// widgets
	private final PieChart chart;
	// callbacks
	private RegistrarSelectionHandler registrarSelectionHandler;

	public TopNRegistrarsPieChart(int width, int height) {
		this.width = width;
		this.height = height;
		chart = new PieChart();
		chart.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				if (registrarSelectionHandler != null) {
					Selection selection = chart.getSelection().get(0);
					if (selection != null) {
						int row = selection.getRow();
						if (row == 0) {
							// sum of other registrars, ignore
						} else {
							int position = row;
							String registrarCode = topNRecords.get(position - 1).getCode();
							registrarSelectionHandler.onSelected(registrarCode);
						}
					}
				}
			}

		});

		initWidget(chart);
	}

	public void setDataAndDraw(int totalAssignments, Map<String, Integer> assignmentsByRegistrar, String title, Map<String, String> registraNames,
			String neutralValueColor, Map<String, String> registrarColorMap) {
		this.topNRecords = extractTopn(assignmentsByRegistrar, MAX_REGISTRARS);
		this.remainingAmount = extractOtherAmount(topNRecords, totalAssignments);
		this.title = title;
		this.registrarNames = registraNames;
		draw(neutralValueColor, registrarColorMap);
	}

	private int extractOtherAmount(List<RegistrarWithStatistic> data, int totalAssignments) {
		int sum = 0;
		for (RegistrarWithStatistic registrar : data) {
			sum += registrar.getData();
		}
		return Math.max(0, totalAssignments - sum);
	}

	private List<RegistrarWithStatistic> extractTopn(Map<String, Integer> assignmentsByRegistrar, int n) {
		List<RegistrarWithStatistic> result = new ArrayList<RegistrarWithStatistic>(assignmentsByRegistrar.size());
		for (String registrarCode : assignmentsByRegistrar.keySet()) {
			Integer amount = assignmentsByRegistrar.get(registrarCode);
			result.add(new RegistrarWithStatistic(registrarCode, amount));
		}
		Collections.sort(result);
		if (n >= result.size()) {
			return result;
		} else {
			return result.subList(0, n);
		}
	}

	private void draw(String neutralValueColor, Map<String, String> registrarColorMap) {
		DataTable pieNewData = DataTable.create();
		// TODO: i18n
		pieNewData.addColumn(ColumnType.STRING, "registrar");
		pieNewData.addColumn(ColumnType.NUMBER, "statistics");
		List<String> colors = new ArrayList<>();
		// others
		pieNewData.addRow("Ostatní", remainingAmount);
		colors.add(neutralValueColor);
		// registrars
		for (RegistrarWithStatistic registrar : topNRecords) {
			// String label = registraNames == null ? registrar.getCode() : registrar.getCode() + " - " + registraNames.get(registrar.getCode());
			String label = registrarNames == null ? registrar.getCode() : registrarNames.get(registrar.getCode());
			pieNewData.addRow(label, registrar.getData());
			colors.add(Utils.getRegistrarColor(registrar.getCode(), neutralValueColor, registrarColorMap));
		}

		PieChartOptions options = PieChartOptions.create();
		options.setWidth(width);
		options.setHeight(height);
		options.setTitle(title);
		if (!Utils.containsNullValue(colors)) {
			options.setColors((colors.toArray(new String[colors.size()])));
		}

		chart.draw(pieNewData, options);
	}

	public void setRegistrarSelectionHandler(RegistrarSelectionHandler registrarSelectionHandler) {
		this.registrarSelectionHandler = registrarSelectionHandler;
	}

}
