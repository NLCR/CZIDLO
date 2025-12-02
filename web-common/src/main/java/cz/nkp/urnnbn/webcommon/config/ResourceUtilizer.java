/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.webcommon.config;

import java.io.File;
import java.io.FileInputStream;
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
        // 1) Try external file via system property
        String sysPropPath = System.getProperty("czidlo.config");
        if (sysPropPath != null) {
            File f = new File(sysPropPath);
            if (f.isFile() && f.canRead()) {
                try {
                    logger.info("Using external config from system property: " + sysPropPath);
                    return new FileInputStream(f);
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Cannot read file from system property: " + sysPropPath, e);
                }
            }
        }

        // 2) Try $HOME/.czidlo/<resourceName>
        File homeFile = new File(System.getProperty("user.home") + "/.czidlo/" + resourceName);
        if (homeFile.isFile() && homeFile.canRead()) {
            try {
                logger.info("Using external config: " + homeFile.getAbsolutePath());
                return new FileInputStream(homeFile);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Cannot read file: " + homeFile, e);
            }
        }

        // 3) Try /etc/czidlo/<resourceName>
        File etcFile = new File("/etc/czidlo/" + resourceName);
        if (etcFile.isFile() && etcFile.canRead()) {
            try {
                logger.info("Using external config: " + etcFile.getAbsolutePath());
                return new FileInputStream(etcFile);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Cannot read file: " + etcFile, e);
            }
        }

        // 4) Fallback → classpath
        InputStream in = getClass().getClassLoader().getResourceAsStream(resourceName);
        if (in == null) {
            throw new RuntimeException("Cannot find resource " + resourceName
                    + " externally nor on classpath");
        } else {
            logger.log(Level.INFO, "Using default classpath resource {0}", resourceName);
            return in;
        }
    }
}
