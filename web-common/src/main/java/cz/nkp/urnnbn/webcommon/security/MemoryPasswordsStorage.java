package cz.nkp.urnnbn.webcommon.security;

import java.util.HashMap;
import java.util.Map;

/**
 * This singleton stores passwords of authenticated users in plain form. Passwords are then obtained by web interface for scheduling processes.
 * 
 * @author Martin Řehánek
 * 
 */
public class MemoryPasswordsStorage {

    private static MemoryPasswordsStorage instance = new MemoryPasswordsStorage();
    private Map<String, String> passwordMap = new HashMap<>();

    public static MemoryPasswordsStorage instanceOf() {
        return instance;
    }

    public void storePassword(String login, String password) {
        passwordMap.put(login, password);
    }

    public String getPassword(String login) {
        return passwordMap.get(login);
    }

}
