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
				result = sortByOrder(result);
				if (!user.isSuperAdmin()) {
					result = removeHidden(result);
				}
				registrars = result;
				for (RegistrarDTO reg : registrars) {
					System.out.println("Order "+reg.getOrder());
				}
				reload();
			}

			public void onFailure(Throwable caught) {
				Window.alert(constants.serverError() + ": " + caught.getMessage());
			}
		});
	}

	private <T extends ArchiverDTO> ArrayList<T> removeHidden(ArrayList<T> list) {
		ArrayList<T> result = new ArrayList<T>();
		for (T item : list) {
			if (!item.isHidden()) {
				result.add(item);
			}
		}
		return result;
	}
	
	private <T extends ArchiverDTO> ArrayList<T> sortByOrder(ArrayList<T> result) {
		Collections.sort(result, new Comparator<ArchiverDTO>() {
			public int compare(ArchiverDTO o1, ArchiverDTO o2) {
				Long first = o1.getOrder()!= null ? o1.getOrder() : 0;
				Long second = o2.getOrder()!= null ? o2.getOrder() : 0;
				return first.compareTo(second);
			}
		});
		return result;
	}
	
	void loadArchivers() {
		institutionsService.getAllArchivers(new AsyncCallback<ArrayList<ArchiverDTO>>() {

			@Override
			public void onSuccess(ArrayList<ArchiverDTO> result) {
				result = sortByOrder(result);
				if (!user.isSuperAdmin()) {
					result = removeHidden(result);
				}
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
	
	private interface GridHelper<T extends ArchiverDTO> {
		
		public Button createDetailsButton(T item);
		public Button createEditButton(T item);
		public Button createDeleteButton(T item);
		public void update(List<T> items, AsyncCallback<Void> callBack);
	}
	
	private class ArchiversGridHelper implements GridHelper<ArchiverDTO> {
		
		public Button createDetailsButton(ArchiverDTO item) {
			return archiverDetailsButton(item);
		}
		
		public Button createEditButton(ArchiverDTO item) {
			return archiverEditButton(item);
		}
		
		public Button createDeleteButton(ArchiverDTO item) {
			return archiverDeleteButton(item);
		}
		
		public void update(List<ArchiverDTO> items, AsyncCallback<Void> callBack) {
			institutionsService.updateArchivers(items, callBack);
		}
	}
	
	private class RegistrarsGridHelper implements GridHelper<RegistrarDTO> {

		public Button createDetailsButton(RegistrarDTO item) {
			return registrarDetailsButton(item);
		}

		public Button createEditButton(RegistrarDTO item) {
			return null;
		}

		public Button createDeleteButton(RegistrarDTO item) {
			return registrarDeleteButton(item);
		}

		public void update(List<RegistrarDTO> items,
				AsyncCallback<Void> callBack) {
			institutionsService.updateRegistrars(items, callBack);
		}
		
	}
	
	private <T extends ArchiverDTO> AbsolutePanel getGrid(List<T> list,
			final GridHelper<T> gridHelper) {
		final AbsolutePanel panel = new AbsolutePanel();
		final FlexTableRowDragController tableRowDragController = new FlexTableRowDragController(
				panel);
		final FlexTable table = new FlexTable();
		for (int row = 0; row < list.size(); row++) {
			T archiver = list.get(row);
			CustomHTML<T> handle = new CustomHTML<T>(archiver.getName(),
					archiver);
			table.setWidget(row, 0, handle);
			if (user.isSuperAdmin()) {
				tableRowDragController.makeDraggable(handle);
			}
			Button detailsButton = gridHelper.createDetailsButton(archiver);
			table.setWidget(row, 1, detailsButton);
			if (user.isSuperAdmin()) {
				int pos = 2;
				Button edit = gridHelper.createEditButton(archiver);
				if (edit != null) {
					table.setWidget(row, pos, edit);
					pos++;
				}
				Button delete = gridHelper.createDeleteButton(archiver);
				if (delete != null) {
					table.setWidget(row, pos, delete);
					pos++;
				}
			}
		}
		panel.add(table);
		if (user.isSuperAdmin()) {
			final Button saveButton = new Button("save");
			saveButton.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent arg0) {
					List<T> items = new ArrayList<T>();
					for (int row = 0; row < table.getRowCount(); row++) {
						CustomHTML<T> widget = (CustomHTML<T>) table.getWidget(
								row, 0);
						T archiver = widget.getObject();
						archiver.setOrder(Long.valueOf(row + 1));
						items.add(archiver);
					}
					AsyncCallback<Void> callBack = new AsyncCallback<Void>() {

						public void onFailure(Throwable caught) {
							saveButton.setText("save");
							Window.alert(constants.serverError() + ": "
									+ caught.getMessage());
						}

						public void onSuccess(Void arg0) {
							saveButton.setText("save");
						}

					};
					gridHelper.update(items, callBack);
				}
			});
			panel.add(saveButton);
			FlexTableRowDropController flexTableRowDropController = new FlexTableRowDropController(
					table);
			tableRowDragController
					.registerDropController(flexTableRowDropController);
		}
		return panel;
	}

	private AbsolutePanel archiversGrid() {
		return getGrid(archivers, new ArchiversGridHelper());
	}
	
	private AbsolutePanel registrarsGrid() {
		return getGrid(registrars, new RegistrarsGridHelper());
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
