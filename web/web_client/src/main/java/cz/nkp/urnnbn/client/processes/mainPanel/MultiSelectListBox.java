package cz.nkp.urnnbn.client.processes.mainPanel;

import com.google.gwt.user.client.ui.ListBox;

import java.util.ArrayList;
import java.util.List;

public class MultiSelectListBox extends ListBox {

    public MultiSelectListBox() {
        super(true);
    }

    public List<String> getSelectedItems() {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < this.getItemCount(); i++) {
            if (this.isItemSelected(i)) {
                result.add(this.getItemText(i));
            }
        }
        return result;
    }

}
