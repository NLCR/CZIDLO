package cz.nkp.urnnbn.client.widgets;

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

import cz.nkp.urnnbn.client.RegistrarWithStatistic;

public class TopNRegistrarsPieChart extends Composite {

	private static final Logger LOGGER = Logger.getLogger(TopNRegistrarsPieChart.class.getSimpleName());
	private static final int MAX_REGISTRARS = 3;

	// data
	private List<RegistrarWithStatistic> topNRecords = null;
	private int remainingAmount = 0;
	private Map<String, String> registraNames = null;
	// labels
	private String title;
	// widgets
	private final PieChart chart;
	// callbacks
	private StringSelectionHandler handler;

	public TopNRegistrarsPieChart() {
		chart = new PieChart();
		chart.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				if (handler != null) {
					Selection selection = chart.getSelection().get(0);
					if (selection != null) {
						int row = selection.getRow();
						if (row == 0) {
							// sum of other registrars, ignore
						} else {
							String registrarCode = topNRecords.get(row - 1).getCode();
							LOGGER.info("selected registrar: " + registrarCode);
							handler.onSelected(registrarCode);
						}
					}
				}
			}
		});

		initWidget(chart);
	}

	public void setDataAndDraw(int totalAssignments, Map<String, Integer> assignmentsByRegistrar, String title, Map<String, String> registraNames) {
		this.topNRecords = extractTopn(assignmentsByRegistrar, MAX_REGISTRARS);
		this.remainingAmount = extractOtherAmount(topNRecords, totalAssignments);
		this.title = title;
		this.registraNames = registraNames;
		draw();
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

	private void draw() {
		DataTable pieNewData = DataTable.create();
		// TODO: i18n
		pieNewData.addColumn(ColumnType.STRING, "registrar");
		pieNewData.addColumn(ColumnType.NUMBER, "statistics");
		pieNewData.addRow("Ostatní", remainingAmount);
		for (RegistrarWithStatistic registrar : topNRecords) {
			String label = registraNames == null ? registrar.getCode() : registrar.getCode() + " - " + registraNames.get(registrar.getCode());
			pieNewData.addRow(label, registrar.getData());
		}
		PieChartOptions options = PieChartOptions.create();
		options.setTitle(title);
		options.setIs3D(true);
		chart.draw(pieNewData, options);
	}

	public void setHandler(StringSelectionHandler handler) {
		this.handler = handler;
	}

}
