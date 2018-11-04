package cz.nkp.urnnbn.client.processes.oaiAdapterConfigPanel;

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
import cz.nkp.urnnbn.shared.dto.process.XmlTransformationDTO;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Martin Řehánek on 27.8.18.
 */
public class TransformationTableWidget extends Composite {

    private final List<XmlTransformationDTO> transformations;
    private final ConstantsImpl constants;

    private final TransformationButtonAction.Operation showOperation;
    private final TransformationButtonAction.Operation downloadOperation;
    private final TransformationButtonAction.Operation deleteOperation;

    public TransformationTableWidget(List<XmlTransformationDTO> transformations, ConstantsImpl constants,
                                     TransformationButtonAction.Operation showOperation,
                                     TransformationButtonAction.Operation downloadOperation,
                                     TransformationButtonAction.Operation deleteOperation
    ) {
        this.transformations = transformations;
        this.constants = constants;
        this.showOperation = showOperation;
        this.downloadOperation = downloadOperation;
        this.deleteOperation = deleteOperation;
        initWidget(buildTable());
    }

    private Widget buildTable() {
        CellTable<XmlTransformationDTO> table = new CellTable<>();
        table.setPageSize(transformations.size());

        //DATA
        //name
        TextColumn<XmlTransformationDTO> nameColumn = new TextColumn<XmlTransformationDTO>() {
            @Override
            public String getValue(XmlTransformationDTO transformation) {
                return transformation.getName();
            }
        };
        table.addColumn(nameColumn, constants.processOaiAdapterTransformationTitle());

        //description
        TextColumn<XmlTransformationDTO> descColumn = new TextColumn<XmlTransformationDTO>() {
            @Override
            public String getValue(XmlTransformationDTO transformation) {
                return transformation.getDescription();
            }
        };
        table.addColumn(descColumn, constants.processOaiAdapterTransformationDescription());

        //created time
        TextColumn<XmlTransformationDTO> createdColumn = new TextColumn<XmlTransformationDTO>() {
            @Override
            public String getValue(XmlTransformationDTO transformation) {
                return new TransformationFormatter(transformation, constants).getCreated();
            }
        };
        table.addColumn(createdColumn, constants.processOaiAdapterTransformationCreated());


        //ACTIONS
        List<HasCell<XmlTransformationDTO, ?>> actionCells = new LinkedList<>();
        actionCells.add(new TransformationButtonHasCell(new TransformationButtonAction(
                "img/process_show_log.png",
                constants.show(),
                showOperation)));
        actionCells.add(new TransformationButtonHasCell(new TransformationButtonAction(
                "img/process_download_output.png",
                constants.download(),
                downloadOperation)));
        actionCells.add(new TransformationButtonHasCell(new TransformationButtonAction(
                "img/process_delete.png",
                constants.delete(),
                deleteOperation)));
        CompositeCell<XmlTransformationDTO> actionsCell = new CompositeCell<>(actionCells);
        Column<XmlTransformationDTO, XmlTransformationDTO> actionsColumn = new Column<XmlTransformationDTO, XmlTransformationDTO>(actionsCell) {

            @Override
            public XmlTransformationDTO getValue(XmlTransformationDTO transformation) {
                return transformation;
            }
        };
        table.addColumn(actionsColumn);

        // Data provider.
        ListDataProvider<XmlTransformationDTO> dataProvider = new ListDataProvider<>(transformations);
        dataProvider.addDataDisplay(table);


        //SORTING
        ColumnSortEvent.ListHandler<XmlTransformationDTO> columnSortHandler = new ColumnSortEvent.ListHandler<>(dataProvider.getList());

        //name
        nameColumn.setSortable(true);
        columnSortHandler.setComparator(nameColumn, new Comparator<XmlTransformationDTO>() {

            public int compare(XmlTransformationDTO o1, XmlTransformationDTO o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        //description
        descColumn.setSortable(true);
        columnSortHandler.setComparator(descColumn, new Comparator<XmlTransformationDTO>() {

            public int compare(XmlTransformationDTO o1, XmlTransformationDTO o2) {
                return o1.getDescription().compareTo(o2.getDescription());
            }
        });
        //created time
        createdColumn.setSortable(true);
        columnSortHandler.setComparator(createdColumn, new Comparator<XmlTransformationDTO>() {

            public int compare(XmlTransformationDTO o1, XmlTransformationDTO o2) {
                if (o1.getCreated() != null && o2.getCreated() != null) {
                    return o1.getCreated().compareTo(o2.getCreated());
                } else if (o1.getCreated() == null && o2.getCreated() == null) {
                    return 0;
                } else if (o1.getCreated() == null) {
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        table.addColumnSortHandler(columnSortHandler);
        // By default sorted by name
        table.getColumnSortList().push(descColumn);
        ColumnSortEvent.fire(table, table.getColumnSortList());

        return table;

    }


}
