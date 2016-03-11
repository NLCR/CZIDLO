package cz.nkp.urnnbn.api.pojo;

import java.util.HashMap;
import java.util.Map;

public class Metadata {

    private static final String ISBN1 = "8090119964";
    private static final String ISBN2 = "9788090119963";
    private static final String ISSN1 = "0317-8471";
    private static final String ISSN2 = "2049-3630";
    private static final String CCNB1 = "cnb111111111";
    private static final String CCNB2 = "cnb222222222";

    public final String description;

    public String type;
    // ie
    public String ieTitle;
    public String ieSubTitle;
    public String ieMonographTitle;
    public String iePeriodicalTitle;
    public String ieVolumeTitle;
    public String ieIssueTitle;
    public String ieCcnb;
    public String ieIsbn;
    public String ieIssn;
    public String ieOtherId;
    public String ieDocumentType;
    public Boolean ieDigitalBorn;
    public String iePrimaryOriginatorType;
    public String iePrimaryOriginator;
    public String ieOtherOriginator;
    public String iePublicationPublisher;
    public String iePublicationPlace;
    public Integer iePublicationyear;
    // dd
    public String ddFinanced;
    public String ddContractNum;
    // technical metadata
    public String techFormat;
    public String techFormatVersion;
    public String techExtent;
    public Integer techResolutionHorizontal;
    public Integer techResolutionVertical;
    public Double techCompressionRatio;
    public String techCompression;
    public String techColorModel;
    public Integer techColorDepth;
    public String techIccProfile;
    public Integer techPictureSizeWidth;
    public Integer techPictureSizeHeight;
    // source document
    public String srcDocTitle;
    public String srcDocVolumeTitle;
    public String srcDocIssueTitle;
    public String srcDocCcnb;
    public String srcDocIsbn;
    public String srcDocIssn;
    public String srcDocOtherId;
    public String srcDocPubPublisher;
    public String srcDocPubPlace;
    public Integer srcDocPubYear;
    // degree awarding institution
    public String degreeAwardingInstitution;

    public Metadata(String description) {
        this.description = description;
    }

    public Map<String, Object> getDataByXmlPath() {
        Map<String, Object> result = new HashMap<String, Object>();

        // ie
        result.put("intelectualEntity.titleInfo.title", ieTitle);
        result.put("intelectualEntity.titleInfo.subTitle", ieSubTitle);
        result.put("intelectualEntity.titleInfo.monographTitle", ieMonographTitle);
        result.put("intelectualEntity.titleInfo.periodicalTitle", iePeriodicalTitle);
        result.put("intelectualEntity.titleInfo.volumeTitle", ieVolumeTitle);
        result.put("intelectualEntity.titleInfo.issueTitle", ieIssueTitle);
        result.put("intelectualEntity.ccnb", ieCcnb);
        result.put("intelectualEntity.isbn", ieIsbn);
        result.put("intelectualEntity.issn", ieIssn);
        result.put("intelectualEntity.otherId", ieOtherId);
        result.put("intelectualEntity.documentType", ieDocumentType);
        result.put("intelectualEntity.digitalBorn", ieDigitalBorn);
        result.put("intelectualEntity.primaryOriginator.@type", iePrimaryOriginatorType);
        result.put("intelectualEntity.primaryOriginator", iePrimaryOriginator);
        result.put("intelectualEntity.otherOriginator", ieOtherOriginator);
        result.put("intelectualEntity.publication.publisher", iePublicationPublisher);
        result.put("intelectualEntity.publication.place", iePublicationPlace);
        result.put("intelectualEntity.publication.year", iePublicationyear);
        // source document
        result.put("intelectualEntity.sourceDocument.titleInfo.title", srcDocTitle);
        result.put("intelectualEntity.sourceDocument.titleInfo.volumeTitle", srcDocVolumeTitle);
        result.put("intelectualEntity.sourceDocument.titleInfo.issueTitle", srcDocIssueTitle);
        result.put("intelectualEntity.sourceDocument.ccnb", srcDocCcnb);
        result.put("intelectualEntity.sourceDocument.isbn", srcDocIsbn);
        result.put("intelectualEntity.sourceDocument.issn", srcDocIssn);
        result.put("intelectualEntity.sourceDocument.otherId", srcDocOtherId);
        result.put("intelectualEntity.sourceDocument.publication.publisher", srcDocPubPublisher);
        result.put("intelectualEntity.sourceDocument.publication.place", srcDocPubPlace);
        result.put("intelectualEntity.sourceDocument.publication.year", srcDocPubYear);
        // degreeAwardingInstitution
        result.put("intelectualEntity.degreeAwardingInstitution", degreeAwardingInstitution);
        // dd
        result.put("financed", ddFinanced);
        result.put("contractNumber", ddContractNum);
        // technical metadata
        result.put("technicalMetadata.format", techFormat);
        result.put("technicalMetadata.format.@version", techFormatVersion);
        result.put("technicalMetadata.extent", techExtent);
        result.put("technicalMetadata.resolution.horizontal", techResolutionHorizontal);
        result.put("technicalMetadata.resolution.vertical", techResolutionVertical);
        result.put("technicalMetadata.compression.@ratio", techCompressionRatio);
        result.put("technicalMetadata.compression", techCompression);
        result.put("technicalMetadata.color.model", techColorModel);
        result.put("technicalMetadata.color.depth", techColorDepth);
        result.put("technicalMetadata.iccProfile", techIccProfile);
        result.put("technicalMetadata.pictureSize.width", techPictureSizeWidth);
        result.put("technicalMetadata.pictureSize.height", techPictureSizeHeight);

        return result;
    }

