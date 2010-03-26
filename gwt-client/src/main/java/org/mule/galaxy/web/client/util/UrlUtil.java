package org.mule.galaxy.web.client.util;

import com.google.gwt.http.client.URL;

public class UrlUtil {

    public static String decode(String str) {
        return URL.decode(str).replaceAll("%2F", "/");
    }

    public static String encode(String str) {
        return URL.encode(str).replaceAll("/", "%2F");
    }

}
