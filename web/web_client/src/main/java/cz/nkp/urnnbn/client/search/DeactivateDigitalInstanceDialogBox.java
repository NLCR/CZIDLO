package cz.nkp.urnnbn.client.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import cz.nkp.urnnbn.client.AbstractDialogBox;
import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.i18n.MessagesImpl;
import cz.nkp.urnnbn.client.services.DataService;
import cz.nkp.urnnbn.client.services.DataServiceAsync;
import cz.nkp.urnnbn.shared.dto.DigitalInstanceDTO;

public class DeactivateDigitalInstanceDialogBox extends AbstractDialogBox {
    private final ConstantsImpl constants = GWT.create(ConstantsImpl.class);
    private final MessagesImpl messages = GWT.create(MessagesImpl.class);
    private final DataServiceAsync dataService = GWT.create(DataService.class);
    private final SearchTab superPanel;
    private final Label errorLabel;
    private final DigitalInstanceDTO digitalInstance;

    public DeactivateDigitalInstanceDialogBox(SearchTab superPanel, DigitalInstanceDTO digitalInstance) {
        this.superPanel = superPanel;
        this.digitalInstance = digitalInstance;
        this.errorLabel = errorLabel(320);
        setText(constants.diDeactivationDialogTitle());
        setAnimationEnabled(true);
        setPopupPosition(100, 100);
        setWidget(contentPanel());
    }

    private Panel contentPanel() {
        VerticalPanel result = new VerticalPanel();
        result.add(new HTML(constants.diDeactivationDialogText()));
        String html = "<a target=\"_blank\" href=\"" + digitalInstance.getUrl() + "\"" + "\">" + digitalInstance.getUrl() + "</a>";
        result.add(new HTML(html));
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
                dataService.deactivateDigitalInstance(digitalInstance, new AsyncCallback<Void>() {

                    @Override
                    public void onSuccess(Void result) {
                        superPanel.refresh();
                        DeactivateDigitalInstanceDialogBox.this.hide();
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        errorLabel.setText(messages.serverError(caught.getMessage()));
                    }
                });
            }
        });
    }

    private Widget closeButton() {
        return new Button(constants.close(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                DeactivateDigitalInstanceDialogBox.this.hide();
            }
        });
    }

}
