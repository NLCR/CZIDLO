package cz.nkp.urnnbn.czidlo_web_api;

import cz.nkp.urnnbn.core.AdminLogger;
import cz.nkp.urnnbn.services.Services;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.logging.Logger;

@WebListener
public class ServicesBootstrap implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(ServicesBootstrap.class.getName());

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Context destroyed, shutting down admin logger and services...");
        try {
            AdminLogger.shutdown();
        } finally {
            Services.shutdownForCurrentClassLoader();
        }
    }
}
