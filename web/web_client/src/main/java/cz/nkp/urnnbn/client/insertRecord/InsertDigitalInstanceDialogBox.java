package cz.nkp.urnnbn.client.insertRecord;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import cz.nkp.urnnbn.client.AbstractDialogBox;
import cz.nkp.urnnbn.client.Operation;
import cz.nkp.urnnbn.client.forms.digitalDocument.DigitalInstanceForm;
import cz.nkp.urnnbn.client.services.DataService;
import cz.nkp.urnnbn.client.services.DataServiceAsync;
import cz.nkp.urnnbn.shared.dto.DigitalInstanceDTO;
import cz.nkp.urnnbn.shared.dto.DigitalLibraryDTO;
import cz.nkp.urnnbn.shared.dto.UrnNbnDTO;

import java.util.ArrayList;

public class InsertDigitalInstanceDialogBox extends AbstractDialogBox {

    private final DataServiceAsync dataService = GWT.create(DataService.class);
    private final UrnNbnDTO urnNbn;
    private final DigitalInstanceForm form;
    private final Label errorLabel = errorLabel(320);
    private final Operation<DigitalInstanceDTO> onUpdated;
    private Button btnSave;
    private Button btnClose;

    public InsertDigitalInstanceDialogBox(UrnNbnDTO urnNbn, ArrayList<DigitalLibraryDTO> libraries, Operation<DigitalInstanceDTO> onUpdated) {
        this.urnNbn = urnNbn;
        this.form = new DigitalInstanceForm(libraries);
        this.onUpdated = onUpdated;
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
        btnSave = saveButton();
        result.add(btnSave);
        btnClose = closeButton();
        result.add(btnClose);
        return result;
    }

    private Button saveButton() {
        return new Button(constants.insert(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (form.isFilledCorrectly()) {
                    btnClose.setEnabled(false);
                    btnSave.setEnabled(false);
                    dataService.saveDigitalInstance(urnNbn, form.getDto(), new AsyncCallback<DigitalInstanceDTO>() {

                        @Override
                        public void onSuccess(DigitalInstanceDTO result) {
                            onUpdated.run(result);
                            closeDialog();
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            errorLabel.setText(messages.serverError(caught.getMessage()));
                            btnClose.setEnabled(true);
                            btnSave.setEnabled(true);
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
                closeDialog();
            }
        });
    }

    private void closeDialog() {
        hide();
    }
}
