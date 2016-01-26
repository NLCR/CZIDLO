package cz.nkp.urnnbn.client.search;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

import cz.nkp.urnnbn.client.i18n.MessagesImpl;
import cz.nkp.urnnbn.client.resources.SearchPanelCss;
import cz.nkp.urnnbn.client.services.SearchService;
import cz.nkp.urnnbn.client.services.SearchServiceAsync;
import cz.nkp.urnnbn.shared.dto.DigitalDocumentDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;

/**
 * 
 * @author Martin Řehánek
 */
public class ResultsPage extends ScrollPanel {

	private static final Logger logger = Logger.getLogger(ResultsPage.class.getName());
	private static final int MAX_ENTITIES_TO_EXPAND = 3;
	private static final int MAX_DOCUMENTS_TO_EXPAND = 1;

	private final MessagesImpl messages = GWT.create(MessagesImpl.class);
	private final SearchServiceAsync searchService = GWT.create(SearchService.class);
	private final SearchPanelCss css = SearchPanelResources.css();

	private final SearchPanel searchPanel;
	private final UserDTO user;
	private final ArrayList<Long> intEntIdentifiers;
	private ArrayList<IntelectualEntityDTO> entities;
	private boolean dataLoaded = false;

	public ResultsPage(SearchPanel searchPanel, UserDTO user, ArrayList<Long> intEntIdentifiers) {
		super();
		this.searchPanel = searchPanel;
		this.user = user;
		this.intEntIdentifiers = intEntIdentifiers;
	}

	@Override
	protected void onLoad() {
		super.onLoad();
	}

	public void onSelected() {
		if (!dataLoaded) {
			loadData();
		}
	}

	private void showData() {
		clear();
		VerticalPanel contentPanel = new VerticalPanel();
		boolean expand = entities.size() <= MAX_ENTITIES_TO_EXPAND;
		for (IntelectualEntityDTO entity : entities) {
			Tree entityTree = new Tree();
			entityTree.setAnimationEnabled(true);
			TreeItem entityItem = EntityTreeItemBuilder.getItem(entity, user, searchPanel);
			entityItem.setState(expand);
			entityTree.addItem(entityItem);
			if (entity.getDocuments() != null) {
				appendDocuments(entityItem, entity.getDocuments());
			}
			contentPanel.add(entityTree);
		}
		add(contentPanel);
	}

	private void loadData() {
		showProcessingWheel();
		searchService.getIntelectualEntities(intEntIdentifiers, new AsyncCallback<ArrayList<IntelectualEntityDTO>>() {

			@Override
			public void onSuccess(ArrayList<IntelectualEntityDTO> result) {
				entities = result;
				dataLoaded = true;
				showData();
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(messages.serverError(caught.getMessage()));
				clear();
			}
		});

	}

	private void showProcessingWheel() {
		clear();
		add(processingWheelPanel());
	}

	private Panel processingWheelPanel() {
		VerticalPanel result = new VerticalPanel();
		result.setStyleName(css.paginationProcessWheelPanel());
		Image booksImg = new Image("img/ajax-loader.gif");
		result.add(booksImg);
		return result;
	}

	private void appendDocuments(TreeItem entityItem, ArrayList<DigitalDocumentDTO> documents) {
		boolean expand = documents.size() <= MAX_DOCUMENTS_TO_EXPAND;
		for (DigitalDocumentDTO doc : documents) {
			DigitalDocumentTreeBuilder builder = new DigitalDocumentTreeBuilder(doc, searchPanel);
			TreeItem documentItem = builder.getItem();
			entityItem.addItem(documentItem);
			documentItem.setState(expand);
		}
	}
}
