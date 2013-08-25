package cz.nkp.urnnbn.client.processes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

import cz.nkp.urnnbn.client.forms.Field;
import cz.nkp.urnnbn.client.forms.TextInputValueField;
import cz.nkp.urnnbn.client.services.UserAccountService;
import cz.nkp.urnnbn.client.services.UserAccountServiceAsync;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTOType;
import cz.nkp.urnnbn.shared.validation.DateTimeValidator;

public class ExportUrnNbnListProcessDialogBox extends AbstractScheduleProcessDialogBox {

	private static final String DATE_FORMAT = "d. M. yyyy H:m.s";
	protected DateTimeFormat dateFormat = DateTimeFormat.getFormat(DATE_FORMAT);
	
	private final UserAccountServiceAsync accountsService = GWT.create(UserAccountService.class);
	private ArrayList<RegistrarDTO> registrarsOfUser = new ArrayList<RegistrarDTO>();
	private final Label errorLabel = errorLabel(320);
	
	private MultiSelectListBox absenceOfIdentifiersListBox;
	private MultiSelectListBox registrarsListBox;
	private MultiSelectListBox documentTypeListBox;
	private TextInputValueField beginDate;
	private TextInputValueField endDate;
	private ListBox activationFlag;

	public ExportUrnNbnListProcessDialogBox(UserDTO user) {
		super(user);
		loadRegistrars();
		reload();
		center();
	}

	private void loadRegistrars() {
		AsyncCallback<ArrayList<RegistrarDTO>> callback = new AsyncCallback<ArrayList<RegistrarDTO>>() {

			public void onSuccess(ArrayList<RegistrarDTO> result) {
				registrarsOfUser = result;
				reload();
			}

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
	
	private static class MultiSelectListBox extends ListBox {
		
		public MultiSelectListBox() {
			super(true);
		}
		
		public List<String> getSelectedItems() {
			ArrayList<String> result = new ArrayList<String>();
			for (int i = 0; i < this.getItemCount(); i++) {
		        if (this.isItemSelected(i)) {
		            result.add(this.getItemText(i));
		        }
		    }
			return result;
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
	
	@SuppressWarnings("deprecation")
	private Panel selectDateRangePanel() {
		HorizontalPanel result = new HorizontalPanel();
		beginDate = new TextInputValueField(new DateTimeValidator(DATE_FORMAT), constants.timestampReserved(), "", false);
		Date start = new Date();
		start.setYear(start.getYear() - 2);
		beginDate.getContentWidget().setValue(dateFormat.format(start));
		result.add(beginDate.getLabelWidget());
		result.add(beginDate.getContentWidget());
		endDate = new TextInputValueField(new DateTimeValidator(DATE_FORMAT), "", "", false);
		endDate.getContentWidget().setValue(dateFormat.format(new Date()));
		result.add(endDate.getLabelWidget());
		result.add(endDate.getContentWidget());
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
		result.add(new Label(constants.allowedRegistrationModes()));
		final ListBox list = new ListBox(true);
		list.addItem("BY_RESOLVER");
		list.addItem("BY_REGISTRAR");
		list.addItem("BY_RESERVATION");
		result.add(list);
		return result;
	}
	
	private Panel selectTypeOfDocumentPanel() {
		HorizontalPanel result = new HorizontalPanel();
		result.add(new Label(constants.documentType()));
		documentTypeListBox = new MultiSelectListBox();
		documentTypeListBox.addItem("monograph");
		documentTypeListBox.addItem("monographVolume");
		documentTypeListBox.addItem("periodical");
		documentTypeListBox.addItem("periodicalVolume");
		documentTypeListBox.addItem("periodicalIssue");
		documentTypeListBox.addItem("thesis");
		documentTypeListBox.addItem("analytical");
		documentTypeListBox.addItem("otherEntity");
		result.add(documentTypeListBox);
		return result;
	}
	
	private Panel selectAbsenceOfIdentifiers() {
		HorizontalPanel result = new HorizontalPanel();
		result.add(new Label(constants.absenceOfIdentifiers()));
		absenceOfIdentifiersListBox = new MultiSelectListBox();
		absenceOfIdentifiersListBox.addItem("CNB");
		absenceOfIdentifiersListBox.addItem("ISSN");
		absenceOfIdentifiersListBox.addItem("ISBN");
		result.add(absenceOfIdentifiersListBox);
		return result;
	}
	
	private Panel numberOfDigitalInstanceCheckbox() {
		HorizontalPanel result = new HorizontalPanel();
		result.add(new Label(constants.includeNumberOfDigitalInstances()));
		CheckBox checkBox = new CheckBox();
		result.add(checkBox);
		return result;
	}
	
	private Panel selectActivationFlag() {
		HorizontalPanel result = new HorizontalPanel();
		result.add(new Label(constants.activityFlag()));
		activationFlag = new ListBox();
		activationFlag.addItem("ACTIVE");
		activationFlag.addItem("INACTIVE");
		activationFlag.addItem("ALL");
		result.add(activationFlag);
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
		registrarsListBox = new MultiSelectListBox();
		for (RegistrarDTO registrar : registrarsOfUser) {
			registrarsListBox.addItem(registrar.getCode());
		}
		return registrarsListBox;
	}

	private Button scheduleProcessButton() {
		return new Button(constants.scheduleProcess(), new ClickHandler() {

			public void onClick(ClickEvent event) {
				List<String> idents = absenceOfIdentifiersListBox.getSelectedItems();
				String cnbAssigned = "null";
				String issnAssigned = "null";
				String isbnAssigned = "null";
				if (idents.contains("CNB")) {
					cnbAssigned = "false";
				}
				if (idents.contains("ISSN")) {
					issnAssigned = "false";
				}
				if (idents.contains("ISBN")) {
					isbnAssigned = "false";
				}
				String registrars = null;
				List<String> selectedRegistrars = registrarsListBox.getSelectedItems();
				if (selectedRegistrars.size() > 0) {
					StringBuilder regs = new StringBuilder();
					String sep = "";
					for (String code : selectedRegistrars) {
						regs.append(sep);
						regs.append(code);
						sep = ",";
					}
					registrars = regs.toString();
				}
				String entityType = null;
				List<String> selectedTypes = documentTypeListBox.getSelectedItems();
				if (selectedTypes.size() > 0) {
					StringBuilder types = new StringBuilder();
					String sep = "";
					for (String code : selectedTypes) {
						types.append(sep);
						types.append(code);
						sep = ",";
					}
					entityType = types.toString();
				}
				String begin = (String) beginDate.getInsertedValue();
				String end = (String) endDate.getInsertedValue();
				String active = null;
				String selectedActivationFlag = null;
				if (activationFlag.getSelectedIndex() >= 0) {
					selectedActivationFlag = activationFlag.getItemText(activationFlag.getSelectedIndex());
				}
				if (selectedActivationFlag.equals("ACTIVE")) {
					active = "true";
				} else if (selectedActivationFlag.equals("INACTIVE")) {
					active = "false";
				}
				if (true) {
					String regMode = null;
					String[] params = new String[] {
							begin,
							end,
							registrars,
							regMode,
							entityType,
							cnbAssigned,
							issnAssigned,
							isbnAssigned,
							active
					};
					processService.scheduleProcess(ProcessDTOType.REGISTRARS_URN_NBN_CSV_EXPORT, params, new AsyncCallback<Void>() {

						public void onSuccess(Void result) {
							ExportUrnNbnListProcessDialogBox.this.hide();
						}

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

			public void onClick(ClickEvent event) {
				ExportUrnNbnListProcessDialogBox.this.hide();
			}
		});
	}
}
