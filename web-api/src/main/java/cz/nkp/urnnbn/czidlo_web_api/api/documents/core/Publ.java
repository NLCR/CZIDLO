package cz.nkp.urnnbn.czidlo_web_api.api.documents.core;

public class Publ {

    public Integer year;
    public String place;
    public String publisher;

    public static Publ from(cz.nkp.urnnbn.core.dto.Publication dto) {
        if (dto == null) {
            return null;
        }
        Publ result = new Publ();
        result.year = dto.getYear();
        result.place = dto.getPlace();
        result.publisher = dto.getPublisher();
        return result;
    }

    public Integer getYear() {
        return year;
    }

    public String getPlace() {
        return place;
    }

    public String getPublisher() {
        return publisher;
    }
}
