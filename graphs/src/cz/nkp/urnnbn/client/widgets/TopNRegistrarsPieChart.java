package cz.nkp.urnnbn.client.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.PieChart;

import cz.nkp.urnnbn.client.RegistrarWithStatistic;

public class TopNRegistrarsPieChart extends Composite {

	private static final Logger LOGGER = Logger.getLogger(TopNRegistrarsPieChart.class.getSimpleName());
	private static final int MAX_REGISTRARS = 3;

	// data
	private List<RegistrarWithStatistic> topNRecords = null;
	private int remainingAmount = 0;
	private Map<String, String> registraNames = null;

	// widgets
	private final PieChart chart;

	public TopNRegistrarsPieChart() {
		chart = new PieChart();
		initWidget(chart);
	}

	public void setDataAndDraw(int totalAssignments, Map<String, Integer> assignmentsByRegistrar, Map<String, String> registraNames) {
		this.topNRecords = extractTopn(assignmentsByRegistrar, MAX_REGISTRARS);
		this.remainingAmount = extractOtherAmount(topNRecords, totalAssignments);
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
		pieNewData.addColumn(ColumnType.STRING, "Registrar");
		pieNewData.addColumn(ColumnType.NUMBER, "registrations");
		pieNewData.addRow("Ostatní", remainingAmount);
		for (RegistrarWithStatistic registrar : topNRecords) {
			String label = registraNames == null ? registrar.getCode() : registrar.getCode() + " - " + registraNames.get(registrar.getCode());
			pieNewData.addRow(label, registrar.getData());
		}
		chart.draw(pieNewData);
	}

}
