package cz.nkp.urnnbn.oaiadapter;

import cz.nkp.urnnbn.api_client.v5.CzidloApiConnector;
import cz.nkp.urnnbn.api_client.v5.CzidloApiErrorException;
import cz.nkp.urnnbn.api_client.v5.utils.DdRegistrationRefiner;
import cz.nkp.urnnbn.api_client.v5.utils.DiImportRefiner;
import cz.nkp.urnnbn.api_client.v5.utils.XmlTools;
import cz.nkp.urnnbn.core.AccessRestriction;
import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.xml.apiv5.builders.request.DiCreateBuilderXml;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.xslt.XSLException;

import java.io.File;
import java.io.IOException;

/**
 * Created by Martin Řehánek on 30.10.17.
 */
public class SingleRecordProcessor {

    // CZIDLO API
    private final String registrarCode;
    private final CzidloApiConnector czidloConnector;
    //XSLT
    private final Document digDocRegistrationTemplate;
    private final Document digInstImportTemplate;
    //XSD
    private final XsdProvider xsdProvider;
    // DD
    private boolean registerDDsWithUrn;
    private boolean registerDDsWithoutUrn;
    // DI
    private final boolean mergeDigitalInstances;
    private final boolean ignoreDifferenceInDiAccessibility;
    private final boolean ignoreDifferenceInDiFormat;
    //helpers
    private final ReportLogger reportLogger;
    private final XmlTools xmlTools = new XmlTools();

    public SingleRecordProcessor(ReportLogger reportLogger,
                                 String registrarCode,
                                 CzidloApiConnector czidloConnector,
                                 Document digDocRegistrationTemplate, Document digInstImportTemplate,
                                 XsdProvider xsdProvider,
                                 boolean registerDDsWithUrn, boolean registerDDsWithoutUrn,
                                 boolean mergeDigitalInstances, boolean ignoreDifferenceInDiAccessibility, boolean ignoreDifferenceInDiFormat) {
        this.reportLogger = reportLogger;
        this.registrarCode = registrarCode;
        this.czidloConnector = czidloConnector;
        this.digDocRegistrationTemplate = digDocRegistrationTemplate;
        this.digInstImportTemplate = digInstImportTemplate;
        this.xsdProvider = xsdProvider;
        this.registerDDsWithUrn = registerDDsWithUrn;
        this.registerDDsWithoutUrn = registerDDsWithoutUrn;
        this.mergeDigitalInstances = mergeDigitalInstances;
        this.ignoreDifferenceInDiAccessibility = ignoreDifferenceInDiAccessibility;
        this.ignoreDifferenceInDiFormat = ignoreDifferenceInDiFormat;
    }

    private void report(String message) {
        if (reportLogger != null) {
            reportLogger.report(message);
        }
    }

    private void report(String message, Throwable e) {
        if (reportLogger != null) {
            reportLogger.report(message, e);
        }
    }

    public RecordResult processRecord(OaiRecord oaiRecord) throws SingleRecordProcessingException {
        //report("------------------------------------------------------");
        String oaiIdentifier = oaiRecord.getIdentifier();
        report("Processing next record");
        report("- OAI-record identifier: " + oaiIdentifier);
        Document digDocRegistrationData = buildAndValidateDdRegistrationData(oaiRecord, digDocRegistrationTemplate);
        Document digInstImportData = buildAndValidateDiImportData(oaiRecord, digInstImportTemplate);
        return processRecord(oaiIdentifier, digDocRegistrationData, digInstImportData);
    }

