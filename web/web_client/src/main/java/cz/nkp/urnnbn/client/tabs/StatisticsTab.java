package cz.nkp.urnnbn.client.tabs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;

import cz.nkp.urnnbn.client.charts.widgets.topLevel.AssignmentsWidget;
import cz.nkp.urnnbn.client.charts.widgets.topLevel.ResolvationsWidget;
import cz.nkp.urnnbn.client.services.GwtStatisticsService;
import cz.nkp.urnnbn.client.services.GwtStatisticsServiceAsync;
import cz.nkp.urnnbn.client.services.InstitutionsService;
import cz.nkp.urnnbn.client.services.InstitutionsServiceAsync;
import cz.nkp.urnnbn.shared.charts.Registrar;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;

public class StatisticsTab extends SingleTabContentPanel {

    private static final Logger LOGGER = Logger.getLogger(StatisticsTab.class.getSimpleName());

    private final GwtStatisticsServiceAsync statisticsService = GWT.create(GwtStatisticsService.class);
    private final InstitutionsServiceAsync institutionsService = GWT.create(InstitutionsService.class);

    // data
    private List<Integer> years;
    private Set<Registrar> registrars;
    private boolean loaded = false;

    // widgets
    private TabLayoutPanel tabPanel;
    private AssignmentsWidget assignmentsWidget;
    private ResolvationsWidget resolvationsWidget;

    public StatisticsTab(TabsPanel superPanel) {
        super(superPanel, "statistics");
        tabPanel = new TabLayoutPanel(1.5, Unit.EM);
        tabPanel.setHeight("800px");
        // tabPanel.setHeight("100%");
        add(tabPanel);
    }

    @Override
    public void onSelected() {
        // LOGGER.fine("onSelected");
        super.onSelected();
        if (!loaded) {
            load();
        }
    }

    private void load() {
        // years
        statisticsService.getAvailableYearsSorted(new AsyncCallback<List<Integer>>() {

            @Override
            public void onSuccess(List<Integer> result) {
                years = result;
                initRegistrarsAndChartLoader();
            }

            private void initRegistrarsAndChartLoader() {
                institutionsService.getAllRegistrars(new AsyncCallback<ArrayList<RegistrarDTO>>() {

                    @Override
                    public void onSuccess(ArrayList<RegistrarDTO> result) {
                        registrars = toRegistrars(result);
                        initChartLoader();
                    }

                    private Set<Registrar> toRegistrars(ArrayList<RegistrarDTO> data) {
                        Set<Registrar> result = new HashSet<>();
                        for (RegistrarDTO dto : data) {
                            Registrar registrar = new Registrar();
                            registrar.setCode(dto.getCode());
                            registrar.setName(dto.getName());
                            result.add(registrar);
                        }
                        return result;
                    }

                    private void initChartLoader() {
                        ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
                        chartLoader.loadApi(new Runnable() {

                            @Override
                            public void run() {
                                LOGGER.info("chart api loaded");
                                loaded = true;
                                // TODO: i18n
                                // assignments tab
                                assignmentsWidget = new AssignmentsWidget(years, registrars);
                                tabPanel.add(assignmentsWidget, "Přiřazení");
                                // resolvations tab
                                resolvationsWidget = new ResolvationsWidget(years, registrars);
                                tabPanel.add(resolvationsWidget, "Rezolvování");
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        LOGGER.severe("Error loading registrars: " + caught.getMessage());
                    }
                });

            }

            @Override
            public void onFailure(Throwable caught) {
                LOGGER.severe("Error loading years for statistics: " + caught.getMessage());
            }
        });

    }

    @Override
    public void onDeselected() {
        // TODO Auto-generated method stub

    }

}
