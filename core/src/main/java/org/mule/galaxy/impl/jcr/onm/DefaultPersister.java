package org.mule.galaxy.impl.jcr.onm;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.PropertyType;
import javax.jcr.Session;

import org.mule.galaxy.impl.jcr.JcrUtil;

public class DefaultPersister implements FieldPersister {

    private Map<Class, ClassPersister> classPersisters = Collections.synchronizedMap(new HashMap<Class, ClassPersister>());
    
    public Object build(Node node, FieldDescriptor fd, Session session) throws Exception {
        if (JcrUtil.isSimpleType(fd.getType())) {
            return JcrUtil.getProperty(fd.getName(), node);
        } else {
            return buildComplexObject(node, fd, session);
        }
    }


    public Object build(String id, FieldDescriptor fd, Session session) throws Exception {
        return id;
    }

    public void persist(Object o, Node n, FieldDescriptor fd, Session session) throws Exception {
        if (o instanceof Reference) {
            n.setProperty(fd.getName(), ((Reference)o).getId(), PropertyType.REFERENCE);
        } else if (JcrUtil.isSimpleType(fd.getType())) {
            JcrUtil.setProperty(fd.getName(), o, n);            
        } else {
            persistComplexObject(o, n, fd, session);
        }
    }

    private void persistComplexObject(Object o, Node n, FieldDescriptor fd, Session session) throws Exception {
        ClassPersister persister = getPersister(fd.getType());
        
        Node fieldNode = n.addNode(fd.getName());
        
        persister.persist(o, fieldNode, session);
    }

    private ClassPersister getPersister(Class<?> type) throws Exception {
        ClassPersister p = classPersisters.get(type);
        if (p == null) {
            p = new ClassPersister(type, null);
            classPersisters.put(type, p);
        }
        return p;
    }

    private Object buildComplexObject(Node node, FieldDescriptor fd, Session session) throws Exception {
        try {
            Node fieldNode = node.getNode(fd.getName());
            
            return getPersister(fd.getType()).build(fieldNode, session);
        } catch (PathNotFoundException e) {
            return null;
        }
    }
    
}
