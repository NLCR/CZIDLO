package cz.nkp.urnnbn.client.forms;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import cz.nkp.urnnbn.client.i18n.ConstantsImpl;

public abstract class Form extends VerticalPanel {
	protected ConstantsImpl constants = GWT.create(ConstantsImpl.class);
	protected FormFields fields;

	public Form() {}

	// must be called by subclass once data is initialized in subclass
	// constructor
	protected void initForm() {
		fields = buildFields();
		renderForm();
	}

	public abstract FormFields buildFields();

	void renderForm() {
		for (int i = 0; i < fields.size(); i++) {
			add(fieldPanel(fields.getFieldByPosition(i)));
		}
	}

	private Widget fieldPanel(Field field) {
		HorizontalPanel result = new HorizontalPanel();
		Widget labelWidget = field.getLabelWidget();
		if (labelWidget != null) {
			result.add(labelWidget);
		}
		// TODO: odsazeni resit stylem
		result.add(new HTML("&nbsp"));
		result.add(field.getContentWidget());
		return result;
	}

	public void frost() {
		for (int i = 0; i < fields.size(); i++) {
			fields.getFieldByPosition(i).disable();
		}
	}

	public boolean isFilledCorrectly() {
		for (int i = 0; i < fields.size(); i++) {
			if (!fields.getFieldByPosition(i).validValueInserted()) {
				return false;
			}
		}
		return true;
	}

	public abstract Object getDto();
}
