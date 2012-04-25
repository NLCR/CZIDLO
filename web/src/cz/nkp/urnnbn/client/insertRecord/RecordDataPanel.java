package cz.nkp.urnnbn.client.insertRecord;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import cz.nkp.urnnbn.client.forms.digitalDocument.DigitalDocumentForm;
import cz.nkp.urnnbn.client.forms.digitalDocument.TechnicalMetadataForm;
import cz.nkp.urnnbn.client.forms.intEntities.IntelectualEntityForm;
import cz.nkp.urnnbn.client.forms.intEntities.SourceDocumentForm;
import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.resources.InsertRecordPanelCss;
import cz.nkp.urnnbn.client.services.InstitutionsService;
import cz.nkp.urnnbn.client.services.InstitutionsServiceAsync;
import cz.nkp.urnnbn.shared.dto.ArchiverDTO;

public class RecordDataPanel extends VerticalPanel {
	private final InstitutionsServiceAsync institutionsService = GWT.create(InstitutionsService.class);
	private final InsertRecordPanelCss css = InsertRecordPanelResources.css();
	protected final ConstantsImpl constants = GWT.create(ConstantsImpl.class);
	private final DataInputPanel superPanel;
	// private final Label entityHeading;
	private final String intelectualEntType;
	private final IntelectualEntityForm intelectualEntForm;
	private final SourceDocumentForm srcDocform;
	private DigitalDocumentForm digitalDocForm;
	private ArrayList<ArchiverDTO> archivers = new ArrayList<ArchiverDTO>();
	private final TechnicalMetadataForm technicalMetadataForm = new TechnicalMetadataForm();

	public RecordDataPanel(DataInputPanel superPanel,Boolean withUrnTextbox, IntelectualEntityForm intelectualEntForm, SourceDocumentForm srcDocForm, String intelectualEntType) {
		this.superPanel = superPanel;
		this.intelectualEntType = intelectualEntType;
		this.intelectualEntForm = intelectualEntForm;
		if (withUrnTextbox){
			//TODO: form pro urn:nbn
		}
		this.srcDocform = srcDocForm;
		loadArchivers();
		reload();
	}

	public RecordDataPanel(DataInputPanel superPanel,Boolean withUrnTextbox, IntelectualEntityForm entityForm, String typeName) {
		this(superPanel, withUrnTextbox, entityForm, null, typeName);
	}

	private void loadArchivers() {
		institutionsService.getAllArchivers(new AsyncCallback<ArrayList<ArchiverDTO>>() {

			@Override
			public void onSuccess(ArrayList<ArchiverDTO> result) {
				archivers = result;
				reload();
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("error: " + caught.getMessage());
			}
		});

	}

	private void reload() {
		clear();
		digitalDocForm = new DigitalDocumentForm(archivers);
		add(backToEntityTypeSelectionButton());
		add(intelectualEntHeading());
		add(intelectualEntForm);
		if (srcDocform != null) {
			add(srcDocform);
		}
		add(digitalDocumentHeading());
		add(digitalDocForm);
		add(technicalMetadataForm);
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

	private Button backToEntityTypeSelectionButton() {
		return new Button("zpět na výběr typu intelektuální entity", new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				superPanel.reload(new SelectEntityTypePanel(superPanel));
			}
		});
	}

}
