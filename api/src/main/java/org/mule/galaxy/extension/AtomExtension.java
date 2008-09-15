package org.mule.galaxy.extension;

import java.util.Collection;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.protocol.server.context.ResponseContextException;
import org.mule.galaxy.Item;
import org.mule.galaxy.type.PropertyDescriptor;

public interface AtomExtension {
    void annotateAtomEntry(Item item, PropertyDescriptor pd, Entry entry, ExtensibleElement metadata, Factory factory);
    
    void updateItem(Item item, Factory factory, ExtensibleElement e) throws ResponseContextException;
    
    Collection<QName> getUnderstoodElements();
}
