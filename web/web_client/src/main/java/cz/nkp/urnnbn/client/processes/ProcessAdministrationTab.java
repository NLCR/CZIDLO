package cz.nkp.urnnbn.client.processes;

import com.google.gwt.user.client.ui.Panel;
import cz.nkp.urnnbn.client.tabs.SingleTabContentPanel;
import cz.nkp.urnnbn.client.tabs.TabsPanel;

import java.util.logging.Logger;

public class ProcessAdministrationTab extends SingleTabContentPanel {

    private static final Logger LOGGER = Logger.getLogger(ProcessAdministrationTab.class.getName());

    private Panel activePanel;


    public ProcessAdministrationTab(TabsPanel superPanel) {
        super(superPanel, "processes");
    }


    @Override
    public void onLoad() {
        //LOGGER.finer("onLoad");
        showProcessAdmin();
    }

    public void showProcessAdmin() {
        detachActivePanel();
        /*if (processPanel == null) {
            processPanel = new ProcessAdministrationPanel(this, getActiveUser());
        }*/
        activePanel = new ProcessAdministrationPanel(this, getActiveUser());
        add(activePanel);
    }

    public void showOaiAdapterConfigPanel() {
        detachActivePanel();
        activePanel = new OaiAdapterConfigPanel(this);
        add(activePanel);
    }

    private void detachActivePanel() {
        if (activePanel != null) {
            activePanel.clear();
            activePanel.removeFromParent();
        }
        clear();
    }

    @Override
    public void onSelected() {
        LOGGER.finer("onSelected");
        super.onSelected();
        /*loadProcesses(false);
        processesRefreshTimer.scheduleRepeating(TIMER_INTERVAL);*/
    }

    @Override
    public void onDeselected() {
        super.onDeselected();
        LOGGER.finer("onDeselected");
        //processesRefreshTimer.cancel();
    }

    public interface Operation {
        void run();
    }

}
