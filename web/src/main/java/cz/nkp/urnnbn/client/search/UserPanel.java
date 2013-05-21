package cz.nkp.urnnbn.client.search;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;

public class UserPanel extends Composite {
	public UserPanel() {
		
		AbsolutePanel absolutePanel = new AbsolutePanel();
		initWidget(absolutePanel);
		
		Button btnNewButton = new Button("New button");
		absolutePanel.add(btnNewButton, 163, 108);
		
		Label label = new Label("Uzivatel mzkAdmin");
		absolutePanel.add(label, 113, 61);
	}
}
