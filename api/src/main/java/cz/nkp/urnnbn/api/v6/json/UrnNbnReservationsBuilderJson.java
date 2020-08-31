package cz.nkp.urnnbn.api.v6.json;

import cz.nkp.urnnbn.core.dto.UrnNbn;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.util.List;

public class UrnNbnReservationsBuilderJson extends JsonBuilder {

    private final int maxReservationSize;
    private final int defaultReservationSize;
    private final List<UrnNbn> urnNbnList;
    private final Integer reservedSize;

    public UrnNbnReservationsBuilderJson(int maxReservationSize, int defaultReservationSize, int totalReserved, List<UrnNbn> urnNbnList) {
        this.maxReservationSize = maxReservationSize;
        this.defaultReservationSize = defaultReservationSize;
        this.urnNbnList = urnNbnList;
        this.reservedSize = totalReserved;
    }

    @Override
    protected String getName() {
        return "urnNbnReservations";
    }

    @Override
    protected JSONObject build() {
        try {
            JSONObject root = new JSONObject();
            root.put("maxReservationSize", maxReservationSize);
            root.put("defaultReservationSize", defaultReservationSize);
            root.put("reservedTotal", reservedSize);
            if (urnNbnList != null && !urnNbnList.isEmpty()) {
                JSONArray array = new JSONArray();
                for (UrnNbn urnNbn : urnNbnList) {
                    JSONObject reservation = new JSONObject();
                    reservation.put("value", urnNbn.toString());
                    reservation.put("reserved", urnNbn.getReserved());
                    array.put(reservation);
                }
                root.put("reservations", array);
            }
            return root;
        } catch (JSONException e) {
            LOGGER.severe(e.getMessage());
            return EMPTY_OBJECT;
        }
    }

}
