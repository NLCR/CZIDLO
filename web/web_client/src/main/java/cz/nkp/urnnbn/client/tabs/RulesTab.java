package cz.nkp.urnnbn.client.tabs;

import java.util.logging.Logger;

public class RulesTab extends EditableContentTab {

    private static final Logger LOGGER = Logger.getLogger(RulesTab.class.getName());
    private static final String CONTENT_NAME = "rules";

    public RulesTab(TabsPanel tabsPanel) {
        super(tabsPanel, CONTENT_NAME, "rules");
    }

}
