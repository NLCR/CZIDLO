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
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import cz.nkp.urnnbn.client.i18n.ConstantsImpl;
import cz.nkp.urnnbn.client.resources.Resources;
import cz.nkp.urnnbn.client.services.AuthService;
import cz.nkp.urnnbn.client.services.AuthServiceAsync;
import cz.nkp.urnnbn.client.services.ConfigurationService;
import cz.nkp.urnnbn.client.services.ConfigurationServiceAsync;
import cz.nkp.urnnbn.client.tabs.TabsPanel;
import cz.nkp.urnnbn.core.Czidlo;
import cz.nkp.urnnbn.shared.ConfigurationData;
import cz.nkp.urnnbn.shared.dto.UserDTO;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Main implements EntryPoint {

    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static final String APP_VERSION = Czidlo.VERSION;
    private final Resources resources = GWT.create(Resources.class);
    private final ConstantsImpl constants = GWT.create(ConstantsImpl.class);
    private final AuthServiceAsync authService = GWT.create(AuthService.class);
    private final ConfigurationServiceAsync configService = GWT.create(ConfigurationService.class);
    private DockLayoutPanel mainPanel;

    public void onModuleLoad() {
        authService.getLoggedUser(new AsyncCallback<UserDTO>() {
            public void onSuccess(final UserDTO user) {
                // HEADER
                Panel headerPanel = headerPanel(user);
                mainPanel.addNorth(headerPanel, 100);
                configService.getConfiguration(new AsyncCallback<ConfigurationData>() {

                    @Override
                    public void onSuccess(ConfigurationData config) {
                        // analytics
                        String gaTrackingCode = config.getGaTrackingCode();
                        if (gaTrackingCode != null) {
                            initAnalytics(config.getGaTrackingCode());
                        }
                        // CONTENT
                        TabsPanel content = new TabsPanel(user, gaTrackingCode != null);
                        mainPanel.add(content);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        logger.severe("Error obtaining configuration: " + caught.getMessage());
                    }
                });
            }

            public void onFailure(Throwable caught) {
                logger.severe("Error obtaining user credentials: " + caught.getMessage());
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

    private native void initAnalytics(String trackingCode)/*-{
                                                          (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
                                                          (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
                                                          m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
                                                          })($wnd,$doc,'script','//www.google-analytics.com/analytics.js','ga');
                                                          $wnd.ga('create', trackingCode, 'auto');
                                                          //ga('send', 'pageview');
                                                          console.log("google analytics script initialized");
                                                          }-*/;

    private Panel headerPanel(UserDTO user) {
        DockLayoutPanel headerPanel = new DockLayoutPanel(Unit.PX);
        headerPanel.setWidth("100%");
        headerPanel.addWest(leftHeaderPanel(), 1000);
        headerPanel.addEast(new UserPanel(user), 200);
        return headerPanel;
    }

    private Widget leftHeaderPanel() {
        VerticalPanel result = new VerticalPanel();
        HorizontalPanel firstRowPanel = new HorizontalPanel();
        firstRowPanel.add(applicationName());
        firstRowPanel.add(applicationDescription());
        result.add(firstRowPanel);
        result.add(applicationVersion());
        result.addStyleName(resources.MainCss().leftHeader());
        return result;
    }

    private Widget applicationName() {
        Widget name = new HTML("CZIDLO");
        name.addStyleName(resources.MainCss().appName());
        return name;
    }

    private Widget applicationDescription() {
        Widget description = new HTML("(CZech IDentification and LOcalization tool)");
        description.addStyleName(resources.MainCss().appDescription());
        return description;
    }

    private Widget applicationVersion() {
        Widget version = new HTML(constants.version() + " " + APP_VERSION);
        version.addStyleName(resources.MainCss().appVersion());
        return version;
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
