package cz.nkp.urnnbn.client.institutions;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.i18n.MessagesImpl;
import cz.nkp.urnnbn.client.resources.InstitutionsPanelCss;
import cz.nkp.urnnbn.client.services.InstitutionsService;
import cz.nkp.urnnbn.client.services.InstitutionsServiceAsync;
import cz.nkp.urnnbn.shared.dto.ArchiverDTO;
import cz.nkp.urnnbn.shared.dto.RegistrarDTO;
import cz.nkp.urnnbn.shared.dto.UserDTO;

public class InstitutionListPanel extends VerticalPanel {
	private final ConstantsImpl constants = GWT.create(ConstantsImpl.class);
	private final MessagesImpl messages = GWT.create(MessagesImpl.class);
	private final InstitutionsPanelCss css = InstitutionsResources.loadCss();
	private final InstitutionsServiceAsync institutionsService = GWT.create(InstitutionsService.class);
	private final InstitutionsAdminstrationPanel superPanel;
	private final UserDTO user;
	private ArrayList<RegistrarDTO> registrars = new ArrayList<RegistrarDTO>(0);
	private ArrayList<ArchiverDTO> archivers = new ArrayList<ArchiverDTO>(0);

	public InstitutionListPanel(InstitutionsAdminstrationPanel superPanel, UserDTO user) {
		this.superPanel = superPanel;
		this.user = user;
	}

	public void onLoad() {
		loadRegistrars();
		loadArchivers();
		reload();
	}

	void loadRegistrars() {
		institutionsService.getAllRegistrars(new AsyncCallback<ArrayList<RegistrarDTO>>() {
			public void onSuccess(ArrayList<RegistrarDTO> result) {
				registrars = result;
				reload();
			}

			public void onFailure(Throwable caught) {
				Window.alert(constants.serverError() + ": " + caught.getMessage());
			}
		});
	}

	void loadArchivers() {
		institutionsService.getAllArchivers(new AsyncCallback<ArrayList<ArchiverDTO>>() {

			@Override
			public void onSuccess(ArrayList<ArchiverDTO> result) {
				archivers = result;
				reload();
			}

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(constants.serverError() + ": " + caught.getMessage());

			}
		});
	}

	public void reload() {
		clear();
		add(registrarsPanel());
		add(archiversPanel());
	}

	private Panel registrarsPanel() {
		VerticalPanel result = new VerticalPanel();
		result.setStyleName(css.block());
		result.add(registrarsHeading());
		result.add(registrarsGrid());
		if (user.isSuperAdmin()) {
			result.add(addRegistrarButton());
		}
		return result;
	}

	private Panel archiversPanel() {
		VerticalPanel result = new VerticalPanel();
		result.setStyleName(css.block());
		result.add(archiversHeading());
		result.add(archiversGrid());
		if (user.isSuperAdmin()) {
			result.add(addArchiverButton());
		}
		return result;
	}

	private Widget registrarsHeading() {
		Label label = new Label(constants.registrarList());
		label.addStyleName(css.listHeading());
		return label;
	}

	private Grid registrarsGrid() {
		Grid result = new Grid(registrars.size(), registrarGridColumns());
		for (int i = 0; i < registrars.size(); i++) {
			RegistrarDTO registrar = registrars.get(i);
			Label name = new Label(registrar.getName());
			result.setWidget(i, 0, name);
			Button detailsButton = registrarDetailsButton(registrar);
			result.setWidget(i, 1, detailsButton);
			if (user.isSuperAdmin()) {
				Button deleteButton = registrarDeleteButton(registrar);
				result.setWidget(i, 2, deleteButton);
			}
		}
		return result;
	}

	private Button registrarDeleteButton(final RegistrarDTO registrar) {
		return new Button(constants.delete(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (Window.confirm(messages.confirmDeleteRegistrar(registrar.getName()))) {
					institutionsService.deleteRegistrar(registrar, new AsyncCallback<Void>() {

						@Override
						public void onSuccess(Void result) {
							registrars.remove(registrar);
							reload();
						}

						@Override
						public void onFailure(Throwable caught) {
							Window.alert(messages.registrarCannotBeDeleted(registrar.getName()) + ": " + caught.getMessage());
						}
					});
				}
			}
		});
	}

	private Button registrarDetailsButton(final RegistrarDTO registrar) {
		return new Button(constants.details(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				superPanel.showRegistrarDetails(registrar);
			}
		});
	}

	private int registrarGridColumns() {
		if (user.isSuperAdmin()) {
			return 3;
		} else if (user.isLoggedUser()) {
			return 2;
		} else {
			return 2;
		}
	}

	private Button addRegistrarButton() {
		return new Button(constants.add(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				AddRegistrarDialogBox dialogBox = new AddRegistrarDialogBox(InstitutionListPanel.this);
				dialogBox.show();
			}
		});
	}

	private Widget archiversHeading() {
		Label label = new Label(constants.archiverList());
		label.addStyleName(css.listHeading());
		return label;
	}

	private Grid archiversGrid() {
		Grid result = new Grid(archivers.size(), archiverGridColumns());
		for (int i = 0; i < archivers.size(); i++) {
			ArchiverDTO archiver = archivers.get(i);
			Label name = new Label(archiver.getName());
			result.setWidget(i, 0, name);
			Button detailsButton = archiverDetailsButton(archiver);
			result.setWidget(i, 1, detailsButton);
			if (user.isSuperAdmin()) {
				result.setWidget(i, 2, archiverEditButton(archiver));
				result.setWidget(i, 3, archiverDeleteButton(archiver));
			}
		}
		return result;
	}

	private Button archiverEditButton(final ArchiverDTO archiver) {
		return new Button(constants.edit(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				new EditArchiverDialogBox(InstitutionListPanel.this, archiver).show();
			}
		});
	}

	private Button archiverDeleteButton(final ArchiverDTO archiver) {
		Button result = new Button(constants.delete());
		result.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (Window.confirm(messages.confirmDeleteArchiver(archiver.getName()))) {
					institutionsService.deleteArchiver(archiver, new AsyncCallback<Void>() {

						@Override
						public void onSuccess(Void result) {
							archivers.remove(archiver);
							reload();
						}

						@Override
						public void onFailure(Throwable caught) {
							Window.alert(messages.archiverCannotBeDeleted(archiver.getName()) + ": " + caught.getMessage());
						}
					});
				}
			}
		});
		return result;
	}

	private Button archiverDetailsButton(final ArchiverDTO archiver) {
		Button result = new Button(constants.details());
		result.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ArchiverDetailsDialogBox dialogBox = new ArchiverDetailsDialogBox(archiver);
				dialogBox.show();
				dialogBox.center();
			}
		});
		return result;
	}

	private int archiverGridColumns() {
		if (user.isSuperAdmin()) {
			return 4;
		} else if (user.isLoggedUser()) {
			return 2;
		} else {
			return 2;
		}
	}

	private Button addArchiverButton() {
		return new Button(constants.add(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				new AddArchiverDialogBox(InstitutionListPanel.this).show();
			}
		});
	}

	public UserDTO getUser() {
		return user;
	}
}
