package cz.nkp.urnnbn.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;

import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.i18n.MessagesImpl;
import cz.nkp.urnnbn.client.resources.DialogsCss;
import cz.nkp.urnnbn.client.resources.Resources;

public class AbstractDialogBox extends DialogBox {

    protected final ConstantsImpl constants = GWT.create(ConstantsImpl.class);
    protected final DialogsCss css = loadCss();
    protected final MessagesImpl messages = GWT.create(MessagesImpl.class);

    public Label errorLabel(int size) {
        Label result = new Label();
        result.setWidth(size + "px");
        result.addStyleName(css.errorLabel());
        return result;
    }

    private DialogsCss loadCss() {
        Resources resources = GWT.create(Resources.class);
        DialogsCss result = resources.DialogsCss();
        result.ensureInjected();
        return result;
    }
}
