package cz.nkp.urnnbn.client.accounts;

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
import cz.nkp.urnnbn.client.forms.userAccounts.EditUserForm;
import cz.nkp.urnnbn.client.services.UserAccountService;
import cz.nkp.urnnbn.client.services.UserAccountServiceAsync;
import cz.nkp.urnnbn.shared.dto.UserDTO;

public class EditUserDialogBox extends AbstractDialogBox {

    private final UserAccountServiceAsync accountsService = GWT.create(UserAccountService.class);
    private final UsersAdministrationTab superPanel;
    private final EditUserForm form;
    private final Label errorLabel = errorLabel(320);

    public EditUserDialogBox(UsersAdministrationTab superPanel, UserDTO user) {
        this.superPanel = superPanel;
        this.form = new EditUserForm(user);
        setText(constants.userAccount() + " - " + constants.recordAdjustment());
        setAnimationEnabled(true);
        setWidget(contentPanel());
        center();
    }

    private Panel contentPanel() {
        VerticalPanel result = new VerticalPanel();
        result.add(form);
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

            @Override
            public void onClick(ClickEvent event) {
                if (form.isFilledCorrectly()) {
                    accountsService.updateUser(form.getDto(), new AsyncCallback<Void>() {

                        @Override
                        public void onSuccess(Void result) {
                            // TODO: chybi casove znamky -
                            superPanel.updateUser(form.getDto());
                            EditUserDialogBox.this.hide();
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

    private Widget closeButtion() {
        return new Button(constants.close(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                EditUserDialogBox.this.hide();
            }
        });
    }
}
