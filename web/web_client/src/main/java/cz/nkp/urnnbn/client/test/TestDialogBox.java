package cz.nkp.urnnbn.client.test;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import cz.nkp.urnnbn.client.forms.intEntities.AnalyticalForm;
import cz.nkp.urnnbn.client.forms.intEntities.SourceDocumentForm;
import cz.nkp.urnnbn.shared.dto.ie.AnalyticalDTO;
import cz.nkp.urnnbn.shared.dto.ie.PrimaryOriginatorDTO;
import cz.nkp.urnnbn.shared.dto.ie.PrimaryOriginatorType;

public class TestDialogBox extends DialogBox {

    private AnalyticalForm analyticalForm;
    private SourceDocumentForm srcDocForm;

    public TestDialogBox() {
        setPopupPosition(5, 5);
        VerticalPanel vertical = new VerticalPanel();
        add(vertical);
        // vertical.add(new MonographForm().getGrid());
        AnalyticalDTO dto = new AnalyticalDTO();
        dto.setTitle("clanek");
        dto.setSubTitle("aneb blabla");
        PrimaryOriginatorDTO originator = new PrimaryOriginatorDTO();
        originator.setType(PrimaryOriginatorType.EVENT);
        originator.setValue("OH Soci");
        // vertical.add(new MonographForm());
        analyticalForm = new AnalyticalForm(dto, originator);
        vertical.add(analyticalForm);
        vertical.add(new Label("zdrojovy dokument"));
        srcDocForm = new SourceDocumentForm();
        vertical.add(srcDocForm);
        vertical.add(validateButton());
        vertical.add(closeButton());
    }

    private Button validateButton() {
        return new Button("validate", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                analyticalForm.isFilledCorrectly();
                srcDocForm.isFilledCorrectly();
            }
        });
    }

    private Button closeButton() {
        return new Button("close", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                TestDialogBox.this.hide();
            }
        });
    }
}