    public static Metadata monographMinimal() {
        Metadata result = new Metadata("monograph-minimal");
        result.type = "monograph";
        result.ieTitle = "monTitle0";
        return result;
    }

    public static Metadata monographFull1() {
        Metadata result = new Metadata("monograph-full_1");
        result.type = "monograph";
        result.ieTitle = "title1";
        result.ieSubTitle = "subtitle1";
        result.ieCcnb = CCNB1;
        result.ieIsbn = ISBN1;
        result.ieOtherId = "other1";
        result.ieDocumentType = "type1";
        result.ieDigitalBorn = true;
        result.iePrimaryOriginatorType = "AUTHOR";
        result.iePrimaryOriginator = "author1";
        result.ieOtherOriginator = "otherOriginator1";
        result.iePublicationPublisher = "publisher1";
        result.iePublicationPlace = "place1";
        result.iePublicationyear = 11;
        // dd
        result.ddFinanced = "financed1";
        result.ddContractNum = "contract1";
        result.techFormat = "format1";
        result.techFormatVersion = "version1";
        result.techExtent = "extent1";
        result.techResolutionHorizontal = 12;
        result.techResolutionVertical = 13;
        result.techCompressionRatio = 1.4;
        result.techCompression = "compression1";
        result.techColorModel = "model1";
        result.techColorDepth = 15;
        result.techIccProfile = "icc1";
        result.techPictureSizeWidth = 16;
        result.techPictureSizeHeight = 17;
        return result;
    }

    public static Metadata monographFull2() {
        Metadata result = new Metadata("monograph-full_2");
        result.type = "monograph";
        result.ieTitle = "title2";
        result.ieSubTitle = "subtitle2";
        result.ieCcnb = CCNB2;
        result.ieIsbn = ISBN2;
        result.ieOtherId = "other2";
        result.ieDocumentType = "type2";
        result.ieDigitalBorn = false;
        result.iePrimaryOriginatorType = "EVENT";
        result.iePrimaryOriginator = "event2";
        result.ieOtherOriginator = "otherOriginator2";
        result.iePublicationPublisher = "publisher2";
        result.iePublicationPlace = "place2";
        result.iePublicationyear = 21;
        // dd
        result.ddFinanced = "financed2";
        result.ddContractNum = "contract2";
        result.techFormat = "format2";
        result.techFormatVersion = "version2";
        result.techExtent = "extent2";
        result.techResolutionHorizontal = 22;
        result.techResolutionVertical = 23;
        result.techCompressionRatio = 2.4;
        result.techCompression = "compression2";
        result.techColorModel = "model2";
        result.techColorDepth = 25;
        result.techIccProfile = "icc2";
        result.techPictureSizeWidth = 26;
        result.techPictureSizeHeight = 27;
        return result;
    }

