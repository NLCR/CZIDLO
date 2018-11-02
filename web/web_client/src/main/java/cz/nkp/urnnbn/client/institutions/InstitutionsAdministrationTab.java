package cz.nkp.urnnbn.client.institutions;

import com.google.gwt.user.client.ui.Panel;
import cz.nkp.urnnbn.client.tabs.SingleTabContentPanel;
import cz.nkp.urnnbn.client.tabs.TabsPanel;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;

import java.util.logging.Logger;

public class InstitutionsAdministrationTab extends SingleTabContentPanel {

    private static final Logger LOGGER = Logger.getLogger(InstitutionsAdministrationTab.class.getName());
    private Panel activePanel;

    public InstitutionsAdministrationTab(TabsPanel superPanel) {
        super(superPanel, "registrars");
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
    public void onSelected() {
        //LOGGER.fine("onSelected");
        super.onSelected();
    }

    @Override
    public void onDeselected() {
        //LOGGER.fine("onDeSelected");
        super.onDeselected();
    }
}
