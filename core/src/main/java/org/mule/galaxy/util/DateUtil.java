package org.mule.galaxy.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static Calendar getCalendarForNow() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        return c;
    }
    
}
