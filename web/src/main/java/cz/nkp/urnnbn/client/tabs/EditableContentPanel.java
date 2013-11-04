package cz.nkp.urnnbn.client.tabs;

import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.widgets.RichTextEditor;

import cz.nkp.urnnbn.client.services.StaticContentService;
import cz.nkp.urnnbn.client.services.StaticContentServiceAsync;
import cz.nkp.urnnbn.shared.dto.ContentDTO;

/**
 * 
 * @author Martin Řehánek
 * @author Václav Rosecký
 */

public class EditableContentPanel extends SingleTabContentPanel {

	private static final Logger logger = Logger.getLogger(EditableContentPanel.class.getName());
	private final StaticContentServiceAsync staticContentService = GWT.create(StaticContentService.class);

	private final String name;
	private ContentDTO content = null;
	private VerticalPanel contentPanel = null;
	private RichTextEditor editor = null;
	private Button editButton = null;
	private HorizontalPanel saveAndCancelButtonsPanel;

	public EditableContentPanel(TabsPanel tabsPanel, String name) {
		super(tabsPanel);
		this.name = name;
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		if (getActiveUser().isSuperAdmin()) {
			editButton = initEditButton();
			saveAndCancelButtonsPanel = initSaveAndCancelButtonsPanel();
			editor = initContentEdit();
		}
		contentPanel = new VerticalPanel();
		contentPanel.add(new HTML(constants.loading() + "..."));
		add(contentPanel);
	}

	private HorizontalPanel initSaveAndCancelButtonsPanel() {
		HorizontalPanel panel = new HorizontalPanel();
		panel.add(saveButton());
		panel.add(cancelButton());
		return panel;
	}

	private Button saveButton() {
		Button result = new Button(constants.save());
		result.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent arg0) {
				content.setContent(editor.getValue());
				saveContent();
			}
		});
		return result;
	}

	private Button cancelButton() {
		Button result = new Button(constants.cancel());
		result.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent arg0) {
				contentPanel.clear();
				contentPanel.add(new HTML(content.getContent()));
				contentPanel.add(editButton);
			}
		});
		return result;

	}

	public void saveContent() {
		staticContentService.update(content, new AsyncCallback<Void>() {

			public void onFailure(Throwable caught) {
				Window.alert(messages.serverError(caught.getMessage()));
			}

			public void onSuccess(Void result) {
				contentPanel.clear();
				contentPanel.add(new HTML(content.getContent()));
				contentPanel.add(editButton);
			}
		});
	}

	private RichTextEditor initContentEdit() {
		RichTextEditor result = new RichTextEditor();
		result.setHeight("350px");
		result.setWidth("900px");
		result.setBorder("2px solid");
		return result;
	}

	private Button initEditButton() {
		Button result = new Button(constants.edit());
		result.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent arg0) {
				contentPanel.clear();
				// In Chrome/Chromium RichTextEditor.setValue(html) works just once (other times sets "") so I have to
				// create other instance
				editor = initContentEdit();
				contentPanel.add(editor);
				editor.setValue(content.getContent());
				contentPanel.add(saveAndCancelButtonsPanel);
			}

		});
		return result;
	}

	public void loadContent() {
		String languageCode = LocaleInfo.getCurrentLocale().getLocaleName();
		staticContentService.getContentByNameAndLanguage(name, languageCode, new AsyncCallback<ContentDTO>() {

			public void onFailure(Throwable error) {
				logger.severe(error.getMessage());
				contentPanel.clear();
			}

			public void onSuccess(ContentDTO result) {
				content = result;
				contentPanel.clear();
				contentPanel.add(new HTML(content.getContent()));
				if (getActiveUser().isSuperAdmin()) {
					contentPanel.add(editButton);
				}
			}
		});
	}

	@Override
	public void onSelection() {
		if (content == null) {
			loadContent();
		}
	}

	@Override
	public void onDeselectionSelection() {
		// TODO Auto-generated method stub
	}
}
