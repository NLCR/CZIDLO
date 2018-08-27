package cz.nkp.urnnbn.client.processes;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import cz.nkp.urnnbn.client.Utils;
import cz.nkp.urnnbn.client.resources.ProcessAdministrationCss;
import cz.nkp.urnnbn.client.resources.Resources;
import cz.nkp.urnnbn.client.services.ProcessService;
import cz.nkp.urnnbn.client.services.ProcessServiceAsync;
import cz.nkp.urnnbn.client.tabs.SingleTabContentPanel;
import cz.nkp.urnnbn.client.tabs.TabsPanel;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTO;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTOState;
import cz.nkp.urnnbn.shared.exceptions.SessionExpirationException;

import java.util.List;
import java.util.logging.Logger;

public class ProcessAdministrationTab extends SingleTabContentPanel {

    private static final Logger LOGGER = Logger.getLogger(ProcessAdministrationTab.class.getName());

    private final ProcessAdministrationCss css = initCss();
    private final Timer processesRefreshTimer = initProcessRefreshTimer();
    private static final int TIMER_INTERVAL = 5000;
    private final ProcessServiceAsync processService = GWT.create(ProcessService.class);
    private List<ProcessDTO> processes;
    private final XmlTransformationsPanel xmlTransformationsPanel;
    private boolean limitToMyProcess = true;

    private ProcessAdministrationCss initCss() {
        Resources resources = GWT.create(Resources.class);
        ProcessAdministrationCss result = resources.ProcessAdministrationCss();
        result.ensureInjected();
        return result;
    }

    private Timer initProcessRefreshTimer() {
        return new Timer() {

            @Override
            public void run() {
                loadProcesses();
            }
        };
    }

    public ProcessAdministrationTab(TabsPanel superPanel) {
        super(superPanel, "processes");
        if (getActiveUser().isSuperAdmin()) {
            limitToMyProcess = false;
        }
        xmlTransformationsPanel = new XmlTransformationsPanel(this);
    }

    @Override
    public void onLoad() {
        loadProcesses();
        processesRefreshTimer.scheduleRepeating(TIMER_INTERVAL);
    }

    private boolean showProcessesOfAllUsers() {
        return getActiveUser().isSuperAdmin() && !limitToMyProcess;
    }

    private void loadProcesses() {
        AsyncCallback<List<ProcessDTO>> callback = new AsyncCallback<List<ProcessDTO>>() {

            @Override
            public void onSuccess(List<ProcessDTO> result) {
                processes = result;
                reload();
            }

            @Override
            public void onFailure(Throwable caught) {
                if (caught instanceof SessionExpirationException) {
                    Utils.sessionExpirationRedirect();
                } else {
                    LOGGER.severe("Error loading processes: " + caught.getMessage());
                }
            }
        };
        if (showProcessesOfAllUsers()) {
            processService.getAllProcesses(callback);
        } else {
            processService.getUsersProcesses(callback);
        }
    }

    private void reload() {
        // if (this.processListScrollPanel != null) {
        // System.err.println("reading position: " +
        // this.processListScrollPanel.getScrollPosition());
        // this.scrollbarPosition =
        // this.processListScrollPanel.getScrollPosition();
        // }
        clear();
        add(contentPanel());
    }

    private Panel contentPanel() {
        VerticalPanel result = new VerticalPanel();
        // process list
        // TODO: 28.8.18 cleanup unused list
        /*result.add(processListHeading());
        result.add(processListPanel());*/
        // process table
        result.add(new ProcessTableWidget(processes, constants,
                new ActionCell.Delegate<ProcessDTO>() {
                    @Override
                    public void execute(ProcessDTO processDTO) {
                        //deletOrKillProcessWidget(processDTO);
                        deletOrKillProcess(processDTO);
                    }
                },
                new ActionCell.Delegate<ProcessDTO>() {
                    @Override
                    public void execute(ProcessDTO processDTO) {
                        //showLogWidget(processDTO);
                        showLog(processDTO);
                    }
                },
                new ActionCell.Delegate<ProcessDTO>() {
                    @Override
                    public void execute(ProcessDTO processDTO) {
                        //downloadOutputWidget(processDTO);
                        downloadOutput(processDTO);
                    }
                }));
        // planning processes
        result.add(planProcessHeading());
        result.add(planProcessPanel());
        result.add(new HTML("<br>"));
        result.add(xmlTransformationsPanel);
        return result;
    }

