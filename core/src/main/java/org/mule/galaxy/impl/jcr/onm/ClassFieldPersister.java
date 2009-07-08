package org.mule.galaxy.impl.jcr.onm;

import javax.jcr.Node;
import javax.jcr.Session;

import org.mule.galaxy.impl.jcr.JcrUtil;

public class ClassFieldPersister implements FieldPersister {

    public Object build(Node node, FieldDescriptor fd, Session session) throws Exception {
        String value = JcrUtil.getProperty(fd.getName(), node);
        
        if (value == null) {
            return null;
        }

        return build(value, fd, session);
    }

    public Object build(String id, FieldDescriptor fd, Session session) throws Exception {
        return getClass().getClassLoader().loadClass(id);
    }

    public void persist(Object o, Node n, FieldDescriptor fd, Session session) throws Exception {
        JcrUtil.setProperty(fd.getName(), ((Class) o).getName(), n);
    }
    
}
