package org.mule.galaxy.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.mule.galaxy.Item;
import org.mule.galaxy.artifact.Artifact;

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

    public static void copyArtifact(Item version, String path) throws IOException {
        Artifact a = version.getProperty("artifact");
        
        File file = new File(path);
        file.createNewFile();
        FileOutputStream out = new FileOutputStream(path);
        IOUtils.copy(a.getInputStream(), out);
        out.close();
    }

}
