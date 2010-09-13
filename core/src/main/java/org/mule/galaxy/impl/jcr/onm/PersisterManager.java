package org.mule.galaxy.impl.jcr.onm;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mule.galaxy.Dao;
import org.mule.galaxy.impl.jcr.CollectionPersister;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springmodules.jcr.SessionFactory;

/**
 * 
 */
public class PersisterManager implements BeanPostProcessor {
    private Map<String, FieldPersister> persisters = new HashMap<String, FieldPersister>();
    private Map<String, ClassPersister> classPersisters = new HashMap<String, ClassPersister>();
    private FieldPersister defaultPersister = new DefaultPersister();
    private EnumPersister enumPersister = new EnumPersister();
    private SessionFactory sessionFactory;
    
    public PersisterManager() {
        super();
        
        persisters.put(Set.class.getName(), new CollectionPersister(HashSet.class, this));
        persisters.put(List.class.getName(), new CollectionPersister(ArrayList.class, this));
        persisters.put(Class.class.getName(), new ClassFieldPersister());
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // Now that all the proxies are init'd, use the proxied version so we get caching goodness
        if (bean instanceof Dao && bean instanceof Proxy) {
            Dao dao = (Dao)bean;
            String type = dao.getTypeClass().getName();
            getPersisters().put(type, new DaoPersister(dao));
        }
        
        return bean;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Dao) {
            Dao dao = (Dao)bean;
            
            addDao(dao);
        }
        
        return bean;
    }

    public void addDao(Dao dao) {
        String type = dao.getTypeClass().getName();
        getPersisters().put(type, new DaoPersister(dao));
        
        if (dao instanceof AbstractReflectionDao) {
            try {
                ClassPersister p = new ClassPersister(dao.getTypeClass(), ((AbstractReflectionDao)dao).getRootNodeName());
                p.setPersisterManager(this);
                classPersisters.put(type, p);
                ((AbstractReflectionDao) dao).setPersister(p);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public FieldPersister getPersister(Class<?> c) {
        FieldPersister p = persisters.get(c.getName());
        if (p == null) {
            if (Enum.class.isAssignableFrom(c)) {
                persisters.put(c.getName(), enumPersister);
                return enumPersister;
            } 
            
            p = defaultPersister;
        }
        
        return p;
    }

    public Map<String, FieldPersister> getPersisters() {
        return persisters;
    }

    public void setPersisters(Map<String, FieldPersister> persisters) {
        // Working around some weird spring bug...
        this.persisters.putAll(persisters);
    }

    public FieldPersister getDefaultPersister() {
        return defaultPersister;
    }

    public void setDefaultPersister(FieldPersister defaultPersister) {
        this.defaultPersister = defaultPersister;
    }

    public ClassPersister getClassPersister(String name) {
        return classPersisters.get(name);
    }
    
    public void setClassPersisters(Map<String, ClassPersister> classPersister) {
        this.classPersisters = classPersister;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
}
