package cz.nkp.urnnbn.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.i18n.MessagesImpl;
import cz.nkp.urnnbn.client.resources.Resources;
import cz.nkp.urnnbn.client.services.ConfigurationService;
import cz.nkp.urnnbn.client.services.ConfigurationServiceAsync;
import cz.nkp.urnnbn.shared.ConfigurationData;
import cz.nkp.urnnbn.shared.dto.UserDTO;

public class UserPanel extends Composite {

    private static final String DEFAULT_LOGIN_PAGE = "spring_security_login";
    private static final String LOGOUT_PAGE = "logout";
    private final ConfigurationServiceAsync configurationService = GWT.create(ConfigurationService.class);
    private final MessagesImpl messages = GWT.create(MessagesImpl.class);
    private final UserDTO user;
    private Resources resources = GWT.create(Resources.class);
    private ConstantsImpl constants = GWT.create(ConstantsImpl.class);
    private VerticalPanel panel = new VerticalPanel();

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

    private void initPanel(ConfigurationData configuration) {
        panel.addStyleName(resources.MainCss().userPanel());
        Anchor language = langAnchor();
        language.addStyleName(resources.MainCss().userPanelContent());
        panel.add(language);
        if (user.getLogin() == null) {
            String loginUrl = configuration.getLoginPage() == null ? DEFAULT_LOGIN_PAGE : configuration.getLoginPage();
            panel.add(buildLink(loginUrl, constants.loginButton()));
        } else {
            Label userName = new Label(user.getLogin());
            userName.addStyleName(resources.MainCss().userPanelContent());
            panel.add(userName);
            panel.add(buildLink(LOGOUT_PAGE, constants.logoutButton()));
        }
    }

    private HTML buildLink(String url, String text) {
        HTML result = new HTML("<a href='" + url + "'>" + text + "</a>");
        result.addStyleName(resources.MainCss().userPanelContent());
        return result;
    }

    private Anchor langAnchor() {
        LocaleInfo locale = LocaleInfo.getCurrentLocale();
        // locale not defined
        if (locale.getLocaleName() == null) {
            return new Anchor(constants.localeEn(), "?locale=en");
        }// locale cs_CZ
        else if (locale.getLocaleName().startsWith("cs")) {
            return new Anchor(constants.localeEn(), GWT.getHostPageBaseURL() + "?locale=en");
        }// locale en or other
        else {
            return new Anchor(constants.localeCz(), GWT.getHostPageBaseURL() + "?locale=cs");
        }
    }
}
