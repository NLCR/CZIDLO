package cz.nkp.urnnbn.client.tabs;

import com.google.gwt.user.client.ui.ScrollPanel;

import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO;

public abstract class SingleTabContentPanel extends ScrollPanel {

	private final TabsPanel superPanel;

	public SingleTabContentPanel(TabsPanel superPanel) {
		super();
		this.superPanel = superPanel;
	}

	public boolean userManagesRegistrar(RegistrarDTO registrar){
		return superPanel.userManagesRegistrar(registrar);
	}
	
	public UserDTO getActiveUser(){
		return superPanel.getActiveUser();
	}
}
