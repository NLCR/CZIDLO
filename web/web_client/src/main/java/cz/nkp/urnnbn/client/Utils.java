package cz.nkp.urnnbn.client;

import com.google.gwt.user.client.Window;

public class Utils {

    public static void sessionExpirationRedirect() {
        Window.Location.replace(".");
    }

}
