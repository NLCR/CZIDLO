package cz.nkp.urnnbn.client.processes;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

import cz.nkp.urnnbn.client.services.UserAccountService;
import cz.nkp.urnnbn.client.services.UserAccountServiceAsync;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTOType;

public class ExportUrnNbnListProcessDialogBox extends AbstractScheduleProcessDialogBox {

	private final UserAccountServiceAsync accountsService = GWT.create(UserAccountService.class);
	private ArrayList<RegistrarDTO> registrarsOfUser = new ArrayList<RegistrarDTO>();
	private final Label errorLabel = errorLabel(320);
	private RegistrarDTO selectedRegistrar;

	public ExportUrnNbnListProcessDialogBox(UserDTO user) {
		super(user);
		loadRegistrars();
		reload();
		center();
	}

	private void loadRegistrars() {

		// TODO: pro admina bude seznam obsahovat vsechny registratory
		accountsService.registrarsManagedByUser(new AsyncCallback<ArrayList<RegistrarDTO>>() {

			@Override
			public void onSuccess(ArrayList<RegistrarDTO> result) {
				registrarsOfUser = result;
				if (!registrarsOfUser.isEmpty()) {
					selectedRegistrar = registrarsOfUser.get(0);
				}
				reload();
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(constants.serverError() + ": " + caught.getMessage());
			}
		});
	}

	void reload() {
		clear();
		setText(messages.processPlaning(constants.REGISTRARS_URN_NBN_CSV_EXPORT()));
		setAnimationEnabled(true);
		setWidget(contentPanel());
	}

	private Panel contentPanel() {
		VerticalPanel result = new VerticalPanel();
		result.add(selectRegistrarPanel());
		result.add(buttonsPanel());
		result.add(errorLabel);
		return result;
	}

	private Panel selectRegistrarPanel() {
		HorizontalPanel result = new HorizontalPanel();
		result.setSpacing(5);
		result.add(new Label(constants.processUrnNbnExportRegistrar() + SEPARATOR));
		result.add(registrarList());
		return result;
	}

	private Panel buttonsPanel() {
		HorizontalPanel result = new HorizontalPanel();
		result.setSpacing(10);
		result.add(scheduleProcessButton());
		result.add(closeButton());
		return result;
	}

	private ListBox registrarList() {
		final ListBox result = new ListBox();
		for (RegistrarDTO registrar : registrarsOfUser) {
			result.addItem(registrar.getCode());
		}
		result.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				int index = result.getSelectedIndex();
				selectedRegistrar = registrarsOfUser.get(index);
			}
		});
		return result;
	}

	private Button scheduleProcessButton() {
		return new Button(constants.scheduleProcess(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (selectedRegistrar != null) {
					String[] params = new String[] { selectedRegistrar.getCode() };
					processService.scheduleProcess(ProcessDTOType.REGISTRARS_URN_NBN_CSV_EXPORT, params, new AsyncCallback<Void>() {

						@Override
						public void onSuccess(Void result) {
							ExportUrnNbnListProcessDialogBox.this.hide();
						}

						@Override
						public void onFailure(Throwable caught) {
							errorLabel.setText(caught.getMessage());
						}
					});
				}
			}
		});
	}

	private Button closeButton() {
		return new Button(constants.close(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ExportUrnNbnListProcessDialogBox.this.hide();
			}
		});
	}
}
