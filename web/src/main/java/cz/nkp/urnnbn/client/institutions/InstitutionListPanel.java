package cz.nkp.urnnbn.client.institutions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import cz.nkp.urnnbn.client.dnd.FlexTableRowDragController;
import cz.nkp.urnnbn.client.dnd.FlexTableRowDropController;
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
	
	private static class CustomHTML<T> extends HTML {
		
		private T object;
		
		public CustomHTML(String html, T object) {
			super(html);
			this.object = object;
		}

		public T getObject() {
			return object;
		}

		public void setObject(T object) {
			this.object = object;
		}
		
	}

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
				registrars = sortByLastModificationDateDownwards(result);
				reload();
			}

			public void onFailure(Throwable caught) {
				Window.alert(constants.serverError() + ": " + caught.getMessage());
			}
		});
	}

	
	private ArrayList<RegistrarDTO> sortByLastModificationDateDownwards(ArrayList<RegistrarDTO> result) {
		Collections.sort(result, new Comparator<RegistrarDTO>() {

			@Override
			public int compare(RegistrarDTO o1, RegistrarDTO o2) {
				Long first = o1.getModifiedMillis() != null ? o1.getModifiedMillis() : o1.getCreatedMillis();
				Long second = o2.getModifiedMillis() != null ? o2.getModifiedMillis() : o2.getCreatedMillis();
				return first.compareTo(second) * (-1);
			}
		});
		return result;
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

	private AbsolutePanel archiversGrid() {
		AbsolutePanel panel = new AbsolutePanel();
		FlexTableRowDragController tableRowDragController = new FlexTableRowDragController(panel);
		final FlexTable table = new FlexTable();
		for (int row = 0; row < archivers.size(); row++) {
			ArchiverDTO archiver = archivers.get(row);
			CustomHTML<ArchiverDTO> handle = new CustomHTML<ArchiverDTO>(archiver.getName(), archiver);
			table.setWidget(row, 0, handle);
			if (user.isSuperAdmin()) {
				tableRowDragController.makeDraggable(handle);
			}
			Button detailsButton = archiverDetailsButton(archiver);
			table.setWidget(row, 1, detailsButton);
			if (user.isSuperAdmin()) {
				table.setWidget(row, 2, archiverEditButton(archiver));
				table.setWidget(row, 3, archiverDeleteButton(archiver));
			}
		}
		panel.add(table);
		if (user.isSuperAdmin()) {
			final Button saveButton = new Button("save");
			saveButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent arg0) {
					List<ArchiverDTO> archivers = new ArrayList<ArchiverDTO>();
					for (int row = 0; row < table.getRowCount(); row++) {
						CustomHTML<ArchiverDTO> widget = (CustomHTML<ArchiverDTO>) table
								.getWidget(row, 0);
						ArchiverDTO archiver = widget.getObject();
						archiver.setOrder(Long.valueOf(row));
						archivers.add(archiver);
					}
					saveButton.setText("saving");
					institutionsService.updateArchivers(archivers,
							new AsyncCallback<Void>() {

								public void onFailure(Throwable caught) {
									saveButton.setText("save");
									Window.alert(constants.serverError() + ": "
											+ caught.getMessage());
								}

								public void onSuccess(Void arg0) {
									saveButton.setText("save");
								}

							});
				}
			});
			panel.add(saveButton);
			FlexTableRowDropController flexTableRowDropController = new FlexTableRowDropController(table);
			tableRowDragController.registerDropController(flexTableRowDropController);
		}
		return panel;
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
