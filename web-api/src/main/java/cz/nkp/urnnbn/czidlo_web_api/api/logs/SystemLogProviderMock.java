package cz.nkp.urnnbn.czidlo_web_api.api.logs;

import java.time.LocalDate;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

public class SystemLogProviderMock implements SystemLogProvider {
    private static final String LOGS = "" +
            "[2.10.2025, 07:58:08] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elyq.\n" +
            "[2.10.2025, 07:58:11] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elyr.\n" +
            "[2.10.2025, 07:58:13] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elys.\n" +
            "[3.10.2025, 07:58:15] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elyt.\n" +
            "[3.10.2025, 07:58:17] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elyu.\n" +
            "[3.10.2025, 07:58:19] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elyv.\n" +
            "[4.10.2025, 07:58:21] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elyw.\n" +
            "[4.10.2025, 08:27:01] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elyx.\n" +
            "[4.10.2025, 08:27:04] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elyy.\n" +
            "[5.10.2025, 08:27:06] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elyz.\n" +
            "[5.10.2025, 08:27:09] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elz0.\n" +
            "[5.10.2025, 08:27:11] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elz1.\n" +
            "[6.10.2025, 08:27:13] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elz2.\n" +
            "[6.10.2025, 08:27:16] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elz3.\n" +
            "[6.10.2025, 08:27:18] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elz4.\n" +
            "[7.10.2025, 08:27:21] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elz5.\n" +
            "[7.10.2025, 08:27:23] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elz6.\n" +
            "[7.10.2025, 08:27:26] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elz7.\n" +
            "[8.10.2025, 08:27:28] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elz8.\n" +
            "[8.10.2025, 08:27:30] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elz9.\n" +
            "[8.10.2025, 08:27:33] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elza.\n" +
            "[9.10.2025, 08:27:35] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elzb.\n" +
            "[9.10.2025, 08:27:38] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elzc.\n" +
            "[9.10.2025, 08:27:40] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elzd.\n" +
            "[11.10.2025, 08:27:42] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elze.\n" +
            "[11.10.2025, 08:27:45] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elzf.\n" +
            "[11.10.2025, 08:27:47] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elzg.\n" +
            "[12.10.2025, 08:27:50] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elzh.\n" +
            "[12.10.2025, 08:27:52] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elzi.\n" +
            "[12.10.2025, 08:27:55] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elzj.\n" +
            "[13.10.2025, 08:27:57] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elzk.\n" +
            "[13.10.2025, 08:28:00] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elzl.\n" +
            "[13.10.2025, 08:28:02] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elzm.\n" +
            "[14.10.2025, 08:28:04] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elzn.\n" +
            "[14.10.2025, 08:28:07] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elzo.\n" +
            "[14.10.2025, 08:28:09] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elzp.\n" +
            "[15.10.2025, 08:28:12] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elzq.\n" +
            "[15.10.2025, 08:28:14] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elzr.\n" +
            "[15.10.2025, 08:28:17] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elzs.\n" +
            "[16.10.2025, 08:28:19] API: User SVKPL registered digital-document to urn:nbn:cz:pna001-00elzt.\n" +
            "[16.10.2025, 08:40:18] API: User Logica_pilot_NDK imported digital-instance with id: 1905888, urn:nbn:cz:nk-008h6x, library: Kramerius K4.\n" +
            "[16.10.2025, 08:40:19] API: User Logica_pilot_NDK imported digital-instance with id: 1905889, urn:nbn:cz:nk-008h6x, library: Kramerius 4.\n" +
            "[17.10.2025, 09:25:03] WEB: User Kurator_FP created user with login: Kurator_Nada, email: natalie.ostrakova@nkp.cz, admin: false, created: 10.10.2025 09:25:03.\n" +
            "[17.10.2025, 09:25:11] WEB: User Kurator_FP created user with login: Kurator_MB, email: miroslava.benackova@nkp.cz, admin: false, created: 10.10.2025 09:25:11.\n" +
            "[17.10.2025, 09:28:09] WEB: User Kurator_FP created user with login: Kurator, email: zdenek.vasek@nkp.cz, admin: false, created: 10.10.2025 09:28:09.\n" +
            "[18.10.2025, 09:28:21] WEB: User Kurator_FP created user with login: Kurator_VJ, email: vaclav.jirousek@nkp.cz, admin: false, created: 10.10.2025 09:28:21.\n" +
            "[18.10.2025, 09:51:16] API: User Logica_pilot_NDK imported digital-instance with id: 1905890, urn:nbn:cz:nk-008h6v, library: Kramerius K4.\n" +
            "[18.10.2025, 09:51:16] API: User Logica_pilot_NDK imported digital-instance with id: 1905891, urn:nbn:cz:nk-008h6v, library: Kramerius 4.\n" +
            "[19.10.2025, 10:09:51] API: User Logica_pilot_NDK imported digital-instance with id: 1905892, urn:nbn:cz:nk-008h6w, library: Kramerius K4.\n" +
            "[19.10.2025, 10:09:51] API: User Logica_pilot_NDK imported digital-instance with id: 1905893, urn:nbn:cz:nk-008h6w, library: Kramerius 4.\n" +
            "[19.10.2025, 10:45:22] API: User Logica_pilot_NDK imported digital-instance with id: 1905894, urn:nbn:cz:mzk-008hey, library: Kramerius K4.\n" +
            "[20.10.2025, 10:45:22] API: User Logica_pilot_NDK imported digital-instance with id: 1905895, urn:nbn:cz:mzk-008hey, library: Kramerius 4.\n" +
            "[20.10.2025, 10:48:14] API: User Logica_pilot_NDK imported digital-instance with id: 1905896, urn:nbn:cz:mzk-008hf6, library: Kramerius K4.\n" +
            "[20.10.2025, 10:48:14] API: User Logica_pilot_NDK imported digital-instance with id: 1905897, urn:nbn:cz:mzk-008hf6, library: Kramerius 4.\n" +
            "[21.10.2025, 10:49:20] API: User Logica_pilot_NDK registered digital-document to urn:nbn:cz:mzk-008hfm.\n" +
            "[21.10.2025, 10:58:59] API: User Logica_pilot_NDK registered digital-document to urn:nbn:cz:mzk-008hfn.\n" +
            "[21.10.2025, 11:05:09] API: User Logica_pilot_NDK imported digital-instance with id: 1905898, urn:nbn:cz:mzk-008hfm, library: Kramerius K4.\n" +
            "[22.10.2025, 11:05:09] API: User Logica_pilot_NDK imported digital-instance with id: 1905899, urn:nbn:cz:mzk-008hfm, library: Kramerius 4.\n" +
            "[22.10.2025, 11:19:49] API: User Logica_pilot_NDK imported digital-instance with id: 1905900, urn:nbn:cz:nk-008h6b, library: Kramerius K4.\n" +
            "[22.10.2025, 11:19:50] API: User Logica_pilot_NDK imported digital-instance with id: 1905901, urn:nbn:cz:nk-008h6b, library: Kramerius 4.\n" +
            "[23.10.2025, 11:21:00] API: User Logica_pilot_NDK registered digital-document to urn:nbn:cz:mzk-008hfo.\n" +
            "[23.10.2025, 11:21:10] API: User Logica_pilot_NDK registered digital-document to urn:nbn:cz:mzk-008hfp.\n" +
            "[23.10.2025, 11:21:30] API: User Logica_pilot_NDK registered digital-document to urn:nbn:cz:mzk-008hfq.\n" +
            "[24.10.2025, 11:21:51] API: User Logica_pilot_NDK registered digital-document to urn:nbn:cz:mzk-008hfr.\n" +
            "[24.10.2025, 11:21:52] API: User Logica_pilot_NDK registered digital-document to urn:nbn:cz:mzk-008hfs.\n" +
            "[24.10.2025, 11:21:53] API: User Logica_pilot_NDK registered digital-document to urn:nbn:cz:mzk-008hft.\n" +
            "[25.10.2025, 11:28:40] API: User Logica_pilot_NDK registered digital-document to urn:nbn:cz:mzk-008hfu.\n" +
            "[25.10.2025, 11:28:50] API: User Logica_pilot_NDK registered digital-document to urn:nbn:cz:mzk-008hfv.\n" +
            "[25.10.2025, 11:28:51] API: User Logica_pilot_NDK registered digital-document to urn:nbn:cz:mzk-008hfw.\n" +
            "[26.10.2025, 11:28:59] API: User Logica_pilot_NDK registered digital-document to urn:nbn:cz:mzk-008hfx.\n" +
            "[26.10.2025, 11:29:00] API: User Logica_pilot_NDK registered digital-document to urn:nbn:cz:mzk-008hfy.\n" +
            "[26.10.2025, 11:29:50] API: User Logica_pilot_NDK registered digital-document to urn:nbn:cz:mzk-008hfz.\n" +
            "[27.10.2025, 11:37:11] API: User Logica_pilot_NDK imported digital-instance with id: 1905902, urn:nbn:cz:mzk-008hfp, library: Kramerius K4.\n" +
            "[27.10.2025, 11:37:11] API: User Logica_pilot_NDK imported digital-instance with id: 1905903, urn:nbn:cz:mzk-008hfp, library: Kramerius 4.\n" +
            "[27.10.2025, 11:38:40] API: User Logica_pilot_NDK imported digital-instance with id: 1905904, urn:nbn:cz:mzk-008hfs, library: Kramerius K4.\n" +
            "[27.10.2025, 11:38:40] API: User Logica_pilot_NDK imported digital-instance with id: 1905905, urn:nbn:cz:mzk-008hfs, library: Kramerius 4.\n" +
            "[28.10.2025, 11:40:10] API: User Logica_pilot_NDK imported digital-instance with id: 1905906, urn:nbn:cz:mzk-008hfo, library: Kramerius K4.\n" +
            "[28.10.2025, 11:40:11] API: User Logica_pilot_NDK imported digital-instance with id: 1905907, urn:nbn:cz:mzk-008hfo, library: Kramerius 4.\n" +
            "[28.10.2025, 11:41:41] API: User Logica_pilot_NDK imported digital-instance with id: 1905908, urn:nbn:cz:mzk-008hfq, library: Kramerius K4.\n" +
            "[29.10.2025, 11:41:42] API: User Logica_pilot_NDK imported digital-instance with id: 1905909, urn:nbn:cz:mzk-008hfq, library: Kramerius 4.\n" +
            "[29.10.2025, 11:42:42] API: User Logica_pilot_NDK imported digital-instance with id: 1905910, urn:nbn:cz:mzk-008hfr, library: Kramerius K4.\n" +
            "[29.10.2025, 11:42:43] API: User Logica_pilot_NDK imported digital-instance with id: 1905911, urn:nbn:cz:mzk-008hfr, library: Kramerius 4.\n" +
            "[30.10.2025, 11:44:09] API: User Logica_pilot_NDK imported digital-instance with id: 1905912, urn:nbn:cz:mzk-008hft, library: Kramerius K4.\n" +
            "[30.10.2025, 11:44:10] API: User Logica_pilot_NDK imported digital-instance with id: 1905913, urn:nbn:cz:mzk-008hft, library: Kramerius 4.\n" +
            "[30.10.2025, 11:44:41] API: User Logica_pilot_NDK imported digital-instance with id: 1905914, urn:nbn:cz:mzk-008hfy, library: Kramerius K4.\n" +
            "[31.10.2025, 11:44:42] API: User Logica_pilot_NDK imported digital-instance with id: 1905915, urn:nbn:cz:mzk-008hfy, library: Kramerius 4.\n" +
            "[31.10.2025, 11:45:41] API: User Logica_pilot_NDK imported digital-instance with id: 1905916, urn:nbn:cz:mzk-008hfv, library: Kramerius K4.\n" +
            "[31.10.2025, 11:45:41] API: User Logica_pilot_NDK imported digital-instance with id: 1905917, urn:nbn:cz:mzk-008hfv, library: Kramerius 4.\n" +
            "[1.11.2025, 11:46:40] API: User Logica_pilot_NDK imported digital-instance with id: 1905918, urn:nbn:cz:mzk-008hfu, library: Kramerius K4.\n" +
            "[1.11.2025, 11:46:40] API: User Logica_pilot_NDK imported digital-instance with id: 1905919, urn:nbn:cz:mzk-008hfu, library: Kramerius 4.\n" +
            "[1.11.2025, 11:46:43] API: User Digitalizace_Vysocina registered digital-document to urn:nbn:cz:hbg001-0005rk.\n" +
            "[2.11.2025, 11:48:39] API: User Logica_pilot_NDK imported digital-instance with id: 1905920, urn:nbn:cz:mzk-008hfw, library: Kramerius K4.\n" +
            "[2.11.2025, 11:48:40] API: User Logica_pilot_NDK imported digital-instance with id: 1905921, urn:nbn:cz:mzk-008hfw, library: Kramerius 4.\n" +
            "[2.11.2025, 11:48:41] API: User Logica_pilot_NDK imported digital-instance with id: 1905922, urn:nbn:cz:mzk-008hfz, library: Kramerius K4.\n" +
            "[3.11.2025, 11:48:42] API: User Logica_pilot_NDK imported digital-instance with id: 1905923, urn:nbn:cz:mzk-008hfz, library: Kramerius 4.\n" +
            "[3.11.2025, 11:49:13] API: User Logica_pilot_NDK imported digital-instance with id: 1905924, urn:nbn:cz:mzk-008hfx, library: Kramerius K4.\n" +
            "[3.11.2025, 11:49:14] API: User Logica_pilot_NDK imported digital-instance with id: 1905925, urn:nbn:cz:mzk-008hfx, library: Kramerius 4.\n" +
            "[4.11.2025, 11:52:09] API: User Logica_pilot_NDK imported digital-instance with id: 1905926, urn:nbn:cz:mzk-008hfn, library: Kramerius K4.\n" +
            "[4.11.2025, 11:52:10] API: User Logica_pilot_NDK imported digital-instance with id: 1905927, urn:nbn:cz:mzk-008hfn, library: Kramerius 4.\n" +
            "[4.11.2025, 11:53:20] API: User Logica_pilot_NDK registered digital-document to urn:nbn:cz:mzk-008hg0.";

