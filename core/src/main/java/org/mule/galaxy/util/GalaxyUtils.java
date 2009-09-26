package org.mule.galaxy.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
    
    public static void expand(String jarPath, String dest) throws IOException, FileNotFoundException {
        JarFile jar = new JarFile(jarPath);
        try {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();

                File file = new File(dest, entry.getName());
                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                    FileOutputStream output = new FileOutputStream(file);
                    try {
                        IOUtils.copy(jar.getInputStream(entry), output);
                    } finally {
                        output.close();
                    }
                }
            }
        } finally {
            jar.close();
        }
    }
}
