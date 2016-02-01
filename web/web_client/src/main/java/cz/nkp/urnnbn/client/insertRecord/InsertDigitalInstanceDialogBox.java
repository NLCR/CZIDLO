package cz.nkp.urnnbn.client.insertRecord;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import cz.nkp.urnnbn.client.AbstractDialogBox;
import cz.nkp.urnnbn.client.OnUpdate;
import cz.nkp.urnnbn.client.forms.digitalDocument.DigitalInstanceForm;
import cz.nkp.urnnbn.client.search.SearchPanel;
import cz.nkp.urnnbn.client.services.DataService;
import cz.nkp.urnnbn.client.services.DataServiceAsync;
import cz.nkp.urnnbn.shared.dto.DigitalInstanceDTO;
import cz.nkp.urnnbn.shared.dto.DigitalLibraryDTO;
import cz.nkp.urnnbn.shared.dto.UrnNbnDTO;

public class InsertDigitalInstanceDialogBox extends AbstractDialogBox {

    private final DataServiceAsync dataService = GWT.create(DataService.class);
    private final UrnNbnDTO urnNbn;
    private final DigitalInstanceForm form;
    private final Label errorLabel = errorLabel(320);
    private final OnUpdate<DigitalInstanceDTO> onUpdate;

    public InsertDigitalInstanceDialogBox(final RecordDataPanel superPanel, UrnNbnDTO urnNbn, ArrayList<DigitalLibraryDTO> libraries) {
        this.urnNbn = urnNbn;
        this.form = new DigitalInstanceForm(libraries);
        this.onUpdate = new OnUpdate<DigitalInstanceDTO>() {

            public void onUpdate(DigitalInstanceDTO instance) {
                superPanel.addDigitalInstance(instance);
            }

        };
        init();
    }

    public InsertDigitalInstanceDialogBox(final SearchPanel superPanel, UrnNbnDTO urnNbn, ArrayList<DigitalLibraryDTO> libraries) {
        this.urnNbn = urnNbn;
        this.form = new DigitalInstanceForm(libraries);
        this.onUpdate = new OnUpdate<DigitalInstanceDTO>() {

            public void onUpdate(DigitalInstanceDTO instance) {
                superPanel.refresh();
            }

        };
        init();
    }

    private void init() {
        setText(constants.digitalInstance() + " - " + constants.recordInsertion());
        setAnimationEnabled(true);
        setWidget(contentPanel());
        center();
    }

    private Widget contentPanel() {
        VerticalPanel result = new VerticalPanel();
        result.add(form);
        result.add(buttons());
        result.add(errorLabel);
        return result;
    }

    private Panel buttons() {
        HorizontalPanel result = new HorizontalPanel();
        result.add(saveButton());
        result.add(closeButton());
        return result;
    }

    private Button saveButton() {
        return new Button(constants.insert(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (form.isFilledCorrectly()) {
                    dataService.saveDigitalInstance(form.getDto(), urnNbn, new AsyncCallback<DigitalInstanceDTO>() {

                        @Override
                        public void onSuccess(DigitalInstanceDTO result) {
                            InsertDigitalInstanceDialogBox.this.onUpdate.onUpdate(result);
                            InsertDigitalInstanceDialogBox.this.hide();
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            errorLabel.setText(messages.serverError(caught.getMessage()));
                        }
                    });
                }
            }
        });
    }

    private Button closeButton() {
        return new Button(constants.close(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                InsertDigitalInstanceDialogBox.this.hide();
            }
        });
    }
}
