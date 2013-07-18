package cz.nkp.urnnbn.client.institutions;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.i18n.MessagesImpl;
import cz.nkp.urnnbn.client.resources.InstitutionsPanelCss;
import cz.nkp.urnnbn.client.services.InstitutionsService;
import cz.nkp.urnnbn.client.services.InstitutionsServiceAsync;
import cz.nkp.urnnbn.shared.dto.CatalogDTO;
import cz.nkp.urnnbn.shared.dto.DigitalLibraryDTO;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO;

public class RegistrarDetailsPanel extends VerticalPanel {
	private final ConstantsImpl constants = GWT.create(ConstantsImpl.class);
	private final MessagesImpl messages = GWT.create(MessagesImpl.class);
	private final InstitutionsPanelCss css = InstitutionsResources.loadCss();
	private final InstitutionsServiceAsync institutionsService = GWT.create(InstitutionsService.class);
	private final UserDTO user;
	private RegistrarDTO registrar;
	private InstitutionsAdminstrationPanel superPanel;
	private ArrayList<DigitalLibraryDTO> libraries = new ArrayList<DigitalLibraryDTO>();
	private ArrayList<CatalogDTO> catalogs = new ArrayList<CatalogDTO>();

	public RegistrarDetailsPanel(InstitutionsAdminstrationPanel superPanel, UserDTO user, RegistrarDTO registrar) {
		this.superPanel = superPanel;
		this.user = user;
		this.registrar = registrar;
	}

	public void onLoad() {
		loadLibraries();
		loadCatalogs();
		reload();
	}

	public void reload(RegistrarDTO registrar) {
		this.registrar = registrar;
		reload();
	}

	public void reload() {
		clear();
		add(backToInsitutionsButton());
		add(registrarDetailsPanel());
		add(digitalLibrariesPanel());
		add(catalogsPanel());
	}

	public Long getRegistrarId() {
		return registrar.getId();
	}

	public void addLibrary(DigitalLibraryDTO library) {
		libraries.add(library);
		reload();
	}

	public void updateLibrary(DigitalLibraryDTO library) {
		libraries.remove(library);
		libraries.add(library);
		reload();
	}

	public void removeCatalog(CatalogDTO catalog) {
		catalogs.remove(catalog);
		reload();
	}

	public void addCatalog(CatalogDTO catalog) {
		catalogs.add(catalog);
		reload();
	}

	public void updateCatalog(CatalogDTO catalog) {
		catalogs.remove(catalog);
		catalogs.add(catalog);
		reload();
	}

	public void removeLibrary(DigitalLibraryDTO library) {
		libraries.remove(library);
		reload();
	}

	private void loadLibraries() {
		institutionsService.getLibraries(registrar.getId(), new AsyncCallback<ArrayList<DigitalLibraryDTO>>() {

			@Override
			public void onSuccess(ArrayList<DigitalLibraryDTO> result) {
				libraries = result;
				reload();
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(constants.serverError() + ": " + caught.getMessage());
			}
		});
	}

	private void loadCatalogs() {
		institutionsService.getCatalogs(registrar.getId(), new AsyncCallback<ArrayList<CatalogDTO>>() {

			@Override
			public void onSuccess(ArrayList<CatalogDTO> result) {
				catalogs = result;
				reload();
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(constants.serverError() + ": " + caught.getMessage());
			}
		});
	}

	private VerticalPanel registrarDetailsPanel() {
		VerticalPanel result = new VerticalPanel();
		result.setStyleName(css.block());
		Label heading = new Label(registrar.getName());
		heading.setStyleName(css.listHeading());
		result.add(heading);
		// name
		HorizontalPanel namePanel = new HorizontalPanel();
		namePanel.add(new Label(constants.title() + ":"));
		namePanel.add(new HTML("&nbsp"));
		namePanel.add(new Label(registrar.getName()));
		result.add(namePanel);
		// code
		HorizontalPanel codePanel = new HorizontalPanel();
		codePanel.add(new Label(constants.code() + ":"));
		codePanel.add(new HTML("&nbsp"));
		codePanel.add(new Label(registrar.getCode()));
		result.add(codePanel);
		// description
		if (registrar.getDescription() != null) {
			HorizontalPanel descPanel = new HorizontalPanel();
			descPanel.add(new Label(constants.description() + ":"));
			descPanel.add(new HTML("&nbsp"));
			descPanel.add(new Label(registrar.getDescription()));
			result.add(descPanel);
		}
		// registration modes
		result.add(registrationModesPanel());
		// visibility and order
		if (user.isSuperAdmin()) {
			result.add(orderAndVisibilityPanel());
			result.add(editRegistrarVisiblityAndOrderButton());
		}
		// edit button 
		if (user.isSuperAdmin() || userManagesRegistrar(registrar)) {
			result.add(editRegistrarButton());
		}
		return result;
	}
	