    public static Metadata monographVolumeMinimal() {
        Metadata result = new Metadata("monographVolume-minimal");
        result.type = "monographVolume";
        result.ieMonographTitle = "title0";
        result.ieVolumeTitle = "volTitle0";
        return result;
    }

    public static Metadata monographVolumeFull1() {
        Metadata result = new Metadata("monographVolume-full_1");
        result.type = "monographVolume";
        result.ieMonographTitle = "title1";
        result.ieVolumeTitle = "volTitle1";
        result.ieCcnb = CCNB1;
        result.ieIsbn = ISBN1;
        result.ieOtherId = "other1";
        result.ieDocumentType = "type1";
        result.ieDigitalBorn = true;
        result.iePrimaryOriginatorType = "AUTHOR";
        result.iePrimaryOriginator = "author1";
        result.ieOtherOriginator = "otherOriginator1";
        result.iePublicationPublisher = "publisher1";
        result.iePublicationPlace = "place1";
        result.iePublicationyear = 11;
        // dd
        result.ddFinanced = "financed1";
        result.ddContractNum = "contract1";
        result.techFormat = "format1";
        result.techFormatVersion = "version1";
        result.techExtent = "extent1";
        result.techResolutionHorizontal = 12;
        result.techResolutionVertical = 13;
        result.techCompressionRatio = 1.4;
        result.techCompression = "compression1";
        result.techColorModel = "model1";
        result.techColorDepth = 15;
        result.techIccProfile = "icc1";
        result.techPictureSizeWidth = 16;
        result.techPictureSizeHeight = 17;
        return result;
    }

    public static Metadata monographVolumeFull2() {
        Metadata result = new Metadata("monographVolume-full_2");
        result.type = "monographVolume";
        result.ieMonographTitle = "title2";
        result.ieVolumeTitle = "volTitle2";
        result.ieCcnb = CCNB2;
        result.ieIsbn = ISBN2;
        result.ieOtherId = "other2";
        result.ieDocumentType = "type2";
        result.ieDigitalBorn = false;
        result.iePrimaryOriginatorType = "EVENT";
        result.iePrimaryOriginator = "event2";
        result.ieOtherOriginator = "otherOriginator2";
        result.iePublicationPublisher = "publisher2";
        result.iePublicationPlace = "place2";
        result.iePublicationyear = 21;
        // dd
        result.ddFinanced = "financed2";
        result.ddContractNum = "contract2";
        result.techFormat = "format2";
        result.techFormatVersion = "version2";
        result.techExtent = "extent2";
        result.techResolutionHorizontal = 22;
        result.techResolutionVertical = 23;
        result.techCompressionRatio = 2.4;
        result.techCompression = "compression2";
        result.techColorModel = "model2";
        result.techColorDepth = 25;
        result.techIccProfile = "icc2";
        result.techPictureSizeWidth = 26;
        result.techPictureSizeHeight = 27;
        return result;
    }

    public static Metadata periodicalMinimal() {
        Metadata result = new Metadata("periodical-minimal");
        result.type = "periodical";
        result.ieTitle = "title0";
        return result;
    }

    public static Metadata periodicalFull1() {
        Metadata result = new Metadata("periodical-full_1");
        result.type = "periodical";
        result.ieTitle = "title1";
        result.ieSubTitle = "subTitle1";
        result.ieCcnb = CCNB1;
        result.ieIssn = ISSN1;
        result.ieOtherId = "other1";
        result.ieDocumentType = "type1";
        result.ieDigitalBorn = true;
        result.iePrimaryOriginatorType = "AUTHOR";
        result.iePrimaryOriginator = "author1";
        result.ieOtherOriginator = "otherOriginator1";
        result.iePublicationPublisher = "publisher1";
        result.iePublicationPlace = "place1";
        result.iePublicationyear = 11;
        // dd
        result.ddFinanced = "financed1";
        result.ddContractNum = "contract1";
        result.techFormat = "format1";
        result.techFormatVersion = "version1";
        result.techExtent = "extent1";
        result.techResolutionHorizontal = 1;
        result.techResolutionVertical = 12;
        result.techCompressionRatio = 1.3;
        result.techCompression = "compression1";
        result.techColorModel = "model1";
        result.techColorDepth = 14;
        result.techIccProfile = "icc1";
        result.techPictureSizeWidth = 15;
        result.techPictureSizeHeight = 16;
        return result;
    }

