package cz.nkp.urnnbn.client.institutions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

import cz.nkp.urnnbn.client.AbstractDialogBox;
import cz.nkp.urnnbn.client.forms.institutions.RegistrarForm;
import cz.nkp.urnnbn.client.services.InstitutionsService;
import cz.nkp.urnnbn.client.services.InstitutionsServiceAsync;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;

public class EditRegistrarDialogBox extends AbstractDialogBox {

    private final InstitutionsServiceAsync institutionsService = GWT.create(InstitutionsService.class);
    private final RegistrarDetailsPanel superPanel;
    private final Label errorLabel = errorLabel(320);
    private RegistrarForm registrarForm;

    public EditRegistrarDialogBox(RegistrarDetailsPanel superPanel, RegistrarDTO registrar) {
        this.superPanel = superPanel;
        this.registrarForm = new RegistrarForm(registrar);
        setText(constants.registrar() + " - " + constants.recordAdjustment());
        setAnimationEnabled(true);
        setWidget(contentPanel());
        center();
    }

    private Panel contentPanel() {
        VerticalPanel result = new VerticalPanel();
        result.add(registrarForm);
        result.add(errorLabel);
        result.add(buttons());
        return result;
    }

    private Panel buttons() {
        HorizontalPanel buttons = new HorizontalPanel();
        buttons.add(saveButton());
        buttons.add(closeButtion());
        return buttons;
    }

    private Button saveButton() {
        return new Button(constants.save(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (registrarForm.isFilledCorrectly()) {
                    institutionsService.updateRegistrar(registrarForm.getDto(), new AsyncCallback<Void>() {

                        @Override
                        public void onSuccess(Void result) {
                            superPanel.init(registrarForm.getDto());
                            EditRegistrarDialogBox.this.hide();
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

    private Button closeButtion() {
        return new Button(constants.close(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                EditRegistrarDialogBox.this.hide();
            }
        });
    }
}
