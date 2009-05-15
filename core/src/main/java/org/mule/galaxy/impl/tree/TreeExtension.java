package org.mule.galaxy.impl.tree;

import java.util.Collection;

import org.mule.galaxy.Item;
import org.mule.galaxy.PropertyException;
import org.mule.galaxy.TreeItem;
import org.mule.galaxy.impl.extension.IdentifiableExtension;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.type.PropertyDescriptor;
import org.mule.galaxy.util.BundleUtils;
import org.mule.galaxy.util.Message;

public class TreeExtension extends IdentifiableExtension<TreeItem> {

    public static final String ROOT_ITEM_KEY = "rootItem";

    @Override
    public void store(Item entry, PropertyDescriptor pd, Object value) throws PolicyException,
        PropertyException, AccessException {
        if (value instanceof TreeItem) {
            // TODO validate parentage
        } else if (value instanceof Collection) {
            // TODO validate parentage 
        } else if (value != null) {
            throw new PropertyException(new Message("ILLEGAL_PROPERTY", 
                                                    BundleUtils.getBundle(getClass()),
                                                    value.getClass()));
        }
        
        super.store(entry, pd, value);
    }

}
