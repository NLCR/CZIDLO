package cz.nkp.urnnbn.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.PieChart;

public class TopNRegistrarsPieChart {

	private static final Logger LOGGER = Logger.getLogger(TopNRegistrarsPieChart.class.getSimpleName());
	private static final int MAX_REGISTRARS = 3;

	private final PieChart chart;

	private List<RegistrarWithData> topNRecords = null;
	private int remainingAmount = 0;

	public TopNRegistrarsPieChart() {
		chart = new PieChart();
	}

	public void setDataAndDraw(int totalAssignments, Map<String, Integer> assignmentsByRegistrar) {
		topNRecords = extractTopn(assignmentsByRegistrar, MAX_REGISTRARS);
		remainingAmount = extractOtherAmount(topNRecords, totalAssignments);
		draw();
	}

	private int extractOtherAmount(List<RegistrarWithData> data, int totalAssignments) {
		int sum = 0;
		for (RegistrarWithData registrar : data) {
			sum += registrar.data;
		}
		return Math.max(0, totalAssignments - sum);
	}

	private List<RegistrarWithData> extractTopn(Map<String, Integer> assignmentsByRegistrar, int n) {
		List<RegistrarWithData> result = new ArrayList<RegistrarWithData>(assignmentsByRegistrar.size());
		for (String registrarCode : assignmentsByRegistrar.keySet()) {
			Integer amount = assignmentsByRegistrar.get(registrarCode);
			result.add(new RegistrarWithData(registrarCode, amount));
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
		for (RegistrarWithData registrar : topNRecords) {
			pieNewData.addRow(registrar.code, registrar.data);
		}
		// TODO: i18n
		pieNewData.addRow("Other", remainingAmount);
		chart.draw(pieNewData);
	}

	public Widget getWidget() {
		return chart;
	}

	public class RegistrarWithData implements Comparable<RegistrarWithData> {
		String code;
		Integer data;

		public RegistrarWithData(String code, Integer data) {
			this.code = code;
			this.data = data;
		}

		@Override
		public int compareTo(RegistrarWithData other) {
			// decreasing order
			return -data.compareTo(other.data);
		}
	}

}
