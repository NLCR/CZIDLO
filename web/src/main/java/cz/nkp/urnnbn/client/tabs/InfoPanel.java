package cz.nkp.urnnbn.client.tabs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

import cz.nkp.urnnbn.client.services.StaticContentService;
import cz.nkp.urnnbn.client.services.StaticContentServiceAsync;

public class InfoPanel extends SingleTabContentPanel {

	private final StaticContentServiceAsync staticContentService = GWT
			.create(StaticContentService.class);

	public InfoPanel(TabsPanel tabsPanel) {
		super(tabsPanel);
		// reload("");
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		VerticalPanel contentPanel = new VerticalPanel();
		final HTML contentView = new HTML("content");
		contentPanel.add(contentView);
		final TextArea contentEdit = new TextArea();
		contentEdit.setVisible(false);
		contentEdit.setHeight("100%");
		contentEdit.setWidth("100%");
		contentPanel.add(contentEdit);
		final Button button = new Button();
		button.setText("edit");
		button.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent arg0) {
				if (contentView.isVisible()) {
					contentView.setVisible(false);
					contentEdit.setText(contentView.getHTML());
					contentEdit.setVisible(true);
					button.setText("save");
				} else {
					contentView.setVisible(true);
					contentView.setHTML(contentEdit.getText());
					contentEdit.setVisible(false);
					button.setText("edit");
				}
			}

		});
		if (getActiveUser().isSuperAdmin()) {
			contentPanel.add(button);
		}
		contentPanel.add(button);
		add(contentPanel);
		// add(button);
		/*
		 * VerticalPanel contentPanel = new VerticalPanel();
		 * contentPanel.add(new HTML("ahoj")); add(contentPanel);
		 */
		// add(new HTML("ahoj"));
		/*
		 * super.onLoad(); staticContentService.getTabInfoContent(new
		 * AsyncCallback<String>() {
		 * 
		 * @Override public void onSuccess(String result) { reload(result); }
		 * 
		 * @Override public void onFailure(Throwable caught) { // TODO
		 * Auto-generated method stub } });
		 */
	}

	private void reload(String content) {
		clear();
		content = "ahoj";
		add(new HTML(content));
	}

	@Override
	public void onSelection() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDeselectionSelection() {
		// TODO Auto-generated method stub
	}
}
