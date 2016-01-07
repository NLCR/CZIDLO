package cz.nkp.urnnbn.client;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import cz.nkp.urnnbn.client.IntegerKeyColumnChart.IntegerSelectionHandler;
import cz.nkp.urnnbn.shared.Registrar;

public class RegistrarAssignmentsWidget extends AbstractStatisticsWidget {

	private static final Logger logger = Logger.getLogger(RegistrarAssignmentsWidget.class.getSimpleName());

	// fixed data
	private final List<Integer> years;
	private final List<Integer> months = initMonths();

	// data
	private Registrar currentRegistrar;
	private Map<Integer, Integer> currentData;
	private Integer currentYear = null;

	// widgets
	private final Label title = new Label();
	private final ListBox timePeriods;
	private final RadioButton stateAll;
	private final RadioButton stateActiveOnly;
	private final RadioButton stateDeactivatedOnly;
	private final CheckBox accumulated;
	private final IntegerKeyColumnChart chart;

	public RegistrarAssignmentsWidget(List<Integer> years) {
		this.years = years;

		// container
		VerticalPanel container = new VerticalPanel();
		container.setSpacing(5);
		container.setWidth("100%");
		// container.setWidth("800px");
		RootLayoutPanel.get().add(container);

		// header
		VerticalPanel header = new VerticalPanel();
		header.setSpacing(10);
		// header.setWidth("100%");
		header.setWidth("1000px");
		container.add(header);

		// title
		header.add(title);

		// year filter
		timePeriods = createTimePeriods();
		HorizontalPanel headerYears = new HorizontalPanel();
		headerYears.add(timePeriods);
		header.add(headerYears);

		// urn state filter
		// TODO: i18n
		stateAll = createUrnStateRadibutton("all", true);
		stateActiveOnly = createUrnStateRadibutton("active only", false);
		stateDeactivatedOnly = createUrnStateRadibutton("decativated only", false);
		HorizontalPanel urnStateFilterPanel = new HorizontalPanel();
		urnStateFilterPanel.add(stateAll);
		urnStateFilterPanel.add(stateActiveOnly);
		urnStateFilterPanel.add(stateDeactivatedOnly);
		header.add(urnStateFilterPanel);

		// accumulated filter
		accumulated = createAccumulatedCheckbox();
		header.add(accumulated);

		// chart
		chart = createChart();
		container.add(chart.getWidget());

		initWidget(container);
		setStyleName("RegistrarAssignmentsGraph");
	}

	private ListBox createTimePeriods() {
		ListBox result = new ListBox();
		result.addItem("celé období");
		for (Integer year : years) {
			result.addItem(year.toString());
		}
		result.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {

				int index = timePeriods.getSelectedIndex();
				if (index == 0 || years.isEmpty()) {
					currentYear = null;
				} else {
					currentYear = years.get(index - 1);
				}
				loadData(currentRegistrar, currentYear);
			}
		});
		return result;
	}

	private RadioButton createUrnStateRadibutton(String title, boolean selected) {
		// TODO: filtrovat instance
		RadioButton result = new RadioButton("registrar-urn-state", title);
		result.setValue(selected);
		result.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if (event.getValue()) {
					loadData(currentRegistrar, currentYear);
				}

			}
		});
		return result;
	}

	private CheckBox createAccumulatedCheckbox() {
		CheckBox result = new CheckBox("kumulované");
		result.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				redrawChart();
			}
		});
		return result;
	}

	private IntegerKeyColumnChart createChart() {
		IntegerKeyColumnChart result = new IntegerKeyColumnChart();
		result.setHandler(new IntegerSelectionHandler() {

			@Override
			public void onSelected(Integer key) {
				if (key > 12) { // year
					for (int position = 0; position < years.size(); position++) {
						int year = years.get(position);
						if (year == key) {
							currentYear = key;
							timePeriods.setSelectedIndex(position + 1);
							break;
						}
					}
					loadData(currentRegistrar, currentYear);
				}
			}
		});
		return result;
	}

	private void loadData(final Registrar registrar, final Integer year) {
		AsyncCallback<Map<Integer, Integer>> callback = new AsyncCallback<Map<Integer, Integer>>() {

			@Override
			public void onSuccess(Map<Integer, Integer> result) {
				currentRegistrar = registrar;
				currentYear = year;
				currentData = result;
				redrawChart();
			}

			@Override
			public void onFailure(Throwable caught) {
				logger.severe(caught.getMessage());
			}
		};
		boolean includeActive = stateAll.getValue() || stateActiveOnly.getValue();
		boolean includeDeactivated = stateAll.getValue() || stateDeactivatedOnly.getValue();

		if (registrar != null) {
			if (year != null) {
				service.getAssignmentsByMonth(registrar.getCode(), year, includeActive, includeDeactivated, callback);
			} else {
				service.getAssignmentsByYear(registrar.getCode(), includeActive, includeDeactivated, callback);
			}
		} else {
			if (year != null) {
				service.getTotalAssignmentsByMonth(year, includeActive, includeDeactivated, callback);
			} else {
				service.getTotalAssignmentsByYear(includeActive, includeDeactivated, callback);
			}
		}
	}

	private void redrawChart() {
		if (chart != null) {
			List<Integer> keys = currentYear != null ? months : years;
			// TODO: i18n
			String title = currentYear != null ? "Počet přiřazení URN:NBN za rok " + currentYear : "Počet přiřazení URN:NBN za celé období";
			String valueDesc = currentRegistrar != null ? currentRegistrar.getCode() : "celkově";
			String xLabel = currentYear != null ? "rok" : "měsíc";
			String yLabel = accumulated.getValue() ? "přiřazení (kumulované)" : "přiřazení";
			Map<Integer, String> columnDesc = currentYear == null ? null : getMonthLabels();
			chart.setDataAndDraw(keys, currentData, accumulated.getValue(), title, valueDesc, xLabel, yLabel, columnDesc);
		}
	}

	public void setRegistrar(Registrar registrar) {
		String registrarName = registrar != null ? registrar.getName() : null;
		title.setText(registrarName);
		currentRegistrar = registrar;
		loadData(currentRegistrar, currentYear);
	}

}
