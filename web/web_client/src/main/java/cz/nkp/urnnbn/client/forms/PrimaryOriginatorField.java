package cz.nkp.urnnbn.client.forms;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.shared.dto.ie.PrimaryOriginatorDTO;
import cz.nkp.urnnbn.shared.dto.ie.PrimaryOriginatorType;
import cz.nkp.urnnbn.client.validation.Validator;

public class PrimaryOriginatorField extends Field {

	ConstantsImpl constants = GWT.create(ConstantsImpl.class);
	private final Validator validator;
	ListBox originatorTypeListBox = new ListBox();
	private TextBox textBox = new TextBox();

	public PrimaryOriginatorField(Validator validator) {
		this(validator, null);
	}

	public PrimaryOriginatorField(Validator validator, PrimaryOriginatorDTO originator) {
		this.validator = validator;
		originatorTypeListBox.addItem(constants.originatorAuthor());
		originatorTypeListBox.addItem(constants.originatorCorporation());
		originatorTypeListBox.addItem(constants.originatorEvent());
		originatorTypeListBox.setStyleName(css.formLabel());
		textBox.setWidth("200px");
		textBox.addChangeHandler(textboxChangeHandler());
		if (originator != null) {
			load(originator);
		}
	}

	private void load(PrimaryOriginatorDTO originator) {
		switch (originator.getType()) {
		case AUTHOR:
			originatorTypeListBox.setSelectedIndex(0);
			break;
		case CORPORATION:
			originatorTypeListBox.setSelectedIndex(1);
			break;
		case EVENT:
			originatorTypeListBox.setSelectedIndex(2);
			break;
		}
		textBox.setText(originator.getValue());
	}

	private ChangeHandler textboxChangeHandler() {
		return new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				validate();
			}
		};
	}

	public boolean validate() {
		boolean valid = validator.isValid(textBox.getValue());
		if (valid) {
			textBox.setStyleName(css.validTextBoxData());
		} else {
			textBox.setTitle(validator.localizedErrorMessage(textBox.getText()));
			textBox.setStyleName(css.invalidTextBoxData());
		}
		return valid;
	}

	@Override
	public void disable() {
		originatorTypeListBox.setEnabled(false);
		textBox.setEnabled(false);
	}

	@Override
	public void enable() {
		originatorTypeListBox.setEnabled(true);
		textBox.setEnabled(true);
	}

	@Override
	public Widget getLabelWidget() {
		return originatorTypeListBox;
	}

	@Override
	public Widget getContentWidget() {
		return textBox;
	}

	@Override
	public boolean validValueInserted() {
		return validate();
	}

	@Override
	public PrimaryOriginatorDTO getInsertedValue() {
		if (textBox.getText().isEmpty()) {
			return null;
		} else {
			PrimaryOriginatorDTO result = new PrimaryOriginatorDTO();
			result.setType(getSelectedType());
			result.setValue(textBox.getText());
			return result;
		}
	}

	private PrimaryOriginatorType getSelectedType() {
		switch (originatorTypeListBox.getSelectedIndex()) {
		case (0):
			return PrimaryOriginatorType.AUTHOR;
		case (1):
			return PrimaryOriginatorType.CORPORATION;
		case (2):
			return PrimaryOriginatorType.EVENT;
		default:
			return null;
		}
	}
}
