package cz.nkp.urnnbn.client.institutions;

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
import cz.nkp.urnnbn.client.forms.institutions.DigitalLibraryForm;
import cz.nkp.urnnbn.client.services.InstitutionsService;
import cz.nkp.urnnbn.client.services.InstitutionsServiceAsync;
import cz.nkp.urnnbn.shared.dto.DigitalLibraryDTO;

public class AddDigitalLibraryDialogBox extends AbstractDialogBox {

	private final InstitutionsServiceAsync institutionsService = GWT.create(InstitutionsService.class);
	private final RegistrarDetailsPanel superPanel;
	private final DigitalLibraryForm digitalLibraryForm = new DigitalLibraryForm();
	private final Label errorLabel = errorLabel(320);

	public AddDigitalLibraryDialogBox(RegistrarDetailsPanel superPanel) {
		this.superPanel = superPanel;
		setText(constants.digitalLibrary() + " - " + constants.recordInsertion());
		setAnimationEnabled(true);
		setWidget(contentPanel());
		center();
	}

	private Panel contentPanel() {
		VerticalPanel result = new VerticalPanel();
		result.add(digitalLibraryForm);
		result.add(buttons());
		result.add(errorLabel);
		return result;
	}

	private Panel buttons() {
		HorizontalPanel result = new HorizontalPanel();
		result.add(saveButton());
		result.add(closeButton());
		return result;
	}

	private Button saveButton() {
		return new Button(constants.insert(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (digitalLibraryForm.isFilledCorrectly()) {
					DigitalLibraryDTO dto = digitalLibraryForm.getDto();
					dto.setRegistrarId(superPanel.getRegistrarId());
					institutionsService.saveDigitalLibrary(dto, new AsyncCallback<DigitalLibraryDTO>() {

						@Override
						public void onSuccess(DigitalLibraryDTO result) {
							superPanel.addLibrary(result);
							AddDigitalLibraryDialogBox.this.hide();
						}

						@Override
						public void onFailure(Throwable caught) {
							errorLabel.setText(messages.serverError(caught.getMessage()));
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
				AddDigitalLibraryDialogBox.this.hide();
			}
		});
	}
}
