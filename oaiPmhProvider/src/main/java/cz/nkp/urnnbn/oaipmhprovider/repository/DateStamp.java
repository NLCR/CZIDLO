/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.oaipmhprovider.repository;

import java.util.TimeZone;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.ISOChronology;

/**
 *
 * @author Martin Řehánek
 */
public class DateStamp implements Comparable {

    private static Chronology chronology = ISOChronology.getInstance(DateTimeZone.forTimeZone(TimeZone.getDefault()));
    public static DateStamp MIN = new DateStamp("1970-01-01T00:00:00Z");
    public static DateStamp MAX = new DateStamp("9999-01-01T00:00:00Z");
    private DateTime dt;

    public static DateStamp parse(String dateStamp) {
        return new DateStamp(dateStamp);
    }

    public static DateStamp now() {
        DateTime now = nowLd();
        return new DateStamp(now);
    }

    public static DateStamp daysAgo(int i) {
        DateTime now = nowLd();
        DateTime yesteray = now.minusDays(i);
        return new DateStamp(yesteray);
    }

    public static DateStamp fromMilliseconds(long milliseconds) {
        return new DateStamp(new DateTime(milliseconds, chronology));
    }

    private static DateTime nowLd() {
        return new DateTime(chronology);
    }

    public DateStamp() {
        this(nowLd());
    }

    public DateStamp(DateTime dt) {
        this.dt = dt;
    }

    public DateStamp(String year, String month, String day) {
        int yearInt = Integer.valueOf(year).intValue();
        int monthInt = Integer.valueOf(month).intValue();
        int dayInt = Integer.valueOf(day).intValue();
        dt = new DateTime(yearInt, monthInt, dayInt, 0, 0, 0, 0, chronology);
    }

    public DateStamp(String dateStamp) {
        String[] tokens = dateStamp.split("T");
        String[] yearMonthDay = tokens[0].split("-");
        String[] hourMinuteSecondZ = tokens[1].split(":");
        int year = Integer.parseInt(yearMonthDay[0]);
        int month = Integer.parseInt(yearMonthDay[1]);
        int day = Integer.parseInt(yearMonthDay[2]);
        int hour = Integer.parseInt(hourMinuteSecondZ[0]);
        int minute = Integer.parseInt(hourMinuteSecondZ[1]);
        int second = Integer.parseInt(hourMinuteSecondZ[2].substring(0, hourMinuteSecondZ[2].length() - 1));
        dt = new DateTime(year, month, day, hour, minute, second, 0);
    }

    public DateTime getDateTime() {
        return dt;
    }

    @Override
    public String toString() {
        return getDateTime().toString().split("\\.")[0] + 'Z';
    }

    public boolean isBefore(DateStamp dateStamp) {
        return getDateTime().isBefore(dateStamp.getDateTime());
    }

    public boolean isAfter(DateStamp dateStamp) {
        return getDateTime().isAfter(dateStamp.getDateTime());
    }

    public boolean isEqual(DateStamp dateStamp) {
        return getDateTime().isEqual(dateStamp.getDateTime());
    }

    public boolean isBeforeOrEqual(DateStamp dateStamp) {
        return isBefore(dateStamp) || isEqual(dateStamp);
    }

    public boolean isAfterOrEqual(DateStamp dateStamp) {
        return isAfter(dateStamp) || isEqual(dateStamp);
    }

    public long getMillis() {
        return dt.getMillis();
    }

    @Override
    public int compareTo(Object object) {
        if (object == null) {
            throw new NullPointerException();
        }
        if (object instanceof DateStamp) {
            DateTime secondDt = ((DateStamp) object).getDateTime();
            return dt.compareTo(secondDt);
        } else {
            throw new IllegalArgumentException("object must be an instance of DateStamp");
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof DateStamp)) {
            return false;
        }
        return dt.equals(((DateStamp) obj).getDateTime());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.dt != null ? this.dt.hashCode() : 0);
        return hash;
    }
}
