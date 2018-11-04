package cz.nkp.urnnbn.client.processes;

import cz.nkp.urnnbn.client.processes.mainPanel.ProcessAdminMainPanel;
import cz.nkp.urnnbn.client.processes.oaiAdapterConfigPanel.ProcessAdminOaiAdapterConfigPanel;
import cz.nkp.urnnbn.client.tabs.SingleTabContentPanel;
import cz.nkp.urnnbn.client.tabs.TabsPanel;

import java.util.logging.Logger;

public class ProcessAdministrationTab extends SingleTabContentPanel {

    private static final Logger LOGGER = Logger.getLogger(ProcessAdministrationTab.class.getName());

    private ProcessAdminMainPanel mainPanel;
    private ProcessAdminOaiAdapterConfigPanel oaiAdapterConfigPanel;
    boolean mainPanelSelected = false;

    public ProcessAdministrationTab(TabsPanel superPanel) {
        super(superPanel, "processes");
    }

    @Override
    public void onLoad() {
        LOGGER.finer("onLoad");
        oaiAdapterConfigPanel = new ProcessAdminOaiAdapterConfigPanel(this);
        mainPanel = new ProcessAdminMainPanel(this, getActiveUser());
        //init process admin panel without selecting it and thus acitvating periodical server requests
        add(mainPanel);
        mainPanelSelected = true;
    }

    public void selectProcessAdminPanel() {
        oaiAdapterConfigPanel.onDeselected();
        oaiAdapterConfigPanel.removeFromParent();
        add(mainPanel);
        mainPanel.onSelected();
        mainPanelSelected = true;
    }

    public void selectOaiAdapterConfigPanel() {
        mainPanel.onDeselected();
        mainPanel.removeFromParent();
        add(oaiAdapterConfigPanel);
        oaiAdapterConfigPanel.onSelected();
        mainPanelSelected = false;
    }

    @Override
    public void onSelected() {
        LOGGER.finer("onSelected");
        super.onSelected();
        if (mainPanelSelected) {
            mainPanel.onSelected();
        } else {
            oaiAdapterConfigPanel.onSelected();
        }
    }

    @Override
    public void onDeselected() {
        LOGGER.finer("onDeselected");
        if (mainPanelSelected) {
            mainPanel.onDeselected();
        } else {
            oaiAdapterConfigPanel.onDeselected();
        }
        super.onDeselected();
    }

    public interface Operation {
        void run();
    }

}