    @Override
    public String getLogs(Integer maxLines, LocalDate minDate, LocalDate dayAfterMaxDate) {
        //jedeme od konce
        String[] lines = LOGS.split("\n");
        List<String> linesFilteredAndReversed = new ArrayList<>();
        //System.out.println("Filtering logs with maxLines=" + maxLines + ", minDate=" + minDate + ", dayAfterMaxDate=" + dayAfterMaxDate);
        for (int i = lines.length; i > 0; i--) {
            String line = lines[i - 1];
            //konec, když dosáhneme maxLines
            if (maxLines != null && linesFilteredAndReversed.size() >= maxLines) {
                break;
            }
            //date filtering enabled
            if (minDate != null || dayAfterMaxDate != null) {
                String logDateStr = line.split(",")[0].substring(1); //'[10.10.2025, 07:58:08] ...'  ->  '10.10.2025'
                LocalDate logDate = LocalDate.parse(logDateStr, new DateTimeFormatterBuilder() //10.10.2025, ale i single-digit day/month
                        .appendValue(ChronoField.DAY_OF_MONTH)
                        .appendLiteral('.')
                        .appendValue(ChronoField.MONTH_OF_YEAR)
                        .appendLiteral('.')
                        .appendValue(ChronoField.YEAR)
                        .toFormatter());

                //přeskočit, pokud je datum logu větší než dayAfterMaxDate
                if (dayAfterMaxDate != null) {
                    if (logDate.isAfter(dayAfterMaxDate.minusDays(1))) {
                        //přeskočíme tento záznam
                        continue;
                    }
                }
                //konec, pokud je datum logu starší než minDate
                if (minDate != null) {
                    if (logDate.isBefore(minDate)) {
                        //končíme cyklus úplně, už budou jen starší záznamy
                        break;
                    }
                }
            }
            linesFilteredAndReversed.add(line + "\n");
        }
        //reverse zpět na správné pořadí a vrať jako string
        StringBuilder result = new StringBuilder();
        for (int i = linesFilteredAndReversed.size(); i > 0; i--) {
            result.append(linesFilteredAndReversed.get(i - 1));
        }
        return result.toString();
    }

}