	private VerticalPanel registrationModesPanel(){
		VerticalPanel result = new VerticalPanel();
		result.setStyleName(css.block());
		//title
		Label modesHeading = new Label(constants.allowedRegistrationModes());
		modesHeading.setStyleName(css.listHeadingLevel2());
		result.add(modesHeading);
		//modes
		result.add(registrationModePanel(constants.modeByResolver(), registrar.isRegModeByResolverAllowed()));
		result.add(registrationModePanel(constants.modeByReservation(), registrar.isRegModeByReservationAllowed()));
		result.add(registrationModePanel(constants.modeByRegistrar(),registrar.isRegModeByRegistrarAllowed()));
		return result;
	}
	
	private VerticalPanel orderAndVisibilityPanel() {
		VerticalPanel result = new VerticalPanel();
		result.setStyleName(css.block());
		//title
		Label modesHeading = new Label(constants.OrderAndVisibility());
		modesHeading.setStyleName(css.listHeadingLevel2());
		result.add(modesHeading);
		// order
		HorizontalPanel orderPanel = new HorizontalPanel();
		orderPanel.add(new Label(constants.order() + ":"));
		orderPanel.add(new HTML("&nbsp"));
		orderPanel.add(new Label(Long.toString(registrar.getOrder())));
		result.add(orderPanel);
		HorizontalPanel hiddenPanel = new HorizontalPanel();
		hiddenPanel.add(new Label(constants.hidden() + ":"));
		hiddenPanel.add(new HTML("&nbsp"));
		hiddenPanel.add(new Label((registrar.isHidden())?constants.yes():constants.no()));
		result.add(hiddenPanel);
		return result;
	}
	
	private HorizontalPanel registrationModePanel(String label, boolean checked){
		HorizontalPanel result = new HorizontalPanel();
		result.add(new Label(label));
		result.add(new HTML("&nbsp"));
		CheckBox checkBox = new CheckBox();
		checkBox.setValue(checked);
		checkBox.setEnabled(false);
		result.add(checkBox);
		return result;
	}
	
	

