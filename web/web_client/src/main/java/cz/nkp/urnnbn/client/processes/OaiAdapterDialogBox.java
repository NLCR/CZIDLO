package cz.nkp.urnnbn.client.processes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import cz.nkp.urnnbn.client.services.UserAccountService;
import cz.nkp.urnnbn.client.services.UserAccountServiceAsync;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.dto.process.ProcessDTOType;
import cz.nkp.urnnbn.shared.dto.process.XmlTransformationDTO;

import java.util.ArrayList;
import java.util.List;

public class OaiAdapterDialogBox extends AbstractScheduleProcessDialogBox {

    private final UserAccountServiceAsync accountsService = GWT.create(UserAccountService.class);
    private ArrayList<RegistrarDTO> registrarsOfUser = new ArrayList<RegistrarDTO>();
    // templates
    private final List<XmlTransformationDTO> ddRegistrationTemplates;
    private final List<XmlTransformationDTO> diImportTemplates;
    private XmlTransformationDTO selectedDdRegistrationTemplate;
    private XmlTransformationDTO selectedDiImportTemplate;
    
    private TextBox oaiBaseUrlTextBox;
    private TextBox oaiMetadataPrefixTextBox;
    private TextBox oaiSetTtextBox;

    private final Label errorLabel = errorLabel(320);
    private RegistrarDTO selectedRegistrar;

    public OaiAdapterDialogBox(UserDTO user, List<XmlTransformationDTO> ddRegistrationTemplates, List<XmlTransformationDTO> diImportTemplates) {
        super(user);
        this.ddRegistrationTemplates = ddRegistrationTemplates;
        this.selectedDdRegistrationTemplate = (ddRegistrationTemplates == null || ddRegistrationTemplates.isEmpty()) ? null : ddRegistrationTemplates
                .get(0);
        this.diImportTemplates = diImportTemplates;
        this.selectedDiImportTemplate = (diImportTemplates == null || diImportTemplates.isEmpty()) ? null : diImportTemplates.get(0);

        loadRegistrars();
        reload();
        center();
    }

    private void loadRegistrars() {

        accountsService.getRegistrarsManagedByUser(new AsyncCallback<ArrayList<RegistrarDTO>>() {

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
                errorLabel.setText(messages.serverError(caught.getMessage()));
            }
        });
    }

    void reload() {
        clear();
        setText(messages.processPlaning(constants.OAI_ADAPTER()));
        setAnimationEnabled(true);
        setWidget(contentPanel());
    }

    private Panel contentPanel() {
        VerticalPanel result = new VerticalPanel();
        result.add(selectRegistrarPanel());
        result.add(insertOaiBasUrlPanel());
        result.add(insertOaiMetadataPrefixPanel());
        result.add(selectOaiSetPanel());
        result.add(selectDdRegistrationPanel());
        result.add(selectDiImportPanel());
        result.add(buttonsPanel());
        result.add(errorLabel);
        return result;
    }

    private Panel selectRegistrarPanel() {
        HorizontalPanel result = new HorizontalPanel();
        result.setSpacing(5);
        result.add(new Label(constants.processOaiAdapterRegistrar() + SEPARATOR));
        result.add(registrarList());
        return result;
    }

    private Panel insertOaiBasUrlPanel() {
        HorizontalPanel result = new HorizontalPanel();
        result.setSpacing(5);
        result.add(new Label(constants.processOaiAdapterOaiBaseUrl() + SEPARATOR));
        oaiBaseUrlTextBox = new TextBox();
        result.add(oaiBaseUrlTextBox);
        return result;
    }

    private Panel insertOaiMetadataPrefixPanel() {
        HorizontalPanel result = new HorizontalPanel();
        result.setSpacing(5);
        result.add(new Label(constants.processOaiAdapterOaiMetadataPrefix() + SEPARATOR));
        oaiMetadataPrefixTextBox = new TextBox();
        result.add(oaiMetadataPrefixTextBox);
        return result;
    }

    private Panel selectOaiSetPanel() {
        HorizontalPanel result = new HorizontalPanel();
        result.setSpacing(5);
        result.add(new Label(constants.processOaiAdapterOaiSet() + SEPARATOR));
        oaiSetTtextBox = new TextBox();
        result.add(oaiSetTtextBox);
        return result;
    }

    private Panel selectDdRegistrationPanel() {
        HorizontalPanel result = new HorizontalPanel();
        result.setSpacing(5);
        result.add(new Label(constants.processOaiAdapterDdRegistrationTransformation() + SEPARATOR));
        result.add(ddRegistrationTemplatesList());
        return result;
    }

    private ListBox ddRegistrationTemplatesList() {
        final ListBox result = new ListBox();
        if (ddRegistrationTemplates != null) {
            for (XmlTransformationDTO transformation : ddRegistrationTemplates) {
                result.addItem(transformation.getName());
            }
        }

        result.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                int index = result.getSelectedIndex();
                selectedDdRegistrationTemplate = ddRegistrationTemplates.get(index);
            }
        });
        return result;
    }

    private Widget selectDiImportPanel() {
        HorizontalPanel result = new HorizontalPanel();
        result.setSpacing(5);
        result.add(new Label(constants.processOaiAdapterDiImportTransformation() + SEPARATOR));
        result.add(diImportTemplatesList());
        return result;
    }

    private ListBox diImportTemplatesList() {
        final ListBox result = new ListBox();
        if (diImportTemplates != null) {
            for (XmlTransformationDTO transformation : diImportTemplates) {
                result.addItem(transformation.getName());
            }
        }

        result.addChangeHandler(new ChangeHandler() {

            @Override
            public void onChange(ChangeEvent event) {
                int index = result.getSelectedIndex();
                selectedDiImportTemplate = diImportTemplates.get(index);
            }
        });
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
                    String oaiSet = (oaiSetTtextBox.getText() == null || oaiSetTtextBox.getText().isEmpty()) ? null : oaiSetTtextBox.getText();
                    //TODO: vzdy musi byt vybrana sablona, jinak neni mozne naplanovat proces
                    //podobne kontroly i pro dalsi parametry
                    String ddRegistrationTemplateId = selectedDdRegistrationTemplate == null ? null : selectedDdRegistrationTemplate.getId().toString();
                    String diImportTemplateId = selectedDiImportTemplate == null ? null : selectedDiImportTemplate.getId().toString();

                    String[] params = new String[]{
                            selectedRegistrar.getCode(),
                            oaiBaseUrlTextBox.getText(),
                            oaiMetadataPrefixTextBox.getText(),
                            oaiSet,
                            ddRegistrationTemplateId,
                            diImportTemplateId,
                            // TODO: 16.11.17 checkboxes for these
                            Boolean.FALSE.toString(), //ddRegistrationRegisterDdsWithUrn
                            Boolean.FALSE.toString(), //ddRegistrationRegisterDdsWithoutUrn
                            Boolean.FALSE.toString(), //diImportMergeDis
                            Boolean.TRUE.toString(), //diImportIgnoreDifferenceInAccessibility
                            Boolean.TRUE.toString() //diImportIgnoreDifferenceInFormat
                    };

                    processService.scheduleProcess(ProcessDTOType.OAI_ADAPTER, params, new AsyncCallback<Void>() {

                        @Override
                        public void onSuccess(Void result) {
                            OaiAdapterDialogBox.this.hide();
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
                OaiAdapterDialogBox.this.hide();
            }
        });
    }

}
