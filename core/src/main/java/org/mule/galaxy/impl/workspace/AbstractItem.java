package org.mule.galaxy.impl.workspace;

import java.util.Iterator;

import org.mule.galaxy.Item;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.PropertyInfo;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.security.AccessException;

public abstract class AbstractItem {

    protected String id;
    
    public abstract void delete() throws RegistryException, AccessException;

    public String getId() {
	return id;
    }

    public Iterator<PropertyInfo> getProperties() {
	// TODO Auto-generated method stub
	return null;
    }

    public Object getProperty(String name) {
	// TODO Auto-generated method stub
	return null;
    }

    public PropertyInfo getPropertyInfo(String name) {
	// TODO Auto-generated method stub
	return null;
    }

    public boolean hasProperty(String name) {
	// TODO Auto-generated method stub
	return false;
    }

    public void setLocked(String name, boolean locked) {
	// TODO Auto-generated method stub
	
    }

    public void setProperty(String name, Object value) throws PropertyException {
	// TODO Auto-generated method stub
	
    }

    public void setVisible(String property, boolean visible) {
	// TODO Auto-generated method stub
	
    }

}
