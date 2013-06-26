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
import cz.nkp.urnnbn.client.forms.institutions.CatalogForm;
import cz.nkp.urnnbn.client.services.InstitutionsService;
import cz.nkp.urnnbn.client.services.InstitutionsServiceAsync;
import cz.nkp.urnnbn.shared.dto.CatalogDTO;

public class EditCatalogDialogBox extends AbstractDialogBox {

	private final InstitutionsServiceAsync institutionsService = GWT.create(InstitutionsService.class);
	private final RegistrarDetailsPanel superPanel;
	private final CatalogForm catalogForm;
	private final Label errorLabel = errorLabel(320);

	public EditCatalogDialogBox(RegistrarDetailsPanel superPanel, CatalogDTO catalog) {
		this.superPanel = superPanel;
		catalogForm = new CatalogForm(catalog);
		setText(constants.catalog() + " - " + constants.recordAdjustment());
		setAnimationEnabled(true);
		setWidget(contentPanel());
		center();
	}

	private Panel contentPanel() {
		VerticalPanel result = new VerticalPanel();
		result.add(catalogForm);
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
				if (catalogForm.isFilledCorrectly()) {
					institutionsService.updateCatalog(catalogForm.getDto(), new AsyncCallback<Void>() {

						@Override
						public void onSuccess(Void result) {
							superPanel.updateCatalog(catalogForm.getDto());
							EditCatalogDialogBox.this.hide();
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
				EditCatalogDialogBox.this.hide();
			}
		});
	}

}
