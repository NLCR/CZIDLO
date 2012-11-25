package cz.nkp.urnnbn.client.search;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TreeItem;

import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.institutions.ArchiverDetailsDialogBox;
import cz.nkp.urnnbn.client.institutions.DigitalLibraryDetailsDialogBox;
import cz.nkp.urnnbn.shared.dto.DigitalDocumentDTO;
import cz.nkp.urnnbn.shared.dto.DigitalInstanceDTO;
import cz.nkp.urnnbn.shared.dto.DigitalLibraryDTO;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.dto.RegistrarScopeIdDTO;
import cz.nkp.urnnbn.shared.dto.TechnicalMetadataDTO;
import cz.nkp.urnnbn.shared.dto.UrnNbnDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO;

public class DigitalDocumentTreeBuilder extends TreeBuilder {

	private static boolean EXPAND_TECHNICAL = false;
	private static boolean EXPAND_IDENTIFIERS = false;
	private static boolean EXPAND_DIGITAL_INSTANCES = false;
	private static String API_VERSION = "v3";

	private ConstantsImpl constants = GWT.create(ConstantsImpl.class);
	private final DigitalDocumentDTO dto;

	public DigitalDocumentTreeBuilder(DigitalDocumentDTO dto, SearchPanel superPanel) {
		super(superPanel);
		this.dto = dto;
	}

	public TreeItem getItem() {
		TreeItem rootItem = new TreeItem(digitalDocumentItem(dto.getUrn().isActive(), dto.getUrn()));
		addUrnNbn(rootItem);

		// appendUrnNbn(rootItem, dto.getUrn());
		addRegistrar(rootItem);
		addArchiver(rootItem);
		addLabeledItemIfValueNotNull(rootItem, constants.financed(), dto.getFinanced());
		addLabeledItemIfValueNotNull(rootItem, constants.contractNumber(), dto.getContractNumber());
		addLabeledItemIfValueNotNull(rootItem, constants.created(), dto.getCreated());
		if (dto.getModified() != null && !dto.getModified().equals(dto.getCreated())) {
			addLabeledItemIfValueNotNull(rootItem, constants.modified(), dto.getModified());
		}
		addIdentifiers(rootItem);
		addTechnicalMetadata(rootItem);
		addDigitalInstances(rootItem);
		if (!dto.getUrn().isActive()) {
			rootItem.setState(false);
		}
		return rootItem;
	}

	private void addUrnNbn(TreeItem root) {
		UrnNbnDTO urnNbn = dto.getUrn();
		String urnNbnItemHtml = urnNbn.isActive() ? urnNbn.toString() : "<span style=\"color:grey;text-decoration:line-through;\">"
				+ urnNbn.toString() + "</span>";
		TreeItem urnNbnItem = root.addItem(urnNbnItemHtml);
		addLabeledItemIfValueNotNull(urnNbnItem, constants.created(), urnNbn.getCreated());
		addLabeledItemIfValueNotNull(urnNbnItem, constants.modified(), urnNbn.getLastModified());

		// predecessors
		List<UrnNbnDTO> predecessors = urnNbn.getPredecessors();
		if (predecessors != null && !predecessors.isEmpty()) {
			TreeItem predecessorsItem = urnNbnItem.addItem("předchůdci");
			for (UrnNbnDTO predecessor : predecessors) {
				predecessorsItem.addItem(urnNbnItemHtlm(predecessor));
			}
			predecessorsItem.setState(true);
		}

		// successors
		List<UrnNbnDTO> successors = urnNbn.getSuccessors();
		if (successors != null && !successors.isEmpty()) {
			TreeItem successorsItem = urnNbnItem.addItem("následovníci");
			for (UrnNbnDTO successor : successors) {
				successorsItem.addItem(urnNbnItemHtlm(successor));
			}
			successorsItem.setState(true);
		}
	}

	private String urnNbnItemHtlm(UrnNbnDTO urnNbn) {
		return urnNbn.toString() + "&nbsp&nbsp" + linkToDigDocHtmlByUrnNbn(urnNbn, constants.showRecord()) + "&nbsp&nbsp"
				+ linkToDigDocXmlByUrnNbn(urnNbn, constants.showRecordInXml());
	}

	private String linkToDigDocXmlByUrnNbn(UrnNbnDTO urnNbn, String linkText) {
		return linkToResolver(urnNbn, linkText, "show", "xml", true);
	}

	private String linkToDigDocHtmlByUrnNbn(UrnNbnDTO urnNbn, String linkText) {
		return linkToResolver(urnNbn, linkText, "show", "html", true);
	}

	private String linkToResolver(UrnNbnDTO urn, String linkText, String action, String format, boolean inNewWindow) {
		StringBuilder result = new StringBuilder();
		result.append("<a href=");
		result.append("\"");
		// url itself
		result.append("/api").append('/').append(API_VERSION).append('/').append("resolver").append('/');
		// result.append("/resolver");
		result.append(urn.toString());
		result.append("?action=").append(action);
		if (!action.equals("decide")) {
			result.append("&format=").append(format);
		}
		result.append("\"");
		if (inNewWindow) {
			result.append(" target=\"_blank\"");
		}
		result.append(">");
		result.append(linkText);
		result.append("</a>");
		return result.toString();
	}

