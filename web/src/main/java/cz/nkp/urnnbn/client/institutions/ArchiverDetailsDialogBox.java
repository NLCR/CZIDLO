package cz.nkp.urnnbn.client.institutions;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import cz.nkp.urnnbn.client.AbstractDialogBox;
import cz.nkp.urnnbn.shared.dto.ArchiverDTO;

public class ArchiverDetailsDialogBox extends AbstractDialogBox {

	private final ArchiverDTO archiver;
	
	public ArchiverDetailsDialogBox(ArchiverDTO archiver) {
		this.archiver = archiver;
		String title = constants.archiver() + " - " + constants.details();
		setTitle(title);
		setText(title);
		setAnimationEnabled(true);
		setWidget(contentPanel());
	}

	private Widget contentPanel() {
		VerticalPanel panel = new VerticalPanel();
		Grid grid = new Grid(determineRows(), 2);
		grid.setWidget(0, 0, new Label(constants.title() + ':'));
		grid.setWidget(0, 1, new Label(archiver.getName()));
		grid.setWidget(1, 0, new Label(constants.id() + ':'));
		grid.setWidget(1, 1, new Label(archiver.getId().toString()));
		grid.setWidget(2, 0, new Label(constants.description() + ':'));
		grid.setWidget(2, 1, new Label(archiver.getDescription()));
		//grid.setWidget(3, 0, new Label(constants.archives() + ':'));
		// TODO: dodat opravdový počet dokumentů
		//grid.setWidget(3, 1, new Label("0 dokumentů"));
		if (archiver.getCreated() != null) {
			grid.setWidget(3, 0, new Label(constants.created() + ':'));
			grid.setWidget(3, 1, new Label(archiver.getCreated()));
			if (archiver.getModified() != null && !archiver.getModified().equals(archiver.getCreated())) {
				grid.setWidget(4, 0, new Label(constants.modified() + ':'));
				grid.setWidget(4, 1, new Label(archiver.getModified()));
			}
		}
		panel.add(grid);
		panel.add(closeButton());
		return panel;
	}

	private int determineRows() {
		int result = 3;
		if (archiver.getCreated() != null) {
			result++;
			if (archiver.getModified() != null && !archiver.getModified().equals(archiver.getCreated())) {
				result++;
			}
		}
		return result;
	}

	private Button closeButton() {
		return new Button(constants.close(), new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				ArchiverDetailsDialogBox.this.hide();
			}
		});
	}
}
