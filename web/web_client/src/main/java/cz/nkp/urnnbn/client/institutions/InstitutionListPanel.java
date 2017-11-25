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

import java.util.*;
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

    private static class CustomHTML<T> extends HTML {

        private T object;

        public CustomHTML(String html, T object) {
            super(html);
            this.object = object;
        }

        public T getObject() {
            return object;
        }

        public void setObject(T object) {
            this.object = object;
        }

    }

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
                registrars = sortByName(result);
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

    private <T extends ArchiverDTO> ArrayList<T> sortByName(ArrayList<T> result) {
        Collections.sort(result, new Comparator<ArchiverDTO>() {
            private final CzechStringComparator stringComparator = new CzechStringComparator();

            public int compare(ArchiverDTO o1, ArchiverDTO o2) {
                return stringComparator.compare(o1.getName(), o2.getName());
            }
        });
        return result;
    }

    void loadArchivers() {
        institutionsService.getAllArchivers(new AsyncCallback<ArrayList<ArchiverDTO>>() {

            @Override
            public void onSuccess(ArrayList<ArchiverDTO> result) {
                if (!user.isSuperAdmin()) {
                    result = removeHidden(result);
                }
                archivers = sortByName(result);
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
        result.add(buildRegistrarsGrid());
        if (user.isSuperAdmin()) {
            result.add(addRegistrarButton());
        }
        return result;
    }

    private Panel archiversPanel() {
        VerticalPanel result = new VerticalPanel();
        result.setStyleName(css.block());
        result.add(archiversHeading());
        result.add(buildArchiversGrid());
        if (user.isSuperAdmin()) {
            result.add(addArchiverButton());
        }
        return result;
    }

    private Widget registrarsHeading() {
        Label label = new Label(constants.registrarList());
        label.addStyleName(css.listHeading());
        return label;
    }

    @Deprecated
    private Button registrarDeleteButton(final RegistrarDTO registrar) {
        return new Button(constants.delete(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                deleteRegistrar(registrar);
            }
        });
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

    @Deprecated
    private Button registrarDetailsButton(final RegistrarDTO registrar) {
        return new Button(constants.details(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                superPanel.showRegistrarDetails(registrar);
            }
        });
    }

    private int registrarGridColumns() {
        if (user.isSuperAdmin()) {
            return 3;
        } else if (user.isLoggedUser()) {
            return 2;
        } else {
            return 2;
        }
    }

    private Button addRegistrarButton() {
        return new Button(constants.add(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                AddRegistrarDialogBox dialogBox = new AddRegistrarDialogBox(InstitutionListPanel.this);
                dialogBox.show();
            }
        });
    }

    private Widget archiversHeading() {
        Label label = new Label(constants.archiverList());
        label.addStyleName(css.listHeading());
        return label;
    }

    private interface GridHelper<T extends ArchiverDTO> {

        public Button createDetailsButton(T item);

        public Button createEditButton(T item);

        public Button createDeleteButton(T item);

        public void update(List<T> items, AsyncCallback<Void> callBack);
    }

    private class ArchiversGridHelper implements GridHelper<ArchiverDTO> {

        public Button createDetailsButton(ArchiverDTO item) {
            return archiverDetailsButton(item);
        }

        public Button createEditButton(ArchiverDTO item) {
            return archiverEditButton(item);
        }

        public Button createDeleteButton(ArchiverDTO item) {
            return archiverDeleteButton(item);
        }

        public void update(List<ArchiverDTO> items, AsyncCallback<Void> callBack) {
            institutionsService.updateArchivers(items, callBack);
        }
    }

    private AbsolutePanel buildRegistrarGrid(List<RegistrarDTO> registrars) {
        final AbsolutePanel panel = new AbsolutePanel();
        CellTable<RegistrarDTO> table = new CellTable<>();
        table.setPageSize(registrars.size());

        //DATA
        //name
        TextColumn<RegistrarDTO> nameColumn = new TextColumn<RegistrarDTO>() {
            @Override
            public String getValue(RegistrarDTO object) {
                return object.getName();
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
            public void execute(RegistrarDTO object) {
                superPanel.showRegistrarDetails(object);
            }
        }));
        //delete button
        if (user.isSuperAdmin()) {
            actionCells.add(new RegistrarActionHasCell(constants.delete(), new ActionCell.Delegate<RegistrarDTO>() {

                @Override
                public void execute(RegistrarDTO object) {
                    deleteRegistrar(object);
                }
            }));
        }
        CompositeCell<RegistrarDTO> actionsCell = new CompositeCell<>(actionCells);
        Column<RegistrarDTO, RegistrarDTO> actionsColumn = new Column<RegistrarDTO, RegistrarDTO>(actionsCell) {

            @Override
            public RegistrarDTO getValue(RegistrarDTO object) {
                return object;
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

        panel.add(table);
        return panel;
    }

    private AbsolutePanel buildArchiverGrid(ArrayList<ArchiverDTO> archivers) {
        final AbsolutePanel panel = new AbsolutePanel();
        CellTable<ArchiverDTO> table = new CellTable<>();
        table.setPageSize(archivers.size());

        //DATA
        //name
        TextColumn<ArchiverDTO> nameColumn = new TextColumn<ArchiverDTO>() {
            @Override
            public String getValue(ArchiverDTO object) {
                return object.getName();
            }
        };
        table.addColumn(nameColumn, constants.title());

        //ACTIONS
        //details button
        List<HasCell<ArchiverDTO, ?>> actionCells = new LinkedList<>();
        actionCells.add(new ArchiverActionHasCell(constants.details(), new ActionCell.Delegate<ArchiverDTO>() {

            @Override
            public void execute(ArchiverDTO object) {
                showArchiverDetails(object);
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
            public ArchiverDTO getValue(ArchiverDTO object) {
                return object;
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

        panel.add(table);
        return panel;
    }


    private <T extends ArchiverDTO> AbsolutePanel getGrid(List<T> list, final GridHelper<T> gridHelper) {
        final AbsolutePanel panel = new AbsolutePanel();


    /*    final FlexTable table = new FlexTable();
        for (int row = 0; row < list.size(); row++) {
            T archiver = list.get(row);
            CustomHTML<T> handle = new CustomHTML<T>(archiver.getName(), archiver);
            table.setWidget(row, 0, handle);
            Button detailsButton = gridHelper.createDetailsButton(archiver);
            table.setWidget(row, 1, detailsButton);
            if (user.isSuperAdmin()) {
                int pos = 2;
                Button edit = gridHelper.createEditButton(archiver);
                if (edit != null) {
                    table.setWidget(row, pos, edit);
                    pos++;
                }
                Button delete = gridHelper.createDeleteButton(archiver);
                if (delete != null) {
                    table.setWidget(row, pos, delete);
                    pos++;
                }
            }
        }
        panel.add(table);
        if (user.isSuperAdmin()) {
            final Button saveButton = new Button(constants.save());
            saveButton.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent arg0) {
                    List<T> items = new ArrayList<T>();
                    for (int row = 0; row < table.getRowCount(); row++) {
                        CustomHTML<T> widget = (CustomHTML<T>) table.getWidget(row, 0);
                        T archiver = widget.getObject();
                        items.add(archiver);
                    }

                    gridHelper.update(items, new AsyncCallback<Void>() {

                        public void onFailure(Throwable caught) {
                            logger.severe(caught.getMessage());
                        }

                        public void onSuccess(Void arg0) {
                            // nothing
                        }

                    });
                }
            });
            panel.add(saveButton);
            //FlexTableRowDropController flexTableRowDropController = new FlexTableRowDropController(table);
            //tableRowDragController.registerDropController(flexTableRowDropController);
        }
        */

        return panel;
    }

    private AbsolutePanel buildArchiversGrid() {
        //return getGrid(archivers, new ArchiversGridHelper());
        return buildArchiverGrid(archivers);
    }

    private AbsolutePanel buildRegistrarsGrid() {
        return buildRegistrarGrid(registrars);
    }

    @Deprecated
    private Button archiverEditButton(final ArchiverDTO archiver) {
        return new Button(constants.edit(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                editArchiver(archiver);
            }
        });
    }

    private void editArchiver(ArchiverDTO archiver) {
        new EditArchiverDialogBox(InstitutionListPanel.this, archiver).show();
    }

    @Deprecated
    private Button archiverDeleteButton(final ArchiverDTO archiver) {
        Button result = new Button(constants.delete());
        result.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                deleteArchiver(archiver);
            }
        });
        return result;
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

    @Deprecated
    private Button archiverDetailsButton(final ArchiverDTO archiver) {
        Button result = new Button(constants.details());
        result.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                showArchiverDetails(archiver);
            }
        });
        return result;
    }

    private void showArchiverDetails(ArchiverDTO archiver) {
        ArchiverDetailsDialogBox dialogBox = new ArchiverDetailsDialogBox(archiver);
        dialogBox.show();
        dialogBox.center();
    }

    private int archiverGridColumns() {
        if (user.isSuperAdmin()) {
            return 4;
        } else if (user.isLoggedUser()) {
            return 2;
        } else {
            return 2;
        }
    }

    private Button addArchiverButton() {
        return new Button(constants.add(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                new AddArchiverDialogBox(InstitutionListPanel.this).show();
            }
        });
    }

    public UserDTO getUser() {
        return user;
    }
}
