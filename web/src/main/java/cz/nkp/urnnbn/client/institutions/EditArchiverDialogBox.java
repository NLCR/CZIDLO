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

public class EditArchiverDialogBox extends AbstractDialogBox {

	private final InstitutionsServiceAsync institutionsService = GWT.create(InstitutionsService.class);
	private final InstitutionListPanel superPanel;
	private final ArchiverForm archiverForm;
	private final Label errorLabel = errorLabel(320);

	public EditArchiverDialogBox(InstitutionListPanel superPanel, ArchiverDTO archiver) {
		this.superPanel = superPanel;
		archiverForm = new ArchiverForm(archiver);
		setText(constants.archiver() + " - " + constants.recordAdjustment());
		setAnimationEnabled(true);
		setWidget(contentPanel());
		center();
	}

	private Panel contentPanel() {
		VerticalPanel result = new VerticalPanel();
		result.add(archiverForm);
		result.add(buttons());
		result.add(errorLabel);
		return result;
	}

	private Panel buttons() {
		HorizontalPanel buttons = new HorizontalPanel();
		buttons.add(saveButton());
		buttons.add(closeButtion());
		return buttons;
	}

	private Widget saveButton() {
		return new Button(constants.save(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (archiverForm.isFilledCorrectly()) {
					institutionsService.updateArchiver(archiverForm.getDto(), new AsyncCallback<Void>() {

						@Override
						public void onSuccess(Void result) {
							superPanel.loadArchivers();
							EditArchiverDialogBox.this.hide();
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

	private Widget closeButtion() {
		return new Button(constants.close(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				EditArchiverDialogBox.this.hide();
			}
		});
	}
}
