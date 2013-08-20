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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DatePicker;

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
		AsyncCallback<ArrayList<RegistrarDTO>> callback = new AsyncCallback<ArrayList<RegistrarDTO>>() {

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
		};
		if (user.isSuperAdmin()) {
			accountsService.getAllRegistrars(callback);
		} else {
			accountsService.getRegistrarsManagedByUser(callback);
		}
	}

	void reload() {
		clear();
		setText(messages.processPlaning(constants.REGISTRARS_URN_NBN_CSV_EXPORT()));
		setAnimationEnabled(true);
		setWidget(contentPanel());
	}

	private Panel contentPanel() {
		VerticalPanel result = new VerticalPanel();
		result.add(selectDateRangePanel());
		result.add(selectRegistrarPanel());
		result.add(selectModusOfRegistrationPanel());
		result.add(selectTypeOfDocumentPanel());
		result.add(selectAbsenceOfIdentifiers());
		result.add(numberOfDigitalInstanceCheckbox());
		result.add(selectActivationFlag());
		result.add(buttonsPanel());
		result.add(errorLabel);
		return result;
	}
	
	private Panel selectDateRangePanel() {
		HorizontalPanel result = new HorizontalPanel();
		result.setSpacing(5);
		DatePicker begin = new DatePicker();
		result.add(begin);
		DatePicker end = new DatePicker();
		result.add(end);
		return result;
	}

	private Panel selectRegistrarPanel() {
		HorizontalPanel result = new HorizontalPanel();
		result.setSpacing(5);
		result.add(new Label(constants.processUrnNbnExportRegistrar() + SEPARATOR));
		result.add(registrarList());
		return result;
	}
	
	private Panel selectModusOfRegistrationPanel() {
		HorizontalPanel result = new HorizontalPanel();
		final ListBox list = new ListBox(true);
		list.addItem("BY_RESOLVER");
		list.addItem("BY_REGISTRAR");
		list.addItem("BY_RESERVATION");
		result.add(list);
		return result;
	}
	
	private Panel selectTypeOfDocumentPanel() {
		HorizontalPanel result = new HorizontalPanel();
		final ListBox list = new ListBox(true);
		list.addItem("monograph");
		list.addItem("monographVolume");
		list.addItem("periodical");
		list.addItem("periodicalVolume");
		list.addItem("periodicalIssue");
		list.addItem("thesis");
		list.addItem("analytical");
		list.addItem("otherEntity");
		result.add(list);
		return result;
	}
	
	private Panel selectAbsenceOfIdentifiers() {
		HorizontalPanel result = new HorizontalPanel();
		final ListBox list = new ListBox(true);
		list.addItem("CNB");
		list.addItem("ISSN");
		list.addItem("ISBN");
		result.add(list);
		return result;
	}
	
	private Panel numberOfDigitalInstanceCheckbox() {
		HorizontalPanel result = new HorizontalPanel();
		CheckBox checkBox = new CheckBox();
		result.add(checkBox);
		return result;
	}
	
	private Panel selectActivationFlag() {
		HorizontalPanel result = new HorizontalPanel();
		final ListBox list = new ListBox(true);
		list.addItem("ACTIVE");
		list.addItem("INACTIVE");
		list.addItem("ALL");
		result.add(list);
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
		final ListBox result = new ListBox(true);
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
					String begin = null;
					String end = null;
					String registrars = selectedRegistrar.getCode();
					String regMode = null;
					String entityType = null;
					String cnbAssigned = null;
		            String issnAsigned =  null;
		            String isbnAssigned = null;
		            String active = null;
					String[] params = new String[] {
							begin,
							end,
							registrars,
							regMode,
							entityType,
							cnbAssigned,
							issnAsigned,
							isbnAssigned,
							active
					};
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
