package cz.nkp.urnnbn.client.insertRecord;

import java.util.ArrayList;

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

import cz.nkp.urnnbn.client.forms.digitalDocument.DigitalDocumentForm;
import cz.nkp.urnnbn.client.forms.digitalDocument.TechnicalMetadataForm;
import cz.nkp.urnnbn.client.forms.digitalDocument.UrnNbnForm;
import cz.nkp.urnnbn.client.forms.intEntities.IntelectualEntityForm;
import cz.nkp.urnnbn.client.forms.intEntities.SourceDocumentForm;
import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.i18n.MessagesImpl;
import cz.nkp.urnnbn.client.resources.InsertRecordPanelCss;
import cz.nkp.urnnbn.client.services.DataService;
import cz.nkp.urnnbn.client.services.DataServiceAsync;
import cz.nkp.urnnbn.client.services.InstitutionsService;
import cz.nkp.urnnbn.client.services.InstitutionsServiceAsync;
import cz.nkp.urnnbn.shared.dto.ArchiverDTO;
import cz.nkp.urnnbn.shared.dto.DigitalDocumentDTO;
import cz.nkp.urnnbn.shared.dto.DigitalInstanceDTO;
import cz.nkp.urnnbn.shared.dto.DigitalLibraryDTO;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.dto.RegistrarScopeIdDTO;
import cz.nkp.urnnbn.shared.dto.UrnNbnDTO;
import cz.nkp.urnnbn.shared.dto.ie.AnalyticalDTO;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;

public class RecordDataPanel extends VerticalPanel {
	private final InstitutionsServiceAsync institutionsService = GWT.create(InstitutionsService.class);
	private final DataServiceAsync dataService = GWT.create(DataService.class);
	private final InsertRecordPanelCss css = InsertRecordPanelResources.css();
	private final ConstantsImpl constants = InsertRecordPanelResources.constants();
	private final MessagesImpl messages = InsertRecordPanelResources.messages();
	private final DataInputPanel superPanel;
	private final String intelectualEntType;
	private final IntelectualEntityForm intelectualEntForm;
	private final SourceDocumentForm srcDocform;
	private DigitalDocumentForm digitalDocForm;
	private ArrayList<ArchiverDTO> archivers = new ArrayList<ArchiverDTO>();
	private final TechnicalMetadataForm technicalMetadataForm = new TechnicalMetadataForm();
	private final UrnNbnForm urnNbnForm;
	private final RegistrarDTO registrar;
	ArrayList<DigitalLibraryDTO> librariesOfRegistrar = new ArrayList<DigitalLibraryDTO>();
	private final Label errorLabel = errorLabel(null);
	private UrnNbnDTO urnNbnAssigned;
	private final ArrayList<DigitalInstanceDTO> digitalInstances = new ArrayList<DigitalInstanceDTO>();

	public Label errorLabel(Integer size) {
		Label result = new Label();
		if (size != null) {
			result.setWidth(size + "px");
		}
		result.addStyleName(css.errorLabel());
		return result;
	}

	public RecordDataPanel(DataInputPanel superPanel, RegistrarDTO registrar, Boolean withUrnTextbox,
			IntelectualEntityForm intelectualEntForm, SourceDocumentForm srcDocForm, String intelectualEntType) {
		this.superPanel = superPanel;
		this.intelectualEntType = intelectualEntType;
		this.intelectualEntForm = intelectualEntForm;
		this.registrar = registrar;
		loadLibrariesOfRegistrar();
		this.urnNbnForm = withUrnTextbox ? new UrnNbnForm(registrar) : null;
		this.srcDocform = srcDocForm;
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
				Window.alert(messages.serverError(caught.getMessage()));
			}
		});
	}

	public RecordDataPanel(DataInputPanel superPanel, RegistrarDTO registrar, Boolean withUrnTextbox, IntelectualEntityForm entityForm,
			String typeName) {
		this(superPanel, registrar, withUrnTextbox, entityForm, null, typeName);
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
				Window.alert("error: " + caught.getMessage());
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
		// TODO
		 return intelectualEntForm.isFilledCorrectly() & (srcDocform == null
		 || srcDocform.isFilledCorrectly())
		 & digitalDocForm.isFilledCorrectly() &
		 technicalMetadataForm.isFilledCorrectly()
		 & (urnNbnForm == null || urnNbnForm.isFilledCorrectly());
		//return true;
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
				// TODO: remove
				//result = new UrnNbnDTO("tst001", "000001", Long.valueOf(2));
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
		Grid insstancesGrid = new Grid(digitalInstances.size(), 3);
		for (int i = 0; i < digitalInstances.size(); i++) {
			DigitalInstanceDTO instance = digitalInstances.get(i);
			insstancesGrid.setWidget(i, 0, new Label(instance.getUrl()));
			insstancesGrid.setWidget(i, 1, detailsButton(instance));
			insstancesGrid.setWidget(i, 2, deleteButton(instance));
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

	private Button detailsButton(final DigitalInstanceDTO instance) {
		return new Button(constants.details(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				new DigitalInstanceDetailsDialogBox(instance).show();
			}
		});
	}

	private Button deleteButton(final DigitalInstanceDTO instance) {
		return new Button(constants.delete(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				dataService.deleteDigitalInstance(instance, new AsyncCallback<Void>() {

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
}