    private Label processListHeading() {
        Label label = new Label(constants.processList());
        label.addStyleName(css.processListHeading());
        return label;
    }

    private Widget processListPanel() {
        Panel panel = new VerticalPanel();
        if (getActiveUser().isSuperAdmin()) {
            panel.add(limitListCheckBox());
        }
        panel.add(processListScrollPanelHeader());
        panel.add(processListScrollPanel());
        return panel;
    }

    private Widget limitListCheckBox() {
        CheckBox checkbox = new CheckBox(constants.processListShowMyOnlyButton());
        checkbox.setValue(limitToMyProcess);
        checkbox.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                limitToMyProcess = ((CheckBox) event.getSource()).getValue();
                loadProcesses();
            }
        });
        return checkbox;
    }

    private Widget processListScrollPanelHeader() {
        boolean showProcessesOfAllUsers = showProcessesOfAllUsers();


        HorizontalPanel panel = new HorizontalPanel();
        panel.setWidth("1000px");

        // id
        Widget idLabel = headerFormated(constants.processId());
        panel.add(idLabel);
        // panel.setCellWidth(idLabel, "40px");
        panel.setCellWidth(idLabel, "2%");

        // typ procesu
        Widget typeLabel = headerFormated(constants.processType());
        panel.add(typeLabel);
        panel.setCellWidth(typeLabel, "13%");

        if (showProcessesOfAllUsers) {
            // owner
            Widget ownerLabel = headerFormated(constants.user());
            panel.add(ownerLabel);
            // panel.setCellWidth(idLabel, "40px");
            panel.setCellWidth(ownerLabel, "3%");
        }

        // //parametry
        // Label paramLabel = new Label("parametry");
        // panel.add(paramLabel);
        // panel.setCellWidth(paramLabel, "5%");

        // stav procesu
        Widget stateLabel = headerFormated(constants.processStatus());
        panel.add(stateLabel);
        panel.setCellWidth(stateLabel, "8%");

        // cas naplanovani
        Widget scheduledLabel = headerFormated(constants.processPlanned());
        panel.add(scheduledLabel);
        panel.setCellWidth(scheduledLabel, "10%");

        // cas spusteni
        Widget startedLabel = headerFormated(constants.processStarted());
        panel.add(startedLabel);
        panel.setCellWidth(startedLabel, "10%");

        // cas ukonceni
        Widget finishedLabel = headerFormated(constants.processFinished());
        panel.add(finishedLabel);
        panel.setCellWidth(finishedLabel, "10%");

        // trvani
        //TODO: i18n
        Widget durationLabel = headerFormated("doba běhu");
        panel.add(durationLabel);
        panel.setCellWidth(durationLabel, "10%");

        // tlacitko na zastaveni procesu
        Widget stopProcess = new Label("");
        panel.add(stopProcess);
        panel.setCellWidth(stopProcess, showProcessesOfAllUsers ? "5%" : "8%");

        // stazeni logu
        Widget downloadLog = new Label("");
        panel.add(downloadLog);
        panel.setCellWidth(downloadLog, "8%");

        // stazeni vystupu
        Widget downloadResults = new Label("");
        panel.add(downloadResults);
        panel.setCellWidth(downloadResults, "10%");

        return panel;
    }

    private Widget headerFormated(String string) {
        return new HTML("<div style=\"color:grey\">" + string + "</style>");
    }

    private ScrollPanel processListScrollPanel() {
        ScrollPanel root = new ScrollPanel();
        root.setWidth("1300px");
        root.setHeight("200px");
        Panel content = new VerticalPanel();
        for (ProcessDTO process : processes) {
            content.add(processWidget(process));
        }
        // if (scrollbarPosition != null) {
        // root.setScrollPosition(scrollbarPosition);
        // }
        root.add(content);
        return root;
    }

    private Widget processWidget(ProcessDTO process) {
        boolean showProcessesOfAllUsers = showProcessesOfAllUsers();

        HorizontalPanel panel = new HorizontalPanel();
        panel.setWidth("1000px");
        // panel.setWidth("100%");
        ProcessFormater formater = new ProcessFormater(process, constants);

        // id
        Label idLabel = new Label(process.getId().toString());
        panel.add(idLabel);
        // panel.setCellWidth(idLabel, "40px");
        panel.setCellWidth(idLabel, "2%");

        // typ procesu
        Widget typeLabel = formater.getProcessType();
        panel.add(typeLabel);
        panel.setCellWidth(typeLabel, "13%");

        if (showProcessesOfAllUsers) {
            // owner
            Label ownerLabel = new Label(process.getOwnerLogin());
            panel.add(ownerLabel);
            // panel.setCellWidth(idLabel, "40px");
            panel.setCellWidth(ownerLabel, "3%");
        }

        // stav procesu
        Widget stateLabel = formater.getProcessState();
        panel.add(stateLabel);
        panel.setCellWidth(stateLabel, "8%");

        // cas naplanovani
        Label scheduledLabel = new Label(formater.getScheduled());
        panel.add(scheduledLabel);
        panel.setCellWidth(scheduledLabel, "10%");

        // cas spusteni
        Label startedLabel = new Label(formater.getStarted());
        panel.add(startedLabel);
        panel.setCellWidth(startedLabel, "10%");

        // cas ukonceni
        Label finishedLabel = new Label(formater.getFinished());
        panel.add(finishedLabel);
        panel.setCellWidth(finishedLabel, "10%");

        // trvani
        Label durationLabel = new Label(formater.getDurationFormatted());
        panel.add(durationLabel);
        panel.setCellWidth(durationLabel, "10%");

        // tlacitko na zastaveni procesu
        Widget stopProcess = deletOrKillProcessWidget(process);
        panel.add(stopProcess);
        panel.setCellWidth(stopProcess, showProcessesOfAllUsers ? "5%" : "8%");

        // stazeni logu
        Widget downloadLog = showLogWidget(process);
        // new Label("stáhnout log");
        panel.add(downloadLog);
        panel.setCellWidth(downloadLog, "8%");

        // stazeni vystupu
        Widget downloadResults = downloadOutputWidget(process);
        // new Label("stáhnout výstup");
        panel.add(downloadResults);
        panel.setCellWidth(downloadResults, "10%");

        return panel;
    }

    private Widget downloadOutputWidget(final ProcessDTO process) {
        if (process.getState() == ProcessDTOState.FINISHED) {
            return new Button(constants.processDownloadOutput(), new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    String url = "/processDataServer/processes/" + process.getId() + "/output";
                    Window.open(url, "_self", "enabled");
                }
            });
        } else {
            return new Label("");
        }
    }

    private void downloadOutput(final ProcessDTO process) {
        if (process.getState() == ProcessDTOState.FINISHED) {
            String url = "/processDataServer/processes/" + process.getId() + "/output";
            Window.open(url, "_self", "enabled");
        }
    }

    private Widget showLogWidget(final ProcessDTO process) {
        if (process.getState() != ProcessDTOState.SCHEDULED && process.getState() != ProcessDTOState.CANCELED) {
            return new Button(constants.processShowLog(), new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    String url = "/processDataServer/processes/" + process.getId() + "/log";
                    Window.open(url, "_blank", "enabled");
                }
            });
        } else {
            return new Label("");
        }
    }

    private void showLog(final ProcessDTO process) {
        if (process.getState() != ProcessDTOState.SCHEDULED && process.getState() != ProcessDTOState.CANCELED) {
            String url = "/processDataServer/processes/" + process.getId() + "/log";
            Window.open(url, "_blank", "enabled");
        }
    }

    private Widget deletOrKillProcessWidget(final ProcessDTO process) {
        if (process.getState() == ProcessDTOState.SCHEDULED) {
            return new Button(constants.processCancel(), new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    processService.cancelScheduledProcess(process.getId(), new AsyncCallback<Boolean>() {

                        @Override
                        public void onSuccess(Boolean result) {
                            // nothing, process list will be updated in at most
                            // on second anyway
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            Window.alert(messages.serverError(caught.getMessage()));
                        }
                    });
                }
            });

        } else if (process.getState() == ProcessDTOState.RUNNING) {
            return new Button(constants.processStop(), new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    processService.killRunningProcess(process.getId(), new AsyncCallback<Boolean>() {

                        @Override
                        public void onSuccess(Boolean result) {
                            // nothing, process list will be updated in at most
                            // on second anyway
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            Window.alert(messages.serverError(caught.getMessage()));
                        }
                    });

                }
            });
        } else {
            return new Button(constants.processDelete(), new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    processService.deleteFinishedProcess(process.getId(), new AsyncCallback<Void>() {

                        @Override
                        public void onSuccess(Void result) {
                            // nothing, process list will be updated in at most
                            // on second anyway
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            Window.alert(messages.serverError(caught.getMessage()));
                        }
                    });
                }
            });
        }
    }


    private void deletOrKillProcess(final ProcessDTO process) {
        if (process.getState() == ProcessDTOState.SCHEDULED) {
            processService.cancelScheduledProcess(process.getId(), new AsyncCallback<Boolean>() {

                @Override
                public void onSuccess(Boolean result) {
                    // nothing, process list will be updated in at most
                    // on second anyway
                }

                @Override
                public void onFailure(Throwable caught) {
                    Window.alert(messages.serverError(caught.getMessage()));
                }
            });
        } else if (process.getState() == ProcessDTOState.RUNNING) {
            processService.killRunningProcess(process.getId(), new AsyncCallback<Boolean>() {

                @Override
                public void onSuccess(Boolean result) {
                    // nothing, process list will be updated in at most
                    // on second anyway
                }

                @Override
                public void onFailure(Throwable caught) {
                    Window.alert(messages.serverError(caught.getMessage()));
                }
            });
        } else {
            processService.deleteFinishedProcess(process.getId(), new AsyncCallback<Void>() {

                @Override
                public void onSuccess(Void result) {
                    // nothing, process list will be updated in at most
                    // on second anyway
                }

                @Override
                public void onFailure(Throwable caught) {
                    Window.alert(messages.serverError(caught.getMessage()));
                }
            });
        }
    }

    private Label planProcessHeading() {
        Label label = new Label(constants.processPlanning());
        label.addStyleName(css.planProcessHeading());
        return label;
    }

    private Widget planProcessPanel() {
        HorizontalPanel result = new HorizontalPanel();
        result.setSpacing(10);

        // EXPORT URN:NBN LIST OF REGISTRAR
        result.add(new Button(constants.REGISTRARS_URN_NBN_CSV_EXPORT(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                new ExportUrnNbnListProcessDialogBox(getActiveUser()).open();
            }
        }));

        // OAI Adapter
        result.add(new Button(constants.OAI_ADAPTER(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                new OaiAdapterDialogBox(
                        getActiveUser(),
                        xmlTransformationsPanel.getDdRegistrationTransformations(),
                        xmlTransformationsPanel.getDiImportTransformations()
                ).open();
            }
        }));

        // admin only processes
        if (getActiveUser().isLoggedUser() && getActiveUser().isSuperAdmin()) {

            // DI availability check
            result.add(new Button(constants.DI_URL_AVAILABILITY_CHECK(), new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    new DiAvailabilityCheckDialogBox(getActiveUser()).open();
                }
            }));

            // Documents' indexation for web search
            result.add(new Button(constants.DOCS_INDEXATION(), new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    new IndexDocumentsProcessDialogBox(getActiveUser()).open();
                }
            }));

        }

        return result;
    }

    @Override
    public void onSelected() {
        // LOGGER.fine("onSelected");
        super.onSelected();
        processesRefreshTimer.scheduleRepeating(TIMER_INTERVAL);
    }

    @Override
    public void onDeselected() {
        processesRefreshTimer.cancel();
    }

}
