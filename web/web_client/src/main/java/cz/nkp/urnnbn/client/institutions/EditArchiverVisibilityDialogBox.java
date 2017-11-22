package cz.nkp.urnnbn.client.institutions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import cz.nkp.urnnbn.client.AbstractDialogBox;
import cz.nkp.urnnbn.client.forms.institutions.ArchiverVisibilityForm;
import cz.nkp.urnnbn.client.services.InstitutionsService;
import cz.nkp.urnnbn.client.services.InstitutionsServiceAsync;
import cz.nkp.urnnbn.shared.dto.ArchiverDTO;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;

public class EditArchiverVisibilityDialogBox extends AbstractDialogBox {

    private final InstitutionsServiceAsync institutionsService = GWT.create(InstitutionsService.class);
    private final RegistrarDetailsPanel superPanel;
    private final ArchiverVisibilityForm archiverForm;
    private final Label errorLabel = errorLabel(320);

    public EditArchiverVisibilityDialogBox(RegistrarDetailsPanel superPanel, ArchiverDTO archiver, boolean isRegistrar) {
        this.superPanel = superPanel;
        archiverForm = new ArchiverVisibilityForm(archiver);
        setText((isRegistrar ? constants.registrar() : constants.archiver()) + " - " + constants.recordAdjustment());
        setAnimationEnabled(true);
        setWidget(contentPanel());
        center();
    }

    private Panel contentPanel() {
        VerticalPanel result = new VerticalPanel();
        result.add(archiverForm);
        result.add(buttons());
        result.add(errorLabel);
        return result;
    }

    private Panel buttons() {
        HorizontalPanel buttons = new HorizontalPanel();
        buttons.add(saveButton());
        buttons.add(closeButtion());
        return buttons;
    }

    private Widget saveButton() {
        return new Button(constants.save(), new ClickHandler() {

            public void onClick(ClickEvent event) {
                if (archiverForm.isFilledCorrectly()) {
                    institutionsService.updateArchiver(archiverForm.getDto(), new AsyncCallback<Void>() {

                        public void onSuccess(Void result) {
                            ArchiverDTO archiver = archiverForm.getDto();
                            if (archiver instanceof RegistrarDTO) {
                                superPanel.init((RegistrarDTO) archiverForm.getDto());
                            }
                            EditArchiverVisibilityDialogBox.this.hide();
                        }

                        public void onFailure(Throwable caught) {
                            errorLabel.setText(messages.serverError(caught.getMessage()));
                        }
                    });
                }
            }
        });
    }

    private Widget closeButtion() {
        return new Button(constants.close(), new ClickHandler() {

            public void onClick(ClickEvent event) {
                EditArchiverVisibilityDialogBox.this.hide();
            }
        });
    }

}
