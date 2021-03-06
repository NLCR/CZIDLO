package cz.nkp.urnnbn.client.insertRecord;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import cz.nkp.urnnbn.client.DigitalInstanceRefreshable;
import cz.nkp.urnnbn.client.Operation;
import cz.nkp.urnnbn.client.editRecord.EditDigitalInstanceDialogBox;
import cz.nkp.urnnbn.client.forms.digitalDocument.DigitalDocumentForm;
import cz.nkp.urnnbn.client.forms.digitalDocument.TechnicalMetadataForm;
import cz.nkp.urnnbn.client.forms.digitalDocument.UrnNbnForm;
import cz.nkp.urnnbn.client.forms.intEntities.IntelectualEntityForm;
import cz.nkp.urnnbn.client.forms.intEntities.SourceDocumentForm;
import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.i18n.MessagesImpl;
import cz.nkp.urnnbn.client.resources.InsertRecordPanelCss;
import cz.nkp.urnnbn.client.services.*;
import cz.nkp.urnnbn.shared.ConfigurationData;
import cz.nkp.urnnbn.shared.UrnNbnRegistrationMode;
import cz.nkp.urnnbn.shared.dto.*;
import cz.nkp.urnnbn.shared.dto.ie.AnalyticalDTO;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;

import java.util.ArrayList;
import java.util.logging.Logger;

public class RecordDataPanel extends VerticalPanel implements DigitalInstanceRefreshable {

    private static final Logger logger = Logger.getLogger(RecordDataPanel.class.getName());
    // services
    private final InstitutionsServiceAsync institutionsService = GWT.create(InstitutionsService.class);
    private final DataServiceAsync dataService = GWT.create(DataService.class);
    private final ConfigurationServiceAsync configurationService = GWT.create(ConfigurationService.class);
    // css, constants, messages
    private final InsertRecordPanelCss css = InsertRecordPanelResources.css();
    private final ConstantsImpl constants = InsertRecordPanelResources.constants();
    private final MessagesImpl messages = InsertRecordPanelResources.messages();
    // superior panel
    private final DataInputTab superPanel;
    // configuration
    private ConfigurationData configuration;
    // forms
    private final IntelectualEntityForm intelectualEntForm;
    private final SourceDocumentForm srcDocform;
    private DigitalDocumentForm digitalDocForm;
    private final TechnicalMetadataForm technicalMetadataForm = new TechnicalMetadataForm();
    private UrnNbnForm urnNbnForm;
    private final Label errorLabel = errorLabel(null);
    // actual data
    private final String intelectualEntType;
    private UrnNbnDTO urnNbnAssigned;
    private ArrayList<ArchiverDTO> archivers = new ArrayList<>();
    private ArrayList<DigitalLibraryDTO> librariesOfRegistrar = new ArrayList<>();
    private final ArrayList<DigitalInstanceDTO> digitalInstances = new ArrayList<>();
    private final ArrayList<RegistrarScopeIdDTO> registrarScopeIds = new ArrayList<>();
    private final RegistrarDTO registrar;
    UrnNbnRegistrationMode registrationMode;

    public Label errorLabel(Integer size) {
        Label result = new Label();
        if (size != null) {
            result.setWidth(size + "px");
        }
        result.addStyleName(css.errorLabel());
        return result;
    }

    public RecordDataPanel(DataInputTab superPanel, RegistrarDTO registrar, UrnNbnRegistrationMode registrationMode,
                           IntelectualEntityForm intelectualEntForm, SourceDocumentForm srcDocForm, String intelectualEntType) {
        this.superPanel = superPanel;
        this.intelectualEntType = intelectualEntType;
        this.intelectualEntForm = intelectualEntForm;
        this.registrar = registrar;
        this.registrationMode = registrationMode;
        loadLibrariesOfRegistrar();
        this.srcDocform = srcDocForm;
        loadConfigurationFromServer();
        loadArchivers();
        loadInitialForms();
    }

    private void loadLibrariesOfRegistrar() {
        institutionsService.getLibraries(registrar.getId(), new AsyncCallback<ArrayList<DigitalLibraryDTO>>() {

            @Override
            public void onSuccess(ArrayList<DigitalLibraryDTO> result) {
                librariesOfRegistrar = result;
            }

            @Override
            public void onFailure(Throwable caught) {
                logger.severe("Error loading digital libraries of registrar: " + caught.getMessage());
            }
        });
    }

    public RecordDataPanel(DataInputTab superPanel, RegistrarDTO registrar, UrnNbnRegistrationMode registrationMode,
                           IntelectualEntityForm entityForm, String typeName) {
        this(superPanel, registrar, registrationMode, entityForm, null, typeName);
    }

    private void loadArchivers() {
        institutionsService.getAllArchivers(new AsyncCallback<ArrayList<ArchiverDTO>>() {

            @Override
            public void onSuccess(ArrayList<ArchiverDTO> result) {
                archivers = result;
                loadInitialForms();
            }

            @Override
            public void onFailure(Throwable caught) {
                logger.severe("Error loading registrars: " + caught.getMessage());
            }
        });
    }

