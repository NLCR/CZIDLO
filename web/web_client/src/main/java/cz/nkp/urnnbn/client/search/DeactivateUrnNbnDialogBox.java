package cz.nkp.urnnbn.client.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import cz.nkp.urnnbn.client.forms.digitalDocument.DeactivateUrnNbnForm;
import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.i18n.MessagesImpl;
import cz.nkp.urnnbn.client.services.DataService;
import cz.nkp.urnnbn.client.services.DataServiceAsync;
import cz.nkp.urnnbn.shared.dto.UrnNbnDTO;

public class DeactivateUrnNbnDialogBox extends DialogBox {
    private final ConstantsImpl constants = GWT.create(ConstantsImpl.class);
    private final MessagesImpl messages = GWT.create(MessagesImpl.class);
    private final DataServiceAsync dataService = GWT.create(DataService.class);
    private final SearchPanel superPanel;
    private final DeactivateUrnNbnForm form;
    private final Label errorLabel;

    public DeactivateUrnNbnDialogBox(SearchPanel superPanel, UrnNbnDTO urn) {
        this.superPanel = superPanel;
        this.form = new DeactivateUrnNbnForm(urn);
        this.errorLabel = errorLabel();
        setText(constants.urnNbnDeactivationDialogTitle());
        setAnimationEnabled(true);
        setPopupPosition(100, 100);
        setWidget(contentPanel(urn));
    }

    private Label errorLabel() {
        Label result = new Label();
        result.setWidth("320px");
        // errorLabel.addStyleName(css.errorLabel());
        return result;
    }

    private Panel contentPanel(UrnNbnDTO urn) {
        VerticalPanel result = new VerticalPanel();
        result.add(new HTML(messages.confirmUrnNbnDeactivation(urn.toString())));
        result.add(form);
        result.add(errorLabel);
        result.add(buttons());
        return result;
    }

    private Panel buttons() {
        HorizontalPanel buttons = new HorizontalPanel();
        buttons.add(deactivateButton());
        buttons.add(closeButton());
        return buttons;
    }

    private Button deactivateButton() {
        return new Button(constants.deactivate(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (form.isFilledCorrectly()) {
                    dataService.deactivateUrnNbn(form.getDto(), new AsyncCallback<Void>() {

                        @Override
                        public void onSuccess(Void result) {
                            superPanel.refresh();
                            DeactivateUrnNbnDialogBox.this.hide();
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

    private Widget closeButton() {
        return new Button(constants.close(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                DeactivateUrnNbnDialogBox.this.hide();
            }
        });
    }

}