    public static Metadata periodicalFull2() {
        Metadata result = new Metadata("periodical-full_2");
        result.type = "periodical";
        result.ieTitle = "title2";
        result.ieSubTitle = "subTitle2";
        result.ieCcnb = CCNB2;
        result.ieIssn = ISSN2;
        result.ieOtherId = "other2";
        result.ieDocumentType = "type2";
        result.ieDigitalBorn = false;
        result.iePrimaryOriginatorType = "AUTHOR";
        result.iePrimaryOriginator = "author2";
        result.ieOtherOriginator = "otherOriginator2";
        result.iePublicationPublisher = "publisher2";
        result.iePublicationPlace = "place2";
        result.iePublicationyear = 21;
        // dd
        result.ddFinanced = "financed2";
        result.ddContractNum = "contract2";
        result.techFormat = "format2";
        result.techFormatVersion = "version2";
        result.techExtent = "extent2";
        result.techResolutionHorizontal = 22;
        result.techResolutionVertical = 23;
        result.techCompressionRatio = 2.4;
        result.techCompression = "compression2";
        result.techColorModel = "model2";
        result.techColorDepth = 25;
        result.techIccProfile = "icc2";
        result.techPictureSizeWidth = 26;
        result.techPictureSizeHeight = 27;
        return result;
    }

    public static Metadata periodicalVolumeMinimal() {
        Metadata result = new Metadata("periodicalVolume-minimal");
        result.type = "periodicalVolume";
        result.iePeriodicalTitle = "title0";
        result.ieVolumeTitle = "volume0";
        return result;
    }

    public static Metadata periodicalVolumeFull1() {
        Metadata result = new Metadata("periodicalVolume-full_1");
        result.type = "periodicalVolume";
        result.iePeriodicalTitle = "title1";
        result.ieVolumeTitle = "volume1";
        result.ieCcnb = CCNB1;
        result.ieIssn = ISSN1;
        result.ieOtherId = "other1";
        result.ieDocumentType = "type1";
        result.ieDigitalBorn = true;
        result.iePrimaryOriginatorType = "AUTHOR";
        result.iePrimaryOriginator = "author1";
        result.ieOtherOriginator = "otherOriginator1";
        result.iePublicationPublisher = "publisher1";
        result.iePublicationPlace = "place1";
        result.iePublicationyear = 11;
        // dd
        result.ddFinanced = "financed1";
        result.ddContractNum = "contract1";
        result.techFormat = "format1";
        result.techFormatVersion = "version1";
        result.techExtent = "extent1";
        result.techResolutionHorizontal = 1;
        result.techResolutionVertical = 12;
        result.techCompressionRatio = 1.3;
        result.techCompression = "compression1";
        result.techColorModel = "model1";
        result.techColorDepth = 14;
        result.techIccProfile = "icc1";
        result.techPictureSizeWidth = 15;
        result.techPictureSizeHeight = 16;
        return result;
    }

    public static Metadata periodicalVolumeFull2() {
        Metadata result = new Metadata("periodicalVolume-full_2");
        result.type = "periodicalVolume";
        result.iePeriodicalTitle = "title2";
        result.ieVolumeTitle = "volume2";
        result.ieCcnb = CCNB2;
        result.ieIssn = ISSN2;
        result.ieOtherId = "other2";
        result.ieDocumentType = "type2";
        result.ieDigitalBorn = false;
        result.iePrimaryOriginatorType = "AUTHOR";
        result.iePrimaryOriginator = "author2";
        result.ieOtherOriginator = "otherOriginator2";
        result.iePublicationPublisher = "publisher2";
        result.iePublicationPlace = "place2";
        result.iePublicationyear = 21;
        // dd
        result.ddFinanced = "financed2";
        result.ddContractNum = "contract2";
        result.techFormat = "format2";
        result.techFormatVersion = "version2";
        result.techExtent = "extent2";
        result.techResolutionHorizontal = 22;
        result.techResolutionVertical = 23;
        result.techCompressionRatio = 2.4;
        result.techCompression = "compression2";
        result.techColorModel = "model2";
        result.techColorDepth = 25;
        result.techIccProfile = "icc2";
        result.techPictureSizeWidth = 26;
        result.techPictureSizeHeight = 27;
        return result;
    }

