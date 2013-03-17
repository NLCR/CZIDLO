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
import cz.nkp.urnnbn.core.dto.Registrar;
import cz.nkp.urnnbn.core.dto.UrnNbn;
import cz.nkp.urnnbn.core.persistence.DatabaseConnector;
import cz.nkp.urnnbn.core.persistence.exceptions.DatabaseException;
import cz.nkp.urnnbn.core.persistence.impl.postgres.PostgresSimpleConnector;
import cz.nkp.urnnbn.processmanager.core.ProcessState;
import cz.nkp.urnnbn.processmanager.core.ProcessType;
import cz.nkp.urnnbn.services.Services;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author Martin Řehánek
 */
public class UrnNbnCsvExportJob extends AbstractJob {

    public static final String CSV_EXPORT_FILE_NAME = "export.csv";
    public static final String PARAM_REG_CODE_KEY = "registrarCode";
    private static final String HEADER_TITLE = "Název titulu";
    private static final String HEADER_URN_NBN = "URN:NBN";
    private PrintWriter csvWriter;
    private Services services;

    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            init(context.getMergedJobDataMap(), ProcessType.REGISTRARS_URN_NBN_CSV_EXPORT);
            logger.info("executing " + UrnNbnCsvExportJob.class.getName());
            csvWriter = openCsvWriter(createWriteableProcessFile(CSV_EXPORT_FILE_NAME));
            this.services = initServices();
            logger.info("services initialized");
            String registrarCode = (String) context.getMergedJobDataMap().get(PARAM_REG_CODE_KEY);
            logger.info("registrar code: " + registrarCode);
            runProcess(registrarCode);
            if (interrupted) {
                context.setResult(ProcessState.KILLED);
            } else {
                context.setResult(ProcessState.FINISHED);
            }
        } catch (Throwable ex) {
            //throw new JobExecutionException(ex);
            logger.error(ex.getMessage());
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
        return new PostgresSimpleConnector(resolverDbHost, resolverDbDatabase, resolverDbPort, resolverDbLogin, resolverDbPassword);
    }

    private void runProcess(String registrarCode) {
        //TODO: kod az na zaklade konfigurace
        CountryCode.initialize("cz");
        try {
            Registrar registrar = services.dataAccessService().registrarByCode(RegistrarCode.valueOf(registrarCode));
            Set<UrnNbn> urnNbnList = services.dataAccessService().urnNbnsOfChangedRecordsOfRegistrar(registrar, null, null);
            csvWriter.println(buildHeaderLine());
            int counter = 0;
            int total = urnNbnList.size();
            for (UrnNbn urn : urnNbnList) {
                if (interrupted) {
                    csvWriter.flush();
                    break;
                }
                logger.info("exporting " + urn + " (" + ++counter + "/" + total + ")");
                DigitalDocument digDoc = services.dataAccessService().digDocByInternalId(urn.getDigDocId());
                IntelectualEntity intEntity = services.dataAccessService().entityById(digDoc.getIntEntId());
                String line = toCsvLine(urn, intEntity);
                csvWriter.println(line);
            }
        } catch (DatabaseException ex) {
            throw new RuntimeException(ex);
        } finally {
            csvWriter.close();
        }
    }

    private String buildHeaderLine() {
        StringBuilder result = new StringBuilder();
        result.append('\"').append(HEADER_TITLE).append('\"');
        result.append(',');
        result.append('\"').append(HEADER_URN_NBN).append('\"');
        return result.toString();
    }

    private String toCsvLine(UrnNbn urn, IntelectualEntity intEntity) throws DatabaseException {
        StringBuilder result = new StringBuilder();
        String aggregateTitle = toAggregateTitle(intEntity);
        result.append('\"').append(aggregateTitle).append('\"');
        result.append(',');
        result.append('\"').append(urn.toString()).append('\"');
        return result.toString();
    }

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
