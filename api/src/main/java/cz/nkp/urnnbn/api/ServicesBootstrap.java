package cz.nkp.urnnbn.api;

import cz.nkp.urnnbn.core.AdminLoggerSimple;
import cz.nkp.urnnbn.services.Services;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.logging.Logger;

@WebListener
public class ServicesBootstrap implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(ServicesBootstrap.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        logger.info("Context initialized");
        //nothing to initialize here, both Services and AdminLogger are initialized in cz.nkp.urnnbn.webcommon.config.ApplicationConfiguration
        if (!AdminLoggerSimple.isInitialized()) {
            logger.warning("AdminLogger not initialized yet, won't initialize here as a fallback, because we don't have AdminLogger config (file path) here.");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Context destroyed, shutting down admin logger and services...");
        try {
            AdminLoggerSimple.shutdown();
        } finally {
            Services.shutdownForCurrentClassLoader();
        }
    }
}

