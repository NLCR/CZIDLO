package cz.nkp.urnnbn.client.insertRecord;

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

public class InsertRegistrarScopeIdDialogBox extends AbstractDialogBox {

    private static final Logger logger = Logger.getLogger(InsertRegistrarScopeIdDialogBox.class.getName());
    private final DataServiceAsync dataService = GWT.create(DataService.class);
    private final RegistrarScopeIdForm form;
    private final Label errorLabel = errorLabel(320);
    private final Operation<RegistrarScopeIdDTO> onInserted;
    private Button btnSave;
    private Button btnClose;

    public InsertRegistrarScopeIdDialogBox(Long registrarId, Long digDocId, Operation<RegistrarScopeIdDTO> onInserted) {
        this.form = new RegistrarScopeIdForm(registrarId, digDocId);
        this.onInserted = onInserted;
        init();
    }

    private void init() {
        setText("registrar-scope id" + " - " + constants.recordInsertion());
        //setText(constants.digitalInstance() + " - " + constants.recordInsertion());
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
                    dataService.addRegistrarScopeIdentifier(form.getDto(), new AsyncCallback<RegistrarScopeIdDTO>() {

                        @Override
                        public void onSuccess(RegistrarScopeIdDTO registrarScopeIdDTO) {
                            onInserted.run(registrarScopeIdDTO);
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
