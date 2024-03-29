package cz.nkp.urnnbn.api.v4.json;

import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import cz.nkp.urnnbn.core.dto.DigitalLibrary;

public class DigitalLibrariesBuilderJson extends JsonBuilder {

    private final List<DigitalLibrary> libraryList;

    public DigitalLibrariesBuilderJson(List<DigitalLibrary> libraryList) {
        this.libraryList = libraryList;
    }

    @Override
    protected String getName() {
        return "digitalLibraries";
    }

    @Override
    public JSONArray build() {
        JSONArray array = new JSONArray();
        for (DigitalLibrary library : libraryList) {
            JSONObject libEl = new DigitalLibraryBuilderJson(library, null).build();
            array.put(libEl);
        }
        return array;
    }

}
