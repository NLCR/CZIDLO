/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaiadapter;

import cz.nkp.urnnbn.oaiadapter.resolver.RegistrationMode;
import cz.nkp.urnnbn.oaiadapter.resolver.ResolverConnectionException;
import cz.nkp.urnnbn.oaiadapter.resolver.ResolverConnector;
import cz.nkp.urnnbn.oaiadapter.resolver.UrnnbnStatus;
import cz.nkp.urnnbn.oaiadapter.utils.ImportDocumentHandler;
import cz.nkp.urnnbn.oaiadapter.utils.Refiner;
import cz.nkp.urnnbn.oaiadapter.utils.XmlTools;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import nu.xom.Document;
import nu.xom.ParsingException;
import nu.xom.xslt.XSLException;

/**
 * 
 * @author Jan Rychtář
 * @author Martin Řehánek
 */
public class OaiAdapter {

	private static final Logger logger = Logger.getLogger(OaiAdapter.class.getName());
	public static final String REGISTAR_SCOPE_ID = "OAI_Adapter";
	// OAI
	private String oaiBaseUrl;
	private String metadataPrefix;
	private String setSpec;
	// RESOLVER
	private String registrarCode;
	private RegistrationMode registrationMode;
	private ResolverConnector resolverConnector;
	// XSLT
	private String metadataToDigDocRegistrationTemplate;
	private String metadataToDigInstImportTemplate;
	// XSD
	private XsdProvider xsdProvider;
	// OTHER
	private int limit = -1;
	private ReportLogger reportLogger;

	public OaiAdapter() {
	}

	public RegistrationMode getRegistrationMode() {
		return registrationMode;
	}

	public void setRegistrationMode(RegistrationMode mode) {
		this.registrationMode = mode;
	}

	public String getOaiBaseUrl() {
		return oaiBaseUrl;
	}

	public void setOaiBaseUrl(String oaiBaseUrl) {
		this.oaiBaseUrl = oaiBaseUrl;
	}

	public String getMetadataPrefix() {
		return metadataPrefix;
	}

	public void setMetadataPrefix(String metadataPrefix) {
		this.metadataPrefix = metadataPrefix;
	}

	public String getSetSpec() {
		return setSpec;
	}

	public void setSetSpec(String setSpec) {
		this.setSpec = setSpec;
	}

	public String getMetadataToDigDocRegistrationTemplate() {
		return metadataToDigDocRegistrationTemplate;
	}

	public void setMetadataToDigDocRegistrationTemplate(String metadataToImportTemplate) {
		this.metadataToDigDocRegistrationTemplate = metadataToImportTemplate;
	}

	public String getMetadataToDigInstImportTemplate() {
		return metadataToDigInstImportTemplate;
	}

	public void setMetadataToDigInstImportTemplate(String metadataToDigitalInstanceTemplate) {
		this.metadataToDigInstImportTemplate = metadataToDigitalInstanceTemplate;
	}

	public String getRegistrarCode() {
		return registrarCode;
	}

