package cz.nkp.urnnbn.client.processes.mainPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import cz.nkp.urnnbn.client.forms.TextInputValueField;
import cz.nkp.urnnbn.client.services.UserAccountService;
import cz.nkp.urnnbn.client.services.UserAccountServiceAsync;
import cz.nkp.urnnbn.client.validation.DateTimeValidator;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTOType;

import java.util.*;
import java.util.logging.Logger;

public class ScheduleProcessExportUrnNbnListDialogBox extends AbstractScheduleProcessDialogBox {

    private static final Logger LOGGER = Logger.getLogger(ScheduleProcessExportUrnNbnListDialogBox.class.getName());
    private static final String[] ENTITY_TYPES = {"MONOGRAPH", "MONOGRAPH_VOLUME", "PERIODICAL", "PERIODICAL_VOLUME", "PERIODICAL_ISSUE", "THESIS", "ANALYTICAL", "OTHER"};
    private static final String DATE_FORMAT = "d. M. yyyy";

    private DateTimeFormat dateFormat = DateTimeFormat.getFormat(DATE_FORMAT);

    private final UserAccountServiceAsync accountsService = GWT.create(UserAccountService.class);
    private ArrayList<RegistrarDTO> registrarsOfUser = new ArrayList<RegistrarDTO>();
    private final Label errorLabel = errorLabel(320);

    private CheckBox filterByRegistrationDate;
    private TextInputValueField registrationStartDate;
    private TextInputValueField registrationEndDate;

    private CheckBox filterByRegistrar;
    private MultiSelectListBox registrarsListBox;

    private CheckBox filterByIeType;
    private MultiSelectListBox ieTypesListBox;

    private CheckBox filterByAbsentIdentifiers;
    private MultiSelectListBox absenceOfIdentifiersListBox;

    private CheckBox filterByState;
    private ListBox stateListBox;

    private Panel filterByDeactivationDateContainer = new VerticalPanel();
    private CheckBox filterByDeactivationDate;
    private TextInputValueField deactivationStartDate;
    private TextInputValueField deactivationEndDate;

    private CheckBox includeNumberOfDigitalInstances;

    public ScheduleProcessExportUrnNbnListDialogBox(UserDTO user) {
        super(user);
        loadRegistrars();
        reload();
    }

