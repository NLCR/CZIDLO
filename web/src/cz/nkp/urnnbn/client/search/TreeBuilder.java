package cz.nkp.urnnbn.client.search;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TreeItem;

import cz.nkp.urnnbn.client.resources.SearchPanelCss;

public class TreeBuilder {
	protected final SearchPanelCss css = SearchPanelResources.css();
	final SearchPanel superPanel;

	public TreeBuilder(SearchPanel superPanel) {
		this.superPanel = superPanel;
	}

	void addLabeledRowIfValueNotNull(String label, Object value, TreeItem root, String spanClass) {
		if (value != null) {
			String row = "<span class=\"" + spanClass + "\">" + label + ": </span>" + value;
			root.addItem(row);
		}
	}

	void addLabeledRowAndButtonIfValueNotNull(String label, Object value, TreeItem root, String spanClass, Button button,
			String buttonStyle) {
		if (value != null) {
			HorizontalPanel panel = new HorizontalPanel();
			HTML labelHtml = new HTML("<span><span class=\"" + spanClass + "\">" + label + ": </span>" + value.toString() + "&nbsp;</span>");
			panel.add(labelHtml);
			panel.add(button);
			button.addStyleName(buttonStyle);
			root.addItem(panel);
		}
	}
}
