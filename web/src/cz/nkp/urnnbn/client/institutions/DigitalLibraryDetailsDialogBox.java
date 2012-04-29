package cz.nkp.urnnbn.client.institutions;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
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
		int index = 0;
		grid.setWidget(index, 0, new Label(constants.title() + ':'));
		grid.setWidget(index++, 1, new Label(library.getName()));
		grid.setWidget(index, 0, new Label(constants.id() + ':'));
		grid.setWidget(index++, 1, new Label(library.getId().toString()));
		grid.setWidget(index, 0, new Label(constants.description() + ':'));
		grid.setWidget(index++, 1, new Label(library.getDescription()));
		if (library.getUrl() != null) {
			grid.setWidget(index, 0, new Label(constants.url() + ':'));
			grid.setWidget(index++, 1, new HTML(buildUrlOrNull(library.getUrl())));
		}
		grid.setWidget(index, 0, new Label(constants.contains() + ':'));
		// TODO: dodat opravdový počet dokumentů
		grid.setWidget(index++, 1, new Label("0 dokumentů"));
		if (library.getCreated() != null) {
			grid.setWidget(index, 0, new Label(constants.created() + ':'));
			grid.setWidget(index++, 1, new Label(library.getCreated()));
			if (library.getModified() != null && !library.getModified().equals(library.getCreated())) {
				grid.setWidget(index, 0, new Label(constants.modified() + ':'));
				grid.setWidget(index++, 1, new Label(library.getModified()));
			}
		}
		panel.add(grid);
		panel.add(closeButton());
		return panel;
	}

	private String buildUrlOrNull(String target) {
		if (target != null) {
			return "<a href=\"" + target + "\">" + target + "</a>";
		} else {
			return null;
		}
	}

	private int determineRows() {
		int result = 4;
		if (library.getUrl() != null) {
			result++;
		}
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
