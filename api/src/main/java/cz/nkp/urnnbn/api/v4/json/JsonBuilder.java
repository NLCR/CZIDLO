package cz.nkp.urnnbn.api.v4.json;

public interface JsonBuilder {

    public static final String EMPTY_JSON = "{}";

    public abstract String toJson();

}
