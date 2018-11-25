package cz.nkp.urnnbn.client.insertRecord;

import com.google.gwt.user.client.ui.Panel;
import cz.nkp.urnnbn.client.tabs.SingleTabContentPanel;
import cz.nkp.urnnbn.client.tabs.TabsPanel;

import java.util.logging.Logger;

public class DataInputTab extends SingleTabContentPanel {

    private static final Logger LOGGER = Logger.getLogger(DataInputTab.class.getName());

    Panel actualPanel;

    public DataInputTab(TabsPanel superPanel) {
        super(superPanel, "record_insertion");
    }

    @Override
    public void onLoad() {
        super.onLoad();
        actualPanel = new SelectEntityParametersPanel(this);
        reload();
    }

    public void reload(Panel panel) {
        this.actualPanel = panel;
        reload();
    }

    private void reload() {
        clear();
        add(actualPanel);
    }

    @Override
    public void onSelected() {
        // LOGGER.fine("onSelected");
        super.onSelected();
    }

    @Override
    public void onDeselected() {
        // LOGGER.fine("onDeselected");
        super.onDeselected();
    }
}
