package org.mule.galaxy.impl.link;

import java.util.Collection;
import java.util.List;

import org.mule.galaxy.Dao;
import org.mule.galaxy.Item;
import org.mule.galaxy.Link;

public interface LinkDao extends Dao<Link> {
    List<Link> getReciprocalLinks(final Item item, final String property);

    List<Link> getLinks(Item item, String property);
    
    List<Link> getAllLinks();

    void deleteLinks(Item item, String property);

    Collection<Link> getLinks(String property, boolean like, Object path);

    Collection<Item> getReciprocalItems(String property, boolean like, Object path);
}
