package cz.nkp.urnnbn.czidlo_web_api.api.documents.core;

public class Record {

    //TODO: registrar, archiver
    //TODO: registrar-scope identifiers
    //TODO: digital instance, digital library

    private Urn urnNbn;
    private Document digitalDocument;
    private Entity intelectualEntity;

    public static Record from(Urn urn, Document document, Entity entity) {
        Record record = new Record();
        record.urnNbn = urn;
        record.digitalDocument = document;
        record.intelectualEntity = entity;
        return record;
    }

    public Urn getUrnNbn() {
        return urnNbn;
    }

    public Document getDigitalDocument() {
        return digitalDocument;
    }

    public Entity getIntelectualEntity() {
        return intelectualEntity;
    }
}
