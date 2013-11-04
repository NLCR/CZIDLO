package cz.nkp.urnnbn.client;

import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import cz.nkp.urnnbn.client.resources.Resources;
import cz.nkp.urnnbn.client.services.AuthService;
import cz.nkp.urnnbn.client.services.AuthServiceAsync;
import cz.nkp.urnnbn.client.tabs.TabsPanel;
import cz.nkp.urnnbn.shared.dto.UserDTO;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Main implements EntryPoint {

	private static final Logger logger = Logger.getLogger(Main.class.getName());
	private Resources resources = GWT.create(Resources.class);
	private DockLayoutPanel mainPanel;

	public void onModuleLoad() {
		AuthServiceAsync service = GWT.create(AuthService.class);
		service.getLoggedUser(new AsyncCallback<UserDTO>() {
			public void onFailure(Throwable caught) {
				logger.severe("Error obtaining user credentials: " + caught.getMessage());
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
		// headerPanel.add(headerTitlePanel());
		headerPanel.add(headerTitle());
		headerPanel.add(headerSubtitle());

		// space
		Label emptyLabel1 = new Label("");
		emptyLabel1.setWidth("100px");
		headerPanel.add(emptyLabel1);

		// logo
		// Image booksImg = new Image("img/logo_books.png");
		// headerPanel.add(booksImg);
		// space
		Label emptyLabel2 = new Label("");
		headerPanel.add(emptyLabel2);
		headerPanel.setCellWidth(emptyLabel2, "100%");
		// user panel
		headerPanel.add(new UserPanel(user));

		return headerPanel;
	}

	private Widget headerTitle() {
		Widget headerSubtitle = new HTML("CZIDLO");
		headerSubtitle.addStyleName(resources.MainCss().headerTitle());
		return headerSubtitle;
	}

	private Widget headerSubtitle() {
		Widget headerSubtitle = new HTML("(CZech IDentification and LOcalization tool)");
		headerSubtitle.addStyleName(resources.MainCss().headerSubtitle());
		return headerSubtitle;
	}

	private HorizontalPanel footerPanel() {
		HorizontalPanel footerPanel = new HorizontalPanel();
		footerPanel.addStyleName(resources.MainCss().footerPanel());
		footerPanel.setWidth("100%");
		footerPanel.setHeight("100%");
		footerPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		footerPanel.setBorderWidth(0);

		// // logo NDK
		// Image logoNDK = new Image("img/logo_ndk.jpg");
		// footerPanel.add(logoNDK);

		// logo NKP
		Image logoNKP = new Image("img/logo_nkp.jpg");
		footerPanel.add(logoNKP);

		// // logo MZK
		// Image logoMZK = new Image("img/logo_mzk.jpg");
		// footerPanel.add(logoMZK);

		// // logo IOP
		// Image logoIOP = new Image("img/logo_iop.jpg");
		// footerPanel.add(logoIOP);

		// logo MK
		Image logoMK = new Image("img/logo_mk.jpg");
		footerPanel.add(logoMK);

		// Hyperlink link = new Hyperlink();
		// link.getElement().getFirstChild().appendChild(logoMK.getElement());

		return footerPanel;
	}
}