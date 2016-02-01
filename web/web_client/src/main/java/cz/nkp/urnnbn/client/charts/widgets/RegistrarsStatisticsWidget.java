package cz.nkp.urnnbn.client.charts.widgets;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import cz.nkp.urnnbn.client.charts.widgets.topLevel.ColorConstants;
import cz.nkp.urnnbn.shared.charts.Registrar;
import cz.nkp.urnnbn.shared.charts.Statistic;
import cz.nkp.urnnbn.shared.charts.Statistic.Option;
import cz.nkp.urnnbn.shared.charts.Statistic.Type;

public class RegistrarsStatisticsWidget extends WidgetWithStatisticsService {

    private static final Logger LOGGER = Logger.getLogger(RegistrarsStatisticsWidget.class.getSimpleName());
    public static final int WIDGET_SEPARATOR_SIZE = 10;
    public static final int LEFT_PANEL_WIDTH = 1000;
    public static final int LEFT_PANEL_WIDGET_HEIGHT = 300;
    public static final int RIGHT_PANEL_WIDTH = 500;
    public static final int RIGHT_PANEL_WIDGET_HEIGHT = 2 * LEFT_PANEL_WIDGET_HEIGHT + WIDGET_SEPARATOR_SIZE;

    // fixed data
    private final List<Integer> years;
    private final List<Integer> months = initMonths();
    private final Map<String, String> registrarNames;
    private final Type statisticType;
    private final String[] graphValueColors;
    private final String graphValueColorOther;
    private final String graphValueColorAll;
    private final RegistrarColorMapChangeListener registrarColorMapChangeListener;

    // data
    private Map<Integer, Map<Integer, Map<String, Integer>>> data; // year -> month -> registrar_code -> statistics
    private Integer selectedYear = null;
    private Map<String, String> registrarColorMap;

    // widgets
    private final Label titleLabel;
    private final ListBox timePeriods;
    private final RadioButton stateAll;
    private final RadioButton stateActiveOnly;
    private final RadioButton stateDeactivatedOnly;
    private final SingleItemColumnChart columnChart;
    private final TopNRegistrarsPieChart pieChart;
    private final TopNRegistrarsAccumulatedAreaChart areaChart;

