/*
 * Copyright (C) 2013 Martin Řehánek
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package cz.nkp.urnnbn.processmanager.scheduler.jobs;

import cz.nkp.urnnbn.core.CountryCode;
import cz.nkp.urnnbn.core.IntEntIdType;
import cz.nkp.urnnbn.core.RegistrarCode;
import cz.nkp.urnnbn.core.dto.DigitalDocument;
import cz.nkp.urnnbn.core.dto.IntEntIdentifier;
import cz.nkp.urnnbn.core.dto.IntelectualEntity;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.dto.UrnNbnExport;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.impl.postgres.PostgresPooledConnector;
import cz.nkp.urnnbn.processmanager.core.ProcessState;
import cz.nkp.urnnbn.processmanager.core.ProcessType;
import cz.nkp.urnnbn.services.Services;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author Martin Řehánek
 */
public class UrnNbnCsvExportJob extends AbstractJob {

	private static final String DATE_FORMAT = "d. M. yyyy H:m.s";
	private final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
	
    public static final String CSV_EXPORT_FILE_NAME = "export.csv";
    
    public static final String PARAM_BEGIN_CODE_KEY = "begin";
    public static final String PARAM_END_CODE_KEY = "end";
    public static final String PARAM_REGISTRARS_CODE_KEY = "registrars";
    public static final String PARAM_REG_MODE_CODE_KEY = "registrationMode";
    public static final String PARAM_ENT_TYPE_CODE_KEY = "entityType";
    public static final String PARAM_HAS_CNB_CODE_KEY = "cnb";
    public static final String PARAM_HAS_ISSN_CODE_KEY = "issn";
    public static final String PARAM_HAS_ISBN_CODE_KEY = "isbn";
    public static final String PARAM_IS_ACTIVE_CODE_KEY = "active";
    public static final String PARAM_EXPORT_NUM_OF_DIG_INSTANCES = "exportNumOfDigInstances";
    
    private static final String HEADER_TITLE = "Název titulu";
    private static final String HEADER_URN_NBN = "URN:NBN";
	private static final String HEADER_RESERVED = "Rezervováno";
	private static final String HEADER_MODIFIED = "Modifikováno";
	private static final String HEADER_REG_MODE = "Mod registrace";
	private static final String HEADER_ENT_TYPE = "Typ dokumentu";
	private static final String HEADER_HAS_CNB = "Má ČNB";
	private static final String HEADER_HAS_ISSN = "Má ISSN";
	private static final String HEADER_HAS_ISBN = "Má ISBN";
	private static final String HEADER_UNR_ACTIVE = "Stav";
	private static final String HEADER_NUM_OF_DIG_INSTANCES = "";
	
