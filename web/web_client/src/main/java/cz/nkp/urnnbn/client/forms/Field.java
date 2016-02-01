package cz.nkp.urnnbn.client.forms;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.i18n.MessagesImpl;
import cz.nkp.urnnbn.client.resources.DialogsCss;
import cz.nkp.urnnbn.client.resources.Resources;

public abstract class Field {

    final protected ConstantsImpl constants = GWT.create(ConstantsImpl.class);
    final protected MessagesImpl messages = GWT.create(MessagesImpl.class);
    final protected DialogsCss css = loadCss();

    private DialogsCss loadCss() {
        Resources resources = GWT.create(Resources.class);
        DialogsCss result = resources.DialogsCss();
        result.ensureInjected();
        return result;
    }

    public abstract Widget getLabelWidget();

    public abstract Widget getContentWidget();

    public abstract boolean validValueInserted();

    public abstract Object getInsertedValue();

    public abstract void disable();

    public abstract void enable();

}
