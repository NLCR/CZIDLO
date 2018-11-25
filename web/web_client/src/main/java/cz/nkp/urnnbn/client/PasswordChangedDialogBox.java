package cz.nkp.urnnbn.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;

/**
 * Created by Martin Řehánek on 22.11.18.
 */
public class PasswordChangedDialogBox extends AbstractDialogBox {

    public PasswordChangedDialogBox() {
        String title = constants.changePasswordDialogTitle();
        setTitle(title);
        setText(title);
        setAnimationEnabled(true);
        setWidget(buildWidget());
        center();
    }

    private IsWidget buildWidget() {
        VerticalPanel panel = new VerticalPanel();
        panel.add(contentPanel());
        panel.add(buttons());
        return panel;
    }

    private IsWidget contentPanel() {
        VerticalPanel panel = new VerticalPanel();
        panel.setHeight("50px");
        panel.setWidth("200px");
        panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        panel.add(new Label(constants.changePasswordDialogChangedLabel()));
        return panel;
    }

    private Panel buttons() {
        HorizontalPanel result = new HorizontalPanel();
        result.setWidth("100%");
        result.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        result.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
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
