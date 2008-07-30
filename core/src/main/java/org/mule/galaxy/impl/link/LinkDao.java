package org.mule.galaxy.impl.link;

import java.util.List;

import org.mule.galaxy.Dao;
import org.mule.galaxy.Item;
import org.mule.galaxy.Link;

public interface LinkDao extends Dao<Link> {
    List<Link> getReciprocalLinks(final Item item);
}
