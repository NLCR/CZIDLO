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
import cz.nkp.urnnbn.client.forms.userAccounts.UserDetailsForm;
import cz.nkp.urnnbn.client.services.UserAccountService;
import cz.nkp.urnnbn.client.services.UserAccountServiceAsync;
import cz.nkp.urnnbn.shared.dto.UserDTO;

public class AddUserAccountDialogBox extends AbstractDialogBox {

    private final UserAccountServiceAsync accountsService = GWT.create(UserAccountService.class);
    private final UsersAdministrationPanel superPanel;
    private final UserDetailsForm form = new UserDetailsForm();
    private final Label errorLabel = errorLabel(320);

    public AddUserAccountDialogBox(UsersAdministrationPanel superPanel) {
        this.superPanel = superPanel;
        setText(constants.userAccount() + " - " + constants.recordInsertion());
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
                    accountsService.insertUser(form.getDto(), new AsyncCallback<UserDTO>() {

                        @Override
                        public void onSuccess(UserDTO result) {
                            superPanel.addUser(result);
                            AddUserAccountDialogBox.this.hide();
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
                AddUserAccountDialogBox.this.hide();
            }
        });
    }
}