    public static Metadata periodicalIssueMinimal() {
        Metadata result = new Metadata("periodicalIssue-minimal");
        result.type = "periodicalIssue";
        result.iePeriodicalTitle = "title0";
        result.ieVolumeTitle = "volume0";
        result.ieIssueTitle = "issue0";
        return result;
    }

    public static Metadata periodicalIssueFull1() {
        Metadata result = new Metadata("periodicalIssue-full_1");
        result.type = "periodicalIssue";
        result.iePeriodicalTitle = "title1";
        result.ieVolumeTitle = "volume1";
        result.ieIssueTitle = "issue1";
        result.ieCcnb = CCNB1;
        result.ieIssn = ISSN1;
        result.ieOtherId = "other1";
        result.ieDocumentType = "type1";
        result.ieDigitalBorn = true;
        result.iePrimaryOriginatorType = "AUTHOR";
        result.iePrimaryOriginator = "author1";
        result.ieOtherOriginator = "otherOriginator1";
        result.iePublicationPublisher = "publisher1";
        result.iePublicationPlace = "place1";
        result.iePublicationyear = 11;
        // dd
        result.ddFinanced = "financed1";
        result.ddContractNum = "contract1";
        result.techFormat = "format1";
        result.techFormatVersion = "version1";
        result.techExtent = "extent1";
        result.techResolutionHorizontal = 1;
        result.techResolutionVertical = 12;
        result.techCompressionRatio = 1.3;
        result.techCompression = "compression1";
        result.techColorModel = "model1";
        result.techColorDepth = 14;
        result.techIccProfile = "icc1";
        result.techPictureSizeWidth = 15;
        result.techPictureSizeHeight = 16;
        return result;
    }

    public static Metadata periodicalIssueFull2() {
        Metadata result = new Metadata("periodicalIssue-full_2");
        result.type = "periodicalIssue";
        result.iePeriodicalTitle = "title2";
        result.ieVolumeTitle = "volume2";
        result.ieIssueTitle = "issue2";
        result.ieCcnb = CCNB2;
        result.ieIssn = ISSN2;
        result.ieOtherId = "other2";
        result.ieDocumentType = "type2";
        result.ieDigitalBorn = false;
        result.iePrimaryOriginatorType = "AUTHOR";
        result.iePrimaryOriginator = "author2";
        result.ieOtherOriginator = "otherOriginator2";
        result.iePublicationPublisher = "publisher2";
        result.iePublicationPlace = "place2";
        result.iePublicationyear = 21;
        // dd
        result.ddFinanced = "financed2";
        result.ddContractNum = "contract2";
        result.techFormat = "format2";
        result.techFormatVersion = "version2";
        result.techExtent = "extent2";
        result.techResolutionHorizontal = 22;
        result.techResolutionVertical = 23;
        result.techCompressionRatio = 2.4;
        result.techCompression = "compression2";
        result.techColorModel = "model2";
        result.techColorDepth = 25;
        result.techIccProfile = "icc2";
        result.techPictureSizeWidth = 26;
        result.techPictureSizeHeight = 27;
        return result;
    }

    public static Metadata thesisMinimal() {
        Metadata result = new Metadata("thesis-minimal");
        result.type = "thesis";
        result.ieTitle = "title0";
        return result;
    }

    public static Metadata thesisFull1() {
        Metadata result = new Metadata("thesis-full_1");
        result.type = "thesis";
        result.ieTitle = "title1";
        result.ieSubTitle = "subTitle1";
        result.ieCcnb = CCNB1;
        result.ieOtherId = "other1";
        result.ieDocumentType = "type1";
        result.ieDigitalBorn = true;
        result.iePrimaryOriginatorType = "AUTHOR";
        result.iePrimaryOriginator = "author1";
        result.ieOtherOriginator = "otherOriginator1";
        result.iePublicationPublisher = "publisher1";
        result.iePublicationPlace = "place1";
        result.iePublicationyear = 11;
        result.degreeAwardingInstitution = "institution1";
        // dd
        result.ddFinanced = "financed1";
        result.ddContractNum = "contract1";
        result.techFormat = "format1";
        result.techFormatVersion = "version1";
        result.techExtent = "extent1";
        result.techResolutionHorizontal = 1;
        result.techResolutionVertical = 12;
        result.techCompressionRatio = 1.3;
        result.techCompression = "compression1";
        result.techColorModel = "model1";
        result.techColorDepth = 14;
        result.techIccProfile = "icc1";
        result.techPictureSizeWidth = 15;
        result.techPictureSizeHeight = 16;
        return result;
    }

