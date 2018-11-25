package cz.nkp.urnnbn.client.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import cz.nkp.urnnbn.client.DigitalInstanceRefreshable;
import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.i18n.MessagesImpl;
import cz.nkp.urnnbn.client.processes.mainPanel.ScheduleProcessExportUrnNbnListDialogBox;
import cz.nkp.urnnbn.client.resources.SearchPanelCss;
import cz.nkp.urnnbn.client.services.ConfigurationService;
import cz.nkp.urnnbn.client.services.ConfigurationServiceAsync;
import cz.nkp.urnnbn.client.services.SearchService;
import cz.nkp.urnnbn.client.services.SearchServiceAsync;
import cz.nkp.urnnbn.client.tabs.SingleTabContentPanel;
import cz.nkp.urnnbn.client.tabs.TabsPanel;
import cz.nkp.urnnbn.shared.ConfigurationData;
import cz.nkp.urnnbn.shared.SearchResult;
import cz.nkp.urnnbn.shared.dto.DigitalDocumentDTO;
import cz.nkp.urnnbn.shared.dto.DigitalInstanceDTO;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Logger;

public class SearchTab extends SingleTabContentPanel implements DigitalInstanceRefreshable {

    private static final Logger LOGGER = Logger.getLogger(SearchTab.class.getName());
    private static final int MAX_DOCUMENTS_TO_EXPAND = 1;
    private static final int ITEMS_PER_PAGE = 10;
    private final ConstantsImpl constants = GWT.create(ConstantsImpl.class);
    private final MessagesImpl messages = GWT.create(MessagesImpl.class);
    private final SearchPanelCss css = SearchPanelResources.css();
    private final SearchServiceAsync searchService = GWT.create(SearchService.class);
    private final ConfigurationServiceAsync configurationService = GWT.create(ConfigurationService.class);
    private final ScrollPanel searchResultsPanel = new ScrollPanel();
    private final TextBox searchBox = searchBox();
    private final TabsPanel superPanel;
    private ConfigurationData configuration;
    private TabPanel searchPaginationPanel;
    private LinkedList<ResultsPage> searchResultsPages;

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

    public SearchTab(TabsPanel superPanel, String query) {
        super(superPanel, "search");
        this.superPanel = superPanel;
        add(contentPanel());
        if (query != null) {
            searchBox.setText(query);
            search(query);
        }
        loadConfigurationFromServer();
    }

    private Panel contentPanel() {
        VerticalPanel contentPanel = new VerticalPanel();
        contentPanel.setWidth("100%");
        contentPanel.setHeight("100%");
        contentPanel.add(searchRequestPanel());
        contentPanel.add(searchResultsPanel);
        // contentPanel.add(testButton());
        return contentPanel;
    }

