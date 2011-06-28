package org.mule.galaxy.web.client.ui.util;

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
     * @param object
     * @return regular representation of provided camel case string ie. testName => Test Name
     */
    public static String camelCaseToRegular(final String string) {
        if (string.length() == 0) {
            return string;
        }

        final StringBuilder regular = new StringBuilder(string.length());
        regular.append(Character.toUpperCase(string.charAt(0)));
        for (int i = 1; i< string.length(); i++) {
            final char character = string.charAt(i);
            if (Character.isLowerCase(character)) {
                regular.append(character);
                if (string.length() > i+1 && Character.isUpperCase(string.charAt(i+1))) {
                    regular.append(" ");
                }
            } else {
                regular.append(character);
            }
        }
        return regular.toString();
    }

    /**
     * @param object
     * @return regular representation of provided camel case string ie. testName => Test Name
     */
    public static String toRegular(final String string) {
        if (!isConstant(string)) {
            return camelCaseToRegular(string);
        }

        return string;
    }

    public static boolean isConstant(final String string) {
        for (int i = 1; i< string.length(); i++) {
            final char character = string.charAt(i);
            if (!(Character.isUpperCase(character) || "_".equals(character))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Split a string (sentence) into two sections.
     *
     * @param s
     * @param len      - how many words should be in the first array element
     * @param ellipsis
     * @return
     */
    public static String[] wordCountSplitter(String s, int len, boolean ellipsis) {

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

        if (ellipsis && b.length() > 0) {
            if (a.endsWith(".")) {
                a = a.concat(".. ");
            } else {
                a = a.concat("... ");
            }
        }
        return new String[]{a, b};
    }

    public static native String getUserAgent() /*-{
        return navigator.userAgent.toLowerCase();
    }-*/;


}
