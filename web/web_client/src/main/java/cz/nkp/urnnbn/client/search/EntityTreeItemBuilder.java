package cz.nkp.urnnbn.client.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TreeItem;

import cz.nkp.urnnbn.client.forms.intEntities.SourceDocumentForm;
import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.resources.SearchPanelCss;
import cz.nkp.urnnbn.shared.ConfigurationData;
import cz.nkp.urnnbn.shared.dto.PublicationDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO;
import cz.nkp.urnnbn.shared.dto.ie.AnalyticalDTO;
import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;
import cz.nkp.urnnbn.shared.dto.ie.MonographDTO;
import cz.nkp.urnnbn.shared.dto.ie.MonographVolumeDTO;
import cz.nkp.urnnbn.shared.dto.ie.OtherEntityDTO;
import cz.nkp.urnnbn.shared.dto.ie.PeriodicalDTO;
import cz.nkp.urnnbn.shared.dto.ie.PeriodicalIssueDTO;
import cz.nkp.urnnbn.shared.dto.ie.PeriodicalVolumeDTO;
import cz.nkp.urnnbn.shared.dto.ie.PrimaryOriginatorDTO;
import cz.nkp.urnnbn.shared.dto.ie.SourceDocumentDTO;
import cz.nkp.urnnbn.shared.dto.ie.ThesisDTO;

public abstract class EntityTreeItemBuilder extends TreeBuilder {

    final ConstantsImpl constants = GWT.create(ConstantsImpl.class);
    final SearchPanelCss css = SearchPanelResources.css();
    final UserDTO user;
    private final PrimaryOriginatorDTO primaryOriginator;
    private final PublicationDTO publication;
    private final SourceDocumentDTO srcDoc;
    protected TreeItem result;

    public EntityTreeItemBuilder(UserDTO user, SearchTab superPanel, PrimaryOriginatorDTO primaryOriginator, PublicationDTO publication,
            SourceDocumentDTO srcDoc) {
        super(superPanel);
        this.user = user;
        this.primaryOriginator = primaryOriginator;
        this.publication = publication;
        this.srcDoc = srcDoc;
    }

    public static TreeItem getItem(IntelectualEntityDTO dto, UserDTO user, SearchTab superPanel) {
        if (dto instanceof MonographDTO) {
            return new MonographBuilder((MonographDTO) dto, user, superPanel).getItem();
        } else if (dto instanceof MonographVolumeDTO) {
            return new MonographVolumeBuilder((MonographVolumeDTO) dto, user, superPanel).getItem();
        } else if (dto instanceof PeriodicalDTO) {
            return new PeriodicalBuilder((PeriodicalDTO) dto, user, superPanel).getItem();
        } else if (dto instanceof PeriodicalVolumeDTO) {
            return new PeriodicalVolumeBuilder((PeriodicalVolumeDTO) dto, user, superPanel).getItem();
        } else if (dto instanceof PeriodicalIssueDTO) {
            return new PeriodicalIssueBuilder((PeriodicalIssueDTO) dto, user, superPanel).getItem();
        } else if (dto instanceof AnalyticalDTO) {
            return new AnalyticalBuilder((AnalyticalDTO) dto, user, superPanel).getItem();
        } else if (dto instanceof ThesisDTO) {
            return new ThesisBuilder((ThesisDTO) dto, user, superPanel).getItem();
        } else if (dto instanceof OtherEntityDTO) {
            return new OtherEntityBuilder((OtherEntityDTO) dto, user, superPanel).getItem();
        } else {
            // can never happen unless new subtype of IntelectualEntityDTO is
            // supplemented
            return null;
        }
    }

    public TreeItem getItem() {
        HorizontalPanel panel = new HorizontalPanel();
        HTML rootItem = new HTML(getAggregateTitle() + "<span style=\"color:grey\"> (" + entityType() + ")</span>");
        rootItem.getElement().addClassName(css.searchTreeItem());
        panel.add(rootItem);
        if (user.isSuperAdmin()) {
            panel.add(new HTML("&nbsp&nbsp"));
            panel.add(editEntityButton());
        }
        result = new TreeItem(panel);
        addRows();
        return result;
    }

