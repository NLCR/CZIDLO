package cz.nkp.urnnbn.czidlo_web_api;

import cz.nkp.urnnbn.processmanager.control.ProcessManagerImpl;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.logging.Logger;

@WebListener
public class ProcessManagerShutdownListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(ProcessManagerShutdownListener.class.getName());

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("Shutting down Process Manager...");
        ProcessManagerImpl.shutdownInstance();
    }
}
