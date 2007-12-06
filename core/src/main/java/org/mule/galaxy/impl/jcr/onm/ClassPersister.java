package org.mule.galaxy.impl.jcr.onm;

import static org.mule.galaxy.impl.jcr.JcrUtil.*;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

/**
 * A simple way to persist/build objects to and from JCR nodes. Only works for a 
 * limited set of use cases, but is good enough for most of our usages.
 */
public class ClassPersister {
    
    private Class type;
    private Map<String, Method> propertyToSetter = new HashMap<String, Method>();
    private Map<Method, String> getterToProperty = new HashMap<Method, String>();
    private Map<String, Class> propertyToType = new HashMap<String, Class>();
    
    private PersisterManager persisterManager;
    
    public ClassPersister(Class type, PersisterManager persisterManager) throws Exception {
        this.type = type;
        this.persisterManager = persisterManager;
        
        BeanInfo info = Introspector.getBeanInfo(type);
        for (PropertyDescriptor pd : info.getPropertyDescriptors()) {
            Method read = pd.getReadMethod();
            Method write = pd.getWriteMethod();
            
            if (read == null || write == null) {
                continue;
            }
            
            if (read.getDeclaringClass().equals(Object.class) 
                || write.getDeclaringClass().equals(Object.class)) {
                continue;
            }
            
            if (pd.getName().equals("id")) {
                continue;
            }
            
            propertyToType.put(pd.getName(), pd.getPropertyType());
            propertyToSetter.put(pd.getName(), write);
            getterToProperty.put(read, pd.getName());
        }
    }
    
    /** {@inheritDoc}*/
    public void persist(Object o, Node n) throws Exception {
        for (Map.Entry<Method, String> e : getterToProperty.entrySet()) {
            Object result = e.getKey().invoke(o, new Object[0]);
            String name = e.getValue();
            FieldPersister persister = persisterManager.getPersister(propertyToType.get(name));
            
            persister.persist(result, n, name);
        }
    }
    
    /** {@inheritDoc}*/
    public Object build(Node n) throws Exception {
        Object o = type.newInstance();
        
        for (Map.Entry<String, Method> e : propertyToSetter.entrySet()) {
            Method method = e.getValue();
            String name = e.getKey();
            
            FieldPersister persister = persisterManager.getPersister(propertyToType.get(name));
            Object value = persister.build(n, name);
            
            method.invoke(o, value);
        }
        
        return o;
    }
}
