package org.mule.galaxy.impl.jcr;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.mule.galaxy.Dao;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.NotFoundException;
import org.mule.galaxy.extension.Extension;
import org.mule.galaxy.security.AccessControlManager;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.Permission;
import org.mule.galaxy.type.PropertyDescriptor;
import org.mule.galaxy.type.Type;
import org.mule.galaxy.type.TypeManager;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrTemplate;

public class TypeManagerImpl implements TypeManager {
    private AccessControlManager accessControlManager;
    private Dao<PropertyDescriptor> propertyDescriptorDao;
    private Dao<Type> typeDao;
    private JcrTemplate jcrTemplate;
    
    public PropertyDescriptor getPropertyDescriptorByName(final String propertyName) {
        return find("property", propertyName);
    }

    private PropertyDescriptor find(final String value, final String propertyName) {
        return (PropertyDescriptor) jcrTemplate.execute(new JcrCallback() {
            public Object doInJcr(Session session) throws IOException, RepositoryException {
                List<PropertyDescriptor> pds = propertyDescriptorDao.find(value, propertyName);
                
                if (pds.size() == 0) {
                    return null;
                }
                return pds.get(0);
            }
        });
    }

    public Collection<PropertyDescriptor> getPropertyDescriptorsForExtension(String extensionId) {
        return propertyDescriptorDao.find("extension", extensionId);
    }

    public PropertyDescriptor getPropertyDescriptor(final String id) throws NotFoundException {
        return propertyDescriptorDao.get(id);
    }
    
    public Collection<PropertyDescriptor> getPropertyDescriptors(boolean includeIndex) {
        if (includeIndex) {
            return propertyDescriptorDao.listAll();
        } else {
            return propertyDescriptorDao.find("index", "false");
        }
    }

    public void savePropertyDescriptor(PropertyDescriptor pd) throws AccessException, DuplicateItemException, NotFoundException {
        accessControlManager.assertAccess(Permission.MANAGE_PROPERTIES);
        
        Map<String, String> config = pd.getConfiguration();
        if (pd.getExtension() != null) {
            Extension extension = pd.getExtension();
            for (String key : extension.getPropertyDescriptorConfigurationKeys()) {
                if (config == null || !config.keySet().contains(key)) {
                    throw new RuntimeException("Configuration key " + key + " must be specified.");
                }
            }
        }
        
        propertyDescriptorDao.save(pd);
    }
    
    public void deletePropertyDescriptor(String id) {
        propertyDescriptorDao.delete(id);
    }
    
    
    public void deleteType(String id) {
        typeDao.delete(id);
    }

    public Type getType(String id) throws NotFoundException {
        return typeDao.get(id);
    }
    
    public Type getDefaultType() {
        return typeDao.listAll().get(0);
    }

    public Collection<Type> getTypes() {
        return typeDao.listAll();
    }

    public void saveType(Type t) throws AccessException, DuplicateItemException,
        NotFoundException {
        typeDao.save(t);
    }

    public void setAccessControlManager(AccessControlManager accessControlManager) {
        this.accessControlManager = accessControlManager;
    }

    public void setJcrTemplate(JcrTemplate jcrTemplate) {
        this.jcrTemplate = jcrTemplate;
    }

    public void setPropertyDescriptorDao(Dao<PropertyDescriptor> pdDao) {
        this.propertyDescriptorDao = pdDao;
    }
    public Dao<Type> getTypeDao() {
        return typeDao;
    }
    public void setTypeDao(Dao<Type> typeDao) {
        this.typeDao = typeDao;
    }
    
}
