package cz.nkp.urnnbn.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.Selection;
import com.googlecode.gwt.charts.client.corechart.PieChart;
import com.googlecode.gwt.charts.client.event.SelectEvent;
import com.googlecode.gwt.charts.client.event.SelectHandler;

import cz.nkp.urnnbn.shared.Registrar;

public class RegistrationsByRegistrarPieChart {

	// private Map<Registrar, Integer> totalRegistrations;
	// private List<Registrar> registrars;
	// private List<Integer> registrations;
	private final PieChart chart = new PieChart();
	private List<RegistrarWithRegistration> data;
	private OnSelectionHandler selectionHandler;

	public void setSelectionHandler(OnSelectionHandler selectionHandler) {
		this.selectionHandler = selectionHandler;
	}

	public void setData(Map<Registrar, Integer> registrarRegistrationsMap) {
		Set<Registrar> registrarSet = registrarRegistrationsMap.keySet();
		data = new ArrayList<>(registrarSet.size());
		for (Registrar registrar : registrarSet) {
			Integer registrations = registrarRegistrationsMap.get(registrar);
			data.add(new RegistrarWithRegistration(registrar, registrations));
		}
		Collections.sort(data);
		// selection handler
		chart.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				if (selectionHandler != null) {
					Selection selections = chart.getSelection().get(0);
					int row = selections.getRow();
					selectionHandler.onSelected(data.get(row).getRegistrar());
				}
			}
		});
	}

	public Widget getWidget() {
		return chart;
	}

	public void draw() {
		if (data != null) {
			DataTable dataTable = DataTable.create();
			// dataTable.addColumn(ColumnType.STRING, "Name");
			// TODO: i18n
			dataTable.addColumn(ColumnType.STRING, "Code");
			dataTable.addColumn(ColumnType.NUMBER, "Registrations");
			for (int i = 0; i < data.size(); i++) {
				RegistrarWithRegistration item = data.get(i);
				dataTable.setValue(i, 0, item.getRegistrar().getCode());
				dataTable.setValue(i, 1, item.getRegistrations());
			}
			// Draw the chart
			chart.draw(dataTable);
		}
	}

	private static class RegistrarWithRegistration implements Comparable<RegistrarWithRegistration> {
		private final Registrar registrar;
		private final Integer registrations;

		public RegistrarWithRegistration(Registrar registrar, Integer registrations) {
			super();
			this.registrar = registrar;
			this.registrations = registrations;
		}

		public Registrar getRegistrar() {
			return registrar;
		}

		public Integer getRegistrations() {
			return registrations;
		}

		@Override
		public int compareTo(RegistrarWithRegistration o) {
			return registrations.compareTo(o.getRegistrations());
		}
	}

	public static interface OnSelectionHandler {
		public void onSelected(Registrar registrar);
	}

}