    private Document buildAndValidateDdRegistrationData(OaiRecord oaiRecord, Document digDocRegistrationTemplate) throws SingleRecordProcessingException {
        //saveToTempFile(oaiRecord.getDocument(), "oai-" + oaiRecord.getIdentifier(), ".xml");
        Document digDocRegistrationData = null;
        //transformation
        try {
            digDocRegistrationData = XmlTools.getTransformedDocument(oaiRecord.getDocument(), digDocRegistrationTemplate);
            report("- OAI-record -> Digital-document-registration data conversion: SUCCESS");
        } catch (XSLException ex) {
            throw new SingleRecordProcessingException("OAI-record -> Digital-document-registration data conversion: ERROR: " + ex.getMessage(), ex);
        }
        //refinement
        new DdRegistrationRefiner().refineDocument(digDocRegistrationData);
        report("- Digital-document-registration data refinement: SUCCESS");
        //validation
        try {
            XmlTools.validateByXsdAsString(digDocRegistrationData, xsdProvider.getDdRegistrationDataXsd());
            checkNoInternalRegistrarScopeIdFound(digDocRegistrationData);
            report("- Digital-document-registration data validation: SUCCESS");
        } catch (DocumentOperationException ex) {
            throw new SingleRecordProcessingException("Digital-document-registration data validation ERROR: " + ex.getMessage(), ex);
        } catch (ParsingException ex) {
            throw new SingleRecordProcessingException("Digital-document-registration data parsing ERROR: " + ex.getMessage(), ex);
        } catch (IOException ex) {
            throw new SingleRecordProcessingException("Digital-document-registration data processing ERROR: " + ex.getMessage(), ex);
        }
        return digDocRegistrationData;
    }

    private void checkNoInternalRegistrarScopeIdFound(Document digDocRegistrationData) throws DocumentOperationException {
        String xpath = String.format("/r:import/r:digitalDocument/r:registrarScopeIdentifiers/r:id[@type='%s']", OaiAdapter.REGISTRAR_SCOPE_ID_TYPE);
        boolean exists = XmlTools.nodeByXpathExists(digDocRegistrationData, xpath);
        if (exists) {
            throw new DocumentOperationException(String.format(
                    "found registrar-scope-id with type '%s', which is reserved for OAI Adapter and must not be used in input data",
                    OaiAdapter.REGISTRAR_SCOPE_ID_TYPE));
        }
    }

    private Document buildAndValidateDiImportData(OaiRecord oaiRecord, Document digInstImportTemplate) throws SingleRecordProcessingException {
        Document digInstImportData = null;
        //transformation
        try {
            digInstImportData = XmlTools.getTransformedDocument(oaiRecord.getDocument(), digInstImportTemplate);
            report("- OAI-record -> Digital-instance-import data conversion: SUCCESS");
        } catch (XSLException ex) {
            throw new SingleRecordProcessingException("OAI-record -> Digital-instance-import data conversion: ERROR: " + ex.getMessage(), ex);
        }
        //refinement
        new DiImportRefiner().refineDocument(digInstImportData);
        report("- Digital-instance-import data refinement: SUCCESS");
        //validation
        try {
            XmlTools.validateByXsdAsString(digInstImportData, xsdProvider.getDiImportDataXsd());
            report("- Digital-instance-import data validation: SUCCESS");
        } catch (ParsingException ex) {
            throw new SingleRecordProcessingException("Digital-instance-import data parsing ERROR: " + ex.getMessage(), ex);
        } catch (IOException ex) {
            throw new SingleRecordProcessingException("Digital-instance-import data processing ERROR: " + ex.getMessage(), ex);
        }
        return digInstImportData;
    }

    private RecordResult processRecord(String oaiIdentifier, Document digDocRegistrationData, Document digInstImportData) throws SingleRecordProcessingException {
        DdRegistrationDataHelper docHelper = new DdRegistrationDataHelper(digDocRegistrationData);
        String urnnbn = docHelper.getUrnnbnFromDocument();
        if (urnnbn == null) { //no URN:NBN in input data
            report("- Digital-document-registration data does not contain URN:NBN.");
            urnnbn = getUrnnbnByRegistrarScopeId(oaiIdentifier);
            if (urnnbn == null) { //no URN:NBN from registrar-scope-id
                report("- No digital document found for registrar-scope-id " + OaiAdapter.REGISTRAR_SCOPE_ID_TYPE + ":" + oaiIdentifier);
                return registerDdIWithoutUrnAndContinue(oaiIdentifier, digDocRegistrationData, digInstImportData);
            } else {
                report("- Digital document found for registrar-scope-id " + OaiAdapter.REGISTRAR_SCOPE_ID_TYPE + ":" + oaiIdentifier + " with " + urnnbn);
                return checkUrnNbnStateAndContinue(urnnbn, oaiIdentifier, digDocRegistrationData, digInstImportData);
            }
        } else { //found URN:NBN in input data
            report("- Digital-document-registration data contains URN:NBN: " + urnnbn);
            return checkUrnNbnStateAndContinue(urnnbn, oaiIdentifier, digDocRegistrationData, digInstImportData);
        }
    }

