package cz.nkp.urnnbn.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import cz.nkp.urnnbn.client.accounts.UsersAdministrationPanel;
import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.institutions.InstitutionsAdminstrationPanel;
import cz.nkp.urnnbn.client.search.SearchPanel;
import cz.nkp.urnnbn.shared.dto.UserDTO;

public class Panels extends Composite {

	private ConstantsImpl constants = GWT.create(ConstantsImpl.class);
	private TabLayoutPanel tabLayoutPanel;

	private final UserDTO user;

	public Panels(UserDTO user) {
		this.user = user;
		// mainResources.tabStyles().ensureInjected();
		tabLayoutPanel = new TabLayoutPanel(1.5, Unit.EM);
		tabLayoutPanel.setAnimationDuration(500);
		tabLayoutPanel.setAnimationVertical(false);

		// test panel - comment before building
		// tabLayoutPanel.add(new TestPanel(), "test", false);

		// info panel
		tabLayoutPanel.add(new InfoPanel(), constants.tabInfoLabel(), false);

		// search panel
		String query = com.google.gwt.user.client.Window.Location.getParameter("q");
		if (query == null || query.isEmpty()) {
			tabLayoutPanel.add(new SearchPanel(null, user), constants.tabSearchLabel(), false);
		} else {
			tabLayoutPanel.add(new SearchPanel(query, user), constants.tabSearchLabel(), false);
			activatePanel(1);
		}

		// institutions panel
		tabLayoutPanel.add(new InstitutionsAdminstrationPanel(user), constants.tabInstitutionsLabel(), false);

		// TODO: dodelat import zaznamu
		// if (user.isLoggedUser()) {
		//tabLayoutPanel.add(new DataInputPanel(), constants.tabDataInputLabel(), false);
		// }

		if (user.isSuperAdmin()) {
			tabLayoutPanel.add(new UsersAdministrationPanel(user), constants.tabAccountManagementLabel(), false);
		}

		// TODO: napoveda
		VerticalPanel help = new VerticalPanel();
		tabLayoutPanel.add(help, constants.tabHelpLabel(), false);
		initHistory(tabLayoutPanel);
		initWidget(tabLayoutPanel);
		activatePanel(1);
		// activatePanel(1);
		// activatePanel(2);
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

	public void activatePanel(int number) {
		tabLayoutPanel.selectTab(number);
		// tabLayoutPanel.animate(100);
	}

}
