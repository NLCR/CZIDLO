package cz.nkp.urnnbn.client.processes.mainPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import cz.nkp.urnnbn.client.Utils;
import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.i18n.MessagesImpl;
import cz.nkp.urnnbn.client.processes.*;
import cz.nkp.urnnbn.client.resources.ProcessAdministrationCss;
import cz.nkp.urnnbn.client.resources.Resources;
import cz.nkp.urnnbn.client.services.ProcessService;
import cz.nkp.urnnbn.client.services.ProcessServiceAsync;
import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTO;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTOState;
import cz.nkp.urnnbn.shared.exceptions.SessionExpirationException;

import java.util.*;
import java.util.logging.Logger;

public class ProcessAdminMainPanel extends VerticalPanel {

    private static final Logger LOGGER = Logger.getLogger(ProcessAdminMainPanel.class.getName());

    private final ConstantsImpl constants = GWT.create(ConstantsImpl.class);
    private final MessagesImpl messages = GWT.create(MessagesImpl.class);
    private final ProcessAdministrationCss css = initCss();
    private final ProcessServiceAsync processService = GWT.create(ProcessService.class);

    private final ProcessAdministrationTab superPanel;
    private final UserDTO user;

    private final Timer processesRefreshTimer = initProcessRefreshTimer();
    private static final int TIMER_INTERVAL = 500;

    private List<ProcessDTO> processes;
    private boolean limitToMyProcess = true;


    public ProcessAdminMainPanel(ProcessAdministrationTab superPanel, UserDTO user) {
        this.superPanel = superPanel;
        this.user = user;
        if (user.isSuperAdmin()) {
            limitToMyProcess = false;
        }
    }

    public void onSelected() {
        LOGGER.finer("onSelected");
        loadProcesses(false);
        processesRefreshTimer.scheduleRepeating(TIMER_INTERVAL);
    }


    public void onDeselected() {
        LOGGER.finer("onDeselected");
        processesRefreshTimer.cancel();
    }


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
                loadProcesses(false);
            }
        };
    }

    private void loadProcesses(final boolean forceReload) {
        AsyncCallback<List<ProcessDTO>> callback = new AsyncCallback<List<ProcessDTO>>() {

            @Override
            public void onSuccess(List<ProcessDTO> result) {
                if (processes != null) {
                    LOGGER.fine("loaded " + processes.size() + " processes");
                }

                if (forceReload || processes == null || foundDifference(processes, result)) {
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

    private boolean showProcessesOfAllUsers() {
        return user.isSuperAdmin() && !limitToMyProcess;
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
        clear();
        add(contentPanel());
    }

    private Panel contentPanel() {
        VerticalPanel panel = new VerticalPanel();
        //EXISTING PROCESSES DEFINITIONS
        panel.add(new HTML("<br>"));
        panel.add(processDefinitionsHeader());
        panel.add(processDefinitionsPanel());

        panel.add(new HTML("<br>"));

        //EXISTING INSTANCES OF PROCESSES
        panel.add(new HTML("<br>"));
        panel.add(processInstancesHeader());
        panel.add(processInstancesPanel());
        return panel;
    }

    private Label processDefinitionsHeader() {
        Label label = new Label(constants.processDefinitions());
        label.addStyleName(css.processDefinitionsHeading());
        return label;
    }

    private Label processInstancesHeader() {
        Label label = new Label(constants.processInstances());
        label.addStyleName(css.processInstancesHeading());
        return label;
    }

    private IsWidget processInstancesPanel() {
        VerticalPanel panel = new VerticalPanel();
        //limit to only those scheduled by me
        if (user.isSuperAdmin()) {
            panel.add(limitListCheckBox());
        }
        // process table
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

        return panel;
    }


    private IsWidget processDefinitionsPanel() {
        Grid result = new Grid(4, 5);
        int row = 0;
        addProcessDefinition(result, row++, constants.OAI_ADAPTER(), new ProcessAdministrationTab.Operation() {
                    @Override
                    public void run() {
                        new OaiAdapterDialogBox(
                                user,
                                //TODO: rozmyslet, kam tahle data patri
                                null, null

                                /*xmlTransformationsPanel.getDdRegistrationTransformations(),
                                xmlTransformationsPanel.getDiImportTransformations()*/
                        ).open();
                    }
                }, new ProcessAdministrationTab.Operation() {
                    @Override
                    public void run() {
                        superPanel.selectOaiAdapterConfigPanel();
                    }
                }
        );
        addProcessDefinition(result, row++, constants.REGISTRARS_URN_NBN_CSV_EXPORT(), new ProcessAdministrationTab.Operation() {
            @Override
            public void run() {
                new ExportUrnNbnListProcessDialogBox(user).open();
            }
        }, null);
        addProcessDefinition(result, row++, constants.DI_URL_AVAILABILITY_CHECK(), new ProcessAdministrationTab.Operation() {
            @Override
            public void run() {
                new DiAvailabilityCheckDialogBox(user).open();
            }
        }, null);
        addProcessDefinition(result, row++, constants.DOCS_INDEXATION(), new ProcessAdministrationTab.Operation() {
            @Override
            public void run() {
                new IndexDocumentsProcessDialogBox(user).open();
            }
        }, null);
        return result;
    }

    private void addProcessDefinition(Grid grid, int row, String processName, final ProcessAdministrationTab.Operation scheduleAction, final ProcessAdministrationTab.Operation configAction) {
        //TODO: i18n
        Button scheduleBtn = new Button("Plánovat proces", new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                scheduleAction.run();
            }
        });
        scheduleBtn.setEnabled(scheduleAction != null);
        Button configBtn = new Button("Nastavení", new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                configAction.run();
            }
        });
        configBtn.setEnabled(configAction != null);

        grid.setWidget(row, 0, new Label(processName));
        grid.setWidget(row, 2, scheduleBtn);
        grid.setWidget(row, 3, configBtn);
    }

    private Widget limitListCheckBox() {
        CheckBox checkbox = new CheckBox(constants.processListShowMyOnlyButton());
        checkbox.setValue(limitToMyProcess);
        checkbox.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                limitToMyProcess = ((CheckBox) event.getSource()).getValue();
                loadProcesses(true);
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

}
