package cz.nkp.urnnbn.client.institutions;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;

import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO;

public class InstitutionsAdminstrationPanel extends ScrollPanel {

	private final UserDTO user;
	private Panel activePanel;

	public InstitutionsAdminstrationPanel(UserDTO user) {
		this.user = user;
	}

	public void onLoad() {
		showInstitutions();
	}

	public void showInstitutions() {
		detachActivePanel();
		activePanel = new InstitutionListPanel(this, user);
		add(activePanel);
	}

	public void showRegistrarDetails(RegistrarDTO registrar) {
		detachActivePanel();
		activePanel = new RegistrarDetailsPanel(this, user, registrar);
		add(activePanel);
	}

	private void detachActivePanel() {
		if (activePanel != null) {
			activePanel.removeFromParent();
		}
		clear();
	}
}
