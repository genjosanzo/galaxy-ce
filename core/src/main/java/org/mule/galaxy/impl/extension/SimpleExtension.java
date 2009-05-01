package org.mule.galaxy.impl.extension;

import org.mule.galaxy.Item;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.type.PropertyDescriptor;

public class SimpleExtension extends AbstractExtension {

    public SimpleExtension() {
        super();
    }

    public Object get(Item entry, PropertyDescriptor pd, boolean getWithNoData) {
        return entry.getInternalProperty(pd.getProperty());
    }

    public void store(Item entry, PropertyDescriptor pd, Object value) throws PolicyException,
            PropertyException, AccessException {
        entry.setInternalProperty(pd.getProperty(), value);
    }

}