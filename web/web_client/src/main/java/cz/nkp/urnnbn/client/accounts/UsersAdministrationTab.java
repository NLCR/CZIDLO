package cz.nkp.urnnbn.client.accounts;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import cz.nkp.urnnbn.client.services.UserAccountService;
import cz.nkp.urnnbn.client.services.UserAccountServiceAsync;
import cz.nkp.urnnbn.client.tabs.SingleTabContentPanel;
import cz.nkp.urnnbn.client.tabs.TabsPanel;
import cz.nkp.urnnbn.shared.dto.UserDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Logger;

public class UsersAdministrationTab extends SingleTabContentPanel {

    private static final Logger LOGGER = Logger.getLogger(UsersAdministrationTab.class.getName());
    private final UserAccountServiceAsync accountsService = GWT.create(UserAccountService.class);
    private ArrayList<UserDTO> users = new ArrayList<>();

    public UsersAdministrationTab(TabsPanel superPanel) {
        super(superPanel, "accounts");
    }

    @Override
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
                LOGGER.severe("Error loading users: " + caught.getMessage());
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

    private void sort(ArrayList<UserDTO> users) {
        Collections.sort(users, new Comparator<UserDTO>() {
            @Override
            public int compare(UserDTO o1, UserDTO o2) {
                return o1.getLogin().compareTo(o2.getLogin());
            }
        });
    }

    private void reload() {
        sort(users);
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
                new EditUserDialogBox(UsersAdministrationTab.this, user).show();
            }
        });
    }

    private Button accessRights(final UserDTO user) {
        return new Button(constants.rights(), new ClickHandler() {

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
                            Window.alert(messages.serverError(caught.getMessage()));
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
                new AddUserAccountDialogBox(UsersAdministrationTab.this).show();
            }
        });
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
