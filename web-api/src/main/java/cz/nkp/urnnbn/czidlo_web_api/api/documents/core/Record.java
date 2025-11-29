package cz.nkp.urnnbn.czidlo_web_api.api.documents.core;

public class Record {

    //TODO: intelectual entity
    //TODO: originator
    //TODO: publication
    //TODO: source document
    //TODO: ieidentifiers
    //TODO: digital instance, digital library
    //TODO: registrar, archiver
    //TODO: registrar-scope identifiers

    private Document document;
    private Urn urn;

    public static Record from(Urn urn, Document document) {
        Record record = new Record();
        record.urn = urn;
        record.document = document;
        return record;
    }

    public Urn getUrn() {
        return urn;
    }

    public Document getDocument() {
        return document;
    }
}
