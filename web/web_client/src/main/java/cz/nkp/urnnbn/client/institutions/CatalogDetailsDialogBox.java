package cz.nkp.urnnbn.client.institutions;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import cz.nkp.urnnbn.client.AbstractDialogBox;
import cz.nkp.urnnbn.shared.dto.CatalogDTO;

public class CatalogDetailsDialogBox extends AbstractDialogBox {

    private final CatalogDTO catalog;

    public CatalogDetailsDialogBox(CatalogDTO catalog) {
        this.catalog = catalog;
        String title = constants.catalog() + " - " + constants.details();
        setTitle(title);
        setText(title);
        setAnimationEnabled(true);
        setWidget(contentPanel());
        center();
    }

    private Widget contentPanel() {
        VerticalPanel panel = new VerticalPanel();
        Grid grid = new Grid(determineRows(), 2);
        grid.setWidget(0, 0, new Label(constants.title() + ':'));
        grid.setWidget(0, 1, new Label(catalog.getName()));
        grid.setWidget(1, 0, new Label(constants.description() + ':'));
        grid.setWidget(1, 1, new Label(catalog.getDescription()));
        grid.setWidget(2, 0, new Label(constants.urlPrefix() + ':'));
        grid.setWidget(2, 1, new Label(catalog.getUrlPrefix()));
        if (catalog.getCreated() != null) {
            grid.setWidget(3, 0, new Label(constants.created() + ':'));
            grid.setWidget(3, 1, new Label(catalog.getCreated()));
            if (catalog.getModified() != null && !catalog.getModified().equals(catalog.getCreated())) {
                grid.setWidget(4, 0, new Label(constants.modified() + ':'));
                grid.setWidget(4, 1, new Label(catalog.getModified()));
            }
        }
        panel.add(grid);
        panel.add(closeButton());
        return panel;
    }

    private int determineRows() {
        int result = 3;
        if (catalog.getCreated() != null) {
            result++;
            if (catalog.getModified() != null && !catalog.getModified().equals(catalog.getCreated())) {
                result++;
            }
        }
        return result;
    }

    private Button closeButton() {
        return new Button(constants.close(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                CatalogDetailsDialogBox.this.hide();
            }
        });
    }

}
