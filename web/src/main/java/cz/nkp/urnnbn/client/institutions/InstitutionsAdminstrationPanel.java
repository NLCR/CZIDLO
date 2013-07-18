package cz.nkp.urnnbn.client.institutions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Panel;

import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.services.UserAccountService;
import cz.nkp.urnnbn.client.services.UserAccountServiceAsync;
import cz.nkp.urnnbn.client.tabs.SingleTabContentPanel;
import cz.nkp.urnnbn.client.tabs.TabsPanel;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;

public class InstitutionsAdminstrationPanel extends SingleTabContentPanel {

	private final UserAccountServiceAsync accountsService = GWT.create(UserAccountService.class);
	private final ConstantsImpl constants = GWT.create(ConstantsImpl.class);
	private Panel activePanel;

	public InstitutionsAdminstrationPanel(TabsPanel superPanel) {
		super(superPanel);
	}

	public void onLoad() {
		showInstitutions();
	}

	public void showInstitutions() {
		detachActivePanel();
		activePanel = new InstitutionListPanel(this, getActiveUser());
		add(activePanel);
	}

	public void showRegistrarDetails(RegistrarDTO registrar) {
		detachActivePanel();
		activePanel = new RegistrarDetailsPanel(this, getActiveUser(), registrar);
		add(activePanel);
	}

	private void detachActivePanel() {
		if (activePanel != null) {
			activePanel.removeFromParent();
		}
		clear();
	}

	@Override
	public void onSelection() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDeselection() {
		// TODO Auto-generated method stub
		
	}
}
