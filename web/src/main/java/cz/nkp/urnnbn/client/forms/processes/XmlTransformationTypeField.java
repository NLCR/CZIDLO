package cz.nkp.urnnbn.client.forms.processes;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import cz.nkp.urnnbn.client.forms.Field;
import cz.nkp.urnnbn.shared.dto.process.XmlTransformationDTOType;

public class XmlTransformationTypeField extends Field {

	private final Label label = label();
	ListBox typesListBox = initTypesListBox();

	private Label label() {
		Label result = new Label("typ sablony: ");
		result.setStyleName(css.formLabel());
		return result;
	}

	private ListBox initTypesListBox() {
		ListBox result = new ListBox();
		result.addItem(XmlTransformationDTOType.DIGITAL_DOCUMENT_REGISTRATION.toString());
		result.addItem(XmlTransformationDTOType.DIGITAL_INSTANCE_IMPORT.toString());
		return result;
	}

	@Override
	public Widget getLabelWidget() {
		return label;
	}

	@Override
	public Widget getContentWidget() {
		return typesListBox;
	}

	@Override
	public boolean validValueInserted() {
		return true;
	}

	@Override
	public XmlTransformationDTOType getInsertedValue() {
		int selected = typesListBox.getSelectedIndex();
		if (selected == 0) {
			return XmlTransformationDTOType.DIGITAL_DOCUMENT_REGISTRATION;
		} else {
			return XmlTransformationDTOType.DIGITAL_INSTANCE_IMPORT;
		}
	}

	@Override
	public void disable() {
		typesListBox.setEnabled(false);
	}

	@Override
	public void enable() {
		typesListBox.setEnabled(true);
	}

}
