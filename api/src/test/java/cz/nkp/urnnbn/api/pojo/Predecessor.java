package cz.nkp.urnnbn.api.pojo;

public class Predecessor {

    public final String urnNbn;
    public final String note;

    public Predecessor(String urnNbn, String note) {
        this.urnNbn = urnNbn;
        this.note = note;
    }

    @Override
    public String toString() {
        return "Predecessor [urnNbn=" + urnNbn + ", note=" + note + "]";
    }

}
