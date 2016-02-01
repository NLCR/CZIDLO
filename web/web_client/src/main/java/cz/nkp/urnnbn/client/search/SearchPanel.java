package cz.nkp.urnnbn.client.search;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

import cz.nkp.urnnbn.client.DigitalInstanceRefreshable;
import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.i18n.MessagesImpl;
import cz.nkp.urnnbn.client.resources.SearchPanelCss;
import cz.nkp.urnnbn.client.services.ConfigurationService;
import cz.nkp.urnnbn.client.services.ConfigurationServiceAsync;
import cz.nkp.urnnbn.client.services.SearchService;
import cz.nkp.urnnbn.client.services.SearchServiceAsync;
import cz.nkp.urnnbn.client.tabs.SingleTabContentPanel;
import cz.nkp.urnnbn.client.tabs.TabsPanel;
import cz.nkp.urnnbn.shared.ConfigurationData;
import cz.nkp.urnnbn.shared.dto.DigitalDocumentDTO;
import cz.nkp.urnnbn.shared.dto.DigitalInstanceDTO;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;

public class SearchPanel extends SingleTabContentPanel implements DigitalInstanceRefreshable {

    private static final Logger logger = Logger.getLogger(SearchPanel.class.getName());
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

    public SearchPanel(TabsPanel superPanel, String searchString) {
        super(superPanel);
        this.superPanel = superPanel;
        add(contentPanel());
        if (searchString != null) {
            searchBox.setText(searchString);
            search(searchString);
        }
        loadConfigurationFromServer();
    }

    private Panel contentPanel() {
        VerticalPanel contentPanel = new VerticalPanel();
        contentPanel.setWidth("100%");
        contentPanel.setHeight("100%");
        contentPanel.add(searchRequestPanel());
        contentPanel.add(searchResultsPanel);
        return contentPanel;
    }

    private void loadConfigurationFromServer() {
        AsyncCallback<ConfigurationData> callback = new AsyncCallback<ConfigurationData>() {
            public void onSuccess(ConfigurationData data) {
                SearchPanel.this.configuration = data;
            }

            public void onFailure(Throwable caught) {
                logger.severe("Error loading configuration: " + caught.getMessage());
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

    public void search(final String request) {
        showProcessingWheel();
        if (request.toLowerCase().startsWith("urn:nbn:")) {
            searchService.searchByUrnNbn(request, new AsyncCallback<IntelectualEntityDTO>() {

                @Override
                public void onSuccess(IntelectualEntityDTO result) {
                    if (result == null) {
                        showNoResults(request);
                    } else {
                        showSingleResult(result);
                    }
                }

                @Override
                public void onFailure(Throwable caught) {
                    Window.alert(messages.serverError(caught.getMessage()));
                    hideProcessingSearchAnimation();
                }
            });
        } else {

            searchService.getIntEntIdentifiersBySearch(request, new AsyncCallback<ArrayList<Long>>() {

                @Override
                public void onSuccess(ArrayList<Long> idList) {
                    if (idList.isEmpty()) {
                        showNoResults(request);
                    } else if (idList.size() == 1) {
                        showSingleResultFromId(idList.get(0));
                    } else {
                        showResultsWithPagination(request, idList);
                    }
                }

                @Override
                public void onFailure(Throwable caught) {
                    Window.alert(messages.serverError(caught.getMessage()));
                    hideProcessingSearchAnimation();
                }
            });
        }
    }

    private void showSingleResultFromId(Long intEntId) {
        searchResultsPanel.clear();
        VerticalPanel processingWheelPanel = new VerticalPanel();
        processingWheelPanel.setStyleName(css.paginationProcessWheelPanel());
        Image booksImg = new Image("img/ajax-loader.gif");
        processingWheelPanel.add(booksImg);
        searchResultsPanel.add(processingWheelPanel);
        searchService.getIntelectualEntity(intEntId, new AsyncCallback<IntelectualEntityDTO>() {

            @Override
            public void onSuccess(IntelectualEntityDTO result) {
                showSingleResult(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                Window.alert(messages.serverError(caught.getMessage()));
                searchResultsPanel.clear();
            }
        });
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

    private void showResultsWithPagination(String request, ArrayList<Long> intEntIdentifiers) {
        searchResultsPanel.clear();
        searchPaginationPanel = new TabPanel();
        if (!intEntIdentifiers.isEmpty()) {
            int first = 0;
            int afterLast = Math.min(ITEMS_PER_PAGE, intEntIdentifiers.size());
            // int tabCounter = 0;
            searchResultsPages = new LinkedList<ResultsPage>();
            logger.info("first=" + first + ", afterLast=" + afterLast);
            while (true) {
                // logger.info("tab=" + ++tabCounter + ", first=" + first + ", afterLast=" + afterLast);
                ArrayList<Long> subList = new ArrayList<Long>(intEntIdentifiers.subList(first, afterLast));
                String tabCaption = "" + (first + 1) + "-" + afterLast;
                ResultsPage page = new ResultsPage(this, superPanel.getActiveUser(), subList);
                searchResultsPages.add(page);
                searchPaginationPanel.add(page, tabCaption);
                if (afterLast == intEntIdentifiers.size()) {
                    // logger.info("last tab");
                    break;
                } else {
                    first = afterLast;
                    afterLast = Math.min(afterLast + ITEMS_PER_PAGE, intEntIdentifiers.size());
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
        logger.info("no results for \"" + query + "\"");
        searchResultsPanel.clear();
        Label label = new Label(messages.noResultsForSearch(query));
        label.addStyleName(css.noSearchResults());
        searchResultsPanel.add(label);
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
        return getActiveUser().isLoggedUser() && superPanel.getRegistrarsManagedByUser().contains(registrar);
    }

    public UserDTO getActiveUser() {
        return superPanel.getActiveUser();
    }

    public ConfigurationData getConfiguration() {
        return configuration;
    }

    @Override
    public void onSelection() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDeselectionSelection() {
        // TODO Auto-generated method stub

    }

    @Override
    public void refresh(DigitalInstanceDTO di) {
        refresh();
    }
}
