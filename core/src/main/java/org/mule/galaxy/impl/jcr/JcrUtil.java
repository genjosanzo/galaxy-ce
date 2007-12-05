package org.mule.galaxy.impl.jcr;

import static org.mule.galaxy.impl.jcr.JcrUtil.getOrCreate;
import static org.mule.galaxy.impl.jcr.JcrUtil.setProperty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;
import javax.xml.namespace.QName;

import org.mule.galaxy.Identifiable;
import org.mule.galaxy.util.QNameUtil;

public class JcrUtil {

    public static final String VALUE = "__value";
    private static final String TYPE = "__type";
    private static final String COMPONENT_TYPE = "__componentType";
    
    /** Recursively outputs the contents of the given node. */
    public static void dump(Node node) throws RepositoryException {
        // First output the node path
        System.out.println(node.getPath());
        // Skip the virtual (and large!) jcr:system subtree
        if (node.getName().equals("jcr:system")) {
            return;
        }

        // Then output the properties
        PropertyIterator properties = node.getProperties();
        while (properties.hasNext()) {
            Property property = properties.nextProperty();
            if (property.getDefinition().isMultiple()) {
                // A multi-valued property, print all values
                Value[] values = property.getValues();
                for (int i = 0; i < values.length; i++) {
                    System.out.println(
                        property.getPath() + " = " + values[i].getString());
                }
            } else {
                // A single-valued property
                System.out.println(
                    property.getPath() + " = " + property.getString());
            }
        }

        // Finally output all the child nodes recursively
        NodeIterator nodes = node.getNodes();
        while (nodes.hasNext()) {
            dump(nodes.nextNode());
        }
    }

    public static Node getOrCreate(Node node, String name) throws RepositoryException, ItemExistsException,
        PathNotFoundException, VersionException, ConstraintViolationException, LockException,
        NoSuchNodeTypeException {
        
        Node child = null;
        try {
            child = node.getNode(name);
        } catch (PathNotFoundException e) {
    
        }
    
        if (child == null) {
            child = node.addNode(name);
            child.addMixin("mix:referenceable");
        }
        return child;
    }

    public static void removeChildren(Node docTypesNode) 
        throws VersionException, LockException, ConstraintViolationException, RepositoryException {
        for (NodeIterator itr = docTypesNode.getNodes(); itr.hasNext();) {
            itr.nextNode().remove();
        }
    }

    public static void removeChildren(Node node, String name)
        throws VersionException, LockException, ConstraintViolationException, RepositoryException {
        for (NodeIterator itr = node.getNodes(); itr.hasNext();) {
            Node child = itr.nextNode();
            if (child.getName().equals(name))
                child.remove();
        }
    }

    public static String getStringOrNull(Node node, String propName) {
        try {
            Value v = getValueOrNull(node, propName);   
            if (v != null) {
                return v.getString();
            }
        } catch (ValueFormatException e) {
            throw new RuntimeException(e);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }  
        
        return null;
    }
    
    public static Calendar getDateOrNull(Node node, String propName) {
        try {
            Value v = getValueOrNull(node, propName);   
            if (v != null) {
                return v.getDate();
            }
        } catch (ValueFormatException e) {
            throw new RuntimeException(e);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }  
        
        return null;
    }
    
    public static Value getValueOrNull(Node node, String propName) throws PathNotFoundException, RepositoryException {
        Property p = null;
        try {
            p = node.getProperty(propName);
        } catch (PathNotFoundException e) {
            return null;
        }
        
        if (p == null) {
            return null;
        }
        
        return p.getValue();
    }

    public static void setProperty(String name, Object value, Node n) throws ItemExistsException,
        PathNotFoundException, VersionException, ConstraintViolationException, LockException,
        RepositoryException {
        if (value instanceof Map) {
            setMap(n, name, (Map<?, ?>) value);
        } else if (value instanceof Collection) {
            Node child = getOrCreate(n, name);
            
            Collection<?> c = (Collection<?>) value;
            
            if (c instanceof Set) {
                n.setProperty(TYPE, Set.class.getName());
            } else if (c instanceof Map) {
                n.setProperty(TYPE, Map.class.getName());
            } else {
                n.setProperty(TYPE, Collection.class.getName());
            }
            
            n.setProperty(COMPONENT_TYPE, getComponentType(c));
            
            for (Object o : c) {
                Node valueNode = child.addNode(VALUE);
                valueNode.setProperty(VALUE, o.toString());
            }
        } else if (value instanceof String) {
            n.setProperty(name, value.toString());
        } else if (value instanceof Calendar) {
            n.setProperty(name, (Calendar) value);
        } else if (value == null) {
            n.setProperty(name, (String) null);
        } else if (value instanceof Identifiable) {
            n.setProperty(name, ((Identifiable) value).getId());
        } else {
            throw new UnsupportedOperationException("Unsupported type " + value.getClass());
        }
    }

    public static void setMap(Node n, String name, Map<?,?> result) throws RepositoryException {
        Node mapNode = getOrCreate(n, name);

        // TODO: make this lazy and write a LazyNodeMap
        for (Map.Entry<?,?> e : result.entrySet()) {
            // TODO: handle more complex maps
            setProperty((String) e.getKey(), e.getValue(), mapNode);
        }
    }

    private static String getComponentType(Collection<?> c) {
        return c.iterator().next().getClass().getName();
    }

    public static Object getProperty(String name, Node node) {
        try {
            Property property = null;
            
            try {
                property = node.getProperty(name);
                
                Value val = property.getValue();
                if (val == null) {
                    return null;
                }

                switch (val.getType()) {
                case PropertyType.STRING:
                    return val.getString();
                case PropertyType.BOOLEAN:
                    return val.getBoolean();
                case PropertyType.DATE:
                    return val.getDate();
                case PropertyType.DOUBLE:
                    return val.getDouble();
                case PropertyType.LONG:
                    return val.getLong();
                default:
                    return null;
                }
            } catch (PathNotFoundException e) {
            } 
            
            Node child = node.getNode(name);
            if (child == null) {
                return null;
            }
            
            String type = getStringOrNull(node, TYPE);
            Collection<Object> values = null;
            if (type.equals(Set.class.getName())) {
                values = new HashSet<Object>();
            } else {
                values = new ArrayList<Object>();
            }
            
            String component = JcrUtil.getStringOrNull(node, COMPONENT_TYPE);
            Class componentCls = JcrUtil.class.getClassLoader().loadClass(component);
            
            for (NodeIterator itr = child.getNodes(); itr.hasNext();) {
                Node next = itr.nextNode();

                Object value = JcrUtil.getStringOrNull(next, VALUE);
                
                if (componentCls.equals(QName.class)) {
                    value = QNameUtil.fromString(value.toString());
                }
                
                values.add(value);
            }
            return values;
        } catch (PathNotFoundException e) {
            return getStringOrNull(node, name);
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
