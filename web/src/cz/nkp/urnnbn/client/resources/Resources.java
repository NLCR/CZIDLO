package cz.nkp.urnnbn.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;

public interface Resources extends ClientBundle {
	public static final Resources INSTANCE = GWT.create(Resources.class);

	@Source("Main.css")
	public MainCss MainCss();

	@Source("SearchPanel.css")
	public SearchPanelCss SearchPanelCss();

	@Source("InstitutionsPanel.css")
	public InstitutionsPanelCss InstitutionsPanelCss();

	@Source("Dialogs.css")
	public DialogsCss DialogsCss();

	@Source("InsertRecordPanel.css")
	public InsertRecordPanelCss InsertRecordPanelCss();
	
	@Source("ProcessAdministration.css")
	public ProcessAdministrationCss ProcessAdministrationCss(); 

	// @Source("config.xml")
	// public TextResource initialConfiguration();
	//
	// @Source("manual.pdf")
	// public DataResource ownersManual();
}