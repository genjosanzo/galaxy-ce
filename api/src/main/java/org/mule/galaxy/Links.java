package org.mule.galaxy;

import java.util.Collection;

/**
 * An interface to query for links. Many different link relationships
 * can be created. To do this create a new PropertyDescriptor with the LinkExtension.
 * 
 * Links for an Item's property can be accessed by doing the following:
 * <pre>
 * Item item = ...;
 * 
 * Links links = (Links) item.getProperty("depends");
 * </pre>
 */
public interface Links {

    Collection<Link> getLinks();
    
    void addLinks(Link link);

    void removeLinks(Link... links);
    
    Collection<Link> getReciprocalLinks();

}
