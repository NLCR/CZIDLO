package cz.nkp.urnnbn.client.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import cz.nkp.urnnbn.client.i18n.MessagesImpl;
import cz.nkp.urnnbn.client.resources.SearchPanelCss;
import cz.nkp.urnnbn.client.services.SearchService;
import cz.nkp.urnnbn.client.services.SearchServiceAsync;
import cz.nkp.urnnbn.shared.SearchResult;
import cz.nkp.urnnbn.shared.dto.DigitalDocumentDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Martin Řehánek
 */
public class ResultsPage extends ScrollPanel {

    private static final Logger LOGGER = Logger.getLogger(ResultsPage.class.getName());
    private static final int MAX_ENTITIES_TO_EXPAND = 3;
    private static final int MAX_DOCUMENTS_TO_EXPAND = 1;

    private final MessagesImpl messages = GWT.create(MessagesImpl.class);
    private final SearchServiceAsync searchService = GWT.create(SearchService.class);
    private final SearchPanelCss css = SearchPanelResources.css();

    private final SearchTab searchPanel;
    private final UserDTO user;
    private final String query;
    private final long start;
    private final int rows;

    private List<IntelectualEntityDTO> entities;
    private boolean dataLoaded = false;


    public ResultsPage(SearchTab searchPanel, UserDTO user, String query, long start, int rows) {
        super();
        this.searchPanel = searchPanel;
        this.user = user;
        this.query = query;
        this.start = start;
        this.rows = rows;
    }

    @Override
    protected void onLoad() {
        super.onLoad();
        //LOGGER.info("loaded: " + query + ": " + start);
    }

    public void onSelected() {
        //LOGGER.info("selected: " + query + ": " + start);
        if (!dataLoaded) {
            loadData();
        }
    }

    private void loadData() {
        showProcessingWheel();
        searchService.search(query, start, rows, new AsyncCallback<SearchResult>() {
            @Override
            public void onFailure(Throwable throwable) {
                Window.alert(messages.serverError(throwable.getMessage()));
                clear();
            }

            @Override
            public void onSuccess(SearchResult searchResult) {
                entities = searchResult.getIntelectualEntities();
                dataLoaded = true;
                showData();
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

    private void appendDocuments(TreeItem entityItem, ArrayList<DigitalDocumentDTO> documents) {
        boolean expand = documents.size() <= MAX_DOCUMENTS_TO_EXPAND;
        for (DigitalDocumentDTO doc : documents) {
            if (doc.getUrn() != null) {
                DigitalDocumentTreeBuilder builder = new DigitalDocumentTreeBuilder(doc, searchPanel);
                TreeItem documentItem = builder.getItem();
                entityItem.addItem(documentItem);
                documentItem.setState(expand);
            } else {
                LOGGER.severe("no urn:nbn for digital document with id " + doc.getId() + ", ignoring");
            }
        }
    }
}
