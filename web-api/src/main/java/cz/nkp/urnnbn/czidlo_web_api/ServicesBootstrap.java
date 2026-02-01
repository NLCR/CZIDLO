package cz.nkp.urnnbn.czidlo_web_api;

import cz.nkp.urnnbn.services.Services;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class ServicesBootstrap implements ServletContextListener {
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Services.shutdownForCurrentClassLoader();
    }
}
