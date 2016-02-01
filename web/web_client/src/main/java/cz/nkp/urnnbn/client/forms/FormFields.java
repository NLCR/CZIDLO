package cz.nkp.urnnbn.client.forms;

import java.util.ArrayList;
import java.util.HashMap;

public class FormFields {

    private HashMap<String, Field> fieldMap = new HashMap<String, Field>();
    private ArrayList<String> keyList = new ArrayList<String>();

    public void addField(String key, Field field) {
        if (fieldMap.containsKey(key)) {
            throw new RuntimeException("key " + key + " already present");
        } else if (key == null) {
            throw new NullPointerException("key");
        } else if ("".equals(key)) {
            throw new IllegalArgumentException("key cannot be empty String");
        } else {
            fieldMap.put(key, field);
            keyList.add(key);
        }
    }

    public int size() {
        return keyList.size();
    }

    public Field getFieldByPosition(int position) {
        return fieldMap.get(keyList.get(position));
    }

    public Field getFieldByKey(String key) {
        return fieldMap.get(key);
    }
}
