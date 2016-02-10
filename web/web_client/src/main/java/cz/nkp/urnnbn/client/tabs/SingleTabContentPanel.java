package cz.nkp.urnnbn.client.tabs;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ScrollPanel;

import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.i18n.MessagesImpl;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO;

public abstract class SingleTabContentPanel extends ScrollPanel {

    private final String gaTabName;
    private final TabsPanel superPanel;
    protected final ConstantsImpl constants = GWT.create(ConstantsImpl.class);
    protected final MessagesImpl messages = GWT.create(MessagesImpl.class);

    public SingleTabContentPanel(TabsPanel superPanel, String gaTabName) {
        super();
        this.superPanel = superPanel;
        this.gaTabName = gaTabName;
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

    /**
     * Implemantations must allways call super.onSelected() preferably as first command.
     */
    public void onSelected() {
        // TODO: only if GA enabled
        gaPageEvent("tab_" + gaTabName);
    }

    public abstract void onDeselected();

    private native void gaPageEvent(String tabName) /*-{
                                                    console.log("onLoad: " + tabName);
                                                    $wnd.ga('send', 'pageview',tabName);
                                                    }-*/;

}
