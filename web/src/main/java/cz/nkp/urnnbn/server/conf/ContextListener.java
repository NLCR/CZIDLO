package cz.nkp.urnnbn.server.conf;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import cz.nkp.urnnbn.utils.PropertyLoader;

/**
 * 
 * @author Martin Řehánek
 */
public class ContextListener implements ServletContextListener {

	private static final String WEB_APP_NAME = "Web";
	private static final Logger logger = Logger.getLogger(ServletContextListener.class.getName());
	private static final String WEB_PROPERTIES = "web.properties";
	private static final String TAB_INFO_CONTENT = "info.html";
	private static final String TAB_RULES_CONTENT = "rules.html";

	abstract class ResourceUtilizer {

		abstract void processResource(InputStream in) throws Exception;

		final void run(String resourceName) {
			InputStream data = loadResource(resourceName);
			try {
				processResource(data);
			} catch (Exception ex) {
				logger.log(Level.SEVERE, "Error processing resource " + resourceName, ex);
			}
		}

		private InputStream loadResource(String resourceName) {
			InputStream in = getClass().getClassLoader().getResourceAsStream(resourceName);
			if (in == null) {
				throw new RuntimeException("Cannot find resource " + resourceName);
			} else {
				logger.log(Level.INFO, "Found resource {0}", resourceName);
				return in;
			}
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		loadWebProperties();
		loadTabInfoContent();
		loadTabRulesContent();
	}

	private void loadWebProperties() {
		new ResourceUtilizer() {

			@Override
			void processResource(InputStream in) throws Exception {
				WebModuleConfiguration.instanceOf().initialize(WEB_APP_NAME, new PropertyLoader(in));
			}
		}.run(WEB_PROPERTIES);
	}

	private void loadTabInfoContent() {
		new ResourceUtilizer() {

			@Override
			void processResource(InputStream in) throws Exception {
				WebModuleConfiguration.instanceOf().loadInfoTabContent(new BufferedReader(new InputStreamReader(in)));
			}
		}.run(TAB_INFO_CONTENT);

	}

	private void loadTabRulesContent() {
		new ResourceUtilizer() {

			@Override
			void processResource(InputStream in) throws Exception {
				WebModuleConfiguration.instanceOf().loadRulesTabContent(new BufferedReader(new InputStreamReader(in)));
			}
		}.run(TAB_RULES_CONTENT);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// nothing
	}
}
