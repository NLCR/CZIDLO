package cz.nkp.urnnbn.client.processes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

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
	private CheckBox numberOfDigitalInstances;

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
				Collections.sort(registrarsOfUser, new Comparator<RegistrarDTO>() {
					@Override
					public int compare(RegistrarDTO o1, RegistrarDTO o2) {
						return o1.getCode().compareTo(o2.getCode());
					}
				});
				reload();
			}

			public void onFailure(Throwable caught) {
				Window.alert(messages.serverError(caught.getMessage()));
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
		result.add(selectRegistrarsPanel());
		result.add(selectTypeOfDocumentPanel());
		result.add(selectAbsenceOfIdentifiers());
		result.add(numberOfDigitalInstancesCheckbox());
		result.add(selectActivationFlag());
		result.add(buttonsPanel());
		result.add(errorLabel);
		return result;
	}

	@SuppressWarnings("deprecation")
	private Panel selectDateRangePanel() {
		HorizontalPanel result = new HorizontalPanel();
		Date start = new Date();
		start.setYear(start.getYear() - 2);
		beginDate = new TextInputValueField(new DateTimeValidator(DATE_FORMAT), constants.timestampRegistered(), dateFormat.format(start),
				false);
		result.add(beginDate.getLabelWidget());
		result.add(beginDate.getContentWidget());
		endDate = new TextInputValueField(new DateTimeValidator(DATE_FORMAT), "", dateFormat.format(new Date()), false);
		result.add(new HTML(" - "));
		result.add(endDate.getContentWidget());
		return result;
	}

	private Panel selectRegistrarsPanel() {
		HorizontalPanel result = new HorizontalPanel();
		result.add(new Label(constants.processUrnNbnExportRegistrar() + SEPARATOR));
		result.add(registrarList());
		return result;
	}

	private Panel selectTypeOfDocumentPanel() {
		HorizontalPanel result = new HorizontalPanel();
		result.add(new Label(constants.documentType() + SEPARATOR));
		documentTypeListBox = new MultiSelectListBox();
		documentTypeListBox.addItem("MONOGRAPH");
		documentTypeListBox.addItem("MONOGRAPH_VOLUME");
		documentTypeListBox.addItem("PERIODICAL");
		documentTypeListBox.addItem("PERIODICAL_VOLUME");
		documentTypeListBox.addItem("PERIODICAL_ISSUE");
		documentTypeListBox.addItem("THESIS");
		documentTypeListBox.addItem("ANALYTICAL");
		documentTypeListBox.addItem("OTHER");
		result.add(documentTypeListBox);
		return result;
	}

	private Panel selectAbsenceOfIdentifiers() {
		HorizontalPanel result = new HorizontalPanel();
		result.add(new Label(constants.absenceOfIdentifiers() + SEPARATOR));
		absenceOfIdentifiersListBox = new MultiSelectListBox();
		absenceOfIdentifiersListBox.addItem("CNB");
		absenceOfIdentifiersListBox.addItem("ISSN");
		absenceOfIdentifiersListBox.addItem("ISBN");
		result.add(absenceOfIdentifiersListBox);
		return result;
	}

	private Panel numberOfDigitalInstancesCheckbox() {
		HorizontalPanel result = new HorizontalPanel();
		result.add(new Label(constants.includeNumberOfDigitalInstances() + SEPARATOR));
		numberOfDigitalInstances = new CheckBox();
		result.add(numberOfDigitalInstances);
		return result;
	}

	private Panel selectActivationFlag() {
		HorizontalPanel result = new HorizontalPanel();
		result.add(new Label(constants.activityFlag() + SEPARATOR));
		activationFlag = new ListBox();
		activationFlag.addItem(constants.activityAll());
		activationFlag.addItem(constants.activityActiveOnly());
		activationFlag.addItem(constants.activityDeactivatedOnly());
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
				// TODO: i18n
				List<String> idents = absenceOfIdentifiersListBox.getSelectedItems();

				Boolean missingCnb = idents.contains("CNB");
				Boolean missingIssn = idents.contains("ISSN");
				Boolean missingIsbn = idents.contains("ISSN");

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
				String entityTypes = null;
				List<String> selectedEntityTypes = documentTypeListBox.getSelectedItems();
				if (selectedEntityTypes.size() > 0) {
					StringBuilder types = new StringBuilder();
					String sep = "";
					for (String code : selectedEntityTypes) {
						types.append(sep);
						types.append(code);
						sep = ",";
					}
					entityTypes = types.toString();
				}

				String begin = (String) beginDate.getInsertedValue();
				String end = (String) endDate.getInsertedValue();

				int activitySelectedIndex = activationFlag.getSelectedIndex();
				Boolean returnActive = activitySelectedIndex == 0 || activitySelectedIndex == 1;
				Boolean returnDeactivated = activitySelectedIndex == 0 || activitySelectedIndex == 2;
				Boolean exportNumberOfDigitalInstances = numberOfDigitalInstances.getValue();
				if (true) {
					String[] params = new String[] { begin, end, registrars, entityTypes, missingCnb.toString(), missingIssn.toString(),
							missingIsbn.toString(), returnActive.toString(), returnDeactivated.toString(),
							exportNumberOfDigitalInstances.toString() };
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
