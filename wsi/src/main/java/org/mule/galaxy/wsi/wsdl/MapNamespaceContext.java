package org.mule.galaxy.wsi.wsdl;

import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;

public  class MapNamespaceContext implements NamespaceContext {
    private Map<String, String> namespaces;

    public MapNamespaceContext(Map<String, String> namespaces) {
        super();
        this.namespaces = namespaces;
    }

    public String getNamespaceURI(String prefix) {
        return namespaces.get(prefix);
    }

    public String getPrefix(String namespaceURI) {
        for (Map.Entry<String, String> e : namespaces.entrySet()) {
            if (e.getValue().equals(namespaceURI)) {
                return e.getKey();
            }
        }
        return null;
    }

    public Iterator getPrefixes(String namespaceURI) {
        return null;
    }

}

