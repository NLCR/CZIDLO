package cz.nkp.urnnbn.czidlo_web_api.api.documents.core;

public class Record {

    //TODO: registrar, archiver
    //TODO: registrar-scope identifiers
    //TODO: digital instance, digital library

    private Urn urn;
    private Document document;
    private Entity entity;

    public static Record from(Urn urn, Document document, Entity entity) {
        Record record = new Record();
        record.urn = urn;
        record.document = document;
        record.entity = entity;
        return record;
    }

    public Urn getUrn() {
        return urn;
    }

    public Document getDocument() {
        return document;
    }

    public Entity getEntity() {
        return entity;
    }
}