	private Button editRegistrarButton() {
		return new Button(constants.edit(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				EditRegistrarDialogBox dialogBox = new EditRegistrarDialogBox(RegistrarDetailsPanel.this, registrar);
				dialogBox.center();
				dialogBox.show();
			}
		});
	}
	
	private Button editRegistrarVisiblityAndOrderButton() {
		return new Button(constants.editOrderAndVisibility(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				EditArchiverOrderAndVisibilityDialogBox dialogBox = new EditArchiverOrderAndVisibilityDialogBox(RegistrarDetailsPanel.this, registrar);
				dialogBox.center();
				dialogBox.show();
			}
		});
	}

	private VerticalPanel digitalLibrariesPanel() {
		VerticalPanel result = new VerticalPanel();
		result.setStyleName(css.block());
		result.add(digitalLibrariesHeading());
		result.add(librariesGrid());
		if (user.isSuperAdmin() || userManagesRegistrar(registrar)) {
			result.add(addLibraryButton());
		}
		return result;
	}

	private Widget digitalLibrariesHeading() {
		Label result = new Label(constants.digitalLibraryList());
		result.setStyleName(css.listHeadingLevel2());
		return result;
	}

	private Grid librariesGrid() {
		Grid result = new Grid(libraries.size(), libraryGridColumns());
		for (int i = 0; i < libraries.size(); i++) {
			DigitalLibraryDTO lib = libraries.get(i);
			Label name = new Label(lib.getName());
			result.setWidget(i, 0, name);
			result.setWidget(i, 1, libraryDetailsButton(lib));
			if (user.isSuperAdmin() || userManagesRegistrar(registrar)) {
				result.setWidget(i, 2, libraryEditButton(lib));
				result.setWidget(i, 3, libraryDeleteButton(lib));
			}
		}
		return result;
	}

	private Button libraryDetailsButton(final DigitalLibraryDTO lib) {
		return new Button(constants.details(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				new DigitalLibraryDetailsDialogBox(lib).show();
			}
		});
	}

	private Button libraryDeleteButton(final DigitalLibraryDTO lib) {
		return new Button(constants.delete(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (Window.confirm(messages.confirmDeleteDigitalLibrary(lib.getName()))) {
					institutionsService.deleteDigitalLibrary(lib, new AsyncCallback<Void>() {

						@Override
						public void onSuccess(Void result) {
							removeLibrary(lib);
						}

						@Override
						public void onFailure(Throwable caught) {
							Window.alert(messages.digitalLibraryCannotBeDeleted(lib.getName()) + ": " + caught.getMessage());
						}
					});
				}
			}
		});
	}

	private Button libraryEditButton(final DigitalLibraryDTO lib) {
		return new Button(constants.edit(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				new EditDigitalLibraryDialogBox(RegistrarDetailsPanel.this, lib).show();
			}
		});
	}

	private int libraryGridColumns() {
		if (user.isSuperAdmin() || userManagesRegistrar(registrar)) {
			return 4;
		} else {
			return 2;
		}
	}

	private Button addLibraryButton() {
		return new Button(constants.add(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				new AddDigitalLibraryDialogBox(RegistrarDetailsPanel.this).show();
			}
		});
	}

	private VerticalPanel catalogsPanel() {
		VerticalPanel result = new VerticalPanel();
		result.setStyleName(css.block());
		result.add(catalogsHeading());
		result.add(catalogsGrid());
		if (user.isSuperAdmin() || userManagesRegistrar(registrar)) {
			result.add(addCatalogButton());
		}
		return result;
	}

	private Grid catalogsGrid() {
		Grid result = new Grid(catalogs.size(), catalogGridColumns());
		for (int i = 0; i < catalogs.size(); i++) {
			CatalogDTO cat = catalogs.get(i);
			Label name = new Label(cat.getName());
			result.setWidget(i, 0, name);
			result.setWidget(i, 1, catalogDetailsButton(cat));
			if (user.isSuperAdmin() || userManagesRegistrar(registrar)) {
				result.setWidget(i, 2, catalogEditButton(cat));
				result.setWidget(i, 3, catalogDeleteButton(cat));
			}
		}
		return result;
	}

	private Widget catalogEditButton(final CatalogDTO cat) {
		return new Button(constants.edit(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				new EditCatalogDialogBox(RegistrarDetailsPanel.this, cat).show();
			}
		});
	}

	private Widget catalogDeleteButton(final CatalogDTO cat) {
		return new Button(constants.delete(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (Window.confirm(messages.confirmDeleteCatalog(cat.getName()))) {
					institutionsService.deleteCatalog(cat, new AsyncCallback<Void>() {

						@Override
						public void onSuccess(Void result) {
							removeCatalog(cat);
						}

						@Override
						public void onFailure(Throwable caught) {
							Window.alert(constants.serverError() + ": " + caught.getMessage());
						}
					});
				}
			}
		});

	}

	private Button catalogDetailsButton(final CatalogDTO cat) {
		return new Button(constants.details(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				new CatalogDetailsDialogBox(cat).show();
			}
		});

	}

	private int catalogGridColumns() {
		if (user.isSuperAdmin() || userManagesRegistrar(registrar)) {
			return 4;
		} else {
			return 2;
		}
	}

	private Widget catalogsHeading() {
		Label heading = new Label(constants.catalogList());
		heading.setStyleName(css.listHeadingLevel2());
		return heading;
	}

	private Button addCatalogButton() {
		return new Button(constants.add(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				new AddCatalogDialogBox(RegistrarDetailsPanel.this);
			}
		});
	}

	private Button backToInsitutionsButton() {
		Button result = new Button("Zpět na seznam institucí");
		result.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				superPanel.showInstitutions();

			}
		});
		return result;
	}

	public boolean userManagesRegistrar(RegistrarDTO registrar) {
		return superPanel.userManagesRegistrar(registrar);
	}
}
