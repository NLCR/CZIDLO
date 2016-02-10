package cz.nkp.urnnbn.client.tabs;

import java.util.logging.Logger;

public class TestTab extends SingleTabContentPanel {

    private static final Logger LOGGER = Logger.getLogger(TestTab.class.getName());

    public TestTab(TabsPanel superPanel) {
        super(superPanel, "test");
    }

    @Override
    public void onSelected() {
    }

    @Override
    public void onDeselected() {
    }

    public void onLoad() {
        LOGGER.info("onSelected");
        super.onSelected();
        reload();
    }

    private void reload() {

    }

}
