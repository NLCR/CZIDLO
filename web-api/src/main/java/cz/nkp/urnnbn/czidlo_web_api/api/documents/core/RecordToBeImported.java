package cz.nkp.urnnbn.czidlo_web_api.api.documents.core;

import cz.nkp.urnnbn.core.EntityType;
import cz.nkp.urnnbn.core.OriginType;
import cz.nkp.urnnbn.czidlo_web_api.api.exceptions.BadArgumentException;
import jakarta.json.JsonObject;
import jakarta.validation.constraints.NotNull;

import java.util.function.Function;

public class RecordToBeImported {
    @NotNull
    public DigDoc digitalDocument;
    @NotNull
    public IntEnt intelectualEntity;
    @NotNull
    public String registrarCode;
    public Long archiverId;
    public String urnNbn;

    public static RecordToBeImported fromJsonObject(JsonObject root) throws BadArgumentException {
        //System.out.println("RecordToBeImported.fromJsonObject: " + root);
        //System.out.println(root.toString());
        RecordToBeImported record = new RecordToBeImported();
        //digitalDocument
        if (root.containsKey("digitalDocument")) {
            record.digitalDocument = parseDigDoc(root.getJsonObject("digitalDocument"));
        } else {
            throw new BadArgumentException("Missing mandatory parameter: digitalDocument");
        }
        //intellectualEntity
        if (root.containsKey("intelectualEntity")) {
            record.intelectualEntity = parseIntEnt(root.getJsonObject("intelectualEntity"));
        } else {
            throw new BadArgumentException("Missing mandatory parameter: intelectualEntity");
        }
        //registrarCode
        record.registrarCode = root.getString("registrarCode");
        if (root.containsKey("archiverId")) {
            record.archiverId = (long) root.getInt("archiverId");
        }
        //urnNbn
        String urnNbn = null;
        if (root.containsKey("urnNbn")) {
            record.urnNbn = root.getString("urnNbn");
        }
        return record;
    }

    private static DigDoc parseDigDoc(JsonObject digitalDocument) {
        DigDoc result = new DigDoc();
        if (digitalDocument.containsKey("financedFrom")) {
            result.financedFrom = digitalDocument.getString("financedFrom");
        }
        if (digitalDocument.containsKey("contractNumber")) {
            result.contractNumber = digitalDocument.getString("contractNumber");
        }
        if (digitalDocument.containsKey("format")) {
            result.format = digitalDocument.getString("format");
        }
        if (digitalDocument.containsKey("formatVersion")) {
            result.formatVersion = digitalDocument.getString("formatVersion");
        }
        if (digitalDocument.containsKey("extent")) {
            result.extent = digitalDocument.getString("extent");
        }
        if (digitalDocument.containsKey("resolutionHorizontal")) {
            result.resolutionHorizontal = digitalDocument.getInt("resolutionHorizontal");
        }
        if (digitalDocument.containsKey("resolutionVertical")) {
            result.resolutionVertical = digitalDocument.getInt("resolutionVertical");
        }
        if (digitalDocument.containsKey("compression")) {
            result.compression = digitalDocument.getString("compression");
        }
        if (digitalDocument.containsKey("compressionRatio")) {
            result.compressionRatio = digitalDocument.getJsonNumber("compressionRatio").doubleValue();
        }
        if (digitalDocument.containsKey("colorModel")) {
            result.colorModel = digitalDocument.getString("colorModel");
        }
        if (digitalDocument.containsKey("colorDepth")) {
            result.colorDepth = digitalDocument.getInt("colorDepth");
        }
        if (digitalDocument.containsKey("iccProfile")) {
            result.iccProfile = digitalDocument.getString("iccProfile");
        }
        if (digitalDocument.containsKey("pictureWidth")) {
            result.pictureWidth = digitalDocument.getInt("pictureWidth");
        }
        if (digitalDocument.containsKey("pictureHeight")) {
            result.pictureHeight = digitalDocument.getInt("pictureHeight");
        }
        return result;
    }

