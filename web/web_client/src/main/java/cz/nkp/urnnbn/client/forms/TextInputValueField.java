package cz.nkp.urnnbn.client.forms;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import cz.nkp.urnnbn.shared.validation.Validator;

public class TextInputValueField extends Field {

	private Label label = new Label();
	private TextBox textBox = new TextBox();
	private final Validator validator;
	private final boolean mandatory;

	public TextInputValueField(Validator validator, String labelText, boolean mandatory) {
		this(validator, labelText, mandatory, true);
	}

	public TextInputValueField(Validator validator, String labelText, boolean mandatory, boolean withLabelInputboxSeparator) {
		this(validator, labelText, null, mandatory, withLabelInputboxSeparator);
	}

	public TextInputValueField(Validator validator, String labelText, Object value, boolean mandatory) {
		this(validator, labelText, value, mandatory, true);
	}

	public TextInputValueField(Validator validator, String labelText, Object value, boolean mandatory, boolean withLabelInputboxSeparator) {
		this.validator = validator;
		this.mandatory = mandatory;
		if (withLabelInputboxSeparator) {
			label.setText(labelText + ": ");
		} else {
			label.setText(labelText);
		}
		label.setStyleName(css.formLabel());
		// todo: sirku textBoxu nastavovat parametrem konstruktoru
		if (value != null) {
			textBox.setValue(value.toString());
		}
//		textBox.setWidth("200px");
		textBox.addKeyUpHandler(textChangeHandler());
	}

	private KeyUpHandler textChangeHandler() {
		return new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				validate();
			}
		};
	}

	@Override
	public TextBox getContentWidget() {
		return textBox;
	}

	public boolean validate() {
		if (textBox.getText().isEmpty()) {
			if (mandatory) {
				activateValidationWarning(messages.validationEmptyField());
				return false;
			} else {
				clearValidationWarning();
				return true;
			}
		} else {
			boolean valueValid = validator.isValid(textBox.getText());
			if (valueValid) {
				clearValidationWarning();
				return true;
			} else {
				activateValidationWarning(validator.localizedErrorMessage(textBox.getText()));
				return false;
			}
		}
	}

	private void activateValidationWarning(String errorMessage) {
		textBox.setTitle(errorMessage);
		textBox.setStyleName(css.invalidTextBoxData());

	}

	private void clearValidationWarning() {
		textBox.setStyleName(css.validTextBoxData());
		// TODO: otestovat, jestli tady nemusi byt empty String
		textBox.setTitle(null);
	}

	@Override
	public void disable() {
		textBox.setEnabled(false);
	}

	@Override
	public void enable() {
		textBox.setEnabled(true);
	}

	@Override
	public Widget getLabelWidget() {
		return label;
	}

	@Override
	public boolean validValueInserted() {
		return validate();
	}

	@Override
	public Object getInsertedValue() {
		if (textBox.getText().isEmpty()) {
			return null;
		} else {
			return textBox.getText();
		}
	}
}
