/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.webcommon.config;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author Martin Řehánek
 */
abstract public class ResourceUtilizer {

	private final Logger logger;

	public ResourceUtilizer(Logger logger) {
		this.logger = logger;
	}

	public abstract void processResource(InputStream in) throws Exception;

	public final void run(String resourceName) {
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
