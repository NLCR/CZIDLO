package cz.nkp.urnnbn.client.editRecord;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import cz.nkp.urnnbn.client.AbstractDialogBox;
import cz.nkp.urnnbn.client.Operation;
import cz.nkp.urnnbn.client.forms.digitalDocument.RegistrarScopeIdForm;
import cz.nkp.urnnbn.client.services.DataService;
import cz.nkp.urnnbn.client.services.DataServiceAsync;
import cz.nkp.urnnbn.shared.dto.RegistrarScopeIdDTO;

import java.util.logging.Logger;

public class EditRegistrarScopeIdDialogBox extends AbstractDialogBox {

    private static final Logger logger = Logger.getLogger(EditRegistrarScopeIdDialogBox.class.getName());
    private final DataServiceAsync dataService = GWT.create(DataService.class);
    private final RegistrarScopeIdForm form;
    private final Label errorLabel = errorLabel(320);
    private final Operation<RegistrarScopeIdDTO> onUpdated;
    private Button btnSave;
    private Button btnClose;

    public EditRegistrarScopeIdDialogBox(RegistrarScopeIdDTO originalDto, Operation<RegistrarScopeIdDTO> onUpdated) {
        this.form = new RegistrarScopeIdForm(originalDto, true);
        this.onUpdated = onUpdated;
        init();
    }

    private void init() {
        setText(constants.registrarScopeId() + " - " + constants.recordAdjustment());
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
        return new Button(constants.save(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (form.isFilledCorrectly()) {
                    btnClose.setEnabled(false);
                    btnSave.setEnabled(false);
                    dataService.updateRegistrarScopeIdentifier(form.getDto(), new AsyncCallback<RegistrarScopeIdDTO>() {

                        @Override
                        public void onSuccess(RegistrarScopeIdDTO registrarScopeIdDTO) {
                            onUpdated.run(registrarScopeIdDTO);
                            closeDialog();
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            btnClose.setEnabled(true);
                            btnSave.setEnabled(true);
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
                closeDialog();
            }
        });
    }

    private void closeDialog() {
        hide();
    }
}
