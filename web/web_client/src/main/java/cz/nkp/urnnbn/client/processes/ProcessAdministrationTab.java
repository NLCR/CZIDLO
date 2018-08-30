package cz.nkp.urnnbn.client.processes;

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

import java.util.*;
import java.util.logging.Logger;

public class ProcessAdministrationTab extends SingleTabContentPanel {

    private static final Logger LOGGER = Logger.getLogger(ProcessAdministrationTab.class.getName());

    private final ProcessAdministrationCss css = initCss();
    private final Timer processesRefreshTimer = initProcessRefreshTimer();
    private static final int TIMER_INTERVAL = 1000;
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
            // TODO: 30.8.18 handle limitToMyProcess in table widget. Now it does not work properly, because table is not being re-drawn every second 
            limitToMyProcess = false;
        }
        xmlTransformationsPanel = new XmlTransformationsPanel(this);
    }

    @Override
    public void onLoad() {
        // TODO: 30.8.18 properly handle tab hiding, i.e. disable fetching process list when switched to another tab
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
                if (processes != null) {
                    LOGGER.fine("loaded " + processes.size() + " processes");
                }

                if (processes == null || foundDifference(processes, result)) {
                    processes = result;
                    reload();
                }
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

    private boolean foundDifference(List<ProcessDTO> originalProcesses, List<ProcessDTO> newProcesses) {

        //search for added or changed processes
        Map<Long, ProcessDTO> originalProcessMap = new HashMap<>();
        for (ProcessDTO process : originalProcesses) {
            originalProcessMap.put(process.getId(), process);
        }
        for (ProcessDTO newProcess : newProcesses) {
            if (!originalProcessMap.keySet().contains(newProcess.getId())) {
                //new process
                return true;
            } else {
                //existing process
                if (foundDifference(newProcess, originalProcessMap.get(newProcess.getId()))) {
                    return true;
                }
            }
        }

        //search for removed processes
        Set<Long> newProcessIds = new HashSet<>();
        for (ProcessDTO process : newProcesses) {
            newProcessIds.add(process.getId());
        }
        for (ProcessDTO originalProcess : originalProcesses) {
            if (!newProcessIds.contains(originalProcess.getId())) {
                return true;
            }
        }

        //otherwise
        return false;
    }

    private boolean foundDifference(ProcessDTO newProcesses, ProcessDTO oldProcesses) {
        if (newProcesses.getState() != oldProcesses.getState()) {
            return true;
        }
        return false;
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
        VerticalPanel panel = new VerticalPanel();
        //limit procesess only for mine
        if (getActiveUser().isSuperAdmin()) {
            panel.add(limitListCheckBox());
        }
        // process table
        // TODO: 28.8.18 add to scrollpanel or some other way solve possible hundreds of processes hiding other stuff in the panel
        // TODO: 28.8.18 keep table state (sorting) througout table re-creation every second when new process data arrives
        panel.add(new ProcessTableWidget(processes, constants, limitToMyProcess,
                new ProcessButtonAction.Operation() {
                    @Override
                    public void run(ProcessDTO process) {
                        cancelProcess(process);
                    }
                },
                new ProcessButtonAction.Operation() {
                    @Override
                    public void run(ProcessDTO process) {
                        stopProcess(process);
                    }
                },
                new ProcessButtonAction.Operation() {
                    @Override
                    public void run(ProcessDTO process) {
                        deleteProcess(process);
                    }
                },
                new ProcessButtonAction.Operation() {
                    @Override
                    public void run(ProcessDTO process) {
                        showProcessLogFile(process);
                    }
                },
                new ProcessButtonAction.Operation() {
                    @Override
                    public void run(ProcessDTO process) {
                        downloadOutputFile(process);
                    }
                }));

        // planning processes
        panel.add(planProcessHeading());
        panel.add(planProcessPanel());
        panel.add(new HTML("<br>"));
        panel.add(xmlTransformationsPanel);
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

    private void cancelProcess(ProcessDTO process) {
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
        }
    }

    private void stopProcess(ProcessDTO process) {
        if (process.getState() == ProcessDTOState.RUNNING) {
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
    }

    private void deleteProcess(ProcessDTO process) {
        if (process.getState() == ProcessDTOState.FINISHED
                || process.getState() == ProcessDTOState.CANCELED
                || process.getState() == ProcessDTOState.FAILED
                || process.getState() == ProcessDTOState.KILLED) {
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

    private void showProcessLogFile(final ProcessDTO process) {
        if (process.getState() == ProcessDTOState.RUNNING
                || process.getState() == ProcessDTOState.CANCELED
                || process.getState() != ProcessDTOState.FINISHED
                || process.getState() != ProcessDTOState.FAILED
                || process.getState() != ProcessDTOState.KILLED
                ) {
            String url = "/processDataServer/processes/" + process.getId() + "/log";
            Window.open(url, "_blank", "enabled");
        }
    }

    private void downloadOutputFile(final ProcessDTO process) {
        if (process.getState() == ProcessDTOState.FINISHED) {
            String url = "/processDataServer/processes/" + process.getId() + "/output";
            Window.open(url, "_self", "enabled");
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
