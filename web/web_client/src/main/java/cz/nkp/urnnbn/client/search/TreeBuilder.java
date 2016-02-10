package cz.nkp.urnnbn.client.search;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TreeItem;

import cz.nkp.urnnbn.client.resources.SearchPanelCss;

public class TreeBuilder {
    protected final SearchPanelCss css = SearchPanelResources.css();
    final SearchTab superPanel;

    public TreeBuilder(SearchTab superPanel) {
        this.superPanel = superPanel;
    }

    void addLabeledRowIfValueNotNull(String label, Object value, TreeItem root, String spanClass) {
        if (value != null) {
            HTML row = new HTML("<span class=\"" + spanClass + "\">" + label + ": </span>" + value);
            row.getElement().addClassName(css.searchTreeItem());
            root.addItem(row);
        }
    }

    void addLabeledRowAndButtonIfValueNotNull(String label, Object value, TreeItem root, String spanClass, Button button, String buttonStyle) {
        if (value != null) {
            HorizontalPanel panel = new HorizontalPanel();
            HTML labelHtml = new HTML("<span><span class=\"" + spanClass + "\">" + label + ": </span>" + value.toString() + "&nbsp;</span>");
            labelHtml.getElement().addClassName(css.searchTreeItem());
            panel.add(labelHtml);
            panel.add(button);
            button.addStyleName(buttonStyle);
            root.addItem(panel);
        }
    }

    Button openUrlButton(String label, final String url) {
        Button result = new Button(label, new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Window.open(url, "_blank", "");
            }
        });
        result.addStyleName(css.treeButton());
        return result;
    }
}
