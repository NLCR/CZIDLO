package cz.nkp.urnnbn.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.StackLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.Selection;
import com.googlecode.gwt.charts.client.corechart.ColumnChart;
import com.googlecode.gwt.charts.client.corechart.ColumnChartOptions;
import com.googlecode.gwt.charts.client.corechart.PieChart;
import com.googlecode.gwt.charts.client.corechart.PieChartOptions;
import com.googlecode.gwt.charts.client.event.ReadyEvent;
import com.googlecode.gwt.charts.client.event.ReadyHandler;
import com.googlecode.gwt.charts.client.event.SelectEvent;
import com.googlecode.gwt.charts.client.event.SelectHandler;
import com.googlecode.gwt.charts.client.options.HAxis;
import com.googlecode.gwt.charts.client.options.VAxis;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Graphs implements EntryPoint {

	private static final Logger logger = Logger.getLogger(Graphs.class.getSimpleName());
	private static final String SERVER_ERROR = "An error occurred while attempting to contact the server. Please check your network connection and try again.";

	/**
	 * Create a remote service proxy to talk to the server-side Greeting service.
	 */
	private final StatisticsServiceAsync service = GWT.create(StatisticsService.class);

	// private SimpleLayoutPanel layoutPanel;
	// private VerticalPanel panel;

	private StackLayoutPanel panel;
	// TabLayoutPanel panel;

	// private RegistrationsByRegistrarPieChart totalByRegistrarChart;
	private ColumnChart yearsChart;
	private PieChart pieChart;
	private ColumnChart columnChart;

	/**
	 * This is the entry point method.
	 */
	@Override
	public void onModuleLoad() {
		Window.enableScrolling(true);
		Window.setMargin("0px");
		RootLayoutPanel.get().add(getPanel());

		// Create the API Loader
		ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
		chartLoader.loadApi(new Runnable() {

			@Override
			public void run() {

				// getPanel().add(getYearsChart(), new HTML("years"));
				// getPanel().add(getTotalByRegistrarPieChart(),
				// new HTML("total by registrar"));

				getPanel().add(getYearsChart(), new HTML("years"), 4);
				getPanel().add(getPieChart(), new HTML("pie test"), 4);
				getPanel().add(getColumnChart(), new HTML("column test"), 4);

				// getPanel().add(new HTML("TODO"),
				// new HTML("months (registrar)"), 4);
				// getPanel().add(getTotalByRegistrarPieChart().getWidget(),
				// new HTML("total by registrar"), 4);
				initData();
			}

		});
	}

	private void initData() {
		// greetingService
		// .getTotalRegistrationsByRegistrar(new AsyncCallback<Map<Registrar,
		// Integer>>() {
		//
		// @Override
		// public void onSuccess(Map<Registrar, Integer> result) {
		// getTotalByRegistrarPieChart().setData(result);
		// getTotalByRegistrarPieChart().setSelectionHandler(
		// new OnSelectionHandler() {
		//
		// @Override
		// public void onSelected(Registrar registrar) {
		// // TODO
		// logger.info("selected "
		// + registrar.getName());
		//
		// }
		// });
		// getTotalByRegistrarPieChart().draw();
		// }
		//
		// @Override
		// public void onFailure(Throwable caught) {
		// logger.severe(caught.getMessage());
		// }
		// });

		service.getAssignmentsByYear(new AsyncCallback<Map<Integer, Map<String, Integer>>>() {

			@Override
			public void onSuccess(Map<Integer, Map<String, Integer>> result) {
				drawColumnChart(result);

			}

			@Override
			public void onFailure(Throwable caught) {
				// logger.severe("here-x-2");
				logger.severe(caught.getMessage());
			}
		});

		drawTestPieChart();
		drawTestColumnChart();

	}

	private void drawTestColumnChart() {
		// String[] countries = new String[] { "Austria", "Bulgaria", "Denmark",
		// "Greece" };
		// String[] countries = new String[] { "Austria", "Bulgaria" };
		String[] countries = new String[] { "Austria" };
		// int[] years = new int[] { 2001, 2002, 2003, 2004, 2005, 2006, 2007,
		// 2008, 2009, 2010, 2011, 2012};
		int[] years = new int[] { 2001, 2002, 2003, 2004, 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2012, 2001, 2002, 2003, 2004, 2005, 2006, 2007,
				2008, 2009, 2010, 2011, 2012 };
		int[][] values = new int[][] {
		// { 1336060, 1538156, 1576579, 1600652, 1968113, 1901067, 1336060,
		// 1538156, 1576579, 1600652, 1968113, 1901067},
		// { 400361, 366849, 440514, 434552, 393032, 517206,400361, 366849,
		// 440514, 434552, 393032, 517206 },
		// { 1001582, 1119450, 993360, 1004163, 979198, 916965,1001582, 1119450,
		// 993360, 1004163, 979198, 916965 },
		// { 997974, 941795, 930593, 897127, 1080887, 1056036,997974, 941795,
		// 930593, 897127, 1080887, 1056036 } };
		// { 997974, 941795, 930593, 897127, 1080887, 1056036,997974, 941795,
		// 930593, 897127, 1080887, 1056036,997974, 941795, 930593, 897127,
		// 1080887, 1056036,997974, 941795, 930593, 897127, 1080887, 1056036 }
		// };
		{ 997974, 941795, 930593, 897127, 1080887, 1056036, 997974, 941795, 930593, 897127, 1080887, 1056036, 997974, 941795, 930593, 7, 1080887,
				100, 997974, 1000, 930593, 10000, 1080887, 1056036 } };

		// Prepare the data
		DataTable dataTable = DataTable.create();
		dataTable.addColumn(ColumnType.STRING, "Year");
		for (int i = 0; i < countries.length; i++) {
			dataTable.addColumn(ColumnType.NUMBER, countries[i]);
		}
		dataTable.addRows(years.length);
		for (int i = 0; i < years.length; i++) {
			dataTable.setValue(i, 0, String.valueOf(years[i]));
		}
		for (int col = 0; col < values.length; col++) {
			for (int row = 0; row < values[col].length; row++) {
				dataTable.setValue(row, col + 1, values[col][row]);
			}
		}

		// Set options
		ColumnChartOptions options = ColumnChartOptions.create();
		options.setFontName("Tahoma");
		options.setTitle("Yearly Coffee Consumption by Country");
		options.setHAxis(HAxis.create("Cups"));
		options.setVAxis(VAxis.create("Year"));

		// Draw the chart
		getColumnChart().draw(dataTable, options);
	}

	private void drawTestPieChart() {
		// Prepare the data
		DataTable dataTable = DataTable.create();
		dataTable.addColumn(ColumnType.STRING, "Task");
		dataTable.addColumn(ColumnType.NUMBER, "Hours per Day");
		dataTable.addRows(5);
		dataTable.setValue(0, 0, "Work");
		dataTable.setValue(0, 1, 11);
		dataTable.setValue(1, 0, "Sleep");
		dataTable.setValue(1, 1, 7);
		dataTable.setValue(2, 0, "Watch TV");
		dataTable.setValue(2, 1, 3);
		dataTable.setValue(3, 0, "Eat");
		dataTable.setValue(3, 1, 2);
		dataTable.setValue(4, 0, "Commute");
		dataTable.setValue(4, 1, 1);
		// dataTable.setValue(5, 0, "Other");
		// dataTable.setValue(5, 1, 3);

		// Set options
		PieChartOptions options = PieChartOptions.create();
		options.setBackgroundColor("#f0f0f0");

		// options.setColors(colors);
		options.setFontName("Tahoma");
		options.setIs3D(false);
		options.setPieResidueSliceColor("#000000");
		options.setPieResidueSliceLabel("Others");
		options.setSliceVisibilityThreshold(0.1);
		options.setTitle("So, how was your day?");

		// Draw the chart
		getPieChart().draw(dataTable, options);
		getPieChart().addReadyHandler(new ReadyHandler() {

			@Override
			public void onReady(ReadyEvent event) {
				getPieChart().setSelection(Selection.create(1, null));
			}
		});

		// TODO Auto-generated method stub

	}

	private StackLayoutPanel getPanel() {
		if (panel == null) {
			panel = new StackLayoutPanel(Unit.EM);
		}
		return panel;
	}

	private PieChart getPieChart() {
		if (pieChart == null) {
			pieChart = new PieChart();
		}
		return pieChart;
	}

	private ColumnChart getColumnChart() {
		if (columnChart == null) {
			columnChart = new ColumnChart();
		}
		return columnChart;
	}

	// return panel;
	// // p.add(new HTML("this content"), new HTML("this"), 4);
	// // p.add(new HTML("that content"), new HTML("that"), 4);
	// // p.add(new HTML("the other content"), new HTML("the other"), 4);
	// }

	// private VerticalPanel getPanel() {
	// if (panel == null) {
	// panel = new VerticalPanel();
	// }
	// return panel;
	// }

	// private TabLayoutPanel getPanel() {
	// if (panel == null) {
	// panel = new TabLayoutPanel(1.5, Unit.EM);
	// }
	// return panel;
	// }

	// private RegistrationsByRegistrarPieChart getTotalByRegistrarPieChart() {
	// if (totalByRegistrarChart == null) {
	// totalByRegistrarChart = new RegistrationsByRegistrarPieChart();
	// }
	// return totalByRegistrarChart;
	// }

	private Widget getYearsChart() {
		if (yearsChart == null) {
			yearsChart = new ColumnChart();
		}
		return yearsChart;
	}

	private void drawColumnChart(Map<Integer, Map<String, Integer>> result) {
		List<Integer> years = toSortedYearList(result);
		List<String> registrarCodes = toSortedRegistrarCodes(result);

		DataTable dataTable = DataTable.create();
		dataTable.addColumn(ColumnType.STRING, "Year");
		for (int i = 0; i < registrarCodes.size(); i++) {
			dataTable.addColumn(ColumnType.NUMBER, registrarCodes.get(i));
		}

		dataTable.addRows(years.size());
		for (int i = 0; i < years.size(); i++) {
			dataTable.setValue(i, 0, String.valueOf(years.get(i)));
		}

		for (int col = 0; col < years.size(); col++) { // sloupec je rok
			int year = years.get(col);
			// logger.severe("year: " + year);
			Map<String, Integer> registrarsMap = result.get(year);

			for (int row = 0; row < registrarCodes.size(); row++) {
				String registrarCode = registrarCodes.get(row);
				// logger.severe("registrar: " + registrarCode);
				Integer registrations = registrarsMap.get(registrarCode);
				// logger.severe("registrations: " + registrations);
				dataTable.setValue(row, col + 1, registrations != null ? registrations : 0);
				// logger.severe("registrar finished");
			}
		}

		// logger.severe("here3");

		// Set options
		ColumnChartOptions options = ColumnChartOptions.create();
		// options.setFontName("Tahoma");
		options.setTitle("Total annual registrations by Registrar");
		options.setHAxis(HAxis.create("Registrations"));
		options.setVAxis(VAxis.create("Year"));

		// Draw the chart
		yearsChart.draw(dataTable, options);
		yearsChart.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {

				JsArray<Selection> selection = yearsChart.getSelection();
				for (int i = 0; i < selection.length(); i++) {
					Selection sel = selection.get(i);
					int column = sel.getColumn();
					int row = sel.getRow();
					logger.severe("column: " + column + ", row: " + row);
				}
			}
		});

	}

	private List<String> toSortedRegistrarCodes(Map<Integer, Map<String, Integer>> data) {
		Set<String> registrarCodes = new HashSet<>();
		for (Integer year : data.keySet()) {
			registrarCodes.addAll(data.get(year).keySet());
		}
		List<String> result = new ArrayList<>();
		result.addAll(registrarCodes);
		Collections.sort(result);
		for (String registrar : result) {
			logger.info("registrar " + registrar);
		}
		return result;
	}

	private List<Integer> toSortedYearList(Map<Integer, Map<String, Integer>> data) {
		List<Integer> result = new ArrayList<>();
		result.addAll(data.keySet());
		Collections.sort(result);
		for (Integer year : result) {
			logger.info("year " + year);
		}
		return result;
	}

	private void drawColumnChart() {
		String[] countries = new String[] { "Austria", "Bulgaria", "Denmark", "Greece" };
		int[] years = new int[] { 2003, 2004, 2005, 2006, 2007, 2008 };
		int[][] values = new int[][] { { 1336060, 1538156, 1576579, 1600652, 1968113, 1901067 }, { 400361, 366849, 440514, 434552, 393032, 517206 },
				{ 1001582, 1119450, 993360, 1004163, 979198, 916965 }, { 997974, 941795, 930593, 897127, 1080887, 1056036 } };

		// Prepare the data
		DataTable dataTable = DataTable.create();
		dataTable.addColumn(ColumnType.STRING, "Year");
		for (int i = 0; i < countries.length; i++) {
			dataTable.addColumn(ColumnType.NUMBER, countries[i]);
		}

		dataTable.addRows(years.length);
		for (int i = 0; i < years.length; i++) {
			dataTable.setValue(i, 0, String.valueOf(years[i]));
		}
		for (int col = 0; col < values.length; col++) {
			for (int row = 0; row < values[col].length; row++) {
				dataTable.setValue(row, col + 1, values[col][row]);
			}
		}

		// Set options
		ColumnChartOptions options = ColumnChartOptions.create();
		options.setFontName("Tahoma");
		options.setTitle("Total annual registrations by Registrar");
		options.setHAxis(HAxis.create("Registrations"));
		options.setVAxis(VAxis.create("Year"));

		// Draw the chart
		yearsChart.draw(dataTable, options);

	}

	// private void drawTotalByRegistrarPieChart(Map<String, Integer> result) {
	// DataTable dataTable = DataTable.create();
	// // dataTable.addColumn(ColumnType.STRING, "Name");
	// dataTable.addColumn(ColumnType.STRING, "Code");
	// dataTable.addColumn(ColumnType.NUMBER, "registrations");
	// Set<String> keys = result.keySet();
	// dataTable.addRows(keys.size());
	// List<String> keyList = new ArrayList<>(keys.size());
	// keyList.addAll(keys);
	// for (int i = 0; i < keyList.size(); i++) {
	// String key = keyList.get(i);
	// Integer registrations = result.get(key);
	// dataTable.setValue(i, 0, key);
	// dataTable.setValue(i, 1, registrations);
	// }
	// // Draw the chart
	// totalByRegistrarChart.draw(dataTable);
	// }

	// @Override
	// public void onClick(ClickEvent event) {
	// logger.severe(event.toDebugString());
	// }

	/**
	 * This is the entry point method.
	 */
	// public void onModuleLoad() {
	// final Button sendButton = new Button("Send");
	// final TextBox nameField = new TextBox();
	// nameField.setText("GWT User");
	// final Label errorLabel = new Label();
	//
	// // We can add style names to widgets
	// sendButton.addStyleName("sendButton");
	//
	// // Add the nameField and sendButton to the RootPanel
	// // Use RootPanel.get() to get the entire body element
	// RootPanel.get("nameFieldContainer").add(nameField);
	// RootPanel.get("sendButtonContainer").add(sendButton);
	// RootPanel.get("errorLabelContainer").add(errorLabel);
	//
	// // Focus the cursor on the name field when the app loads
	// nameField.setFocus(true);
	// nameField.selectAll();
	//
	// // Create the popup dialog box
	// final DialogBox dialogBox = new DialogBox();
	// dialogBox.setText("Remote Procedure Call");
	// dialogBox.setAnimationEnabled(true);
	// final Button closeButton = new Button("Close");
	// // We can set the id of a widget by accessing its Element
	// closeButton.getElement().setId("closeButton");
	// final Label textToServerLabel = new Label();
	// final HTML serverResponseLabel = new HTML();
	// VerticalPanel dialogVPanel = new VerticalPanel();
	// dialogVPanel.addStyleName("dialogVPanel");
	// dialogVPanel.add(new HTML("<b>Sending name to the server:</b>"));
	// dialogVPanel.add(textToServerLabel);
	// dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
	// dialogVPanel.add(serverResponseLabel);
	// dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
	// dialogVPanel.add(closeButton);
	// dialogBox.setWidget(dialogVPanel);
	//
	// // Add a handler to close the DialogBox
	// closeButton.addClickHandler(new ClickHandler() {
	// public void onClick(ClickEvent event) {
	// dialogBox.hide();
	// sendButton.setEnabled(true);
	// sendButton.setFocus(true);
	// }
	// });
	//
	// // Create a handler for the sendButton and nameField
	// class MyHandler implements ClickHandler, KeyUpHandler {
	// /**
	// * Fired when the user clicks on the sendButton.
	// */
	// public void onClick(ClickEvent event) {
	// sendNameToServer();
	// }
	//
	// /**
	// * Fired when the user types in the nameField.
	// */
	// public void onKeyUp(KeyUpEvent event) {
	// if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
	// sendNameToServer();
	// }
	// }
	//
	// /**
	// * Send the name from the nameField to the server and wait for a response.
	// */
	// private void sendNameToServer() {
	// // First, we validate the input.
	// errorLabel.setText("");
	// String textToServer = nameField.getText();
	// if (!FieldVerifier.isValidName(textToServer)) {
	// errorLabel.setText("Please enter at least 4 characters");
	// return;
	// }
	//
	// // Then, we send the input to the server.
	// sendButton.setEnabled(false);
	// textToServerLabel.setText(textToServer);
	// serverResponseLabel.setText("");
	// greetingService.greetServer(textToServer,
	// new AsyncCallback<String>() {
	// public void onFailure(Throwable caught) {
	// // Show the RPC error message to the user
	// dialogBox
	// .setText("Remote Procedure Call - Failure");
	// serverResponseLabel
	// .addStyleName("serverResponseLabelError");
	// serverResponseLabel.setHTML(SERVER_ERROR);
	// dialogBox.center();
	// closeButton.setFocus(true);
	// }
	//
	// public void onSuccess(String result) {
	// dialogBox.setText("Remote Procedure Call");
	// serverResponseLabel
	// .removeStyleName("serverResponseLabelError");
	// serverResponseLabel.setHTML(result);
	// dialogBox.center();
	// closeButton.setFocus(true);
	// }
	// });
	// }
	// }
	//
	// // Add a handler to send the name to the server
	// MyHandler handler = new MyHandler();
	// sendButton.addClickHandler(handler);
	// nameField.addKeyUpHandler(handler);
	// }
}
