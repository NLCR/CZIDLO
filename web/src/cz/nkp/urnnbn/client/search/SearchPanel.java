package cz.nkp.urnnbn.client.search;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.i18n.MessagesImpl;
import cz.nkp.urnnbn.client.resources.SearchPanelCss;
import cz.nkp.urnnbn.client.services.SearchService;
import cz.nkp.urnnbn.client.services.SearchServiceAsync;
import cz.nkp.urnnbn.client.tabs.TabsPanel;
import cz.nkp.urnnbn.shared.dto.DigitalDocumentDTO;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;

public class SearchPanel extends DockLayoutPanel {

	private static final int MAX_ENTITIES_TO_EXPAND = 3;
	private static final int MAX_DOCUMENTS_TO_EXPAND = 1;
	private final ConstantsImpl constants = GWT.create(ConstantsImpl.class);
	private final MessagesImpl messages = GWT.create(MessagesImpl.class);
	private final SearchPanelCss css = SearchPanelResources.css();
	private final SearchServiceAsync searchService = GWT.create(SearchService.class);
	private final ScrollPanel searchResultsPanel = new ScrollPanel();
	private final TextBox searchBox = searchBox();
	private final TabsPanel superPanel;

	private Tree searchResultTree;

	private TextBox searchBox() {
		final TextBox result = new TextBox();
		result.addStyleName(css.searchBox());
		result.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (KeyCodes.KEY_ENTER == event.getNativeKeyCode()) {
					search(result.getText());
				}
			}
		});
		return result;
	}

	public SearchPanel(TabsPanel superPanel, String searchString) {
		super(Unit.PX);
		this.superPanel = superPanel;
		addNorth(searchRequestPanel(), 45);
		add(searchResultsPanel);
		if (searchString != null) {
			searchBox.setText(searchString);
			search(searchString);
		}
	}

	public void refresh() {
		search(searchBox.getText());
	}

	private HorizontalPanel searchRequestPanel() {
		HorizontalPanel searchPanel = new HorizontalPanel();
		searchPanel.addStyleName(css.searchPanel());
		searchPanel.setSpacing(4);
		searchPanel.add(searchBox);
		searchPanel.add(searchButton());
		Label searchStrut = new Label("");
		searchPanel.add(searchStrut);
		searchPanel.setCellWidth(searchStrut, "100%");
		return searchPanel;
	}

	private Button searchButton() {
		Button result = new Button(constants.tabSearchSearchButtonLabel(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				search(searchBox.getText());
			}
		});
		result.addStyleName(css.searchButton());
		return result;
	}

	public void search(final String request) {
		AsyncCallback<ArrayList<IntelectualEntityDTO>> callback = new AsyncCallback<ArrayList<IntelectualEntityDTO>>() {
			public void onSuccess(ArrayList<IntelectualEntityDTO> result) {
				refreshResults(request, result);
			}

			public void onFailure(Throwable caught) {
				Window.alert(constants.serverError() + ": " + caught.getMessage());
			}

		};
		searchService.getSearchResults(request, callback);
	}

	public void refreshResults(String request, ArrayList<IntelectualEntityDTO> results) {
		searchResultsPanel.clear();
		searchResultTree = new Tree();
		searchResultTree.setAnimationEnabled(true);
		searchResultTree.addItem(resultsItem(request, results));
		searchResultsPanel.add(searchResultTree);
	}

	private TreeItem resultsItem(String searchRequest, ArrayList<IntelectualEntityDTO> searchResults) {
		TreeItem searchResult = new TreeItem(messages.searchResults(searchRequest, searchResults.size()));
		for (IntelectualEntityDTO entity : searchResults) {
			TreeItem entityItem = EntityTreeItemBuilder.getItem(entity, superPanel.getActiveUser(), this);
			if (entity.getDocuments() != null) {
				appendDocuments(entityItem, entity.getDocuments());
			}
			searchResult.addItem(entityItem);
		}
		// expand results
		searchResult.setState(true);
		boolean expand = searchResult.getChildCount() <= MAX_ENTITIES_TO_EXPAND;
		// expand intelectual entities
		if (expand) {
			for (int i = 0; i < searchResult.getChildCount(); i++) {
				searchResult.getChild(i).setState(true);
			}
		}
		return searchResult;
	}

	private void appendDocuments(TreeItem entityItem, ArrayList<DigitalDocumentDTO> documents) {
		boolean expand = documents.size() <= MAX_DOCUMENTS_TO_EXPAND;
		for (DigitalDocumentDTO doc : documents) {
			DigitalDocumentTreeBuilder builder = new DigitalDocumentTreeBuilder(doc, this);
			TreeItem documentItem = builder.getItem();
			entityItem.addItem(documentItem);
			documentItem.setState(expand);
		}
	}

	public boolean userManagesRegistrar(RegistrarDTO registrar) {
		return superPanel.userManagesRegistrar(registrar);
	}

	public UserDTO getActiveUser() {
		return superPanel.getActiveUser();
	}
}
