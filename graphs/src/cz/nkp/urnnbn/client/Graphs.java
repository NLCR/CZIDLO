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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
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

import cz.nkp.urnnbn.shared.Registrar;

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

	// private StackLayoutPanel panel;
	private VerticalPanel container;
	private VerticalPanel header;
	private HorizontalPanel headerRegistrators;
	private HorizontalPanel headerCumulated;
	private HorizontalPanel headerYears;

	// data
	private Registrar currentRegistrar;
	private Map<Integer, Integer> currentData;
	private boolean accumulated = false;
	private Integer currentYear = null;

	// TabLayoutPanel panel;

	// private RegistrationsByRegistrarPieChart totalByRegistrarChart;
	private ColumnChart yearsChart;
	private PieChart pieChart;
	private ColumnChart columnChart;

	private IntegerKeyColumnChart registrarYearlyChart;
	// after chartloader
	private RegistrarAssignmentsWidget registrarGraph;
	private RegistrarsAssignmentsWidget registrarsGraph;

	/**
	 * This is the entry point method.
	 */
	@Override
	public void onModuleLoad() {
		// logger.info("onModuleLoad");
		Window.enableScrolling(true);
		Window.setMargin("0px");

		container = new VerticalPanel();
		container.setWidth("100%");
		// container.setWidth("5000px");
		// container.setHeight("500px");
		RootLayoutPanel.get().add(container);

		// header
		header = new VerticalPanel();
		header.setSpacing(10);
		header.setWidth("100%");
		container.add(header);
		// registrar selection
		headerRegistrators = new HorizontalPanel();
		header.add(headerRegistrators);
		initRegistrars();
		// accumulated checkbox
		headerCumulated = new HorizontalPanel();
		CheckBox cumulatedCheckbox = new CheckBox("kumulované");
		cumulatedCheckbox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				accumulated = event.getValue();
				reloadGraphs();
			}
		});
		headerCumulated.add(cumulatedCheckbox);
		header.add(headerCumulated);
		// year selection

		headerYears = new HorizontalPanel();
		header.add(headerYears);
		initYears();

		// registrarYearlyChart = new IntegerKeyColumnChart();
		// container.add(registrarYearlyChart.getWidget());

		// Create the API Loader
		ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
		chartLoader.loadApi(new Runnable() {

			@Override
			public void run() {
				logger.info("chart api loaded");

				// registrarYearlyChart = new IntegerKeyColumnChart();
				// container.add(registrarYearlyChart.getWidget());

				// container.add(getYearsChart());
				// container.add(getPieChart());
				// container.add(getColumnChart());

				TextBox blabla = new TextBox();
				blabla.setText("blabla");
				// container.add(blabla);
				service.getYearsSorted(new AsyncCallback<List<Integer>>() {

					@Override
					public void onSuccess(List<Integer> result) {
						registrarGraph = new RegistrarAssignmentsWidget(result);
						container.add(registrarGraph);
						registrarsGraph = new RegistrarsAssignmentsWidget(result);
						container.add(registrarsGraph);
					}

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}
				});
			}

		});
	}

	private void initYears() {
		service.getYearsSorted(new AsyncCallback<List<Integer>>() {

			@Override
			public void onSuccess(List<Integer> result) {
				RadioButton wholeTimeCheckbox = new RadioButton("years", "celé období");
				headerYears.add(wholeTimeCheckbox);
				wholeTimeCheckbox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

					@Override
					public void onValueChange(ValueChangeEvent<Boolean> event) {
						// currentYear = null;
						// reloadGraphs();
						loadData(currentRegistrar, null);
					}
				});
				wholeTimeCheckbox.setValue(true);

				for (Integer year : result) {
					final Integer thisYear = year;
					RadioButton yearCheckbox = new RadioButton("years", year.toString());
					headerYears.add(yearCheckbox);
					yearCheckbox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

						@Override
						public void onValueChange(ValueChangeEvent<Boolean> event) {
							// currentYear = thisYear;
							loadData(currentRegistrar, thisYear);
							// reloadGraphs();
						}
					});
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				logger.severe(caught.getMessage());
			}
		});

	}

	private void initRegistrars() {
		service.getRegistrars(new AsyncCallback<Set<Registrar>>() {

			@Override
			public void onSuccess(Set<Registrar> result) {
				RadioButton totalButton = new RadioButton("registrars", "total");
				headerRegistrators.add(totalButton);
				totalButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

					@Override
					public void onValueChange(ValueChangeEvent<Boolean> event) {
						boolean selected = event.getValue();
						if (selected) {
							loadData(null, currentYear);
						}
					}
				});
				totalButton.setValue(true);

				for (Registrar registrar : result) {
					RadioButton registrarButton = new RadioButton("registrars", registrar.getCode());
					headerRegistrators.add(registrarButton);
					final Registrar thisRegistrar = registrar;
					registrarButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

						@Override
						public void onValueChange(ValueChangeEvent<Boolean> event) {
							boolean selected = event.getValue();
							if (selected) {
								loadData(thisRegistrar, currentYear);
							}
						}
					});
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				logger.severe(caught.getMessage());
			}
		});
	}

	private void loadData(final Registrar registrar, final Integer year) {
		if (registrarGraph != null) {
			registrarGraph.setRegistrar(registrar);
		}

		AsyncCallback<Map<Integer, Integer>> callback = new AsyncCallback<Map<Integer, Integer>>() {

			@Override
			public void onSuccess(Map<Integer, Integer> result) {
				currentRegistrar = registrar;
				currentYear = year;
				currentData = result;
				reloadGraphs();
			}

			@Override
			public void onFailure(Throwable caught) {
				logger.severe(caught.getMessage());
			}
		};

		if (registrar != null) {
			if (year != null) {
				service.getAssignmentsByMonth(registrar.getCode(), year, true, true, callback);
			} else {
				service.getAssignmentsByYear(registrar.getCode(), true, true, callback);
			}
		} else {
			if (year != null) {
				service.getTotalAssignmentsByMonth(year, true, true, callback);
			} else {
				service.getTotalAssignmentsByYear(true, true, callback);
			}
		}
	}

	private void reloadGraphs() {
		if (registrarYearlyChart != null) {
			String yLabel = accumulated ? "přiřazení (kumulované)" : "přiřazení";
			String xLabel = currentYear != null ? "rok" : "měsíc";
			String valueLabel = currentRegistrar != null ? currentRegistrar.getCode() : "celkově";
			String title = currentYear != null ? "Počet přiřazení URN:NBN za rok " + currentYear : "Počet přiřazení URN:NBN za celé období";
			registrarYearlyChart.setDataAndDraw(Collections.<Integer> emptyList(), currentData, accumulated, null, title, xLabel, yLabel, valueLabel);
		}
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

		service.getAssignmentsByYear(true, true, new AsyncCallback<Map<Integer, Map<String, Integer>>>() {

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

		service.getAssignmentsByYear("mzk", true, true, new AsyncCallback<Map<Integer, Integer>>() {

			@Override
			public void onSuccess(Map<Integer, Integer> result) {
				// getRegistrarYearlyChart().setDataAndDraw(result, "Počet přiřazení URN:NBN za celé období", "Rok", "přiřazených URN:NBN", "MZK");
				registrarYearlyChart.setDataAndDraw(Collections.<Integer> emptyList(), result, false, null, "Počet přiřazení URN:NBN za celé období",
						"Rok", "přiřazených URN:NBN", "MZK");
			}

			@Override
			public void onFailure(Throwable caught) {
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

	// private StackLayoutPanel getPanel() {
	// if (panel == null) {
	// panel = new StackLayoutPanel(Unit.EM);
	// }
	// return panel;
	// }

	// private VerticalPanel getContainer() {
	// if (container == null) {
	// container = new VerticalPanel();
	// container.setWidth("100%");
	// header = new VerticalPanel();
	// header.setWidth("100%");
	// headerRegistrators = new HorizontalPanel();
	// header.add(headerRegistrators);
	// }
	// return container;
	// }

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
		// for (String registrar : result) {
		// logger.info("registrar " + registrar);
		// }
		return result;
	}

	private List<Integer> toSortedYearList(Map<Integer, Map<String, Integer>> data) {
		List<Integer> result = new ArrayList<>();
		result.addAll(data.keySet());
		Collections.sort(result);
		// for (Integer year : result) {
		// logger.info("year " + year);
		// }
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
