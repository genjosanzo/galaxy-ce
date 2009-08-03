package org.mule.galaxy.impl.extension;

import static org.mule.galaxy.util.AbderaUtils.assertNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.mule.galaxy.Item;
import org.mule.galaxy.extension.AtomExtension;
import org.mule.galaxy.type.PropertyDescriptor;
import org.mule.galaxy.util.Constants;

public class MapExtension extends SimpleExtension implements AtomExtension {
    private static final QName MAP_QNAME = new QName(Constants.ATOM_NAMESPACE, "map");
    private static final QName ENTRY_QNAME = new QName(Constants.ATOM_NAMESPACE, "entry");
    private static final Collection<QName> UNDERSTOOD = new ArrayList<QName>();

    static {
        UNDERSTOOD.add(MAP_QNAME);
    }
    
    public void annotateAtomEntry(Item item, 
                                  PropertyDescriptor pd, 
                                  Entry entry,
                                  ExtensibleElement metadata,
                                  Factory factory) {
        Map<String,String> map = item.getProperty(pd.getProperty());
        
        if (map == null) {
            return;
        }
        
        Element mapEl = factory.newElement(MAP_QNAME, metadata);
        mapEl.setAttributeValue("property", pd.getProperty());
        for (Map.Entry e : map.entrySet()) {
            Element entryEl = factory.newElement(ENTRY_QNAME, mapEl);
            entryEl.setAttributeValue("key", e.getKey().toString());
            entryEl.setAttributeValue("value", e.getValue().toString());
        }
    }

    public Object getValue(Item item, ExtensibleElement e, Factory factory) throws ResponseContextException {
        Map<String, String> map = new HashMap<String,String>();
        
        for (Element entry : e.getExtensions(ENTRY_QNAME)) {
            String key = entry.getAttributeValue("key");
            String value = entry.getAttributeValue("value");
            
            assertNotNull(key, "Map key attribute cannot be null.");
            assertNotNull(value, "Map value attribute cannot be null.");

            map.put(key, value);
        }
        
        return map;            
    }

    public Collection<QName> getUnderstoodElements() {
        return UNDERSTOOD;
    }

}