    private static IntEnt parseIntEnt(JsonObject ie) throws BadArgumentException {
        IntEnt result = new IntEnt();
        if (ie.containsKey("entityType")) {
            String entityTypeStr = ie.getString("entityType");
            try {
                result.entityType = EntityType.valueOf(entityTypeStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BadArgumentException("Invalid value for parameter entityType: " + entityTypeStr);
            }
        }
        if (ie.containsKey("documentType")) {
            result.documentType = ie.getString("documentType");
        }
        if (ie.containsKey("digitalBorn")) {
            result.digitalBorn = ie.getBoolean("digitalBorn");
        }
        if (ie.containsKey("otherOriginator")) {
            result.otherOriginator = ie.getString("otherOriginator");
        }
        if (ie.containsKey("degreeAwardingInstitution")) {
            result.degreeAwardingInstitution = ie.getString("degreeAwardingInstitution");
        }
        if (ie.containsKey("originator")) {
            JsonObject originator = ie.getJsonObject("originator");
            result.originator = new Orig();
            if (originator.containsKey("type")) {
                String typeStr = originator.getString("type");
                try {
                    result.originator.type = OriginType.valueOf(typeStr);
                } catch (IllegalArgumentException e) {
                    throw new BadArgumentException("Invalid value for parameter originator.type: " + typeStr);
                }
            }
            if (originator.containsKey("value")) {
                result.originator.value = originator.getString("value");
            }
        }
        if (ie.containsKey("publication")) {
            JsonObject publication = ie.getJsonObject("publication");
            result.publication = new Publ();
            if (publication.containsKey("publisher")) {
                result.publication.publisher = publication.getString("publisher");
            }
            if (publication.containsKey("place")) {
                result.publication.place = publication.getString("place");
            }
            if (publication.containsKey("year")) {
                result.publication.year = publication.getInt("year");
            }
        }
        if (ie.containsKey("sourceDocument")) {
            JsonObject sourceDocument = ie.getJsonObject("sourceDocument");
            result.sourceDocument = new SrcDoc();
            if (sourceDocument.containsKey("ccnb")) {
                result.sourceDocument.ccnb = sourceDocument.getString("ccnb");
            }
            if (sourceDocument.containsKey("isbn")) {
                result.sourceDocument.isbn = sourceDocument.getString("isbn");
            }
            if (sourceDocument.containsKey("issn")) {
                result.sourceDocument.issn = sourceDocument.getString("issn");
            }
            if (sourceDocument.containsKey("otherId")) {
                result.sourceDocument.otherId = sourceDocument.getString("otherId");
            }
            if (sourceDocument.containsKey("title")) {
                result.sourceDocument.title = sourceDocument.getString("title");
            }
            if (sourceDocument.containsKey("volumeTitle")) {
                result.sourceDocument.volumeTitle = sourceDocument.getString("volumeTitle");
            }
            if (sourceDocument.containsKey("issueTitle")) {
                result.sourceDocument.issueTitle = sourceDocument.getString("issueTitle");
            }
            if (sourceDocument.containsKey("publicationPlace")) {
                result.sourceDocument.publicationPlace = sourceDocument.getString("publicationPlace");
            }
            if (sourceDocument.containsKey("publisher")) {
                result.sourceDocument.publisher = sourceDocument.getString("publisher");
            }
            if (sourceDocument.containsKey("publicationYear")) {
                result.sourceDocument.publicationYear = sourceDocument.getInt("publicationYear");
            }
        }

        return result;
    }

    static <T> T readParam(String paramName, Function<String, T> funk) throws BadArgumentException {
        try {
            return funk.apply(paramName);
        } catch (NullPointerException e) {
            throw new BadArgumentException("Missing mandatory parameter: " + paramName);
        } catch (ClassCastException e) {
            throw new BadArgumentException("Invalid type for parameter: " + paramName);
        }
    }

    @Override
    public String toString() {
        return "RecordToBeImported{\n" +
                "digitalDocument=" + digitalDocument +
                ",\n intelectualEntity=" + intelectualEntity +
                ",\n registrarCode='" + registrarCode + '\'' +
                ",\n archiverId=" + archiverId +
                ",\n urnNbn='" + urnNbn + '\'' +
                "\n}";
    }
}
