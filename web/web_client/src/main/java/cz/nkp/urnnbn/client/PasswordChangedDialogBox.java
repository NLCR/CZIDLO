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
        /*String title = constants.changePasswordDialogTitle();
        setTitle(title);
        setText(title);*/
        setAnimationEnabled(true);
        setWidget(contentPanel());
        center();
    }

    private IsWidget contentPanel() {
        VerticalPanel panel = new VerticalPanel();
        panel.add(new Label(constants.changePasswordDialogChangedLabel()));
        panel.add(buttons());
        return panel;
    }

    private Panel buttons() {
        HorizontalPanel result = new HorizontalPanel();
        result.add(closeButton());
        return result;
    }

    private Button closeButton() {
        return new Button(constants.ok(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                PasswordChangedDialogBox.this.hide();
            }
        });
    }

}
