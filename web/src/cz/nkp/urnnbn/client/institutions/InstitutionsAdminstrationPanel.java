package cz.nkp.urnnbn.client.institutions;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;

import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.services.UserAccountService;
import cz.nkp.urnnbn.client.services.UserAccountServiceAsync;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO;

public class InstitutionsAdminstrationPanel extends ScrollPanel {

	private final UserAccountServiceAsync accountsService = GWT.create(UserAccountService.class);
	private final ConstantsImpl constants = GWT.create(ConstantsImpl.class);
	private final UserDTO user;
	private Panel activePanel;
	private ArrayList<RegistrarDTO> registrarsManagedByUser = new ArrayList<RegistrarDTO>(0);

	public InstitutionsAdminstrationPanel(UserDTO user) {
		this.user = user;
	}

	public void onLoad() {
		if (user.isLoggedUser()) {
			accountsService.registrarsManagedByUser(new AsyncCallback<ArrayList<RegistrarDTO>>() {

				@Override
				public void onSuccess(ArrayList<RegistrarDTO> result) {
					registrarsManagedByUser = result;
					showInstitutions();
				}

				@Override
				public void onFailure(Throwable caught) {
					Window.alert(constants.serverError() + ": " + caught.getMessage());
				}
			});
		} else {
			showInstitutions();
		}
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

	public boolean userManagesRegistrar(RegistrarDTO registrar){
		return registrarsManagedByUser.contains(registrar);
	}
}
