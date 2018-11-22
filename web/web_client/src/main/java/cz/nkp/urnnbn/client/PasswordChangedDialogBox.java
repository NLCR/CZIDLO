package cz.nkp.urnnbn.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;

/**
 * Created by Martin Řehánek on 22.11.18.
 */
public class PasswordChangedDialogBox extends AbstractDialogBox {

    public PasswordChangedDialogBox() {
        // TODO: 22.11.18 nastylovat, vypadá to hrozně
        // TODO: 22.11.18 i18n
        /*String title = constants.changPasswordDialogTitle();
        setTitle(title);
        setText(title);*/
        setAnimationEnabled(true);
        setWidget(contentPanel());
        center();
    }

    private IsWidget contentPanel() {
        VerticalPanel panel = new VerticalPanel();
        // TODO: 22.11.18 i18n
        panel.add(new Label("Heslo bylo změněno"));
        panel.add(buttons());
        return panel;
    }

    private Panel buttons() {
        HorizontalPanel result = new HorizontalPanel();
        result.add(closeButton());
        return result;
    }

    private Button closeButton() {
        // TODO: 22.11.18 tady mozna radeji tlacitko OK
        return new Button(constants.close(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                PasswordChangedDialogBox.this.hide();
            }
        });
    }

}