    public static Metadata thesisFull2() {
        Metadata result = new Metadata("thesis-full_2");
        result.type = "thesis";
        result.ieTitle = "title2";
        result.ieSubTitle = "subTitle2";
        result.ieCcnb = CCNB2;
        result.ieOtherId = "other2";
        result.ieDocumentType = "type2";
        result.ieDigitalBorn = false;
        result.iePrimaryOriginatorType = "AUTHOR";
        result.iePrimaryOriginator = "author2";
        result.ieOtherOriginator = "otherOriginator2";
        result.iePublicationPublisher = "publisher2";
        result.iePublicationPlace = "place2";
        result.iePublicationyear = 21;
        result.degreeAwardingInstitution = "institution2";
        // dd
        result.ddFinanced = "financed2";
        result.ddContractNum = "contract2";
        result.techFormat = "format2";
        result.techFormatVersion = "version2";
        result.techExtent = "extent2";
        result.techResolutionHorizontal = 22;
        result.techResolutionVertical = 23;
        result.techCompressionRatio = 2.4;
        result.techCompression = "compression2";
        result.techColorModel = "model2";
        result.techColorDepth = 25;
        result.techIccProfile = "icc2";
        result.techPictureSizeWidth = 26;
        result.techPictureSizeHeight = 27;
        return result;
    }

    public static Metadata otherEntityMinimal() {
        Metadata result = new Metadata("otherEntnity-minimal");
        result.type = "otherEntity";
        result.ieTitle = "title0";
        return result;
    }

    public static Metadata otherEntityFull1() {
        Metadata result = new Metadata("otherEntnity-full_1");
        result.type = "otherEntity";
        result.ieTitle = "title1";
        result.ieSubTitle = "subTitle1";
        result.ieCcnb = CCNB1;
        result.ieIsbn = ISBN1;
        result.ieOtherId = "other1";
        result.ieDocumentType = "type1";
        result.ieDigitalBorn = true;
        result.iePrimaryOriginatorType = "AUTHOR";
        result.iePrimaryOriginator = "author1";
        result.ieOtherOriginator = "otherOriginator1";
        result.iePublicationPublisher = "publisher1";
        result.iePublicationPlace = "place1";
        result.iePublicationyear = 11;
        // dd
        result.ddFinanced = "financed1";
        result.ddContractNum = "contract1";
        result.techFormat = "format1";
        result.techFormatVersion = "version1";
        result.techExtent = "extent1";
        result.techResolutionHorizontal = 1;
        result.techResolutionVertical = 12;
        result.techCompressionRatio = 1.3;
        result.techCompression = "compression1";
        result.techColorModel = "model1";
        result.techColorDepth = 14;
        result.techIccProfile = "icc1";
        result.techPictureSizeWidth = 15;
        result.techPictureSizeHeight = 16;
        return result;
    }

    public static Metadata otherEntityFull2() {
        Metadata result = new Metadata("otherEntnity-full_2");
        result.type = "otherEntity";
        result.ieTitle = "title2";
        result.ieSubTitle = "subTitle2";
        result.ieCcnb = CCNB2;
        result.ieIsbn = ISBN2;
        result.ieOtherId = "other2";
        result.ieDocumentType = "type2";
        result.ieDigitalBorn = false;
        result.iePrimaryOriginatorType = "AUTHOR";
        result.iePrimaryOriginator = "author2";
        result.ieOtherOriginator = "otherOriginator2";
        result.iePublicationPublisher = "publisher2";
        result.iePublicationPlace = "place2";
        result.iePublicationyear = 21;
        // dd
        result.ddFinanced = "financed2";
        result.ddContractNum = "contract2";
        result.techFormat = "format2";
        result.techFormatVersion = "version2";
        result.techExtent = "extent2";
        result.techResolutionHorizontal = 22;
        result.techResolutionVertical = 23;
        result.techCompressionRatio = 2.4;
        result.techCompression = "compression2";
        result.techColorModel = "model2";
        result.techColorDepth = 25;
        result.techIccProfile = "icc2";
        result.techPictureSizeWidth = 26;
        result.techPictureSizeHeight = 27;
        return result;
    }

