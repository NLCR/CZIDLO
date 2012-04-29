package cz.nkp.urnnbn.client.accounts;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.i18n.MessagesImpl;
import cz.nkp.urnnbn.client.services.UserAccountService;
import cz.nkp.urnnbn.client.services.UserAccountServiceAsync;
import cz.nkp.urnnbn.client.tabs.SingleTabContentPanel;
import cz.nkp.urnnbn.client.tabs.TabsPanel;
import cz.nkp.urnnbn.shared.dto.UserDTO;

public class UsersAdministrationPanel extends SingleTabContentPanel {

	private final ConstantsImpl constants = GWT.create(ConstantsImpl.class);
	private final MessagesImpl messages = GWT.create(MessagesImpl.class);
	private final UserAccountServiceAsync accountsService = GWT.create(UserAccountService.class);
	private ArrayList<UserDTO> users = new ArrayList<UserDTO>();

	public UsersAdministrationPanel(TabsPanel superPanel) {
		super(superPanel);
	}

	public void onLoad() {
		loadUsers();
		reload();
	}

	private void loadUsers() {
		accountsService.getAllUsers(new AsyncCallback<ArrayList<UserDTO>>() {

			@Override
			public void onSuccess(ArrayList<UserDTO> result) {
				users = result;
				reload();
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(constants.serverError() + ": " + caught.getMessage());
			}
		});
	}

	void removeUser(UserDTO user) {
		users.remove(user);
		reload();
	}

	void addUser(UserDTO user) {
		users.add(user);
		reload();
	}

	void updateUser(UserDTO user) {
		users.remove(user);
		users.add(user);
		reload();
	}

	private void reload() {
		clear();
		add(contentPanel());
	}

	private Panel contentPanel() {
		VerticalPanel result = new VerticalPanel();
		// result.setStyleName(css.block());
		result.add(heading());
		result.add(grid());
		result.add(addUserButton());
		return result;
	}

	private Label heading() {
		Label label = new Label(constants.userAccountList());
		// label.addStyleName(css.listHeading());
		return label;
	}

	private Grid grid() {
		Grid result = new Grid(users.size(), 5);
		for (int i = 0; i < users.size(); i++) {
			UserDTO user = users.get(i);
			Label name = new Label(user.getLogin());
			result.setWidget(i, 0, name);
			result.setWidget(i, 1, userDetailsButton(user));
			result.setWidget(i, 2, userEditButton(user));
			result.setWidget(i, 3, accessRights(user));
			result.setWidget(i, 4, userDeleteButton(user));
		}
		return result;
	}

	private Button userDetailsButton(final UserDTO user) {
		return new Button(constants.details(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				new UserDetailsDialogBox(user).show();
			}
		});
	}

	private Widget userEditButton(final UserDTO user) {
		return new Button(constants.edit(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				new EditUserDialogBox(UsersAdministrationPanel.this, user).show();
			}
		});
	}

	private Button accessRights(final UserDTO user) {
		// TODO: i18n
		return new Button("práva", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				new UserAccessRightsDialogBox(user).show();
			}
		});
	}

	private Button userDeleteButton(final UserDTO user) {
		return new Button(constants.delete(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (Window.confirm(messages.confirmDeleteUser(user.getLogin()))) {
					accountsService.deleteUser(user.getId(), new AsyncCallback<Void>() {

						@Override
						public void onSuccess(Void result) {
							removeUser(user);
						}

						@Override
						public void onFailure(Throwable caught) {
							Window.alert(constants.serverError() + ": " + caught.getMessage());
						}
					});
				}
			}
		});
	}

	private Button addUserButton() {
		return new Button(constants.add(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				new AddUserAccountDialogBox(UsersAdministrationPanel.this).show();
			}
		});
	}

}
