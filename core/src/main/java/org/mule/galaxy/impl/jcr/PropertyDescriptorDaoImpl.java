package org.mule.galaxy.impl.jcr;

import org.mule.galaxy.impl.jcr.onm.AbstractReflectionDao;
import org.mule.galaxy.security.Group;
import org.mule.galaxy.type.PropertyDescriptor;

public class PropertyDescriptorDaoImpl extends AbstractReflectionDao<PropertyDescriptor>{

    public PropertyDescriptorDaoImpl() throws Exception {
        super(PropertyDescriptor.class, "propertyDescriptors", true);
    }
    
    protected String generateNodeName(PropertyDescriptor pd) {
        return pd.getProperty();
    }
}