    @Override
    public void open() {
        reload();
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

    void reload() {
        clear();
        setText(messages.processPlaning(constants.REGISTRARS_URN_NBN_CSV_EXPORT()));
        setAnimationEnabled(true);
        setWidget(contentPanel());
        center();
    }

    private Panel contentPanel() {
        VerticalPanel result = new VerticalPanel();
        result.add(filterByRegistrationDatePanel());
        result.add(filterByRegistrars());
        result.add(filterByEntityTypes());
        result.add(filterByAbsentIdentifier());
        result.add(filterByState());
        result.add(filterByDeactivationDateContainer);
        result.add(new HTML("<hr>"));
        result.add(includeNumberOfDigitalInstancesCheckbox());
        result.add(new HTML("<br>"));
        result.add(buttonsPanel());
        result.add(errorLabel);
        return result;
    }

    private Panel filterByRegistrationDatePanel() {
        VerticalPanel result = new VerticalPanel();

        filterByRegistrationDate = new CheckBox(constants.processUrnNbnExportFilterByRegistrationDate());
        filterByRegistrationDate.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                if (valueChangeEvent.getValue()) {
                    registrationStartDate.enable();
                    registrationEndDate.enable();
                } else {
                    registrationStartDate.disable();
                    registrationEndDate.disable();
                }
            }
        });
        result.add(filterByRegistrationDate);

        HorizontalPanel dataPanel = new HorizontalPanel();
        dataPanel.add(new HTML("&nbsp"));
        //start
        Date start = new Date(112, 8, 1); // 1.9.2012
        registrationStartDate = new TextInputValueField(new DateTimeValidator(DATE_FORMAT), "", dateFormat.format(start), false, 80);
        registrationStartDate.disable();
        dataPanel.add(registrationStartDate.getContentWidget());
        //end
        registrationEndDate = new TextInputValueField(new DateTimeValidator(DATE_FORMAT), "", dateFormat.format(new Date()), false, 80);
        dataPanel.add(new HTML("&nbsp-&nbsp"));
        dataPanel.add(registrationEndDate.getContentWidget());
        registrationEndDate.disable();
        result.add(dataPanel);

        result.add(new HTML("<br>"));
        return result;
    }

    private Panel filterByRegistrars() {
        VerticalPanel result = new VerticalPanel();

        filterByRegistrar = new CheckBox(constants.processUrnNbnExportFilterByRegistrar());
        result.add(filterByRegistrar);
        filterByRegistrar.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                registrarsListBox.setEnabled(valueChangeEvent.getValue());
            }
        });

        HorizontalPanel dataPanel = new HorizontalPanel();
        dataPanel.add(new HTML("&nbsp"));
        registrarsListBox = new MultiSelectListBox();
        for (int i = 0; i < registrarsOfUser.size(); i++) {
            RegistrarDTO registrar = registrarsOfUser.get(i);
            registrarsListBox.addItem(registrar.getCode());
        }
        dataPanel.add(registrarsListBox);
        registrarsListBox.setEnabled(false);
        result.add(dataPanel);

        result.add(new HTML("<br>"));
        return result;
    }

    private Panel filterByEntityTypes() {
        VerticalPanel result = new VerticalPanel();

        filterByIeType = new CheckBox(constants.processUrnNbnExportFilterByIeType());
        result.add(filterByIeType);
        filterByIeType.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                ieTypesListBox.setEnabled(valueChangeEvent.getValue());
            }
        });

        HorizontalPanel dataPanel = new HorizontalPanel();
        dataPanel.add(new HTML("&nbsp"));
        ieTypesListBox = new MultiSelectListBox();
        for (String entityType : ENTITY_TYPES) {
            ieTypesListBox.addItem(entityType);
        }
        ieTypesListBox.setEnabled(false);
        dataPanel.add(ieTypesListBox);
        result.add(dataPanel);

        result.add(new HTML("<br>"));
        return result;
    }

    private Panel filterByAbsentIdentifier() {
        VerticalPanel result = new VerticalPanel();

        filterByAbsentIdentifiers = new CheckBox(constants.processUrnNbnExportFilterByMissingIdentifiers());
        filterByAbsentIdentifiers.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                absenceOfIdentifiersListBox.setEnabled(valueChangeEvent.getValue());
            }
        });
        result.add(filterByAbsentIdentifiers);

        HorizontalPanel dataPanel = new HorizontalPanel();
        dataPanel.add(new HTML("&nbsp"));
        absenceOfIdentifiersListBox = new MultiSelectListBox();
        absenceOfIdentifiersListBox.addItem("CNB");
        absenceOfIdentifiersListBox.addItem("ISSN");
        absenceOfIdentifiersListBox.addItem("ISBN");
        absenceOfIdentifiersListBox.setEnabled(false);
        dataPanel.add(absenceOfIdentifiersListBox);
        result.add(dataPanel);

        result.add(new HTML("<br>"));
        return result;
    }

    private Panel filterByState() {
        VerticalPanel result = new VerticalPanel();

        filterByState = new CheckBox(constants.processUrnNbnExportFilterByState());
        filterByState.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                stateListBox.setEnabled(valueChangeEvent.getValue());
            }
        });
        result.add(filterByState);

        HorizontalPanel dataPanel = new HorizontalPanel();
        dataPanel.add(new HTML("&nbsp"));
        stateListBox = new ListBox();
        stateListBox.addItem(constants.activityActiveOnly());
        stateListBox.addItem(constants.activityDeactivatedOnly());
        stateListBox.setEnabled(false);
        stateListBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                if (stateListBox.getSelectedIndex() == 0) {
                    //active only
                    filterByDeactivationDateContainer.clear();
                } else {
                    //deactivated only
                    filterByDeactivationDateContainer.clear();
                    filterByDeactivationDateContainer.add(buildFilterByDeactivationDate());
                }
            }
        });
        dataPanel.add(stateListBox);
        result.add(dataPanel);

        result.add(new HTML("<br>"));
        return result;
    }

    private Panel buildFilterByDeactivationDate() {
        VerticalPanel result = new VerticalPanel();

        filterByDeactivationDate = new CheckBox(constants.processUrnNbnExportFilterByDeactivationDate());
        filterByDeactivationDate.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> valueChangeEvent) {
                if (valueChangeEvent.getValue()) {
                    deactivationStartDate.enable();
                    deactivationEndDate.enable();
                } else {
                    deactivationStartDate.disable();
                    deactivationEndDate.disable();
                }
            }
        });
        result.add(filterByDeactivationDate);

        HorizontalPanel dataPanel = new HorizontalPanel();
        dataPanel.add(new HTML("&nbsp"));
        //start
        Date start = new Date(112, 8, 1); // 1.9.2012
        deactivationStartDate = new TextInputValueField(new DateTimeValidator(DATE_FORMAT), "", dateFormat.format(start), false, 80);
        deactivationStartDate.disable();
        dataPanel.add(deactivationStartDate.getContentWidget());
        //end
        deactivationEndDate = new TextInputValueField(new DateTimeValidator(DATE_FORMAT), "", dateFormat.format(new Date()), false, 80);
        dataPanel.add(new HTML("&nbsp-&nbsp"));
        dataPanel.add(deactivationEndDate.getContentWidget());
        deactivationEndDate.disable();
        result.add(dataPanel);

        result.add(new HTML("<br>"));

        return result;
    }

    private Panel includeNumberOfDigitalInstancesCheckbox() {
        VerticalPanel result = new VerticalPanel();
        includeNumberOfDigitalInstances = new CheckBox(constants.processUrnNbnExportIncludeNumberOfDigitalInstances());
        result.add(includeNumberOfDigitalInstances);
        result.add(new HTML("<br>"));
        return result;
    }

    private Panel buttonsPanel() {
        HorizontalPanel result = new HorizontalPanel();
        result.setSpacing(10);
        result.add(scheduleProcessButton());
        result.add(closeButton());
        return result;
    }


    private Button scheduleProcessButton() {
        return new Button(constants.scheduleProcess(), new ClickHandler() {

            public void onClick(ClickEvent event) {
                boolean formWithoutErrors = true;
                if (filterByRegistrationDate.getValue()) {
                    formWithoutErrors &= registrationStartDate.validValueInserted() && registrationEndDate.validValueInserted();
                }
                if (stateListBox.getSelectedIndex() == 1 && filterByDeactivationDate.getValue()) {//only deactivated with filter selected
                    formWithoutErrors &= deactivationStartDate.validValueInserted() && deactivationEndDate.validValueInserted();
                }

                if (formWithoutErrors) {
                    //registration date
                    String paramRegistrationStart = filterByRegistrationDate.getValue() ? (String) registrationStartDate.getInsertedValue() : null;
                    String paramRegistrationEnd = filterByRegistrationDate.getValue() ? (String) registrationEndDate.getInsertedValue() : null;

                    //registrars
                    String paramRegistrars = null;
                    if (filterByRegistrar.getValue()) {
                        List<String> registrarCodes = registrarsListBox.getSelectedItems();
                        if (registrarCodes.size() > 0) {
                            StringBuilder builder = new StringBuilder();
                            String separator = "";
                            for (String code : registrarCodes) {
                                builder.append(separator);
                                builder.append(code);
                                separator = ",";
                            }
                            paramRegistrars = builder.toString();
                        }
                    }

                    //document types
                    String paramEntityTypes = null;
                    if (filterByIeType.getValue()) {
                        List<String> selectedEntityTypes = ieTypesListBox.getSelectedItems();
                        if (selectedEntityTypes.size() > 0) {
                            StringBuilder types = new StringBuilder();
                            String separator = "";
                            for (String code : selectedEntityTypes) {
                                types.append(separator);
                                types.append(code);
                                separator = ",";
                            }
                            paramEntityTypes = types.toString();
                        }
                    }

                    //missing identifiers
                    List<String> idents = absenceOfIdentifiersListBox.getSelectedItems();
                    Boolean paramMissingCnb = idents.contains("CNB") && filterByAbsentIdentifiers.getValue();
                    Boolean paramMissingIssn = idents.contains("ISSN") && filterByAbsentIdentifiers.getValue();
                    Boolean paramMissingIsbn = idents.contains("ISBN") && filterByAbsentIdentifiers.getValue();

                    //states
                    int activitySelectedIndex = stateListBox.getSelectedIndex();
                    Boolean returnActive = !filterByState.getValue() || activitySelectedIndex == 0;
                    Boolean returnDeactivated = !filterByState.getValue() || activitySelectedIndex == 1;

                    //deactivation date range
                    String paramDeactivationStart = null;
                    String paramDeactivationEnd = null;
                    if (!returnActive && returnDeactivated && filterByDeactivationDate.getValue()) {
                        paramDeactivationStart = (String) deactivationStartDate.getInsertedValue();
                        paramDeactivationEnd = (String) deactivationEndDate.getInsertedValue();
                    }

                    //include number of DIs
                    Boolean exportNumberOfDigitalInstances = includeNumberOfDigitalInstances.getValue();

                    String[] params = new String[]{
                            paramRegistrationStart, paramRegistrationEnd,
                            paramRegistrars,
                            paramEntityTypes,
                            paramMissingCnb.toString(), paramMissingIssn.toString(), paramMissingIsbn.toString(),
                            returnActive.toString(), returnDeactivated.toString(),
                            paramDeactivationStart, paramDeactivationEnd,
                            exportNumberOfDigitalInstances.toString()};
                    //log(params);
                    processService.scheduleProcess(ProcessDTOType.REGISTRARS_URN_NBN_CSV_EXPORT, params, new AsyncCallback<Void>() {

                        public void onSuccess(Void result) {
                            ScheduleProcessExportUrnNbnListDialogBox.this.hide();
                        }

                        public void onFailure(Throwable caught) {
                            errorLabel.setText(caught.getMessage());
                        }
                    });
                }
            }
        });
    }

    private void log(String[] params) {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        for (int i = 0; i < params.length; i++) {
            builder.append(params[i]);
            if (i != params.length - 1) {
                builder.append(',');
            }
        }
        builder.append(']');
        LOGGER.info("params:" + builder.toString());
    }

    private Button closeButton() {
        return new Button(constants.close(), new ClickHandler() {

            public void onClick(ClickEvent event) {
                ScheduleProcessExportUrnNbnListDialogBox.this.hide();
            }
        });
    }
}
