package cz.nkp.urnnbn.client.institutions;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

import cz.nkp.urnnbn.client.AbstractDialogBox;
import cz.nkp.urnnbn.shared.dto.DigitalLibraryDTO;

public class DigitalLibraryDetailsDialogBox extends AbstractDialogBox {

	private final DigitalLibraryDTO library;

	public DigitalLibraryDetailsDialogBox(DigitalLibraryDTO library) {
		this.library = library;
		String title = constants.digitalLibrary() + " - " + constants.details();
		setTitle(title);
		setText(title);
		setAnimationEnabled(true);
		setWidget(contentPanel());
		center();
	}

	private Panel contentPanel() {
		VerticalPanel panel = new VerticalPanel();
		Grid grid = new Grid(determineRows(), 2);
		grid.setWidget(0, 0, new Label(constants.title() + ':'));
		grid.setWidget(0, 1, new Label(library.getName()));
		grid.setWidget(1, 0, new Label(constants.description() + ':'));
		grid.setWidget(1, 1, new Label(library.getDescription()));
		grid.setWidget(2, 0, new Label(constants.url() + ':'));
		grid.setWidget(2, 1, new Label(library.getUrl()));
		grid.setWidget(3, 0, new Label(constants.contains() + ':'));
		// TODO: dodat opravdový počet dokumentů
		grid.setWidget(3, 1, new Label("0 dokumentů"));
		if (library.getCreated() != null) {
			grid.setWidget(4, 0, new Label(constants.created() + ':'));
			grid.setWidget(4, 1, new Label(library.getCreated()));
			if (library.getModified() != null && !library.getModified().equals(library.getCreated())) {
				grid.setWidget(5, 0, new Label(constants.modified() + ':'));
				grid.setWidget(5, 1, new Label(library.getModified()));
			}
		}
		panel.add(grid);
		panel.add(closeButton());
		return panel;
	}

	private int determineRows() {
		int result = 4;
		if (library.getCreated() != null) {
			result++;
			if (library.getModified() != null && !library.getModified().equals(library.getCreated())) {
				result++;
			}
		}
		return result;
	}

	private Button closeButton() {
		return new Button(constants.close(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				DigitalLibraryDetailsDialogBox.this.hide();
			}
		});
	}
}