	public void setRegistrarCode(String registrarCode) {
		this.registrarCode = registrarCode;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public void setOutputStream(OutputStream os) {
		this.reportLogger = new ReportLogger(os);
	}

	public ResolverConnector getResolverConnector() {
		return resolverConnector;
	}

	public void setResolverConnector(ResolverConnector resolverConnector) {
		this.resolverConnector = resolverConnector;
	}

	public XsdProvider getXsdProvider() {
		return xsdProvider;
	}

	public void setXsdProvider(XsdProvider xsdProvider) {
		this.xsdProvider = xsdProvider;
	}

	private Document getDigDocRegistrationTemplateDoc() throws TemplateException {
		try {
			return XmlTools.getTemplateDocumentFromString(getMetadataToDigDocRegistrationTemplate());
		} catch (XSLException ex) {
			throw new TemplateException("XSLException occurred during building Digital Document Registration templat: " + ex.getMessage());
		} catch (ParsingException ex) {
			throw new TemplateException("ParsingException occurred during building Digital Document Registration templat: "
					+ ex.getMessage());
		} catch (IOException ex) {
			throw new TemplateException("IOException occurred during building Digital Document Registration templat: " + ex.getMessage());
		}
	}

	private Document getDigInstImportTemplateDoc() throws TemplateException {
		try {
			return XmlTools.getTemplateDocumentFromString(getMetadataToDigInstImportTemplate());
		} catch (XSLException ex) {
			throw new TemplateException("XSLException occurred during building Digital Instance Import template: " + ex.getMessage());
		} catch (ParsingException ex) {
			throw new TemplateException("ParsingException occurred during building Digital Instance Import template: " + ex.getMessage());
		} catch (IOException ex) {
			throw new TemplateException("IOException occurred during building Digital Instance Import template: " + ex.getMessage());
		}
	}

	private void report(String message) {
		if (reportLogger != null) {
			reportLogger.report(message);
		}
	}

	public Object[] processSingleDocument(String oaiIdentifier, Document digDocRegistrationData, Document digInstImportData)
			throws OaiAdapterException, ResolverConnectionException {
		Refiner.refineDocument(digDocRegistrationData, xsdProvider.getDigitalDocumentRegistrationDataXsd());
		ImportDocumentHandler.putRegistrarScopeIdentifier(digDocRegistrationData, oaiIdentifier);
		String urnnbn = ImportDocumentHandler.getUrnnbnFromDocument(digDocRegistrationData);
		if (urnnbn == null) {
			if (getRegistrationMode() == RegistrationMode.BY_RESOLVER) {
				urnnbn = resolverConnector.getUrnnbnByTriplet(registrarCode, OaiAdapter.REGISTAR_SCOPE_ID, oaiIdentifier);
				if (urnnbn == null) {
					urnnbn = registerDigitalDocument(digDocRegistrationData, oaiIdentifier);
				}
			} else {
				throw new OaiAdapterException("Incorrect mode - document doesn't contain URN:NBN and mode is not BY_RESOLVER");
			}
		} else {
			if (getRegistrationMode() == RegistrationMode.BY_RESOLVER) {
				throw new OaiAdapterException("Incorrect mode - document contains URN:NBN and mode is BY_RESOLVER");
			}
			UrnnbnStatus urnnbnStatus = resolverConnector.getUrnnbnStatus(urnnbn);
			report("- URN:NBN status: " + urnnbnStatus);

			if (urnnbnStatus == UrnnbnStatus.UNDEFINED) {
				throw new OaiAdapterException("Checking URN:NBN status failed");
			}
			if (urnnbnStatus == UrnnbnStatus.RESERVED && getRegistrationMode() != RegistrationMode.BY_RESERVATION) {
				throw new OaiAdapterException("Incorrect mode - URN:NBN has status RESERVED and mode is not RESERVATION");
			}
			if (urnnbnStatus == UrnnbnStatus.FREE && getRegistrationMode() != RegistrationMode.BY_REGISTRAR) {
				throw new OaiAdapterException("Incorrect mode - URN:NBN has status FREE and mode is not BY_REGISTRAR");
			}

			if (urnnbnStatus == UrnnbnStatus.ACTIVE) {
				String urnnbnByTriplet = resolverConnector.getUrnnbnByTriplet(registrarCode, OaiAdapter.REGISTAR_SCOPE_ID, oaiIdentifier);
				if (urnnbnByTriplet != null && !urnnbn.equals(urnnbnByTriplet)) {
					throw new OaiAdapterException("URN:NBN in import document (" + urnnbn
							+ ") doesn't match URN:NBN obtained by OAI_ADAPTER ID (" + urnnbnByTriplet + ")");
				}
			} else {
				registerDigitalDocument(digDocRegistrationData, oaiIdentifier);
			}
		}
		DigitalInstance newDi = ImportDocumentHandler.getDIFromSourceDocument(digInstImportData);
		DigitalInstance oldDi = null;
		try {
			oldDi = resolverConnector.getDigitalInstanceByLibraryId(urnnbn, newDi);
		} catch (IOException ex) {
			Logger.getLogger(OaiAdapter.class.getName()).log(Level.SEVERE, null, ex);
		} catch (ParsingException ex) {
			Logger.getLogger(OaiAdapter.class.getName()).log(Level.SEVERE, null, ex);
		}
		if (oldDi == null) {
			// di doesnt exist yet
			// IMPORT
			importDigitalInstance(digInstImportData, urnnbn, oaiIdentifier);
			report("- DI doesn't exists - importing DI.");
			return new Object[] { urnnbn, Boolean.TRUE };
		} else {
			// di already exist
			if (newDi.isChanged(oldDi)) {
				// di has been changed
				// REMOVE
				resolverConnector.removeDigitalInstance(oldDi.getId());
				// IMPORT
				importDigitalInstance(digInstImportData, urnnbn, oaiIdentifier);
				report("- DI already exists and is modified - removing old one and imporing new DI.");
				return new Object[] { urnnbn, Boolean.TRUE };
			} else {
				// no change ..do nothing
				report("- DI already exists and is not modified - doing nothing.");
				return new Object[] { urnnbn, Boolean.FALSE };
			}
		}
	}

	private void importDigitalInstance(Document digitalInstance, String urnnbn, String oaiIdentifier) throws OaiAdapterException {
		try {
			resolverConnector.importDigitalInstance(digitalInstance, urnnbn);
			report("- Digital Instance Import successful - continuing.");
		} catch (IOException ex) {
			throw new OaiAdapterException("IOException occurred during Digital Instance Import: " + ex.getMessage());
		} catch (ParsingException ex) {
			throw new OaiAdapterException("ParsingException occurred during Digital Instance Import: " + ex.getMessage());
		} catch (ResolverConnectionException ex) {
			throw new OaiAdapterException("ResolverConnectionException occurred during Digital Instance Import: " + ex.getMessage());
		}

	}

	private String registerDigitalDocument(Document digitalDocument, String oaiIdentifier) throws OaiAdapterException {
		try {
			String urnnbn = resolverConnector.importDocument(digitalDocument, registrarCode);
			report("- Digital Document Registration successful - continuing.");
			report("- URN:NBN: " + urnnbn);
			return urnnbn;
		} catch (IOException ex) {
			throw new OaiAdapterException("IOException occurred during Digital Document Registration: " + ex.getMessage());
		} catch (ParsingException ex) {
			throw new OaiAdapterException("ParsingException occurred during Digital Document Registration: " + ex.getMessage());
		} catch (ResolverConnectionException ex) {
			throw new OaiAdapterException("ResolverConnectionException occurred during Digital Document Registration: " + ex.getMessage());
		}
	}

	private boolean processRecord(OriginalRecordFromOai originalRecord, Document digDocRegistrationTemplate, Document digInstImportTemplate)
			throws OaiAdapterException {
		report("------------------------------------------------------");
		String identifier = originalRecord.getIdentifier();
		report("Processing next record - identifier: " + identifier);

		// DIGITAL DOCUMENT REGISTRATION
		Document digDocRegistrationData = null;
		try {
			digDocRegistrationData = XmlTools.getTransformedDocument(originalRecord.getDocument(), digDocRegistrationTemplate);
			report("- record successfuly transformed to Digital Document Registration data - continuing.");
			// saveToTempFile(importDocument, "digitalDocument-" + record.getIdentifier(), ".xml");
		} catch (XSLException ex) {
			throw new OaiAdapterException("XSLException occurred when transforming record to Digital Document Registration data: "
					+ ex.getMessage());
		}
		try {
			XmlTools.validateByXsdAsString(digDocRegistrationData, xsdProvider.getDigitalDocumentRegistrationDataXsd());
			report("- Digital Document Registration data validation successful - continuing.");
		} catch (DocumentOperationException ex) {
			// saveToTempFile(digitalInstanceDocument, "digitalDocument-" + record.getIdentifier(), ".xml");
			throw new OaiAdapterException("- Digital Document Registration data invalid - skipping.\nMessage: " + ex.getMessage());
		}

		// DIGITAL INSTANCE IMPORT
		Document digInstImportData = null;
		try {
			digInstImportData = XmlTools.getTransformedDocument(originalRecord.getDocument(), digInstImportTemplate);
			report("- record successfuly transformed to Digital Instance Import data - continuing.");
			// File tmpFile = saveToTempFile(digInstImportData, "digitalInstance-" + originalRecord.getIdentifier(),
			// ".xml");
		} catch (XSLException ex) {
			throw new OaiAdapterException("XSLException occurred when transforming record to Digital Instance Import data: "
					+ ex.getMessage());
		}
		try {
			XmlTools.validateByXsdAsString(digInstImportData, xsdProvider.getDigitalInstanceImportDataXsd());
			report("- Digital Instance Import data validation successful - continuing.");
			// File tmpFile = saveToTempFile(digitalInstanceDocument, "digitalInstance-" + record.getIdentifier(),
			// ".xml");
		} catch (DocumentOperationException ex) {
			throw new OaiAdapterException("- Digital Instance Import data invalid - skipping.\nMessage: " + ex.getMessage());
		}

		try {
			Object[] documentProcessingResult = processSingleDocument(identifier, digDocRegistrationData, digInstImportData);
			String urnnbn = (String) documentProcessingResult[0];
			if (urnnbn != null) {
				report("- " + urnnbn);
			}
			return (Boolean) documentProcessingResult[1];
		} catch (ResolverConnectionException ex) {
			throw new OaiAdapterException(ex.getMessage());
		}

	}

	public void run() {
		try {
			Document digDocRegistrationTemplate = getDigDocRegistrationTemplateDoc();
			Document digInstImportTemplate = getDigInstImportTemplateDoc();
			report("REPORT:");
			report("------------------------------");
			report(" OAI data provider");
			report(" -----------------");
			report("  OAI base url: " + getOaiBaseUrl());
			report("  Metadata prefix: " + getMetadataPrefix());
			report("  Set: " + (setSpec == null ? "not defined" : setSpec));
			report(" ");
			report(" CZIDLO API");
			report(" -----------------");
			report("  CZIDLO API base url: " + resolverConnector.getResolverApiUrl());
			report("  Mode: " + getRegistrationMode());
			report("------------------------------");

			try {
				if (!resolverConnector.checkRegistrarMode(getRegistrarCode(), getRegistrationMode())) {
					report(" Mode " + getRegistrationMode() + " is not enabled for registrar " + getRegistrarCode());
					logger.log(Level.SEVERE, "Mode {0} is not enabled for registrar {1}", new Object[] { getRegistrationMode(),
							getRegistrarCode() });
					return;
				}
			} catch (ResolverConnectionException e) {
				report("Resolver API not available: " + e.getMessage());
				logger.log(Level.SEVERE, "Resolver API not available: " + e.getMessage());
				return;
			}

			OaiHarvester harvester = null;
			try {
				harvester = new OaiHarvester(getOaiBaseUrl(), getMetadataPrefix(), getSetSpec());
			} catch (OaiHarvesterException ex) {
				report("OaiHarvester initialization failed. " + ex.getMessage() + ", url: " + ex.getMessage());
				logger.log(Level.SEVERE, "OaiHarvester initialization failed. {0}, url: {1}",
						new Object[] { ex.getMessage(), ex.getMessage() });
				return;
			}
			int counter = 0;
			int success = 0;
			int all = 0;
			int alreadyImported = 0;
			while (harvester.hasNext()) {
				if (limit > 0 && counter++ >= limit) {
					break;
				}
				try {
					OriginalRecordFromOai record = harvester.getNext();
					all++;
					try {
						boolean imported = processRecord(record, digDocRegistrationTemplate, digInstImportTemplate);
						if (imported) {
							success++;
							logger.log(Level.INFO, "Record successfully processed. Identifier {0}", record.getIdentifier());
						} else {
							alreadyImported++;
						}
						report("STATUS: OK");
					} catch (OaiAdapterException ex) {
						logger.log(Level.SEVERE, ex.getMessage());
						report(ex.getMessage());
						report("STATUS: NOT OK");
					}
				} catch (OaiHarvesterException ex) {
					logger.log(Level.SEVERE, "OaiHarvester exception while getting next document: {0}, url: {1}",
							new Object[] { ex.getMessage(), ex.getUrl() });
					report("OaiHarvester exception while getting next document: " + ex.getMessage() + ", url: " + ex.getUrl());
					report("STATUS: NOT OK");
				}
			}
			report("-----------------------------------------------------");
			report("ALL RECORDS: " + all);
			report("SUCCESSFUL RECORDS (NEW/MODIFIED): " + success);
			report("SUCCESSFUL RECORDS:(ALREADY IMPORTED AND NOT MODIFIED): " + alreadyImported);
			report("NOT SUCCESSFUL: " + (all - (success + alreadyImported)));

			if (reportLogger != null) {
				reportLogger.close();
			}
		} catch (TemplateException ex) {
			logger.log(Level.SEVERE, ex.getMessage());
		}

	}

	private File saveToTempFile(Document document, String prefix, String suffix) throws IOException {
		File tmpFile = File.createTempFile(prefix, suffix);
		XmlTools.saveDocumentToFile(document, tmpFile.getAbsolutePath());
		return tmpFile;
	}
}
