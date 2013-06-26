package cz.nkp.urnnbn.client.tabs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;

import cz.nkp.urnnbn.client.services.StaticContentService;
import cz.nkp.urnnbn.client.services.StaticContentServiceAsync;

public class RulesPanel extends SingleTabContentPanel {

	private final StaticContentServiceAsync staticContentService = GWT.create(StaticContentService.class);

	public RulesPanel(TabsPanel tabsPanel) {
		super(tabsPanel);
		reload("");
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		staticContentService.getTabRulesContent(new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {
				reload(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}
		});
	}

	private void reload(String content) {
		clear();
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