    private void loadInitialForms() {
        clear();
        digitalDocForm = new DigitalDocumentForm(registrar, archivers);
        add(backToInitialConfigurationButton(constants.back()));
        add(new HTML("<br/>"));
        add(intelectualEntHeading());
        add(intelectualEntForm);
        if (srcDocform != null) {
            Label srcDocLabel = new Label(constants.sourceDoc());
            srcDocLabel.setStyleName(css.heading());
            add(srcDocLabel);
            add(srcDocform);
        }
        add(new HTML("<br/>"));
        add(digitalDocumentHeading());
        add(digitalDocForm);
        add(technicalMetadataForm);
        if (urnNbnForm != null) {
            add(urnNbnForm);
        }
        add(insertRecordButton());
        add(errorLabel);
    }

    private Button backToInitialConfigurationButton(String buttonLabel) {
        return new Button(buttonLabel, new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                superPanel.reload(new SelectEntityParametersPanel(superPanel));
            }
        });
    }

    private Label intelectualEntHeading() {
        Label result = new Label(intelectualEntType);
        result.setStyleName(css.heading());
        return result;
    }

    private Label digitalDocumentHeading() {
        Label result = new Label(constants.digitalDocument());
        result.setStyleName(css.heading());
        return result;
    }

    private Button insertRecordButton() {
        final Button btnInsert = new Button(constants.insert());
        btnInsert.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                if (formsFilledCorrectly()) {
                    btnInsert.setEnabled(false);
                    IntelectualEntityDTO entity = intelectualEntForm.getDto();
                    if (srcDocform != null) {
                        ((AnalyticalDTO) entity).setSourceDocument(srcDocform.getDto());
                    }
                    DigitalDocumentDTO digDoc = digitalDocForm.getDto();
                    digDoc.setRegistrar(registrar);
                    digDoc.setTechnicalMetadata(technicalMetadataForm.getDto());
                    UrnNbnDTO urnNbn = urnNbnForm != null ? urnNbnForm.getDto() : null;
                    //rsIds are not being inserted in this step
                    ArrayList<RegistrarScopeIdDTO> identifiers = new ArrayList<>();
                    dataService.saveRecord(entity, digDoc, urnNbn, identifiers, new AsyncCallback<UrnNbnDTO>() {

                        @Override
                        public void onSuccess(UrnNbnDTO result) {
                            RecordDataPanel.this.urnNbnAssigned = result;
                            reloadPanelAfterRecordInserted();
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            errorLabel.setText(messages.serverError(caught.getMessage()));
                            btnInsert.setEnabled(true);
                        }
                    });

                } else {
                    logger.info("form not filled correcty");
                }
            }
        });
        return btnInsert;
    }

    private boolean formsFilledCorrectly() {
        return intelectualEntForm.isFilledCorrectly() & (srcDocform == null || srcDocform.isFilledCorrectly()) & digitalDocForm.isFilledCorrectly()
                & technicalMetadataForm.isFilledCorrectly() & (urnNbnForm == null || urnNbnForm.isFilledCorrectly());
    }

    private void reloadPanelAfterRecordInserted() {
        clear();
        add(backToInitialConfigurationButton(constants.back()));
        add(new HTML("<br/>"));
        add(intelectualEntHeading());
        intelectualEntForm.frost();
        add(intelectualEntForm);
        if (srcDocform != null) {
            srcDocform.frost();
            add(srcDocform);
        }
        add(new HTML("<br/>"));
        add(digitalDocumentHeading());
        digitalDocForm.frost();
        add(digitalDocForm);
        technicalMetadataForm.frost();
        add(technicalMetadataForm);
        add(assignedUrnNbnPanel());
        add(new HTML("<br/>"));
        add(registrarScopeIdsPanel());
        add(new HTML("<br/>"));
        add(digitalInstancesPanel());
        add(new HTML("<br/>"));
        add(backToInitialConfigurationButton(constants.insertNewRecord()));
    }

    private HorizontalPanel assignedUrnNbnPanel() {
        HorizontalPanel result = new HorizontalPanel();
        result.add(new HTML(constants.assignedOrConfirmed() + ": &nbsp;"));
        Label urnNbnLabel = new Label(urnNbnAssigned.toString());
        urnNbnLabel.setStyleName(css.urnNbnAssigned());
        result.add(urnNbnLabel);
        return result;
    }

    private Panel digitalInstancesPanel() {
        VerticalPanel result = new VerticalPanel();
        //heading
        Label heading = new Label(constants.digitalInstances());
        heading.setStyleName(css.heading());
        result.add(heading);
        //digital instances
        Grid diGrid = new Grid(digitalInstances.size(), 4);
        for (int i = 0; i < digitalInstances.size(); i++) {
            DigitalInstanceDTO instance = digitalInstances.get(i);
            diGrid.setWidget(i, 0, new Label(instance.getUrl()));
            diGrid.setWidget(i, 1, digitalInstanceDetailsButton(instance));
            diGrid.setWidget(i, 2, editDigitalInstanceButton(instance));
            diGrid.setWidget(i, 3, deactivateDigitalInstanceButton(instance));
        }
        result.add(diGrid);
        //add new di button
        Button addDiButton = new Button(constants.add(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                new InsertDigitalInstanceDialogBox(urnNbnAssigned, librariesOfRegistrar, new Operation<DigitalInstanceDTO>() {
                    @Override
                    public void run(DigitalInstanceDTO di) {
                        addDigitalInstance(di);
                    }
                }).show();
            }
        });
        result.add(addDiButton);
        return result;
    }

    private Panel registrarScopeIdsPanel() {
        VerticalPanel result = new VerticalPanel();
        //heading
        Label heading = new Label(constants.registrarScopeIds());
        heading.setStyleName(css.heading());
        result.add(heading);
        //registrar-scope ids
        Grid rsIdGrid = new Grid(registrarScopeIds.size(), 2);
        for (int i = 0; i < registrarScopeIds.size(); i++) {
            RegistrarScopeIdDTO rsId = registrarScopeIds.get(i);
            rsIdGrid.setWidget(i, 0, new Label(rsId.getType() + ":" + rsId.getValue()));
            rsIdGrid.setWidget(i, 1, deleteRsIdButton(rsId));
        }
        result.add(rsIdGrid);
        //add new rsid button
        Button addBtn = new Button(constants.add(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Long registrarId = registrar.getId();
                Long digDocId = urnNbnAssigned.getDigdocId();

                new InsertRegistrarScopeIdDialogBox(registrarId, digDocId, new Operation<RegistrarScopeIdDTO>() {
                    @Override
                    public void run(RegistrarScopeIdDTO id) {
                        addRegistarScopeId(id);
                    }
                }).show();
            }
        });
        result.add(addBtn);
        return result;
    }

    private Button deleteRsIdButton(final RegistrarScopeIdDTO rsId) {
        final Button btn = new Button(constants.delete());
        btn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                btn.setEnabled(false);
                dataService.removeRegistrarScopeIdentifier(rsId, new AsyncCallback<Void>() {

                            @Override
                            public void onSuccess(Void aVoid) {
                                removeRsId(rsId);
                            }

                            @Override
                            public void onFailure(Throwable caught) {
                                Window.alert(messages.serverError(caught.getMessage()));
                                btn.setEnabled(true);
                            }
                        }
                );

            }
        });
        return btn;
    }


    private Button digitalInstanceDetailsButton(final DigitalInstanceDTO instance) {
        return new Button(constants.details(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                new DigitalInstanceDetailsDialogBox(instance).show();
            }
        });
    }

    private Button deactivateDigitalInstanceButton(final DigitalInstanceDTO instance) {
        return new Button(constants.deactivate(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                dataService.deactivateDigitalInstance(instance, new AsyncCallback<Void>() {

                    @Override
                    public void onSuccess(Void result) {
                        removeDigitalInstance(instance);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert(messages.serverError(caught.getMessage()));
                    }
                });
            }
        });
    }

    private Button editDigitalInstanceButton(final DigitalInstanceDTO instance) {
        return new Button(constants.edit(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ArrayList<DigitalLibraryDTO> libraries = new ArrayList<DigitalLibraryDTO>();
                libraries.add(instance.getLibrary());
                EditDigitalInstanceDialogBox dialog = new EditDigitalInstanceDialogBox(RecordDataPanel.this, urnNbnAssigned, instance, libraries);
                dialog.show();
            }
        });
    }

    public void addDigitalInstance(DigitalInstanceDTO instance) {
        digitalInstances.add(instance);
        reloadPanelAfterRecordInserted();
    }

    public void removeDigitalInstance(DigitalInstanceDTO instance) {
        digitalInstances.remove(instance);
        reloadPanelAfterRecordInserted();
    }

    private void addRegistarScopeId(RegistrarScopeIdDTO rsId) {
        registrarScopeIds.add(rsId);
        reloadPanelAfterRecordInserted();
    }

    private void removeRsId(RegistrarScopeIdDTO rsId) {
        registrarScopeIds.remove(rsId);
        reloadPanelAfterRecordInserted();
    }

    private void loadConfigurationFromServer() {
        AsyncCallback<ConfigurationData> callback = new AsyncCallback<ConfigurationData>() {
            public void onSuccess(ConfigurationData data) {
                RecordDataPanel.this.configuration = data;
                initUrnNbnFormIfNecessary();
            }

            public void onFailure(Throwable caught) {
                logger.info("Error loading configuration: " + caught.getMessage());
            }

        };
        configurationService.getConfiguration(callback);
    }

    private void initUrnNbnFormIfNecessary() {
        if (registrationMode == UrnNbnRegistrationMode.BY_REGISTRAR || registrationMode == UrnNbnRegistrationMode.BY_RESERVATION) {
            this.urnNbnForm = new UrnNbnForm(registrar, configuration.getCountryCode());
        } else {
            this.urnNbnForm = null;
        }
    }

    @Override
    public void refresh(DigitalInstanceDTO di) {
        removeDigitalInstance(di);
        addDigitalInstance(di);
    }
}