    private Button editEntityButton() {
        Button button = new Button(constants.edit(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IntelectualEntityDTO entity = getDto();
                SourceDocumentForm srcDocForm = (entity instanceof AnalyticalDTO) ? new SourceDocumentForm(srcDoc) : null;
                new EditIntelectualEntityDialogBox(superPanel, entity, primaryOriginator, srcDocForm).show();
            }
        });
        button.addStyleName(css.treeButton());
        return button;
    }

    abstract IntelectualEntityDTO getDto();

    abstract void addRows();

    abstract String entityType();

    abstract String getAggregateTitle();

    void addLabeledRowIfNotNull(String label, Object value) {
        addLabeledRowIfNotNull(result, label, value);
    }

    void addLabeledRowIfNotNull(TreeItem root, String label, Object value) {
        addLabeledRowIfValueNotNull(label, value, root, css.attrLabel());
    }

    void appendSourceDocumentIfNotNull() {
        String title = buildTitle(srcDoc.getTitle(), srcDoc.getVolumeTitle(), srcDoc.getIssueTitle());
        TreeItem srcDocItem = new TreeItem(new HTML("<span class=\"" + css.attrLabel() + "\">" + constants.sourceDoc() + ": </span>" + title));
        if (srcDoc != null) {
            addLabeledRowIfNotNull(srcDocItem, constants.title(), srcDoc.getTitle());
            addLabeledRowIfNotNull(srcDocItem, constants.volumeTitle(), srcDoc.getVolumeTitle());
            addLabeledRowIfNotNull(srcDocItem, constants.issueTitle(), srcDoc.getIssueTitle());
            addLabeledRowIfNotNull(srcDocItem, constants.ccnb(), srcDoc.getCcnb());
            addLabeledRowIfNotNull(srcDocItem, constants.isbn(), srcDoc.getIsbn());
            addLabeledRowIfNotNull(srcDocItem, constants.issn(), srcDoc.getIssn());
            addLabeledRowIfNotNull(srcDocItem, constants.otherId(), srcDoc.getOtherId());
            PublicationDTO publication = srcDoc.getPublication();
            if (publication != null) {
                addLabeledRowIfNotNull(srcDocItem, constants.publicationPlace(), publication.getPublicationPlace());
                addLabeledRowIfNotNull(srcDocItem, constants.publicationYear(), publication.getPublicationYear());
                addLabeledRowIfNotNull(srcDocItem, constants.publisher(), publication.getPublisher());
            }
        }
        result.addItem(srcDocItem);
    }

    void appendPrimaryOriginatorIfNotNull() {
        if (primaryOriginator != null) {
            switch (primaryOriginator.getType()) {
            case AUTHOR:
                addLabeledRowIfNotNull(constants.originatorAuthor(), primaryOriginator.getValue());
                break;
            case CORPORATION:
                addLabeledRowIfNotNull(constants.originatorCorporation(), primaryOriginator.getValue());
                break;
            case EVENT:
                addLabeledRowIfNotNull(constants.originatorEvent(), primaryOriginator.getValue());
                break;
            }
        }
    }

    void addDigitalBorn(Boolean digitalBorn) {
        if (digitalBorn != null) {
            addLabeledRowIfNotNull(constants.digitalBorn(), digitalBorn ? constants.yes() : constants.no());
        }
    }

    void appendPublicationIfNotNull() {
        if (publication != null) {
            addLabeledRowIfNotNull(constants.publisher(), publication.getPublisher());
            addLabeledRowIfNotNull(constants.publicationPlace(), publication.getPublicationPlace());
            addLabeledRowIfNotNull(constants.publicationYear(), publication.getPublicationYear());
        }
    }

    void addTimestamps(IntelectualEntityDTO dto) {
        addLabeledRowIfNotNull(constants.created(), dto.getCreated());
        if (dto.getModified() != null && !dto.getModified().equals(dto.getCreated())) {
            addLabeledRowIfNotNull(constants.modified(), dto.getModified());
        }
    }

    final String buildTitle(String first, String second) {
        String separator = ", ";
        StringBuilder result = new StringBuilder();
        result.append(first);
        if (second != null) {
            result.append(separator).append(second);
        }
        return result.toString();
    }

    final String buildTitle(String first, String second, String third) {
        String separator = ", ";
        StringBuilder result = new StringBuilder();
        result.append(first);
        if (second != null) {
            result.append(separator).append(second);
        }
        if (third != null) {
            result.append(separator).append(third);
        }
        return result.toString();
    }

    void appendAlephLinkIfEnabledAndCcnbPresent(String ccnb) {
        ConfigurationData configuration = superPanel.getConfiguration();
        if (configuration.showAlephLinks() && (ccnb != null) && (ccnb.length() == 12)) {
            String docNumber = ccnb.substring(3);
            String url = configuration.getAlephUrl() + "/F/?func=direct&doc_number=" + docNumber + "&local_base=" + configuration.getAlephBase();
            SafeHtmlBuilder builder = new SafeHtmlBuilder();
            builder.appendHtmlConstant("<a href ='").appendEscaped(url).appendHtmlConstant("' target='blank'>");
            builder.appendHtmlConstant(constants.showInCatalog());
            builder.appendHtmlConstant("</a>");
            // TreeItem alephItem = new TreeItem("<a href =\"" + url + "\" target=\"_blank\">" + constants.showInCatalog() + "</a>");
            // result.addItem(alephItem);
            result.addItem(builder.toSafeHtml());
        }
    }
}
