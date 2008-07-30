package org.mule.galaxy;

import java.util.Collection;
import java.util.Set;

public interface Links {

    Collection<Link> getLinks();
    
    void addLinks(Link link) throws RegistryException;

    void removeLinks(Link... links) throws RegistryException;
    
    Collection<Link> getReciprocalLinks() throws RegistryException;

}
