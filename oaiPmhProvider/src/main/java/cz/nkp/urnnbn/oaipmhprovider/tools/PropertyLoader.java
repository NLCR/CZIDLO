/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.tools;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Martin Řehánek (rehan at mzk.cz)
 */
public class PropertyLoader {

    private Properties properties;

    public PropertyLoader(Properties properties) {
        this.properties = properties;
    }

    public PropertyLoader(String propetiesFileName) throws IOException {
        File propertiesFile = new File(propetiesFileName);
        properties = new Properties();
        properties.load(new FileReader(propertiesFile));
    }

    public PropertyLoader(InputStream inputStream) throws IOException {
        properties = new Properties();
        properties.load(inputStream);
    }

    public String loadString(String key) {
        String result = properties.getProperty(key);
        if (result == null) {
            throw new IllegalArgumentException("Cannot load property '" + key + "'");
        }
        return result;
    }

    /**
     *
     * @param key
     * @param mustExist
     *            if true exception is thrown in case that file doesn't exist
     * @return
     */
    public File loadFile(String key, boolean mustExist) {
        String fileName = loadString(key);
        File result = new File(fileName);
        if (mustExist) {
            if (!result.exists()) {
                throw new IllegalStateException("File " + result.getAbsolutePath() + " doesn't exist");
            }
        }
        return result;
    }

    public File loadDir(String key) {
        File dir = loadFile(key, true);
        if (!dir.canExecute()) {
            throw new IllegalStateException("Cannot access dir '" + dir.getAbsolutePath() + "'");
        }
        return dir;
    }

    public Properties getProperties() {
        return properties;
    }

    public int loadInt(String key) {
        String stringValue = loadString(key);
        try {
            return Integer.valueOf(stringValue);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid value of property '" + key + "':" + e.getMessage());
        }
    }
}
