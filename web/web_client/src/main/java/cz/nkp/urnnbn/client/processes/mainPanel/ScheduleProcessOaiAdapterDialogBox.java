package cz.nkp.urnnbn.client.processes.mainPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import cz.nkp.urnnbn.client.forms.TextInputValueField;
import cz.nkp.urnnbn.client.services.UserAccountService;
import cz.nkp.urnnbn.client.services.UserAccountServiceAsync;
import cz.nkp.urnnbn.client.validation.LimitedLengthUrlValidator;
import cz.nkp.urnnbn.client.validation.LimitedLengthValidator;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTOType;
import cz.nkp.urnnbn.shared.dto.process.XmlTransformationDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

public class ScheduleProcessOaiAdapterDialogBox extends AbstractScheduleProcessDialogBox {

    private static final Logger LOGGER = Logger.getLogger(ScheduleProcessOaiAdapterDialogBox.class.getName());

    private final UserAccountServiceAsync accountsService = GWT.create(UserAccountService.class);
    private ArrayList<RegistrarDTO> registrarsOfUser = new ArrayList<>();
    // transformations
    private List<XmlTransformationDTO> ddRegistrationTransformations;
    private List<XmlTransformationDTO> diImportTransformations;
    private XmlTransformationDTO selectedDdRegistrationTransformation;
    private XmlTransformationDTO selectedDiImportTransformation;

    private TextInputValueField oaiBaseUrlField;
    private TextInputValueField oaiMetadataPrefixField;
    private TextInputValueField oaiSetField;

    private CheckBox registerDDsWithUrnCheckbox;
    private CheckBox registerDDsWithoutUrnCheckbox;

    private CheckBox diImportMergeDisCheckbox;
    private CheckBox diImportIgnoreDifferenceInAccessibilityCheckbox;
    private CheckBox diImportIgnoreDifferenceInFormatCheckbox;

    private final Label errorLabel = errorLabel(320);
    private RegistrarDTO selectedRegistrar;

    public ScheduleProcessOaiAdapterDialogBox(UserDTO user) {
        super(user);
        loadRegistrars();
        loadTransformations();
    }

