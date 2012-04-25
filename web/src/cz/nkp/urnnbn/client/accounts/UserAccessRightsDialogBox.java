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

import cz.nkp.urnnbn.client.AbstractDialogBox;
import cz.nkp.urnnbn.client.services.InstitutionsService;
import cz.nkp.urnnbn.client.services.InstitutionsServiceAsync;
import cz.nkp.urnnbn.client.services.UserAccountService;
import cz.nkp.urnnbn.client.services.UserAccountServiceAsync;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO;

public class UserAccessRightsDialogBox extends AbstractDialogBox {

	private final UserAccountServiceAsync accountsService = GWT.create(UserAccountService.class);
	private final InstitutionsServiceAsync institutionsService = GWT.create(InstitutionsService.class);
	private final Label errorLabel = errorLabel(320);
	private final UserDTO user;
	private ArrayList<RegistrarDTO> registrarsOfUser = new ArrayList<RegistrarDTO>();
	private ArrayList<RegistrarDTO> otherRegistrars = new ArrayList<RegistrarDTO>();

	public UserAccessRightsDialogBox(UserDTO user) {
		this.user = user;
		loadRegistrars();
		reload();
		center();
	}

	private void loadRegistrars() {
		accountsService.registrarsManagedByUser(user.getId(), new AsyncCallback<ArrayList<RegistrarDTO>>() {

			@Override
			public void onSuccess(ArrayList<RegistrarDTO> result) {
				registrarsOfUser = result;
				reload();
				loadAllRegistrars();
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(constants.serverError() + ": " + caught.getMessage());
			}
		});
	}

	private void loadAllRegistrars() {
		institutionsService.getAllRegistrars(new AsyncCallback<ArrayList<RegistrarDTO>>() {
			public void onSuccess(ArrayList<RegistrarDTO> result) {
				otherRegistrars = subtract(result, registrarsOfUser);
			}

			public void onFailure(Throwable caught) {
				Window.alert(constants.serverError() + ": " + caught.getMessage());
			}
		});
	}

	private ArrayList<RegistrarDTO> subtract(ArrayList<RegistrarDTO> first, ArrayList<RegistrarDTO> second) {
		ArrayList<RegistrarDTO> result = (ArrayList<RegistrarDTO>) first.clone();
		for (RegistrarDTO toBeRemoved : second) {
			result.remove(toBeRemoved);
		}
		return result;
	}

	void reload() {
		clear();
		// TODO: i18n
		setText("přístupová práva uživatele " + user.getLogin() + " k registrátorům");
		setAnimationEnabled(true);
		setWidget(contentPanel());
	}

	private Panel contentPanel() {
		VerticalPanel result = new VerticalPanel();
		result.add(registrarsGrid());
		result.add(addRightButton());
		result.add(closeButton());
		result.add(errorLabel);
		return result;
	}

	private Button addRightButton() {
		//TODO: i18n
		return new Button("přidat právo", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				new AddRightDialogBox(UserAccessRightsDialogBox.this, user, otherRegistrars).show();
			}
		});
	}

	private Grid registrarsGrid() {
		Grid result = new Grid(registrarsOfUser.size(), 2);
		for (int i = 0; i < registrarsOfUser.size(); i++) {
			RegistrarDTO registrar = registrarsOfUser.get(i);
			Label code = new Label(registrar.getCode());
			result.setWidget(i, 0, code);
			result.setWidget(i, 1, deleteRightButton(registrar));
		}
		return result;
	}

	private Button deleteRightButton(final RegistrarDTO registrar) {
		return new Button(constants.delete(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// TODO: i18n
				if (Window.confirm("opravdu odstranit přístupové právo uživatele " + user.getLogin() + " k registrátorovi "
						+ registrar.getName())) {
					accountsService.deleteRegistrarRight(user.getId(), registrar.getId(), new AsyncCallback<Void>() {

						@Override
						public void onSuccess(Void result) {
							removeRight(registrar);
						}

						@Override
						public void onFailure(Throwable caught) {
							errorLabel.setText(constants.serverError() + ": " + caught.getMessage());
						}
					});
				}
			}
		});
	}

	void removeRight(RegistrarDTO registrar) {
		registrarsOfUser.remove(registrar);
		otherRegistrars.add(registrar);
		reload();
	}

	void addRight(RegistrarDTO registrar) {
		otherRegistrars.remove(registrar);
		registrarsOfUser.add(registrar);
		reload();
	}

	private Button closeButton() {
		return new Button(constants.close(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				UserAccessRightsDialogBox.this.hide();
			}
		});
	}
}