	private Panel digitalDocumentItem(boolean active, UrnNbnDTO urnNbn) {
		HorizontalPanel result = new HorizontalPanel();
		// label
		if (active) {
			result.add(new Label(constants.digitalDocument()));
		} else {
			// result.add(new
			// HTML("<span style=\"text-decoration:line-through;\">" +
			// constants.digitalDocument() + "</span>"
			result.add(new HTML(constants.digitalDocument() + " <span style=\"color:grey\">(" + constants.inactiveDD() + ")</span>"));
		}
		// button to edit record
		if (activeUser().isSuperAdmin() || activeUserManagesRegistrar()) {
			result.add(new HTML("&nbsp&nbsp"));
			result.add(editDocumentButton());
		}
		// link to record in xml
		result.add(new HTML("&nbsp&nbsp" + linkToDigDocXmlByUrnNbn(urnNbn, constants.showRecordInXml())));
		return result;
	}

	private boolean activeUserManagesRegistrar() {
		return superPanel.userManagesRegistrar(dto.getRegistrar());
	}

	private Button editDocumentButton() {
		return new Button(constants.edit(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				EditDigitalDocumentDialogBox dialog = new EditDigitalDocumentDialogBox(dto, superPanel);
				dialog.center();
				dialog.setPopupPosition(dialog.getPopupLeft(), 105);
				dialog.show();
			}
		});
	}

	private void addRegistrar(TreeItem rootItem) {
		if (dto.getRegistrar() != null) {
			RegistrarDTO registrar = dto.getRegistrar();
			// TODO: odkazovat se do aplikace na zaznam registratora
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
		if (dto.getArchiver() != null) {
			Button button = new Button(constants.details());
			button.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					ArchiverDetailsDialogBox dialogBox = new ArchiverDetailsDialogBox(dto.getArchiver());
					dialogBox.show();
					dialogBox.center();
				}
			});

			addLabeledItemAndButtonIfValueNotNull(rootItem, constants.archiver(), dto.getArchiver().getName(), button);
		} else {
			addLabeledItemIfValueNotNull(rootItem, constants.archiver(), dto.getRegistrar().getName());
		}
	}

	private void addLabeledItemAndButtonIfValueNotNull(TreeItem rootItem, String label, String text, Button button) {
		String spanClass = css.attrLabel();
		addLabeledRowAndButtonIfValueNotNull(label, text, rootItem, spanClass, button, css.detailsButton());
	}

	private void addIdentifiers(TreeItem rootItem) {
		ArrayList<RegistrarScopeIdDTO> idList = dto.getRegistrarScopeIdList();
		if (idList != null && !idList.isEmpty()) {
			TreeItem idsItem = addItemIfNotNull(rootItem, constants.identifiers());
			for (RegistrarScopeIdDTO idDTO : idList) {
				String row = idDTO.getType() + '=' + idDTO.getValue();
				addItemIfNotNull(idsItem, row);
			}
			idsItem.setState(EXPAND_IDENTIFIERS);
		}
	}

	private void addTechnicalMetadata(TreeItem rootItem) {
		TechnicalMetadataDTO metadata = dto.getTechnicalMetadata();
		if (metadata != null) {
			TreeItem metadataItem = addItemIfNotNull(rootItem, constants.technicalMetadata());
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
				String colorDepth = metadata.getColorDepth() + ' ' + constants.bits();
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

	private void addDigitalInstances(TreeItem rootItem) {
		ArrayList<DigitalInstanceDTO> instances = dto.getInstances();
		if (instances != null) {
			for (DigitalInstanceDTO instanceDTO : reorderActiveFirst(instances)) {
				if (instanceDTO.getUrl() == null) {
					System.err.println("empty url for instance with id " + instanceDTO.getId() == null ? "unknown" : instanceDTO.getId());
				} else {
					TreeItem instanceItem = digitalInstanceItem(rootItem, instanceDTO);
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

	private TreeItem digitalInstanceItem(TreeItem rootItem, DigitalInstanceDTO instanceDTO) {
		String url = instanceDTO.getUrl();
		TreeItem instanceItem = null;
		if (instanceDTO.isActive()) {
			instanceItem = new TreeItem("<a href =\"" + url + "\" target=\"_blank\">" + url + "</a>");
		} else {
			instanceItem = new TreeItem(new HTML("<span style=\"color:grey\">" + constants.deactivatedDigitalInstance() + "</span>"));
			addLabeledItemIfValueNotNull(instanceItem, constants.url(), instanceDTO.getUrl());
		}
		addLabeledItemIfValueNotNull(instanceItem, constants.format(), instanceDTO.getFormat());
		addLabeledItemIfValueNotNull(instanceItem, constants.accessibility(), instanceDTO.getAccessibility());
		addDigitalLibrary(instanceItem, instanceDTO.getLibrary());
		addLabeledItemIfValueNotNull(instanceItem, constants.created(), instanceDTO.getCreated());
		if (instanceDTO.getModified() != null && !instanceDTO.getModified().equals(instanceDTO.getCreated())) {
			if (instanceDTO.isActive()) {
				addLabeledItemIfValueNotNull(instanceItem, constants.modified(), instanceDTO.getModified());
			} else {
				addLabeledItemIfValueNotNull(instanceItem, constants.deactivated(), instanceDTO.getModified());
			}
		}
		return instanceItem;
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

	private TreeItem addItemIfNotNull(TreeItem item, String text) {
		if (text != null) {
			TreeItem newItem = new TreeItem(text);
			item.addItem(newItem);
			return newItem;
		} else {
			return null;
		}
	}

	private UserDTO activeUser() {
		return superPanel.getActiveUser();
	}
}
