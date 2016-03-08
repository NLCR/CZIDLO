package cz.nkp.urnnbn.api.v4.json;

import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

public class RegistrarsBuilderJson extends JsonBuilder {

    private final List<RegistrarBuilderJson> registrarBuilders;

    public RegistrarsBuilderJson(List<RegistrarBuilderJson> registrarBuilders) {
        this.registrarBuilders = registrarBuilders;
    }

    @Override
    protected String getName() {
        return "registrars";
    }

    @Override
    protected Object build() {
        JSONArray array = new JSONArray();
        for (RegistrarBuilderJson builder : registrarBuilders) {
            JSONObject built = builder.build();
            if (built != null) {
                array.put(built);
            }
        }
        return array;
    }

}
