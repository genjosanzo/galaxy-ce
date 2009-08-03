package org.mule.galaxy.util;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GalaxyUtils {

    public static Calendar getCalendarForNow() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        return c;
    }

    public static <K, T> Map<K,T> asMap(K key, T val) {
        Map<K, T> map = new HashMap<K,T>();
        map.put(key, val);
        return map;
    }

}
