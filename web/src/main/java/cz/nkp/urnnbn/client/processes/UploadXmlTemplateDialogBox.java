package cz.nkp.urnnbn.client.processes;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

import cz.nkp.urnnbn.client.AbstractDialogBox;
import cz.nkp.urnnbn.client.forms.processes.XmlTemplateForm;
import cz.nkp.urnnbn.client.services.ProcessService;
import cz.nkp.urnnbn.client.services.ProcessServiceAsync;
import cz.nkp.urnnbn.shared.dto.process.XmlTransformationDTOType;

public class UploadXmlTemplateDialogBox extends AbstractDialogBox {

	private final ProcessServiceAsync processService = GWT.create(ProcessService.class);
	private final XmlTransformationsPanel superPanel;
	private final XmlTemplateForm xmlTemplateForm;

	private final Label errorLabel = errorLabel(320);

	public UploadXmlTemplateDialogBox(XmlTransformationsPanel superPanel, XmlTransformationDTOType type) {
		this.superPanel = superPanel;
		setText(title(type));
		xmlTemplateForm = new XmlTemplateForm(type);
		setAnimationEnabled(true);
		setWidget(contentPanel());
		center();
	}

	private String title(XmlTransformationDTOType type) {
		String typeStr = null;
		switch (type) {
		case DIGITAL_DOCUMENT_REGISTRATION:
			typeStr = constants.DIGITAL_DOCUMENT_REGISTRATION();
			break;
		case DIGITAL_INSTANCE_IMPORT:
			typeStr = constants.DIGITAL_INSTANCE_IMPORT();
			break;
		}
		return constants.uploadXslTemplate() + " (" + typeStr + ")";
	}

	private Panel contentPanel() {
		VerticalPanel result = new VerticalPanel();
		result.add(xmlTemplateForm);
		result.add(buttonsPanel());
		result.add(errorLabel);
		return result;
	}

	private Panel buttonsPanel() {
		HorizontalPanel result = new HorizontalPanel();
		result.setSpacing(10);
		result.add(uploadTemplateButton());
		result.add(closeButton());
		return result;
	}

	private Button uploadTemplateButton() {
		return new Button(constants.upload(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (xmlTemplateForm.isFilledCorrectly()) {
					processService.createXmlTransformation(xmlTemplateForm.getDto(), new AsyncCallback<Void>() {

						@Override
						public void onSuccess(Void result) {
							UploadXmlTemplateDialogBox.this.hide();
							superPanel.reloadTransformations();
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
				UploadXmlTemplateDialogBox.this.hide();
			}
		});
	}

}
