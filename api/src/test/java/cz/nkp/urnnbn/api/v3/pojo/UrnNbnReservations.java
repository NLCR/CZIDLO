package cz.nkp.urnnbn.api.v3.pojo;

import java.util.List;

public class UrnNbnReservations {
    public final int maxReservationSize;
    public final int defaultReservationSize;
    public final int totalReserved;
    public final List<String> reservedOffered;

    public UrnNbnReservations(int maxReservationSize, int defaultReservationSize, int totalReserved, List<String> reservedOffered) {
        this.maxReservationSize = maxReservationSize;
        this.defaultReservationSize = defaultReservationSize;
        this.totalReserved = totalReserved;
        this.reservedOffered = reservedOffered;
    }
}
