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
import cz.nkp.urnnbn.client.forms.institutions.CatalogForm;
import cz.nkp.urnnbn.client.services.InstitutionsService;
import cz.nkp.urnnbn.client.services.InstitutionsServiceAsync;
import cz.nkp.urnnbn.shared.dto.CatalogDTO;

public class AddCatalogDialogBox extends AbstractDialogBox {

    private final InstitutionsServiceAsync institutionsService = GWT.create(InstitutionsService.class);
    private final RegistrarDetailsPanel superPanel;
    private final CatalogForm catalogForm = new CatalogForm();
    private final Label errorLabel = errorLabel(320);

    public AddCatalogDialogBox(RegistrarDetailsPanel superPanel) {
        this.superPanel = superPanel;
        setText(constants.catalog() + " - " + constants.recordInsertion());
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
        HorizontalPanel result = new HorizontalPanel();
        result.add(saveButton());
        result.add(closeButton());
        return result;
    }

    private Button saveButton() {
        return new Button(constants.insert(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (catalogForm.isFilledCorrectly()) {
                    CatalogDTO dtoFromForm = catalogForm.getDto();
                    dtoFromForm.setRegistrarId(superPanel.getRegistrarId());
                    institutionsService.saveCatalog(dtoFromForm, new AsyncCallback<CatalogDTO>() {

                        @Override
                        public void onSuccess(CatalogDTO result) {
                            superPanel.addCatalog(result);
                            AddCatalogDialogBox.this.hide();
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
                AddCatalogDialogBox.this.hide();
            }
        });
    }

}
