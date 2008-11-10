package org.mule.galaxy.web.client.util;

import com.google.gwt.core.client.GWT;

public class StringUtil {


    public static String getFileExtension(String s) {
        int dotPos = s.lastIndexOf(".");
        String extension = "";
        if (dotPos != -1) {
            extension = s.substring(dotPos + 1);
        }
        GWT.log("File extension is " + extension, new Exception());
        return extension;
    }

}
