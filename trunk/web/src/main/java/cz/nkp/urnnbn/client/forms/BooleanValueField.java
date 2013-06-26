package cz.nkp.urnnbn.client.forms;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class BooleanValueField extends Field {

	private Label label = new Label();
	private CheckBox checkBox;

	public BooleanValueField(String labelText) {
		this(labelText, null);
	}

	public BooleanValueField(String labelText, Boolean value) {
		label.setText(labelText);
		label.setStyleName(css.formLabel());
		checkBox = new CheckBox();
		if (value != null) {
			checkBox.setValue(value);
		} else {
			checkBox.setValue(false);
		}
	}

	@Override
	public void disable() {
		checkBox.setEnabled(false);
	}

	@Override
	public void enable() {
		checkBox.setEnabled(true);
	}

	@Override
	public Widget getContentWidget() {
		return checkBox;
	}

	@Override
	public Widget getLabelWidget() {
		return label;
	}

	@Override
	public boolean validValueInserted() {
		return true;
	}

	@Override
	public Boolean getInsertedValue() {
		return checkBox.getValue();
	}
}
