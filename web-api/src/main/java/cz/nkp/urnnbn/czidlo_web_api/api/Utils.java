package cz.nkp.urnnbn.czidlo_web_api.api;

import org.joda.time.DateTime;

import java.util.Date;

public class Utils {

    public static DateTime dateToDateTime(java.util.Date date) {
        if (date == null) {
            return null;
        } else {
            return new DateTime(date);
        }
    }

    public static Date dateTimeToDate(DateTime dateTime) {
        if (dateTime == null) {
            return null;
        } else {
            return dateTime.toDate();
        }
    }
}
