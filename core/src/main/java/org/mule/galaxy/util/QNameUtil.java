package org.mule.galaxy.util;

import javax.xml.namespace.QName;

import org.w3c.dom.Element;

public class QNameUtil {
    public static QName fromString(String s) {
        if (s == null) {
            return null;
        }
        
        int i = s.indexOf('}');
        if (i == -1) {
            return new QName(s);
        }
        
        String ns = s.substring(1, i);
        String local = s.substring(i+1);
        
        return new QName(ns, local);
    }
    
    public static QName getName(Element el) {
        String name = el.getLocalName();
        String ns = el.getNamespaceURI();

        if ("".equals(ns) || ns == null) {
            return new QName(name);
        } else {
            return new QName(ns, name);
        }
    }
}
