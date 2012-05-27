package cz.nkp.urnnbn.client.insertRecord;

import com.google.gwt.user.client.ui.Panel;

import cz.nkp.urnnbn.client.tabs.SingleTabContentPanel;
import cz.nkp.urnnbn.client.tabs.TabsPanel;

public class DataInputPanel extends SingleTabContentPanel {

	Panel actualPanel;

	public DataInputPanel(TabsPanel superPanel) {
		super(superPanel);
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
}
