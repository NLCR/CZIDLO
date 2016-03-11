package cz.nkp.urnnbn.api.v4.xml;

import java.util.List;

import cz.nkp.urnnbn.api.Utils;
import cz.nkp.urnnbn.api.pojo.Metadata;
import cz.nkp.urnnbn.api.pojo.Predecessor;
import cz.nkp.urnnbn.api.pojo.RsId;

public class DigDocRegistrationXmlBuilder {

    public static final String MONOGRAPH = "monograph";
    public static final String MONOGRAPH_VOLUME = "monographVolume";
    public static final String PERIODICAL = "periodical";
    public static final String PERIODICAL_VOLUME = "periodicalVolume";
    public static final String PERIODICAL_ISSUE = "periodicalIssue";
    public static final String THESIS = "thesis";
    public static final String ANALYTICAL = "analytical";
    public static final String OTHER = "other";

    private final String namespace;

    public DigDocRegistrationXmlBuilder(String namespace) {
        this.namespace = namespace;
    }

    public String noNamespace() {
        return "<import>"//
                + "<monograph>" //
                + "<titleInfo><title>TestTitle</title></titleInfo>"//
                + "</monograph>"//
                + "<digitalDocument/>"//
                + "</import>";
    }

    public String build(Metadata metadata, Long archiverId, String urnNbn, List<Predecessor> predecessors, List<RsId> ids) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("<import xmlns=\"%s\">", namespace));
        builder.append("<").append(metadata.type).append(">");
        builder.append("<titleInfo>");
        if (metadata.ieTitle != null) {
            builder.append("<title>").append(metadata.ieTitle).append("</title>");
        }
        if (metadata.ieSubTitle != null) {
            builder.append("<subTitle>").append(metadata.ieSubTitle).append("</subTitle>");
        }
        if (metadata.ieMonographTitle != null) {
            builder.append("<monographTitle>").append(metadata.ieMonographTitle).append("</monographTitle>");
        }
        if (metadata.iePeriodicalTitle != null) {
            builder.append("<periodicalTitle>").append(metadata.iePeriodicalTitle).append("</periodicalTitle>");
        }
        if (metadata.ieVolumeTitle != null) {
            builder.append("<volumeTitle>").append(metadata.ieVolumeTitle).append("</volumeTitle>");
        }
        if (metadata.ieIssueTitle != null) {
            builder.append("<issueTitle>").append(metadata.ieIssueTitle).append("</issueTitle>");
        }

        builder.append("</titleInfo>");
        if (metadata.ieCcnb != null) {
            builder.append("<ccnb>").append(metadata.ieCcnb).append("</ccnb>");
        }
        if (metadata.ieIsbn != null) {
            builder.append("<isbn>").append(metadata.ieIsbn).append("</isbn>");
        }
        if (metadata.ieIssn != null) {
            builder.append("<issn>").append(metadata.ieIssn).append("</issn>");
        }
        if (metadata.ieOtherId != null) {
            builder.append("<otherId>").append(metadata.ieOtherId).append("</otherId>");
        }
        if (metadata.ieDocumentType != null) {
            builder.append("<documentType>").append(metadata.ieDocumentType).append("</documentType>");
        }
        if (metadata.ieDigitalBorn != null) {
            builder.append("<digitalBorn>").append(metadata.ieDigitalBorn).append("</digitalBorn>");
        }
        if (metadata.iePrimaryOriginator != null && metadata.iePrimaryOriginatorType != null) {
            builder.append("<primaryOriginator").append(" type=\"").append(metadata.iePrimaryOriginatorType).append("\">");
            builder.append(metadata.iePrimaryOriginator).append("</primaryOriginator>");
        }
        if (metadata.ieOtherOriginator != null) {
            builder.append("<otherOriginator>").append(metadata.ieOtherOriginator).append("</otherOriginator>");
        }
        // source document
        if (metadata.srcDocCcnb != null || metadata.srcDocIsbn != null || metadata.srcDocIssn != null || metadata.srcDocIssueTitle != null
                || metadata.srcDocOtherId != null || metadata.srcDocPubPlace != null || metadata.srcDocPubPublisher != null
                || metadata.srcDocPubYear != null || metadata.srcDocTitle != null || metadata.srcDocVolumeTitle != null) {
            builder.append("<sourceDocument>");
            if (metadata.srcDocTitle != null || metadata.srcDocVolumeTitle != null || metadata.srcDocIssueTitle != null) {
                builder.append("<titleInfo>");
                if (metadata.srcDocTitle != null) {
                    builder.append("<title>").append(metadata.srcDocTitle).append("</title>");
                }
                if (metadata.srcDocVolumeTitle != null) {
                    builder.append("<volumeTitle>").append(metadata.srcDocVolumeTitle).append("</volumeTitle>");
                }
                if (metadata.srcDocIssueTitle != null) {
                    builder.append("<issueTitle>").append(metadata.srcDocIssueTitle).append("</issueTitle>");
                }
                builder.append("</titleInfo>");
            }
            if (metadata.srcDocCcnb != null) {
                builder.append("<ccnb>").append(metadata.srcDocCcnb).append("</ccnb>");
            }
            if (metadata.srcDocIsbn != null) {
                builder.append("<isbn>").append(metadata.srcDocIsbn).append("</isbn>");
            }
            if (metadata.srcDocIssn != null) {
                builder.append("<issn>").append(metadata.srcDocIssn).append("</issn>");
            }

            if (metadata.srcDocOtherId != null) {
                builder.append("<otherId>").append(metadata.srcDocOtherId).append("</otherId>");
            }
            // TODO: publication
            if (metadata.srcDocPubPublisher != null || metadata.srcDocPubPlace != null || metadata.srcDocPubYear != null) {
                builder.append("<publication>");
                if (metadata.srcDocPubPublisher != null) {
                    builder.append("<publisher>").append(metadata.srcDocPubPublisher).append("</publisher>");
                }
                if (metadata.srcDocPubPlace != null) {
                    builder.append("<place>").append(metadata.srcDocPubPlace).append("</place>");
                }
                if (metadata.srcDocPubYear != null) {
                    builder.append("<year>").append(metadata.srcDocPubYear).append("</year>");
                }
                builder.append("</publication>");
            }
            builder.append("</sourceDocument>");
        }
        // publication
        if (metadata.iePublicationPublisher != null || metadata.iePublicationPlace != null || metadata.iePublicationyear != null) {
            builder.append("<publication>");
            if (metadata.iePublicationPublisher != null) {
                builder.append("<publisher>").append(metadata.iePublicationPublisher).append("</publisher>");
            }
            if (metadata.iePublicationPlace != null) {
                builder.append("<place>").append(metadata.iePublicationPlace).append("</place>");
            }
            if (metadata.iePublicationyear != null) {
                builder.append("<year>").append(metadata.iePublicationyear).append("</year>");
            }
            builder.append("</publication>");
        }
        // degree awarding institution
        if (metadata.degreeAwardingInstitution != null) {
            builder.append("<degreeAwardingInstitution>").append(metadata.degreeAwardingInstitution).append("</degreeAwardingInstitution>");
        }

        builder.append("</").append(metadata.type).append(">");
        builder.append("<digitalDocument>");

        // archiver
        if (archiverId != null) {
            builder.append(String.format("<archiverId>%d</archiverId>", archiverId));
        }

        // URN:NBN, predecessors
        if (urnNbn == null && (predecessors != null && !predecessors.isEmpty())) {// predecessors only
            builder.append("<urnNbn>");
            for (Predecessor predecessor : predecessors) {
                if (predecessor.note == null) {
                    builder.append(String.format("<predecessor value=\"%s\"/>", predecessor.urnNbn));
                } else {
                    builder.append(String.format("<predecessor value=\"%s\" note=\"%s\"/>", predecessor.urnNbn, predecessor.note));
                }
            }
            builder.append("</urnNbn>");
        } else if (urnNbn != null && (predecessors == null || predecessors.isEmpty())) {// urn only
            builder.append(String.format("<urnNbn><value>%s</value></urnNbn>", urnNbn));
        } else if (urnNbn != null && (predecessors != null && !predecessors.isEmpty())) {// both
            builder.append("<urnNbn>");
            builder.append(String.format("<value>%s</value>", urnNbn));
            for (Predecessor predecessor : predecessors) {
                if (predecessor.note == null) {
                    builder.append(String.format("<predecessor value=\"%s\"/>", predecessor.urnNbn));
                } else {
                    builder.append(String.format("<predecessor value=\"%s\" note=\"%s\"/>", predecessor.urnNbn, predecessor.note));
                }
            }
            builder.append("</urnNbn>");
        }
        // REGISTRAR-SCOPE-IDs
        if (ids != null && !ids.isEmpty()) {
            builder.append("<registrarScopeIdentifiers>");
            for (RsId id : ids) {
                builder.append(String.format("<id type=\"%s\">%s</id>", Utils.xmlEscape(id.type), Utils.xmlEscape(id.value)));
            }
            builder.append("</registrarScopeIdentifiers>");
        }

        if (metadata.ddFinanced != null) {
            builder.append("<financed>").append(metadata.ddFinanced).append("</financed>");
        }
        if (metadata.ddContractNum != null) {
            builder.append("<contractNumber>").append(metadata.ddContractNum).append("</contractNumber>");
        }

        // technical metadata
        builder.append("<technicalMetadata>");
        if (metadata.techFormat != null || metadata.techFormatVersion != null) {
            builder.append("<format");
            if (metadata.techFormatVersion != null) {
                builder.append(String.format(" version=\"%s\"", metadata.techFormatVersion));
            }
            builder.append(">");
            if (metadata.techFormat != null) {
                builder.append(metadata.techFormat);
            }
            builder.append("</format>");
        }
        if (metadata.techExtent != null) {
            builder.append("<extent>").append(metadata.techExtent).append("</extent>");
        }
        if (metadata.techResolutionHorizontal != null || metadata.techResolutionVertical != null) {
            builder.append("<resolution>");
            if (metadata.techResolutionHorizontal != null) {
                builder.append("<horizontal>").append(metadata.techResolutionHorizontal).append("</horizontal>");
            }
            if (metadata.techResolutionVertical != null) {
                builder.append("<vertical>").append(metadata.techResolutionVertical).append("</vertical>");
            }
            builder.append("</resolution>");
        }
        if (metadata.techCompressionRatio != null || metadata.techCompression != null) {
            builder.append("<compression");
            if (metadata.techCompressionRatio != null) {
                builder.append(String.format(" ratio=\"%f\"", metadata.techCompressionRatio));
            }
            builder.append(">");
            if (metadata.techCompression != null) {
                builder.append(metadata.techCompression);
            }
            builder.append("</compression>");
        }
        if (metadata.techColorModel != null || metadata.techColorDepth != null) {
            builder.append("<color>");
            if (metadata.techColorModel != null) {
                builder.append("<model>").append(metadata.techColorModel).append("</model>");
            }
            if (metadata.techColorDepth != null) {
                builder.append("<depth>").append(metadata.techColorDepth).append("</depth>");
            }
            builder.append("</color>");
        }
        if (metadata.techIccProfile != null) {
            builder.append("<iccProfile>").append(metadata.techIccProfile).append("</iccProfile>");
        }
        if (metadata.techPictureSizeHeight != null || metadata.techPictureSizeWidth != null) {
            builder.append("<pictureSize>");
            if (metadata.techPictureSizeWidth != null) {
                builder.append("<width>").append(metadata.techPictureSizeWidth).append("</width>");
            }
            if (metadata.techPictureSizeHeight != null) {
                builder.append("<height>").append(metadata.techPictureSizeHeight).append("</height>");
            }
            builder.append("</pictureSize>");
        }
        builder.append("</technicalMetadata>");

        builder.append("</digitalDocument>");
        builder.append("</import>");
        String result = builder.toString();
        return result;
    }

    public String minimal() {
        return build(Metadata.monographMinimal(), null, null, null, null);
    }

    public String withMetadata(Metadata metadata) {
        return build(metadata, null, null, null, null);
    }

    public String withUrnNbn(String urnNbn) {
        return build(Metadata.monographMinimal(), null, urnNbn, null, null);
    }

    public String withPredecessors(List<Predecessor> predecessors) {
        return build(Metadata.monographMinimal(), null, null, predecessors, null);
    }

    public String withPredecessors(String urnNbn, List<Predecessor> predecessors) {
        return build(Metadata.monographMinimal(), null, urnNbn, predecessors, null);
    }

    public String withRsIds(List<RsId> rsIds) {
        return build(Metadata.monographMinimal(), null, null, null, rsIds);
    }

    public String withRsIds(String urnNbn, List<RsId> rsIds) {
        return build(Metadata.monographMinimal(), null, urnNbn, null, rsIds);
    }

    public String withArchiver(long archiverId) {
        return build(Metadata.monographMinimal(), archiverId, null, null, null);
    }

}
