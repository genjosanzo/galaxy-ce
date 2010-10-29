package org.mule.galaxy.impl.jcr.onm;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.galaxy.Identifiable;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.mapping.OneToMany;

/**
 * A simple way to persist/build objects to and from JCR nodes. Only works for a 
 * limited set of use cases, but is good enough for most of our usages.
 */
public class ClassPersister {

    private final Log log = LogFactory.getLog(getClass());

    private Class type;
    private Map<String, FieldDescriptor> propertyToFD = new HashMap<String, FieldDescriptor>();
    private List<String> parents = new ArrayList<String>();
    
    private PersisterManager persisterManager;
    private String path;
    
    public ClassPersister(Class type, String path) throws Exception {
        this.type = type;
        this.path = path;
        
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
            
            // Only persist the id field if we are not a top level node
            if (pd.getName().equals("id") && path != null) {
                continue;
            }
            
            FieldDescriptor fd = new FieldDescriptor();
            fd.setName(pd.getName());
            fd.setReadMethod(read);
            fd.setWriteMethod(write);
            fd.setType(pd.getPropertyType());
            
            if (Collection.class.isAssignableFrom(fd.getType())) {
                Type returnType = read.getGenericReturnType();
                
                if(returnType instanceof ParameterizedType){
                    ParameterizedType pType = (ParameterizedType) returnType;
                    Type[] typeArguments = pType.getActualTypeArguments();
                    for(Type typeArgument : typeArguments){
                        Class typeArgClass = (Class) typeArgument;
                        
                        fd.setComponentType(typeArgClass);
                    }
                }
            }
            OneToMany otm = read.getAnnotation(OneToMany.class);
            if (otm != null) {
                fd.setOneToMany(otm);
                
                parents.add(otm.mappedBy());
            }
            fd.setClassPersister(this);
            
            propertyToFD.put(pd.getName(), fd);
        }
    }
    
    public void setPersisterManager(PersisterManager persisterManager) {
        this.persisterManager = persisterManager;
    }

    public void persist(Object o, Node n, Session session) throws Exception {
        for (FieldDescriptor fd : propertyToFD.values()) {
            Object result = fd.getReadMethod().invoke(o, new Object[0]);
            FieldPersister persister = persisterManager.getPersister(fd.getType());
            
            persister.persist(result, n, fd, session);
        }
    }
    
    public Object build(Node n, Session session) throws Exception {
        Object o = type.newInstance();
        
        for (FieldDescriptor fd : propertyToFD.values()) {
            if (parents.contains(fd.getName())) {
                continue;
            }
            
            FieldPersister persister = persisterManager.getPersister(fd.getType());
            try {
                Object value = persister.build(n, fd, session);
                
                if (value != null) {
                    try {
                        fd.getWriteMethod().invoke(o, value);
                    } catch(IllegalArgumentException e) {
                        log.error("Error writing " + value + " with type " + value.getClass().getName() + " to " + fd.getName(), e);
                    }
                    
                }
            } catch (NotFoundException e) {
                n.setProperty(fd.getName(), (String)null);
                log.info("Item the value of the property '" + fd.getName() + "' on '" + n.getPath() + "' no longer exists: " + e.getMessage());
            }
        }
        
        return o;
    }

    public Object get(String id, Session session) throws Exception {
        return build(session.getNodeByUUID(id), session);
    }
    
    public String getId(Object o) {
        return ((Identifiable) o).getId();
    }
    
    public String getPath() {
        return path;
    }

    public Class getType() {
        return type;
    }
}
