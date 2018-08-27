package cz.nkp.urnnbn.client.processes;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTO;

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
    private final ActionCell.Delegate<ProcessDTO> deleteProcessAction;
    private final ActionCell.Delegate<ProcessDTO> showLogAction;
    private final ActionCell.Delegate<ProcessDTO> downloadOutputAction;


    public ProcessTableWidget(List<ProcessDTO> processes, ConstantsImpl constants,
                              boolean limitToMyProcesses, ActionCell.Delegate<ProcessDTO> deleteProcessAction,
                              ActionCell.Delegate<ProcessDTO> showLogAction,
                              ActionCell.Delegate<ProcessDTO> downloadOutputAction) {
        this.processes = processes;
        this.constants = constants;
        this.limitToMyProcesses = limitToMyProcesses;
        this.deleteProcessAction = deleteProcessAction;
        this.showLogAction = showLogAction;
        this.downloadOutputAction = downloadOutputAction;
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

        if (!limitToMyProcesses) {
            //owner
            TextColumn<ProcessDTO> ownerColumn = new TextColumn<ProcessDTO>() {
                @Override
                public String getValue(ProcessDTO process) {
                    return process.getOwnerLogin();
                }
            };
            table.addColumn(ownerColumn, constants.user());
        }

        //state
        TextColumn<ProcessDTO> stateColumn = new TextColumn<ProcessDTO>() {
            @Override
            public String getValue(ProcessDTO process) {
                return process.getState().toString();
            }
        };
        table.addColumn(stateColumn, constants.processStatus());
        // TODO: 27.8.18 stylovani, barva podle stavu

        //scheduled time
        TextColumn<ProcessDTO> scheduledColumn = new TextColumn<ProcessDTO>() {
            @Override
            public String getValue(ProcessDTO process) {
                return new ProcessFormater(process, constants).getScheduled();
            }
        };
        table.addColumn(scheduledColumn, constants.processPlanned());

        //started time
        TextColumn<ProcessDTO> startedColumn = new TextColumn<ProcessDTO>() {
            @Override
            public String getValue(ProcessDTO process) {
                return new ProcessFormater(process, constants).getStarted();
            }
        };
        table.addColumn(startedColumn, constants.processStarted());

        //finished time
        TextColumn<ProcessDTO> finishedColumn = new TextColumn<ProcessDTO>() {
            @Override
            public String getValue(ProcessDTO process) {
                return new ProcessFormater(process, constants).getFinished();
            }
        };
        table.addColumn(finishedColumn, constants.processFinished());

        //duration
        TextColumn<ProcessDTO> durationColumn = new TextColumn<ProcessDTO>() {
            @Override
            public String getValue(ProcessDTO process) {
                return new ProcessFormater(process, constants).getDurationFormatted();
            }
        };
        table.addColumn(durationColumn, constants.processDuration());

        //ACTIONS
        List<HasCell<ProcessDTO, ?>> actionCells = new LinkedList<>();

        //delete process
        // TODO: 27.8.18 separate buttons
        actionCells.add(new ProcessActionHasCell(constants.processCancel() + "/" + constants.processStop() + "/" + constants.processDelete(), deleteProcessAction));
        /*actionCells.add(new ProcessActionHasCell(constants.delete(), new ActionCell.Delegate<ProcessDTO>() {

            @Override
            public void execute(ProcessDTO process) {
                //TODO: delete
            }
        }));*/

        //show log
        // TODO: 27.8.18 show only for proper states
        actionCells.add(new ProcessActionHasCell(constants.processShowLog(), showLogAction));
        /*actionCells.add(new ProcessActionHasCell(constants.processShowLog(), new ActionCell.Delegate<ProcessDTO>() {

            @Override
            public void execute(ProcessDTO process) {
                //TODO: show log
            }
        }));*/


        //download output
        // TODO: 27.8.18 show only for proper states
        actionCells.add(new ProcessActionHasCell(constants.processDownloadOutput(), downloadOutputAction));
        /*actionCells.add(new ProcessActionHasCell(constants.processDownloadOutput(), new ActionCell.Delegate<ProcessDTO>() {

            @Override
            public void execute(ProcessDTO process) {
                //TODO: download output
            }
        }));*/


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
                Long first = new ProcessFormater(o1, constants).getDurationMillis();
                Long second = new ProcessFormater(o2, constants).getDurationMillis();
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