    private Widget testButton() {
        return new Button("test", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                // new DiAvailabilityCheckDialogBox(getActiveUser());
                new ScheduleProcessExportUrnNbnListDialogBox(getActiveUser());
            }
        });
    }

    private void loadConfigurationFromServer() {
        AsyncCallback<ConfigurationData> callback = new AsyncCallback<ConfigurationData>() {
            public void onSuccess(ConfigurationData data) {
                SearchTab.this.configuration = data;
            }

            public void onFailure(Throwable caught) {
                LOGGER.severe("Error loading configuration: " + caught.getMessage());
            }

        };
        configurationService.getConfiguration(callback);
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

    public void search(String query) {
        final String queryTrimmed = query.trim();
        if (tabsPanel.isGaEnabled()) {
            sendGaSearchEvent(query);
        }
        showProcessingWheel();
        //LOGGER.log(Level.INFO, "searching: " + query);

        searchService.search(queryTrimmed, 0, 0, new AsyncCallback<SearchResult>() {
            @Override
            public void onFailure(Throwable throwable) {
                //LOGGER.log(Level.INFO, "error: ", throwable);
                Window.alert(messages.serverError(throwable.getMessage()));
                hideProcessingSearchAnimation();
            }

            @Override
            public void onSuccess(SearchResult searchResult) {
                Long numFound = searchResult.getNumFound();
                if (searchResult.getNumFound() == 0) {
                    showNoResults(queryTrimmed);
                } else if (searchResult.getNumFound() == 1) {
                    showSingleResultFromId(queryTrimmed);
                } else {
                    //pagination
                    showResultsWithPagination(queryTrimmed, searchResult);
                }
            }
        });
    }

    private native void sendGaSearchEvent(String query) /*-{
                                                          $wnd.ga('send', 'event', 'search', query);
                                                          }-*/;

    private void showSingleResultFromId(final String queryTrimmed) {
        searchResultsPanel.clear();
        VerticalPanel processingWheelPanel = new VerticalPanel();
        processingWheelPanel.setStyleName(css.paginationProcessWheelPanel());
        Image booksImg = new Image("img/ajax-loader.gif");
        processingWheelPanel.add(booksImg);
        searchResultsPanel.add(processingWheelPanel);
        searchService.search(queryTrimmed, 0, 1, new AsyncCallback<SearchResult>() {
            @Override
            public void onFailure(Throwable throwable) {
                Window.alert(messages.serverError(throwable.getMessage()));
                hideProcessingSearchAnimation();
            }

            @Override
            public void onSuccess(SearchResult searchResult) {
                if (searchResult.getNumFound() == 0) {
                    showNoResults(queryTrimmed);
                } else {
                    showSingleResult(searchResult.getIntelectualEntities().get(0));
                }
            }
        });
    }

    private void showResultsWithPagination(String request, SearchResult searchResult) {
        searchResultsPanel.clear();
        searchPaginationPanel = new TabPanel();

        long first = 0;
        long afterLast = Math.min(ITEMS_PER_PAGE, searchResult.getNumFound());
        searchResultsPages = new LinkedList<ResultsPage>();
        //LOGGER.info("first=" + first + ", afterLast=" + afterLast);

        while (true) {
            //LOGGER.info("tab=" + ++tabCounter + ", first=" + first + ", afterLast=" + afterLast);
            String tabCaption = "" + (first + 1) + "-" + afterLast;
            ResultsPage page = new ResultsPage(this, superPanel.getActiveUser(), request, first, ITEMS_PER_PAGE);
            searchResultsPages.add(page);
            searchPaginationPanel.add(page, tabCaption);
            if (afterLast == searchResult.getNumFound()) {
                //LOGGER.info("last tab");
                break;
            } else {
                first = afterLast;
                afterLast = Math.min(afterLast + ITEMS_PER_PAGE, searchResult.getNumFound());
            }
        }

        searchPaginationPanel.addStyleName(css.resultsPagesPanel());
        searchResultsPanel.add(searchPaginationPanel);
        // load data on tab selection
        searchPaginationPanel.addSelectionHandler(new SelectionHandler<Integer>() {

            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                searchResultsPages.get(event.getSelectedItem()).onSelected();
            }
        });
        searchPaginationPanel.selectTab(0);
    }

    private void hideProcessingSearchAnimation() {
        searchResultsPanel.clear();
    }

    private void showProcessingWheel() {
        searchResultsPanel.clear();
        searchResultsPanel.add(processingWheelPanel());
    }

    private Panel processingWheelPanel() {
        VerticalPanel result = new VerticalPanel();
        result.setStyleName(css.processWheelPanel());
        Image booksImg = new Image("img/ajax-loader.gif");
        result.add(booksImg);
        return result;
    }

    private void showSingleResult(IntelectualEntityDTO entity) {
        searchResultsPanel.clear();
        Tree entityTree = new Tree();
        entityTree.setAnimationEnabled(true);
        TreeItem entityItem = EntityTreeItemBuilder.getItem(entity, superPanel.getActiveUser(), this);
        entityItem.setState(true);
        entityTree.addItem(entityItem);
        if (entity.getDocuments() != null) {
            appendDocuments(entityItem, entity.getDocuments());
        }
        searchResultsPanel.add(entityTree);
    }

    private void showNoResults(String query) {
        LOGGER.info("no results for \"" + query + "\"");
        searchResultsPanel.clear();
        Label label = new Label(messages.noResultsForSearch(query));
        label.addStyleName(css.noSearchResults());
        searchResultsPanel.add(label);
    }

    private void appendDocuments(TreeItem entityItem, ArrayList<DigitalDocumentDTO> documents) {
        boolean expand = documents.size() <= MAX_DOCUMENTS_TO_EXPAND;
        for (DigitalDocumentDTO doc : documents) {
            if (doc.getUrn() != null) {
                DigitalDocumentTreeBuilder builder = new DigitalDocumentTreeBuilder(doc, this);
                TreeItem documentItem = builder.getItem();
                entityItem.addItem(documentItem);
                documentItem.setState(expand);
            } else {
                LOGGER.severe("no urn:nbn for digital document with id " + doc.getId() + ", ignoring");
            }
        }
    }

    public boolean userManagesRegistrar(RegistrarDTO registrar) {
        return getActiveUser().isLoggedUser() && superPanel.getRegistrarsManagedByUser().contains(registrar);
    }

    public UserDTO getActiveUser() {
        return superPanel.getActiveUser();
    }

    public ConfigurationData getConfiguration() {
        return configuration;
    }

    @Override
    public void onSelected() {
        // LOGGER.fine("onSelected");
        super.onSelected();
    }

    @Override
    public void onDeselected() {
        // LOGGER.fine("onDeselected");
        super.onDeselected();
    }

    @Override
    public void refresh(DigitalInstanceDTO di) {
        refresh();
    }
}
