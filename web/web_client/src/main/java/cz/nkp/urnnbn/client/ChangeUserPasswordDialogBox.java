package cz.nkp.urnnbn.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import cz.nkp.urnnbn.client.forms.userAccounts.ChangePasswordForm;
import cz.nkp.urnnbn.client.services.AuthService;
import cz.nkp.urnnbn.client.services.AuthServiceAsync;
import cz.nkp.urnnbn.client.services.UserAccountService;
import cz.nkp.urnnbn.client.services.UserAccountServiceAsync;
import cz.nkp.urnnbn.shared.dto.UserDTO;

import java.util.logging.Logger;

public class ChangeUserPasswordDialogBox extends AbstractDialogBox {

    private static final Logger logger = Logger.getLogger(ChangeUserPasswordDialogBox.class.getName());
    private final UserAccountServiceAsync accountsService = GWT.create(UserAccountService.class);
    private final AuthServiceAsync authService = GWT.create(AuthService.class);
    private final UserDTO user;
    private final ChangePasswordForm form;
    private final Label errorLabel = errorLabel(320);


    public ChangeUserPasswordDialogBox(UserDTO user) {
        this.user = user;
        this.form = new ChangePasswordForm(user);
        String title = constants.changPasswordDialogTitle();
        setTitle(title);
        setText(title);
        setAnimationEnabled(true);
        setWidget(contentPanel());
        center();
    }

    private Widget contentPanel() {
        VerticalPanel panel = new VerticalPanel();
        panel.add(form);
        panel.add(buttons());
        panel.add(errorLabel);
        return panel;
    }

    private Panel buttons() {
        HorizontalPanel result = new HorizontalPanel();
        result.add(closeButton());
        result.add(confirmButton());
        return result;
    }

    private Button confirmButton() {
        // TODO: 22.11.18 i18n
        return new Button("změnit heslo", new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                if (form.isFilledCorrectly()) {
                    updatePasswordIfNoOtherFormError(form);
                }
            }
        });
    }

    private void updatePasswordIfNoOtherFormError(ChangePasswordForm form) {
        final String login = user.getLogin();
        String passOld = form.getPassOld();
        final String passNew1 = form.getPassNew1();
        String passNew2 = form.getPassNew2();
        if (!passNew1.equals(passNew2)) {
            // TODO: 22.11.18 i18n
            errorLabel.setText("první a druhý zápis nového hesla se liší");
        } else if (passNew1.equals(passOld)) {
            // TODO: 22.11.18 i18n
            errorLabel.setText("nové heslo se shoduje s původním heslem");
        } else {
            authService.checkPasswordMatch(login, passOld, new AsyncCallback<Boolean>() {
                @Override
                public void onFailure(Throwable throwable) {
                    errorLabel.setText(messages.serverError(throwable.getMessage()));
                }

                @Override
                public void onSuccess(Boolean match) {
                    if (!match) {
                        // TODO: 22.11.18 i18n
                        errorLabel.setText("původní heslo nesouhlasí");
                    } else {
                        authService.changePassword(login, passNew1, new AsyncCallback<Void>() {
                            @Override
                            public void onFailure(Throwable throwable) {
                                errorLabel.setText(messages.serverError(throwable.getMessage()));
                            }

                            @Override
                            public void onSuccess(Void aVoid) {
                                ChangeUserPasswordDialogBox.this.hide();
                                new PasswordChangedDialogBox().show();
                            }
                        });
                    }
                }
            });
        }
    }

    private Button closeButton() {
        return new Button(constants.close(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ChangeUserPasswordDialogBox.this.hide();
            }
        });
    }
}
