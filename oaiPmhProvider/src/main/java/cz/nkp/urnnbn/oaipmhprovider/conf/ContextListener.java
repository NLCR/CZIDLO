/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.conf;

import cz.nkp.urnnbn.utils.PropertyLoader;
import cz.nkp.urnnbn.webcommon.config.ResourceUtilizer;
import cz.nkp.urnnbn.xml.config.XmlModuleConfiguration;
import java.io.InputStream;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 
 * @author Martin Řehánek
 */
public class ContextListener implements ServletContextListener {

	private static final String WEB_APP_NAME = "oaiPmhProvider";
	private static final Logger logger = Logger.getLogger(ServletContextListener.class.getName());
	private static final String PROPERTIES_FILE = "provider.properties";
	private static final String CZIDLO_TO_OAIDC_XSLT = "resolverToOaiDc.xsl";

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		loadPropertiesFile();
		loadCzidloToOaidcXslt();
	}

	private void loadPropertiesFile() {
		new ResourceUtilizer(logger) {
			@Override
			public void processResource(InputStream in) throws Exception {
				PropertyLoader loader = new PropertyLoader(in);
				OaiPmhConfiguration.instanceOf().initialize(WEB_APP_NAME, loader);
				XmlModuleConfiguration.instanceOf().initialize(loader);
			}
		}.run(PROPERTIES_FILE);
	}

	private void loadCzidloToOaidcXslt() {
		new ResourceUtilizer(logger) {
			@Override
			public void processResource(InputStream in) throws Exception {
				OaiPmhConfiguration.instanceOf().initCzidloToOaidcTemplate(in);
			}
		}.run(CZIDLO_TO_OAIDC_XSLT);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// nothing
	}
}
