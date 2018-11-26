package cz.nkp.urnnbn.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import cz.nkp.urnnbn.client.accounts.current.CurrentUserDetailsDialogBox;
import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.i18n.MessagesImpl;
import cz.nkp.urnnbn.client.resources.Resources;
import cz.nkp.urnnbn.client.services.ConfigurationService;
import cz.nkp.urnnbn.client.services.ConfigurationServiceAsync;
import cz.nkp.urnnbn.shared.ConfigurationData;
import cz.nkp.urnnbn.shared.dto.UserDTO;

public class UserPanel extends Composite {

    private static final String DEFAULT_LOGIN_PAGE = "login";
    private static final String LOGOUT_PAGE = "logout";
    private final ConfigurationServiceAsync configurationService = GWT.create(ConfigurationService.class);
    private final MessagesImpl messages = GWT.create(MessagesImpl.class);
    private final UserDTO user;
    private Resources resources = GWT.create(Resources.class);
    private ConstantsImpl constants = GWT.create(ConstantsImpl.class);
    private HorizontalPanel panel = new HorizontalPanel();

    public UserPanel(UserDTO user) {
        this.user = user;
        initWidget(panel);
    }

    @Override
    protected void onLoad() {
        configurationService.getConfiguration(new AsyncCallback<ConfigurationData>() {
            public void onSuccess(ConfigurationData data) {
                initPanel(data);
            }

            public void onFailure(Throwable caught) {
                Window.alert(messages.serverError(caught.getMessage()));
            }
        });
    }

    private void initPanel(final ConfigurationData configuration) {
        panel.addStyleName(resources.MainCss().userPanel());
        panel.add(langSwitchWidget());
        if (user.getLogin() == null) {
            //login button
            final String loginUrl = configuration.getLoginPage() == null ? DEFAULT_LOGIN_PAGE : configuration.getLoginPage();
            panel.add(new Button(constants.loginButton(), new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    Window.Location.assign(loginUrl);
                }
            }));
        } else {
            //button with avatar and username
            Button avatarButton = new Button();
            avatarButton.setHTML("" +
                    "<div>" +
                    "<img src='img/avatar_black_14.png' style='vertical-align: middle;' width='14px' height='14px'/>" +
                    "<span style='vertical-align: middle; margin-left: 4px;'><b>" + user.getLogin() + "</b></span>" +
                    "</div>");
            avatarButton.addStyleName(resources.MainCss().avatarBtn());
            avatarButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    new CurrentUserDetailsDialogBox(user, LOGOUT_PAGE).show();
                }
            });
            panel.add(avatarButton);
        }
    }

    private Widget langSwitchWidget() {
        HorizontalPanel panel = new HorizontalPanel();
        LocaleInfo locale = LocaleInfo.getCurrentLocale();
        //CZ
        PushButton btnCz = new PushButton(new Image("img/flag_cz_24_rect.png"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                Window.Location.assign(GWT.getHostPageBaseURL() + "?locale=cs");
            }
        });
        if (locale.getLocaleName().startsWith("cs")) {
            btnCz.setEnabled(false);
        }
        btnCz.addStyleName(resources.MainCss().langBtn());
        panel.add(btnCz);
        //EN
        PushButton btnEn = new PushButton(new Image("img/flag_en_24_rect.png"), new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                Window.Location.assign(GWT.getHostPageBaseURL() + "?locale=en");
            }
        });
        if (locale.getLocaleName().startsWith("en")) {
            btnEn.setEnabled(false);
        }
        btnEn.addStyleName(resources.MainCss().langBtn());
        panel.add(btnEn);
        return panel;
    }
}
