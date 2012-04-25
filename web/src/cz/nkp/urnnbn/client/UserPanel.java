package cz.nkp.urnnbn.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;

import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.shared.dto.UserDTO;

public class UserPanel extends Composite {

	private ConstantsImpl constants = GWT.create(ConstantsImpl.class);
	private AbsolutePanel absolutePanel = new AbsolutePanel();
	private final UserDTO user;

	public UserPanel(UserDTO user) {
		this.user = user;
		initWidget(absolutePanel);
	}

	@Override
	protected void onLoad() {
		absolutePanel.clear();
		absolutePanel.setSize("221px", "128px");
		Anchor lang = langAnchor(); 
		absolutePanel.add(lang, 145, 20);

		if (user.getLogin() == null) {
			Anchor login = new Anchor(constants.loginButton(), "spring_security_login");
			absolutePanel.add(login, 145, 45);
		} else {
			Label userNameLbl = new Label(user.getLogin());
			// userNameLbl.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
			// absolutePanel.add(userNameLbl, 20, 65);
			// absolutePanel.add(userNameLbl, 110, 45);
			absolutePanel.add(userNameLbl, 145, 45);
			// userNameLbl.setSize("135px", "17px");
			Anchor logout = new Anchor(constants.logoutButton(), "logout");
			absolutePanel.add(logout, 145, 65);
		}
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
