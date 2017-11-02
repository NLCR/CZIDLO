package cz.nkp.urnnbn.oaiadapter;

import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.oaiadapter.czidlo.CzidloApiConnector;
import cz.nkp.urnnbn.oaiadapter.czidlo.CzidloApiErrorException;
import cz.nkp.urnnbn.oaiadapter.czidlo.UrnnbnStatus;
import cz.nkp.urnnbn.oaiadapter.utils.*;
import cz.nkp.urnnbn.xml.apiv4.builders.request.DiCreateBuilderXml;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.xslt.XSLException;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Martin Řehánek on 30.10.17.
 */
public class SingleRecordProcessor {

    // TODO: 30.10.17 note very clean to reference OaiAdapter just because of ReportLogger
    private final OaiAdapter oaiAdapter;
    // CZIDLO API
    private final String registrarCode;
    private final CzidloApiConnector czidloConnector;
    //XSLT
    private final Document digDocRegistrationTemplate;
    private final Document digInstImportTemplate;
    //XSD
    private final XsdProvider xsdProvider;
    // DD
    private final boolean registerDigitalDocuments;
    // DI
    private final boolean mergeDigitalInstances;
    private final boolean ignoreDifferenceInDiAccessibility;
    private final boolean ignoreDifferenceInDiFormat;

    public SingleRecordProcessor(OaiAdapter oaiAdapter, String registrarCode, CzidloApiConnector czidloConnector, Document digDocRegistrationTemplate, Document digInstImportTemplate, XsdProvider xsdProvider, boolean registerDigitalDocuments, boolean mergeDigitalInstances, boolean ignoreDifferenceInDiAccessibility, boolean ignoreDifferenceInDiFormat) {
        this.oaiAdapter = oaiAdapter;
        this.registrarCode = registrarCode;
        this.czidloConnector = czidloConnector;
        this.digDocRegistrationTemplate = digDocRegistrationTemplate;
        this.digInstImportTemplate = digInstImportTemplate;
        this.xsdProvider = xsdProvider;
        this.registerDigitalDocuments = registerDigitalDocuments;
        this.mergeDigitalInstances = mergeDigitalInstances;
        this.ignoreDifferenceInDiAccessibility = ignoreDifferenceInDiAccessibility;
        this.ignoreDifferenceInDiFormat = ignoreDifferenceInDiFormat;
    }

    private void report(String message) {
        oaiAdapter.report(message);
    }

    private void report(String message, Throwable e) {
        oaiAdapter.report(message, e);
    }

    public RecordResult processRecord(OaiRecord oaiRecord)
            throws OaiAdapterException {
        report("------------------------------------------------------");
        String oaiIdentifier = oaiRecord.getIdentifier();
        report("Processing next record");
        report("- OAI-record identifier: " + oaiIdentifier);
        Document digDocRegistrationData = buildAndValidateDdRegistrationData(oaiRecord, digDocRegistrationTemplate);
        Document digInstImportData = buildAndValidateDiImportData(oaiRecord, digInstImportTemplate);
        try {
            RecordResult recordResult = processRecord(oaiIdentifier, digDocRegistrationData, digInstImportData);
            return recordResult;
        }
        /*catch (CzidloConnectionException ex) {
            throw new OaiAdapterException("Czidlo API error:", ex);
        }*/
        catch (IOException e) {
            throw new OaiAdapterException("IOException:", e);
        } catch (ParsingException e) {
            throw new OaiAdapterException("ParsingException:", e);
        } catch (CzidloApiErrorException e) {
            throw new OaiAdapterException("CzidloApiErrorException:", e);
        }
    }

    private Document buildAndValidateDdRegistrationData(OaiRecord oaiRecord, Document digDocRegistrationTemplate) throws OaiAdapterException {
        //saveToTempFile(oaiRecord.getDocument(), "oai-" + oaiRecord.getIdentifier(), ".xml");
        Document digDocRegistrationData = null;
        //transformation
        try {
            digDocRegistrationData = XmlTools.getTransformedDocument(oaiRecord.getDocument(), digDocRegistrationTemplate);
            report("- OAI-record -> Digital-document-registration data conversion: SUCCESS");
        } catch (XSLException ex) {
            throw new OaiAdapterException("OAI-record -> Digital-document-registration data conversion: ERROR: ", ex);
        }
        //refinement
        try {
            new DdRegistrationRefiner().refineDocument(digDocRegistrationData);
            report("- Digital-document-registration data refinement: SUCCESS");
        } catch (DocumentOperationException ex) {
            throw new OaiAdapterException("Digital-document-registration data refinement: ERROR: ", ex);
        }
        //validation
        try {
            XmlTools.validateByXsdAsString(digDocRegistrationData, xsdProvider.getDigitalDocumentRegistrationDataXsd());
            checkNoInternalRegistrarScopeIdFound(digDocRegistrationData);
            report("- Digital-document-registration data validation: SUCCESS");
        } catch (DocumentOperationException ex) {
            throw new OaiAdapterException("Digital-document-registration data validation ERROR: ", ex);
        }
        return digDocRegistrationData;
    }

