package cz.nkp.urnnbn.client.forms;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import cz.nkp.urnnbn.shared.dto.DigitalLibraryDTO;

public class DigitalLibraryListField extends Field {

    private final Label label = label();
    private final ArrayList<DigitalLibraryDTO> libraries;
    ListBox librariesListBox = new ListBox();

    private Label label() {
        Label result = new Label(constants.digitalLibrary() + ": ");
        result.setStyleName(css.formLabel());
        return result;
    }

    public DigitalLibraryListField(ArrayList<DigitalLibraryDTO> libraries) {
        this.libraries = libraries;
        initListBox();
    }

    private void initListBox() {
        for (DigitalLibraryDTO library : libraries) {
            librariesListBox.addItem(library.getName());
        }
    }

    @Override
    public Widget getLabelWidget() {
        return label;
    }

    @Override
    public Widget getContentWidget() {
        return librariesListBox;
    }

    @Override
    public boolean validValueInserted() {
        return true;
    }

    @Override
    public DigitalLibraryDTO getInsertedValue() {
        return libraries.get(librariesListBox.getSelectedIndex());
    }

    @Override
    public void disable() {
        librariesListBox.setEnabled(false);
    }

    @Override
    public void enable() {
        librariesListBox.setEnabled(true);
    }

}
