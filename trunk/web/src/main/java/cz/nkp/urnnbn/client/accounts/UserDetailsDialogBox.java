package cz.nkp.urnnbn.client.accounts;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

import cz.nkp.urnnbn.client.AbstractDialogBox;
import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO.ROLE;

public class UserDetailsDialogBox extends AbstractDialogBox {
	private final UserDTO user;

	public UserDetailsDialogBox(UserDTO user) {
		this.user = user;
		String title = constants.user() + " - " + constants.details();
		setTitle(title);
		setText(title);
		setAnimationEnabled(true);
		setWidget(contentPanel());
		center();
	}

	private Panel contentPanel() {
		VerticalPanel panel = new VerticalPanel();
		Grid grid = new Grid(determineRows(), 2);
		// TODO: i18n
		grid.setWidget(0, 0, new Label(constants.login() + ':'));
		grid.setWidget(0, 1, new Label(user.getLogin()));
		grid.setWidget(1, 0, new Label(constants.email() + ':'));
		grid.setWidget(1, 1, new Label(user.getEmail()));
		grid.setWidget(2, 0, new Label(constants.administrator() + ':'));
		boolean superAdim = user.getRole() == ROLE.SUPER_ADMIN;
		grid.setWidget(2, 1, new Label(superAdim ? constants.yes() : constants.no()));
		if (user.getCreated() != null) {
			grid.setWidget(3, 0, new Label(constants.created() + ':'));
			grid.setWidget(3, 1, new Label(user.getCreated()));
			if (user.getModified() != null && !user.getModified().equals(user.getCreated())) {
				grid.setWidget(4, 0, new Label(constants.modified() + ':'));
				grid.setWidget(4, 1, new Label(user.getModified()));
			}
		}
		panel.add(grid);
		panel.add(closeButton());
		return panel;
	}

	private int determineRows() {
		int result = 3;
		if (user.getCreated() != null) {
			result++;
			if (user.getModified() != null && !user.getModified().equals(user.getCreated())) {
				result++;
			}
		}
		return result;
	}

	private Button closeButton() {
		return new Button(constants.close(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				UserDetailsDialogBox.this.hide();
			}
		});
	}
}
