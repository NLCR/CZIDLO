package cz.nkp.urnnbn.client.forms;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import cz.nkp.urnnbn.shared.dto.DigitalInstanceDTO;

public class AccessRestrictionField extends Field {

    private final Label label = label();
    private final DigitalInstanceDTO.ACCESS_RESTRICTION original;
    ListBox listBox = new ListBox();

    private Label label() {
        Label result = new Label(constants.accessRestriction() + ": ");
        result.setStyleName(css.formLabel());
        return result;
    }

    public AccessRestrictionField(DigitalInstanceDTO.ACCESS_RESTRICTION original) {
        this.original = original;
        initListBox();
    }

    private void initListBox() {
        for (DigitalInstanceDTO.ACCESS_RESTRICTION res : DigitalInstanceDTO.ACCESS_RESTRICTION.values()) {
            String localizedName = localize(res);
            System.err.println("localized:" + localizedName);
            listBox.addItem(localizedName);
        }
        listBox.setSelectedIndex(original.ordinal());
    }

    private String localize(DigitalInstanceDTO.ACCESS_RESTRICTION accessRestriction) {
        switch (accessRestriction) {
            case UNKNOWN:
                return constants.accessRestrictionUnknown();
            case LIMITED_ACCESS:
                return constants.accessRestrictionLimited();
            case UNLIMITED_ACCESS:
                return constants.accessRestrictionUnlimited();
            default:
                return "";
        }
    }

    @Override
    public Widget getLabelWidget() {
        return label;
    }

    @Override
    public Widget getContentWidget() {
        return listBox;
    }

    @Override
    public boolean validValueInserted() {
        return true;
    }

    @Override
    public DigitalInstanceDTO.ACCESS_RESTRICTION getInsertedValue() {
        return DigitalInstanceDTO.ACCESS_RESTRICTION.values()[listBox.getSelectedIndex()];
    }

    @Override
    public void disable() {
        listBox.setEnabled(false);
    }

    @Override
    public void enable() {
        listBox.setEnabled(true);
    }

}
