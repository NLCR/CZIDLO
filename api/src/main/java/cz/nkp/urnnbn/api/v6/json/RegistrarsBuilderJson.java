package cz.nkp.urnnbn.api.v6.json;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

import java.util.List;

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
