package cz.nkp.urnnbn.client.insertRecord;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class DataInputPanel extends ScrollPanel {

	Panel actualPanel;

	@Override
	public void onLoad() {
		super.onLoad();
		actualPanel = new SelectEntityTypePanel(this);
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
