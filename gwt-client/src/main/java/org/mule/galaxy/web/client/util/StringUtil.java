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

    /**
     * Create a header and body panel. Tokenize by word count.
     *
     * @param s
     * @param len
     * @param ellipsis
     * @return
     */
    public static String[] createInlineHelpHeaderAndBody(String s, int len, boolean ellipsis) {

        String a = "";
        String b = "";

        int i = 0;
        for (String token : s.split("\\s+")) {
            if (len > i) {
                a = a.concat(token + " ");
            } else {
                b = b.concat(token + " ");
            }
            i++;
        }

        a = a.trim();

        if (ellipsis) {
            if (a.endsWith(".")) {
                a = a.concat("..");
            } else {
                a = a.concat("...");
            }
        }
        return new String[]{a, b};
    }

}
