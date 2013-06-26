package cz.nkp.urnnbn.client.search;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import cz.nkp.urnnbn.client.forms.digitalDocument.DigitalDocumentForm;
import cz.nkp.urnnbn.client.forms.digitalDocument.TechnicalMetadataForm;
import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.services.DataService;
import cz.nkp.urnnbn.client.services.DataServiceAsync;
import cz.nkp.urnnbn.client.services.InstitutionsService;
import cz.nkp.urnnbn.client.services.InstitutionsServiceAsync;
import cz.nkp.urnnbn.shared.dto.ArchiverDTO;
import cz.nkp.urnnbn.shared.dto.DigitalDocumentDTO;

public class EditDigitalDocumentDialogBox extends DialogBox {

	private final ConstantsImpl constants = GWT.create(ConstantsImpl.class);
	private final InstitutionsServiceAsync institutionsService = GWT.create(InstitutionsService.class);
	private final DataServiceAsync dataService = GWT.create(DataService.class);
	private final SearchPanel superPanel;
	private Label errorLabel = errorLabel();
	private DigitalDocumentForm digDocForm;
	private TechnicalMetadataForm technicalMetadataForm;

	private Label errorLabel() {
		Label result = new Label();
		result.setWidth("320px");
		// errorLabel.addStyleName(css.errorLabel());
		return result;
	}

	public EditDigitalDocumentDialogBox(final DigitalDocumentDTO digDoc, SearchPanel superPanel) {
		this.superPanel = superPanel;
		institutionsService.getAllArchivers(new AsyncCallback<ArrayList<ArchiverDTO>>() {

			@Override
			public void onSuccess(ArrayList<ArchiverDTO> result) {
				digDocForm = new DigitalDocumentForm(digDoc, digDoc.getRegistrar(), result, digDoc.getArchiver());
				setWidget(contentPanel());
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("error: " + caught.getMessage());
			}
		});
		technicalMetadataForm = new TechnicalMetadataForm(digDoc.getTechnicalMetadata());
		String title = "úprava digitálního dokumentu";
		setText(title);
		setAnimationEnabled(true);
		setPopupPosition(100, 100);
	}

	private Panel contentPanel() {
		VerticalPanel result = new VerticalPanel();
		result.add(digDocForm);
		result.add(technicalMetadataForm);
		result.add(errorLabel);
		result.add(buttons());
		return result;
	}

	private Panel buttons() {
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.add(saveButton());
		buttons.add(closeButton());
		return buttons;
	}

	private Button saveButton() {
		return new Button(constants.save(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (digDocForm.isFilledCorrectly() && technicalMetadataForm.isFilledCorrectly()) {
					dataService.updateDigitalDocument(digDocForm.getDto(), technicalMetadataForm.getDto(), new AsyncCallback<Void>() {

						@Override
						public void onSuccess(Void result) {
							// TODO: refresh search results
							superPanel.refresh();
							EditDigitalDocumentDialogBox.this.hide();
						}

						@Override
						public void onFailure(Throwable caught) {
							errorLabel.setText("server error: " + caught.getMessage());
						}
					});
				}
			}
		});
	}

	private Widget closeButton() {
		return new Button(constants.close(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				EditDigitalDocumentDialogBox.this.hide();
			}
		});
	}
}
