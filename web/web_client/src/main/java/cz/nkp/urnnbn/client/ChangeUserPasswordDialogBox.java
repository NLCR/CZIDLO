package cz.nkp.urnnbn.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import cz.nkp.urnnbn.client.forms.userAccounts.ChangeUserPasswordOwnForm;
import cz.nkp.urnnbn.client.services.AuthService;
import cz.nkp.urnnbn.client.services.AuthServiceAsync;
import cz.nkp.urnnbn.client.services.UserAccountService;
import cz.nkp.urnnbn.client.services.UserAccountServiceAsync;
import cz.nkp.urnnbn.shared.dto.UserDTO;

import java.util.logging.Logger;

public class ChangeUserPasswordDialogBox extends AbstractDialogBox {

    private static final Logger logger = Logger.getLogger(ChangeUserPasswordDialogBox.class.getName());
    private final AuthServiceAsync authService = GWT.create(AuthService.class);
    private final UserDTO user;
    private final ChangeUserPasswordOwnForm form;
    private final Label errorLabel = errorLabel(320);


    public ChangeUserPasswordDialogBox(UserDTO user) {
        this.user = user;
        this.form = new ChangeUserPasswordOwnForm(user);
        String title = constants.changePasswordDialogTitle();
        setTitle(title);
        setText(title);
        setAnimationEnabled(true);
        setWidget(buildWidget());
        center();
    }

    private Widget buildWidget() {
        VerticalPanel panel = new VerticalPanel();
        panel.add(contentPanel());
        panel.add(buttons());
        return panel;
    }

    private IsWidget contentPanel() {
        VerticalPanel panel = new VerticalPanel();
        panel.setHeight("180px");
        panel.setWidth("200px");
        panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        panel.add(form);
        panel.add(errorLabel);
        return panel;
    }

    private Panel buttons() {
        HorizontalPanel result = new HorizontalPanel();
        result.setWidth("100%");
        result.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        result.add(buttonsContainer());
        return result;
    }

    private IsWidget buttonsContainer() {
        HorizontalPanel result = new HorizontalPanel();
        result.add(closeButton());
        result.add(confirmButton());
        return result;
    }

    private Button confirmButton() {
        return new Button(constants.changePasswordButton(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                if (form.isFilledCorrectly()) {
                    updatePasswordIfNoOtherFormError(form);
                }
            }
        });
    }

    private void updatePasswordIfNoOtherFormError(ChangeUserPasswordOwnForm form) {
        final String login = user.getLogin();
        String passOld = form.getPassOld();
        final String passNew1 = form.getPassNew1();
        String passNew2 = form.getPassNew2();
        if (!passNew1.equals(passNew2)) {
            errorLabel.setText(constants.changePasswordDialogNewPasswordsMismatch());
        } else if (passNew1.equals(passOld)) {
            errorLabel.setText(constants.changePasswordDialogNewPasswordSameAsOld());
        } else {
            authService.checkPasswordMatch(login, passOld, new AsyncCallback<Boolean>() {
                @Override
                public void onFailure(Throwable throwable) {
                    errorLabel.setText(messages.serverError(throwable.getMessage()));
                }

                @Override
                public void onSuccess(Boolean match) {
                    if (!match) {
                        errorLabel.setText(constants.changePasswordDialogOriginalPasswordMismatch());
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
