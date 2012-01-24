/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.nkp.urnnbn.core;

import java.sql.Timestamp;
import org.joda.time.DateTime;

/**
 * //TODO: handle time zones properly
 * @author Martin Řehánek
 */
public class Utils {

    public static Timestamp nowTs(){
        return datetimeToTimestamp(new DateTime());
    }
    
    public static Timestamp datetimeToTimestamp(DateTime dt) {
        long millis = dt.getMillis();
        return new Timestamp(millis);
    }

    public static DateTime timestampToDatetime(Timestamp ts) {
        long millis = ts.getTime();
        return new DateTime(millis);
    }
}
