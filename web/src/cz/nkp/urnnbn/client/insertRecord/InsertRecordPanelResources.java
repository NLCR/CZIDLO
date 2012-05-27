package cz.nkp.urnnbn.client.insertRecord;

import com.google.gwt.core.client.GWT;

import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.i18n.MessagesImpl;
import cz.nkp.urnnbn.client.resources.InsertRecordPanelCss;
import cz.nkp.urnnbn.client.resources.Resources;

public class InsertRecordPanelResources {

	static InsertRecordPanelCss css() {
		Resources resources = GWT.create(Resources.class);
		InsertRecordPanelCss result = resources.InsertRecordPanelCss();
		result.ensureInjected();
		return result;
	}

	static ConstantsImpl constants() {
		return GWT.create(ConstantsImpl.class);
	}

	static MessagesImpl messages() {
		return GWT.create(MessagesImpl.class);
	}
}
