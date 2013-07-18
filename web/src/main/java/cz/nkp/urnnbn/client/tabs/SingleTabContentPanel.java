package cz.nkp.urnnbn.client.tabs;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ScrollPanel;

import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.i18n.MessagesImpl;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO;

public abstract class SingleTabContentPanel extends ScrollPanel {

	private final TabsPanel superPanel;
	protected final ConstantsImpl constants = GWT.create(ConstantsImpl.class);
	protected final MessagesImpl messages = GWT.create(MessagesImpl.class);
	
	public SingleTabContentPanel(TabsPanel superPanel) {
		super();
		this.superPanel = superPanel;
	}

	public boolean userManagesRegistrar(RegistrarDTO registrar) {
		return getActiveUser().isLoggedUser() && superPanel.getRegistrarsManagedByUser().contains(registrar);
	}

	public ArrayList<RegistrarDTO> getRegistrarsManagedByUser() {
		return superPanel.getRegistrarsManagedByUser();
	}

	public UserDTO getActiveUser() {
		return superPanel.getActiveUser();
	}
	
	public abstract void onSelection();
	
	public abstract void onDeselection();
	
}
