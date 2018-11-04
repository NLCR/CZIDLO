package cz.nkp.urnnbn.client.processes.mainPanel;

import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTO;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTOState;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Martin Řehánek on 27.8.18.
 */
public class ProcessTableWidget extends Composite {

    private final List<ProcessDTO> processes;
    private final ConstantsImpl constants;
    private final boolean limitToMyProcesses;

    private final ProcessButtonAction.Operation cancelProcessOperation;
    private final ProcessButtonAction.Operation stopProcessOperation;
    private final ProcessButtonAction.Operation deleteProcessOperation;
    private final ProcessButtonAction.Operation showLogOperation;
    private final ProcessButtonAction.Operation downloadOutputOperation;

    public ProcessTableWidget(List<ProcessDTO> processes, ConstantsImpl constants,
                              boolean limitToMyProcesses,
                              ProcessButtonAction.Operation cancelProcessOperation,
                              ProcessButtonAction.Operation stopProcessOperation,
                              ProcessButtonAction.Operation deleteProcessOperation,
                              ProcessButtonAction.Operation showLogOperation,
                              ProcessButtonAction.Operation downloadOutputOperation
    ) {
        this.processes = processes;
        this.constants = constants;
        this.limitToMyProcesses = limitToMyProcesses;
        this.cancelProcessOperation = cancelProcessOperation;
        this.stopProcessOperation = stopProcessOperation;
        this.deleteProcessOperation = deleteProcessOperation;
        this.showLogOperation = showLogOperation;
        this.downloadOutputOperation = downloadOutputOperation;
        initWidget(buildTable());
    }

