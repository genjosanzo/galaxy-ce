package org.mule.galaxy.impl.jcr.onm;

import static org.mule.galaxy.impl.jcr.JcrUtil.COMPONENT_TYPE;
import static org.mule.galaxy.impl.jcr.JcrUtil.TYPE;
import static org.mule.galaxy.impl.jcr.JcrUtil.VALUE;
import static org.mule.galaxy.impl.jcr.JcrUtil.getStringOrNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.xml.namespace.QName;

import org.mule.galaxy.Identifiable;
import org.mule.galaxy.impl.jcr.JcrUtil;
import org.mule.galaxy.util.QNameUtil;

public class EnumPersister implements FieldPersister {

    @SuppressWarnings("unchecked")
    public Object build(Node node, FieldDescriptor fd, Session session) throws Exception {
        String value = (String) JcrUtil.getProperty(fd.getName(), node);
        
        if (value == null) {
            return null;
        }
        
       return Enum.valueOf((Class<? extends Enum>)fd.getType(), value);
    }

    public void persist(Object o, Node n, FieldDescriptor fd, Session session) throws Exception {
        JcrUtil.setProperty(fd.getName(), o.toString(), n);
    }
    
}
