package org.mule.galaxy.mule2.registry;

import org.mule.galaxy.Item;
import org.mule.galaxy.Registry;

/**
 * Builds an object from an Item inside the Galaxy {@link Registry}. This
 * enables things such as connection factories to be easily built from metadata.
 */
public interface ObjectFactory {
    Object create(Item item);
}