    private String getUrnnbnByRegistrarScopeId(String oaiIdentifier) throws SingleRecordProcessingException {
        try {
            return czidloConnector.getUrnnbnByRegistrarScopeId(registrarCode, OaiAdapter.REGISTRAR_SCOPE_ID_TYPE, oaiIdentifier);
        } catch (ParsingException | IOException | CzidloApiErrorException e) {
            throw new SingleRecordProcessingException("Getting URN:NBN by registrar-scope-id ERROR: " + e.getMessage(), e);
        }
    }

    private RecordResult checkUrnNbnStateAndContinue(String urnnbn, String oaiIdentifier, Document digDocRegistrationData, Document digInstImportData) throws SingleRecordProcessingException {
        UrnNbnWithStatus.Status urnnbnStatus = getUrnNbnStatus(urnnbn);
        report("- URN:NBN status: " + urnnbnStatus);
        switch (urnnbnStatus) {
            case DEACTIVATED:
                return new RecordResult(urnnbn, RecordResult.DigitalDocumentStatus.IS_DEACTIVATED, null);
            case RESERVED:
            case FREE:
                return registerDdIWithUrnAndContinue(oaiIdentifier, urnnbn, digDocRegistrationData, digInstImportData);
            case ACTIVE:
                String urnnbnByRegistrarScopeId = getUrnnbnByRegistrarScopeId(oaiIdentifier);
                if (urnnbnByRegistrarScopeId == null) {
                    report("- URN:NBN by registrar-scope-id: NOT FOUND");
                    if (urnMatchesRegistrar(urnnbn, registrarCode)) {
                        setRegistrarScopeId(urnnbn, oaiIdentifier);
                        report("- Setting registrar-scope-id " + OaiAdapter.REGISTRAR_SCOPE_ID_TYPE + ":" + oaiIdentifier + ": SUCCESS");
                    } else {
                        report("- Not setting registrar-scope-id because digital-document with " + urnnbn + " does not belong to registrar " + registrarCode);
                    }
                    return processDigitalInstance(urnnbn, digInstImportData, RecordResult.DigitalDocumentStatus.REGISTERED_ALREADY);
                } else {
                    report("- URN:NBN by registrar-scope-id: FOUND");
                    if (!urnnbn.equals(urnnbnByRegistrarScopeId)) {
                        throw new SingleRecordProcessingException(urnnbn + " (from input data) does not match " + urnnbnByRegistrarScopeId + " (from registrar-scope-id " + OaiAdapter.REGISTRAR_SCOPE_ID_TYPE + ": " + oaiIdentifier + ")");
                    } else {
                        return processDigitalInstance(urnnbn, digInstImportData, RecordResult.DigitalDocumentStatus.REGISTERED_ALREADY);
                    }
                }
            default:
                throw new IllegalStateException();
        }
    }

    private boolean urnMatchesRegistrar(String urnnbn, String registrarCode) {
        return UrnNbn.valueOf(urnnbn).getRegistrarCode().equals(RegistrarCode.valueOf(registrarCode));
    }

    private UrnNbnWithStatus.Status getUrnNbnStatus(String urnnbn) throws SingleRecordProcessingException {
        try {
            return czidloConnector.getUrnnbnStatus(urnnbn);
        } catch (ParsingException | CzidloApiErrorException | IOException e) {
            throw new SingleRecordProcessingException("Getting URN:NBN status ERROR: " + e.getMessage(), e);
        }
    }

