package org.mule.galaxy.impl.extension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mule.galaxy.Item;
import org.mule.galaxy.extension.Extension;
import org.mule.galaxy.impl.jcr.query.SimpleQueryBuilder;
import org.mule.galaxy.type.PropertyDescriptor;
import org.mule.galaxy.type.TypeManager;

public abstract class ExtensionQueryBuilder extends SimpleQueryBuilder {
    protected TypeManager typeManager;
    protected Extension extension;

    protected List<String> suffixes = new ArrayList<String>();
    
    public ExtensionQueryBuilder() {
        super();
        
        appliesTo.add(Item.class);
    }
    
    @Override
    public Collection<String> getProperties() {
        List<String> props = new ArrayList<String>();
        Collection<PropertyDescriptor> pds = typeManager.getPropertyDescriptorsForExtension(extension.getId());
        if (pds != null) {
            for (PropertyDescriptor pd : pds) {
                for (String suffix : getSuffixes()) {
                    String name = ("".equals(suffix)) ? pd.getProperty() : pd.getProperty() + "." + suffix;
                    
                    props.add(getRoot() + name);
                }
            }
        }
        return props;
    }
    
    @Override
    protected String getProperty(String property) {
        for (String s : getSuffixes()) {
            String end = "." + s;
            if (!"".equals(s) && property.endsWith(end)) {
                property = property.substring(0, property.length() - (s.length()+1));
                break;
            }
        }
        
        return property;
    }

    public String getRoot() {
        return "";
    }
    
    public List<String> getSuffixes() {
        return suffixes;
    }
    
    public void setExtension(Extension extension) {
        this.extension = extension;
    }

    public void setTypeManager(TypeManager typeManager) {
        this.typeManager = typeManager;
    }
}
