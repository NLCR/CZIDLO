package cz.nkp.urnnbn.client.processes;

import com.google.gwt.i18n.client.DateTimeFormat;

import java.util.Date;

/**
 * Created by Martin Řehánek on 2.11.18.
 */
public class AbstractFormatter {
    String formatDateTime(Long millis) {
        if (millis == null) {
            return null;
        } else {
            Date now = new Date();
            Date then = new Date(millis);
            if (isToday(then, now)) {
                return DateTimeFormat.getFormat("HH:mm:ss").format(then);
            } else if (isSameYear(then, now)) {
                return DateTimeFormat.getFormat("dd. M. HH:mm").format(then);
            } else { //another year
                return DateTimeFormat.getFormat("dd. M. yyyy").format(then);
            }
        }
    }

    boolean isSameYear(Date then, Date now) {
        return then.getYear() == now.getYear();
    }

    boolean isToday(Date then, Date now) {
        return then.getYear() == now.getYear() &&
                then.getMonth() == now.getMonth() &&
                then.getDay() == now.getDay();
    }

    String formatDuration(long duration) {
        long hours = duration / 3600000;
        duration = duration % 3600000;
        long minutes = duration / 60000;
        duration = duration % 60000;
        long seconds = duration / 1000;
        duration = duration % 1000;
        long millis = duration;
        StringBuilder builder = new StringBuilder();
        if (hours != 0) {
            builder.append(hours).append("h");
        }
        if (minutes != 0) {
            builder.append(' ').append(minutes).append("m");
        }
        if (seconds != 0) {
            builder.append(' ').append(seconds).append("s");
        }
        if (millis != 0 && (hours == 0 && minutes == 0)) {
            builder.append(' ').append(millis).append("ms");
        }
        return builder.toString();
    }
}
