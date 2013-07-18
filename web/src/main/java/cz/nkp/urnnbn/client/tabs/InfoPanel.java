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
import cz.nkp.urnnbn.shared.dto.ContentDTO;

public class InfoPanel extends SingleTabContentPanel {

	private ContentDTO content = null;
	
	private HTML contentView = null;
	private TextArea contentEdit = null;
	private Button saveButton = null;
	
	private String language = "cz";
	private String name = "info";
	
	private final StaticContentServiceAsync staticContentService = GWT.create(StaticContentService.class);

	public InfoPanel(TabsPanel tabsPanel) {
		super(tabsPanel);
	}

	@Override
	protected void onLoad() {
		System.err.println("loading");
		super.onLoad();
		VerticalPanel contentPanel = new VerticalPanel();
		contentView = new HTML("loading...");
		contentPanel.add(contentView);
		contentEdit = new TextArea();
		contentEdit.setVisible(false);
		contentEdit.setHeight("350px");
		contentEdit.setWidth("700px");
		contentPanel.add(contentEdit);
		saveButton = new Button();
		saveButton.setText("edit");
		saveButton.setEnabled(false);
		saveButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent arg0) {
				if (contentView.isVisible()) {
					contentView.setVisible(false);
					contentEdit.setText(contentView.getHTML());
					contentEdit.setVisible(true);
					saveButton.setText("save");
				} else {
					contentEdit.setVisible(false);
					contentView.setVisible(true);
					contentView.setHTML(contentEdit.getText());
					saveButton.setText("edit");
					saveContent();
				}
			}

		});
		if (getActiveUser().isSuperAdmin()) {
			contentPanel.add(saveButton);
		}
		add(contentPanel);
		loadContent();
		System.err.println("done");
	}
	
	public void loadContent() {
		staticContentService.getContentByNameAndLanguage(language, name, new
				 AsyncCallback<ContentDTO>() {

					public void onFailure(Throwable error) {
						saveButton.setEnabled(false);
					}

					public void onSuccess(ContentDTO result) {
						content = result;
						saveButton.setEnabled(true);
						contentView.setText(content.getContent());		
					}
		});
	}
	
	public void saveContent() {
		content.setContent(contentEdit.getText());
		staticContentService.update(content, new AsyncCallback<Void>() {
			
			public void onFailure(Throwable error) {
				saveButton.setEnabled(true);
				error.printStackTrace();
			}

			public void onSuccess(Void result) {
				saveButton.setEnabled(true);
				contentView.setText(content.getContent());	
			}
		});
	}

	@Override
	public void onSelection() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeselection() {
		// TODO Auto-generated method stub
		
	}
}