    public RegistrarsStatisticsWidget(List<Integer> years, Set<Registrar> registrars, Type statisticType, String[] graphValueColors,
            String graphValueColorOther, String graphValueColorAll, RegistrarSelectionHandler registrarSelectionHandler,
            RegistrarColorMapChangeListener registrarColorMapChangeListener) {
        this.years = years;
        this.registrarNames = extractRegistrarNames(registrars);
        this.statisticType = statisticType;
        this.graphValueColors = graphValueColors;
        this.graphValueColorOther = graphValueColorOther;
        this.graphValueColorAll = graphValueColorAll;
        this.registrarColorMapChangeListener = registrarColorMapChangeListener;

        // container
        VerticalPanel container = new VerticalPanel();
        container.getElement().getStyle().setPadding(WIDGET_SEPARATOR_SIZE, Unit.PX);
        RootLayoutPanel.get().add(container);

        // title
        titleLabel = new Label(buildTitle());
        titleLabel.setStyleName("czidloChartTitle");
        container.add(titleLabel);

        // header
        VerticalPanel header = new VerticalPanel();
        header.setSpacing(10);// TODO: should be in css
        header.getElement().getStyle().setMarginTop(WIDGET_SEPARATOR_SIZE, Unit.PX);
        header.setStyleName("czidloChartHeader");
        container.add(header);

        // year filter
        timePeriods = createTimePeriods();
        HorizontalPanel headerYears = new HorizontalPanel();
        headerYears.add(timePeriods);
        header.add(headerYears);

        // urn state filter
        if (statisticType == Type.URN_NBN_ASSIGNMENTS) {
            stateAll = createUrnStateRadibutton(constants.chartsStateAll(), true);
            stateActiveOnly = createUrnStateRadibutton(constants.chartsStateActiveOnly(), false);
            stateDeactivatedOnly = createUrnStateRadibutton(constants.chartsStateDeactivatedOnly(), false);
            HorizontalPanel urnStateFilterPanel = new HorizontalPanel();
            urnStateFilterPanel.add(stateAll);
            urnStateFilterPanel.add(stateActiveOnly);
            urnStateFilterPanel.add(stateDeactivatedOnly);
            header.add(urnStateFilterPanel);
        } else {
            stateAll = null;
            stateActiveOnly = null;
            stateDeactivatedOnly = null;
        }

        // content
        HorizontalPanel contentPanel = new HorizontalPanel();
        contentPanel.getElement().getStyle().setMarginTop(WIDGET_SEPARATOR_SIZE, Unit.PX);
        container.add(contentPanel);

        // left panel
        VerticalPanel leftPanel = new VerticalPanel();
        contentPanel.add(leftPanel);

        // assignments per period chart
        columnChart = new SingleItemColumnChart(LEFT_PANEL_WIDTH, LEFT_PANEL_WIDGET_HEIGHT);
        columnChart.setYearSelectionHandler(createYearSelectionHandler());
        leftPanel.add(columnChart);

        // registrar accumulated volume area chart
        areaChart = new TopNRegistrarsAccumulatedAreaChart(LEFT_PANEL_WIDTH, LEFT_PANEL_WIDGET_HEIGHT);
        areaChart.getElement().getStyle().setMarginTop(WIDGET_SEPARATOR_SIZE, Unit.PX);
        areaChart.setRegistrarSelectionHandler(registrarSelectionHandler);
        leftPanel.add(areaChart);

        // right panel
        // registrar ratio chart
        pieChart = new TopNRegistrarsPieChart(RIGHT_PANEL_WIDTH, RIGHT_PANEL_WIDGET_HEIGHT);
        pieChart.getElement().getStyle().setMarginLeft(WIDGET_SEPARATOR_SIZE, Unit.PX);
        pieChart.setRegistrarSelectionHandler(registrarSelectionHandler);
        contentPanel.add(pieChart);

        initWidget(container);
        setStyleName("czidloChartRegistrars");
        container.getElement().getStyle().setBackgroundColor(getBackgroundColor());
        loadData(selectedYear);
    }

    private String getBackgroundColor() {
        switch (statisticType) {
        case URN_NBN_ASSIGNMENTS:
            return ColorConstants.ASSIGNMENTS_BACKGROUND;
        case URN_NBN_RESOLVATIONS:
            return ColorConstants.RESOLVATIONS_BACKGROUND;
        default:
            return "#ffffff";
        }
    }

    private String buildTitle() {
        // TODO: i18n
        switch (statisticType) {
        case URN_NBN_ASSIGNMENTS:
            return "Souhrnné Statistiky přiřazení URN:NBN";
        case URN_NBN_RESOLVATIONS:
            return "Souhrnné Statistiky rezolvování URN:NBN";
        default:
            return "";
        }
    }

