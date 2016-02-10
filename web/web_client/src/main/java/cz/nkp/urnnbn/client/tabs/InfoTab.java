package cz.nkp.urnnbn.client.tabs;

import java.util.logging.Logger;

public class InfoTab extends EditableContentTab {

    private static final Logger LOGGER = Logger.getLogger(InfoTab.class.getName());
    private static final String CONTENT_NAME = "info";

    public InfoTab(TabsPanel tabsPanel) {
        super(tabsPanel, CONTENT_NAME, "info");
    }

}
