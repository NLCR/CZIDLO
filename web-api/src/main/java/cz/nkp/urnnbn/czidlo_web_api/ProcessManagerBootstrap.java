package cz.nkp.urnnbn.czidlo_web_api;

import cz.nkp.urnnbn.czidlo_web_api.api.processes.process_manager.ProcessManager;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import cz.nkp.urnnbn.czidlo_web_api.api.processes.process_manager.ProcessManagerImpl;

import java.util.logging.Logger;


@WebListener
public class ProcessManagerBootstrap implements ServletContextListener {

    public static final String ATTR = ProcessManagerImpl.class.getName();
    private static final Logger logger = Logger.getLogger(ProcessManagerBootstrap.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Initializing Process Manager...");
        //ProcessManager pm = new ProcessManagerMockInMemory();
        ProcessManager pm = new ProcessManagerImpl();
        sce.getServletContext().setAttribute(ATTR, pm);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Shutting down Process Manager...");

        Object o = sce.getServletContext().getAttribute(ProcessManagerBootstrap.ATTR);
        if (o instanceof ProcessManager pm) {
            pm.shutdown(true);
            sce.getServletContext().removeAttribute(ProcessManagerBootstrap.ATTR);
        } else {
            logger.warning("ProcessManager not found in ServletContext; nothing to shutdown.");
        }
    }
}


