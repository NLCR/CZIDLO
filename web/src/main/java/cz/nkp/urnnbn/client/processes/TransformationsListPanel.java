package cz.nkp.urnnbn.client.processes;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.services.ProcessService;
import cz.nkp.urnnbn.client.services.ProcessServiceAsync;
import cz.nkp.urnnbn.shared.dto.process.XmlTransformationDTO;

public class TransformationsListPanel extends ScrollPanel {

	private final ProcessServiceAsync processService = GWT.create(ProcessService.class);
	private final ConstantsImpl constants = GWT.create(ConstantsImpl.class);
	private final List<XmlTransformationDTO> transformations;
	private final XmlTransformationsPanel superPanel;

	public TransformationsListPanel(XmlTransformationsPanel superPanel, List<XmlTransformationDTO> transformations) {
		super();
		this.superPanel = superPanel;
		this.transformations = transformations;
		setWidth("600px");
		setHeight("100px");
		add(contentPanel());
	}

	private Widget contentPanel() {
		VerticalPanel result = new VerticalPanel();
		result.add(transformationsListHeader());
		for (XmlTransformationDTO transformation : transformations) {
			result.add(transforamtionWidget(transformation));
		}
		return result;
	}

	private Widget transformationsListHeader() {
		HorizontalPanel panel = new HorizontalPanel();
		panel.setWidth("500px");

		// title
		Widget titelLabel = headerFormated(constants.processOaiAdapterTransformationTitle());
		panel.add(titelLabel);
		panel.setCellWidth(titelLabel, "20%");

		// description
		Widget descriptionLabel = headerFormated(constants.processOaiAdapterTransformationDescription());
		panel.add(descriptionLabel);
		panel.setCellWidth(descriptionLabel, "35%");

		// created
		Widget createdLabel = headerFormated(constants.processOaiAdapterTransformationCreated());
		panel.add(createdLabel);
		panel.setCellWidth(createdLabel, "30%");

		// delete button
		Widget deleteButtonLabel = headerFormated("");
		panel.add(deleteButtonLabel);
		panel.setCellWidth(deleteButtonLabel, "15%");

		return panel;
	}

	private Widget headerFormated(String string) {
		return new HTML("<div style=\"color:grey\">" + string + "</style>");
	}

	private Widget transforamtionWidget(XmlTransformationDTO transformation) {
		HorizontalPanel panel = new HorizontalPanel();
		panel.setWidth("500px");
		// panel.setWidth("100%");

		// name
		Widget nameLabel = new Label(transformation.getName());
		panel.add(nameLabel);
		panel.setCellWidth(nameLabel, "20%");

		// description
		Widget descriptionLabel = new Label(transformation.getDescription());
		panel.add(descriptionLabel);
		panel.setCellWidth(descriptionLabel, "35%");

		// created
		Label createdLabel = new Label(transformation.getCreated());
		panel.add(createdLabel);
		panel.setCellWidth(createdLabel, "30%");

		// // TODO: just for testing
		// // type
		// Label typeLabel = new Label(transformation.getType().toString());
		// panel.add(typeLabel);
		// panel.setCellWidth(typeLabel, "10%");

		// //TODO: implement if required
		// download template button
		// Widget downloadTemplateButton =
		// downloadTemplateButton(transformation);
		// panel.add(downloadTemplateButton);
		// panel.setCellWidth(downloadTemplateButton, "15%");

		// remove button
		Widget removeButton = removeButton(transformation);
		panel.add(removeButton);
		panel.setCellWidth(removeButton, "15%");

		return panel;
	}

	// private Widget downloadTemplateButton(XmlTransformationDTO
	// transformation) {
	// // TODO Auto-generated method stub
	// return null;
	// }

	private Widget removeButton(final XmlTransformationDTO transformation) {
		return new Button(constants.delete(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				processService.deleteXmlTransformation(transformation, new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						superPanel.reloadTransformations();
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(constants.serverError() + ": " + caught.getMessage());
					}
				});
			}
		});
	}

}
