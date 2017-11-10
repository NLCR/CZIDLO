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
import java.util.IllegalFormatException;
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
     * @param key string identifying the property
     * @return value of property identified by key
     * @throws IllegalArgumentException if property for given key is missing
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
        String value = properties.getProperty(key);
        if (value != null && value.isEmpty()) {
            return null;
        } else {
            return value;
        }
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

    /**
     * @param key
     * @return property boolean value
     * @throws IllegalArgumentException if property is missing or containes invalid value
     */
    public boolean loadBoolean(String key) {
        String strValue = loadString(key);
        return parseBoolean(strValue);
    }

    /**
     * @param key
     * @param defaultValue
     * @return property boolean value or defaultValue if property is missing or empty
     */
    public boolean loadBoolean(String key, boolean defaultValue) {
        String strValue = loadStringOrNull(key);
        if (strValue == null || strValue.isEmpty()) {
            return defaultValue;
        } else {
            return parseBoolean(strValue);
        }
    }

    private boolean parseBoolean(String strValue) {
        if (strValue == null || strValue.isEmpty()) {
            throw new IllegalArgumentException();
        } else {
            String strLowerCase = strValue.toLowerCase();
            if (strLowerCase.equals("true") || strLowerCase.equals("yes")) {
                return true;
            } else if (strLowerCase.equals("false") || strLowerCase.equals("no")) {
                return false;
            } else {
                throw new IllegalArgumentException(String.format("'%s' is not valid boolean value"));
            }
        }
    }

    public Properties getProperties() {
        return properties;
    }

}
