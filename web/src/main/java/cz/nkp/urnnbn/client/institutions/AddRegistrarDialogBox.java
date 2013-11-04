package cz.nkp.urnnbn.client.institutions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import cz.nkp.urnnbn.client.AbstractDialogBox;
import cz.nkp.urnnbn.client.forms.institutions.RegistrarForm;
import cz.nkp.urnnbn.client.services.InstitutionsService;
import cz.nkp.urnnbn.client.services.InstitutionsServiceAsync;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;

public class AddRegistrarDialogBox extends AbstractDialogBox {

	private final InstitutionsServiceAsync institutionsService = GWT.create(InstitutionsService.class);
	private final InstitutionListPanel superPanel;
	private final RegistrarForm registrarForm = new RegistrarForm();
	private final Label errorLabel = errorLabel(320);

	public AddRegistrarDialogBox(InstitutionListPanel superPanel) {
		this.superPanel = superPanel;
		setText(constants.registrar() + " - " + constants.recordInsertion());
		setAnimationEnabled(true);
		setWidget(contentPanel());
		center();
	}

	private Widget contentPanel() {
		VerticalPanel result = new VerticalPanel();
		result.add(registrarForm);
		result.add(buttons());
		result.add(errorLabel);
		return result;
	}

	private Widget buttons() {
		HorizontalPanel result = new HorizontalPanel();
		result.add(saveButton());
		result.add(closeButton());
		return result;
	}

	private Button saveButton() {
		return new Button(constants.insert(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (registrarForm.isFilledCorrectly()) {
					institutionsService.saveRegistrar(registrarForm.getDto(), new AsyncCallback<RegistrarDTO>() {

						@Override
						public void onSuccess(RegistrarDTO result) {
							superPanel.loadRegistrars();
							AddRegistrarDialogBox.this.hide();
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
				AddRegistrarDialogBox.this.hide();
			}
		});
	}

}
