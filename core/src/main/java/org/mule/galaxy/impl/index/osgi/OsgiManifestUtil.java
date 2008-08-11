/*
 * Based on the Knopflerfish implementation.
 *
 * Copyright (c) 2003-2006, KNOPFLERFISH project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following
 * conditions are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials
 *   provided with the distribution.
 *
 * - Neither the name of the KNOPFLERFISH project nor the names of its
 *   contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.mule.galaxy.impl.index.osgi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OsgiManifestUtil
{

    /**
     * Parse strings of format:
     * <p/>
     * ENTRY (, ENTRY)*
     *
     * @param d Directive being parsed
     * @param s String to parse
     *
     * @return A sorted ArrayList with enumeration or null if enumeration string was null.
     *
     * @throws IllegalArgumentException If syntax error in input string.
     */
    public static List<String> parseEnumeration(String d, String s)
    {
        ArrayList<String> result = new ArrayList<String>();
        if (s != null)
        {
            AttributeTokenizer at = new AttributeTokenizer(s);
            do
            {
                String key = at.getKey();
                if (key == null)
                {
                    throw new IllegalArgumentException("Directive " + d + ", unexpected character at: "
                                                       + at.getRest());
                }
                if (!at.getEntryEnd())
                {
                    throw new IllegalArgumentException("Directive " + d + ", expected end of entry at: "
                                                       + at.getRest());
                }
                int i = Math.abs(binarySearch(result, strComp, key) + 1);
                result.add(i, key);
            }
            while (!at.getEnd());
            return result;
        }
        else
        {
            return Collections.emptyList();
        }
    }

    /**
     * Parse strings of format:
     * <p/>
     * ENTRY (, ENTRY)* ENTRY = key (; key)* (; PARAM)* PARAM = attribute '=' value PARAM = directive ':=' value
     *
     * @param a            Attribute being parsed
     * @param s            String to parse
     * @param single       If true, only allow one key per ENTRY
     * @param unique       Only allow unique parameters for each ENTRY.
     * @param single_entry If true, only allow one ENTRY is allowed.
     *
     * @return Iterator(Map(param -> value)) or null if input string is null.
     *
     * @throws IllegalArgumentException If syntax error in input string.
     */
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> parseEntries(String a, String s, boolean single, boolean unique, boolean single_entry)
    {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (s != null)
        {
            AttributeTokenizer at = new AttributeTokenizer(s);
            do
            {
                List<String> keys = new ArrayList<String>();
                Map<String, Object> params = new HashMap<String, Object>();
                String key = at.getKey();
                if (key == null)
                {
                    throw new IllegalArgumentException("Definition, " + a + ", expected key at: " + at.getRest());
                }
                if (!single)
                {
                    keys.add(key);
                    while ((key = at.getKey()) != null)
                    {
                        keys.add(key);
                    }
                }
                String param;
                while ((param = at.getParam()) != null)
                {
                    List<String> old = (List<String>) params.get(param);
                    boolean is_directive = at.isDirective();
                    if (old != null && unique)
                    {
                        throw new IllegalArgumentException("Definition, " + a + ", duplicate " +
                                                           (is_directive ? "directive" : "attribute") +
                                                           ": " + param);
                    }
                    String value = at.getValue();
                    if (value == null)
                    {
                        throw new IllegalArgumentException("Definition, " + a + ", expected value at: " + at.getRest());
                    }
                    if (is_directive)
                    {
                        // NYI Handle directives and check them
                        // This method has become very ugly, please rewrite.
                    }
                    if (unique)
                    {
                        params.put(param, value);
                    }
                    else
                    {
                        if (old == null)
                        {
                            old = new ArrayList<String>();
                            params.put(param, old);
                        }
                        old.add(value);
                    }
                }
                if (at.getEntryEnd())
                {
                    if (single)
                    {
                        params.put("key", key);
                    }
                    else
                    {
                        params.put("keys", keys);
                    }
                    result.add(params);
                }
                else
                {
                    throw new IllegalArgumentException("Definition, " + a + ", expected end of entry at: " + at.getRest());
                }
                if (single_entry && !at.getEnd())
                {
                    throw new IllegalArgumentException("Definition, " + a + ", expected end of single entry at: " + at.getRest());
                }
            }
            while (!at.getEnd());
        }
        return result;
    }


    public interface Comparator
    {
        public int compare(Object a, Object b);
    }


    /**
     * Do binary search for a package entry in the list with the same version number add the specifies package entry.
     *
     * @param pl Sorted list of package entries to search.
     * @param p  Package entry to search for.
     *
     * @return index of the found entry. If no entry is found, return <tt>(-(<i>insertion point</i>) - 1)</tt>.  The
     *         insertion point</i> is defined as the point at which the key would be inserted into the list.
     */
    public static int binarySearch(List<String> pl, Comparator c, Object p)
    {
        int l = 0;
        int u = pl.size() - 1;

        while (l <= u)
        {
            int m = (l + u) / 2;
            int v = c.compare(pl.get(m), p);
            if (v > 0)
            {
                l = m + 1;
            }
            else
            {
                if (v < 0)
                {
                    u = m - 1;
                }
                else
                {
                    return m;
                }
            }
        }
        return -(l + 1);  // key not found.
    }

    static final Comparator strComp = new Comparator()
    {
        /**
         * String compare
         *
         * @param oa Object to compare.
         * @param ob Object to compare.
         * @return Return 0 if equals, negative if first object is less than second
         *         object and positive if first object is larger then second object.
         * @exception ClassCastException if objects are not a String objects.
         */
        public int compare(Object oa, Object ob) throws ClassCastException
        {
            String a = (String) oa;
            String b = (String) ob;
            return a.compareTo(b);
        }
    };


}


