/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.utils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * Class contains methods for loading and parsing values from properties object (file) supplied in constructor. All the loadSomething methods load
 * (and potentially parse) property by key. If value of this property is missing, the exception is thrown. On the other hand the loadSomethingOrNull
 * alternatives return null if property value is missing.
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

    /**
     * 
     * @param key
     *            string identifying the property
     * @return value of property identified by key
     * @throws IllegalArgumentException
     *             if property for given key is missing
     */
    public String loadString(String key) {
        String result = loadStringOrNull(key);
        if (result == null) {
            throw new IllegalArgumentException("Cannot load property '" + key + "'");
        }
        return result;
    }

    public URL loadUrl(String key) throws MalformedURLException {
        return new URL(loadString(key));
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

    public Long loadLongOrNull(String key) {
        String stringValue = loadStringOrNull(key);
        if (stringValue == null) {
            return null;
        } else {
            return Long.valueOf(stringValue);
        }
    }

    public Long loadLong(String key) {
        Long result = loadLongOrNull(key);
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

    public boolean loadBooleanFalseIfNullOrEmpty(String key) {
        if (key == null || key.isEmpty()) {
            return false;
        } else {
            return loadBoolean(key);
        }
    }

    public Properties getProperties() {
        return properties;
    }
}
