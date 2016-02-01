package cz.nkp.urnnbn.client.insertRecord;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import cz.nkp.urnnbn.client.DigitalInstanceRefreshable;
import cz.nkp.urnnbn.client.editRecord.EditDigitalInstanceDialogBox;
import cz.nkp.urnnbn.client.forms.digitalDocument.DigitalDocumentForm;
import cz.nkp.urnnbn.client.forms.digitalDocument.TechnicalMetadataForm;
import cz.nkp.urnnbn.client.forms.digitalDocument.UrnNbnForm;
import cz.nkp.urnnbn.client.forms.intEntities.IntelectualEntityForm;
import cz.nkp.urnnbn.client.forms.intEntities.SourceDocumentForm;
import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.i18n.MessagesImpl;
import cz.nkp.urnnbn.client.resources.InsertRecordPanelCss;
import cz.nkp.urnnbn.client.services.ConfigurationService;
import cz.nkp.urnnbn.client.services.ConfigurationServiceAsync;
import cz.nkp.urnnbn.client.services.DataService;
import cz.nkp.urnnbn.client.services.DataServiceAsync;
import cz.nkp.urnnbn.client.services.InstitutionsService;
import cz.nkp.urnnbn.client.services.InstitutionsServiceAsync;
import cz.nkp.urnnbn.shared.ConfigurationData;
import cz.nkp.urnnbn.shared.UrnNbnRegistrationMode;
import cz.nkp.urnnbn.shared.dto.ArchiverDTO;
import cz.nkp.urnnbn.shared.dto.DigitalDocumentDTO;
import cz.nkp.urnnbn.shared.dto.DigitalInstanceDTO;
import cz.nkp.urnnbn.shared.dto.DigitalLibraryDTO;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.dto.RegistrarScopeIdDTO;
import cz.nkp.urnnbn.shared.dto.UrnNbnDTO;
import cz.nkp.urnnbn.shared.dto.ie.AnalyticalDTO;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;

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
    private final DataInputPanel superPanel;
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
    private ArrayList<ArchiverDTO> archivers = new ArrayList<ArchiverDTO>();
    private ArrayList<DigitalLibraryDTO> librariesOfRegistrar = new ArrayList<DigitalLibraryDTO>();
    private final ArrayList<DigitalInstanceDTO> digitalInstances = new ArrayList<DigitalInstanceDTO>();
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

    public RecordDataPanel(DataInputPanel superPanel, RegistrarDTO registrar, UrnNbnRegistrationMode registrationMode,
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

    public RecordDataPanel(DataInputPanel superPanel, RegistrarDTO registrar, UrnNbnRegistrationMode registrationMode,
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
        return new Button(constants.insert(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (formsFilledCorrectly()) {
                    importRecord();
                }
            }
        });
    }

    private boolean formsFilledCorrectly() {
        return intelectualEntForm.isFilledCorrectly() & (srcDocform == null || srcDocform.isFilledCorrectly()) & digitalDocForm.isFilledCorrectly()
                & technicalMetadataForm.isFilledCorrectly() & (urnNbnForm == null || urnNbnForm.isFilledCorrectly());
    }

    private void importRecord() {
        IntelectualEntityDTO entity = intelectualEntForm.getDto();
        if (srcDocform != null) {
            ((AnalyticalDTO) entity).setSourceDocument(srcDocform.getDto());
        }
        DigitalDocumentDTO digDoc = digitalDocForm.getDto();
        digDoc.setRegistrar(registrar);
        digDoc.setTechnicalMetadata(technicalMetadataForm.getDto());
        UrnNbnDTO urnNbn = urnNbnForm != null ? urnNbnForm.getDto() : null;
        // TODO: identifiers
        ArrayList<RegistrarScopeIdDTO> identifiers = new ArrayList<RegistrarScopeIdDTO>();
        dataService.saveRecord(entity, digDoc, urnNbn, identifiers, new AsyncCallback<UrnNbnDTO>() {

            @Override
            public void onSuccess(UrnNbnDTO result) {
                RecordDataPanel.this.urnNbnAssigned = result;
                reloadPanelAfterRecordInserted();
            }

            @Override
            public void onFailure(Throwable caught) {
                errorLabel.setText(messages.serverError(caught.getMessage()));
            }
        });
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
        result.add(digitalInstancesHeading());
        Grid insstancesGrid = new Grid(digitalInstances.size(), 4);
        for (int i = 0; i < digitalInstances.size(); i++) {
            DigitalInstanceDTO instance = digitalInstances.get(i);
            insstancesGrid.setWidget(i, 0, new Label(instance.getUrl()));
            insstancesGrid.setWidget(i, 1, digitalInstanceDetailsButton(instance));
            insstancesGrid.setWidget(i, 2, editDigitalInstanceButton(instance));
            insstancesGrid.setWidget(i, 3, deactivateDigitalInstanceButton(instance));
        }
        result.add(insstancesGrid);
        result.add(addDigitalInstanceButton());
        return result;
    }

    private Widget digitalInstancesHeading() {
        Label result = new Label(constants.digitalInstances());
        result.setStyleName(css.heading());
        return result;
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

    private Button addDigitalInstanceButton() {
        return new Button(constants.add(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                new InsertDigitalInstanceDialogBox(RecordDataPanel.this, urnNbnAssigned, librariesOfRegistrar).show();
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
