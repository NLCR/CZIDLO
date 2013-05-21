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
import com.google.gwt.user.client.ui.Widget;

import cz.nkp.urnnbn.client.AbstractDialogBox;
import cz.nkp.urnnbn.client.forms.institutions.ArchiverForm;
import cz.nkp.urnnbn.client.services.InstitutionsService;
import cz.nkp.urnnbn.client.services.InstitutionsServiceAsync;
import cz.nkp.urnnbn.shared.dto.ArchiverDTO;

public class AddArchiverDialogBox extends AbstractDialogBox {

	private final InstitutionsServiceAsync institutionsService = GWT.create(InstitutionsService.class);
	private final InstitutionListPanel superPanel;
	private final ArchiverForm archiverForm = new ArchiverForm();
	private final Label errorLabel = errorLabel(320);

	public AddArchiverDialogBox(InstitutionListPanel superPanel) {
		this.superPanel = superPanel;
		setText(constants.archiver() + " - " + constants.recordInsertion());
		setAnimationEnabled(true);
		setWidget(contentPanel());
		center();
	}

	private Widget contentPanel() {
		VerticalPanel result = new VerticalPanel();
		result.add(archiverForm);
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
				if (archiverForm.isFilledCorrectly()) {
					institutionsService.saveArchiver(archiverForm.getDto(), new AsyncCallback<ArchiverDTO>() {

						@Override
						public void onSuccess(ArchiverDTO result) {
							superPanel.loadArchivers();
							AddArchiverDialogBox.this.hide();
						}

						@Override
						public void onFailure(Throwable caught) {
							errorLabel.setText(constants.serverError() + ": " + caught.getMessage());
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
				AddArchiverDialogBox.this.hide();
			}
		});
	}
}
