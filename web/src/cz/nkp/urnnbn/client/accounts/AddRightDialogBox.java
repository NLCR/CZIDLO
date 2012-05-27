package cz.nkp.urnnbn.client.accounts;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

import cz.nkp.urnnbn.client.AbstractDialogBox;
import cz.nkp.urnnbn.client.services.UserAccountService;
import cz.nkp.urnnbn.client.services.UserAccountServiceAsync;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO;

public class AddRightDialogBox extends AbstractDialogBox {

	private final UserAccountServiceAsync accountsService = GWT.create(UserAccountService.class);
	private final ArrayList<RegistrarDTO> otherRegistrars;
	private final UserAccessRightsDialogBox superDialogBox;
	private final UserDTO user;
	private final Label errorLabel = errorLabel(320);
	private RegistrarDTO selectedRegistrar;

	public AddRightDialogBox(UserAccessRightsDialogBox superDialogBox, UserDTO user, ArrayList<RegistrarDTO> otherRegistrars) {
		this.superDialogBox = superDialogBox;
		this.user = user;
		this.otherRegistrars = otherRegistrars;
		if (!otherRegistrars.isEmpty()) {
			selectedRegistrar = otherRegistrars.get(0);
		}
		setText(messages.registrarsAccessRigths(user.getLogin()) + " - " + constants.recordInsertion());
		setAnimationEnabled(true);
		setWidget(contentPanel());
		center();
	}

	private Panel contentPanel() {
		VerticalPanel result = new VerticalPanel();
		result.add(heading());
		result.add(registrarRightPanel());
		result.add(closeButton());
		result.add(errorLabel);
		return result;
	}

	private Panel registrarRightPanel() {
		HorizontalPanel result = new HorizontalPanel();
		result.add(registrarList());
		result.add(addRightButton());
		return result;
	}

	private Label heading() {
		// TODO: style
		return new Label(constants.selectRegistrarCode());
	}

	private ListBox registrarList() {
		final ListBox result = new ListBox();
		for (RegistrarDTO registrar : otherRegistrars) {
			result.addItem(registrar.getCode());
		}
		result.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				int index = result.getSelectedIndex();
				selectedRegistrar = otherRegistrars.get(index);
			}
		});
		return result;
	}

	private Button addRightButton() {
		return new Button(constants.add(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				accountsService.insertRegistrarRight(user.getId(), selectedRegistrar.getId(), new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						superDialogBox.addRight(selectedRegistrar);
						AddRightDialogBox.this.hide();
					}

					@Override
					public void onFailure(Throwable caught) {
						errorLabel.setText(constants.serverError() + ": " + caught.getMessage());
					}
				});
			}
		});
	}

	private Button closeButton() {
		return new Button(constants.close(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				AddRightDialogBox.this.hide();
			}
		});
	}
}