    private RecordResult registerDdIWithUrnAndContinue(String oaiIdentifier, String urnNbn, Document digDocRegistrationData, Document digInstImportData) throws SingleRecordProcessingException {
        if (registerDDsWithUrn) {
            if (urnMatchesRegistrar(urnNbn, registrarCode)) {
                urnNbn = registerDigitalDocument(digDocRegistrationData);
                report("- Digital document registered with " + urnNbn);
                report("- Setting registrar-scope-id " + OaiAdapter.REGISTRAR_SCOPE_ID_TYPE + ": " + oaiIdentifier + " to DD with " + urnNbn + ": SUCCESS");
                setRegistrarScopeId(urnNbn, oaiIdentifier);
                return processDigitalInstance(urnNbn, digInstImportData, RecordResult.DigitalDocumentStatus.REGISTERED_NOW);
            } else {
                report("- Digital document is not being registered with " + urnNbn + " now because registrar code is not " + registrarCode);
                return new RecordResult(urnNbn, RecordResult.DigitalDocumentStatus.NOT_REGISTERED, null);
            }
        } else {
            report("- Digital document will not be registered");
            return new RecordResult(urnNbn, RecordResult.DigitalDocumentStatus.NOT_REGISTERED, null);
        }
    }

    private RecordResult registerDdIWithoutUrnAndContinue(String oaiIdentifier, Document digDocRegistrationData, Document digInstImportData) throws SingleRecordProcessingException {
        if (registerDDsWithoutUrn) {
            String urnNbn = registerDigitalDocument(digDocRegistrationData);
            report("- Digital document registered with " + urnNbn);
            report("- Setting registrar-scope-id " + OaiAdapter.REGISTRAR_SCOPE_ID_TYPE + ": " + oaiIdentifier + " to DD with " + urnNbn + ": SUCCESS");
            setRegistrarScopeId(urnNbn, oaiIdentifier);
            return processDigitalInstance(urnNbn, digInstImportData, RecordResult.DigitalDocumentStatus.REGISTERED_NOW);
        } else {
            report("- Digital document will not be registered");
            return new RecordResult(null, RecordResult.DigitalDocumentStatus.NOT_REGISTERED, null);
        }
    }

    private void setRegistrarScopeId(String urnNbn, String oaiIdentifier) throws SingleRecordProcessingException {
        try {
            czidloConnector.putRegistrarScopeIdentifier(urnNbn, OaiAdapter.REGISTRAR_SCOPE_ID_TYPE, oaiIdentifier);
        } catch (CzidloApiErrorException | IOException e) {
            throw new SingleRecordProcessingException("Setting registrar-scope-id: ERROR: " + e.getMessage(), e);
        }
    }

    private RecordResult processDigitalInstance(String urnnbn, Document diImportData, RecordResult.DigitalDocumentStatus ddStatus) throws SingleRecordProcessingException {
        DigitalInstance newDi = xmlTools.buildDiFromImportDigitalInstanceRequest(diImportData);
        DigitalInstance currentDi = getActiveDigitalInstance(urnnbn, newDi.getLibraryId());
        if (currentDi == null) {
            // DI doesnt exist yet
            report("- DI doesn't exists - creating new DI ...");
            // import DI
            importDigitalInstance(diImportData, urnnbn);
            report("- New DI created.");
            return new RecordResult(urnnbn, ddStatus, RecordResult.DigitalInstanceStatus.IMPORTED);
        } else {
            // DI already exist
            if (!equals(currentDi, newDi, ignoreDifferenceInDiFormat, ignoreDifferenceInDiAccessibility)) {
                // current DI is different from new DI
                report("- DI already exists and is considered different from new DI.");
                report("- Current DI: " + currentDi.toString());
                report("- New DI: " + newDi.toString());
                if (mergeDigitalInstances) {
                    DigitalInstance mergedDi = merge(newDi, currentDi);
                    diImportData = new Document(new DiCreateBuilderXml(mergedDi).buildRootElement());
                    report("- Merged DI: " + mergedDi.toString());
                }
                // deactivate current DI
                report("- Deactivating current DI");
                deactivateDigitalInstance(currentDi);
                report("- Current DI deactivated.");
                // import new (possibly merged) DI
                if (mergeDigitalInstances) {
                    report("- Creating another DI (from new DI merged with old DI) ...");
                } else {
                    report("- Creating another DI (from new DI) ...");
                }
                importDigitalInstance(diImportData, urnnbn);
                report("- Another DI created.");
                return new RecordResult(urnnbn, ddStatus, RecordResult.DigitalInstanceStatus.UPDATED);
            } else {
                // no change - do nothing
                report("- DI already exists and is not considered different from new DI: IGNORING");
                return new RecordResult(urnnbn, ddStatus, RecordResult.DigitalInstanceStatus.UNCHANGED);
            }
        }
    }

