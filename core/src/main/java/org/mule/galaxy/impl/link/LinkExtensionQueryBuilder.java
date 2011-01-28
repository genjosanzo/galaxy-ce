package org.mule.galaxy.impl.link;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.mule.galaxy.Item;
import org.mule.galaxy.Link;
import org.mule.galaxy.impl.extension.IdentifiableExtension;
import org.mule.galaxy.impl.extension.IdentifiableExtensionQueryBuilder;
import org.mule.galaxy.query.OpRestriction.Operator;
import org.mule.galaxy.query.QueryException;
import org.mule.galaxy.type.PropertyDescriptor;

public class LinkExtensionQueryBuilder extends IdentifiableExtensionQueryBuilder {

    public LinkExtensionQueryBuilder(IdentifiableExtension e) throws IntrospectionException {
        super(e);
        
        getSuffixes().add("");
        getSuffixes().add("reciprocal");
    }

    @Override
    protected List<String> getMatches(Object o, String property, Operator operator) throws QueryException {
        LinkDao linkDao = (LinkDao) getDao();
        
        boolean reciprocal = false;
        if (property.endsWith(".reciprocal")) {
            property = property.substring(0, property.lastIndexOf('.'));
            reciprocal = true;
        }
        
        PropertyDescriptor pd = typeManager.getPropertyDescriptorByName(property);
        
        if (pd == null) {
            return Collections.emptyList();
        }
        
        if (reciprocal) {
            Collection<Item> items = linkDao.getReciprocalItems(pd.getId(), operator == Operator.LIKE, o);
            ArrayList<String> ids = new ArrayList<String>();
            for (Item result : items) {
                String id = result.getId();
                
                ids.add(id.substring(id.indexOf('$') + 1));
            }
            
            return ids;
        } else {
            Collection<Link> links = linkDao.getLinks(pd.getId(), operator == Operator.LIKE, o);
            ArrayList<String> ids = new ArrayList<String>();
            for (Link result : links) {
                if (result.getItem() != null) {
                    String id = result.getItem().getId();
                    
                    ids.add(id.substring(id.indexOf('$') + 1));
                }
            }
            
            return ids;
        }
    }

    @Override
    protected String getProperty(String property) {
        return "jcr:uuid";
    }

}
