package cz.nkp.urnnbn.oaiadapter;

import cz.nkp.urnnbn.core.UrnNbnRegistrationMode;
import cz.nkp.urnnbn.core.UrnNbnWithStatus;
import cz.nkp.urnnbn.core.dto.DigitalInstance;
import cz.nkp.urnnbn.oaiadapter.czidlo.CzidloApiConnector;
import cz.nkp.urnnbn.oaiadapter.czidlo.CzidloConnectionException;
import cz.nkp.urnnbn.oaiadapter.czidlo.UrnnbnStatus;
import cz.nkp.urnnbn.oaiadapter.utils.DdRegistrationDataHelper;
import cz.nkp.urnnbn.oaiadapter.utils.DdRegistrationRefiner;
import cz.nkp.urnnbn.oaiadapter.utils.DiBuilder;
import cz.nkp.urnnbn.oaiadapter.utils.XmlTools;
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
    private final UrnNbnRegistrationMode registrationMode;
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

    public SingleRecordProcessor(OaiAdapter oaiAdapter, String registrarCode, UrnNbnRegistrationMode registrationMode, CzidloApiConnector czidloConnector, Document digDocRegistrationTemplate, Document digInstImportTemplate, XsdProvider xsdProvider, boolean registerDigitalDocuments, boolean mergeDigitalInstances, boolean ignoreDifferenceInDiAccessibility, boolean ignoreDifferenceInDiFormat) {
        this.oaiAdapter = oaiAdapter;
        this.registrarCode = registrarCode;
        this.registrationMode = registrationMode;
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
        report("Processing next record - identifier: " + oaiIdentifier);
        Document digDocRegistrationData = buildAndValidateDdRegistrationData(oaiRecord, digDocRegistrationTemplate);
        Document digInstImportData = buildAndValidateDiImportData(oaiRecord, digInstImportTemplate);
        try {
            RecordResult recordResult = processRecord(oaiIdentifier, digDocRegistrationData, digInstImportData);
            return recordResult;
        } catch (CzidloConnectionException ex) {
            throw new OaiAdapterException("Czidlo API error:", ex);
        }
    }

    private Document buildAndValidateDdRegistrationData(OaiRecord oaiRecord, Document digDocRegistrationTemplate) throws OaiAdapterException {
        //saveToTempFile(oaiRecord.getDocument(), "oai-" + oaiRecord.getIdentifier(), ".xml");
        Document digDocRegistrationData = null;
        //transformation
        try {
            digDocRegistrationData = XmlTools.getTransformedDocument(oaiRecord.getDocument(), digDocRegistrationTemplate);
            report("- OAI record successfuly transformed into Digital-document-registration data - continuing.");
        } catch (XSLException ex) {
            throw new OaiAdapterException("XSLException occurred when transforming record into Digital-document-registration data:", ex);
        }
        //refinement
        try {
            new DdRegistrationRefiner().refineDocument(digDocRegistrationData);
        } catch (DocumentOperationException ex) {
            throw new OaiAdapterException("Error in Digital-document-registration data refinement:", ex);
        }
        //validation
        try {
            XmlTools.validateByXsdAsString(digDocRegistrationData, xsdProvider.getDigitalDocumentRegistrationDataXsd());
            checkNoInternalRegistrarScopeIdFound(digDocRegistrationData);
            report("- Digital-document-registration data validation successful - continuing.");
        } catch (DocumentOperationException ex) {
            throw new OaiAdapterException("Digital-document-registration data invalid:", ex);
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
        try {
            digInstImportData = XmlTools.getTransformedDocument(oaiRecord.getDocument(), digInstImportTemplate);
            report("- OAI record successfuly transformed to digital-instance-import data - continuing.");
            // File tmpFile = saveToTempFile(digInstImportData, "digitalInstance-" +
            // oaiRecord.getIdentifier(),
            // ".xml");
        } catch (XSLException ex) {
            throw new OaiAdapterException("XSLException occurred when transforming record into digital-instance-import data:", ex);
        }
        try {
            XmlTools.validateByXsdAsString(digInstImportData, xsdProvider.getDigitalInstanceImportDataXsd());
            report("- Digital-instance-import data validation successful - continuing.");
            // File tmpFile = saveToTempFile(digitalInstanceDocument, "digitalInstance-" +
            // record.getIdentifier(),
            // ".xml");
        } catch (DocumentOperationException ex) {
            throw new OaiAdapterException("Digital-instance-import data invalid:", ex);
        }
        return digInstImportData;
    }

    private RecordResult processRecord(String oaiIdentifier, Document digDocRegistrationData, Document digInstImportData)
            throws OaiAdapterException, CzidloConnectionException {
        DdRegistrationDataHelper docHelper = new DdRegistrationDataHelper(digDocRegistrationData);
        docHelper.putRegistrarScopeIdentifier(oaiIdentifier);
        String urnnbn = docHelper.getUrnnbnFromDocument();
        if (urnnbn == null) {
            if (registrationMode == UrnNbnRegistrationMode.BY_RESOLVER) {
                urnnbn = czidloConnector.getUrnnbnByRegistrarScopeId(registrarCode, OaiAdapter.REGISTAR_SCOPE_ID_TYPE, oaiIdentifier);
                if (urnnbn == null) {
                    urnnbn = registerDigitalDocument(digDocRegistrationData, oaiIdentifier);
                    return processDigitalInstance(urnnbn, oaiIdentifier, digInstImportData, RecordResult.DigitalDocumentStatus.NOW_REGISTERED);
                } else {
                    throw new OaiAdapterException(String.format("Cannot find urn:nbn by registrar-scope id %1 -> %2!",
                            OaiAdapter.REGISTAR_SCOPE_ID_TYPE, oaiIdentifier));
                }
            } else {
                throw new OaiAdapterException(String.format("Incorrect mode - document doesn't contain URN:NBN and mode is not %s!",
                        UrnNbnRegistrationMode.BY_RESOLVER));
            }
        } else {
            if (registrationMode == UrnNbnRegistrationMode.BY_RESOLVER) {
                throw new OaiAdapterException(String.format("Incorrect mode - document contains URN:NBN and mode is %s!",
                        UrnNbnRegistrationMode.BY_RESOLVER));
            }
            UrnnbnStatus urnnbnStatus = czidloConnector.getUrnnbnStatus(urnnbn);
            report("- " + urnnbn);
            report("- URN:NBN status: " + urnnbnStatus);
            switch (urnnbnStatus) {
                case RESERVED:
                    if (registrationMode != UrnNbnRegistrationMode.BY_RESERVATION) {
                        throw new OaiAdapterException(String.format("Incorrect mode - URN:NBN has status %s and mode is not %s!", UrnNbnWithStatus.Status.RESERVED,
                                UrnNbnRegistrationMode.BY_RESERVATION));
                    } else {
                        registerDigitalDocument(digDocRegistrationData, oaiIdentifier);
                        return processDigitalInstance(urnnbn, oaiIdentifier, digInstImportData, RecordResult.DigitalDocumentStatus.NOW_REGISTERED);
                    }
                case FREE:
                    if (registrationMode != UrnNbnRegistrationMode.BY_REGISTRAR) {
                        throw new OaiAdapterException(String.format("Incorrect mode - URN:NBN has status %s and mode is not %s!", UrnNbnWithStatus.Status.FREE,
                                UrnNbnRegistrationMode.BY_REGISTRAR));
                    } else {
                        registerDigitalDocument(digDocRegistrationData, oaiIdentifier);
                        return processDigitalInstance(urnnbn, oaiIdentifier, digInstImportData, RecordResult.DigitalDocumentStatus.NOW_REGISTERED);
                    }
                case ACTIVE:
                    String urnnbnByRegistrarScopeId = czidloConnector.getUrnnbnByRegistrarScopeId(registrarCode, OaiAdapter.REGISTAR_SCOPE_ID_TYPE,
                            oaiIdentifier);
                    if (urnnbnByRegistrarScopeId != null && !urnnbn.equals(urnnbnByRegistrarScopeId)) {
                        throw new OaiAdapterException(String.format(
                                "URN:NBN in digital-document-registration data (%s) doesn't match URN:NBN obtained by OAI_ADAPTER ID (%s)!", urnnbn,
                                urnnbnByRegistrarScopeId));
                    } else {
                        return processDigitalInstance(urnnbn, oaiIdentifier, digInstImportData, RecordResult.DigitalDocumentStatus.ALREADY_REGISTERED);
                    }
                case DEACTIVATED:
                    return new RecordResult(urnnbn, RecordResult.DigitalDocumentStatus.IS_DEACTIVATED, null);
                case UNDEFINED:
                    throw new OaiAdapterException("Checking URN:NBN status failed");
                default:
                    throw new IllegalStateException();
            }
        }
    }

    private RecordResult processDigitalInstance(String urnnbn, String oaiIdentifier, Document diImportData, RecordResult.DigitalDocumentStatus ddStatus)
            throws OaiAdapterException, CzidloConnectionException {
        DigitalInstance newDi = DiBuilder.buildDiFromImportDigitalInstanceRequest(diImportData);
        //report(diImportData.toXML());
        DigitalInstance currentDi = null;
        try {
            currentDi = czidloConnector.getDigitalInstanceByLibraryId(urnnbn, newDi);
        } catch (IOException ex) {
            Logger.getLogger(OaiAdapter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParsingException ex) {
            Logger.getLogger(OaiAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (currentDi == null) {
            // DI doesnt exist yet
            report("- DI doesn't exists - creating new DI ...");
            // import DI
            importDigitalInstance(diImportData, urnnbn, oaiIdentifier);
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
                importDigitalInstance(diImportData, urnnbn, oaiIdentifier);
                report("- Another DI created.");
                return new RecordResult(urnnbn, ddStatus, RecordResult.DigitalInstanceStatus.UPDATED);
            } else {
                // no change - do nothing
                report("- DI already exists and is not considered different from new DI - doing nothing.");
                return new RecordResult(urnnbn, ddStatus, RecordResult.DigitalInstanceStatus.UNCHANGED);
            }
        }
    }

    private String registerDigitalDocument(Document digDocRegistrationData, String oaiIdentifier) throws OaiAdapterException {
        try {
            String urnnbn = czidloConnector.registerDigitalDocument(digDocRegistrationData, registrarCode);
            report("- Digital-document-registration successful - continuing.");
            return urnnbn;
        } catch (IOException ex) {
            throw new OaiAdapterException("IOException occurred during Digital-document-registration:", ex);
        } catch (ParsingException ex) {
            throw new OaiAdapterException("ParsingException occurred during Digital-document-registration:", ex);
        } catch (CzidloConnectionException ex) {
            throw new OaiAdapterException("CzidloConnectionException occurred during Digital-document-registration:", ex);
        }
    }

    private void importDigitalInstance(Document diImportData, String urnnbn, String oaiIdentifier) throws OaiAdapterException {
        try {
            czidloConnector.importDigitalInstance(diImportData, urnnbn);
        } catch (IOException ex) {
            throw new OaiAdapterException("IOException occurred during digital-instance-import:", ex);
        } catch (ParsingException ex) {
            throw new OaiAdapterException("ParsingException occurred during Digital-instance-import:", ex);
        } catch (CzidloConnectionException ex) {
            throw new OaiAdapterException("CzidloConnectionException occurred during Digital-instance-import:", ex);
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