    public static Metadata analyticalMinimal() {
        Metadata result = new Metadata("analytical-minimal");
        result.type = "analytical";
        result.ieTitle = "title0";
        result.srcDocTitle = "srcDocTitle0";
        return result;
    }

    public static Metadata analyticalFull1() {
        Metadata result = new Metadata("analytical-full_1");
        result.type = "analytical";
        result.ieTitle = "title1";
        result.ieSubTitle = "subtitle1";
        result.ieOtherId = "other1";
        result.ieDocumentType = "type1";
        result.iePrimaryOriginatorType = "CORPORATION";
        result.iePrimaryOriginator = "corporation1";
        result.ieOtherOriginator = "otherOriginator1";
        // source doc
        result.srcDocTitle = "srcTtl1";
        result.srcDocVolumeTitle = "srcVolTtl1";
        result.srcDocIssueTitle = "srcIssueTtl1";
        result.srcDocCcnb = "cnb000000011";
        result.srcDocIsbn = ISBN1;
        result.srcDocIssn = ISSN1;
        result.srcDocOtherId = "srcId1";
        result.srcDocPubPublisher = "srcPublisher1";
        result.srcDocPubPlace = "srcPubPlace1";
        result.srcDocPubYear = 11;
        // dd
        result.ddFinanced = "financed1";
        result.ddContractNum = "contract1";
        result.techFormat = "format1";
        result.techFormatVersion = "version1";
        result.techExtent = "extent1";
        result.techResolutionHorizontal = 12;
        result.techResolutionVertical = 13;
        result.techCompressionRatio = 1.4;
        result.techCompression = "compression1";
        result.techColorModel = "model1";
        result.techColorDepth = 15;
        result.techIccProfile = "icc1";
        result.techPictureSizeWidth = 16;
        result.techPictureSizeHeight = 17;
        return result;
    }

    public static Metadata analyticalFull2() {
        Metadata result = new Metadata("analytical-full_2");
        result.type = "analytical";
        result.ieTitle = "title2";
        result.ieSubTitle = "subtitle2";
        result.ieOtherId = "other2";
        result.ieDocumentType = "type2";
        result.iePrimaryOriginatorType = "EVENT";
        result.iePrimaryOriginator = "event2";
        result.ieOtherOriginator = "otherOriginator2";
        // source doc
        result.srcDocTitle = "srcTtl2";
        result.srcDocVolumeTitle = "srcVolTtl2";
        result.srcDocIssueTitle = "srcIssueTtl2";
        result.srcDocCcnb = "cnb000000022";
        result.srcDocIsbn = ISBN2;
        result.srcDocIssn = ISSN2;
        result.srcDocOtherId = "srcId2";
        result.srcDocPubPublisher = "srcPublisher2";
        result.srcDocPubPlace = "srcPubPlace2";
        result.srcDocPubYear = 21;
        // dd
        result.ddFinanced = "financed2";
        result.ddContractNum = "contract2";
        result.techFormat = "format2";
        result.techFormatVersion = "version2";
        result.techExtent = "extent2";
        result.techResolutionHorizontal = 22;
        result.techResolutionVertical = 23;
        result.techCompressionRatio = 2.4;
        result.techCompression = "compression2";
        result.techColorModel = "model2";
        result.techColorDepth = 25;
        result.techIccProfile = "icc2";
        result.techPictureSizeWidth = 26;
        result.techPictureSizeHeight = 27;
        return result;
    }

