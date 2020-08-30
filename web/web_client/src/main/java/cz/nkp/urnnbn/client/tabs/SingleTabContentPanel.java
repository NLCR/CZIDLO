package cz.nkp.urnnbn.client.tabs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ScrollPanel;
import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.i18n.MessagesImpl;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO;

import java.util.ArrayList;

public abstract class SingleTabContentPanel extends ScrollPanel {

    protected final ConstantsImpl constants = GWT.create(ConstantsImpl.class);
    protected final MessagesImpl messages = GWT.create(MessagesImpl.class);
    private final String gaTabName;
    protected final TabsPanel tabsPanel;

    public SingleTabContentPanel(TabsPanel tabsPanel, String gaTabName) {
        super();
        this.tabsPanel = tabsPanel;
        this.gaTabName = gaTabName;
    }

    public boolean userManagesRegistrar(RegistrarDTO registrar) {
        return getActiveUser().isLoggedUser() && tabsPanel.getRegistrarsManagedByUser().contains(registrar);
    }

    public ArrayList<RegistrarDTO> getRegistrarsManagedByUser() {
        return tabsPanel.getRegistrarsManagedByUser();
    }

    public UserDTO getActiveUser() {
        return tabsPanel.getActiveUser();
    }

    /**
     * Implemantations must always call super.onSelected() preferably as first command.
     */
    public void onSelected() {
        if (tabsPanel.isGaEnabled()) {
            gaPageEvent("tab_" + gaTabName);
        }
    }

    public void onDeselected() {
        //nothing here, but not abstract method to be consistent with onSelected
    }

    private native void gaPageEvent(String tabName) /*-{
                                                    $wnd.ga('send', 'pageview',tabName);
                                                    }-*/;

}
