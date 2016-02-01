package cz.nkp.urnnbn.client.editRecord;

import java.util.ArrayList;

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
import cz.nkp.urnnbn.client.DigitalInstanceRefreshable;
import cz.nkp.urnnbn.client.forms.digitalDocument.DigitalInstanceForm;
import cz.nkp.urnnbn.client.services.DataService;
import cz.nkp.urnnbn.client.services.DataServiceAsync;
import cz.nkp.urnnbn.shared.dto.DigitalInstanceDTO;
import cz.nkp.urnnbn.shared.dto.DigitalLibraryDTO;
import cz.nkp.urnnbn.shared.dto.UrnNbnDTO;

public class EditDigitalInstanceDialogBox extends AbstractDialogBox {

    private final DataServiceAsync dataService = GWT.create(DataService.class);
    private final DigitalInstanceRefreshable superPanel;
    private final DigitalInstanceForm form;
    private final Label errorLabel = errorLabel(320);
    private final UrnNbnDTO urn;

    public EditDigitalInstanceDialogBox(DigitalInstanceRefreshable superPanel, UrnNbnDTO urn, DigitalInstanceDTO originalDto,
            ArrayList<DigitalLibraryDTO> libraries) {
        super();
        this.superPanel = superPanel;
        this.form = new DigitalInstanceForm(originalDto, libraries);
        this.urn = urn;
        setText(constants.digitalInstance() + " - " + constants.edit());
        setAnimationEnabled(true);
        setWidget(contentPanel());
        center();
    }

    private Widget contentPanel() {
        VerticalPanel result = new VerticalPanel();
        result.add(form);
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
        return new Button(constants.save(), new ClickHandler() {

            public void onClick(ClickEvent event) {
                if (form.isFilledCorrectly()) {
                    final DigitalInstanceDTO di = form.getDto();
                    dataService.updateDigitalInstance(urn, di, new AsyncCallback<Void>() {

                        public void onSuccess(Void result) {
                            EditDigitalInstanceDialogBox.this.hide();
                            superPanel.refresh(di);
                        }

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
                EditDigitalInstanceDialogBox.this.hide();
            }
        });
    }

}
