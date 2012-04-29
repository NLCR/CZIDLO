package cz.nkp.urnnbn.client.forms;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import cz.nkp.urnnbn.shared.dto.ArchiverDTO;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;

public class ArchiverListField extends Field {

	private Label label = new Label(constants.archiver() + ": ");
	private final RegistrarDTO registrar;
	private final ArrayList<ArchiverDTO> archivers;
	ListBox archiversListBox = new ListBox();

	public ArchiverListField(RegistrarDTO registrar, ArrayList<ArchiverDTO> archivers) {
		this(registrar, archivers, null);
	}

	public ArchiverListField(RegistrarDTO registrar, ArrayList<ArchiverDTO> archivers, ArchiverDTO selectedArchiver) {
		this.registrar = registrar;
		this.archivers = archivers;
		label.setStyleName(css.formLabel());
		initListBox(selectedArchiver);
	}
	
	private void initListBox(ArchiverDTO selectedArchiver){
		archiversListBox.addItem(registrar.getName());
		int selected = 0;
		for (int i = 0; i < archivers.size(); i++) {
			ArchiverDTO archiver = archivers.get(i);
			archiversListBox.addItem(archiver.getName());
			if (selectedArchiver != null && selectedArchiver.equals(archiver)) {
				selected = i + 1;
			}
		}
		archiversListBox.setSelectedIndex(selected);
	}

	@Override
	public Widget getLabelWidget() {
		return label;
	}

	@Override
	public Widget getContentWidget() {
		return archiversListBox;
	}

	@Override
	public boolean validValueInserted() {
		return true;
	}

	@Override
	public ArchiverDTO getInsertedValue() {
		int index = archiversListBox.getSelectedIndex();
		if (index == 0) {
			return registrar;
		} else {
			return archivers.get(index - 1);
		}
	}

	@Override
	public void disable() {
		archiversListBox.setEnabled(false);
	}

	@Override
	public void enable() {
		archiversListBox.setEnabled(true);
	}

}
