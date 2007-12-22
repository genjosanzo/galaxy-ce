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

public class DefaultPersister implements FieldPersister {

    public Object build(Node node, FieldDescriptor fd, Session session) throws Exception {
        return JcrUtil.getProperty(fd.getName(), node);
    }

    public void persist(Object o, Node n, FieldDescriptor fd, Session session) throws Exception {
        JcrUtil.setProperty(fd.getName(), o, n);
    }
    
//
//    public Object build(Node node, FieldDescriptor fd, Session session) throws Exception {
//        try {
//            String name = fd.getName();
//            Node child = node.getNode(name);
//            if (child == null) {
//                return null;
//            }
//            
//            String type = getStringOrNull(child, TYPE);
//            
//            if (type == null) {
//                Property property = child.getProperty(VALUE);
//                
//                Value val = property.getValue();
//                if (val == null) {
//                    return null;
//                }
//
//                switch (val.getType()) {
//                case PropertyType.STRING:
//                    return val.getString();
//                case PropertyType.BOOLEAN:
//                    return val.getBoolean();
//                case PropertyType.DATE:
//                    return val.getDate();
//                case PropertyType.DOUBLE:
//                    return val.getDouble();
//                case PropertyType.LONG:
//                    return val.getLong();
//                default:
//                    return null;
//                }
//            } 
//            
//            Collection<Object> values = null;
//            if (type.equals(Set.class.getName())) {
//                values = new HashSet<Object>();
//            } else {
//                values = new ArrayList<Object>();
//            }
//            
//            String component = JcrUtil.getStringOrNull(child, COMPONENT_TYPE);
//            Class componentCls = JcrUtil.class.getClassLoader().loadClass(component);
//            
//            for (NodeIterator itr = child.getNodes(); itr.hasNext();) {
//                Node next = itr.nextNode();
//
//                Object value = JcrUtil.getStringOrNull(next, VALUE);
//                
//                if (componentCls.equals(QName.class)) {
//                    value = QNameUtil.fromString(value.toString());
//                }
//                
//                values.add(value);
//            }
//            return values;
//        } catch (PathNotFoundException e) {
//            return null;
//        } catch (RepositoryException e) {
//            throw new RuntimeException(e);
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public void persist(Object value, Node n, FieldDescriptor fd, Session session) throws Exception {
//        String name = fd.getName();
//        
//        if (value instanceof Collection) {
//            Node child = JcrUtil.getOrCreate(n, name);
//            
//            Collection<?> c = (Collection<?>) value;
//            if (c.size() == 0) {
//                return;
//            }
//            
//            
//            if (c instanceof Set) {
//                child.setProperty(JcrUtil.TYPE, Set.class.getName());
//            } else if (c instanceof Map) {
//                throw new UnsupportedOperationException();
//            } else {
//                child.setProperty(JcrUtil.TYPE, Collection.class.getName());
//            }
//            
//            child.setProperty(JcrUtil.COMPONENT_TYPE, JcrUtil.getComponentType(c));
//            
//            for (Object o : c) {
//                Node valueNode = child.addNode(JcrUtil.VALUE);
//                valueNode.setProperty(JcrUtil.VALUE, o.toString());
//            }
//        } else {
//            Node child = JcrUtil.getOrCreate(n, name);
//            
//            if (value instanceof String) {
//                child.setProperty(JcrUtil.VALUE, value.toString());
//            } else if (value instanceof Calendar) {
//                child.setProperty(JcrUtil.VALUE, (Calendar) value);
//            } else if (value == null) {
//                child.setProperty(JcrUtil.VALUE, (String) null);
//            } else if (value instanceof Identifiable) {
//                child.setProperty(JcrUtil.VALUE, ((Identifiable) value).getId());
//            } else {
//                throw new UnsupportedOperationException("Unsupported type " + value.getClass());
//            }
//        }
//    }

    
}
