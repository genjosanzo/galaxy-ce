package org.mule.galaxy;

import java.util.Collection;

public interface Links {

    Collection<Link> getLinks();
    
    void addLinks(Link link);

    void removeLinks(Link... links);
    
    Collection<Link> getReciprocalLinks();

}
