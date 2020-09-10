package cz.nkp.urnnbn.client.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import cz.nkp.urnnbn.client.Operation;
import cz.nkp.urnnbn.client.editRecord.EditDigitalInstanceDialogBox;
import cz.nkp.urnnbn.client.editRecord.EditRegistrarScopeIdDialogBox;
import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.i18n.MessagesImpl;
import cz.nkp.urnnbn.client.insertRecord.InsertDigitalInstanceDialogBox;
import cz.nkp.urnnbn.client.insertRecord.InsertRegistrarScopeIdDialogBox;
import cz.nkp.urnnbn.client.institutions.ArchiverDetailsDialogBox;
import cz.nkp.urnnbn.client.institutions.DigitalLibraryDetailsDialogBox;
import cz.nkp.urnnbn.client.services.DataService;
import cz.nkp.urnnbn.client.services.DataServiceAsync;
import cz.nkp.urnnbn.client.services.InstitutionsService;
import cz.nkp.urnnbn.client.services.InstitutionsServiceAsync;
import cz.nkp.urnnbn.shared.dto.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DigitalDocumentTreeBuilder extends TreeBuilder {

    private static final Logger LOGGER = Logger.getLogger(DigitalDocumentTreeBuilder.class.getName());
    private static final boolean EXPAND_TECHNICAL = false;
    private static final boolean EXPAND_DIGITAL_INSTANCES = false;
    private static final boolean EXPAND_REGISTRAR_SCOPE_IDENTIFIERS = false;
    private static final String API_VERSION = "v6";

    private final DataServiceAsync dataService = GWT.create(DataService.class);
    private final InstitutionsServiceAsync institutionsService = GWT.create(InstitutionsService.class);
    private final MessagesImpl messages = GWT.create(MessagesImpl.class);
    private ConstantsImpl constants = GWT.create(ConstantsImpl.class);
    private final DigitalDocumentDTO dto;

    public DigitalDocumentTreeBuilder(DigitalDocumentDTO dto, SearchTab superPanel) {
        super(superPanel);
        this.dto = dto;
    }

    public TreeItem getItem() {
        TreeItem rootItem = new TreeItem(digitalDocumentItem(dto.getUrn().isActive(), dto.getUrn()));
        addUrnNbnOfDigitalDocument(rootItem);
        addRegistrar(rootItem);
        addArchiver(rootItem);
        addLabeledItemIfValueNotNull(rootItem, constants.financed(), dto.getFinanced());
        addLabeledItemIfValueNotNull(rootItem, constants.contractNumber(), dto.getContractNumber());
        addLabeledItemIfValueNotNull(rootItem, constants.created(), dto.getCreated());
        if (dto.getModified() != null && !dto.getModified().equals(dto.getCreated())) {
            addLabeledItemIfValueNotNull(rootItem, constants.modified(), dto.getModified());
        }
        addTechnicalMetadata(rootItem);
        addRegistrarScopeIdentifiers(rootItem);
        addDigitalInstances(dto.getUrn(), rootItem);
        if (!dto.getUrn().isActive()) {
            rootItem.setState(false);
        }
        return rootItem;
    }

    private void addUrnNbnOfDigitalDocument(TreeItem root) {
        UrnNbnDTO urnNbn = dto.getUrn();
        addUrnNbn(root, urnNbn, false);
    }

    private void addUrnNbn(TreeItem root, UrnNbnDTO urnNbn, boolean linkToWebRecord) {
        Panel urnNbnPanel = urnNbnItemHtlm(urnNbn, linkToWebRecord);

        TreeItem urnNbnItem = root.addItem(urnNbnPanel);
        addLabeledItemIfValueNotNull(urnNbnItem, constants.note(), urnNbn.getDeactivationNote());
        addLabeledItemIfValueNotNull(urnNbnItem, constants.note(), urnNbn.getNote());
        addLabeledItemIfValueNotNull(urnNbnItem, constants.timestampReserved(), urnNbn.getReserved());
        addLabeledItemIfValueNotNull(urnNbnItem, constants.timestampRegistered(), urnNbn.getRegistered());
        addLabeledItemIfValueNotNull(urnNbnItem, constants.timestampDeactivated(), urnNbn.getDeactivated());

        // predecessors
        List<UrnNbnDTO> predecessors = urnNbn.getPredecessors();
        if (predecessors != null && !predecessors.isEmpty()) {
            TreeItem predecessorsItem = urnNbnItem.addItem(new HTML(constants.predecessors()));
            for (UrnNbnDTO predecessor : predecessors) {
                addUrnNbn(predecessorsItem, predecessor, true);
            }
            predecessorsItem.setState(true);
        }

        // successors
        List<UrnNbnDTO> successors = urnNbn.getSuccessors();
        if (successors != null && !successors.isEmpty()) {
            TreeItem successorsItem = urnNbnItem.addItem(new HTML(constants.successors()));
            for (UrnNbnDTO successor : successors) {
                addUrnNbn(successorsItem, successor, true);
            }
            successorsItem.setState(true);
        }
    }

    private Panel urnNbnItemHtlm(UrnNbnDTO urnNbn, boolean withLinkToWebRecord) {
        HorizontalPanel panel = new HorizontalPanel();

        if (urnNbn.isActive()) {
            panel.add(new HTML(urnNbn.toString()));
        } else {
            panel.add(new HTML("<span style=\"color:grey;text-decoration:line-through;\">" + urnNbn.toString() + "</span>&nbsp&nbsp"));
        }
        // button to deactivate urn:nbn and add new digital instance
        if (urnNbn.isActive() && (activeUser().isSuperAdmin() || activeUserManagesRegistrar())) {
            panel.add(new HTML("&nbsp&nbsp"));
            panel.add(deactivateUrnNbnButton(urnNbn));
            panel.add(new HTML("&nbsp&nbsp"));
            panel.add(addDigitalInstanceButton(urnNbn));
        }

        if (withLinkToWebRecord) { // for predecessors and successors - redirect to web search of them
            panel.add(new HTML("&nbsp&nbsp"));
            String url = buildUrlToDigDocRecord(urnNbn, null);
            panel.add(openUrlButton(constants.showRecord(), url));
        }
        // links sto urn:nbn records
        panel.add(new HTML("&nbsp&nbsp"));
        panel.add(buildImageLink(buildUrlToUrnNbnRecord(urnNbn, "xml"), "img/xml.png"));
        panel.add(new HTML("&nbsp"));
        panel.add(buildImageLink(buildUrlToUrnNbnRecord(urnNbn, "json"), "img/json.png"));

        return panel;
    }

    String buildUrlToDigDocRecord(UrnNbnDTO urn, String format) {
        StringBuilder result = new StringBuilder();
        result.append("/api/").append(API_VERSION).append("/resolver/").append(urn.toString());
        if (format != null) {
            result.append("?format=").append(format);
        }
        return result.toString();
    }

    String buildUrlToUrnNbnRecord(UrnNbnDTO urn, String format) {
        StringBuilder result = new StringBuilder();
        result.append("/api/").append(API_VERSION).append("/urnnbn/").append(urn.toString());
        if (format != null) {
            result.append("?format=").append(format);
        }
        return result.toString();
    }

    String buildUrlToDigInstRecord(long id, String format) {
        StringBuilder result = new StringBuilder();
        result.append("/api/").append(API_VERSION).append("/digitalInstances/id/").append(id);
        if (format != null) {
            result.append("?format=").append(format);
        }
        return result.toString();
    }

    private Panel digitalDocumentItem(boolean active, UrnNbnDTO urnNbn) {
        HorizontalPanel result = new HorizontalPanel();
        // label
        if (active) {
            result.add(new Label(constants.digitalDocument()));
        } else {
            result.add(new HTML(constants.digitalDocument() + " <span style=\"color:grey\">(" + constants.inactiveDD() + ")</span>"));
        }
        // button to edit record
        if (activeUser().isSuperAdmin() || activeUserManagesRegistrar()) {
            result.add(new HTML("&nbsp&nbsp"));
            result.add(editDocumentButton());
        }
        // links to records in xml, json
        result.add(new HTML("&nbsp&nbsp"));
        result.add(buildImageLink(buildUrlToDigDocRecord(urnNbn, "xml"), "img/xml.png"));
        result.add(new HTML("&nbsp"));
        result.add(buildImageLink(buildUrlToDigDocRecord(urnNbn, "json"), "img/json.png"));
        return result;
    }

    private Image buildImageLink(final String url, String imageUrl) {
        Image linkXml = new Image(imageUrl);
        linkXml.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Window.open(url, "_blank", "");
            }
        });
        return linkXml;
    }

    private boolean activeUserManagesRegistrar() {
        return superPanel.userManagesRegistrar(dto.getRegistrar());
    }

    private Button editDocumentButton() {
        Button button = new Button(constants.edit(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                EditDigitalDocumentDialogBox dialog = new EditDigitalDocumentDialogBox(dto, superPanel);
                dialog.center();
                dialog.setPopupPosition(dialog.getPopupLeft(), 105);
                dialog.show();
            }
        });
        button.addStyleName(css.treeButton());
        return button;
    }

    private Button deactivateUrnNbnButton(final UrnNbnDTO urnNbn) {
        Button button = new Button(constants.deactivate(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                DeactivateUrnNbnDialogBox dialog = new DeactivateUrnNbnDialogBox(superPanel, urnNbn);
                dialog.center();
                dialog.setPopupPosition(dialog.getPopupLeft(), 105);
                dialog.show();
            }
        });
        button.addStyleName(css.treeButton());
        return button;
    }

    private Button addDigitalInstanceButton(final UrnNbnDTO urn) {
        Button button = new Button(constants.insertDigitalInstance(), new ClickHandler() {

            public void onClick(ClickEvent event) {
                institutionsService.getLibraries(dto.getRegistrar().getId(), new AsyncCallback<ArrayList<DigitalLibraryDTO>>() {

                    public void onFailure(Throwable caught) {
                        Window.alert(messages.serverError(caught.getMessage()));
                    }

                    public void onSuccess(ArrayList<DigitalLibraryDTO> libraries) {
                        new InsertDigitalInstanceDialogBox(urn, libraries, new Operation<DigitalInstanceDTO>() {
                            @Override
                            public void run(DigitalInstanceDTO di) {
                                superPanel.refresh();
                            }
                        }).show();
                    }
                });
            }
        });
        button.addStyleName(css.treeButton());
        return button;
    }

    private void refreshSuperPanel() {
        superPanel.refresh();
    }

    private void addRegistrar(TreeItem rootItem) {
        if (dto.getRegistrar() != null) {
            RegistrarDTO registrar = dto.getRegistrar();
            // TODO: mozna se odkazovat do aplikace na zaznam registratora
            String url = "TODO";
            // addLabeledItemIfValueNotNull(rootItem, constants.registrar(),
            // registrar.getName() + " <a href=\"" + url +
            // "\">podrobnosti</a>");
            addLabeledItemIfValueNotNull(rootItem, constants.registrar(), registrar.getName());
        } else {
            System.err.println("no registrar");
        }
    }

    private void addArchiver(TreeItem rootItem) {
        if (dto.getArchiver() != null && (dto.getArchiver().getId() != dto.getRegistrar().getId())) {
            Button button = new Button(constants.details());
            button.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    ArchiverDetailsDialogBox dialogBox = new ArchiverDetailsDialogBox(dto.getArchiver());
                    dialogBox.show();
                    dialogBox.center();
                }
            });

            addLabeledItemAndButtonIfValueNotNull(rootItem, constants.anotherArchiver(), dto.getArchiver().getName(), button);
        }
    }

    private void addLabeledItemAndButtonIfValueNotNull(TreeItem rootItem, String label, String text, Button button) {
        String spanClass = css.attrLabel();
        addLabeledRowAndButtonIfValueNotNull(label, text, rootItem, spanClass, button, css.treeButton());
    }

    private void addTechnicalMetadata(TreeItem rootItem) {
        TechnicalMetadataDTO metadata = dto.getTechnicalMetadata();
        if (metadata != null) {
            TreeItem metadataItem = addItemIfNotNull(rootItem, new HTML(constants.technicalMetadata()));
            addLabeledItemIfValueNotNull(metadataItem, constants.format(), metadata.getFormat());
            addLabeledItemIfValueNotNull(metadataItem, constants.formatVersion(), metadata.getFormatVersion());
            addLabeledItemIfValueNotNull(metadataItem, constants.extent(), metadata.getExtent());
            if (metadata.getResolutionHorizontal() != null && metadata.getResolutionVertical() != null) {
                String resolution = buildResolutionString(metadata);
                addLabeledItemIfValueNotNull(metadataItem, constants.resolution(), resolution);
            }
            addLabeledItemIfValueNotNull(metadataItem, constants.compressionAlgorithm(), metadata.getCompression());
            if (metadata.getCompressionRatio() != null) {
                addLabeledItemIfValueNotNull(metadataItem, constants.compressionRatio(), metadata.getCompressionRatio());
            }
            addLabeledItemIfValueNotNull(metadataItem, constants.colorModel(), metadata.getColorModel());
            if (metadata.getColorDepth() != null) {
                String colorDepth = metadata.getColorDepth().toString() + ' ' + constants.bits();
                addLabeledItemIfValueNotNull(metadataItem, constants.colorDepth(), colorDepth);
            }
            addLabeledItemIfValueNotNull(metadataItem, constants.iccProfile(), metadata.getIccProfile());
            if (metadata.getPictureWidth() != null && metadata.getPicturHeight() != null) {
                String pictureSize = buildSizeString(metadata);
                addLabeledItemIfValueNotNull(metadataItem, constants.pictureSize(), pictureSize);
            }
            metadataItem.setState(EXPAND_TECHNICAL);
            if (metadataItem.getChildCount() == 0) {
                metadataItem.remove();
            }
        }
    }

    private String buildResolutionString(TechnicalMetadataDTO metadata) {
        if (metadata.getResolutionHorizontal() == null && metadata.getResolutionVertical() == null) {
            return null;
        }
        String horizontal = metadata.getResolutionHorizontal() == null ? "unknown" : metadata.getResolutionHorizontal().toString();
        String vertical = metadata.getResolutionVertical() == null ? "unknown" : metadata.getResolutionVertical().toString();
        return horizontal + 'x' + vertical + ' ' + constants.dpi();
    }

    private String buildSizeString(TechnicalMetadataDTO metadata) {
        if (metadata.getPictureWidth() == null && metadata.getPicturHeight() == null) {
            return null;
        }
        String width = metadata.getPictureWidth() == null ? "unknown" : metadata.getPictureWidth().toString();
        String height = metadata.getPicturHeight() == null ? "unknown" : metadata.getPicturHeight().toString();
        return width + 'x' + height + ' ' + constants.pixels();
    }

    void addLabeledItemIfValueNotNull(TreeItem item, String label, Object value) {
        addLabeledRowIfValueNotNull(label, value, item, css.attrLabel());
    }

    private void addRegistrarScopeIdentifiers(TreeItem rootItem) {
        ArrayList<RegistrarScopeIdDTO> idList = dto.getRegistrarScopeIdList();
        boolean canEdit = activeUser().isSuperAdmin() || activeUserManagesRegistrar();
        boolean showIdsPanel = (idList != null && !idList.isEmpty()) || canEdit;
        if (showIdsPanel) {
            HorizontalPanel idsPanel = new HorizontalPanel();
            idsPanel.add(new HTML("<i>registrar-scope</i>&nbsp;" + constants.identifiers()));
            if (canEdit) {
                idsPanel.add(new HTML("&nbsp&nbsp"));
                idsPanel.add(addRsIdButton());
            }
            //identifiers
            if (idList != null && !idList.isEmpty()) {
                TreeItem idsItem = new TreeItem(idsPanel);
                idsItem.setState(EXPAND_REGISTRAR_SCOPE_IDENTIFIERS);
                for (RegistrarScopeIdDTO idDTO : idList) {
                    idsItem.addItem(buildRsIdItem(idDTO, canEdit));
                }
                rootItem.addItem(idsItem);
            } else {
                rootItem.addItem(idsPanel);
            }
        }
    }

    private Widget buildRsIdItem(RegistrarScopeIdDTO rsId, boolean canEdit) {
        Widget core = new HTML("<span class=\"" + css.attrLabel() + "\">" + rsId.getType() + ":</span>" + rsId.getValue());
        if (canEdit) {
            HorizontalPanel panel = new HorizontalPanel();
            panel.add(core);
            panel.add(new HTML("&nbsp&nbsp"));
            panel.add(editRsIdButton(rsId));
            panel.add(new HTML("&nbsp&nbsp"));
            panel.add(deleteRsIdButton(rsId));
            panel.add(new HTML("&nbsp&nbsp"));
            return panel;
        } else {
            return core;
        }
    }

    private Button deleteRsIdButton(final RegistrarScopeIdDTO idDTO) {
        Button btn = new Button(constants.delete());
        btn.addStyleName(css.treeButton());
        btn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                dataService.removeRegistrarScopeIdentifier(idDTO, new AsyncCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        superPanel.refresh();
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        Window.alert(messages.serverError(caught.getMessage()));
                    }
                });
            }
        });
        return btn;
    }

    private Button editRsIdButton(final RegistrarScopeIdDTO idDTO) {
        Button btn = new Button(constants.edit());
        btn.addStyleName(css.treeButton());
        btn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                new EditRegistrarScopeIdDialogBox(idDTO, new Operation<RegistrarScopeIdDTO>() {
                    @Override
                    public void run(RegistrarScopeIdDTO id) {
                        superPanel.refresh();
                    }
                }).show();
            }
        });
        return btn;
    }

    private Button addRsIdButton() {
        Button btn = new Button(constants.add());
        btn.addStyleName(css.treeButton());
        btn.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Long registrarId = dto.getRegistrar().getId();
                Long digDocId = dto.getId();

                new InsertRegistrarScopeIdDialogBox(registrarId, digDocId, new Operation<RegistrarScopeIdDTO>() {
                    @Override
                    public void run(RegistrarScopeIdDTO id) {
                        superPanel.refresh();
                    }
                }).show();
            }
        });
        return btn;
    }

    private void addDigitalInstances(UrnNbnDTO urn, TreeItem rootItem) {
        ArrayList<DigitalInstanceDTO> instances = dto.getInstances();
        if (instances != null) {
            for (DigitalInstanceDTO instanceDTO : reorderActiveFirst(instances)) {
                if (instanceDTO.getUrl() == null) {
                    System.err.println("empty url for instance with id " + instanceDTO.getId() == null ? "unknown" : instanceDTO.getId());
                } else {
                    TreeItem instanceItem = digitalInstanceItem(rootItem, urn, instanceDTO);
                    instanceItem.setState(EXPAND_DIGITAL_INSTANCES);
                    rootItem.addItem(instanceItem);
                }
            }
        }
    }

    private ArrayList<DigitalInstanceDTO> reorderActiveFirst(ArrayList<DigitalInstanceDTO> original) {
        ArrayList<DigitalInstanceDTO> result = new ArrayList<DigitalInstanceDTO>(original.size());
        ArrayList<DigitalInstanceDTO> deactivated = new ArrayList<DigitalInstanceDTO>(original.size());
        for (DigitalInstanceDTO instance : original) {
            if (instance.isActive()) {
                result.add(instance);
            } else {
                deactivated.add(instance);
            }
        }
        result.addAll(deactivated);
        return result;
    }

    private TreeItem digitalInstanceItem(TreeItem rootItem, UrnNbnDTO urn, DigitalInstanceDTO instanceDTO) {
        TreeItem instanceItem = new TreeItem(digitalInstancePanel(instanceDTO, urn));
        if (!instanceDTO.isActive()) {
            addLabeledItemIfValueNotNull(instanceItem, constants.url(), instanceDTO.getUrl());
        }
        addLabeledItemIfValueNotNull(instanceItem, constants.format(), instanceDTO.getFormat());
        addLabeledItemIfValueNotNull(instanceItem, constants.accessibility(), instanceDTO.getAccessibility());
        String accessRestriction = null;
        switch (instanceDTO.getAccessRestriction()) {
            case UNKNOWN:
                accessRestriction = constants.accessRestrictionUnknown();
                break;
            case LIMITED_ACCESS:
                accessRestriction = constants.accessRestrictionLimited();
                break;
            case UNLIMITED_ACCESS:
                accessRestriction = constants.accessRestrictionUnlimited();
                break;
        }
        addLabeledItemIfValueNotNull(instanceItem, constants.accessRestriction(), accessRestriction);
        addDigitalLibrary(instanceItem, instanceDTO.getLibrary());
        addLabeledItemIfValueNotNull(instanceItem, constants.created(), instanceDTO.getCreated());
        addLabeledItemIfValueNotNull(instanceItem, constants.deactivated(), instanceDTO.getDeactivated());
        return instanceItem;
    }

    private HorizontalPanel digitalInstancePanel(DigitalInstanceDTO instanceDTO, UrnNbnDTO urn) {
        HorizontalPanel panel = new HorizontalPanel();
        if (instanceDTO.isActive()) {
            // TODO: 4.12.17 icons for access restriction
/*            switch (instanceDTO.getAccessRestriction()) {
                case LIMITED_ACCESS:
                    panel.add(new HTML("<img src='img/todo.png' alt='limited access'/>"));
                    break;
                case UNLIMITED_ACCESS:
                    panel.add(new HTML("<img src='img/todo.png' alt='unlimited access'/>"));
                    break;
                case UNKNOWN:
                    //nothing
                    break;
            }
*/
            String url = instanceDTO.getUrl();
            panel.add(new HTML("<a href =\"" + url + "\" target=\"_blank\">" + url + "</a>"));
            if (activeUser().isSuperAdmin() || activeUserManagesRegistrar()) {
                panel.add(new HTML("&nbsp&nbsp"));
                panel.add(editDigitalInstanceButton(urn, instanceDTO));
                panel.add(new HTML("&nbsp&nbsp"));
                panel.add(deactivateDigitalInstanceButton(instanceDTO));
            }

        } else {
            panel.add(new HTML("<span style=\"color:grey\">" + constants.deactivatedDigitalInstance() + "</span>"));
        }
        // digital instance records
        panel.add(new HTML("&nbsp&nbsp"));
        panel.add(buildImageLink(buildUrlToDigInstRecord(instanceDTO.getId(), "xml"), "img/xml.png"));
        panel.add(new HTML("&nbsp"));
        panel.add(buildImageLink(buildUrlToDigInstRecord(instanceDTO.getId(), "json"), "img/json.png"));


        return panel;
    }

    private void addDigitalLibrary(TreeItem instanceItem, final DigitalLibraryDTO library) {
        if (library != null) {
            Button button = new Button(constants.details());
            button.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    DigitalLibraryDetailsDialogBox dialogBox = new DigitalLibraryDetailsDialogBox(library);
                    dialogBox.show();
                    dialogBox.center();
                }
            });
            addLabeledItemAndButtonIfValueNotNull(instanceItem, constants.digitalLibrary(), library.getName(), button);
        }
    }

    private Button editDigitalInstanceButton(final UrnNbnDTO urn, final DigitalInstanceDTO instance) {
        Button button = new Button(constants.edit(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                ArrayList<DigitalLibraryDTO> libraries = new ArrayList<DigitalLibraryDTO>();
                libraries.add(instance.getLibrary());
                EditDigitalInstanceDialogBox dialog = new EditDigitalInstanceDialogBox(superPanel, urn, instance, libraries);
                dialog.show();
            }
        });
        button.addStyleName(css.treeButton());
        return button;
    }

    private Button deactivateDigitalInstanceButton(final DigitalInstanceDTO instance) {
        Button button = new Button(constants.deactivate(), new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                DeactivateDigitalInstanceDialogBox dialog = new DeactivateDigitalInstanceDialogBox(superPanel, instance);
                dialog.center();
                dialog.setPopupPosition(dialog.getPopupLeft(), 105);
                dialog.show();
            }
        });
        button.addStyleName(css.treeButton());
        return button;
    }

    private TreeItem addItemIfNotNull(TreeItem rootItem, HTML textHtml) {
        if (textHtml != null) {
            TreeItem newItem = new TreeItem(textHtml);
            rootItem.addItem(newItem);
            return newItem;
        } else {
            return null;
        }
    }

    private UserDTO activeUser() {
        return superPanel.getActiveUser();
    }
}
