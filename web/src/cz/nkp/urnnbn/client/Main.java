package cz.nkp.urnnbn.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.resources.Resources;
import cz.nkp.urnnbn.client.services.AuthService;
import cz.nkp.urnnbn.client.services.AuthServiceAsync;
import cz.nkp.urnnbn.client.tabs.TabsPanel;
import cz.nkp.urnnbn.shared.dto.UserDTO;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Main implements EntryPoint {

	private ConstantsImpl constants = GWT.create(ConstantsImpl.class);
	private Resources resources = GWT.create(Resources.class);
	private DockLayoutPanel mainPanel;

	public void onModuleLoad() {
		AuthServiceAsync service = GWT.create(AuthService.class);
		service.getActiveUser(new AsyncCallback<UserDTO>() {
			public void onFailure(Throwable caught) {
				// Window.alert("Remote Procedure Call - Failure");
			}

			public void onSuccess(UserDTO user) {
				// HEADER
				HorizontalPanel headerPanel = headerPanel(user);
				mainPanel.addNorth(headerPanel, 100);
				// CONTENT
				TabsPanel content = new TabsPanel(user);
				mainPanel.add(content);
			}
		});
		resources.MainCss().ensureInjected();
		// MAIN PANEL
		mainPanel = new DockLayoutPanel(Unit.PX);
		mainPanel.setSize("450", "350");
		RootLayoutPanel.get().add(mainPanel);
		// FOOTER
		HorizontalPanel footerPanel = footerPanel();
		mainPanel.addSouth(footerPanel, 50);
	}

	private HorizontalPanel headerPanel(UserDTO user) {
		HorizontalPanel headerPanel = new HorizontalPanel();

		headerPanel.addStyleName(resources.MainCss().headerPanel());
		headerPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		// title
		headerPanel.add(headerTitlePanel());
		// space
		Label emptyLabel1 = new Label("");
		emptyLabel1.setWidth("100px");
		headerPanel.add(emptyLabel1);

		// logo
		Image booksImg = new Image("img/logo_books.png");
		headerPanel.add(booksImg);
		// space
		Label emptyLabel2 = new Label("");
		headerPanel.add(emptyLabel2);
		headerPanel.setCellWidth(emptyLabel2, "100%");
		// user panel
		headerPanel.add(new UserPanel(user));

		return headerPanel;
	}

	private VerticalPanel headerTitlePanel() {
		VerticalPanel headerTitlePanel = new VerticalPanel();
		headerTitlePanel.addStyleName(resources.MainCss().headerTitlePanel());
		// title
		// Label headerTitle = new HTML(
		// "<span style=\"color: black;\">URN</span><span style=\"color: red;\">:</span><span style=\"color: black;\">NBN Resolver</span></a>");
		Label headerTitle = new HTML(
		// "<a href=\"http://resolver.nkp.cz\"><span style=\"color: black;text-decoration:none;\">čIDLO</span></a>");
				"<span style=\"color: black;text-decoration:none;\"><i>čIDLO</i></span>");
		headerTitle.addStyleName(resources.MainCss().headerTitle());
		headerTitlePanel.add(headerTitle);
		// subtitle
		Label headerSubtitle = new Label(constants.headerSubtitle());
		headerSubtitle.addStyleName(resources.MainCss().headerSubtitle());
		headerTitlePanel.add(headerSubtitle);
		return headerTitlePanel;
	}

	private HorizontalPanel footerPanel() {
		HorizontalPanel footerPanel = new HorizontalPanel();
		footerPanel.addStyleName(resources.MainCss().footerPanel());
		footerPanel.setWidth("100%");
		footerPanel.setHeight("100%");
		footerPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		footerPanel.setBorderWidth(0);
		
		// logo NDK
		Image logoNDK = new Image("img/logo_ndk.jpg");
		footerPanel.add(logoNDK);
		
		// logo NKP
		Image logoNKP = new Image("img/logo_nkp.jpg");
		footerPanel.add(logoNKP);
		
		// logo MZK
		Image logoMZK = new Image("img/logo_mzk.jpg");
		footerPanel.add(logoMZK);
		
		// logo IOP
		Image logoIOP = new Image("img/logo_iop.jpg");
		footerPanel.add(logoIOP);

		// logo MK
		Image logoMK = new Image("img/logo_mk.jpg");
		footerPanel.add(logoMK);
		
		Hyperlink link = new Hyperlink();
		link.getElement().getFirstChild().appendChild(logoMK.getElement());
				
		return footerPanel;
	}
}