    private PrintWriter csvWriter;
    private Services services;

    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            init(context.getMergedJobDataMap(), ProcessType.REGISTRARS_URN_NBN_CSV_EXPORT);
            logger.info("executing " + UrnNbnCsvExportJob.class.getName());
            csvWriter = openCsvWriter(createWriteableProcessFile(CSV_EXPORT_FILE_NAME));
            this.services = initServices();
            logger.info("services initialized");
            DateTime begin = null;
            if (context.getMergedJobDataMap().containsKey(PARAM_BEGIN_CODE_KEY)) {
            	String val = context.getMergedJobDataMap().getString(PARAM_BEGIN_CODE_KEY);
            	if (val != null) {
            		begin = new DateTime(dateFormat.parse(val));
            	}
            }
            DateTime end = null;
            if (context.getMergedJobDataMap().containsKey(PARAM_END_CODE_KEY)) {
            	String val = context.getMergedJobDataMap().getString(PARAM_END_CODE_KEY);
            	if (val != null) {
            		end = new DateTime(dateFormat.parse(val));
            	}
            }
            List<String> registrars = null;
            if (context.getMergedJobDataMap().get(PARAM_REGISTRARS_CODE_KEY) != null) {
            	registrars = Arrays.asList(((String) context.getMergedJobDataMap().get(PARAM_REGISTRARS_CODE_KEY)).split(","));
            }
            String registrationMode = context.getMergedJobDataMap().getString(PARAM_REG_MODE_CODE_KEY);
            List<String> entityTypes = null;
            if (context.getMergedJobDataMap().get(PARAM_ENT_TYPE_CODE_KEY) != null) {
            	entityTypes = Arrays.asList(((String) context.getMergedJobDataMap().get(PARAM_ENT_TYPE_CODE_KEY)).split(","));
            }
            //context.getMergedJobDataMap().getString(PARAM_ENT_TYPE_CODE_KEY);
            Boolean cnbAssigned = null;
            if (!"null".equals(context.getMergedJobDataMap().getString(PARAM_HAS_CNB_CODE_KEY))) {
            	cnbAssigned = context.getMergedJobDataMap().getBoolean(PARAM_HAS_CNB_CODE_KEY);
            }
            Boolean issnAsigned = null;
            if (!"null".equals(context.getMergedJobDataMap().getString(PARAM_HAS_ISSN_CODE_KEY))) {
            	issnAsigned =  context.getMergedJobDataMap().getBoolean(PARAM_HAS_ISSN_CODE_KEY);
            }
            Boolean isbnAssigned = null;
            if (!"null".equals(context.getMergedJobDataMap().getString(PARAM_HAS_CNB_CODE_KEY))) {
            	isbnAssigned = context.getMergedJobDataMap().getBoolean(PARAM_HAS_CNB_CODE_KEY);
            }
            Boolean active = null;
            if (!"null".equals(context.getMergedJobDataMap().getString(PARAM_HAS_CNB_CODE_KEY))) {
            	active = context.getMergedJobDataMap().getBoolean(PARAM_IS_ACTIVE_CODE_KEY);
            }
            Boolean exportNumOfDigInstances = context.getMergedJobDataMap().getBoolean(PARAM_EXPORT_NUM_OF_DIG_INSTANCES);
            logger.info("registrar code: " + registrars);
            this.runProcess(begin, end, registrars, registrationMode, entityTypes, cnbAssigned, issnAsigned, isbnAssigned, active, exportNumOfDigInstances);
            if (interrupted) {
                context.setResult(ProcessState.KILLED);
            } else {
                context.setResult(ProcessState.FINISHED);
            }
        } catch (Throwable ex) {
            //throw new JobExecutionException(ex);
            logger.error("urn:nbn export process failed", ex);
            context.setResult(ProcessState.FAILED);
        } finally {
            close();
        }
    }

    @Override
    void close() {
        if (csvWriter != null) {
            csvWriter.close();
        }
    }

    private PrintWriter openCsvWriter(File file) throws FileNotFoundException {
        return new PrintWriter(file);
    }

    private Services initServices() {
        Services.init(initDatabaseConnector());
        return Services.instanceOf();
    }

    private DatabaseConnector initDatabaseConnector() {
        return new PostgresPooledConnector();
    }

    private void runProcess(DateTime begin, DateTime end, List<String> registrars, String registrationMode, 
    		List<String> entityTypes,  Boolean cnbAssigned, Boolean issnAsigned,  Boolean isbnAssigned, Boolean active, Boolean exportNumOfDigInstances) {
    	System.out.println(String.format("%s-%s", begin, end));
    	if (registrars != null) {
    		for (String reg : registrars) {
    			System.out.println(reg);
    		}
    	}
    	if (entityTypes != null) {
    		for (String entityType : entityTypes) {
    			System.out.println(entityType);
    		}
    	}
    	System.out.println(String.format("cnb: %s, issn: %s, isbn: %s, active: %s, digInstances: %s", cnbAssigned, issnAsigned, isbnAssigned, active, exportNumOfDigInstances));
        //TODO: kod az na zaklade konfigurace
        CountryCode.initialize("cz");
        try {
            List<UrnNbnExport> urnNbnList = services.dataAccessService().selectByCriteria(
            		begin,
            		end,
            		registrars,
            		registrationMode,
            		entityTypes,
            		cnbAssigned,
            		issnAsigned,
            		isbnAssigned,
            		active
            );
            //.urnNbnsOfRegistrar(RegistrarCode.valueOf(registrarCode));
            csvWriter.println(buildHeaderLine(exportNumOfDigInstances));
            int counter = 0;
            int total = urnNbnList.size();
            for (UrnNbnExport urn : urnNbnList) {
                if (interrupted) {
                    csvWriter.flush();
                    break;
                }
                String line = toCsvLine(urn, exportNumOfDigInstances);
                csvWriter.println(line);
                /*
                logger.info("exporting " + urn + " (" + ++counter + "/" + total + ")");
                DigitalDocument digDoc = services.dataAccessService().digDocByInternalId(urn.getDigDocId());
                IntelectualEntity intEntity = services.dataAccessService().entityById(digDoc.getIntEntId());
                String line = toCsvLine(urn, intEntity);
                csvWriter.println(line);
                */
            }
        } /*catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        }*/ finally {
            csvWriter.close();
        }
    }

    private String buildHeaderLine(Boolean exportNumOfDigInstances) {
        StringBuilder result = new StringBuilder();
        result.append('\"').append(HEADER_URN_NBN).append("\",");
        result.append('\"').append(HEADER_RESERVED).append("\",");
        result.append('\"').append(HEADER_MODIFIED).append("\",");
        result.append('\"').append(HEADER_REG_MODE).append("\",");
        result.append('\"').append(HEADER_ENT_TYPE).append("\",");
        result.append('\"').append(HEADER_HAS_CNB).append("\",");
        result.append('\"').append(HEADER_HAS_ISSN).append("\",");
        result.append('\"').append(HEADER_HAS_ISBN).append("\",");
        result.append('\"').append(HEADER_TITLE).append("\",");
        result.append('\"').append(HEADER_UNR_ACTIVE);
        if (exportNumOfDigInstances) {
        	result.append(",\"").append(HEADER_NUM_OF_DIG_INSTANCES).append("\"");
        }
        return result.toString();
    }
    
    private String toCsvLine(UrnNbnExport item, Boolean exportNumOfDigInstances) {
    	StringBuilder result = new StringBuilder();
        result.append('\"').append(item.getUrn()).append("\",");
        result.append('\"').append(item.getReserved()).append("\",");
        result.append('\"').append(item.getModified()).append("\",");
        result.append('\"').append(item.getRegistrationMode()).append("\",");
        result.append('\"').append(item.getEntityType()).append("\",");
        result.append('\"').append(item.isCnbAssigned()).append("\",");
        result.append('\"').append(item.isIssnAssigned()).append("\",");
        result.append('\"').append(item.isIsbnAssigned()).append("\",");
        result.append('\"').append(item.getTitle()).append("\",");
        result.append('\"').append(item.isActive());
        if (exportNumOfDigInstances) {
        	result.append(",\"").append(item.getNumberOfDigitalInstances()).append("\"");
        }
        return result.toString();
    }

    /*
    private String toCsvLine(UrnNbn urn, IntelectualEntity intEntity) throws DatabaseException {
        StringBuilder result = new StringBuilder();
        String aggregateTitle = toAggregateTitle(intEntity);
        result.append('\"').append(aggregateTitle).append('\"');
        result.append(',');
        result.append('\"').append(urn.toString()).append('\"');
        return result.toString();
    }
    */

    private String toAggregateTitle(IntelectualEntity intEntity) throws DatabaseException {
        Map<IntEntIdType, String> identifierMap = intEntIdentifierMap(intEntity);
        StringBuilder builder = new StringBuilder();
        switch (intEntity.getEntityType()) {
            case MONOGRAPH:
            case PERIODICAL:
            case ANALYTICAL:
            case THESIS:
            case OTHER:
                builder.append(identifierMap.get(IntEntIdType.TITLE));
                String subtitle = identifierMap.get(IntEntIdType.SUB_TITLE);
                if (subtitle != null) {
                    builder.append(" (").append(subtitle).append(')');
                }
                return builder.toString();
            case MONOGRAPH_VOLUME:
            case PERIODICAL_VOLUME:
                builder.append(identifierMap.get(IntEntIdType.TITLE));
                builder.append(", ").append(identifierMap.get(IntEntIdType.VOLUME_TITLE));
                return builder.toString();
            case PERIODICAL_ISSUE:
                builder.append(identifierMap.get(IntEntIdType.TITLE));
                builder.append(", ").append(identifierMap.get(IntEntIdType.VOLUME_TITLE));
                builder.append(", ").append(identifierMap.get(IntEntIdType.ISSUE_TITLE));
                return builder.toString();
            default:
                return null;
        }
    }

    private Map<IntEntIdType, String> intEntIdentifierMap(IntelectualEntity intEntity) throws DatabaseException {
        List<IntEntIdentifier> identifiers = services.dataAccessService().intEntIdentifiersByIntEntId(intEntity.getId());
        Map<IntEntIdType, String> result = new HashMap<IntEntIdType, String>(identifiers.size());
        for (IntEntIdentifier id : identifiers) {
            result.put(id.getType(), fixForCsv(id.getValue()));
        }
        return result;
    }

    private String fixForCsv(String value) {
        //zatim jen nahrada " za '
        return value.replace('"', '\'');
    }
}