    private void loadTransformations() {
        processService.getXmlTransformationsOfUser(new AsyncCallback<List<XmlTransformationDTO>>() {

            @Override
            public void onSuccess(List<XmlTransformationDTO> result) {
                //LOGGER.info("transformations loaded");
                ddRegistrationTransformations = new ArrayList<>();
                diImportTransformations = new ArrayList<>();
                for (XmlTransformationDTO transformation : result) {
                    switch (transformation.getType()) {
                        case DIGITAL_DOCUMENT_REGISTRATION:
                            ddRegistrationTransformations.add(transformation);
                            break;
                        case DIGITAL_INSTANCE_IMPORT:
                            diImportTransformations.add(transformation);
                            break;
                    }
                }
                Collections.sort(ddRegistrationTransformations, new Comparator<XmlTransformationDTO>() {

                    @Override
                    public int compare(XmlTransformationDTO o1, XmlTransformationDTO o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
                Collections.sort(diImportTransformations, new Comparator<XmlTransformationDTO>() {

                    @Override
                    public int compare(XmlTransformationDTO o1, XmlTransformationDTO o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
                reload();
            }

            @Override
            public void onFailure(Throwable caught) {
                errorLabel.setText(messages.serverError(caught.getMessage()));
            }
        });
    }

    private void loadRegistrars() {

        accountsService.getRegistrarsManagedByUser(new AsyncCallback<ArrayList<RegistrarDTO>>() {

            @Override
            public void onSuccess(ArrayList<RegistrarDTO> result) {
                //LOGGER.info("registrars loaded");
                registrarsOfUser = result;
                Collections.sort(registrarsOfUser, new Comparator<RegistrarDTO>() {

                    @Override
                    public int compare(RegistrarDTO o1, RegistrarDTO o2) {
                        return o1.getCode().compareTo(o2.getCode());
                    }
                });
                if (!registrarsOfUser.isEmpty()) {
                    selectedRegistrar = registrarsOfUser.get(0);
                }
                reload();
            }

            @Override
            public void onFailure(Throwable caught) {
                errorLabel.setText(messages.serverError(caught.getMessage()));
            }
        });
    }

    @Override
    public void open() {
        reload();
    }

    void reload() {
        //LOGGER.info("reload");
        clear();
        setText(messages.processPlaning(constants.OAI_ADAPTER()));
        setAnimationEnabled(true);
        setWidget(contentPanel());
        center();
    }

    private Panel contentPanel() {
        VerticalPanel result = new VerticalPanel();
        result.add(selectRegistrarPanel());
        result.add(insertOaiBaseUrlPanel());
        result.add(insertOaiMetadataPrefixPanel());
        result.add(selectOaiSetPanel());
        result.add(selectDdRegistrationXsltPanel());
        result.add(selectDiImportXsltPanel());
        result.add(registerDdPanel());
        result.add(importDiPanel());
        result.add(buttonsPanel());
        result.add(errorLabel);
        return result;
    }

    private Panel importDiPanel() {
        VerticalPanel result = new VerticalPanel();

        HorizontalPanel row1 = new HorizontalPanel();
        row1.setSpacing(5);
        diImportMergeDisCheckbox = new CheckBox(constants.processOaiAdapterDiImportMergDis());
        diImportMergeDisCheckbox.setChecked(true);
        row1.add(diImportMergeDisCheckbox);
        result.add(row1);

        HorizontalPanel row2 = new HorizontalPanel();
        row2.setSpacing(5);
        diImportIgnoreDifferenceInAccessibilityCheckbox = new CheckBox(constants.processOaiAdapterDiImportIgnoreDifferenceInAccessibility());
        diImportIgnoreDifferenceInAccessibilityCheckbox.setChecked(true);
        row2.add(diImportIgnoreDifferenceInAccessibilityCheckbox);
        diImportIgnoreDifferenceInFormatCheckbox = new CheckBox(constants.processOaiAdapterDiImportIgnoreDifferenceInFormat());
        diImportIgnoreDifferenceInFormatCheckbox.setChecked(true);
        row2.add(diImportIgnoreDifferenceInFormatCheckbox);

        result.add(row2);
        return result;
    }

    private Panel registerDdPanel() {
        HorizontalPanel result = new HorizontalPanel();
        result.setSpacing(5);
        result.add(new Label(constants.processOaiAdapterRegisterDDs() + SEPARATOR));
        registerDDsWithUrnCheckbox = new CheckBox(constants.processOaiAdapterRegisterDDsWithUrn());
        registerDDsWithUrnCheckbox.setChecked(true);
        result.add(registerDDsWithUrnCheckbox);
        registerDDsWithoutUrnCheckbox = new CheckBox(constants.processOaiAdapterRegisterDDsWithoutUrn());
        registerDDsWithoutUrnCheckbox.setChecked(false);
        result.add(registerDDsWithoutUrnCheckbox);
        return result;
    }

    private Panel selectRegistrarPanel() {
        HorizontalPanel result = new HorizontalPanel();
        result.setSpacing(5);
        result.add(new Label(constants.processOaiAdapterRegistrar() + SEPARATOR));
        result.add(registrarList());
        return result;
    }

    private Panel insertOaiBaseUrlPanel() {
        HorizontalPanel result = new HorizontalPanel();
        result.setSpacing(5);
        oaiBaseUrlField = new TextInputValueField(new LimitedLengthUrlValidator(100), constants.processOaiAdapterOaiBaseUrl(), "", true, 200);
        result.add(oaiBaseUrlField.getLabelWidget());
        result.add(oaiBaseUrlField.getContentWidget());
        return result;
    }

    private Panel insertOaiMetadataPrefixPanel() {
        HorizontalPanel result = new HorizontalPanel();
        result.setSpacing(5);
        oaiMetadataPrefixField = new TextInputValueField(new LimitedLengthValidator(40), constants.processOaiAdapterOaiMetadataPrefix(), "", true, 160);
        result.add(oaiMetadataPrefixField.getLabelWidget());
        result.add(oaiMetadataPrefixField.getContentWidget());
        return result;
    }


    private Panel selectOaiSetPanel() {
        HorizontalPanel result = new HorizontalPanel();
        result.setSpacing(5);
        oaiSetField = new TextInputValueField(new LimitedLengthValidator(40), constants.processOaiAdapterOaiSet(), "", false, 160);
        result.add(oaiSetField.getLabelWidget());
        result.add(oaiSetField.getContentWidget());
        return result;
    }

    private Panel selectDdRegistrationXsltPanel() {
        HorizontalPanel result = new HorizontalPanel();
        result.setSpacing(5);
        result.add(new Label(constants.processOaiAdapterDdRegistrationTransformation() + SEPARATOR));
        result.add(ddRegistrationTemplatesList());
        return result;
    }

    private ListBox ddRegistrationTemplatesList() {
        final ListBox result = new ListBox();
        result.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                int index = result.getSelectedIndex();
                selectedDdRegistrationTransformation = ddRegistrationTransformations.get(index);
            }
        });
        if (ddRegistrationTransformations != null) {
            for (XmlTransformationDTO transformation : ddRegistrationTransformations) {
                result.addItem(transformation.getName());
            }
            if (!ddRegistrationTransformations.isEmpty()) {
                //this does not work, ChangeHandler.onChange is not fired here
                result.setSelectedIndex(0);
                //so it must by done manually like this:
                selectedDdRegistrationTransformation = ddRegistrationTransformations.get(0);
            }
        }
        return result;
    }

    private Widget selectDiImportXsltPanel() {
        HorizontalPanel result = new HorizontalPanel();
        result.setSpacing(5);
        result.add(new Label(constants.processOaiAdapterDiImportTransformation() + SEPARATOR));
        result.add(diImportTemplatesList());
        return result;
    }

    private ListBox diImportTemplatesList() {
        final ListBox result = new ListBox();
        result.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                int index = result.getSelectedIndex();
                selectedDiImportTransformation = diImportTransformations.get(index);
            }
        });
        if (diImportTransformations != null) {
            for (XmlTransformationDTO transformation : diImportTransformations) {
                result.addItem(transformation.getName());
            }
            if (!diImportTransformations.isEmpty()) {
                //this does not work, ChangeHandler.onChange is not fired here
                result.setSelectedIndex(0);
                //so it must by done manually like this:
                selectedDiImportTransformation = diImportTransformations.get(0);
            }
        }
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
                    String oaiSet = null;
                    String oaiSetRaw = (String) oaiSetField.getInsertedValue();
                    if (oaiSetRaw != null && !oaiSetRaw.trim().isEmpty()) {
                        oaiSet = oaiSetRaw.trim();
                    }
                    String ddRegistrationTemplateId = selectedDdRegistrationTransformation == null ? null : selectedDdRegistrationTransformation.getId().toString();
                    String diImportTemplateId = selectedDiImportTransformation == null ? null : selectedDiImportTransformation.getId().toString();

                    String[] params = new String[]{
                            selectedRegistrar.getCode(),
                            (String) oaiBaseUrlField.getInsertedValue(),
                            (String) oaiMetadataPrefixField.getInsertedValue(),
                            oaiSet,
                            ddRegistrationTemplateId,
                            diImportTemplateId,
                            registerDDsWithUrnCheckbox.getValue().toString(),
                            registerDDsWithoutUrnCheckbox.getValue().toString(),
                            diImportMergeDisCheckbox.getValue().toString(),
                            diImportIgnoreDifferenceInAccessibilityCheckbox.getValue().toString(),
                            diImportIgnoreDifferenceInFormatCheckbox.getValue().toString()
                    };

                    boolean formOk = true;
                    formOk &= oaiBaseUrlField.validValueInserted();
                    formOk &= oaiMetadataPrefixField.validValueInserted();
                    formOk &= oaiSetField.validValueInserted();
                    formOk &= ddRegistrationTemplateId != null;
                    formOk &= diImportTemplateId != null;

                    if (formOk) {
                        //log(LOGGER, params);
                        processService.scheduleProcess(ProcessDTOType.OAI_ADAPTER, params, new AsyncCallback<Void>() {

                            @Override
                            public void onSuccess(Void result) {
                                ScheduleProcessOaiAdapterDialogBox.this.hide();
                            }

                            @Override
                            public void onFailure(Throwable caught) {
                                errorLabel.setText(caught.getMessage());
                            }
                        });
                    }
                }
            }
        });
    }

    private Button closeButton() {
        return new Button(constants.close(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ScheduleProcessOaiAdapterDialogBox.this.hide();
            }
        });
    }

}