    private Map<String, String> extractRegistrarNames(Set<Registrar> registrars) {
        Map<String, String> result = new HashMap<>();
        if (registrars != null) {
            for (Registrar registrar : registrars) {
                result.put(registrar.getCode(), registrar.getName());
            }
        }
        return result;
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
                    selectedYear = null;
                } else {
                    selectedYear = years.get(index - 1);
                }
                loadData(selectedYear);
            }
        });
        return result;
    }

    private RadioButton createUrnStateRadibutton(String title, boolean selected) {
        RadioButton result = new RadioButton("registrars-urn-state", title);
        result.setValue(selected);
        result.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                if (event.getValue()) {
                    loadData(selectedYear);
                }
            }
        });
        return result;
    }

    private IntegerSelectionHandler createYearSelectionHandler() {
        return new IntegerSelectionHandler() {

            @Override
            public void onSelected(Integer key) {
                if (key == null || key <= 12) { // not year
                    selectedYear = null;
                    timePeriods.setSelectedIndex(0);
                } else {
                    for (int position = 0; position < years.size(); position++) {
                        int year = years.get(position);
                        if (year == key) {
                            selectedYear = key;
                            timePeriods.setSelectedIndex(position + 1);
                            break;
                        }
                    }
                }
                loadData(selectedYear);
            }
        };
    }

    private void loadData(final Integer year) {
        statisticService.getStatistics(statisticType, buildOptions(), new AsyncCallback<Map<String, Map<Integer, Map<Integer, Integer>>>>() {
            @Override
            public void onSuccess(Map<String, Map<Integer, Map<Integer, Integer>>> result) {
                selectedYear = year;
                data = transform(result);
                redrawCharts();
            }

            private Map<Integer, Map<Integer, Map<String, Integer>>> transform(Map<String, Map<Integer, Map<Integer, Integer>>> input) {
                Map<Integer, Map<Integer, Map<String, Integer>>> result = new HashMap<>();
                for (Integer year : years) {
                    Map<Integer, Map<String, Integer>> anualData = new HashMap<>();
                    for (Integer month : months) {
                        Map<String, Integer> monthData = new HashMap<>();
                        for (String registrarCode : input.keySet()) {
                            Integer statistics = input.get(registrarCode).get(year).get(month);
                            monthData.put(registrarCode, statistics);
                        }
                        anualData.put(month, monthData);
                    }
                    result.put(year, anualData);
                }
                return result;
            }

            @Override
            public void onFailure(Throwable caught) {
                LOGGER.severe(caught.getMessage());
            }
        });
    }

    private HashMap<Option, Serializable> buildOptions() {
        HashMap<Statistic.Option, Serializable> options = new HashMap<>();
        switch (statisticType) {
        case URN_NBN_ASSIGNMENTS:
            Boolean includeActive = stateAll.getValue() || stateActiveOnly.getValue();
            Boolean includeDeactivated = stateAll.getValue() || stateDeactivatedOnly.getValue();
            options.put(Statistic.Option.URN_NBN_ASSIGNMENTS_INCLUDE_ACTIVE, includeActive);
            options.put(Statistic.Option.URN_NBN_ASSIGNMENTS_INCLUDE_DEACTIVATED, includeDeactivated);
            break;
        case URN_NBN_RESOLVATIONS:
            // TODO
            break;
        }
        return options;
    }

    private void redrawCharts() {
        if (data != null) {
            Set<String> registrarCodes = extractRegistrarCodes(data);
            Map<Integer, Map<String, Integer>> currentData = selectedYear != null ? data.get(selectedYear) : aggregateYearlyData(registrarCodes);
            List<Integer> periods = selectedYear != null ? months : years;
            int totalVolume = selectedYear == null ? sumAllStatistics() : sumStatistics(selectedYear);
            Map<String, Integer> volumeByRegistrar = computeStatisticsByRegistrar(currentData, registrarCodes);
            this.registrarColorMap = buildRegistarColorMap(volumeByRegistrar);
            if (columnChart != null) {
                Map<Integer, Integer> aggregatedData = agregate(periods, currentData);
                // TODO: i18n
                String title = buildColumnChartTitle();
                String valueLabel = "Celkem";
                String xAxisLabel = selectedYear != null ? "měsíc v roce " + selectedYear : "rok";
                String yAxisLabel = buildColumnChartYAxisLabel();
                Map<Integer, String> columnLabels = selectedYear == null ? null : getMonthLabels();
                columnChart.setDataAndDraw(periods, aggregatedData, title, valueLabel, xAxisLabel, yAxisLabel, columnLabels, graphValueColorAll);
            }
            if (pieChart != null) {
                String title = buildiPieChartTitle();
                pieChart.setDataAndDraw(totalVolume, volumeByRegistrar, title, registrarNames, graphValueColorOther, registrarColorMap);
            }
            if (areaChart != null) {
                Map<String, Integer> volumesBeforeFistPeriod = selectedYear != null ? aggregateYearlyData(registrarCodes).get(selectedYear - 1)
                        : null;
                // TODO: i18n
                String title = buildAreaChartTitle();
                String xAxisLabel = selectedYear != null ? "měsíc v roce " + selectedYear : "rok";
                String yAxisLabel = buildAreChartYAxisLabel();
                Map<Integer, String> columnLabels = selectedYear == null ? null : getMonthLabels();
                areaChart.setDataAndDraw(periods, registrarNames, volumesBeforeFistPeriod, currentData, title, xAxisLabel, yAxisLabel, columnLabels,
                        graphValueColorOther, registrarColorMap);
            }
            if (registrarColorMapChangeListener != null) {
                registrarColorMapChangeListener.onChanged(registrarColorMap);
            }
        }
    }

    private Map<String, String> buildRegistarColorMap(Map<String, Integer> volumeByRegistrar) {
        Map<String, String> result = new HashMap<>();
        if (graphValueColors != null) {
            List<RegistrarWithStatistic> list = new ArrayList<>();
            for (String code : volumeByRegistrar.keySet()) {
                list.add(new RegistrarWithStatistic(code, volumeByRegistrar.get(code)));
            }

            Collections.sort(list);
            for (int i = 0; i < list.size(); i++) {
                if (i < graphValueColors.length) {
                    String code = list.get(i).getCode();
                    String color = graphValueColors[i];
                    result.put(code, color);
                }
            }
        }
        return result;
    }

    private String buildAreChartYAxisLabel() {
        // TODO: i18n
        switch (statisticType) {
        case URN_NBN_ASSIGNMENTS:
            return "Počet přiřazených URN:NBN";
        case URN_NBN_RESOLVATIONS:
            return "Agregovaný počet rezolvování";
        default:
            return "";
        }
    }

    private String buildAreaChartTitle() {
        // TODO: i18n
        String title = "";
        switch (statisticType) {
        case URN_NBN_ASSIGNMENTS:
            title = selectedYear != null ? "Měsíční vývoj počtu přiřazených URN:NBN v roce " + selectedYear : "Roční vývoj počtu přiřazených URN:NBN";
            if (stateActiveOnly.getValue()) {
                title += " (" + constants.chartsStateActiveOnly() + ")";
            } else if (stateDeactivatedOnly.getValue()) {
                title += " (" + constants.chartsStateDeactivatedOnly() + ")";
            }
            break;
        case URN_NBN_RESOLVATIONS:
            title = selectedYear != null ? "Měsíční vývoj agregovaného počtu rezolvování URN:NBN v roce " + selectedYear
                    : "Roční vývoj agregovaného počtu rezolvování URN:NBN";
            break;
        }
        return title;
    }

    private String buildColumnChartYAxisLabel() {
        // TODO: i18n
        switch (statisticType) {
        case URN_NBN_ASSIGNMENTS:
            return "Nových přiřazení";
        case URN_NBN_RESOLVATIONS:
            return "Nových rezolvování";
        default:
            return "";
        }
    }

    private String buildColumnChartTitle() {
        // TODO: i18n
        String title = "";
        switch (statisticType) {
        case URN_NBN_ASSIGNMENTS:
            title = selectedYear != null ? "Počet přiřazení URN:NBN v roce " + selectedYear : "Počet přiřazení URN:NBN přes jednotlivé roky";
            if (stateActiveOnly.getValue()) {
                title += " (" + constants.chartsStateActiveOnly() + ")";
            } else if (stateDeactivatedOnly.getValue()) {
                title += " (" + constants.chartsStateDeactivatedOnly() + ")";
            }
            break;
        case URN_NBN_RESOLVATIONS:
            title = selectedYear != null ? "Počet rezolvování URN:NBN v roce " + selectedYear : "Počet rezolvování URN:NBN přes jednotlivé roky";
            break;
        }
        return title;
    }

    private String buildiPieChartTitle() {
        // TODO: i18n
        String title = "";
        switch (statisticType) {
        case URN_NBN_ASSIGNMENTS:
            title = selectedYear != null ? "Podíl registrátorů na objemu přiřazených URN:NBN v roce " + selectedYear
                    : "Celkový podíl registrátorů na objemu přiřazených URN:NBN";
            if (stateActiveOnly.getValue()) {
                title += " (" + constants.chartsStateActiveOnly() + ")";
            } else if (stateDeactivatedOnly.getValue()) {
                title += " (" + constants.chartsStateDeactivatedOnly() + ")";
            }
            break;
        case URN_NBN_RESOLVATIONS:
            title = selectedYear != null ? "Podíl registrátorů na počtu rezolvovaných URN:NBN v roce " + selectedYear
                    : "Celkový podíl registrátorů na počtu rezolvovaných URN:NBN";
            break;
        }
        return title;
    }

    private Map<String, Integer> computeStatisticsByRegistrar(Map<Integer, Map<String, Integer>> currentData, Set<String> registrarCodes) {
        Map<String, Integer> result = new HashMap<>();
        for (String registrarCode : registrarCodes) {
            int sum = 0;
            for (Map<String, Integer> value : currentData.values()) {
                sum += value.get(registrarCode);
            }
            result.put(registrarCode, sum);
        }
        return result;
    }

    private int sumAllStatistics() {
        int sum = 0;
        for (Map<Integer, Map<String, Integer>> monthData : data.values()) {
            for (Map<String, Integer> registrarsMonthData : monthData.values()) {
                for (Integer registrarMonthValue : registrarsMonthData.values()) {
                    sum += registrarMonthValue;
                }
            }
        }
        return sum;
    }

    private int sumStatistics(int year) {
        int sum = 0;
        Map<Integer, Map<String, Integer>> monthData = data.get(year);
        for (Map<String, Integer> registrarsMonthData : monthData.values()) {
            for (Integer registrarMonthValue : registrarsMonthData.values()) {
                sum += registrarMonthValue;
            }
        }
        return sum;
    }

    private Map<Integer, Map<String, Integer>> aggregateYearlyData(Set<String> registrarCodes) {
        Map<Integer, Map<String, Integer>> result = new HashMap<>();
        for (Integer year : years) {
            Map<Integer, Map<String, Integer>> anualData = data.get(year);
            result.put(year, sumOverMonths(anualData, registrarCodes));
        }
        return result;
    }

    // month -> reg_code -> volume => reg_code -> total_volume_over_all_months
    private Map<String, Integer> sumOverMonths(Map<Integer, Map<String, Integer>> anualData, Set<String> registrarCodes) {
        Map<String, Integer> result = new HashMap<>();
        for (String registrarCode : registrarCodes) {
            int sum = 0;
            for (Map<String, Integer> monthData : anualData.values()) {
                sum += monthData.get(registrarCode);
            }
            result.put(registrarCode, sum);
        }
        return result;
    }

    private Set<String> extractRegistrarCodes(Map<Integer, Map<Integer, Map<String, Integer>>> dataByYears) {
        Set<String> result = new HashSet<>();
        for (Map<Integer, Map<String, Integer>> dataByMonths : dataByYears.values()) {
            for (Map<String, Integer> dataByRegistrar : dataByMonths.values()) {
                for (String registrarCode : dataByRegistrar.keySet()) {
                    result.add(registrarCode);
                }
            }
        }
        return result;
    }

    private Map<Integer, Integer> agregate(List<Integer> periods, Map<Integer, Map<String, Integer>> input) {
        Map<Integer, Integer> result = new HashMap<>();
        for (Integer period : periods) {
            Integer sum = 0;
            if (input != null) {
                Map<String, Integer> registrars = input.get(period);
                if (registrars != null) {
                    for (Integer perRegistrar : registrars.values()) {
                        sum += perRegistrar;
                    }
                }
            }
            result.put(period, sum);
        }
        return result;
    }

}