    private Widget buildTable() {
        CellTable<ProcessDTO> table = new CellTable<>();
        table.setPageSize(processes.size());

        //DATA
        //id
        TextColumn<ProcessDTO> idColumn = new TextColumn<ProcessDTO>() {
            @Override
            public String getValue(ProcessDTO process) {
                return process.getId().toString();
            }
        };
        table.addColumn(idColumn, constants.processId());

        //type
        TextColumn<ProcessDTO> typeColumn = new TextColumn<ProcessDTO>() {
            @Override
            public String getValue(ProcessDTO process) {
                return process.getType().toString();
            }
        };
        table.addColumn(typeColumn, constants.processType());

        //owner
        if (!limitToMyProcesses) {
            TextColumn<ProcessDTO> ownerColumn = new TextColumn<ProcessDTO>() {
                @Override
                public String getValue(ProcessDTO process) {
                    return process.getOwnerLogin();
                }
            };
            table.addColumn(ownerColumn, constants.user());
        }

        //state
        Column<ProcessDTO, SafeHtml> stateColumn = new Column<ProcessDTO, SafeHtml>(new SafeHtmlCell()) {

            @Override
            public SafeHtml getValue(final ProcessDTO process) {
                SafeHtmlBuilder builder = new SafeHtmlBuilder();
                builder.append(new SafeHtml() {
                    @Override
                    public String asString() {
                        return new ProcessFormatter(process, constants).getProcessStateHtml();
                    }
                });
                return builder.toSafeHtml();
            }
        };
        table.addColumn(stateColumn, constants.processStatus());

        //scheduled time
        TextColumn<ProcessDTO> scheduledColumn = new TextColumn<ProcessDTO>() {
            @Override
            public String getValue(ProcessDTO process) {
                return new ProcessFormatter(process, constants).getScheduled();
            }
        };
        table.addColumn(scheduledColumn, constants.processPlanned());

        //started time
        TextColumn<ProcessDTO> startedColumn = new TextColumn<ProcessDTO>() {
            @Override
            public String getValue(ProcessDTO process) {
                return new ProcessFormatter(process, constants).getStarted();
            }
        };
        table.addColumn(startedColumn, constants.processStarted());

        //finished time
        TextColumn<ProcessDTO> finishedColumn = new TextColumn<ProcessDTO>() {
            @Override
            public String getValue(ProcessDTO process) {
                return new ProcessFormatter(process, constants).getFinished();
            }
        };
        table.addColumn(finishedColumn, constants.processFinished());

        //duration
        TextColumn<ProcessDTO> durationColumn = new TextColumn<ProcessDTO>() {
            @Override
            public String getValue(ProcessDTO process) {
                return new ProcessFormatter(process, constants).getDurationFormatted();
            }
        };
        table.addColumn(durationColumn, constants.processDuration());

        //ACTIONS
        List<HasCell<ProcessDTO, ?>> actionCells = new LinkedList<>();

        //cancel process
        actionCells.add(new ProcessButtonHasCell(new ProcessButtonAction("img/process_cancel.png", constants.processCancel(),
                cancelProcessOperation,
                ProcessDTOState.SCHEDULED)));

        //stop process
        actionCells.add(new ProcessButtonHasCell(new ProcessButtonAction("img/process_stop.png", constants.processStop(),
                stopProcessOperation,
                ProcessDTOState.RUNNING)));

        //delete process
        actionCells.add(new ProcessButtonHasCell(new ProcessButtonAction("img/process_delete.png", constants.processDelete(),
                deleteProcessOperation,
                ProcessDTOState.FINISHED, ProcessDTOState.CANCELED, ProcessDTOState.FAILED, ProcessDTOState.KILLED)));

        //show log
        actionCells.add(new ProcessButtonHasCell(new ProcessButtonAction("img/process_show_log.png", constants.processShowLog(),
                showLogOperation,
                ProcessDTOState.RUNNING, ProcessDTOState.CANCELED, ProcessDTOState.FINISHED, ProcessDTOState.FAILED, ProcessDTOState.KILLED)));

        //download output
        actionCells.add(new ProcessButtonHasCell(new ProcessButtonAction("img/process_download_output.png", constants.processDownloadOutput(),
                downloadOutputOperation,
                ProcessDTOState.FINISHED)));

        CompositeCell<ProcessDTO> actionsCell = new CompositeCell<>(actionCells);
        Column<ProcessDTO, ProcessDTO> actionsColumn = new Column<ProcessDTO, ProcessDTO>(actionsCell) {

            @Override
            public ProcessDTO getValue(ProcessDTO process) {
                return process;
            }
        };
        table.addColumn(actionsColumn);

        // Data provider.
        ListDataProvider<ProcessDTO> dataProvider = new ListDataProvider<>(processes);
        dataProvider.addDataDisplay(table);


        //SORTING
        ColumnSortEvent.ListHandler<ProcessDTO> columnSortHandler = new ColumnSortEvent.ListHandler<>(dataProvider.getList());

        //id
        idColumn.setSortable(true);
        columnSortHandler.setComparator(idColumn, new Comparator<ProcessDTO>() {

            public int compare(ProcessDTO o1, ProcessDTO o2) {
                return o1.getId().compareTo(o2.getId());
            }
        });

        //type
        typeColumn.setSortable(true);
        columnSortHandler.setComparator(typeColumn, new Comparator<ProcessDTO>() {

            public int compare(ProcessDTO o1, ProcessDTO o2) {
                return o1.getType().compareTo(o2.getType());
            }
        });

        //state
        stateColumn.setSortable(true);
        columnSortHandler.setComparator(stateColumn, new Comparator<ProcessDTO>() {

            public int compare(ProcessDTO o1, ProcessDTO o2) {
                return o1.getState().compareTo(o2.getState());
            }
        });

        //scheduled time
        scheduledColumn.setSortable(true);
        columnSortHandler.setComparator(scheduledColumn, new Comparator<ProcessDTO>() {

            public int compare(ProcessDTO o1, ProcessDTO o2) {
                if (o1.getScheduled() != null && o2.getScheduled() != null) {
                    return o1.getScheduled().compareTo(o2.getScheduled());
                } else if (o1.getScheduled() == null && o2.getScheduled() == null) {
                    return 0;
                } else if (o1.getScheduled() == null) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        //started time
        startedColumn.setSortable(true);
        columnSortHandler.setComparator(startedColumn, new Comparator<ProcessDTO>() {

            public int compare(ProcessDTO o1, ProcessDTO o2) {
                if (o1.getStarted() != null && o2.getStarted() != null) {
                    return o1.getStarted().compareTo(o2.getStarted());
                } else if (o1.getStarted() == null && o2.getStarted() == null) {
                    return 0;
                } else if (o1.getStarted() == null) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        //finished time
        finishedColumn.setSortable(true);
        columnSortHandler.setComparator(finishedColumn, new Comparator<ProcessDTO>() {

            public int compare(ProcessDTO o1, ProcessDTO o2) {
                if (o1.getFinished() != null && o2.getFinished() != null) {
                    return o1.getFinished().compareTo(o2.getFinished());
                } else if (o1.getFinished() == null && o2.getFinished() == null) {
                    return 0;
                } else if (o1.getFinished() == null) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        //duration
        durationColumn.setSortable(true);
        columnSortHandler.setComparator(durationColumn, new Comparator<ProcessDTO>() {

            public int compare(ProcessDTO o1, ProcessDTO o2) {
                Long first = new ProcessFormatter(o1, constants).getDurationMillis();
                Long second = new ProcessFormatter(o2, constants).getDurationMillis();
                if (first != null && second != null) {
                    return first.compareTo(second);
                } else if (first == null && second == null) {
                    return 0;
                } else if (first != null) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });


        /*codeColumn.setSortable(true);
        columnSortHandler.setComparator(codeColumn, new Comparator<RegistrarDTO>() {
            public int compare(RegistrarDTO first, RegistrarDTO second) {
                return first.getCode().compareTo(second.getCode());
            }
        });*/
        table.addColumnSortHandler(columnSortHandler);
        // By default sorted by name
        table.getColumnSortList().push(idColumn);
        ColumnSortEvent.fire(table, table.getColumnSortList());

        return table;

    }


}