/**
 * Class for tokenize an attribute string.
 */
class AttributeTokenizer
{

    String s;
    int length;
    int pos = 0;

    AttributeTokenizer(String input)
    {
        s = input;
        length = s.length();
    }

    String getWord()
    {
        skipWhite();
        boolean backslash = false;
        boolean quote = false;
        StringBuffer val = new StringBuffer();
        int end = 0;
        loop:
        for (; pos < length; pos++)
        {
            if (backslash)
            {
                backslash = false;
                val.append(s.charAt(pos));
            }
            else
            {
                char c = s.charAt(pos);
                switch (c)
                {
                case '"':
                    quote = !quote;
                    end = val.length();
                    break;
                case '\\':
                    backslash = true;
                    break;
                case ',':
                case ':':
                case ';':
                case '=':
                    if (!quote)
                    {
                        break loop;
                    }
                    // Fall through
                default:
                    val.append(c);
                    if (!Character.isWhitespace(c))
                    {
                        end = val.length();
                    }
                    break;
                }
            }
        }
        if (quote || backslash || end == 0)
        {
            return null;
        }
        char[] res = new char[end];
        val.getChars(0, end, res, 0);
        return new String(res);
    }

    String getKey()
    {
        if (pos >= length)
        {
            return null;
        }
        int save = pos;
        if (s.charAt(pos) == ';')
        {
            pos++;
        }
        String res = getWord();
        if (res != null)
        {
            if (pos == length)
            {
                return res;
            }
            char c = s.charAt(pos);
            if (c == ';' || c == ',')
            {
                return res;
            }
        }
        pos = save;
        return null;
    }

    String getParam()
    {
        if (pos == length || s.charAt(pos) != ';')
        {
            return null;
        }
        int save = pos++;
        String res = getWord();
        if (res != null)
        {
            if (pos < length && s.charAt(pos) == '=')
            {
                return res;
            }
            if (pos + 1 < length && s.charAt(pos) == ':' && s.charAt(pos + 1) == '=')
            {
                return res;
            }
        }
        pos = save;
        return null;
    }

    boolean isDirective()
    {
        if (pos + 1 < length && s.charAt(pos) == ':')
        {
            pos++;
            return true;
        }
        else
        {
            return false;
        }
    }

    String getValue()
    {
        if (s.charAt(pos) != '=')
        {
            return null;
        }
        int save = pos++;
        skipWhite();
        String val = getWord();
        if (val == null)
        {
            pos = save;
            return null;
        }
        return val;
    }

    boolean getEntryEnd()
    {
        int save = pos;
        skipWhite();
        if (pos == length)
        {
            return true;
        }
        else
        {
            if (s.charAt(pos) == ',')
            {
                pos++;
                return true;
            }
            else
            {
                pos = save;
                return false;
            }
        }
    }

    boolean getEnd()
    {
        int save = pos;
        skipWhite();
        if (pos == length)
        {
            return true;
        }
        else
        {
            pos = save;
            return false;
        }
    }

    String getRest()
    {
        String res = s.substring(pos).trim();
        return res.length() == 0 ? "<END OF LINE>" : res;
    }

    private void skipWhite()
    {
        for (; pos < length; pos++)
        {
            if (!Character.isWhitespace(s.charAt(pos)))
            {
                break;
            }
        }
    }


}