    private void checkNoInternalRegistrarScopeIdFound(Document digDocRegistrationData) throws DocumentOperationException {
        try {
            String xpath = String.format("/r:import/r:digitalDocument/r:registrarScopeIdentifiers/r:id[@type='%s']", OaiAdapter.REGISTAR_SCOPE_ID_TYPE);
            boolean exists = XmlTools.nodeByXpathExists(digDocRegistrationData, xpath);
            if (exists) {
                throw new DocumentOperationException(String.format(
                        "found registrar-scope-id with type '%s', which is reserved for OAI Adapter and must not be used in input data",
                        OaiAdapter.REGISTAR_SCOPE_ID_TYPE));
            }
        } catch (Throwable ex) {
            throw new DocumentOperationException(ex.getMessage());
        }
    }

    private Document buildAndValidateDiImportData(OaiRecord oaiRecord, Document digInstImportTemplate) throws OaiAdapterException {
        Document digInstImportData = null;
        //transformation
        try {
            digInstImportData = XmlTools.getTransformedDocument(oaiRecord.getDocument(), digInstImportTemplate);
            report("- OAI-record -> Digital-instance-import data conversion: SUCCESS");
        } catch (XSLException ex) {
            throw new OaiAdapterException("OAI-record -> Digital-instance-import data conversion: ERROR", ex);
        }
        //refinement
        try {
            new DiImportRefiner().refineDocument(digInstImportData);
            report("- Digital-instance-import data refinement: SUCCESS");
        } catch (DocumentOperationException ex) {
            throw new OaiAdapterException("Digital-instance-import data refinement: ERROR: ", ex);
        }
        //validation
        try {
            XmlTools.validateByXsdAsString(digInstImportData, xsdProvider.getDigitalInstanceImportDataXsd());
            report("- Digital-instance-import data validation: SUCCESS");
        } catch (DocumentOperationException ex) {
            throw new OaiAdapterException("Digital-instance-import data validation: ERROR: ", ex);
        }
        return digInstImportData;
    }

    private RecordResult processRecord(String oaiIdentifier, Document digDocRegistrationData, Document digInstImportData)
            throws OaiAdapterException, IOException, ParsingException, CzidloApiErrorException {
        DdRegistrationDataHelper docHelper = new DdRegistrationDataHelper(digDocRegistrationData);
        String urnnbn = docHelper.getUrnnbnFromDocument();
        if (urnnbn == null) { //no URN:NBN in input data
            report("- Digital-document-registration data does not contain URN:NBN.");
            urnnbn = czidloConnector.getUrnnbnByRegistrarScopeId(registrarCode, OaiAdapter.REGISTAR_SCOPE_ID_TYPE, oaiIdentifier);
            if (urnnbn == null) { //no URN:NBN from registrar-scope-id
                report("- No digital document found for registrar-scope-id " + OaiAdapter.REGISTAR_SCOPE_ID_TYPE + ":" + oaiIdentifier);
                return registerDdIfEnabledAndContinue(oaiIdentifier, null, digDocRegistrationData, digInstImportData);
            } else {
                report("- Digital document found for registrar-scope-id " + OaiAdapter.REGISTAR_SCOPE_ID_TYPE + ":" + oaiIdentifier + " with " + urnnbn);
                return checkUrnNbnStateAndContinue(urnnbn, oaiIdentifier, digDocRegistrationData, digInstImportData);
            }
        } else { //found URN:NBN in input data
            report("- Digital-document-registration data does contain URN:NBN " + urnnbn);
            return checkUrnNbnStateAndContinue(urnnbn, oaiIdentifier, digDocRegistrationData, digInstImportData);
        }
    }

    private RecordResult checkUrnNbnStateAndContinue(String urnnbn, String oaiIdentifier, Document digDocRegistrationData, Document digInstImportData) throws OaiAdapterException, IOException, ParsingException, CzidloApiErrorException {
        UrnnbnStatus urnnbnStatus = czidloConnector.getUrnnbnStatus(urnnbn);
        report("- URN:NBN status: " + urnnbnStatus);
        switch (urnnbnStatus) {
            case DEACTIVATED:
                return new RecordResult(urnnbn, RecordResult.DigitalDocumentStatus.IS_DEACTIVATED, null);
            case UNDEFINED:
                throw new OaiAdapterException("Checking URN:NBN status failed");
            case RESERVED:
            case FREE:
                return registerDdIfEnabledAndContinue(oaiIdentifier, urnnbn, digDocRegistrationData, digInstImportData);
            case ACTIVE:
                String urnnbnByRegistrarScopeId = czidloConnector.getUrnnbnByRegistrarScopeId(registrarCode, OaiAdapter.REGISTAR_SCOPE_ID_TYPE, oaiIdentifier);
                if (urnnbnByRegistrarScopeId == null) {
                    report("- URN:NBN by registrar-scope-id not found");
                    czidloConnector.putRegistrarScopeIdentifier(urnnbn, OaiAdapter.REGISTAR_SCOPE_ID_TYPE, oaiIdentifier);
                    report("- Inserting registrar-scope-id " + OaiAdapter.REGISTAR_SCOPE_ID_TYPE + ": " + oaiIdentifier + " to DD with " + urnnbn + ": SUCCESS");
                    return processDigitalInstance(urnnbn, digInstImportData, RecordResult.DigitalDocumentStatus.ALREADY_REGISTERED);
                } else {
                    if (!urnnbn.equals(urnnbnByRegistrarScopeId)) {
                        // TODO: 1.11.17 a neposilat nejak ten stav? protoze tady vim, ze ALREADY_REGISTERED
                        // nebo mozna jenom warning, nebo pokus o napravu
                        throw new OaiAdapterException(urnnbn + " (from input data) does not match " + urnnbnByRegistrarScopeId + " (from registrar-scope-id " + OaiAdapter.REGISTAR_SCOPE_ID_TYPE + ": " + oaiIdentifier + ")");
                    } else {
                        return processDigitalInstance(urnnbn, digInstImportData, RecordResult.DigitalDocumentStatus.ALREADY_REGISTERED);
                    }
                }
            default:
                throw new IllegalStateException();
        }
    }


