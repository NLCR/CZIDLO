package cz.nkp.urnnbn.client.forms;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class LabelField extends Field {

	private final Widget label;

	public LabelField(String labelText) {
		// this.label = new Label(labelText);
		this.label = new HTML("<b>" + labelText + "</b>");
	}

	@Override
	public Widget getLabelWidget() {
		return label;
	}

	@Override
	public Widget getContentWidget() {
		return null;
	}

	@Override
	public boolean validValueInserted() {
		return true;
	}

	@Override
	public Object getInsertedValue() {
		return null;
	}

	@Override
	public void disable() {
		// nothing
	}

	@Override
	public void enable() {
		// nothing
	}

}
