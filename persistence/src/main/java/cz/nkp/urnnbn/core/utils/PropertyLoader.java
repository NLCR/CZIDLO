/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author Martin Řehánek
 */
public class PropertyLoader {

    private Properties properties;

    public PropertyLoader(Properties properties) {
        this.properties = properties;
    }

    public PropertyLoader(String propertiesFileName) throws IOException {
        this(new File(propertiesFileName));
    }

    public PropertyLoader(File propertiesFile) throws IOException {
        properties = new Properties();
        properties.load(new FileReader(propertiesFile));
    }

    public PropertyLoader(InputStream inputStream) throws IOException {
        properties = new Properties();
        properties.load(inputStream);
    }

    public String loadString(String key) {
        String result = loadStringOrNull(key);
        if (result == null) {
            throw new IllegalArgumentException("Cannot load property '" + key + "'");
        }
        return result;
    }

    public String loadStringOrNull(String key) {
        return properties.getProperty(key);
    }

    public Integer loadIntOrNull(String key) {
        String stringValue = loadStringOrNull(key);
        if (stringValue == null) {
            return null;
        } else {
            return Integer.valueOf(stringValue);
        }
    }

    public Integer loadInt(String key) {
        Integer result = loadIntOrNull(key);
        if (result == null) {
            throw new IllegalArgumentException("Cannot load property '" + key + "'");
        }
        return result;
    }

    /**
     *
     * @param key
     * @param mustExist if true exception is thrown in case that file doesn't exist
     * @return
     */
    public File loadFile(String key, boolean mustExist) {
        String fileName = loadString(key);
        File result = new File(fileName);
        if (mustExist) {
            if (!result.exists()) {
                throw new NullPointerException("File " + result.getAbsolutePath() + " doesn't exist");
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

    public boolean loadBoolean(String key) {
        String strValue = loadString(key);
        return Boolean.parseBoolean(strValue);
    }

    public Properties getProperties() {
        return properties;
    }
}
