package cz.nkp.urnnbn.client.accounts.current;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import cz.nkp.urnnbn.client.AbstractDialogBox;
import cz.nkp.urnnbn.shared.dto.UserDTO;

public class CurrentUserDetailsDialogBox extends AbstractDialogBox {

    private final UserDTO user;
    private final String logoutPage;

    public CurrentUserDetailsDialogBox(UserDTO user, String logoutPage) {
        this.user = user;
        this.logoutPage = logoutPage;
        String title = constants.user() + " - " + constants.details();
        setTitle(title);
        setText(title);
        setAnimationEnabled(true);
        setWidget(buildWidget());
        center();
    }

    private Widget buildWidget() {
        VerticalPanel panel = new VerticalPanel();
        panel.add(contentPanel());
        panel.add(buttons());
        return panel;
    }

    private IsWidget contentPanel() {
        VerticalPanel panel = new VerticalPanel();
        panel.setHeight("140px");
        panel.setWidth("340px");
        panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        panel.add(buidGrid());
        return panel;
    }

    private IsWidget buidGrid() {
        Grid grid = new Grid(determineRows(), 2);
        grid.setWidget(0, 0, new Label(constants.login() + ':'));
        grid.setWidget(0, 1, new Label(user.getLogin()));
        grid.setWidget(1, 0, new Label(constants.email() + ':'));
        grid.setWidget(1, 1, new Label(user.getEmail()));
        grid.setWidget(2, 0, new Label(constants.administrator() + ':'));
        boolean superAdim = user.getRole() == UserDTO.ROLE.SUPER_ADMIN;
        grid.setWidget(2, 1, new Label(superAdim ? constants.yes() : constants.no()));
        if (user.getCreated() != null) {
            grid.setWidget(3, 0, new Label(constants.created() + ':'));
            grid.setWidget(3, 1, new Label(user.getCreated()));
            if (user.getModified() != null && !user.getModified().equals(user.getCreated())) {
                grid.setWidget(4, 0, new Label(constants.modified() + ':'));
                grid.setWidget(4, 1, new Label(user.getModified()));
            }
        }
        return grid;
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

    private Panel buttons() {
        HorizontalPanel result = new HorizontalPanel();
        result.setWidth("100%");
        result.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        result.add(buttonContainer());
        return result;
    }

    private IsWidget buttonContainer() {
        HorizontalPanel result = new HorizontalPanel();
        result.add(closeButton());
        result.add(logoutButton());
        result.add(changePasswordButton());
        return result;
    }

    private Button changePasswordButton() {
        return new Button(constants.changePasswordButton(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                CurrentUserDetailsDialogBox.this.hide();
                new ChangeUserPasswordDialogBox(user).show();
            }
        });
    }

    private Button logoutButton() {
        return new Button(constants.logoutButton(), new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                Window.Location.assign(logoutPage);
            }
        });
    }

    private Button closeButton() {
        return new Button(constants.close(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                CurrentUserDetailsDialogBox.this.hide();
            }
        });
    }
}
