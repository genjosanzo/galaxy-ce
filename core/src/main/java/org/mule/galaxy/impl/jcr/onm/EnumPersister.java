package org.mule.galaxy.impl.jcr.onm;

import javax.jcr.Node;
import javax.jcr.Session;

import org.mule.galaxy.api.Identifiable;
import org.mule.galaxy.impl.jcr.JcrUtil;

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
