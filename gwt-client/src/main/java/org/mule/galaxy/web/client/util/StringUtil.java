package org.mule.galaxy.web.client.util;

public class StringUtil {


    public static String getFileExtension(String s) {
        int dotPos = s.lastIndexOf(".");
        String extension = "";
        if (dotPos != -1) {
            extension = s.substring(dotPos + 1);
        }
        return extension;
    }

    public static String[] createInlineHelpHeaderAndBody(String s, int len, boolean ellipsis) {
        String a = s.substring(0, len);
        if (ellipsis) {
            a = a.concat("...");
        }
        String b = s.substring(len);
        return new String[]{a, b};
    }

}
