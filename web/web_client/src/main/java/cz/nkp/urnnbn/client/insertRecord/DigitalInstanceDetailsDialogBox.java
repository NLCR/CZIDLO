package cz.nkp.urnnbn.client.insertRecord;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

import cz.nkp.urnnbn.client.AbstractDialogBox;
import cz.nkp.urnnbn.client.institutions.DigitalLibraryDetailsDialogBox;
import cz.nkp.urnnbn.shared.dto.DigitalInstanceDTO;

public class DigitalInstanceDetailsDialogBox extends AbstractDialogBox {

    private final DigitalInstanceDTO instance;

    public DigitalInstanceDetailsDialogBox(DigitalInstanceDTO instance) {
        this.instance = instance;
        String title = constants.digitalInstance() + " - " + constants.details();
        setTitle(title);
        setText(title);
        setAnimationEnabled(true);
        setWidget(contentPanel());
        center();
    }

    private Panel contentPanel() {
        VerticalPanel panel = new VerticalPanel();
        Grid grid = new Grid(determineRows(), 3);
        int index = 0;
        grid.setWidget(index, 0, new Label(constants.digitalLibrary() + ':'));
        grid.setWidget(index, 1, new Label(instance.getLibrary().getName()));
        grid.setWidget(index++, 2, libraryDetailsButton());
        if (instance.getFormat() != null) {
            grid.setWidget(index, 0, new Label(constants.format() + ':'));
            grid.setWidget(index++, 1, new Label(instance.getFormat()));
        }
        if (instance.getAccessibility() != null) {
            grid.setWidget(index, 0, new Label(constants.accessibility() + ':'));
            grid.setWidget(index++, 1, new Label(instance.getAccessibility()));
        }
        // TODO: 3.12.17 access_restriction
        grid.setWidget(index, 0, new Label(constants.url() + ':'));
        grid.setWidget(index++, 1, new HTML(buildUrlOrNull(instance.getUrl())));
        grid.setWidget(index, 0, new Label(constants.created() + ':'));
        grid.setWidget(index++, 1, new Label(instance.getCreated()));
        panel.add(grid);
        panel.add(closeButton());
        return panel;
    }

    private Button libraryDetailsButton() {
        return new Button(constants.details(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                new DigitalLibraryDetailsDialogBox(instance.getLibrary()).show();
            }
        });
    }

    private String buildUrlOrNull(String target) {
        if (target != null) {
            return "<a href=\"" + target + "\">" + target + "</a>";
        } else {
            return null;
        }
    }

    private int determineRows() {
        int result = 3;
        if (instance.getFormat() != null) {
            result++;
        }
        if (instance.getAccessibility() != null) {
            result++;
        }
        // TODO: 3.12.17 access_restriction
        return result;
    }

    private Button closeButton() {
        return new Button(constants.close(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                DigitalInstanceDetailsDialogBox.this.hide();
            }
        });
    }

}
