package cz.nkp.urnnbn.client.institutions;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;
import cz.nkp.urnnbn.client.CzechStringComparator;
import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.i18n.MessagesImpl;
import cz.nkp.urnnbn.client.resources.InstitutionsPanelCss;
import cz.nkp.urnnbn.client.services.InstitutionsService;
import cz.nkp.urnnbn.client.services.InstitutionsServiceAsync;
import cz.nkp.urnnbn.shared.dto.ArchiverDTO;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class InstitutionListPanel extends VerticalPanel {

    private static final Logger logger = Logger.getLogger(InstitutionListPanel.class.getName());
    private final ConstantsImpl constants = GWT.create(ConstantsImpl.class);
    private final MessagesImpl messages = GWT.create(MessagesImpl.class);
    private final InstitutionsPanelCss css = InstitutionsResources.loadCss();
    private final InstitutionsServiceAsync institutionsService = GWT.create(InstitutionsService.class);
    private final InstitutionsAdminstrationTab superPanel;
    private final UserDTO user;
    private ArrayList<RegistrarDTO> registrars = new ArrayList<>(0);
    private ArrayList<ArchiverDTO> archivers = new ArrayList<>(0);

    public InstitutionListPanel(InstitutionsAdminstrationTab superPanel, UserDTO user) {
        this.superPanel = superPanel;
        this.user = user;
    }

    public void onLoad() {
        loadRegistrars();
        loadArchivers();
        reload();
    }

    void loadRegistrars() {
        institutionsService.getAllRegistrars(new AsyncCallback<ArrayList<RegistrarDTO>>() {
            public void onSuccess(ArrayList<RegistrarDTO> result) {
                if (!user.isSuperAdmin()) {
                    result = removeHidden(result);
                }
                registrars = result;
                reload();
            }

            public void onFailure(Throwable caught) {
                logger.severe("Error loading registrars: " + caught.getMessage());
            }
        });
    }

    private <T extends ArchiverDTO> ArrayList<T> removeHidden(ArrayList<T> list) {
        ArrayList<T> result = new ArrayList<T>();
        for (T item : list) {
            if (!item.isHidden()) {
                result.add(item);
            }
        }
        return result;
    }

    void loadArchivers() {
        institutionsService.getAllArchivers(new AsyncCallback<ArrayList<ArchiverDTO>>() {

            @Override
            public void onSuccess(ArrayList<ArchiverDTO> result) {
                if (!user.isSuperAdmin()) {
                    result = removeHidden(result);
                }
                archivers = result;
                reload();
            }

            @Override
            public void onFailure(Throwable caught) {
                logger.severe("Error loading archivers: " + caught.getMessage());
            }
        });
    }

    public void reload() {
        clear();
        add(registrarsPanel());
        add(archiversPanel());
    }

    private Panel registrarsPanel() {
        VerticalPanel result = new VerticalPanel();
        result.setStyleName(css.block());
        result.add(registrarsHeading());
        result.add(buildRegistrarsGrid(registrars));
        if (user.isSuperAdmin()) {
            result.add(
                    new Button(constants.add(), new ClickHandler() {

                        @Override
                        public void onClick(ClickEvent event) {
                            addRegistrar();
                        }
                    }));
        }
        return result;
    }

    private Panel archiversPanel() {
        VerticalPanel result = new VerticalPanel();
        result.setStyleName(css.block());
        result.add(archiversHeading());
        result.add(buildArchiversGrid(archivers));
        if (user.isSuperAdmin()) {
            result.add(
                    new Button(constants.add(), new ClickHandler() {

                        @Override
                        public void onClick(ClickEvent event) {
                            addArchiver();
                        }
                    }));
        }
        return result;
    }

    private Widget registrarsHeading() {
        Label label = new Label(constants.registrarList());
        label.addStyleName(css.listHeading());
        return label;
    }

    private Widget archiversHeading() {
        Label label = new Label(constants.archiverList());
        label.addStyleName(css.listHeading());
        return label;
    }

    private AbsolutePanel buildRegistrarsGrid(List<RegistrarDTO> registrars) {
        final AbsolutePanel panel = new AbsolutePanel();
        CellTable<RegistrarDTO> table = new CellTable<>();
        table.setPageSize(registrars.size());

        //DATA
        //name
        TextColumn<RegistrarDTO> nameColumn = new TextColumn<RegistrarDTO>() {
            @Override
            public String getValue(RegistrarDTO registrar) {
                return registrar.getName();
            }
        };
        table.addColumn(nameColumn, constants.title());
        //code
        TextColumn<RegistrarDTO> codeColumn = new TextColumn<RegistrarDTO>() {
            @Override
            public String getValue(RegistrarDTO registrar) {
                return registrar.getCode();
            }
        };
        table.addColumn(codeColumn, constants.code());

        //ACTIONS
        //details button
        List<HasCell<RegistrarDTO, ?>> actionCells = new LinkedList<>();
        actionCells.add(new RegistrarActionHasCell(constants.details(), new ActionCell.Delegate<RegistrarDTO>() {

            @Override
            public void execute(RegistrarDTO registrar) {
                showRegistrarDetails(registrar);
            }
        }));
        //delete button
        if (user.isSuperAdmin()) {
            actionCells.add(new RegistrarActionHasCell(constants.delete(), new ActionCell.Delegate<RegistrarDTO>() {

                @Override
                public void execute(RegistrarDTO registrar) {
                    deleteRegistrar(registrar);
                }
            }));
        }
        CompositeCell<RegistrarDTO> actionsCell = new CompositeCell<>(actionCells);
        Column<RegistrarDTO, RegistrarDTO> actionsColumn = new Column<RegistrarDTO, RegistrarDTO>(actionsCell) {

            @Override
            public RegistrarDTO getValue(RegistrarDTO registrar) {
                return registrar;
            }
        };
        table.addColumn(actionsColumn);

        // Data provider.
        ListDataProvider<RegistrarDTO> dataProvider = new ListDataProvider<>(registrars);
        dataProvider.addDataDisplay(table);

        // Sorting
        ColumnSortEvent.ListHandler<RegistrarDTO> columnSortHandler = new ColumnSortEvent.ListHandler<>(dataProvider.getList());
        nameColumn.setSortable(true);
        codeColumn.setSortable(true);
        columnSortHandler.setComparator(nameColumn, new Comparator<RegistrarDTO>() {
            CzechStringComparator stringComparator = new CzechStringComparator();

            public int compare(RegistrarDTO o1, RegistrarDTO o2) {
                return stringComparator.compare(o1.getName(), o2.getName());
            }
        });
        codeColumn.setSortable(true);
        columnSortHandler.setComparator(codeColumn, new Comparator<RegistrarDTO>() {
            public int compare(RegistrarDTO first, RegistrarDTO second) {
                return first.getCode().compareTo(second.getCode());
            }
        });
        table.addColumnSortHandler(columnSortHandler);
        // By default sorted by name
        table.getColumnSortList().push(nameColumn);
        ColumnSortEvent.fire(table, table.getColumnSortList());

        panel.add(table);
        return panel;
    }

    private AbsolutePanel buildArchiversGrid(ArrayList<ArchiverDTO> archivers) {
        final AbsolutePanel panel = new AbsolutePanel();
        CellTable<ArchiverDTO> table = new CellTable<>();
        table.setPageSize(archivers.size());

        //DATA
        //name
        TextColumn<ArchiverDTO> nameColumn = new TextColumn<ArchiverDTO>() {
            @Override
            public String getValue(ArchiverDTO archiver) {
                return archiver.getName();
            }
        };
        table.addColumn(nameColumn, constants.title());

        //ACTIONS
        //details button
        List<HasCell<ArchiverDTO, ?>> actionCells = new LinkedList<>();
        actionCells.add(new ArchiverActionHasCell(constants.details(), new ActionCell.Delegate<ArchiverDTO>() {

            @Override
            public void execute(ArchiverDTO archiver) {
                showArchiverDetails(archiver);
            }
        }));
        if (user.isSuperAdmin()) {
            //edit button
            actionCells.add(new ArchiverActionHasCell(constants.edit(), new ActionCell.Delegate<ArchiverDTO>() {
                @Override
                public void execute(ArchiverDTO archiver) {
                    editArchiver(archiver);
                }
            }));
            //delete button
            actionCells.add(new ArchiverActionHasCell(constants.delete(), new ActionCell.Delegate<ArchiverDTO>() {

                @Override
                public void execute(ArchiverDTO archiver) {
                    deleteArchiver(archiver);
                }
            }));
        }
        CompositeCell<ArchiverDTO> actionsCell = new CompositeCell<>(actionCells);
        Column<ArchiverDTO, ArchiverDTO> actionsColumn = new Column<ArchiverDTO, ArchiverDTO>(actionsCell) {

            @Override
            public ArchiverDTO getValue(ArchiverDTO archiver) {
                return archiver;
            }
        };
        table.addColumn(actionsColumn);

        // Data provider.
        ListDataProvider<ArchiverDTO> dataProvider = new ListDataProvider<>(archivers);
        dataProvider.addDataDisplay(table);

        // Sorting
        ColumnSortEvent.ListHandler<ArchiverDTO> columnSortHandler = new ColumnSortEvent.ListHandler<>(dataProvider.getList());
        nameColumn.setSortable(true);
        columnSortHandler.setComparator(nameColumn, new Comparator<ArchiverDTO>() {
            CzechStringComparator stringComparator = new CzechStringComparator();

            public int compare(ArchiverDTO o1, ArchiverDTO o2) {
                return stringComparator.compare(o1.getName(), o2.getName());
            }
        });
        table.addColumnSortHandler(columnSortHandler);

        // By default sorted by name
        table.getColumnSortList().push(nameColumn);
        ColumnSortEvent.fire(table, table.getColumnSortList());

        panel.add(table);
        return panel;
    }


    /* archiver CRUD methods */

    private void addArchiver() {
        new AddArchiverDialogBox(InstitutionListPanel.this).show();
    }

    private void showArchiverDetails(ArchiverDTO archiver) {
        ArchiverDetailsDialogBox dialogBox = new ArchiverDetailsDialogBox(archiver);
        dialogBox.show();
        dialogBox.center();
    }

    private void editArchiver(ArchiverDTO archiver) {
        new EditArchiverDialogBox(InstitutionListPanel.this, archiver).show();
    }

    private void deleteArchiver(final ArchiverDTO archiver) {
        if (Window.confirm(messages.confirmDeleteArchiver(archiver.getName()))) {
            institutionsService.deleteArchiver(archiver, new AsyncCallback<Void>() {

                @Override
                public void onSuccess(Void result) {
                    archivers.remove(archiver);
                    reload();
                }

                @Override
                public void onFailure(Throwable caught) {
                    Window.alert(messages.archiverCannotBeDeleted(archiver.getName()) + ": " + caught.getMessage());
                }
            });
        }
    }

    /*registrar CRUD*/

    private void addRegistrar() {
        AddRegistrarDialogBox dialogBox = new AddRegistrarDialogBox(InstitutionListPanel.this);
        dialogBox.show();
    }

    private void showRegistrarDetails(RegistrarDTO registrar) {
        superPanel.showRegistrarDetails(registrar);
    }

    private void deleteRegistrar(final RegistrarDTO registrar) {
        if (Window.confirm(messages.confirmDeleteRegistrar(registrar.getName()))) {
            institutionsService.deleteRegistrar(registrar, new AsyncCallback<Void>() {

                @Override
                public void onSuccess(Void result) {
                    registrars.remove(registrar);
                    reload();
                }

                @Override
                public void onFailure(Throwable caught) {
                    Window.alert(messages.registrarCannotBeDeleted(registrar.getName()) + ": " + caught.getMessage());
                }
            });
        }
    }

}
