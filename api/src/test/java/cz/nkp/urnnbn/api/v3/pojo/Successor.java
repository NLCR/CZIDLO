package cz.nkp.urnnbn.api.v3.pojo;

public class Successor {

    public final String urnNbn;
    public final String note;

    public Successor(String urnNbn, String note) {
        this.urnNbn = urnNbn;
        this.note = note;
    }

    @Override
    public String toString() {
        return "Successor [urnNbn=" + urnNbn + ", note=" + note + "]";
    }

}