    private void deactivateDigitalInstance(DigitalInstance currentDi) throws SingleRecordProcessingException {
        try {
            czidloConnector.deactivateDigitalInstance(currentDi.getId());
        } catch (CzidloApiErrorException | IOException e) {
            throw new SingleRecordProcessingException("Deactivating digital instance: ERROR: " + e.getMessage(), e);
        }
    }

    private DigitalInstance getActiveDigitalInstance(String urnnbn, Long libraryId) throws SingleRecordProcessingException {
        try {
            return czidloConnector.getActiveDigitalInstanceByUrnnbnAndLibraryId(urnnbn, libraryId);
        } catch (IOException | ParsingException | CzidloApiErrorException e) {
            throw new SingleRecordProcessingException("Getting digital instance: ERROR: " + e.getMessage(), e);
        }
    }

    private String registerDigitalDocument(Document digDocRegistrationData) throws SingleRecordProcessingException {
        try {
            String urnnbn = czidloConnector.registerDigitalDocument(digDocRegistrationData, registrarCode);
            report("- Digital-document-registration SUCCESS");
            return urnnbn;
        } catch (IOException | ParsingException | CzidloApiErrorException ex) {
            throw new SingleRecordProcessingException("Digital-document-registration: ERROR: " + ex.getMessage(), ex);
        }
    }

    private void importDigitalInstance(Document diImportData, String urnnbn) throws SingleRecordProcessingException {
        try {
            czidloConnector.importDigitalInstance(diImportData, urnnbn);
        } catch (IOException | CzidloApiErrorException ex) {
            throw new SingleRecordProcessingException("Digital-instance-import: ERROR: " + ex.getMessage(), ex);
        }
    }

    private DigitalInstance merge(DigitalInstance newDi, DigitalInstance currentDi) {
        DigitalInstance merged = new DigitalInstance();
        merged.setLibraryId(newDi.getLibraryId());
        merged.setUrl(newDi.getUrl());
        //format
        if (newDi.getAccessibility() == null || newDi.getAccessibility().isEmpty()) {
            merged.setAccessibility(currentDi.getAccessibility());
        } else {
            merged.setAccessibility(newDi.getAccessibility());
        }
        //accessiblity
        if (newDi.getFormat() == null || newDi.getFormat().isEmpty()) {
            merged.setFormat(currentDi.getFormat());
        } else {
            merged.setFormat(newDi.getFormat());
        }
        //access restriction
        if (newDi.getAccessRestriction() == null || newDi.getAccessRestriction() == AccessRestriction.UNKNOWN) {
            merged.setAccessRestriction(currentDi.getAccessRestriction());
        } else {
            merged.setAccessRestriction(newDi.getAccessRestriction());
        }
        return merged;
    }

    private boolean equals(DigitalInstance currentDi, DigitalInstance newDi, boolean ignoreDifferentDiFormat, boolean ignoreDifferentDiAccessibility) {
        if (!equals(currentDi.getUrl(), newDi.getUrl())) {
            return false;
        }
        if (!equals(currentDi.getAccessRestriction(), newDi.getAccessRestriction())) {
            return false;
        }
        if (!ignoreDifferentDiFormat && !equals(currentDi.getFormat(), newDi.getFormat())) {
            return false;
        }
        if (!ignoreDifferentDiAccessibility && !equals(currentDi.getAccessibility(), newDi.getAccessibility())) {
            return false;
        }
        return true;
    }

    private boolean equals(Object first, Object second) {
        if (first == null && second == null) {
            return true;
        } else if (first != null && second != null && first.equals(second)) {
            return true;
        } else {
            return false;
        }
    }

    private File saveToTempFile(Document document, String prefix, String suffix) throws IOException {
        File tmpFile = File.createTempFile(prefix, suffix);
        XmlTools.saveDocumentToFile(document, tmpFile.getAbsolutePath());
        return tmpFile;
    }

}
