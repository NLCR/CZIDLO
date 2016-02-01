package cz.nkp.urnnbn.client.tabs;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TabLayoutPanel;

import cz.nkp.urnnbn.client.accounts.UsersAdministrationPanel;
import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.i18n.MessagesImpl;
import cz.nkp.urnnbn.client.insertRecord.DataInputPanel;
import cz.nkp.urnnbn.client.institutions.InstitutionsAdminstrationPanel;
import cz.nkp.urnnbn.client.processes.ProcessAdministrationPanel;
import cz.nkp.urnnbn.client.search.SearchPanel;
import cz.nkp.urnnbn.client.services.UserAccountService;
import cz.nkp.urnnbn.client.services.UserAccountServiceAsync;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO;

public class TabsPanel extends Composite {

    private final ConstantsImpl constants = GWT.create(ConstantsImpl.class);
    private final MessagesImpl messages = GWT.create(MessagesImpl.class);
    private final UserAccountServiceAsync accountsService = GWT.create(UserAccountService.class);
    private final UserDTO activeUser;
    private TabLayoutPanel tabLayoutPanel;
    private ArrayList<RegistrarDTO> registrarsManagedByUser = null;

    public TabsPanel(UserDTO activeUser) {
        this.activeUser = activeUser;
        loadRegistrarsManagedByUser();
        initTabs();
        // activatePanel(3);
        activatePanel(2);
    }

    private void loadRegistrarsManagedByUser() {
        if (activeUser.isLoggedUser()) {
            accountsService.getRegistrarsManagedByUser(new AsyncCallback<ArrayList<RegistrarDTO>>() {

                @Override
                public void onSuccess(ArrayList<RegistrarDTO> result) {
                    registrarsManagedByUser = result;
                }

                @Override
                public void onFailure(Throwable caught) {
                    Window.alert(messages.serverError(caught.getMessage()));
                }
            });
        }
    }

    private void initTabs() {
        // mainResources.tabStyles().ensureInjected();
        tabLayoutPanel = new TabLayoutPanel(1.5, Unit.EM);
        // tabLayoutPanel.setHeight("300px");
        // TODO: znovu povolit animaci, jakmile bude vyresen bug "kyvadlo"
        // tabLayoutPanel.setAnimationDuration(500);
        // tabLayoutPanel.setAnimationVertical(false);

        // // info panel
        // tabLayoutPanel.add(new InfoPanel(this), constants.tabInfoLabel(),
        // false);
        // // search panel
        // String query =
        // com.google.gwt.user.client.Window.Location.getParameter("q");
        // if (query == null || query.isEmpty()) {
        // tabLayoutPanel.add(new SearchPanel(this, null),
        // constants.tabSearchLabel(), false);
        // } else {
        // tabLayoutPanel.add(new SearchPanel(this, query),
        // constants.tabSearchLabel(), false);
        // activatePanel(1);
        // }
        // // institutions panel
        // tabLayoutPanel.add(new InstitutionsAdminstrationPanel(this),
        // constants.tabInstitutionsLabel(), false);
        // // insert record panel
        // if (activeUser.isLoggedUser()) {
        // tabLayoutPanel.add(new DataInputPanel(this),
        // constants.tabDataInputLabel(), false);
        // }
        // // user accounts panel
        // if (activeUser.isSuperAdmin()) {
        // tabLayoutPanel.add(new UsersAdministrationPanel(this),
        // constants.tabAccountManagementLabel(), false);
        // }
        // // TODO: povolit jen prihlasenym uzivatelum
        // // if (activeUser.isLoggedUser()) {
        // tabLayoutPanel.add(new ProcessAdministrationPanel(this),
        // constants.tabProcessesLabel(), false);
        // // }

        PanelsBuilder builder = new PanelsBuilder(tabLayoutPanel);

        // info panel
        builder.appendPanel(new EditableContentPanel(this, "info"), constants.tabInfoLabel());

        // rules panel
        builder.appendPanel(new EditableContentPanel(this, "rules"), constants.tabRulesLabel());

        // search panel
        String query = com.google.gwt.user.client.Window.Location.getParameter("q");
        if (query == null || query.isEmpty()) {
            builder.appendPanel(new SearchPanel(this, null), constants.tabSearchLabel());
        } else {
            builder.appendPanel(new SearchPanel(this, query), constants.tabSearchLabel());
            activatePanel(1);
        }

        // institutions panel
        builder.appendPanel(new InstitutionsAdminstrationPanel(this), constants.tabInstitutionsLabel());

        // TODO: i18n
        builder.appendPanel(new StatisticsTab(this), "Statistics");

        // insert record panel
        if (activeUser.isLoggedUser()) {
            builder.appendPanel(new DataInputPanel(this), constants.tabDataInputLabel());
        }

        // user accounts panel
        if (activeUser.isSuperAdmin()) {
            builder.appendPanel(new UsersAdministrationPanel(this), constants.tabAccountManagementLabel());
        }

        // process administration panel
        if (activeUser.isLoggedUser()) {
            builder.appendPanel(new ProcessAdministrationPanel(this), constants.tabProcessesLabel());
        }

        // TODO: just for testing new features
        // builder.appendPanel(new TestTab(this), "TEST");

        // logs for admin
        if (activeUser.isSuperAdmin()) {
            builder.appendPanel(new LogsTab(this), constants.tabLogsLabel());
        }

        initHistory(tabLayoutPanel);
        initWidget(tabLayoutPanel);
        builder.appendSelectionHandler();
    }

    class PanelsBuilder {

        private TabLayoutPanel rootPanel;
        private ArrayList<SingleTabContentPanel> panels = new ArrayList<SingleTabContentPanel>();

        public PanelsBuilder(TabLayoutPanel rootPanel) {
            this.rootPanel = rootPanel;
        }

        void appendPanel(SingleTabContentPanel panel, String label) {
            rootPanel.add(panel, label, false);
            panels.add(panel);
        }

        void appendSelectionHandler() {
            rootPanel.addSelectionHandler(new SelectionHandler<Integer>() {

                @Override
                public void onSelection(SelectionEvent<Integer> event) {
                    int selected = event.getSelectedItem();
                    if (selected < panels.size()) {
                        SingleTabContentPanel selectedPanel = panels.get(selected);
                        selectedPanel.onSelection();
                    }
                }
            });
        }
    }

    private void initHistory(TabLayoutPanel tabPanel) {
        tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                History.newItem("tab" + event.getSelectedItem());
            }
        });
        History.addValueChangeHandler(new ValueChangeHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                String historyToken = event.getValue();

                // Parse the history token
                try {
                    if (historyToken.startsWith("tab") && historyToken.length() == 4) {
                        String tabIndexToken = historyToken.substring(3, 4);
                        int tabIndex = Integer.parseInt(tabIndexToken);
                        activatePanel(tabIndex);
                    } else {
                        activatePanel(0);
                    }

                } catch (IndexOutOfBoundsException e) {
                    activatePanel(0);
                }
            }
        });
    }

    private void activatePanel(int number) {
        tabLayoutPanel.selectTab(number);
        // tabLayoutPanel.animate(100);
    }

    public ArrayList<RegistrarDTO> getRegistrarsManagedByUser() {
        return registrarsManagedByUser;
    }

    public UserDTO getActiveUser() {
        return activeUser;
    }
}