    public Metadata fillOnlyEmptyFields(Metadata newData) {
        if (ieTitle == null) {
            ieTitle = newData.ieTitle;
        }
        if (ieSubTitle == null) {
            ieSubTitle = newData.ieSubTitle;
        }
        if (ieMonographTitle == null) {
            ieMonographTitle = newData.ieMonographTitle;
        }
        if (iePeriodicalTitle == null) {
            iePeriodicalTitle = newData.iePeriodicalTitle;
        }
        if (ieVolumeTitle == null) {
            ieVolumeTitle = newData.ieVolumeTitle;
        }
        if (ieIssueTitle == null) {
            ieIssueTitle = newData.ieIssueTitle;
        }
        if (ieCcnb == null) {
            ieCcnb = newData.ieCcnb;
        }
        if (ieIsbn == null) {
            ieIsbn = newData.ieIsbn;
        }
        if (ieIssn == null) {
            ieIssn = newData.ieIssn;
        }
        if (ieOtherId == null) {
            ieOtherId = newData.ieOtherId;
        }
        if (ieDocumentType == null) {
            ieDocumentType = newData.ieDocumentType;
        }
        // if (ieDigitalBorn == null) {
        // ieDigitalBorn = newData.ieDigitalBorn;
        // }
        if (iePrimaryOriginatorType == null) {
            iePrimaryOriginatorType = newData.iePrimaryOriginatorType;
        }
        if (iePrimaryOriginator == null) {
            iePrimaryOriginator = newData.iePrimaryOriginator;
        }
        if (ieOtherOriginator == null) {
            ieOtherOriginator = newData.ieOtherOriginator;
        }
        if (iePublicationPublisher == null) {
            iePublicationPublisher = newData.iePublicationPublisher;
        }
        if (iePublicationPlace == null) {
            iePublicationPlace = newData.iePublicationPlace;
        }
        if (iePublicationyear == null) {
            iePublicationyear = newData.iePublicationyear;
        }
        if (ddFinanced == null) {
            ddFinanced = newData.ddFinanced;
        }
        if (ddContractNum == null) {
            ddContractNum = newData.ddContractNum;
        }
        if (techFormat == null) {
            techFormat = newData.techFormat;
        }
        if (techFormatVersion == null) {
            techFormatVersion = newData.techFormatVersion;
        }
        if (techExtent == null) {
            techExtent = newData.techExtent;
        }
        if (techResolutionHorizontal == null) {
            techResolutionHorizontal = newData.techResolutionHorizontal;
        }
        if (techResolutionVertical == null) {
            techResolutionVertical = newData.techResolutionVertical;
        }
        if (techCompressionRatio == null) {
            techCompressionRatio = newData.techCompressionRatio;
        }
        if (techCompression == null) {
            techCompression = newData.techCompression;
        }
        if (techColorModel == null) {
            techColorModel = newData.techColorModel;
        }
        if (techColorDepth == null) {
            techColorDepth = newData.techColorDepth;
        }
        if (techIccProfile == null) {
            techIccProfile = newData.techIccProfile;
        }
        if (techPictureSizeWidth == null) {
            techPictureSizeWidth = newData.techPictureSizeWidth;
        }
        if (techPictureSizeHeight == null) {
            techPictureSizeHeight = newData.techPictureSizeHeight;
        }
        if (srcDocTitle == null) {
            srcDocTitle = newData.srcDocTitle;
        }
        if (srcDocVolumeTitle == null) {
            srcDocVolumeTitle = newData.srcDocVolumeTitle;
        }
        if (srcDocIssueTitle == null) {
            srcDocIssueTitle = newData.srcDocIssueTitle;
        }
        if (srcDocCcnb == null) {
            srcDocCcnb = newData.srcDocCcnb;
        }
        if (srcDocIsbn == null) {
            srcDocIsbn = newData.srcDocIsbn;
        }
        if (srcDocIssn == null) {
            srcDocIssn = newData.srcDocIssn;
        }
        if (srcDocOtherId == null) {
            srcDocOtherId = newData.srcDocOtherId;
        }
        if (srcDocPubPublisher == null) {
            srcDocPubPublisher = newData.srcDocPubPublisher;
        }
        if (srcDocPubPlace == null) {
            srcDocPubPlace = newData.srcDocPubPlace;
        }
        if (srcDocPubYear == null) {
            srcDocPubYear = newData.srcDocPubYear;
        }
        if (degreeAwardingInstitution == null) {
            degreeAwardingInstitution = newData.degreeAwardingInstitution;
        }
        return this;
    }

}
