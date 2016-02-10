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

import cz.nkp.urnnbn.client.accounts.UsersAdministrationTab;
import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.i18n.MessagesImpl;
import cz.nkp.urnnbn.client.insertRecord.DataInputTab;
import cz.nkp.urnnbn.client.institutions.InstitutionsAdminstrationTab;
import cz.nkp.urnnbn.client.processes.ProcessAdministrationTab;
import cz.nkp.urnnbn.client.search.SearchTab;
import cz.nkp.urnnbn.client.services.UserAccountService;
import cz.nkp.urnnbn.client.services.UserAccountServiceAsync;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO;

public class TabsPanel extends Composite {

    private static final int DEFAULT_SELECTED_TAB_POSITION = 2;
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
        selectTab(DEFAULT_SELECTED_TAB_POSITION);
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

        PanelsBuilder builder = new PanelsBuilder(tabLayoutPanel);

        // test tab
        // builder.appendPanel(new TestTab(this), "TEST");

        // info tab
        builder.appendPanel(new InfoTab(this), constants.tabInfoLabel());

        // rules tab
        builder.appendPanel(new RulesTab(this), constants.tabRulesLabel());

        // search tab
        String query = com.google.gwt.user.client.Window.Location.getParameter("q");
        if (query == null || query.isEmpty()) {
            builder.appendPanel(new SearchTab(this, null), constants.tabSearchLabel());
        } else {
            builder.appendPanel(new SearchTab(this, query), constants.tabSearchLabel());
            selectTab(1);
        }

        // institutions tab
        builder.appendPanel(new InstitutionsAdminstrationTab(this), constants.tabInstitutionsLabel());

        // statistics tab
        builder.appendPanel(new StatisticsTab(this), constants.tabStatisticsLabel());

        // insert record tab (logged users only)
        if (activeUser.isLoggedUser()) {
            builder.appendPanel(new DataInputTab(this), constants.tabDataInputLabel());
        }

        // user accounts tab (admin only)
        if (activeUser.isSuperAdmin()) {
            builder.appendPanel(new UsersAdministrationTab(this), constants.tabAccountManagementLabel());
        }

        // process administration tab (logged users only)
        if (activeUser.isLoggedUser()) {
            builder.appendPanel(new ProcessAdministrationTab(this), constants.tabProcessesLabel());
        }

        // logss tab (admin only)
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
                        selectedPanel.onSelected();
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
                        selectTab(tabIndex);
                    } else {
                        selectTab(0);
                    }

                } catch (IndexOutOfBoundsException e) {
                    selectTab(0);
                }
            }
        });
    }

    private void selectTab(int number) {
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
