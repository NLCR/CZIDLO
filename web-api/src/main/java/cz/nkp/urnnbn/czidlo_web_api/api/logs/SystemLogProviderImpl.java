package cz.nkp.urnnbn.czidlo_web_api.api.logs;

import cz.nkp.urnnbn.czidlo_web_api.WebApiModuleConfiguration;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

public class SystemLogProviderImpl implements SystemLogProvider {

    private static final DateTimeFormatter LOG_DATE_FORMATTER =
            new DateTimeFormatterBuilder()
                    .appendValue(ChronoField.DAY_OF_MONTH)
                    .appendLiteral('.')
                    .appendValue(ChronoField.MONTH_OF_YEAR)
                    .appendLiteral('.')
                    .appendValue(ChronoField.YEAR)
                    .toFormatter();


    @Override
    public String getLogs(Integer maxLines, LocalDate minDate, LocalDate dayAfterMaxDate) throws IOException {
        File adminLogFile = WebApiModuleConfiguration.instanceOf().getAdminLogFile();
        //System.out.println("Reading admin log file: " + adminLogFile.getAbsolutePath());
        if (!adminLogFile.exists()) {
            return "Log file does not exist: " + adminLogFile.getAbsolutePath();
        }
        if (!adminLogFile.isFile()) {
            return "Log file is not a file: " + adminLogFile.getAbsolutePath();
        }
        if (!adminLogFile.canRead()) {
            return "Log file is not readable: " + adminLogFile.getAbsolutePath();
        }
        return getLogsFromFile(adminLogFile, maxLines, minDate, dayAfterMaxDate);
    }

    /**
     * Přečte logy ze souboru od konce:
     * - jde po řádcích pozpátku
     * - filtruje podle minDate / dayAfterMaxDate
     * - uplatní maxLines
     * - vrací text v normálním chronologickém pořadí (od nejstaršího po nejnovější z vybraných)
     */
    public String getLogsFromFile(File adminLogFile,
                                  Integer maxLines,
                                  LocalDate minDate,
                                  LocalDate dayAfterMaxDate) throws IOException {

        List<String> linesFilteredAndReversed = new ArrayList<>();

        try (RandomAccessFile raf = new RandomAccessFile(adminLogFile, "r")) {
            long fileLength = raf.length();
            if (fileLength == 0) {
                return "";
            }

            ByteArrayOutputStream lineBytesReversed = new ByteArrayOutputStream(256);
            long pos = fileLength - 1;
            boolean stopReading = false;

            while (pos >= 0 && !stopReading) {
                raf.seek(pos);
                int b = raf.read();
                pos--;

                if (b == '\n' || b == '\r') {
                    // máme jeden řádek (aktuálně poskládaný pozpátku)
                    if (lineBytesReversed.size() > 0) {
                        String line = decodeReversedUtf8Line(lineBytesReversed);
                        stopReading = processLine(line, linesFilteredAndReversed, maxLines, minDate, dayAfterMaxDate);
                        lineBytesReversed.reset();
                    }
                    // \r\n vyřeší další iterace
                } else {
                    lineBytesReversed.write(b);
                }
            }

            // poslední řádek (začátek souboru), pokud není ukončen newline
            if (!stopReading && lineBytesReversed.size() > 0) {
                String line = decodeReversedUtf8Line(lineBytesReversed);
                processLine(line, linesFilteredAndReversed, maxLines, minDate, dayAfterMaxDate);
            }
        }

        StringBuilder result = new StringBuilder();
        for (int i = linesFilteredAndReversed.size() - 1; i >= 0; i--) {
            result.append(linesFilteredAndReversed.get(i));
        }
        return result.toString();
    }

    private static String decodeReversedUtf8Line(ByteArrayOutputStream reversed) {
        byte[] rev = reversed.toByteArray();
        // otočit bajty do správného pořadí
        for (int i = 0, j = rev.length - 1; i < j; i++, j--) {
            byte tmp = rev[i];
            rev[i] = rev[j];
            rev[j] = tmp;
        }
        return new String(rev, StandardCharsets.UTF_8);
    }

    /**
     * Zpracuje jeden řádek z konce souboru (tj. nejnovější jako první).
     * Vrací true, pokud máme skončit (dosažen maxLines nebo log starší než minDate).
     */
    private boolean processLine(String line,
                                List<String> linesFilteredAndReversed,
                                Integer maxLines,
                                LocalDate minDate,
                                LocalDate dayAfterMaxDate) {
        if (line.isEmpty()) {
            return false;
        }

        // konec, když dosáhneme maxLines
        if (maxLines != null && linesFilteredAndReversed.size() >= maxLines) {
            return true;
        }

        // date filtering enabled
        if (minDate != null || dayAfterMaxDate != null) {
            // očekává se formát:
            // [2.10.2025, 07:58:08] ...
            String[] parts = line.split(",");
            if (parts.length == 0) {
                // řádek neobsahuje datum v očekávaném formátu -> ignoruj, ale nepřerušuj
                return false;
            }
            String logDateStr = parts[0].substring(1); // odstraníme '['

            LocalDate logDate = LocalDate.parse(logDateStr, LOG_DATE_FORMATTER);

            // přeskočit, pokud je datum logu větší než dayAfterMaxDate-1 (tj. > maxDate)
            if (dayAfterMaxDate != null) {
                if (logDate.isAfter(dayAfterMaxDate.minusDays(1))) {
                    // přeskočíme tento záznam a pokračujeme k dalšímu (staršímu)
                    return false;
                }
            }

            // konec, pokud je datum logu starší než minDate
            if (minDate != null) {
                if (logDate.isBefore(minDate)) {
                    // končíme cyklus úplně, už budou jen starší záznamy
                    return true;
                }
            }
        }

        linesFilteredAndReversed.add(line + "\n");

        // znovu kontrola maxLines – pokud jsme právě přidali poslední
        if (maxLines != null && linesFilteredAndReversed.size() >= maxLines) {
            return true;
        }

        return false;
    }
}
