package cz.nkp.urnnbn.client.search;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

import cz.nkp.urnnbn.shared.dto.ie.IntelectualEntityDTO;

public class ResultsPage extends ScrollPanel {

	private final String caption;
	private final ArrayList<Long> intEntIdentifiers;
	private ArrayList<IntelectualEntityDTO> entities;

	public ResultsPage(String caption, ArrayList<Long> intEntIdentifiers) {
		super();
		this.caption = caption;
		this.intEntIdentifiers = intEntIdentifiers;
	//	add(new Label(caption));
	}
	
	@Override
	protected void onLoad() {
		// TODO Auto-generated method stub
		super.onLoad();
		//VerticalPanel panel = new VerticalPanel();
		add(new Label(caption));
		//panel.ap
		//append()
		//add(new HTML(caption));
		
	}

}