    private RecordResult registerDdIfEnabledAndContinue(String oaiIdentifier, String urnNbn, Document digDocRegistrationData, Document digInstImportData) throws OaiAdapterException, IOException, CzidloApiErrorException {
        if (registerDigitalDocuments) {
            // TODO: 1.11.17 poresit chyby
            urnNbn = registerDigitalDocument(digDocRegistrationData);
            report("- Digital document registered with " + urnNbn);
            czidloConnector.putRegistrarScopeIdentifier(urnNbn, OaiAdapter.REGISTAR_SCOPE_ID_TYPE, oaiIdentifier);
            report("- Inserting registrar-scope-id " + OaiAdapter.REGISTAR_SCOPE_ID_TYPE + ": " + oaiIdentifier + " to DD with " + urnNbn + ": SUCCESS");
            return processDigitalInstance(urnNbn, digInstImportData, RecordResult.DigitalDocumentStatus.NOW_REGISTERED);
        } else {
            report("- Digital document will not be registered");
            return new RecordResult(urnNbn, RecordResult.DigitalDocumentStatus.NOT_REGISTERED, null);
        }
    }


    private RecordResult processDigitalInstance(String urnnbn, Document diImportData, RecordResult.DigitalDocumentStatus ddStatus)
            throws OaiAdapterException, CzidloApiErrorException, IOException {
        DigitalInstance newDi = DiBuilder.buildDiFromImportDigitalInstanceRequest(diImportData);
        //report(diImportData.toXML());
        DigitalInstance currentDi = null;
        try {
            //currentDi = czidloConnector.getDigitalInstanceByLibraryId(urnnbn, newDi);
            currentDi = czidloConnector.getActiveDigitalInstanceByUrnnbnAndLibraryId(urnnbn, newDi.getLibraryId());
        } catch (IOException ex) {
            Logger.getLogger(OaiAdapter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParsingException ex) {
            Logger.getLogger(OaiAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
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
                report("- Deactivating current DI ...");
                czidloConnector.deactivateDigitalInstance(currentDi.getId());
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
                report("- DI already exists and is not considered different from new DI - doing nothing.");
                return new RecordResult(urnnbn, ddStatus, RecordResult.DigitalInstanceStatus.UNCHANGED);
            }
        }
    }

    private String registerDigitalDocument(Document digDocRegistrationData) throws OaiAdapterException, CzidloApiErrorException {
        try {
            String urnnbn = czidloConnector.registerDigitalDocument(digDocRegistrationData, registrarCode);
            report("- Digital-document-registration SUCCESS");
            return urnnbn;
        } catch (IOException ex) {
            throw new OaiAdapterException("Digital-document-registration ERROR: IOException: ", ex);
        } catch (ParsingException ex) {
            throw new OaiAdapterException("Digital-document-registration ERROR: ParsingException: ", ex);
        }
        /*catch (CzidloConnectionException ex) {
            throw new OaiAdapterException("Digital-document-registration ERROR: CzidloConnectionException: ", ex);
        }*/
    }

    private void importDigitalInstance(Document diImportData, String urnnbn) throws OaiAdapterException, CzidloApiErrorException {
        try {
            czidloConnector.importDigitalInstance(diImportData, urnnbn);
        } catch (IOException ex) {
            throw new OaiAdapterException("Digital-instance-import ERROR: IOException: ", ex);
        }
        /*catch (ParsingException ex) {
            throw new OaiAdapterException("Digital-instance-import ERROR: ParsingException: ", ex);
        } catch (CzidloConnectionException ex) {
            throw new OaiAdapterException("Digital-instance-import ERROR: CzidloConnectionException: ", ex);
        }*/
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
        return merged;
    }

    private boolean equals(DigitalInstance currentDi, DigitalInstance newDi, boolean ignoreDifferentDiFormat, boolean ignoreDifferentDiAccessibility) {
        if (!equals(currentDi.getUrl(), newDi.getUrl())) {
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
