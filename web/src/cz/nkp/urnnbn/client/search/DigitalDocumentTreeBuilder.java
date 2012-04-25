package cz.nkp.urnnbn.client.search;

import java.util.ArrayList;

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
import cz.nkp.urnnbn.shared.dto.UserDTO;

public class DigitalDocumentTreeBuilder extends TreeBuilder {

	private static boolean EXPAND_TECHNICAL = false;
	private static boolean EXPAND_IDENTIFIERS = false;
	private static boolean EXPAND_DIGITAL_INSTANCES = false;
	private final UserDTO user;

	private ConstantsImpl constants = GWT.create(ConstantsImpl.class);
	private final DigitalDocumentDTO dto;

	public DigitalDocumentTreeBuilder(DigitalDocumentDTO dto, UserDTO user, SearchPanel superPanel) {
		super(superPanel);
		this.dto = dto;
		this.user = user;
	}

	public TreeItem getItem() {
		TreeItem rootItem = new TreeItem(digitalDocumentItem());
		addItemIfNotNull(rootItem, dto.getUrn());
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
		return rootItem;
	}

	private Panel digitalDocumentItem() {
		HorizontalPanel result = new HorizontalPanel();
		result.add(new Label(constants.digitalDocument()));
		// if (true) {
		if (user.isSuperAdmin()) {
			result.add(new HTML("&nbsp&nbsp"));
			result.add(editDocumentButton());
		}
		return result;
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
			//addLabeledItemIfValueNotNull(rootItem, constants.registrar(), registrar.getName() + " <a href=\"" + url + "\">podrobnosti</a>");
			addLabeledItemIfValueNotNull(rootItem, constants.registrar(), registrar.getName());
		} else {
			System.err.println("no registrar");
		}
	}

	private void addArchiver(TreeItem rootItem) {
		if (dto.getArchiver() != null) {
			Button button = new Button("podrobnosti");
			button.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					ArchiverDetailsDialogBox dialogBox = new ArchiverDetailsDialogBox(dto.getArchiver());
					dialogBox.show();
					dialogBox.center();
				}
			});

			addLabeledItemAndButtonIfValueNotNull(rootItem, constants.archiver(), dto.getArchiver().getName(), button);
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
				addLabeledItemIfValueNotNull(metadataItem, constants.colorDepth(), metadata.getColorDepth());
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
		return horizontal + "x" + vertical;
	}

	private String buildSizeString(TechnicalMetadataDTO metadata) {
		if (metadata.getPictureWidth() == null && metadata.getPicturHeight() == null) {
			return null;
		}
		String width = metadata.getPictureWidth() == null ? "unknown" : metadata.getPictureWidth().toString();
		String height = metadata.getPicturHeight() == null ? "unknown" : metadata.getPicturHeight().toString();
		return width + "x" + height;
	}

	void addLabeledItemIfValueNotNull(TreeItem item, String label, Object value) {
		addLabeledRowIfValueNotNull(label, value, item, css.attrLabel());
	}

	private void addDigitalInstances(TreeItem rootItem) {
		ArrayList<DigitalInstanceDTO> instances = dto.getInstances();
		if (instances != null) {
			for (DigitalInstanceDTO instanceDTO : instances) {
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

	private TreeItem digitalInstanceItem(TreeItem rootItem, DigitalInstanceDTO instanceDTO) {
		String url = instanceDTO.getUrl();
		TreeItem instanceItem = new TreeItem("<a href =\"" + url + "\">" + url + "</a>");
		addLabeledItemIfValueNotNull(instanceItem, constants.format(), instanceDTO.getFormat());
		addLabeledItemIfValueNotNull(instanceItem, constants.accessibility(), instanceDTO.getAccessibility());
		addDigitalLibrary(instanceItem, instanceDTO.getLibrary());
		addLabeledItemIfValueNotNull(instanceItem, constants.created(), instanceDTO.getCreated());
		if (instanceDTO.getModified() != null && !instanceDTO.getModified().equals(instanceDTO.getCreated())) {
			addLabeledItemIfValueNotNull(instanceItem, constants.modified(), instanceDTO.getModified());
		}
		return instanceItem;
	}

	private void addDigitalLibrary(TreeItem instanceItem, final DigitalLibraryDTO library) {
		if (library != null) {
			Button button = new Button("podrobnosti");
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
}
