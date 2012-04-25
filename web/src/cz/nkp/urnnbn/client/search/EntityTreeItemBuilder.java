package cz.nkp.urnnbn.client.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TreeItem;

import cz.nkp.urnnbn.client.forms.intEntities.SourceDocumentForm;
import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
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

	ConstantsImpl constants = GWT.create(ConstantsImpl.class);
	final UserDTO user;
	private final PrimaryOriginatorDTO primaryOriginator;
	private final PublicationDTO publication;
	private final SourceDocumentDTO srcDoc;
	TreeItem result;

	public EntityTreeItemBuilder(UserDTO user, SearchPanel superPanel, PrimaryOriginatorDTO primaryOriginator, PublicationDTO publication,
			SourceDocumentDTO srcDoc) {
		super(superPanel);
		this.user = user;
		this.primaryOriginator = primaryOriginator;
		this.publication = publication;
		this.srcDoc = srcDoc;
	}

	public static TreeItem getItem(IntelectualEntityDTO dto, UserDTO user, SearchPanel superPanel) {
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
		panel.add(new HTML(entityType()));
		if (user.isSuperAdmin()) {
			panel.add(new HTML("&nbsp&nbsp"));
			panel.add(editEntityButton());
		}
		result = new TreeItem(panel);
		addRows();
		return result;
	}

	private Button editEntityButton() {
		return new Button(constants.edit(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				IntelectualEntityDTO entity = getDto();
				SourceDocumentForm srcDocForm = (entity instanceof AnalyticalDTO) ? new SourceDocumentForm(srcDoc) : null;
				new EditIntelectualEntityDialogBox(superPanel, entity, primaryOriginator, srcDocForm).show();
			}
		});
	}

	abstract IntelectualEntityDTO getDto();

	abstract void addRows();

	abstract String entityType();
	
	void addLabeledRowIfNotNull(String label, Object value) {
		addLabeledRowIfNotNull(result, label, value);
	}

	void addLabeledRowIfNotNull(TreeItem root, String label, Object value) {
		addLabeledRowIfValueNotNull(label, value, root, css.attrLabel());
	}

	void appendSourceDocumentIfNotNull() {
		TreeItem srcDocItem = new TreeItem(constants.sourceDoc());
		if (srcDoc != null) {
			addLabeledRowIfNotNull(srcDocItem, constants.ccnb(), srcDoc.getCcnb());
			addLabeledRowIfNotNull(srcDocItem, constants.isbn(), srcDoc.getIsbn());
			addLabeledRowIfNotNull(srcDocItem, constants.issn(), srcDoc.getIssn());
			addLabeledRowIfNotNull(srcDocItem, constants.periodicalIssueTitle(), srcDoc.getIssueTitle());
			addLabeledRowIfNotNull(srcDocItem, constants.otherId(), srcDoc.getOtherId());
			PublicationDTO publication = srcDoc.getPublication();
			if (publication != null) {
				addLabeledRowIfNotNull(srcDocItem, constants.publicationPlace(), publication.getPublicationPlace());
				addLabeledRowIfNotNull(srcDocItem, constants.publicationYear(), publication.getPublicationYear());
				addLabeledRowIfNotNull(srcDocItem, constants.publisher(), publication.getPublisher());
			}
			addLabeledRowIfNotNull(srcDocItem, constants.title(), srcDoc.getTitle());
			addLabeledRowIfNotNull(srcDocItem, constants.volumeTitle(), srcDoc.getVolumeTitle());
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

	String buildTitle(String first, String second) {
		String separator = ", ";
		StringBuilder result = new StringBuilder();
		result.append(first);
		result.append(separator).append(second);
		return result.toString();
	}

	String buildTitle(String first, String second, String third) {
		String separator = ", ";
		StringBuilder result = new StringBuilder();
		result.append(first);
		result.append(separator).append(second);
		result.append(separator).append(third);
		return result.toString();
	}
}
