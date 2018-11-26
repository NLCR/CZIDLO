package cz.nkp.urnnbn.client.accounts;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import cz.nkp.urnnbn.client.AbstractDialogBox;
import cz.nkp.urnnbn.client.forms.userAccounts.ChangeUserPasswordByAdminForm;
import cz.nkp.urnnbn.client.services.AuthService;
import cz.nkp.urnnbn.client.services.AuthServiceAsync;
import cz.nkp.urnnbn.shared.dto.UserDTO;

/**
 * Created by Martin Řehánek on 26.11.18.
 */
public class UserChangePasswordDialogBox extends AbstractDialogBox {

    private final AuthServiceAsync authService = GWT.create(AuthService.class);
    private final ChangeUserPasswordByAdminForm form;
    private final Label errorLabel = errorLabel(320);


    public UserChangePasswordDialogBox(UserDTO user) {
        form = new ChangeUserPasswordByAdminForm(user);
        String title = constants.changePasswordDialogTitle();
        setTitle(title);
        setText(title);
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
        return new Button(constants.changePasswordButton(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (form.isFilledCorrectly()) {
                    UserDTO user = form.getDto();
                    authService.changePassword(user.getLogin(), user.getPassword(), new AsyncCallback<Void>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            errorLabel.setText(messages.serverError(throwable.getMessage()));
                        }

                        @Override
                        public void onSuccess(Void aVoid) {
                            UserChangePasswordDialogBox.this.hide();
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
                UserChangePasswordDialogBox.this.hide();
            }
        });
    }

}
